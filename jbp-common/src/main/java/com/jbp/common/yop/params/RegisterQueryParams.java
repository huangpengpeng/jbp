package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class RegisterQueryParams extends BaseYopRequest {

    private String parentMerchantNo;

    @NotBlank(message = "入网请求号不能为空")
    private String requestNo;

    public RegisterQueryParams(String requestNo){
        this.requestNo = requestNo;
    }

}
