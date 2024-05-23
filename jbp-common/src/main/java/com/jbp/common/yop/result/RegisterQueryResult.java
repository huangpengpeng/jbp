package com.jbp.common.yop.result;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RegisterQueryResult extends RegisterResult {
    // 审核意见 申请已驳回或者申请已完成时，回传的审核已经
    private String auditOpinion;
}
