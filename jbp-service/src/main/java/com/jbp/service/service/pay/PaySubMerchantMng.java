package com.jbp.service.service.pay;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.pay.PayChannel;
import com.jbp.common.model.pay.PaySubMerchant;
import com.jbp.common.request.PageParamRequest;

public interface PaySubMerchantMng extends IService<PaySubMerchant> {

    PageInfo<PaySubMerchant> page(PageParamRequest pageParamRequest, String merchantName, String merchantNo);
}
