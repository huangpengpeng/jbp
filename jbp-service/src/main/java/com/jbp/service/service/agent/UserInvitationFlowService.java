package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserInvitationFlow;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.UserInvitationGplotVo;

import java.util.List;

public interface UserInvitationFlowService extends IService<UserInvitationFlow> {

    List<UserInvitationFlow> getUnderList(Integer pId, Long minCapaId);

    List<UserInvitationFlow> getXsUnderList(Integer pId, Long minXsCapaId);

    List<UserInvitationFlow> getUnderList(Integer pId);

    /**
     * 用户关系如果变更需要将更当前用户有关的的记录全部删除重新生成
     */
    void clear(Integer uId);

    /**
     * 刷新用户关系
     */
    void refreshFlowAndTeam(Integer uId);

    PageInfo<UserInvitationFlow> pageList(Integer uid, Integer pid, Integer level, PageParamRequest pageParamRequest);

    List<UserInvitationGplotVo> gplotInfo(Integer uid);
}
