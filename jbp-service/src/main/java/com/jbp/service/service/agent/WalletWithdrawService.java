package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletWithdraw;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.WalletWithdrawCancelRequest;
import com.jbp.common.request.agent.WalletWithdrawRequest;
import com.jbp.common.vo.WalletWithdrawExcelInfoVo;
import com.jbp.common.vo.WalletWithdrawVo;

import java.math.BigDecimal;
import java.util.List;

public interface WalletWithdrawService extends IService<WalletWithdraw> {

    WalletWithdraw create(Integer uid, String account, Integer walletType, String walletName, BigDecimal amt, String postscript);

    WalletWithdraw getByUniqueNo(String uniqueNo);

    void send(List<WalletWithdrawRequest> walletWithdrawList);

    void cancel(WalletWithdrawCancelRequest request);

    PageInfo<WalletWithdrawVo> pageList(String account, String walletName, String status, String dateLimit, String realName, String nickName, String teamId, PageParamRequest pageParamRequest);

    WalletWithdrawExcelInfoVo excel(String account, String walletName, String status, String realName, String dateLimit,String nickName, String teamId);
}
