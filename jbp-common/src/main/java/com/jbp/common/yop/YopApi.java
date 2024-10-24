package com.jbp.common.yop;

import com.jbp.common.utils.JacksonTool;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.yop.constants.YopURI;
import com.jbp.common.yop.dto.Response;
import com.jbp.common.yop.dto.WechatConfigAddResponse;
import com.jbp.common.yop.dto.WechatConfigQueryResponse;
import com.jbp.common.yop.params.WechatConfigAddRequest;
import com.jbp.common.yop.params.WechatConfigQueryRequest;
import com.jbp.common.yop.utils.BeanValidator;
import com.yeepay.yop.sdk.service.common.YopClient;
import com.yeepay.yop.sdk.service.common.request.YopRequest;
import com.yeepay.yop.sdk.service.common.response.YopResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;

@Component
@Slf4j
public class YopApi {

    @Resource
    private YopClient yopClient;

    public WechatConfigAddResponse wechatConfigAdd(WechatConfigAddRequest request) {
        return makeRequest(YopURI.wechatconfigadd, request, WechatConfigAddResponse.class);
    }

    public WechatConfigQueryResponse wechatConfigQuery(WechatConfigQueryRequest request){
        return makeRequest(YopURI.wechatconfigquery, request, WechatConfigQueryResponse.class);
    }



    public <T> T makeRequest(YopURI uri, BaseYopRequest parameters, Class<T> responseClass) {
        //这里做参数校验， 确保传递到易宝接口的参数都是有效的
        BeanValidator.ValidationResult validationResult = BeanValidator.validateObject(parameters);
        if (validationResult.isHasError()) {
            Collection<String> errors = validationResult.getErrors().values();
            String errorMessage = StringUtils.joinWith(",", errors);
            throw new RuntimeException(errorMessage);
        }
        //生成易宝请求
        YopRequest request = new YopRequest(uri.getValue(), uri.getMethod());
        //设置参数
        Map<String, Object> mapObj = JacksonTool.objectToMap(parameters);
        for (Map.Entry<String, Object> entry : mapObj.entrySet()) {
            if (entry.getValue() != null) {
                request.addParameter(entry.getKey(), entry.getValue());
            }
        }
        String requestText = JacksonTool.toJsonString(request.getParameters().asMap());
        log.info("易宝请求参数" + requestText);
        try {
            YopResponse response = yopClient.request(request);
            String responseText = JacksonTool.toJsonString(response);
            log.info("易宝返回参数" + responseText);
            //结果转换成对应的response
            Response resp = (Response) JacksonTool.toObject(response.getStringResult(), responseClass);
            return (T) resp;
        } catch (Exception e) {
            log.error("易宝请求异常:" + e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

}
