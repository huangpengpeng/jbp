package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.user.UserSkin;

public interface UserSkinService extends IService<UserSkin> {

    UserSkin getByNo(String number);
}
