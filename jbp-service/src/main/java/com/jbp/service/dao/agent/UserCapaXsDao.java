package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.UserCapaXs;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCapaXsDao extends BaseMapper<UserCapaXs> {

    List<UserCapaXs> getRelationUnder(@Param("uid") Integer uid, @Param("capaId") Long capaId);

    List<UserCapaXs> getInvitationUnder(@Param("uid") Integer uid, @Param("capaId") Long capaId);
}
