package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.FundClearingItem;
import com.jbp.common.model.agent.FundClearingProduct;
import com.jbp.common.model.agent.UserInfo;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.FundClearingRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface FundClearingService extends IService<FundClearing> {

    PageInfo<FundClearing> pageList(String uniqueNo, String externalNo, Date startClearingTime, Date endClearingTime, Date startCreateTime, Date endCreateTime, String status, PageParamRequest pageParamRequest);

    FundClearing create(Integer uid, String externalNo, String commName, BigDecimal commAmt,
                     List<FundClearingItem> items, List<FundClearingProduct> productList,
                     String description, String remark);

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

    String exportOrder(String uniqueNo, String externalNo, Date startClearingTime, Date endClearingTime, Date starteCreateTime, Date endCreateTime, String status);

    void updateRemark(Long id, String remark);
}
