package com.jbp.admin.task.user;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.jdpay.vo.JdPayToPersonalWalletResponse;
import com.jbp.common.model.agent.WalletWithdraw;
import com.jbp.common.model.user.UserJd;
import com.jbp.service.service.JdPayService;
import com.jbp.service.service.UserJdService;
import com.jbp.service.service.agent.WalletWithdrawService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component("JdWithdrawTask")
public class JdWithdrawTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(JdWithdrawTask.class);


    @Autowired
    private WalletWithdrawService walletWithdrawService;
    @Autowired
    private JdPayService jdPayService;
    @Autowired
    private UserJdService userJdService;


    public void draw() {
        logger.info("---JdWithdrawTask draw------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        List<WalletWithdraw> walletWithdrawList = walletWithdrawService.list(new QueryWrapper<WalletWithdraw>().lambda().eq(WalletWithdraw::getStatus, "待出款").eq(WalletWithdraw::getChannel, "JD").last(" limit 10"));

        if (walletWithdrawList.size() < 1) {
            return;
        }
        for (int i = 0; i < walletWithdrawList.size(); i++) {
            WalletWithdraw walletWithdraw = walletWithdrawList.get(i);
            UserJd userJd = userJdService.getOne(new QueryWrapper<UserJd>().lambda().eq(UserJd::getUid, walletWithdraw.getUid()));
            JdPayToPersonalWalletResponse jdPayToPersonalWalletResponse = jdPayService.payToPersonalWallet(walletWithdraw.getUniqueNo(), userJd.getXid(), walletWithdraw.getAmt(), "提现");

            if (jdPayToPersonalWalletResponse.getCode().equals("00000")) {
                if (jdPayToPersonalWalletResponse.getMerchantTradeStatus().equals("TRADE_PRO") || jdPayToPersonalWalletResponse.getMerchantTradeStatus().equals("TRADE_SUCC")) {
                    walletWithdraw.setStatus(WalletWithdraw.StatusEnum.打款成功.name());
                    walletWithdraw.setSuccessTime(new Date());
                } else if (jdPayToPersonalWalletResponse.getMerchantTradeStatus().equals("TRADE_FAIL")) {
                    walletWithdraw.setStatus(WalletWithdraw.StatusEnum.打款失败.name());
                    walletWithdraw.setRemark(jdPayToPersonalWalletResponse.getInfo());
                }
            } else {
                walletWithdraw.setStatus(WalletWithdraw.StatusEnum.打款失败.name());
                walletWithdraw.setRemark(jdPayToPersonalWalletResponse.getInfo());
            }

            walletWithdrawService.updateById(walletWithdraw);

        }
    }

}
