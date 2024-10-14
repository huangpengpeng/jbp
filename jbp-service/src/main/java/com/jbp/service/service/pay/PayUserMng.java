package com.jbp.service.service.pay;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.pay.PayUser;

public interface PayUserMng extends IService<PayUser> {

    PayUser getByAppKey(String appKey);
}
