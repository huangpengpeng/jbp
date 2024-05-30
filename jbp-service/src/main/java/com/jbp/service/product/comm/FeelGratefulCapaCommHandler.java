package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 升级等级级差
 */
@Component
public class FeelGratefulCapaCommHandler extends AbstractProductCommHandler {

    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private CapaService capaService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private CapaXsService capaXsService;


    @Override
    public Integer getType() {
        return ProductCommEnum.感恩奖.getType();
    }

    @Override
    public Integer order() {
        return 27;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        return true;
    }

    @Override
    public FeelGratefulCapaCommHandler.Rule getRule(ProductComm productComm) {
        try {
            ProductCommConfig config = productCommConfigService.getByType(getType());
            Rule rules = JSONObject.parseObject(config.getRatioJson(), Rule.class);
            return rules;
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, List<OrderDetail> orderDetails, LinkedList<CommCalculateResult> resultList) {
        List<FundClearing> fundClearingList = fundClearingService.list(new QueryWrapper<FundClearing>().select(" sum(send_amt) as send_amt,uid,external_no,comm_name ").lambda().eq(FundClearing::getExternalNo, order.getOrderNo()).last(" group by  uid ,comm_name"));

        if (fundClearingList.isEmpty()) {
            return;
        }
        FeelGratefulCapaCommHandler.Rule rule = getRule(null);
        List<FeelGratefulCapaCommHandler.LevelRatio> levelRatios = rule.getLevelRatios();
        for (FundClearing fundClearing : fundClearingList) {
            List<Integer> userList = new ArrayList<>();
            User user = userService.getById(fundClearing.getUid());
            //使用星级计算
            if (fundClearing.getCommName().equals(ProductCommEnum.星级级差佣金无伯乐.getName())) {

                UserCapaXs userCapaxs = userCapaXsService.getByUser(fundClearing.getUid());
                CapaXs capa = capaXsService.getById(userCapaxs.getCapaId());
                Integer pid = invitationService.getPid(fundClearing.getUid());

                do {
                    if (pid == null || pid == 0) {
                        break;
                    }
                    UserCapaXs pidCapaXs = userCapaXsService.getByUser(pid);
                    CapaXs pCapa = capaXsService.getById(pidCapaXs.getCapaId());
                    //上级等级为空，紧缩
                    if (pidCapaXs == null) {
                        pid = invitationService.getPid(pid);
                        continue;
                    }
                    //上级等级为小于设计等级，紧缩
                    if (pidCapaXs.getCapaId() < rule.getCapaXsId()) {
                        pid = invitationService.getPid(pid);
                        continue;
                    }
                    //上级等级大于当前用户等级，直接退出
                    if (pidCapaXs.getCapaId() > userCapaxs.getCapaId()) {
                        break;
                    }
                    //同级或者是低一等级的上级用户，计算感恩奖
                    if (pCapa.getPCapaId() == capa.getId() || pCapa.getId() == capa.getId()) {
                        userList.add(pid);
                    }
                    pid = invitationService.getPid(pid);

                } while (userList.size() < 3);


            } else {

                //使用等级计算
                UserCapa userCapa = userCapaService.getByUser(fundClearing.getUid());
                Capa capa = capaService.getById(userCapa.getCapaId());
                Integer pid = invitationService.getPid(fundClearing.getUid());
                do {
                    if (pid == null ||  pid == 0) {
                        break;
                    }
                    UserCapa pidCapa = userCapaService.getByUser(pid);
                    Capa pCapa = capaService.getById(pidCapa.getCapaId());
                    //上级等级为空，紧缩
                    if (pidCapa == null) {
                        continue;
                    }
                    //上级等级为小于设计等级，紧缩
                    if (pidCapa.getCapaId() < rule.getCapaId()) {
                        continue;
                    }
                    //上级等级大于当前用户等级，直接退出
                    if (pidCapa.getCapaId() > userCapa.getCapaId()) {
                        break;
                    }
                    //同级或者是低一等级的上级用户，计算感恩奖
                    if (pCapa.getPCapaId() == capa.getId() || pCapa.getId() == capa.getId()) {
                        userList.add(pid);
                    }

                    pid = invitationService.getPid(pid);

                } while (userList.size() < 3);
            }

            BigDecimal amtRatio = BigDecimal.ZERO;
            //分佣
            if(!userList.isEmpty()) {
                for (FeelGratefulCapaCommHandler.LevelRatio levelRatio : levelRatios) {

                    if (levelRatio.number == userList.size()) {
                        for (int i = 0; i < levelRatio.getRatio().size(); i++) {
                            fundClearingService.create(userList.get(i), order.getOrderNo(), ProductCommEnum.感恩奖.getName(), fundClearing.getSendAmt().multiply(levelRatio.getRatio().get(i)),
                                    null, user.getAccount() + "获取到的" + ProductCommEnum.感恩奖.getName(), "");

                            amtRatio= amtRatio.add(levelRatio.getRatio().get(i));

                        }
                        break;
                    }

                }

                List<FundClearing> fundClearingList2 = fundClearingService.list(new QueryWrapper<FundClearing>().lambda().eq(FundClearing::getExternalNo, order.getOrderNo()).eq(FundClearing::getCommName,fundClearing.getCommName()).ne(FundClearing::getCommName,ProductCommEnum.感恩奖.getName()).eq(FundClearing::getUid,user.getId()));
                //减去用户发放的佣金金额
                for(FundClearing fundClearing1 :fundClearingList2){
                    fundClearing1.setSendAmt(fundClearing1.getSendAmt().subtract( fundClearing1.getSendAmt().multiply(amtRatio)));
                    fundClearing1.setCommAmt(fundClearing1.getCommAmt().subtract(fundClearing1.getCommAmt().multiply(amtRatio)));
                    fundClearingService.updateById(fundClearing1);
                }

            }


        }






    }

    /**
     * 等级比例
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 等级
         */
        private Long capaId;
        /**
         * 等级
         */
        private Long capaXsId;
        /**
         * 系数
         */
        private BigDecimal scale;

        /**
         * 模式 比例or金额
         */
        private List<LevelRatio> levelRatios;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelRatio {
        /**
         * 获取佣金人数
         */
        private Integer number;

        /**
         * 比例
         */
        private List<BigDecimal> ratio;

    }


}
