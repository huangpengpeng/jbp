package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.TeamUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeamUserDao extends BaseMapper<TeamUser> {
 List<TeamUser> pageList(@Param("tid") Integer tid, @Param("account") String account, @Param("teamLeader") Integer teamLeader, @Param("nickname") String nickname);

}
