package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.vo.FundClearingVo;
import io.lettuce.core.dynamic.annotation.Param;
import org.mapstruct.Mapper;

import java.util.Date;
import java.util.List;
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
                                            @Param("id")Long id,@Param("channelName")  String channelName,@Param("commName") String commName);
}
