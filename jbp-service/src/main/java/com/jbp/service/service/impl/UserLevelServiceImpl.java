package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.service.dao.UserLevelDao;
import com.jbp.service.service.SystemUserLevelService;
import com.jbp.service.service.UserLevelService;
import com.jbp.service.service.UserService;
import com.jbp.common.model.user.UserLevel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * UserLevelServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class UserLevelServiceImpl extends ServiceImpl<UserLevelDao, UserLevel> implements UserLevelService {

    @Resource
    private UserLevelDao dao;

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;


    /**
     * 删除（通过系统等级id）
     *
     * @param levelId 系统等级id
     * @return Boolean
     */
    @Override
    public Boolean deleteByLevelId(Integer levelId) {
        LambdaUpdateWrapper<UserLevel> luw = Wrappers.lambdaUpdate();
        luw.set(UserLevel::getIsDel, true);
        luw.eq(UserLevel::getLevelId, levelId);
        luw.eq(UserLevel::getIsDel, false);
        return update(luw);
    }
}

