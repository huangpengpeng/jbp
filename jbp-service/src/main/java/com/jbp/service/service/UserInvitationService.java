package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.dto.UserInvitationDto;
import com.jbp.common.model.UserUpperModel;
import com.jbp.common.model.user.UserInvitation;


import java.util.List;
import java.util.Map;


public interface UserInvitationService extends IService<UserInvitation> {


    UserInvitation getByUser(Long userId);

    Long getParent(Long userId);

    Long getParentNoMount(Long userId);

    List<UserInvitation> getNext(Long userId);

    List<UserInvitation> getNextNoMount(Long userId);

    UserInvitation band(Long userId, Long pId, Boolean ifM, Boolean ifForce);

    void validBand(Long userId, Long pId);

    void validOperatePermission(Long operateUserId, Long userId, Long pId);

    Page<UserInvitationDto> adminPage(Page<UserInvitationDto> page, Map map);



    /**
     * 查询用户的所有上级[第一条记录是从自己开始]
     * <p>
     * 没有记录 没有上级
     * 有记录 要看pId 是否为null
     * 所有的上级要从 第一条记录的pId  开始 判断null
     */
    List<UserUpperModel> getAllUpper(Long userId);


    /**
     * 当前用户是否是指定上级的下级
     */
    Boolean hasChild(Long userId, Long pId);

    List<UserUpperModel> getAllUpperNoMount(Long userId);

    /**
     * 当前用户是否是指定上级的下级
     */
    Boolean hasChildNoMount(Long userId, Long pId);

    Integer getUnderCount4Capa(Long userId, Long capaId, Integer level);

    Integer getUnderCount4CapaXs(Long userId, Long capaId);

    List<UserInvitation> getNoInvitationFlowList();


}
