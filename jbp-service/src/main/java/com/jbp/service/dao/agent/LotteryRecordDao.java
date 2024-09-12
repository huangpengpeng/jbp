package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.LotteryRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface LotteryRecordDao extends BaseMapper<LotteryRecord> {
    List<LotteryRecord> getList(@Param("uid") Integer uid, @Param("prizeType")Integer prizeType, @Param("startTime")Date startTime, @Param("endTime")Date endTime);

    List<LotteryRecord> getFrontListByLotteryId(@Param("id")Integer id);
}
