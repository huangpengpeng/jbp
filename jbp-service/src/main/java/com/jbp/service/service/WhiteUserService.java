package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.UserWhiteDto;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteUserRequest;

import java.util.List;

public interface WhiteUserService extends IService<WhiteUser> {
    PageInfo<WhiteUser> pageList(WhiteUserRequest request, PageParamRequest pageParamRequest);

    WhiteUser add(WhiteUserRequest userWhiteRequest);

    Boolean batchSave(List<UserWhiteDto> userWhiteExpresses);

    void deleteByWhite(Long id);

    WhiteUser getByUser(Integer uid, Long  whiteId);
}
