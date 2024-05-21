package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.UserInvitationFlow;
import com.jbp.common.mybatis.RootMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInvitationFlowDao extends RootMapper<UserInvitationFlow> {

    List<UserInvitationFlow> getUnderCapaList(@Param("pId") Integer pId, @Param("minCapaId") Long minCapaId);

    List<UserInvitationFlow> getUnderXsCapaList(@Param("pId") Integer pId, @Param("minXsCapaId") Long minXsCapaId);

}
