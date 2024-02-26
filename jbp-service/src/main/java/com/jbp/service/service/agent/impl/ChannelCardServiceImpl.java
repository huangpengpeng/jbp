package com.jbp.service.service.agent.impl;


import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.ChannelCard;
import com.jbp.common.response.AliBankcardResponse;
import com.jbp.common.utils.RestTemplateUtil;
import com.jbp.service.dao.agent.ChannelCardDao;
import com.jbp.service.service.agent.ChannelCardService;

import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ChannelCardServiceImpl extends ServiceImpl<ChannelCardDao, ChannelCard> implements ChannelCardService {

    @Resource
    private RestTemplateUtil restTemplateUtil;

    @Override
    public AliBankcardResponse getAliBankCard(String kaHao) {
        String host = "http://ali-bankcard.showapi.com/bankcard?kahao=" + kaHao;
        String appcode = "160a46d115d14afd8f391a23ea036160";
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("kahao", kaHao);
        try {
            JSONObject s = restTemplateUtil.getData(host, headers);
            log.info("com.mall.pay.manager.impl.CardServiceImpl.getAliBankCard:{0}", s);
            return AliBankcardResponse.get(s.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ChannelCard add(Integer uid, String bankId, String bankCardNo, String bankName, String branchId, String branchName, String type, String province, String city, String phone, String channel) {
        ChannelCard channelCard = new ChannelCard(uid, bankId, bankCardNo, bankName, branchId, branchName, type, province, city, phone, channel);
        save(channelCard);
        return channelCard;
    }

    @Override
    public ChannelCard getByUser(Integer uid, String channel) {
        return getOne(new QueryWrapper<ChannelCard>().lambda().eq(ChannelCard::getUid, uid).eq(ChannelCard::getChannel, channel));
    }
}
