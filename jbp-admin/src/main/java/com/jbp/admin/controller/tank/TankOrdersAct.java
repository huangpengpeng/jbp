package com.jbp.admin.controller.tank;

import com.jbp.common.model.tank.TankOrders;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.TankOrderAdminListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankEquipmentNumberService;
import com.jbp.service.service.TankOrdersService;
import com.jbp.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("api/admin/tank/tankOrders")
@Api(tags = "设备管理")
public class TankOrdersAct {



    @Resource
    private TankOrdersService tankOrdersService;
    @Resource
    private TankEquipmentNumberService tankEquipmentNumberService;

    @Resource
    private UserService userService;


    @PreAuthorize("hasAuthority('tank:tankOrders:list')")
    @ApiOperation(value = "充值列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<TankOrderAdminListResponse>> list(HttpServletRequest request,String username,String status,String startCreateTime,String endCreateTime, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(tankOrdersService.getAdminPageList(username, status,startCreateTime,endCreateTime, pageParamRequest)));
    }


    @PreAuthorize("hasAuthority('tank:tankOrders:pay')")
    @ApiOperation(value = "手动确认支付", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = {"/pay"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult success(Long id, String mark) {
        Integer userId = userService.getUserId();
        TankOrders tankOrders = tankOrdersService.getById(id);
        tankOrders.setStatus("已支付");
        tankOrders.setPayTime(new Date());
        tankOrdersService.updateById(tankOrders);
        tankEquipmentNumberService.increase(tankOrders.getStoreUserId(), tankOrders.getNumber(), tankOrders.getOrderSn(),mark);

        //发放佣金
        //      fuxiao(tankOrders);
  //      bole(tankOrders);

        return CommonResult.success();
    }
//
//    //伯乐  团队奖
//    public void bole(TankOrders tankOrders) {
//
////        fundClearingMng.add(null, tankOrders.getOrderSn(),
////                FundClearing.FundClearingType.货款.toString() + "[" + tankOrders.getOrderSn() + "]",
////                tankOrders.getPayPrice(), DateTimeUtils.getNow(), tankOrders.getOrderSn(),
////                DateTimeUtils.getNow(),
////                FundClearing.FundClearingType.货款.toString());
//
//        Config jinConfig = configMng.getByName("紧缩订单号");
//
//        TankStoreRelation tankStoreRelation = tankStoreRelationMng.getStoreUserId(tankOrders.getStoreUserId());
//        if (tankStoreRelation == null) {
//            return;
//        }
//        User createUser = userMng.get(tankStoreRelation.getTankUserId());
//        if (createUser == null) {
//            return;
//        }
//        if (createUser.getParentId() == null) {
//            return;
//        }
//
//        BigDecimal mangeCommAmt = BigDecimal.ZERO;
//        JSONObject remarkJSON = new JSONObject();
//
//
//        BigDecimal orderAmt = tankOrders.getPayPrice();
//
//
//        User user = userMng.get(createUser.getParentId());
//        XsUserCapa xsUserCapa = null;
//        BigDecimal amtbl = BigDecimal.ZERO;
//        do {
//
//            XsUserCapa currentUserCapa = xsUserCapaMng.getByUser(user.getId());
//
//            // 伯乐奖只有3星以上才有
//            if (xsUserCapa != null && (currentUserCapa != null && currentUserCapa.getXsCapaId() >= 3)) {
//                // 增加伯乐奖
//                if (ArithmeticUtils.gt(amtbl, BigDecimal.ZERO)) {
//                    {
//                        eachb(createUser, user, tankOrders, amtbl);
//                    }
//                    {
//                        remarkJSON.put("userId", user.getId());
//                        remarkJSON.put("username", user.getUsername());
//                        remarkJSON.put("amtbl", amtbl);
//                        remarkJSON.put("type", FundClearing.FundClearingType.教育培训费.toString());
//                        mangeCommAmt = mangeCommAmt.add(amtbl);
//                    }
//                    {
//                        amtbl = BigDecimal.ZERO;
//                    }
//                }
//            }
//
//            // 如果上级没到3星 伯乐奖则部分
//            // 条件满足 设置为0说明不紧缩，不设置为0 则金额存在 就会继续找下个满足条件的人，则叫紧缩
//            if ((jinConfig == null || !StringUtils.equals(jinConfig.getValue(), "1"))) {
//                amtbl = BigDecimal.ZERO;
//
//            }
//
//            // 增加管理奖 管理奖 是递归算差价 currentUserCapa 不等于空 最低要是董事
//            if (currentUserCapa != null) {
//                if (xsUserCapa == null || currentUserCapa.getXsCapaId() > xsUserCapa.getXsCapaId()) {
//
//                    XsCapa xsCapa = xsUserCapa == null ? null : xsCapaMng.get(xsUserCapa.getXsCapaId());
//                    XsCapa currentXsCapa = xsCapaMng.get(currentUserCapa.getXsCapaId());
//
//                    BigDecimal scale = currentXsCapa.getScale();
//                    if (xsCapa != null) {
//                        scale = currentXsCapa.getScale().subtract(xsCapa.getScale());
//                    }
//
//                    BigDecimal amt = orderAmt.multiply(scale.divide(new BigDecimal("100")));
//
//                    // 增加佣金
//                    fundClearingMng.add(user.getId(),
//                            "G" + tankOrders.getOrderSn() + user.getId(),
//                            createUser.getUsername() + "[充值共享仓次数]"
//                                    + FundClearing.FundClearingType.店务补贴.toString(),
//                            amt, DateTimeUtils.getNow(), tankOrders.getOrderSn(),
//                            DateTimeUtils.getNow(),
//                            FundClearing.FundClearingType.店务补贴.toString());
//
//                    remarkJSON.put("userId2", user.getId());
//                    remarkJSON.put("username2", user.getUsername());
//                    remarkJSON.put("amt", amt);
//                    remarkJSON.put("type2", FundClearing.FundClearingType.店务补贴.toString());
//                    mangeCommAmt = mangeCommAmt.add(amt);
//
//                    // 本次管理提成奖 设置为一下次的伯乐奖
//                    amtbl = amtbl.add(amt);
//                    xsUserCapa = currentUserCapa;
//                }
//            }
//
//            if (user.getParentId() == null) {
//                break;
//            }
//
//            user = userMng.get(user.getParentId());
//            {
//            }
//        } while (true);
//
//
//    }
//
//    protected void eachb(User createUser, User user, TankOrders order, BigDecimal amtb) {
//
//        BigDecimal amt = BigDecimal.ZERO;
//        {
//            amt = amtb.multiply(new BigDecimal("0.15")).setScale(2, BigDecimal.ROUND_DOWN);
//            fundClearingMng.add(user.getId(), "B" + order.getOrderSn() + user.getId(),
//                    createUser.getUsername() + "[充值共享仓]"
//                            + FundClearing.FundClearingType.培训基金.toString(),
//                    amt, DateTimeUtils.getNow(), order.getOrderSn(),
//                    DateTimeUtils.getNow(),
//                    FundClearing.FundClearingType.培训基金.toString());
//        }
//        Integer J = 2;
//        do {
//            if (user.getParentId() == null) {
//                break;
//            }
//            if (J == 2) {
//                amt = amtb.multiply(new BigDecimal("0.15")).setScale(2, BigDecimal.ROUND_DOWN);
//            } else if (J >= 3 && J <= 4) {
//                amt = amtb.multiply(new BigDecimal("0.1")).setScale(2, BigDecimal.ROUND_DOWN);
//                ;
//            } else if (J >= 5 && J <= 10) {
//                amt = amtb.multiply(new BigDecimal("0.05")).setScale(2, BigDecimal.ROUND_DOWN);
//                ;
//            } else if (J >= 11 && J <= 18) {
//                amt = amtb.multiply(new BigDecimal("0.025")).setScale(2, BigDecimal.ROUND_DOWN);
//                ;
//            }
//            user = userMng.get(user.getParentId());
//            XsUserCapa currentUserCapa = xsUserCapaMng.getByUser(user.getId());
//            // 伯乐奖只有3星以上才有
//            if (currentUserCapa != null && currentUserCapa.getXsCapaId() >= 3) {
//                {
//                    fundClearingMng.add(user.getId(),
//                            "B" + order.getOrderSn() + user.getId() + "_" + J,
//                            createUser.getUsername() + "[充值共享仓次数]"
//                                    + FundClearing.FundClearingType.培训基金.toString(),
//                            amt, DateTimeUtils.getNow(), order.getOrderSn(),
//                            DateTimeUtils.getNow(),
//                            FundClearing.FundClearingType.培训基金.toString());
//                }
//                J++;
//            }
//        } while (J <= 18);
//    }
//

}
