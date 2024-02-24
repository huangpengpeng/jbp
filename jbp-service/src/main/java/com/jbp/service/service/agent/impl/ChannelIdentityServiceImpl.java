package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ChannelCard;
import com.jbp.common.model.agent.ChannelIdentity;
import com.jbp.common.model.agent.ChannelWallet;
import com.jbp.common.request.agent.ChannelIdentityRequest;
import com.jbp.common.response.AliBankcardResponse;
import com.jbp.service.dao.agent.ChannelIdentityDao;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.agent.ChannelCardService;
import com.jbp.service.service.agent.ChannelIdentityService;
import com.jbp.service.service.agent.ChannelWalletService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ChannelIdentityServiceImpl extends ServiceImpl<ChannelIdentityDao, ChannelIdentity> implements ChannelIdentityService {

    @Resource
    private ChannelWalletService channelWalletService;
   @Resource
   private ChannelCardService channelCardService;
   @Resource
   private SystemConfigService systemConfigService;

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
}
