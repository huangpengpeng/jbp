package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.UserInvitation;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserInvitationDao extends BaseMapper<UserInvitation> {

    List<UserUpperDto> getAllUpper(Integer uId);

    List<UserUpperDto> getNoMountAllUpper(Integer uId);

    @Select("select u.* from eb_user_invitation u left join eb_user_invitation_flow f on f.uId = u.uId where f.id is null")
    List<UserInvitation> getNoFlowList();

}
