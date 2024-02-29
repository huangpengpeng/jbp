package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.params.LztFundTransferParams;
import com.jbp.common.lianlian.result.LztFundTransferResult;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.LztFundTransfer;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.LztFundTransferDao;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.agent.LztFundTransferService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztFundTransferServiceImpl extends ServiceImpl<LztFundTransferDao, LztFundTransfer> implements LztFundTransferService {

    @Resource
    private LianLianPayService lianLianPayService;

    @Override
    public LztFundTransfer fundTransfer(Integer merId, String userId, String bankAccountNo, BigDecimal amt, String postscript) {
        String txnSeqno = StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通划拨资金.getPrefix());
        LztFundTransferParams params = new LztFundTransferParams(txnSeqno, userId, bankAccountNo, amt.toString());
        LztFundTransferResult result = lianLianPayService.lztFundTransfer(params);
        LztFundTransfer lztFundTransfer = new LztFundTransfer(merId, userId, txnSeqno, amt, DateTimeUtils.parseDate(params.getTxn_time()),
                bankAccountNo, postscript, result.getAccp_txno());
        save(lztFundTransfer);
        return lztFundTransfer;
    }

    @Override
    public PageInfo<LztFundTransfer> pageList(Integer merId, String userId, String bankAccountNo,
                                              String txnSeqno, String accpTxno, Date startTime, Date endTime, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<LztFundTransfer> lqw = new LambdaQueryWrapper<LztFundTransfer>()
                .eq(LztFundTransfer::getMerId, merId)
                .eq(StringUtils.isNotEmpty(userId), LztFundTransfer::getLianLianAcct, userId)
                .eq(StringUtils.isNotEmpty(bankAccountNo), LztFundTransfer::getBankAccountNo, bankAccountNo)
                .eq(StringUtils.isNotEmpty(txnSeqno), LztFundTransfer::getTxnSeqno, txnSeqno)
                .eq(StringUtils.isNotEmpty(accpTxno), LztFundTransfer::getAccpTxno, accpTxno)
                .ge(startTime != null, LztFundTransfer::getTxnTime, startTime)
                .le(endTime != null, LztFundTransfer::getTxnTime, endTime)
                .orderByDesc(LztFundTransfer::getId);
        Page<FundClearing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<LztFundTransfer> list = list(lqw);
        return CommonPage.copyPageInfo(page, list);
    }
}
