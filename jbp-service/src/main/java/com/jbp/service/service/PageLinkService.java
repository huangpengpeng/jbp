package com.jbp.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.page.PageLink;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.page.PageLinkSearchRequest;

import java.util.List;

/**
* @author dazongzi
* @description PageLinkService 接口
* @date 2023-05-16
*/
public interface PageLinkService extends IService<PageLink> {

    List<PageLink> getList(PageLinkSearchRequest request, PageParamRequest pageParamRequest);
}
