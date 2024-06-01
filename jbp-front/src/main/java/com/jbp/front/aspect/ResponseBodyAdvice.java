package com.jbp.front.aspect;

import com.jbp.common.model.user.User;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Order(-1)
@ControllerAdvice
public class ResponseBodyAdvice implements org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice<Object> {

    private static final String DEF_SECRET = "c4ca4238a0b923820dcc509a6f75849b";

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (!(body instanceof CommonResult)) {
            return body;
        }
        CommonResult result = (CommonResult) body;
        if (result != null && StringUtils.isEmpty(result.getSecret())) {

            Integer userId = userService.getUserId();


            if (userId.intValue() > 0) {
                User user = userService.getById(userId);
                result.setSecret(user.getSignature());
            } else {
                // 默认秘钥
                result.setSecret(DEF_SECRET);
            }
        }
        return result;
    }


}
