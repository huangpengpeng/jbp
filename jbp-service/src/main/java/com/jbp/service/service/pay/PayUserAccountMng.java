package com.jbp.service.service.pay;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.pay.PayUserAccount;
import com.jbp.common.model.pay.PayUserSubMerchant;
import com.jbp.common.request.PageParamRequest;

public interface PayUserAccountMng extends IService<PayUserAccount> {

    PageInfo<PayUserAccount> page(PageParamRequest pageParamRequest, String accountName, String accountNo, Integer merId);
}
