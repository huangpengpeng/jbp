package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.article.ArticlePlate;
import com.jbp.common.request.ArticlePlateRequest;

import java.util.List;
import java.util.Map;

public interface ArticlePlateService extends IService<ArticlePlate> {

    /**
     * 添加文章板块
     * @param request 文章板块请求参数
     * @return Boolean
     */
    boolean add(ArticlePlateRequest request);

    /**
     * 删除文章板块
     * @param id 文章板块id
     * @return Boolean
     */
    boolean delete(Integer id);

    /**
     * 获取文章板块列表map
     * @param ids 文章板块ids
     * @return Map
     */
    Map<Long,ArticlePlate> getPlateIdMapList(List<Long> ids);
}
