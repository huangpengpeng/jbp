package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserInviteInfoResponse;
import com.jbp.common.response.UserInviteResponse;

import java.util.LinkedList;
import java.util.List;

public interface UserInvitationService extends IService<UserInvitation> {

    UserInvitation getByUser(Integer uId);

    List<UserInvitation> getNextList(Integer uid);

    List<UserInvitation> getNextList(List<Integer> uid);

    LinkedList<List<UserInvitation>> getLevelList(Integer uid, int level);

    Integer getPid(Integer uId);

    List<UserUpperDto> getAllUpper(Integer uId);

    List<UserUpperDto> getNoMountAllUpper(Integer uId);

    Boolean hasChild(Integer uId, Integer pId);

    void validBand(Integer uId, Integer pId);

    UserInvitation band(Integer uId, Integer pId, Boolean ifM, Boolean ifForce, Boolean ifPlatOperate);

    void del(Integer uId);

    /**
     * 存在上下级关系没有层级关系的明细记录列表
     * @return
     */
    List<UserInvitation> getNoFlowList();

    PageInfo<UserInvitation> pageList(Integer uid, Integer pid, Integer mid, PageParamRequest pageParamRequest);

    List<UserInviteResponse> getUserNextList(Integer uid, String account);


    List<UserInviteInfoResponse> getUserNextInfoList(Integer uid, String account);


    //获取邀请关系数量
    Integer getInviteNumber(Integer pId);
}
