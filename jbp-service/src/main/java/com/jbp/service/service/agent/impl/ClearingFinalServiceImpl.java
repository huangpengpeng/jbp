package com.jbp.service.service.agent.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ClearingBonus;
import com.jbp.common.model.agent.ClearingFinal;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ClearingRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.RedisUtil;
import com.jbp.service.dao.agent.ClearingFinalDao;
import com.jbp.service.product.comm.ProductCommChain;
import com.jbp.service.service.agent.*;
import com.jbp.service.util.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingFinalServiceImpl extends UnifiedServiceImpl<ClearingFinalDao, ClearingFinal> implements ClearingFinalService {


    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ClearingBonusService clearingBonusService;
    @Resource
    private ClearingUserService clearingUserService;
    @Resource
    private ClearingInvitationFlowService invitationFlowService;
    @Resource
    private ClearingRelationFlowService relationFlowService;
    @Resource
    private ProductCommChain productCommChain;
    @Resource
    private RedisUtil redisUtil;


    /**
     * 一键结算
     */
    @Async
    @Override
    public ClearingFinal oneKeyClearing(ClearingRequest clearingRequest) {
        redisUtil.delete("clearing_final");
        Map<Object, Object> logMap = Maps.newConcurrentMap();


        Set<String> logSet = new HashSet<>();
        logSet.add(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) + "-结算任务开始创建");
        redisUtil.set("clearing_final", logSet);
        // 1.创建结算记录
        ClearingFinal clearingFinal = create(clearingRequest.getCommName(), clearingRequest.getCommType(),
                clearingRequest.getStartTime(), clearingRequest.getEndTime());
        logSet.add(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) + "结算任务完成创建");
        redisUtil.set("clearing_final", logSet);


        // 2.生成结算名单
        if (clearingRequest.getIfImportUser()) {
            logSet.add(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) + "结算名单开始导入");
            redisUtil.set("clearing_final", logSet);
            clearingUserService.importUserList(clearingFinal.getId(), clearingRequest.getUserList());
            logSet.add(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) + "结算名单完成导入");
            redisUtil.set("clearing_final", logSet);
        }else{
            logSet.add(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) + "结算名单开始创建");
            redisUtil.set("clearing_final", logSet);
            clearingUserService.create(clearingFinal.getId());
            logSet.add(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) + "结算名单完成创建");
            redisUtil.set("clearing_final", logSet);
        }
        // 3.生成结算关系
        logSet.add(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) + "结算销售关系开始紧缩");
        redisUtil.set("clearing_final", logSet);
        invitationFlowService.create(clearingFinal.getId());
        logSet.add(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) + "结算销售关系完成紧缩");
        redisUtil.set("clearing_final", logSet);


        logSet.add(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) + "结算服务关系开始紧缩");
        redisUtil.set("clearing_final", logSet);
        relationFlowService.create(clearingFinal.getId());
        logSet.add(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) + "结算服务关系完成紧缩");
        redisUtil.set("clearing_final", logSet);

        // 计算佣金
        logSet.add(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) + "结算佣金开始计算");
        redisUtil.set("clearing_final", logSet);
        productCommChain.clearing(clearingFinal);
        logSet.add(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) + "结算佣金完成计算");
        redisUtil.set("clearing_final", logSet);
        clearingFinal = getById(clearingFinal.getId());
        clearingFinal.setLogs(JSONArray.toJSONString(logSet));
        updateById(clearingFinal);

        redisUtil.delete("clearing_final");
        return clearingFinal;
    }

    /**
     * 一键删除
     */
    @Override
    public Boolean oneKeyDel(Long clearingId) {
        ClearingFinal clearingFinal = getById(clearingId);
        if (clearingFinal.getStatus().equals(ClearingFinal.Constants.已出款.name())) {
            throw new CrmebException("已出款不允许删除");
        }
        removeById(clearingId);
        clearingUserService.del4Clearing(clearingId);
        invitationFlowService.del4Clearing(clearingId);
        relationFlowService.del4Clearing(clearingId);
        productCommChain.del4Clearing(clearingFinal);
        return null;
    }

    @Override
    public Boolean oneKeySend(Long clearingId) {
        ClearingFinal clearingFinal = getById(clearingId);
        if (clearingFinal.getStatus().equals(ClearingFinal.Constants.待出款.name())) {
            throw new CrmebException("只能操作待出款");
        }
        List<ClearingBonus> clearing = clearingBonusService.get4Clearing(clearingId);
        for (ClearingBonus clearingBonus : clearing) {
            fundClearingService.create(clearingBonus.getUid(), clearingBonus.getUniqueNo(),
                    clearingBonus.getCommName(), clearingBonus.getCommAmt(), null,
                    clearingFinal.getName(), "");
        }
        clearingFinal.setStatus(ClearingFinal.Constants.已出款.name());
        updateById(clearingFinal);
        return true;
    }

    @Override
    public ClearingFinal create(String commName, Integer commType, String startTime, String endTime) {
        String name = commName + "_" + startTime + "-" + endTime;
        ClearingFinal clearingFinal = getByName(name);
        if (clearingFinal != null) {
            throw new CrmebException(commName + "结算开始-结束,时间周期已经存在请勿重复操作");
        }
        clearingFinal = ClearingFinal.builder().name(name).commName(commName).commType(commType).startTime(startTime)
                .endTime(endTime).status(ClearingFinal.Constants.待结算.name()).totalScore(BigDecimal.ZERO).totalAmt(BigDecimal.ZERO).build();
        save(clearingFinal);
        return clearingFinal;
    }

    @Override
    public ClearingFinal getByName(String name) {
        return getOne(new QueryWrapper<ClearingFinal>().lambda().eq(ClearingFinal::getName, name));
    }

    @Override
    public ClearingFinal getLastOne(Long id, Integer commType) {
        return getOne(new QueryWrapper<ClearingFinal>().lambda().lt(ClearingFinal::getId, id)
                .eq(ClearingFinal::getCommType, commType).last(" limit 1"));
    }

    @Override
    public PageInfo<ClearingFinal> pageList(Integer commType, String status, PageParamRequest pageParamRequest) {
        Page<ClearingFinal> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<ClearingFinal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(commType != null, ClearingFinal::getCommType, commType);
        lqw.eq(StringUtils.isNotEmpty(status), ClearingFinal::getStatus, status).orderByDesc(ClearingFinal::getId);
        return CommonPage.copyPageInfo(page, list(lqw));
    }
}
