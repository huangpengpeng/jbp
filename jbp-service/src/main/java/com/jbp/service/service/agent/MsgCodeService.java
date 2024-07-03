package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.MsgCode;
import com.jbp.common.request.PageParamRequest;

public interface MsgCodeService extends IService<MsgCode> {

    PageInfo<MsgCode> page(String phone, PageParamRequest pageParamRequest);
}
