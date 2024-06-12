package com.jbp.admin.task.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.order.OrderExt;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.service.service.OrderExtService;
import jodd.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component("OrderAiServerTask")
public class OrderAiServerTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderAiServerTask.class);

    @Autowired
    private OrderExtService orderExtService;

    /**
     * 1分钟同步一次数据
     */
    public void aiServer() {
        logger.info("---OrderAiServerTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {

            List<OrderExt> orderExts = orderExtService.list(new QueryWrapper<OrderExt>().lambda().eq(OrderExt::getAiPushServer, false).last(" and order_no like '%PT%'  and ai_server_sn is not null"));

            for (OrderExt orderExt : orderExts) {
                List<String> list = Arrays.asList(orderExt.getAiServerSn().split(","));
                list.forEach(e -> {
                    JSONObject data = new JSONObject();
                    data.put("serviceCode", e);
                    data.put("day", orderExt.getAiDay());
                    String body = JSON.toJSONString(data);
                    HttpRequest request = HttpRequest.post("https://service.bianla.cn/serviceOrder/add/serviceCode");
                    request.contentType("application/json");
                    request.charset("utf-8");
                    String response = request.body(body).send().bodyText();
                    logger.info("返回报文 {}", response);



                });
                orderExt.setAiPushServer(true);
                orderExtService.updateById(orderExt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderAiServerTask.task" + " | msg : " + e.getMessage());
        }
    }
}
