package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.constants.Constants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.system.GroupConfig;
import com.jbp.service.dao.GroupConfigDao;
import com.jbp.service.service.GroupConfigService;
import com.jbp.service.service.SystemAttachmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * SystemConfigServiceImpl 接口实现
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
public class GroupConfigServiceImpl extends ServiceImpl<GroupConfigDao, GroupConfig> implements GroupConfigService {

    @Resource
    private GroupConfigDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 通过tag获取数据列表
     *
     * @param tag 标签
     * @param sortRule 排序规则
     * @param status 展示状态：1-展示
     */
    @Override
    public List<GroupConfig> findByTag(Integer tag, String sortRule, Boolean status) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, tag);
        lqw.eq(GroupConfig::getIsDel, 0);
        if (ObjectUtil.isNotNull(status)) {
            lqw.eq(GroupConfig::getStatus, status ? 1 : 0);
        }
        if (StrUtil.isBlank(sortRule) || sortRule.equals(Constants.SORT_ASC)) {
            lqw.orderByAsc(GroupConfig::getSort);
        } else {
            lqw.orderByDesc(GroupConfig::getSort);
        }
        lqw.orderByDesc(GroupConfig::getId);
        return dao.selectList(lqw);
    }

    /**
     * 通过tag获取数据列表
     *
     * @param tag 标签
     * @param merId 商户ID
     * @param sortRule 排序规则
     * @param status 展示状态：1-展示
     */
    @Override
    public List<GroupConfig> findByTag(Integer tag, Integer merId, String sortRule, Boolean status) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, tag);
        lqw.eq(GroupConfig::getMerId, merId);
        lqw.eq(GroupConfig::getIsDel, 0);
        if (ObjectUtil.isNotNull(status)) {
            lqw.eq(GroupConfig::getStatus, status ? 1 : 0);
        }
        if (StrUtil.isBlank(sortRule) || sortRule.equals(Constants.SORT_ASC)) {
            lqw.orderByAsc(GroupConfig::getSort);
        } else {
            lqw.orderByDesc(GroupConfig::getSort);
        }
        lqw.orderByDesc(GroupConfig::getId);
        return dao.selectList(lqw);
    }

    /**
     * 添加数据
     * @param configList 组合配置
     */
    @Override
    public Boolean saveList(List<GroupConfig> configList) {
        Integer tag = configList.get(0).getTag();
        configList.forEach(groupConfig -> {
            if (StrUtil.isNotBlank(groupConfig.getImageUrl())) {
                groupConfig.setImageUrl(systemAttachmentService.clearPrefix(groupConfig.getImageUrl()));
            }
        });
        Integer merId = Optional.ofNullable(configList.get(0).getMerId()).orElse(0);
        return transactionTemplate.execute(e -> {
            deleteByTag(tag, merId);
            saveBatch(configList);
            return Boolean.TRUE;
        });
    }

    /**
     * 按tag删除
     */
    @Override
    public Boolean deleteByTag(Integer tag, Integer merId) {
        LambdaUpdateWrapper<GroupConfig> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(GroupConfig::getIsDel, 1);
        wrapper.eq(GroupConfig::getTag, tag);
        wrapper.eq(GroupConfig::getIsDel, 0);
        if (ObjectUtil.isNotNull(merId) && merId > 0) {
            wrapper.eq(GroupConfig::getMerId, merId);
        }
        return update(wrapper);
    }

    private GroupConfig getLastOneByTag(Integer tag) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, tag);
        lqw.eq(GroupConfig::getIsDel, 0);
        lqw.orderByAsc(GroupConfig::getSort);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 查询tag下数据条数
     */
    @Override
    public Integer getCountByTag(Integer tag) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, tag);
        lqw.eq(GroupConfig::getIsDel, 0);
        return dao.selectCount(lqw);
    }

    @Override
    public GroupConfig getByIdException(Integer id) {
        GroupConfig groupConfig = getById(id);
        if (ObjectUtil.isNull(groupConfig) || groupConfig.getIsDel()) {
            throw new CrmebException("数据不存在");
        }
        return groupConfig;
    }

    /**
     * 通过tag和商户ID获取唯一数据
     *
     * @param tag 标签
     * @param merId 商户ID
     */
    @Override
    public GroupConfig getOneByTagAndMerId(Integer tag, Integer merId) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, tag);
        lqw.eq(GroupConfig::getMerId, merId);
        lqw.eq(GroupConfig::getIsDel, 0);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 通过tag和商户ID删除数据
     *
     * @param tag 标签
     * @param merId 商户ID
     */
    @Override
    public Boolean deleteByTagAndMerId(Integer tag, Integer merId) {
        LambdaUpdateWrapper<GroupConfig> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(GroupConfig::getIsDel, 1);
        wrapper.eq(GroupConfig::getTag, tag);
        wrapper.eq(GroupConfig::getMerId, merId);
        wrapper.eq(GroupConfig::getIsDel, 0);
        return update(wrapper);
    }

    /**
     * 保存单一数据
     * @param groupConfig 组合配置
     */
    @Override
    public Boolean saveOne(GroupConfig groupConfig) {
        Integer tag = groupConfig.getTag();
        if (StrUtil.isNotBlank(groupConfig.getImageUrl())) {
            groupConfig.setImageUrl(systemAttachmentService.clearPrefix(groupConfig.getImageUrl()));
        }
        return transactionTemplate.execute(e -> {
            deleteByTagAndMerId(tag, groupConfig.getMerId());
            save(groupConfig);
            return Boolean.TRUE;
        });
    }

    /**
     * 是否存在推荐板块名称
     * @param name 名称
     * @param tag 标签
     */
    @Override
    public Boolean isExistName(String name, Integer tag) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, tag);
        lqw.eq(GroupConfig::getName, name);
        lqw.eq(GroupConfig::getIsDel, 0);
        lqw.last(" limit 1");
        GroupConfig groupConfig = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(groupConfig);
    }


}

