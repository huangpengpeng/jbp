package com.jbp.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.page.PageCategory;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.page.PageCategorySearchRequest;

import java.util.List;

/**
* @author dazongzi
* @description PageCategoryService 接口
* @date 2023-05-16
*/
public interface PageCategoryService extends IService<PageCategory> {

    List<PageCategory> getList(PageCategorySearchRequest request, PageParamRequest pageParamRequest);
}
