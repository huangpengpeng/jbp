package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.result.*;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.model.agent.LztAcctOpen;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.LztInfoResponse;
import com.jbp.common.utils.DateTimeUtils;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
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
            if(bankAcctInfoResult != null){
                List<LztQueryAcctInfo> list = bankAcctInfoResult.getList();
                if (CollectionUtils.isNotEmpty(list)) {
                    list =  list.stream().filter(s-> !("CANCEL".equals(s.getAcct_stat()) || "FAIL".equals(s.getAcct_stat()))).collect(Collectors.toList());
                    for (LztQueryAcctInfo lztQueryAcctInfo : list) {
                        lztQueryAcctInfo.setAcct_stat(LianLianPayConfig.AcctState.valueOf(lztQueryAcctInfo.getAcct_stat()).getCode());
                    }
                    lztAcct.setBankAcctInfoList(list);
                }
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
            if(StringUtils.isNotEmpty(s.getPhone())){
                String phone = s.getPhone();
                s.setPhone(phone.substring(0, 3) + "****" + phone.substring(7, phone.length()));
            }
            //查询用户用户信息
            LztAcct bankAcctInfo = details(s.getUserId());
            s.setAcctInfoList(bankAcctInfo.getAcctInfoList());
            s.setBankAcctInfoList(bankAcctInfo.getBankAcctInfoList());
            BigDecimal amtBalcur = BigDecimal.ZERO, amtBalaval = BigDecimal.ZERO, amtBankBalaval = BigDecimal.ZERO, amtBalfrz = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(bankAcctInfo.getAcctInfoList())) {
                for (AcctInfo acctInfo : bankAcctInfo.getAcctInfoList()) {
                    amtBalcur = amtBalcur.add(StringUtils.isNotEmpty(acctInfo.getAmt_balcur()) ? new BigDecimal(acctInfo.getAmt_balcur()) : BigDecimal.ZERO);
                    amtBalaval = amtBalaval.add(StringUtils.isNotEmpty(acctInfo.getAmt_balaval()) ? new BigDecimal(acctInfo.getAmt_balaval()) : BigDecimal.ZERO);
                    amtBalfrz = amtBalfrz.add(StringUtils.isNotEmpty(acctInfo.getAmt_balfrz()) ? new BigDecimal(acctInfo.getAmt_balfrz()) : BigDecimal.ZERO);
                }
            }
            if (CollectionUtils.isNotEmpty(bankAcctInfo.getBankAcctInfoList())) {
                for (LztQueryAcctInfo acctInfo : bankAcctInfo.getBankAcctInfoList()) {
                    amtBankBalaval = amtBankBalaval.add(StringUtils.isNotEmpty(acctInfo.getBank_acct_balance()) ? new BigDecimal(acctInfo.getBank_acct_balance()) : BigDecimal.ZERO);
                    s.setBankAcctNo(acctInfo.getBank_acct_no());
                }
            }
            amtBalcur = amtBalcur.add(amtBankBalaval);
            s.setAmtBalcur(amtBalcur);
            s.setAmtBalaval(amtBalaval);
            s.setAmtBankBalaval(amtBankBalaval);
            s.setAmtBalfrz(amtBalfrz);
        });

        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public LztInfoResponse lztInfo(Integer merId) {
        LztInfoResponse response = new LztInfoResponse();
        Merchant merchant = merchantService.getById(merId);
        MerchantPayInfo payInfo = merchant.getPayInfo();
        if (payInfo == null || StringUtils.isAnyEmpty(payInfo.getOidPartner(), payInfo.getPriKey())) {
            throw new RuntimeException("商户信息未配置完成请联系管理员");
        }
        //  账户数量
        List<LztAcct> lztAcctList = list(new QueryWrapper<LztAcct>().lambda().eq(LztAcct::getMerId, merId));
        response.setAccountNum(lztAcctList.size());

        //总金额
        BigDecimal totalAmt = BigDecimal.ZERO;
        for (LztAcct lztAcct : lztAcctList) {
            LztAcct details = details(lztAcct.getUserId());
            List<LztQueryAcctInfo> bankAcctInfoList = details.getBankAcctInfoList();
            if (CollectionUtils.isNotEmpty(bankAcctInfoList)) {
                for (LztQueryAcctInfo acctInfo : bankAcctInfoList) {
                    BigDecimal balance = StringUtils.isEmpty(acctInfo.getBank_acct_balance()) ? BigDecimal.ZERO : new BigDecimal(acctInfo.getBank_acct_balance());
                    totalAmt = totalAmt.add(balance);
                }
            }
            List<AcctInfo> acctInfoList = details.getAcctInfoList();
            if (CollectionUtils.isNotEmpty(acctInfoList)) {
                for (AcctInfo acctInfo : acctInfoList) {
                    BigDecimal balance = StringUtils.isEmpty(acctInfo.getAmt_balcur()) ? BigDecimal.ZERO : new BigDecimal(acctInfo.getAmt_balcur());
                    totalAmt = totalAmt.add(balance);
                }
            }
        }
        response.setTotalAmt(totalAmt);
        // 昨天
        Date now = DateTimeUtils.getNow();
        BigDecimal yesterdayInAmt = BigDecimal.ZERO;
        BigDecimal yesterdayOutAmt = BigDecimal.ZERO;
        String yesterdayStart = DateTimeUtils.format(DateTimeUtils.getStartDate(DateTimeUtils.addDays(now, -1)), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        String yesterdayEnd = DateTimeUtils.format(DateTimeUtils.getFinallyDate(DateTimeUtils.addDays(now, -1)), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        List<AcctBalList> yesterdayList = Lists.newArrayList();
        for (LztAcct lztAcct : lztAcctList) {
            Integer pageNo = 1;
            do {
                AcctSerialResult result = lztService.queryAcctSerial(payInfo.getOidPartner(), payInfo.getPriKey(), lztAcct.getUserId(),
                        LianLianPayConfig.UserType.getCode(lztAcct.getUserType()), yesterdayStart, yesterdayEnd, null, pageNo.toString());
                if (CollectionUtils.isEmpty(result.getAcctbal_list())) {
                    break;
                }
                yesterdayList.addAll(result.getAcctbal_list());
                pageNo++;
            } while (true);
        }
        for (AcctBalList acctBal : yesterdayList) {
            // 出账
            if ("DEBIT".equals(acctBal.getFlag_dc())) {
                yesterdayOutAmt = yesterdayOutAmt.add(new BigDecimal(acctBal.getAmt()));
            }
            // 入账
            if ("CREDIT".equals(acctBal.getFlag_dc())) {
                yesterdayInAmt = yesterdayInAmt.add(new BigDecimal(acctBal.getAmt()));
            }
        }
        response.setYesterdayInAmt(yesterdayInAmt);
        response.setYesterdayOutAmt(yesterdayOutAmt);

        // 今天
        BigDecimal todayOutAmt = BigDecimal.ZERO;
        BigDecimal todayInAmt = BigDecimal.ZERO;
        String todayStart = DateTimeUtils.format(DateTimeUtils.getStartDate(now), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        String todayEnd = DateTimeUtils.format(DateTimeUtils.getFinallyDate(now), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        List<AcctBalList> todayList = Lists.newArrayList();
        for (LztAcct lztAcct : lztAcctList) {
            Integer pageNo = 1;
            do {
                AcctSerialResult result = lztService.queryAcctSerial(payInfo.getOidPartner(), payInfo.getPriKey(), lztAcct.getUserId(),
                        LianLianPayConfig.UserType.getCode(lztAcct.getUserType()), todayStart, todayEnd, null, pageNo.toString());
                if (CollectionUtils.isEmpty(result.getAcctbal_list())) {
                    break;
                }
                todayList.addAll(result.getAcctbal_list());
                pageNo++;
            } while (true);
        }

        for (AcctBalList acctBal : todayList) {
            // 出账
            if ("DEBIT".equals(acctBal.getFlag_dc())) {
                todayOutAmt = todayOutAmt.add(new BigDecimal(acctBal.getAmt()));
            }
            // 入账
            if ("CREDIT".equals(acctBal.getFlag_dc())) {
                todayInAmt = todayInAmt.add(new BigDecimal(acctBal.getAmt()));
            }
        }
        response.setTodayInAmt(todayInAmt);
        response.setTodayOutAmt(todayOutAmt);
        return response;
    }
}
