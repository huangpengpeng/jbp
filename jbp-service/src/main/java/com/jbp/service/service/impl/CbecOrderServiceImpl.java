package com.jbp.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.dto.CbecOrderDetailDto;
import com.jbp.common.dto.CbecOrderSyncDTO;
import com.jbp.common.model.order.CbecOrder;
import com.jbp.common.model.user.CbecUser;
import com.jbp.common.model.user.User;
import com.jbp.common.request.agent.CbecOrderSyncRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.ArticleCategoryDao;
import com.jbp.service.dao.CbecOrderDao;
import com.jbp.service.service.CbecOrderService;
import com.jbp.service.service.CbecUserService;
import com.jbp.service.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


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
            List<CbecOrderDetailDto> goodsDetails = Lists.newArrayList();
            for (CbecOrderSyncDTO.GoodsDetail goodsDetail : dto.getGoodsDetails()) {
                CbecOrderDetailDto detailDto = new CbecOrderDetailDto(goodsDetail.getGoodsName(),
                        goodsDetail.getQuantity(), goodsDetail.getPrice(), pv, "");
                goodsDetails.add(detailDto);
            }

            CbecOrder cbecOrder2 =
                    new CbecOrder(cbecAccount.getUid(), cbecAccount.getAccountNo(), dto.getMobile(), dto.getOrderSn(), dto.getStatus(),
                            dto.getTotalFee(), dto.getGoodsFee(), dto.getPostFee(), dto.getScore(), pv, dto.getCreateTime(),
                            dto.getPaymentTime(), dto.getShipmentTime(), null, goodsDetails, true, BigDecimal.ZERO);
            save(cbecOrder2);
            return;
        }
        // 更新
        cbecOrder.setShipmentsTime(dto.getShipmentTime());
        cbecOrder.setStatus(dto.getStatus());
        cbecOrder.setIfClearing(Boolean.TRUE);
        updateById(cbecOrder);
    }


    @Override
    public Long save(CbecOrderSyncRequest request) {
        Boolean ifClearing = false;
        BigDecimal commAmt = BigDecimal.ZERO;
        CbecOrder cbecOrder = getOne(new LambdaQueryWrapper<CbecOrder>().eq(CbecOrder::getCbecOrderNo, request.getOrderNo()));
        if (cbecOrder != null) {
            ifClearing = cbecOrder.getIfClearing();
            commAmt = cbecOrder.getCommAmt();
            removeById(cbecOrder.getId());  // 删除历史
        }
        // 保存新的订单
        User user = userService.getByAccount(request.getAccount());
        Date shipmentsTime = null;
        if (StringUtils.isNotEmpty(request.getShipmentsTime())) {
            shipmentsTime = DateTimeUtils.parseDate(request.getShipmentsTime());
        }
        Date refundTimeTime = null;
        if (StringUtils.isNotEmpty(request.getRefundTime())) {
            refundTimeTime = DateTimeUtils.parseDate(request.getRefundTime());
        }
        cbecOrder = new CbecOrder(user.getId(), user.getAccount(), request.getMobile(), request.getOrderNo(), request.getStatus(),
                request.getTotalFee(), request.getGoodsFee(), request.getPostFee(), request.getScore(), request.getPv(),
                DateTimeUtils.parseDate(request.getCreateTime()), DateTimeUtils.parseDate(request.getPaymentTime()),
                shipmentsTime, refundTimeTime, request.getGoodsDetails(), ifClearing, commAmt);
        save(cbecOrder);
        return cbecOrder.getId();
    }
}

