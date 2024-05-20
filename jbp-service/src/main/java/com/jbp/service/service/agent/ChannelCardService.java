package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ChannelCard;
import com.jbp.common.model.agent.ChannelIdentity;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.AliBankcardResponse;

import java.util.List;
import java.util.Map;

public interface ChannelCardService extends IService<ChannelCard> {
    PageInfo<ChannelCard> pageList(Integer uid, String bankCardNo, String type, String phone,String teamId, PageParamRequest pageParamRequest);


    AliBankcardResponse getAliBankCard(String kaHao);

    ChannelCard add(Integer uid, String bankId, String bankCardNo,
                    String bankName, String branchId, String branchName, String type,
                    String province, String city, String phone, String channel);

    ChannelCard getByUser(Integer uid, String channel);

    Map<Integer, ChannelCard> getChannelCardMap(List<Integer> uidList, String channel);

    void update(Integer id , String bankName, String bankCardNo,  String bankId, String phone, String type, String branchId, String branchName, String province, String city);

}
