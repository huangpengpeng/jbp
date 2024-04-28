package com.jbp.service.service.agent;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.FundClearingItem;
import com.jbp.common.model.agent.FundClearingProduct;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.FundClearingVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FundClearingService extends IService<FundClearing> {

    PageInfo<FundClearing> pageList(String uniqueNo, String externalNo, Date startClearingTime,
                                    Date endClearingTime, Date startCreateTime, Date endCreateTime,
                                    String status,Integer uid,String teamName,String description,
                                    String commName, Boolean ifRefund, PageParamRequest pageParamRequest);

    FundClearing create(Integer uid, String externalNo, String commName, BigDecimal commAmt, List<FundClearingProduct> productList,
                     String description, String remark);

    List<FundClearing> getByExternalNo(String externalNo, List<String> statusList);

    List<FundClearing> getByUser(Integer uid, String commName, List<String> statusList);


    /**
     * 修改发放金额
     */
    void updateSendAmt(Long id, BigDecimal sendAmt, String remark);


    /**
     * 更新为待审核
     */
    void updateWaitAudit(String externalNo, String remark);


    /**
     * 更新待审核
     * @param ids 记录ids
     * @param remark
     */
    void updateWaitAudit(List<Long> ids, String remark);


    /**
     * 更新待出款
     * @param ids 记录ids
     * @param remark
     */
    void updateWaitSend(List<Long> ids, String remark);


    /**
     * 更新已出款
     * @param ids 记录ids
     * @param remark
     */
    void updateSend(List<Long> ids, String remark);

    /**
     * 更新已取消
     * @param ids 记录ids
     * @param remark
     */
    void updateCancel(List<Long> ids, String remark);

    /**
     * 更新已拦截
     * @param ids 记录ids
     * @param remark
     */
    void updateIntercept(List<Long> ids, String remark);

    /**
     * 佣金发放导出
     * @return
     */
    List<FundClearingVo> exportFundClearing(String uniqueNo, String externalNo,
                                            Date startClearingTime, Date endClearingTime, Date startCreateTime, Date endCreateTime,
                                            String status,Integer uid,String teamName,String description,String commName, Boolean ifRefund);

    void updateRemark(Long id, String remark);

    Map<String, Object> totalGet(Integer uid);

    PageInfo<FundClearing> flowGet(Integer uid, Integer headerStatus, PageParamRequest pageParamRequest);

    void updateIfRefund(List<Long> ids, String remark);

    boolean hasCreate(String orderNo, String commName);

    BigDecimal getSendCommAmt(Integer uid, Date start, Date end, String ...commName);


   BigDecimal getUserTotal(Integer uid);

    BigDecimal getUserTotalMonth(Integer uid,String month);
    List<Map<String,Object>> getUserTotalMonthList(Integer uid,String month);


    BigDecimal getUserTotalContMonth(Integer uid,String month);

    BigDecimal getUserTotalDay(Integer uid,String day);
}
