package com.jbp.service.service.agent.impl;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ChannelCard;
import com.jbp.common.model.agent.ChannelIdentity;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.AliBankcardResponse;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.RestTemplateUtil;
import com.jbp.service.dao.agent.ChannelCardDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ChannelCardService;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ChannelCardServiceImpl extends ServiceImpl<ChannelCardDao, ChannelCard> implements ChannelCardService {

    @Resource
    private RestTemplateUtil restTemplateUtil;
    @Resource
    private UserService userService;

    @Override
    public PageInfo<ChannelCard> pageList(Integer uid, String bankCardNo, String type, String phone, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<ChannelCard> lqw = new LambdaQueryWrapper<ChannelCard>()
                .eq(!ObjectUtil.isNull(uid), ChannelCard::getUid, uid)
                .like(StringUtils.isNotEmpty(bankCardNo), ChannelCard::getBankCardNo, bankCardNo)
                .like(StringUtils.isNotEmpty(type), ChannelCard::getType, type)
                .like(StringUtils.isNotEmpty(phone), ChannelCard::getPhone, phone);
        Page<ChannelCard> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ChannelCard> list = list(lqw);
        List<Integer> uIdList = list.stream().map(ChannelCard::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

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

    @Override
    public Map<Integer, ChannelCard> getChannelCardMap(List<Integer> uidList, String channel) {
        QueryWrapper<ChannelCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(ChannelCard::getUid, uidList).eq(ChannelCard::getChannel, channel);
        List<ChannelCard> list = list(queryWrapper);
        return FunctionUtil.keyValueMap(list, ChannelCard::getUid);

    }
}
