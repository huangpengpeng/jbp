package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.vo.FundClearingVo;
import io.lettuce.core.dynamic.annotation.Param;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface FundClearingDao extends BaseMapper<FundClearing> {
    List<FundClearing> pageList(@Param("uniqueNo") String uniqueNo,@Param("externalNo") String externalNo,
                                @Param("startClearingTime") Date startClearingTime,@Param("endClearingTime") Date endClearingTime,
                                @Param("starteCreateTime") Date starteCreateTime,@Param("endCreateTime") Date endCreateTime,
                                @Param("status") String status,@Param("uid") Integer uid,@Param("teamName") String teamName,@Param("description") String description,
                                @Param("commName") String commName, @Param("ifRefund")Boolean ifRefund);

    List<FundClearingVo> exportFundClearing(@Param("uniqueNo") String uniqueNo,@Param("externalNo") String externalNo,
                                            @Param("startClearingTime") Date startClearingTime,@Param("endClearingTime") Date endClearingTime,
                                            @Param("starteCreateTime") Date starteCreateTime,@Param("endCreateTime") Date endCreateTime,
                                            @Param("status") String status,@Param("uid") Integer uid,@Param("teamName") String teamName,@Param("description") String description,
                                            @Param("id")Long id,@Param("channelName")  String channelName,@Param("commName") String commName, @Param("ifRefund")Boolean ifRefund);

    BigDecimal getUserTotal(Integer uid);

    BigDecimal getUserTotalMonth(@Param("uid")Integer uid,@Param("month")String month);

    List<Map<String,Object>>  getUserTotalMonthList(@Param("uid")Integer uid, @Param("month")String month);

    BigDecimal getUserTotalContMonth(@Param("uid")Integer uid, @Param("month")String month);

    BigDecimal getUserTotalDay(@Param("uid")Integer uid,@Param("day")String day);

}
