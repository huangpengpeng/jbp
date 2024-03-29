package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ChannelCard;
import com.jbp.common.model.agent.ChannelIdentity;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ChannelIdentityRequest;
import com.jbp.common.response.AliBankcardResponse;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.dao.agent.ChannelIdentityDao;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ChannelCardService;
import com.jbp.service.service.agent.ChannelIdentityService;
import com.jbp.service.service.agent.ChannelWalletService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ChannelIdentityServiceImpl extends ServiceImpl<ChannelIdentityDao, ChannelIdentity> implements ChannelIdentityService {

    @Resource
    private ChannelWalletService channelWalletService;
    @Resource
    private ChannelCardService channelCardService;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private UserService userService;

    @Override
    public PageInfo<ChannelIdentity> pageList(Integer uid, String idCardNo, String realName, String channel, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<ChannelIdentity> lqw = new LambdaQueryWrapper<ChannelIdentity>()
                .eq(!ObjectUtil.isNull(uid), ChannelIdentity::getUid, uid)
                .like(StringUtils.isNotEmpty(idCardNo), ChannelIdentity::getIdCardNo, idCardNo)
                .like(StringUtils.isNotEmpty(realName), ChannelIdentity::getRealName, realName)
                .like(StringUtils.isNotEmpty(channel), ChannelIdentity::getChannel, channel)
                .orderByDesc(ChannelIdentity::getId);
        Page<ChannelIdentity> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ChannelIdentity> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(ChannelIdentity::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public ChannelIdentity add(Integer uid, String idCardNo, String realName,
                               String idCardNoFrontImg, String idCardNoBackImg, String otherJSON, String channel) {
        ChannelIdentity channelIdentity = new ChannelIdentity(uid, idCardNo, realName,
                idCardNoFrontImg, idCardNoBackImg, otherJSON, channel);
        save(channelIdentity);
        return channelIdentity;
    }

    @Override
    public ChannelIdentity getByUser(Integer uid, String channel) {
        return getOne(new QueryWrapper<ChannelIdentity>().lambda().eq(ChannelIdentity::getUid, uid).eq(ChannelIdentity::getChannel, channel));
    }

    @Override
    public void identity(Integer uid, ChannelIdentityRequest request) {
        String channelName = systemConfigService.getValueByKey("pay_channel_name");
        channelName = StringUtils.isEmpty(channelName) ? "平台" : channelName;
        AliBankcardResponse aliBankCard = channelCardService.getAliBankCard(request.getBankCardNo());
        if (aliBankCard == null) {
            throw new CrmebException("银行卡信息错误");
        }
        ChannelIdentity channelIdentity = getByUser(uid, channelName);
        if (channelIdentity != null) {
            removeById(channelIdentity.getId());
        }
        ChannelCard channelCard = channelCardService.getByUser(uid, channelName);
        if (channelCard != null) {
            channelCardService.removeById(channelCard.getId());
        }
        String channelCode = "";
        // 调用三方开户获取渠道钱包编号
        if (!channelName.equals("平台")) {

        }
        add(uid, request.getIDCard(), request.getRealName(),
                request.getIDCardFrontUrl(), request.getIDCardBackUrl(), null, channelName);
        channelCardService.add(uid, null, aliBankCard.getCardNum(),
                aliBankCard.getBankName(), null, request.getBankName(), aliBankCard.getCardType(),
                request.getDistrictCode(), request.getAddress(), request.getMobile(), channelName);
    }

    @Override
    public void update(Integer id, String idCardNo, String realName, String idCardNoFrontImg, String idCardNoBackImg, String otherJSON) {
        LambdaUpdateWrapper<ChannelIdentity> luw = new LambdaUpdateWrapper<ChannelIdentity>()
                .eq(ChannelIdentity::getId, id)
                .set(ChannelIdentity::getIdCardNo, idCardNo)
                .set(ChannelIdentity::getRealName, realName)
                .set(ChannelIdentity::getIdCardNoFrontImg, idCardNoFrontImg)
                .set(ChannelIdentity::getIdCardNoBackImg, idCardNoBackImg)
                .set(ChannelIdentity::getOtherJSON, otherJSON);

        update(luw);
    }

    @Override
    public Map<Integer, ChannelIdentity> getChannelIdentityMap(List<Integer> uidList, String channel) {
        QueryWrapper<ChannelIdentity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(ChannelIdentity::getUid, uidList).eq(ChannelIdentity::getChannel, channel);
        List<ChannelIdentity> list = list(queryWrapper);
        return FunctionUtil.keyValueMap(list, ChannelIdentity::getUid);
    }
}
