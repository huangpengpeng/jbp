package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.category.Category;
import com.jbp.common.request.CategoryRequest;
import com.jbp.common.request.CategorySearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.CategoryTreeVo;

import java.util.List;

/**
*   CategoryService 接口
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
public interface CategoryService extends IService<Category> {

    List<Category> getList(CategorySearchRequest request, PageParamRequest pageParamRequest);

    int delete(Integer id);

    /**
     * 获取树形结构数据
     * @param type 分类
     * @param status 状态
     * @param name 名称
     * @return List
     */
    List<CategoryTreeVo> getListTree(Integer type, Integer status, String name);

    List<Category> getByIds(List<Integer> ids);

    Boolean update(CategoryRequest request, Integer id);

    Boolean updateStatus(Integer id);

    /**
     * 新增分类表
     */
    Boolean create(CategoryRequest categoryRequest);

    /**
     * 获取分类详情
     * @param id 分了ID
     * @return Category
     */
    Category getByIdException(Integer id);
}
