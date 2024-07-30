package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.article.ArticlePlate;
import com.jbp.common.request.ArticlePlateRequest;
import com.jbp.service.dao.ArticlePlateDao;
import com.jbp.service.service.ArticlePlateService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class ArticlePlateServiceImpl extends ServiceImpl<ArticlePlateDao, ArticlePlate> implements ArticlePlateService {

    /**
     * 添加文章板块
     * @param request 文章板块请求参数
     * @return Boolean
     */
    @Override
    public boolean add(ArticlePlateRequest request) {
        ArticlePlate one = getOne(new QueryWrapper<ArticlePlate>().lambda().eq(ArticlePlate::getName, request.getName()));
        if (ObjectUtil.isNotNull(one)) {
            throw new CrmebException("该板块已存在");
        }
        ArticlePlate articlePlate = new ArticlePlate();
        articlePlate.setName(request.getName());
        return save(articlePlate);
    }

    /**
     * 删除文章板块
     * @param id 文章板块id
     * @return Boolean
     */
    @Override
    public boolean delete(Integer id) {
        if (ObjectUtil.isNull(id)){
            throw new CrmebException("id不能为空");
        }
        return removeById(id);
    }

    /**
     * 获取文章板块列表map
     * @param ids 文章板块ids
     * @return Map
     */
    @Override
    public Map<Long, ArticlePlate> getPlateIdMapList(List<Long> ids) {
        LambdaQueryWrapper<ArticlePlate> lqw = new LambdaQueryWrapper<>();
        lqw.in(ArticlePlate::getId, ids);
        List<ArticlePlate> list = list(lqw);
        Map<Long,ArticlePlate> map = new HashMap<>();
        list.forEach(e->{
            map.put(e.getId(),e);
        });
        return map;
    }
}
