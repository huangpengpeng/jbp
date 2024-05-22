package com.jbp.service.service.agent.impl;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ChannelCard;
import com.jbp.common.model.agent.Team;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.AliBankcardResponse;
import com.jbp.common.response.ChannelCardExtResponse;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.RestTemplateUtil;
import com.jbp.service.dao.agent.ChannelCardDao;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ChannelCardService;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
    @Resource
    private TeamService teamService;
    @Resource
    private ChannelCardDao dao;
    @Override
    public PageInfo<ChannelCardExtResponse> pageList(Integer uid, String bankCardNo, String type, String phone, String teamId, String nickname,PageParamRequest pageParamRequest) {


        Page<ChannelCardExtResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ChannelCardExtResponse> list = dao.getList(uid,bankCardNo,type,phone,teamId,nickname);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(ChannelCard::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
            e.setNickname(user != null ? user.getNickname() : "");
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
    public void update(Integer id, String bankName, String bankCardNo, String bankId, String phone, String type, String branchId, String branchName, String province, String city) {
        LambdaUpdateWrapper<ChannelCard> luw = new LambdaUpdateWrapper<ChannelCard>()
                .eq(ChannelCard::getId, id)
                .set(ChannelCard::getBankName, bankName)
                .set(ChannelCard::getBankCardNo, bankCardNo)
                .set(ChannelCard::getBankId, bankId)
                .set(ChannelCard::getPhone, phone)
                .set(ChannelCard::getType, type)
                .set(ChannelCard::getBranchId, branchId)
                .set(ChannelCard::getBranchName, branchName)
                .set(ChannelCard::getProvince, province)
                .set(ChannelCard::getCity, city);
        update(luw);
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
