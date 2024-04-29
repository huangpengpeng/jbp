package com.jbp.front;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.result.CommonResult;
import com.jbp.front.controller.tank.TankAct;
import com.jbp.service.service.agent.WalletFlowService;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Map;
import java.util.TreeMap;

/**
 * 程序主入口
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
@EnableAsync //开启异步调用
@EnableSwagger2
@Configuration
@EnableTransactionManagement
@SpringBootApplication(exclude = {WxMaAutoConfiguration.class, DataSourceAutoConfiguration.class}) //去掉数据源
@ComponentScan(basePackages = {"com.jbp", "com.jbp.front"})
@MapperScan(basePackages = {"com.jbp.**.dao"})
public class JbpFrontApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(JbpFrontApplication.class, args);
        Environment bean = run.getBean(Environment.class);
        System.out.println("spring.datasource.url=" + bean.getProperty("spring.datasource.url"));
        System.out.println("启动完成");


//        Map<String, String> params = new TreeMap<String, String>();
//
//        HttpRequest request = HttpRequest.post("https://system.swgzsb.com/api/external_service/user/login");
//        request.contentType("application/json");
//        request.charset("utf-8");
//
//        params.put("appid", "2t7VHlqR0vZn5x1EijFYJom8SKkIDTbc");
//        params.put("secret", "uhQgSRoyM3FifxwnpDatO7TAUP2Nmd1J");
//        request.body(JSON.toJSONString(params));
//        HttpResponse response1 = request.send();
//        String respJson = response1.bodyText();
//        JSONObject goodsJson = JSONObject.parseObject(respJson).getJSONObject("data");
//
//
//
//        Map<String, Object> params2 = new TreeMap<String, Object>();
//        HttpRequest request2 = HttpRequest.post("https://system.swgzsb.com/api/external_service/config/updateEquipmentQrcodeRule");
//        request2.contentType("application/json");
//        request2.charset("utf-8");
//
//      //  String userPrefix = environment.getProperty("gxc.url");
//        params2.put("token",goodsJson.get("token"));
//        params2.put("equipmentQrcodeRule", "https://sm.fnyss.cc/equipment_id/?equipmentSn={$equipment_id}");
//        request2.body(JSON.toJSONString(params2));
//        HttpResponse response3 = request2.send();
//        String respJson3 = response3.bodyText();
//        JSONObject goodsJson3 = JSONObject.parseObject(respJson3).getJSONObject("data");
////        return CommonResult.success(goodsJson);



    }
}
