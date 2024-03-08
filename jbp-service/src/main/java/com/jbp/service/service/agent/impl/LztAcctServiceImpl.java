package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.result.AcctInfo;
import com.jbp.common.lianlian.result.AcctInfoResult;
import com.jbp.common.lianlian.result.LztQueryAcctInfo;
import com.jbp.common.lianlian.result.LztQueryAcctInfoResult;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.model.agent.LztAcctOpen;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.LztAcctDao;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztAcctApplyService;
import com.jbp.service.service.agent.LztAcctService;
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
public class LztAcctServiceImpl extends ServiceImpl<LztAcctDao, LztAcct> implements LztAcctService {

    @Resource
    private MerchantService merchantService;
    @Resource
    private LztService lztService;
    @Resource
    private LztAcctApplyService lztAcctApplyService;

    @Override
    public LztAcct getByUserId(String userId) {
        return getOne(new QueryWrapper<LztAcct>().lambda().eq(LztAcct::getUserId, userId));
    }

    @Override
    public LztAcct create(Integer merId, String userId, String userType, String userNo, String username, String bankAccount) {
        LztAcct lztAcct = new LztAcct(merId, userId, userType, userNo, username, bankAccount);
        save(lztAcct);
        return lztAcct;
    }

    @Override
    public LztAcct details(String userId) {
        LztAcct lztAcct = getByUserId(userId);
        LztAcctApply lztAcctApply = lztAcctApplyService.getByUserId(userId);
        if (lztAcctApply != null) {
            lztAcct.setGatewayUrl(lztAcctApply.getGatewayUrl());
        }
        Merchant merchant = merchantService.getById(lztAcct.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        AcctInfoResult acctInfoResult = lztService.queryAcct(payInfo.getOidPartner(), payInfo.getPriKey(),
                userId, LianLianPayConfig.UserType.getCode(lztAcct.getUserType()));
        if (lztAcct.getIfOpenBankAcct()) {
            LztQueryAcctInfoResult bankAcctInfoResult = lztService.queryBankAcct(payInfo.getOidPartner(), payInfo.getPriKey(), userId);
            List<LztQueryAcctInfo> list = bankAcctInfoResult.getList();
            if (CollectionUtils.isNotEmpty(list)) {
                for (LztQueryAcctInfo lztQueryAcctInfo : list) {
                    lztQueryAcctInfo.setAcct_stat(LianLianPayConfig.AcctState.valueOf(lztQueryAcctInfo.getAcct_stat()).getCode());
                }
                lztAcct.setBankAcctInfoList(bankAcctInfoResult.getList());
            }
        }
        List<AcctInfo> acctinfoList = acctInfoResult.getAcctinfo_list();
        if (CollectionUtils.isNotEmpty(acctinfoList)) {
            for (AcctInfo acctInfo : acctinfoList) {
                acctInfo.setAcct_state(LianLianPayConfig.AcctState.valueOf(acctInfo.getAcct_state()).getCode());
                acctInfo.setAcct_type(LianLianPayConfig.AcctType.getName(acctInfo.getAcct_type()));
            }
            lztAcct.setAcctInfoList(acctinfoList);
        }
        return lztAcct;
    }


    @Override
    public PageInfo<LztAcct> pageList(Integer merId, String userId, String username, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<LztAcct> lqw = new LambdaQueryWrapper<LztAcct>()
                .eq(StringUtils.isNotEmpty(userId), LztAcct::getUserId, userId)
                .eq(StringUtils.isNotEmpty(username), LztAcct::getUsername, username)
                .eq(merId != null && merId > 0, LztAcct::getMerId, merId)
                .orderByDesc(LztAcct::getId);

        Page<LztAcct> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<LztAcct> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> merIdList = list.stream().map(LztAcct::getMerId).collect(Collectors.toList());
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
}
