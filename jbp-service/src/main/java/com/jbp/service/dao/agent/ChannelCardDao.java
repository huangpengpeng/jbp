package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.ChannelCard;
import com.jbp.common.response.ChannelCardExtResponse;
import com.jbp.common.response.OrdersFundSummaryExtResponse;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface ChannelCardDao extends BaseMapper<ChannelCard> {


    List<ChannelCardExtResponse> getList(@Param("uId")Integer uId, @Param("bankCardNo")String bankCardNo, @Param("type")String type, @Param("phone")String phone, @Param("teamId")String teamId, @Param("nickname")String nickname);

}
