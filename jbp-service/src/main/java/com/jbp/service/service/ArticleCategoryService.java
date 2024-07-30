package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.article.ArticleCategory;
import com.jbp.common.request.ArticleCategoryFrontRequest;
import com.jbp.common.request.ArticleCategoryListRequest;
import com.jbp.common.request.ArticleCategoryRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ArticleCategoryResponse;

import java.util.List;

/**
 * ArticleCategoryService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface ArticleCategoryService extends IService<ArticleCategory> {

    /**
     * 获取文章分类列表
     */
    PageInfo<ArticleCategoryResponse> getAdminList(ArticleCategoryListRequest request, PageParamRequest pageParamRequest);

    /**
     * 文章新增
     *
     * @param request 文章分类新增对象
     * @return Boolean
     */
    Boolean create(ArticleCategoryRequest request);

    /**
     * 文章分类删除
     *
     * @param id 文章分类id
     * @return Boolean
     */
    Boolean deleteById(Integer id);

    /**
     * 文章分类修改
     *
     * @param request 文章分类修改参数
     */
    Boolean edit(ArticleCategoryRequest request);

    /**
     * 文章分类开关
     * @param id 文章分类ID
     * @return Boolean
     */
    Boolean categorySwitch(Integer id);

    /**
     * 获取移动端文章分类列表
     */
    List<ArticleCategoryResponse> getFrontList(ArticleCategoryFrontRequest request);

    /**
     * 获取文章分类详情
     * @param id 分类ID
     * @return ArticleCategory
     */
    ArticleCategory detail(Integer id);

}
