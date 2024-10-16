package com.jbp.service.service.pay;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.pay.PayChannel;
import com.jbp.common.model.pay.PayCompanyInfo;
import com.jbp.common.request.PageParamRequest;

public interface PayChannelMng extends IService<PayChannel> {
    PageInfo<PayChannel> page(PageParamRequest pageParamRequest, String name);

    PayChannel getByCode(String code);
}
