package com.jbp.admin.task.order;

import com.jbp.common.lianlian.result.ReceiptDownloadResult;
import com.jbp.common.model.agent.LztTransfer;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.model.agent.LztWithdrawal;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.OrderTaskService;

import com.jbp.service.service.agent.LztReceiptService;
import com.jbp.service.service.agent.LztTransferMorepyeeService;
import com.jbp.service.service.agent.LztTransferService;
import com.jbp.service.service.agent.LztWithdrawalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户取消订单task任务
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Component("OrderCancelTask")
public class OrderCancelTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderCancelTask.class);

    @Autowired
    private LztTransferMorepyeeService lztTransferMorepyeeService;
    @Autowired
    private LztWithdrawalService lztWithdrawalService;
    @Autowired
    private LztTransferService lztTransferService;
    @Autowired
    private LztReceiptService lztReceiptService;





    /**
     * 1分钟同步一次数据
     */
    public void userCancel() {
        // cron : 0 */1 * * * ?
        logger.info("---OrderCancelTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
//            orderTaskService.cancelByUser();
            // 查询订单存在token 没 文件的记录

            List<LztTransfer> lztTransferList = lztTransferService.getWaitDownloadList();
            for (LztTransfer lztTransfer : lztTransferList) {
                ReceiptDownloadResult download = lztReceiptService.download(lztTransfer.getTxnSeqno());
                if (download == null || StringUtils.isEmpty(download.getReceipt_sum_file())) {
                    lztTransfer.setReceiptStatus(0);
                    lztTransferService.updateById(lztTransfer);
                }
            }

            List<LztWithdrawal> lztWithdrawalList = lztWithdrawalService.getWaitDownloadList();
            for (LztWithdrawal lztWithdrawal : lztWithdrawalList) {
                ReceiptDownloadResult download = lztReceiptService.download(lztWithdrawal.getTxnSeqno());
                if (download == null || StringUtils.isEmpty(download.getReceipt_sum_file())) {
                    lztWithdrawal.setReceiptStatus(0);
                    lztWithdrawalService.updateById(lztWithdrawal);
                }
            }

            List<LztTransferMorepyee> lztTransferMorepyeeList = lztTransferMorepyeeService.getWaitDownloadList();
            for (LztTransferMorepyee lztTransferMorepyee : lztTransferMorepyeeList) {
                ReceiptDownloadResult download = lztReceiptService.download(lztTransferMorepyee.getTxnSeqno());
                if (download == null || StringUtils.isEmpty(download.getReceipt_sum_file())) {
                    lztTransferMorepyee.setReceiptStatus(0);
                    lztTransferMorepyeeService.updateById(lztTransferMorepyee);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderCancelTask.task" + " | msg : " + e.getMessage());
        }
    }
}
