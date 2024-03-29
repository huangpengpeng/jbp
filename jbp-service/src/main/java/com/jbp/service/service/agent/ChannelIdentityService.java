package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.ChannelIdentity;
import com.github.pagehelper.PageInfo;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ChannelIdentityRequest;

import java.util.List;
import java.util.Map;

public interface ChannelIdentityService extends IService<ChannelIdentity> {
    PageInfo<ChannelIdentity> pageList(Integer uid, String idCardNo, String realName, String channel, PageParamRequest pageParamRequest);


    ChannelIdentity add(Integer uid, String idCardNo, String realName,
                        String idCardNoFrontImg, String idCardNoBackImg, String otherJSON, String channel);

    ChannelIdentity getByUser(Integer uid, String channel);

    void identity(Integer uid, ChannelIdentityRequest request);

    Map<Integer, ChannelIdentity> getChannelIdentityMap(List<Integer> uidList, String channel);

    void update(Integer id, String idCardNo, String realName, String idCardNoFrontImg, String idCardNoBackImg, String otherJSON);
}
