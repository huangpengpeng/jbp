package com.jbp.common.yop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WechatConfigAddResponse extends BaseResponseV1 {

    private String status;

    private String configResult;
}
