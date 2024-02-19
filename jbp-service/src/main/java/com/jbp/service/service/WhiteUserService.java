package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.UserWhiteDto;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteUserRequest;

import java.util.List;

public interface WhiteUserService extends IService<WhiteUser> {
    PageInfo<WhiteUser> pageList(Integer uid, Long whiteId, PageParamRequest pageParamRequest);

    Boolean batchSave(List<UserWhiteDto> userWhiteExpresses);

    void deleteByWhite(Long id);

    WhiteUser getByUser(Integer uid, Long  whiteId);

    List<Long> getByUser(Integer uid);

    List<WhiteUser> getByUserList(Integer uid);

    void add(Integer uid, Long whiteId,String ordersSn);
}
