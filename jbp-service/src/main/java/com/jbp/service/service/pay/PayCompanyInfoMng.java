package com.jbp.service.service.pay;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.pay.PayCompanyInfo;
import com.jbp.common.request.PageParamRequest;

public interface PayCompanyInfoMng extends IService<PayCompanyInfo> {

    PageInfo<PayCompanyInfo> page(PageParamRequest pageParamRequest, String name, String status);

}
