package com.jbp.service.service.pay;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.pay.PaySubMerchant;
import com.jbp.common.model.pay.PayUserSubMerchant;
import com.jbp.common.request.PageParamRequest;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public interface PayUserSubMerchantMng extends IService<PayUserSubMerchant> {

    PageInfo<PayUserSubMerchant> page(PageParamRequest pageParamRequest, String payUserAccountName, String payUserAccountNo);

    PayUserSubMerchant get(Long payUserId, String method);

    List<PayUserSubMerchant> getByPayUser(Long payUserId);

}
