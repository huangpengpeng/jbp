package com.jbp.common.yop.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author dengmin
 * @Created 2021/4/22 上午11:38
 */
@Setter
@Getter
public class BaseResponseV1 extends Response {
    private String code;
    private String message;
    @Override
    public boolean validate() {
        return true;
    }

}
