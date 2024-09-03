package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.user.UserSkin;
import com.jbp.service.dao.UserSkinDao;
import com.jbp.service.service.UserSkinService;
import org.springframework.stereotype.Service;

@Service

public class UserSkinServiceImpl extends ServiceImpl<UserSkinDao, UserSkin> implements UserSkinService {


    @Override
    public UserSkin getByNo(String number) {
        return getOne(new QueryWrapper<UserSkin>().lambda().eq(UserSkin::getRecordListNo, number));
    }
}
