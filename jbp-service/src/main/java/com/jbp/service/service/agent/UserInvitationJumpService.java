package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserInvitationJump;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserInvitationJumpListResponse;

import java.util.LinkedList;

public interface UserInvitationJumpService extends IService<UserInvitationJump> {

    UserInvitationJump add(Integer uId, Integer pId, Integer orgPid);

    Boolean ifJump(Integer uId);

    /**
     * 第一次是从我这里跳走的
     *
     * @return
     */
    LinkedList<UserInvitationJump> getFirst4OrgPid(Integer orgPid);

    PageInfo<UserInvitationJumpListResponse> pageList(Integer uid, Integer pid, Integer orgPid, PageParamRequest pageParamRequest);

    UserInvitationJump getFirst4User(Integer uid);

    Integer getOrgPid(Integer uid);

}
