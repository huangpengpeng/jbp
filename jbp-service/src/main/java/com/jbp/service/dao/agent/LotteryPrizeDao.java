package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.LotteryPrize;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LotteryPrizeDao extends BaseMapper<LotteryPrize> {
    List<LotteryPrize> getListByLotteryId(@Param("id") Long id);
}
