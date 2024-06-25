package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class RegisterMicroH5Result extends BaseYopResponse {

    private String code;

    private String msg;

    private String url;

    @Override
    public boolean validate() {
        return "MAS000000".equals(code);
    }
}
