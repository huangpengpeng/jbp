package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.ActivityScoreClearing;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ActivityScoreClearingDao extends BaseMapper<ActivityScoreClearing> {
    List<ActivityScoreClearing> getList(@Param("uid") Integer uid, @Param("activityScoreName")String activityScoreName);
}
