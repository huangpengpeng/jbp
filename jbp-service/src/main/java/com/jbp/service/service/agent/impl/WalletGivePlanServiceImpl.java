package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.agent.WalletGivePlan;
import com.jbp.common.model.user.User;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.WalletGivePlanDao;
import com.jbp.service.service.agent.PlatformWalletService;
import com.jbp.service.service.agent.WalletGivePlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class WalletGivePlanServiceImpl extends UnifiedServiceImpl<WalletGivePlanDao, WalletGivePlan> implements WalletGivePlanService {

    @Resource
    private PlatformWalletService platformWalletService;

    @Override
    public WalletGivePlan add(User user, WalletConfig walletConfig, BigDecimal amt, String externalNo, String postscript, String planTime) {
        WalletGivePlan plan = new WalletGivePlan(user.getId(), user.getAccount(), walletConfig.getName(),
                walletConfig.getType(), amt, externalNo, postscript, planTime);
        save(plan);
        return plan;
    }

    @Override
    public void release() {
        String now = DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN);
        LambdaQueryWrapper<WalletGivePlan> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WalletGivePlan::getStatus, "未使用");
        lqw.le(WalletGivePlan::getPlanTime, now);
        List<WalletGivePlan> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        for (WalletGivePlan plan : list) {
            platformWalletService.transferToUser(plan.getUid(), plan.getWalletType(), plan.getAmt(),
                    WalletFlow.OperateEnum.奖励.toString(), plan.getExternalNo(), plan.getPostscript());
            plan.setUpdateTime(now);
            plan.setStatus("已使用");
            updateById(plan);
        }
    }

    @Override
    public void cancel(String externalNo) {
        LambdaQueryWrapper<WalletGivePlan> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WalletGivePlan::getStatus, "未使用");
        lqw.le(WalletGivePlan::getExternalNo, externalNo);
        List<WalletGivePlan> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        String now = DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN);
        for (WalletGivePlan plan : list) {
            plan.setUpdateTime(now);
            plan.setStatus("已取消");
            updateById(plan);
        }
    }

}
