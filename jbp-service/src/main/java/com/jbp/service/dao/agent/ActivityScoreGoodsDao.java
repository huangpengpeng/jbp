package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.ActivityScoreGoods;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ActivityScoreGoodsDao extends BaseMapper<ActivityScoreGoods> {
    public BigDecimal getProductNumber(@Param("productId") List<Integer> productId, @Param("uid")List<Integer> uid, @Param("startTime")String startTime,
                                       @Param("endTime")String endTime, @Param("activityId")Integer activityId);
    List<ActivityScoreGoods> getList(@Param("activityScoreId") Integer activityScoreId);
}
