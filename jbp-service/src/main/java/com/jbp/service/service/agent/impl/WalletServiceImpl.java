package com.jbp.service.service.agent.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.product.ProductDeduction;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.agent.WalletDao;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.PlatformWalletService;
import com.jbp.service.service.agent.WalletFlowService;
import com.jbp.service.service.agent.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class WalletServiceImpl extends ServiceImpl<WalletDao, Wallet> implements WalletService {
    @Resource
    private WalletFlowService walletFlowService;
    @Resource
    private PlatformWalletService platformWalletService;
    @Resource
    private WalletConfigService walletConfigService;
    @Resource
    private UserService userService;

    @Override
    public Wallet add(Integer uId, Integer type) {
        Wallet wallet = new Wallet(uId, type);
        save(wallet);
        return wallet;
    }

    @Override
    public Wallet getByUser(Integer uid, Integer type) {
        LambdaQueryWrapper<Wallet> wrapper = new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUId, uid)
                .eq(Wallet::getType, type);
        return getOne(wrapper);
    }


    @Override
    public Boolean increase(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript) {
        if (amt == null || ArithmeticUtils.lessEquals(amt, BigDecimal.ZERO)) {
            throw new CrmebException(type + "增加用户积分金额不能小于0:" + amt);
        }
        Wallet wallet = getByUser(uid, type);
        if (wallet == null) {
            wallet = add(uid, type);
        }
        BigDecimal orgBalance = wallet.getBalance();
        wallet.setBalance(wallet.getBalance().add(amt));
        boolean ifSuccess = updateById(wallet);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }
        walletFlowService.add(uid, type, amt, operate, WalletFlow.ActionEnum.收入.name(), externalNo, orgBalance, wallet.getBalance(), postscript);
        return ifSuccess;
    }

    @Override
    public Boolean reduce(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript) {
        if (amt == null || ArithmeticUtils.lessEquals(amt, BigDecimal.ZERO)) {
            throw new CrmebException(type + "减少用户积分金额不能小于0:" + amt);
        }
        Wallet wallet = getByUser(uid, type);
        if (wallet == null || ArithmeticUtils.less(wallet.getBalance(), amt)) {
            throw new CrmebException("用户余额不足");
        }
        BigDecimal orgBalance = wallet.getBalance();
        wallet.setBalance(wallet.getBalance().subtract(amt));
        boolean ifSuccess = updateById(wallet);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }
        walletFlowService.add(uid, type, amt, operate, WalletFlow.ActionEnum.支出.name(), externalNo, orgBalance, wallet.getBalance(), postscript);
        return ifSuccess;
    }

    @Override
    public Boolean transferToPlatform(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript) {
        reduce(uid, type, amt, operate, externalNo, postscript);
        platformWalletService.increase(type, amt, operate, externalNo, postscript);
        return true;
    }

    @Override
    public void deduction(Integer uid, List<ProductDeduction> deductionList, String externalNo, String postscript) {
        if (CollUtil.isEmpty(deductionList)) {
            return;
        }
        List<WalletFlow> walletFlows = walletFlowService.getByUser(uid, externalNo, WalletFlow.OperateEnum.抵扣.toString(), WalletFlow.ActionEnum.支出.name());
        if (CollUtil.isNotEmpty(walletFlows)) {
            return;
        }
        for (ProductDeduction deduction : deductionList) {
            if (deduction.getDeductionFee() != null && ArithmeticUtils.gt(deduction.getDeductionFee(), BigDecimal.ZERO)) {
                transferToPlatform(uid, deduction.getWalletType(), deduction.getDeductionFee(),
                        WalletFlow.OperateEnum.抵扣.toString(), externalNo, postscript);
            }
        }
    }

    @Override
    public void refundDeduction(Integer uid, String externalNo, String postscript) {
        List<WalletFlow> walletFlows = walletFlowService.getByUser(uid, externalNo, WalletFlow.OperateEnum.抵扣.toString(),
                WalletFlow.ActionEnum.收入.name());
        if (CollectionUtils.isNotEmpty(walletFlows)) {
            return;
        }
        walletFlows = walletFlowService.getByUser(uid, externalNo, WalletFlow.OperateEnum.抵扣.toString(), WalletFlow.ActionEnum.支出.name());
        if (CollUtil.isEmpty(walletFlows)) {
            return;
        }
        for (WalletFlow flow : walletFlows) {
            platformWalletService.transferToUser(flow.getUid(), flow.getWalletType(), flow.getAmt(),
                    WalletFlow.OperateEnum.抵扣.toString(), externalNo, postscript);
        }
    }

    @Override
    public Boolean transfer(Integer uid, Integer receiveUserId, BigDecimal amt, Integer type, String postscript) {
        String externalNo = StringUtils.N_TO_10("ZZ_");
        reduce(uid, type, amt, WalletFlow.OperateEnum.转账.name(), externalNo, postscript);
        increase(receiveUserId, type, amt, WalletFlow.OperateEnum.转账.name(), externalNo, postscript);
        return true;
    }

    @Override
    public Boolean change(Integer uid, BigDecimal amt, Integer type, Integer changeType, String postscript) {
        //扣除用户
        String externalNo = StringUtils.N_TO_10("DH_");
        reduce(uid, type, amt, WalletFlow.OperateEnum.兑换.name(), externalNo, postscript);
        //类型兑换比例积分
        WalletConfig walletConfig = walletConfigService.getByType(type);
        BigDecimal amtIntegral = amt.multiply(walletConfig.getChangeScale());
        increase(uid, changeType, amtIntegral, WalletFlow.OperateEnum.兑换.name(), externalNo, postscript);
        return true;
    }


    @Override
    public PageInfo<Wallet> pageList(Integer uid, Integer type, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<Wallet>()
                .eq(!ObjectUtil.isNull(uid), Wallet::getUId, uid)
                .eq(!ObjectUtil.isNull(type), Wallet::getType, type);
        Page<Wallet> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Wallet> list = list(walletLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(Wallet::getUId).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            WalletConfig walletConfig = walletConfigService.getByType(e.getType());
            e.setTypeName(walletConfig != null ? walletConfig.getName() : "");
            User user = uidMapList.get(e.getUId());
            e.setAccount(user != null ? user.getAccount() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Wallet getCanPayByUser(Integer uid) {
        WalletConfig walletConfig = walletConfigService.getCanPay();
        if (walletConfig == null) {
            throw new CrmebException("平台未配置可付款积分类型");
        }
        return getByUser(uid, walletConfig.getType());
    }

    @Resource
    private OrderService orderService;
    @Override
    public void init() {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Order::getLevel, 0);
        List<Order> list = orderService.list(queryWrapper);
        for (Order order : list) {
            deduction(order.getPayUid(), order.getWalletDeductionList(), order.getOrderNo(), "下单抵扣");
            log.info("正在初始化抵扣减少");
        }
        for (Order order : list) {
            if (order.getStatus().equals(Integer.valueOf(9))) {
                refundDeduction(order.getPayUid(), order.getOrderNo(), "取消回退");
                log.info("正在初始化抵扣回退");
            }
        }
    }
}
