package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.InvitationScore;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.common.model.agent.RelationScoreFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.RelationScoreDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.RelationScoreFlowService;
import com.jbp.service.service.agent.RelationScoreService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class RelationScoreServiceImpl extends ServiceImpl<RelationScoreDao, RelationScore> implements RelationScoreService {
    @Resource
    private UserService userService;
    @Resource
    private RelationScoreFlowService relationScoreFlowService;

    @Override
    public RelationScore getByUser(Integer uId, Integer node) {
        LambdaQueryWrapper<RelationScore> query = new LambdaQueryWrapper<>();
        query.eq(RelationScore::getUid, uId).eq(RelationScore::getNode, node);
        return getOne(query);
    }

    @Override
    public PageInfo<RelationScore> pageList(Integer uid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<RelationScore> lqw = new LambdaQueryWrapper<RelationScore>()
                .eq(!ObjectUtil.isNull(uid), RelationScore::getUid, uid);
        Page<RelationScore> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<RelationScore> list = list(lqw);
        if(CollectionUtils.isEmpty(list)){
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(RelationScore::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            e.setAccount(uidMapList.get(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public RelationScoreFlow orderSuccessIncrease(Integer uid, Integer orderUid, BigDecimal score, int node,
                                                  String ordersSn, Date payTime, List<ProductInfoDto> productInfo, Integer level) {
        RelationScore relationScore = getByUser(uid, node);
        if (relationScore == null) {
            relationScore = new RelationScore(uid, node);
            save(relationScore);
        }
        // 更新可用
        relationScore.setUsableScore(relationScore.getUsableScore().add(score));
        updateById(relationScore);
        // 增加明细
        RelationScoreFlow flow = new RelationScoreFlow(uid, orderUid, score, node,
                "下单", "增加", ordersSn, payTime, productInfo, "", level, BigDecimal.ZERO, BigDecimal.ZERO);
        relationScoreFlowService.save(flow);
        return flow;
    }

    @Override
    public RelationScoreFlow orderSuccessReduce(Integer uid, Integer orderUid, BigDecimal score, int node, String ordersSn,
                                                Date payTime, Integer level, BigDecimal amt, BigDecimal ratio) {
        RelationScore relationScore = getByUser(uid, node);
        if (relationScore == null) {
            throw new CrmebException("对碰减少积分不存在，用户:" + uid + ",点位:" + node);
        }
        relationScore.setUsableScore(relationScore.getUsableScore().subtract(score));
        relationScore.setUsedScore(relationScore.getUsedScore().add(score));
        updateById(relationScore);
        if (relationScore.hasError()) {
            throw new CrmebException("对碰减少积分错误，用户:" + uid + ",点位:" + node + ",减少积分:" + score);
        }
        // 增加明细
        RelationScoreFlow flow = new RelationScoreFlow(uid, orderUid, score, node,
                "对碰", "减少", ordersSn, payTime, null, "", level, amt, ratio);
        relationScoreFlowService.save(flow);
        return flow;
    }
}
