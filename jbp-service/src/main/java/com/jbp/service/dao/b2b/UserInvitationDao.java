package com.jbp.service.dao.b2b;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.b2b.UserInvitation;
import com.jbp.common.model.user.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserInvitationDao extends BaseMapper<UserInvitation> {

    List<UserUpperDto> getAllUpper(Integer uId);

    List<UserUpperDto> getNoMountAllUpper(Integer uId);

    @Select("select u.* from b2b_user_invitation u left join b2b_user_invitation_flow f on f.uId = u.uId where f.id is null")
    List<UserInvitation> getNoFlowList();

}
