package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.article.ArticleCategory;
import com.jbp.common.model.article.ArticlePlate;
import com.jbp.common.request.ArticleCategoryFrontRequest;
import com.jbp.common.request.ArticleCategoryListRequest;
import com.jbp.common.request.ArticleCategoryRequest;
import com.jbp.common.response.ArticleCategoryResponse;
import com.jbp.service.dao.ArticleCategoryDao;
import com.jbp.service.service.ArticleCategoryService;
import com.jbp.service.service.ArticlePlateService;
import com.jbp.service.service.ArticleService;
import com.jbp.service.service.SystemAttachmentService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ArticleCategoryServiceImpl 接口实现
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
@Service
public class ArticleCategoryServiceImpl extends ServiceImpl<ArticleCategoryDao, ArticleCategory> implements ArticleCategoryService {

    @Resource
    private ArticleCategoryDao dao;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private ArticlePlateService articlePlateService;

    /**
     * 获取文章分类列表
     */
    @Override
    public List<ArticleCategoryResponse> getAdminList(ArticleCategoryListRequest request) {
        LambdaQueryWrapper<ArticleCategory> lqw = Wrappers.lambdaQuery();
        lqw.eq(ArticleCategory::getIsDel, false);
        lqw.eq(!ObjectUtil.isNull(request.getPlateId()),ArticleCategory::getPlateId,request.getPlateId());
        lqw.orderByDesc(ArticleCategory::getPlateId);
        lqw.orderByDesc(ArticleCategory::getSort, ArticleCategory::getId);
        List<ArticleCategory> categoryList = dao.selectList(lqw);
        if (CollUtil.isEmpty(categoryList)) {
            return CollUtil.newArrayList();
        }
        List<Long> plateIdList = categoryList.stream().map(ArticleCategory::getPlateId).collect(Collectors.toList());
        Map<Long, ArticlePlate> plateIdMapList = articlePlateService.getPlateIdMapList(plateIdList);
        List<ArticleCategoryResponse> responseList = CollUtil.newArrayList();
        for (ArticleCategory category : categoryList) {
            ArticleCategoryResponse response = new ArticleCategoryResponse();
            BeanUtils.copyProperties(category, response);
            ArticlePlate articlePlate = plateIdMapList.get(category.getPlateId());
            response.setPlateName(articlePlate != null ? articlePlate.getName() : "");
            responseList.add(response);
        }
        return responseList;
    }

    /**
     * 文章分类新增
     *
     * @param request 文章分类新增对象
     * @return Boolean
     */
    @Override
    public Boolean create(ArticleCategoryRequest request) {
        ArticleCategory articleCategory = new ArticleCategory();
        BeanUtils.copyProperties(request, articleCategory);
        articleCategory.setId(null);
        if (StrUtil.isNotBlank(request.getIcon())) {
            articleCategory.setIcon(systemAttachmentService.clearPrefix(articleCategory.getIcon()));
        }
        articleCategory.setStatus(true);
        return save(articleCategory);
    }

    /**
     * 文章分类删除
     *
     * @param id 文章分类id
     * @return Boolean
     */
    @Override
    public Boolean deleteById(Integer id) {
        ArticleCategory articleCategory = getByIdException(id);
        if (articleService.isUseCategory(id)) {
            throw new CrmebException("有文章使用此分类，请先修改对应文章分类");
        }
        articleCategory.setIsDel(true);
        return dao.updateById(articleCategory) > 0;
    }

    private ArticleCategory getByIdException(Integer id) {
        ArticleCategory articleCategory = getById(id);
        if (ObjectUtil.isNull(articleCategory)) {
            throw new CrmebException("文章分类不存在");
        }
        if (articleCategory.getIsDel()) {
            throw new CrmebException("文章分类不存在");
        }
        return articleCategory;
    }

    /**
     * 文章分类修改
     *
     * @param request 文章分类修改参数
     */
    @Override
    public Boolean edit(ArticleCategoryRequest request) {
        if (ObjectUtil.isNull(request.getId()) || request.getId() < 1) {
            throw new CrmebException("请传入分类ID");
        }
        getByIdException(request.getId());
        ArticleCategory articleCategory = new ArticleCategory();
        BeanUtils.copyProperties(request, articleCategory);
        if (StrUtil.isNotBlank(request.getIcon())) {
            articleCategory.setIcon(systemAttachmentService.clearPrefix(articleCategory.getIcon()));
        }
        return updateById(articleCategory);
    }

    /**
     * 文章分类开关
     * @param id 文章分类ID
     * @return Boolean
     */
    @Override
    public Boolean categorySwitch(Integer id) {
        ArticleCategory articleCategory = getByIdException(id);
        LambdaUpdateWrapper<ArticleCategory> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ArticleCategory::getStatus, !articleCategory.getStatus());
        wrapper.eq(ArticleCategory::getId, id);
        return update(wrapper);
    }

    /**
     * 获取移动端文章分类列表
     */
    @Override
    public List<ArticleCategoryResponse> getFrontList(ArticleCategoryFrontRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException("板块id不能为空！");
        }
        LambdaQueryWrapper<ArticleCategory> lqw = Wrappers.lambdaQuery();
        lqw.eq(ArticleCategory::getIsDel, false);
        lqw.eq(ArticleCategory::getStatus, true);
        lqw.eq(ArticleCategory::getPlateId, request.getId());
        lqw.orderByDesc(ArticleCategory::getSort, ArticleCategory::getId);
        List<ArticleCategory> categoryList = dao.selectList(lqw);
        if (CollUtil.isEmpty(categoryList)) {
            return CollUtil.newArrayList();
        }
        List<ArticleCategoryResponse> responseList = CollUtil.newArrayList();
        for (ArticleCategory category : categoryList) {
            ArticleCategoryResponse response = new ArticleCategoryResponse();
            BeanUtils.copyProperties(category, response);
            responseList.add(response);
        }
        return responseList;
    }

    /**
     * 获取文章分类详情
     * @param id 分类ID
     * @return ArticleCategory
     */
    @Override
    public ArticleCategory detail(Integer id) {
        if (ObjectUtil.isNull(id)) {
            throw new CrmebException("分类ID不能为空！");
        }
        ArticleCategory articleCategory = getById(id);
        if (ObjectUtil.isNull(articleCategory)) {
            throw new CrmebException("该分类不存在！");
        }
        return articleCategory;
    }
}

