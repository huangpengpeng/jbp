package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.result.*;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.LztAcctApplyDao;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztAcctApplyService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztAcctApplyServiceImpl extends ServiceImpl<LztAcctApplyDao, LztAcctApply> implements LztAcctApplyService {

    @Resource
    private LztService lztService;
    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private MerchantService merchantService;

    @Override
    public LztAcctApply apply(Integer merId, String userId, String shopId, String shopName, String province,
                              String city, String area, String address, String openBank) {

        LztAcct lztAcct = lztAcctService.getByUserId(userId);
        if (lztAcct == null) {
            throw new CrmebException("未开通子商户号");
        }
        if (BooleanUtils.isTrue(lztAcct.getIfOpenBankAcct())) {
            throw new CrmebException("银行虚拟户已申请");
        }
        Merchant merchant = merchantService.getById(merId);
        MerchantPayInfo payInfo = merchant.getPayInfo();
        String txnSeqno = StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通开通银行虚拟户.getPrefix());
        String notifyUrl = "/api/publicly/payment/callback/lianlian/lzt/" + txnSeqno;
        LztOpenacctApplyResult result = lztService.createBankUser(payInfo.getOidPartner(), payInfo.getPriKey(),
                userId, txnSeqno, shopId, shopName, province, city, area, address, notifyUrl, openBank);
        UserInfoResult userInfoResult = lztService.queryUserInfo(payInfo.getOidPartner(), payInfo.getPriKey(), userId);
        LztAcctApply lztAcctApply = new LztAcctApply(merId, userId, lztAcct.getUserType(), userInfoResult.getOid_userno(), userInfoResult.getUser_name(),
                txnSeqno, result.getAccp_txno(),
                result.getGateway_url(), openBank);
        save(lztAcctApply);

        lztAcct.setIfOpenBankAcct(true);
        lztAcct.setOpenBank(openBank);
        lztAcctService.updateById(lztAcct);
        return lztAcctApply;
    }

    @Override
    public LztAcctApply refresh(String userId, String notifyInfo) {
        LztAcctApply lztAcctApply = getByUserId(userId);
        Merchant merchant = merchantService.getById(lztAcctApply.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        LztQueryAcctInfoResult result = lztService.queryBankAcct(payInfo.getOidPartner(), payInfo.getPriKey(), userId);
        if(result != null){
            List<LztQueryAcctInfo> list = result.getList();
            if (CollectionUtils.isNotEmpty(list)) {
                list =  list.stream().filter(s-> !("CANCEL".equals(s.getAcct_stat()) || "FAIL".equals(s.getAcct_stat()))).collect(Collectors.toList());
                LianLianPayConfig.AcctState acctState = LianLianPayConfig.AcctState.valueOf(list.get(0).getAcct_stat());
                lztAcctApply.setStatus(acctState.getCode());
            }
            lztAcctApply.setRetMsg(result.getRet_msg());
        }
        if(StringUtils.isNotEmpty(notifyInfo)){
            lztAcctApply.setNotifyInfo(notifyInfo);
        }
        updateById(lztAcctApply);
        return lztAcctApply;
    }

    @Override
    public LztAcctApply getByUserId(String userId) {
        return getOne(new QueryWrapper<LztAcctApply>().lambda().eq(LztAcctApply::getUserId, userId));
    }

    @Override
    public LztAcctApply getByTxnSeqno(String txnSeqno) {
        return getOne(new QueryWrapper<LztAcctApply>().lambda().eq(LztAcctApply::getTxnSeqno, txnSeqno));
    }

    @Override
    public PageInfo<LztAcctApply> pageList(Integer merId, String userId, String username, String status, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<LztAcctApply> lqw = new LambdaQueryWrapper<LztAcctApply>()
                .eq(StringUtils.isNotEmpty(userId), LztAcctApply::getUserId, userId)
                .eq(StringUtils.isNotEmpty(username), LztAcctApply::getUsername, username)
                .eq(StringUtils.isNotEmpty(status), LztAcctApply::getStatus, status)
                .eq(merId != null && merId > 0, LztAcctApply::getMerId, merId)
                .orderByDesc(LztAcctApply::getId);
        Page<LztAcctApply> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<LztAcctApply> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }

        List<Integer> merIdList = list.stream().map(LztAcctApply::getMerId).collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        list.forEach(s -> {
            Merchant merchant = merchantMap.get(s.getMerId());
            if (merchant == null) {
                s.setMerName("平台");
            } else {
                s.setMerName(merchant.getName());
            }
            if (StringUtils.isNotEmpty(s.getNotifyInfo())) {
                try {
                    JSONObject jsonObject = new JSONObject(s.getNotifyInfo());
                    if(jsonObject.has("gateway_url")){
                        s.setGateway_url2(jsonObject.getString("gateway_url"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return CommonPage.copyPageInfo(page, list);
    }


    @Override
    public void del(Long id) {
        // 查询开户是否成功
        LztAcctApply lztAcctApply = getById(id);
        Merchant merchant = merchantService.getById(lztAcctApply.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        LztQueryAcctInfoResult result = lztService.queryBankAcct(payInfo.getOidPartner(), payInfo.getPriKey(), lztAcctApply.getUserId());
        if (result != null && CollectionUtils.isNotEmpty(result.getList())) {
            for (LztQueryAcctInfo acctInfo : result.getList()) {
                if (!"FAIL".equals(acctInfo.getAcct_stat()) && !"CANCEL".equals(acctInfo.getAcct_stat()) && !"PENDING".equals(acctInfo.getAcct_stat())) {
                    throw new RuntimeException("银行户已开户成功不能删除");
                }
            }
        }
        LztAcct lztAcct = lztAcctService.getByUserId(lztAcctApply.getUserId());
        lztAcct.setIfOpenBankAcct(false);
        lztAcctService.updateById(lztAcct);
        removeById(id);
    }
}
