package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.ActivityScoreGoods;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface ActivityScoreGoodsDao extends BaseMapper<ActivityScoreGoods> {
    public Integer getProductNumber(@Param("productId") List<Integer> productId, @Param("uid")List<Integer> uid, @Param("startTime")String startTime,
                                                      @Param("endTime")String endTime,@Param("activityId")Integer activityId);
}
