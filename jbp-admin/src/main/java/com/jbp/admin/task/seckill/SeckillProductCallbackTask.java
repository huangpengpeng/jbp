package com.jbp.admin.task.seckill;

import com.jbp.admin.service.SeckillService;
import com.jbp.admin.task.order.OrderReceiptTask;
import com.jbp.common.utils.CrmebDateUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 秒杀商品回归库存Task
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
@Component("SeckillProductCallbackTask")
public class SeckillProductCallbackTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderReceiptTask.class);

    @Autowired
    private SeckillService seckillService;


    /**
     * 4小时同步一次数据
     */
    public void productCallbackTask() {
        // cron : 0 0 */4 * * ?
        logger.info("---SeckillProductCallbackTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            seckillService.productCallbackTask();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("BrokerageFrozenTask.task" + " | msg : " + e.getMessage());
        }
    }

}
