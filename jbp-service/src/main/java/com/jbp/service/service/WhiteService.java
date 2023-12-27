package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.White;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteRequest;

import java.util.List;

public interface WhiteService extends IService<White> {
    PageInfo<White> pageList(WhiteRequest request, PageParamRequest pageParamRequest);

    Boolean addWhiten(WhiteRequest white);

    Boolean cascadingDelte(Long id);
}
