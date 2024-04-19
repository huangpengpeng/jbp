package com.jbp.service.service.impl;

import com.beust.jcommander.internal.Lists;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.result.AcctInfo;
import com.jbp.common.lianlian.result.AcctInfoResult;
import com.jbp.common.lianlian.result.LztQueryAcctInfo;
import com.jbp.common.lianlian.result.LztQueryAcctInfoResult;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztPayChannel;
import com.jbp.common.yop.result.AccountBalanceQueryResult;
import com.jbp.common.yop.result.BankAccountBalanceQueryResult;
import com.jbp.service.service.DegreePayService;
import com.jbp.service.service.LztService;
import com.jbp.service.service.YopService;
import com.jbp.service.service.agent.LztPayChannelService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DegreePayServiceImpl implements DegreePayService {

    @Resource
    private LztPayChannelService lztPayChannelService;
    @Resource
    private LztService lztService;
    @Resource
    private YopService yopService;


    @Override
    public AcctInfoResult queryAcct(LztAcct lztAcct) {
        AcctInfoResult acctInfoResult = new AcctInfoResult();
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        if (StringUtils.equals(lztAcct.getPayChannelType(), "连连")) {
            acctInfoResult = lztService.queryAcct(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(), lztAcct.getUserId(), LianLianPayConfig.UserType.getCode(lztAcct.getUserType()));
        }
        if (StringUtils.equals(lztAcct.getPayChannelType(), "易宝")) {
            AccountBalanceQueryResult result = yopService.accountBalanceQuery(lztAcct.getUserId());
            if (result != null && result.validate()) {
                List<AcctInfo> acctinfoList = Lists.newArrayList();
                AcctInfo acctInfo = new AcctInfo();
                acctInfo.setAcct_type("USEROWN_AVAILABLE");
                acctInfo.setAcct_state(result.getAccountStatus().equals("AVAILABLE") ? "NORMAL" : "CANCEL");
                acctInfo.setAmt_balcur(result.getBalance());
                acctInfo.setAmt_balaval(result.getBalance());
                acctInfoResult.setAcctinfo_list(acctinfoList);
            }
        }
        return acctInfoResult;
    }



    @Override
    public LztQueryAcctInfoResult queryBankAcct(LztAcct lztAcct) {
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        LztQueryAcctInfoResult result = new LztQueryAcctInfoResult();
        if (lztAcct.getPayChannelType().equals("连连")) {
            result = lztService.queryBankAcct(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(), lztAcct.getUserId());
        }
        if (lztAcct.getPayChannelType().equals("易宝")) {
            BankAccountBalanceQueryResult yopResult = yopService.bankAccountBalanceQuery(lztAcct.getUserId(), lztAcct.getOpenBank(),
                    lztAcct.getBankAccount());
            if (yopResult != null && yopResult.validate()) {
                List<LztQueryAcctInfo> list = getAcctInfoList(lztAcct, yopResult);
                result.setList(list);
            }
        }
        return result;
    }





    private static List<LztQueryAcctInfo> getAcctInfoList(LztAcct lztAcct, BankAccountBalanceQueryResult result) {
        List<LztQueryAcctInfo> list = Lists.newArrayList();
        LztQueryAcctInfo lztQueryAcctInfo = new LztQueryAcctInfo();
        lztQueryAcctInfo.setAcct_stat("NORMAL");
        lztQueryAcctInfo.setBank_acct_name(lztAcct.getUsername());
        lztQueryAcctInfo.setBank_acct_no(lztAcct.getBankAccount());
        lztQueryAcctInfo.setBank_acct_name(lztAcct.getOpenBank());
        lztQueryAcctInfo.setBank_acct_balance(result.getUseableAmt());
        lztQueryAcctInfo.setBank_acct_frz_balance(result.getFrozenAmt());
        list.add(lztQueryAcctInfo);
        return list;
    }
}
