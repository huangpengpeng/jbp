package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RegisterResult extends BaseYopResponse {
    //入网请求号
    private String requestNo;
    //申请单编号
    private String applicationNo;
    //申请状态
    // {"auditOpinion":"","applicationStatus":"COMPLETED","applicationNo":"TYSHRW20230506161115310852"
    // ,"requestNo":"YT14246987043868","merchantNo":"10089299159"}
    // REVIEWING: 申请审核中， REVIEW_BACK 申请已驳回 ,BUSINESS_OPING 业务开通中 , COMPLETED 申请完成
    private String applicationStatus;

    private String merchantNo;

    @Override
    public boolean validate() {
        return true;
    }
}
