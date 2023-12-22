package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.jbp.common.dto.UserInvitationDto;
import com.jbp.common.model.UserUpperModel;
import com.jbp.common.model.user.UserInvitation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserInvitationMapper extends BaseMapper<UserInvitation> {


    Page<UserInvitationDto> adminPage(Page<UserInvitationDto> page, @Param("map") Map map);


    List<UserUpperModel> getAllUpper(@Param("userId") Long userId);


    List<UserUpperModel> getUnder4PId(@Param("userId") Long userId, @Param("pId") Long pId);

    List<UserUpperModel> getAllUpperNoMount(@Param("userId") Long userId);

    List<UserUpperModel> getUnder4PIdNoMount(@Param("userId") Long userId, @Param("pId") Long pId);

    Integer getUnderCount4Capa(@Param("userId") Long userId,  @Param("capaId") Long capaId, @Param("level") Integer level);

    Integer getUnderCount4CapaXs(@Param("userId") Long userId,  @Param("capaId") Long capaId);

    List<UserInvitation> getNoInvitationFlowList();
}
