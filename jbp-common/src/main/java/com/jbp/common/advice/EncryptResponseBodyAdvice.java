package com.jbp.common.advice;

import com.jbp.common.encryptapi.AESUtils;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.JacksonTool;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.UnsupportedEncodingException;
@Order(1)
@ControllerAdvice
@ConditionalOnProperty(prefix = "com.jbp.common.advice.EncryptResponse", name = "enabled" , havingValue = "true", matchIfMissing = true)
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private String charset = "UTF-8";


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        boolean encrypt = NeedCrypto.needEncrypt(methodParameter);
        if (!encrypt) {
            return body;
        }
        CommonResult result = (CommonResult) body;
        if (null == result) {
            return body;
        }
        String secret = result.getSecret();
        result.setSecret("");
        String xx = JacksonTool.toJsonString(result);
        byte[] bs = AESUtils.encrypt(xx.getBytes(), secret.substring(0, 32).getBytes(), "3IPLa89}668@23)!");
        String encodeBase64String = Base64.encodeBase64String(bs);
        try {
            return encodeBase64String.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
