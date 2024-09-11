package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.UserSkin;
import com.jbp.common.request.PageParamRequest;

import java.util.Date;
import java.util.List;

public interface UserSkinService extends IService<UserSkin> {

    List<UserSkin> getByNo(String number);

    PageInfo<UserSkin> getList(Integer uid, String nickname, String phone, Date startCreateTime, Date endCreateTime, PageParamRequest pageParamRequest);

    String export(Integer uid, String nickname, String phone, Date startCreateTime, Date endCreateTime);
}
