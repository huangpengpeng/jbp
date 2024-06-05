package com.jbp.admin.controller.publicly;

import com.beust.jcommander.internal.Maps;
import com.jbp.common.encryptapi.EncryptIgnore;
import com.jbp.common.result.CommonResult;
import com.jbp.service.erp.service.JushuitanOrderSvc;
import com.jbp.service.erp.tools.Constants;
import com.jbp.service.erp.tools.SignUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import com.jbp.common.encryptapi.EncryptIgnore;

@Slf4j
@Controller
@RequestMapping("api/publicly/jushuitan")
@Api(tags = "聚水潭控制器")
@EncryptIgnore
public class AuthorizeAct {

    @Autowired
    private Environment environment;
    @Autowired
    private JushuitanOrderSvc jushuitanOrderSvc;


    @ApiOperation(value = "授权订单", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = {"/sync2"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<String> sync2() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("app_key", environment.getProperty("jushuitan.appKey"));
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("charset", Constants.CHARSET);
        String state = environment.getProperty("jushuitan.url");
        params.put("state", state);
        String sign = SignUtil.getSign(environment.getProperty("jushuitan.appSecret"), params);
        /**
         * 拼接跳转地址
         */
        String url = Constants.AUTH_URL.replace("[app_key]", (String) params.get("app_key"))
                .replace("[timestamp]", (String) params.get("timestamp")).replace("[charset]", (String) params.get("charset"))
                .replace("[sign]", sign).replace("[state]", state);
        //	return "redirect:"+url;
        return CommonResult.success(url);

    }


    @ApiOperation(value = "同步订单", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = {"/sync"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<String> sync() {
        jushuitanOrderSvc.sync("");
        return CommonResult.success("同步成功");
    }


}
