package com.jbp.common.yop.result;

import com.yeepay.yop.sdk.model.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * @Author dengmin
 * @Created 2021/4/14 下午6:57
 */
@Setter
@Getter
@NoArgsConstructor
public class MerFileUploadResponse extends BaseResponse {
    private String merQualUrl;
    private String merQualOriginalFileName;
}
