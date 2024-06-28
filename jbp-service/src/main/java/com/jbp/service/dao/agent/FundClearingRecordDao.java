package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.FundClearingRecord;
import com.jbp.common.response.FundClearingRecordResponse;
import io.lettuce.core.dynamic.annotation.Param;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Mapper

public interface FundClearingRecordDao extends BaseMapper<FundClearingRecord> {

    List<FundClearingRecord> pageList(@Param("orderAccount") String orderAccount, @Param("account") String account, @Param("starteCreateTime") Date starteCreateTime, @Param("endCreateTime") Date endCreateTime, @Param("orderList")List<String> orderList);

    List<FundClearingRecordResponse> total(@Param("account") String account, @Param("orderAccount") String orderAccount, @Param("month") String month);

    List<FundClearingRecordResponse> selfTotal(@Param("uid") Integer uid);

    BigDecimal selfTotalAmount(@Param("uid") Integer uid);

    List<FundClearingRecord> detail(@Param("uid")Integer uid,@Param("day")String day);
}
