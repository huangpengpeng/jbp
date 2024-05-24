package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztPayChannel;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.LztPayChannelDao;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztPayChannelService;
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
public class LztPayChannelServiceImpl extends ServiceImpl<LztPayChannelDao, LztPayChannel> implements LztPayChannelService {

    @Resource
    private MerchantService merchantService;

    @Override
    public PageInfo<LztPayChannel> pageList(Integer merId, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<LztPayChannel> lqw = new LambdaQueryWrapper<LztPayChannel>()
                .eq(merId != null && merId > 0, LztPayChannel::getMerId, merId)
                .orderByDesc(LztPayChannel::getId);

        Page<LztAcct> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<LztPayChannel> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> merIdList = list.stream().map(LztPayChannel::getMerId).collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        list.forEach(s -> {
            Merchant merchant = merchantMap.get(s.getMerId());
            if (merchant == null) {
                s.setMerName("平台");
            } else {
                s.setMerName(merchant.getName());
            }
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public LztPayChannel add(LztPayChannel lztPayChannel) {
        save(lztPayChannel);
        return lztPayChannel;
    }

    @Override
    public List<LztPayChannel> getByMer(Integer merId) {
        return list(new LambdaQueryWrapper<LztPayChannel>().eq(LztPayChannel::getMerId, merId));
    }

    @Override
    public LztPayChannel getByMer(Integer merId, String type) {
        return getOne(new LambdaQueryWrapper<LztPayChannel>().eq(LztPayChannel::getMerId, merId).eq(LztPayChannel::getType, type).last(" limit 1"));
    }
}
