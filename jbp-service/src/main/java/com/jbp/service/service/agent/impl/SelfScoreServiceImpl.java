package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.model.agent.SelfScore;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.SelfScoreDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.SelfScoreFlowService;
import com.jbp.service.service.agent.SelfScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class SelfScoreServiceImpl extends ServiceImpl<SelfScoreDao, SelfScore> implements SelfScoreService {
    @Resource
    private UserService userService;
    @Resource
    private SelfScoreFlowService selfScoreFlowService;

    @Override
    public PageInfo<SelfScore> pageList(Integer uid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<SelfScore> lqw = new LambdaQueryWrapper<SelfScore>()
                .eq(!ObjectUtil.isNull(uid), SelfScore::getUid, uid);
        Page<SelfScore> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<SelfScore> list = list(lqw);
        List<Integer> uIdList = list.stream().map(SelfScore::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            e.setAccount(uidMapList.get(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public SelfScore add(Integer uid) {
        SelfScore selfScore = new SelfScore(uid);
        save(selfScore);
        return selfScore;
    }

    @Override
    public SelfScore getByUser(Integer uid) {
        return getOne(new QueryWrapper<SelfScore>().lambda().eq(SelfScore::getUid, uid));
    }

    @Override
    public void orderSuccess(Integer uid, BigDecimal score, String ordersSn, Date payTime, List<ProductInfoDto> productInfo) {
        SelfScore selfScore = getByUser(uid);
        if (selfScore == null) {
            selfScore = add(uid);
        }
        selfScore.setScore(selfScore.getScore().add(score));
        updateById(selfScore);
        // 增加明细
        selfScoreFlowService.add(uid, score, "增加", "下单", ordersSn, payTime, productInfo, "");
    }
}
