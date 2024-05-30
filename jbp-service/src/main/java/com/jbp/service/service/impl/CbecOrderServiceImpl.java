package com.jbp.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.dto.CbecOrderSyncDTO;
import com.jbp.common.model.order.CbecOrder;
import com.jbp.common.model.user.CbecUser;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.dao.ArticleCategoryDao;
import com.jbp.service.dao.CbecOrderDao;
import com.jbp.service.service.CbecOrderService;
import com.jbp.service.service.CbecUserService;
import com.jbp.service.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;


@Service
public class CbecOrderServiceImpl extends ServiceImpl<CbecOrderDao, CbecOrder> implements CbecOrderService {


    @Resource
    private CbecOrderDao dao;

    @Resource
    private UserService userService;

    @Resource
    private CbecUserService cbecUserService;

    @Override
    public void orderSync(CbecOrderSyncDTO dto) {
        // 跨境账户
        CbecUser cbecAccount = cbecUserService.getOne(new QueryWrapper<CbecUser>().lambda().eq(CbecUser::getAccountNo, dto.getBizId()));
        if (cbecAccount == null) {
            User user = userService.getByAccount(dto.getBizId());
//            if (user == null) {
//                return;
//            }
            cbecAccount.setUid(user == null ? 0 :user.getId());
            cbecAccount.setAccountNo(user == null ?  dto.getBizId(): user.getAccount());
        }
        // 跨境订单信息
        CbecOrder cbecOrder = dao.selectOne(new QueryWrapper<CbecOrder>().lambda().eq(CbecOrder::getCbecOrderNo, dto.getOrderSn()));
        // 新增跨境订单
        if (cbecOrder == null) {
            // 计算pv值 （积分-（积分+钱）*15%）/35% = PV值
            BigDecimal pv = BigDecimal.ZERO;
            BigDecimal totalFee = dto.getGoodsFee().add(dto.getScore());
            BigDecimal grossProfit = totalFee.multiply(BigDecimal.valueOf(0.23));
            BigDecimal diffScore = dto.getScore().subtract(grossProfit);
            if (ArithmeticUtils.gt(diffScore, BigDecimal.ZERO)) {
                pv = diffScore.divide(totalFee, 8, BigDecimal.ROUND_DOWN)
                        .divide(BigDecimal.valueOf(0.4), 8, BigDecimal.ROUND_DOWN)
                        .setScale(1, BigDecimal.ROUND_DOWN);
            }
            if (pv != null && ArithmeticUtils.gt(pv, BigDecimal.valueOf(0.8))) {
                pv = BigDecimal.valueOf(0.8);
            }
            CbecOrder cbecOrder2 =
                    new CbecOrder(cbecAccount.getUid(), cbecAccount.getAccountNo(), dto.getMobile(), dto.getOrderSn(), dto.getStatus(),
                            dto.getTotalFee(), dto.getGoodsFee(), dto.getPostFee(), dto.getScore(), pv, dto.getCreateTime(),
                            dto.getPaymentTime(), dto.getShipmentTime(), JSON.toJSONString(dto.getGoodsDetails()), true);
            save(cbecOrder2);
            return;
        }
        // 更新
        cbecOrder.setShipmentsTime(dto.getShipmentTime());
        cbecOrder.setStatus(dto.getStatus());
        cbecOrder.setIfClearing(Boolean.TRUE);
        updateById(cbecOrder);
    }
}

