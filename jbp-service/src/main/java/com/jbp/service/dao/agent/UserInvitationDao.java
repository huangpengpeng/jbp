package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.user.User;
import com.jbp.common.response.UserInviteInfoResponse;
import com.jbp.common.response.UserInviteResponse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UserInvitationDao extends BaseMapper<UserInvitation> {

    List<UserUpperDto> getAllUpper(Integer uId);

    List<UserUpperDto> getNoMountAllUpper(Integer uId);

    @Select("select u.* from eb_user_invitation u left join eb_user_invitation_flow f on f.uId = u.uId where f.id is null limit 1000 ")
    List<UserInvitation> getNoFlowList();

    List<UserInviteResponse> getUserNextList(@Param("uId")Integer uId, @Param("account")String account);

    List<UserInviteInfoResponse> getUserNextInfoList(@Param("uId")Integer uId, @Param("account")String account);


    List<UserInviteResponse> getUserNotService(Integer uId);
}
