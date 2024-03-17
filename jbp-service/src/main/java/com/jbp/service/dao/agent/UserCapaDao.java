package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.UserCapa;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCapaDao extends BaseMapper<UserCapa> {
    List<UserCapa> getInvitationUnder(@Param("uid") Integer uid, @Param("capaId") Long capaId);
}
