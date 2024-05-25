package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.InvitationScore;
import com.jbp.common.model.agent.InvitationScoreFlow;
import com.jbp.common.model.agent.SelfScore;
import com.jbp.common.model.agent.SelfScoreFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.service.dao.agent.SelfScoreDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.SelfScoreFlowService;
import com.jbp.service.service.agent.SelfScoreService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
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

    @Resource
    private SelfScoreDao selfScoreDao;

    @Override
    public PageInfo<SelfScore> pageList(Integer uid, String nickname ,PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<SelfScore> lqw = new LambdaQueryWrapper<SelfScore>()
                .eq(!ObjectUtil.isNull(uid), SelfScore::getUid, uid)
                .orderByDesc(SelfScore::getId);
        if (StrUtil.isNotBlank(nickname)){
            lqw.apply("1=1 and uid in (select id from eb_user where nickname like '%" + nickname + "%')");
        }
        Page<SelfScore> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<SelfScore> list = list(lqw);
        if(CollectionUtils.isEmpty(list)){
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(SelfScore::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
            e.setNickname(user != null ? user.getNickname() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public PageInfo<SelfScore> pageTeamList(Integer uid,String startPayTime,String endPayTime, PageParamRequest pageParamRequest) {
        Page<SelfScore> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<SelfScore> list =selfScoreDao.getTeamUserScore(uid,startPayTime,endPayTime);
        if(CollectionUtils.isEmpty(list)){
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(SelfScore::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
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
    public BigDecimal getUserNext(Integer uid, Boolean containsSelf) {

        BigDecimal score = selfScoreDao.getUserNext(uid);
        if (BooleanUtils.isNotTrue(containsSelf)) {
            return score;
        }

        SelfScore selfScore =  getOne(new QueryWrapper<SelfScore>().lambda().eq(SelfScore::getUid, uid));
        if(selfScore==null){
            return score;
        }
        return selfScore.getScore().add(score);


    }

    @Override
    public void orderSuccess(Integer uid, BigDecimal score, String ordersSn, Date payTime, List<ProductInfoDto> productInfo) {
        SelfScoreFlow one = selfScoreFlowService.getOne(new QueryWrapper<SelfScoreFlow>().lambda()
                .eq(SelfScoreFlow::getOrdersSn, ordersSn).last(" limit 1"));
        if (one != null) {
            return;
        }
        SelfScore selfScore = getByUser(uid);
        if (selfScore == null) {
            selfScore = add(uid);
        }
        selfScore.setScore(selfScore.getScore().add(score));
        updateById(selfScore);
        // 增加明细
        selfScoreFlowService.add(uid, score, "增加", "下单", ordersSn, payTime, productInfo, "");
    }

    @Override
    public void orderRefund(String ordersSn) {
        //退还个人业绩
        List<SelfScoreFlow> selfList = selfScoreFlowService.list(new QueryWrapper<SelfScoreFlow>().lambda().eq(SelfScoreFlow::getOrdersSn, ordersSn));
        selfScoreFlowService.remove(new QueryWrapper<SelfScoreFlow>().lambda().eq(SelfScoreFlow::getOrdersSn, ordersSn));
        if (!selfList.isEmpty()) {
            for (SelfScoreFlow selfScoreFlow : selfList) {
                SelfScore selfScore = getByUser(selfScoreFlow.getUid());
                if (selfScore != null && ArithmeticUtils.gte(selfScore.getScore(), selfScoreFlow.getScore())) {
                    BigDecimal score = selfScore.getScore().subtract(selfScoreFlow.getScore());
                    selfScore.setScore(score);
                    updateById(selfScore);
                }
            }
        }
    }

    @Override
    public void updateScore(Integer uid, BigDecimal score, String ordersSn, Date payTime, String remark, Boolean ifAdd) {
        SelfScore selfScore = getByUser(uid);
        //减少
        if (BooleanUtils.isFalse(ifAdd)) {
            if (selfScore == null || ArithmeticUtils.less(selfScore.getScore(), score)) {
                throw new CrmebException("个人积分不足");
            }
            selfScore.setScore(selfScore.getScore().subtract(score));
            updateById(selfScore);
            Boolean ifSuccess = updateById(selfScore);
            if (BooleanUtils.isNotTrue(ifSuccess)) {
                throw new CrmebException("当前操作人数过多");
            }
            SelfScoreFlow flow = new SelfScoreFlow(uid,score, "减少", "人工", ordersSn, payTime, null,remark);
            selfScoreFlowService.save(flow);
            return;
        }
        //增加
        if (BooleanUtils.isTrue(ifAdd) && selfScore == null) {
            selfScore = new SelfScore(uid);
            save(selfScore);
        }
        selfScore.setScore(selfScore.getScore().add(score));
        Boolean ifSuccess = updateById(selfScore);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }
        SelfScoreFlow flow = new SelfScoreFlow(uid,score, "增加", "人工", ordersSn, payTime, null,remark);
        selfScoreFlowService.save(flow);
    }
}
