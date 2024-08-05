package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.lianlian.result.ReceiptDownloadResult;
import com.jbp.common.lianlian.result.ReceiptProduceResult;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.agent.LztReceiptDao;
import com.jbp.service.service.DegreePayService;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztReceiptServiceImpl extends ServiceImpl<LztReceiptDao, LztReceipt> implements LztReceiptService {

    @Resource
    private LztTransferService lztTransferService;
    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private LztService lztService;
    @Resource
    private MerchantService merchantService;
    @Resource
    private LztWithdrawalService lztWithdrawalService;
    @Resource
    private LztTransferMorepyeeService lztTransferMorepyeeService;
    @Resource
    private DegreePayService degreePayService;
    @Resource
    private LztSalaryTransferService lztSalaryTransferService;

    @Override
    public List<LztReceipt> getList(Integer merId, String tradeTxnSeqno, String memo) {
        return list(new QueryWrapper<LztReceipt>().lambda()
                .eq(merId != null, LztReceipt::getMerId, merId)
                .eq(StringUtils.isNotEmpty(tradeTxnSeqno), LztReceipt::getTradeTxnSeqno, tradeTxnSeqno)
                .like(StringUtils.isNotEmpty(memo), LztReceipt::getMemo, memo)
                .ge(LztReceipt::getGmtCreated, DateTimeUtils.addMinutes(DateTimeUtils.getNow(), -130))
                .orderByDesc(LztReceipt::getId));
    }

    @Override
    public LztReceipt add(Integer merId, String tradeTxnSeqno, String memo, String tradeBillType, String totalAmount) {
        // 提现记录
        LztWithdrawal lztWithdrawal = lztWithdrawalService.getByTxnSeqno(tradeTxnSeqno);
        String payChannelType = "";
        if (lztWithdrawal != null) {
            payChannelType = lztWithdrawal.getPayChannelType();
        }
        // 转账记录
        LztTransferMorepyee lztTransferMorepyee = lztTransferMorepyeeService.getByTxnSeqno(tradeTxnSeqno);
        if (lztTransferMorepyee != null) {
            payChannelType = lztTransferMorepyee.getPayChannelType();
        }
        // 外部转账记录
        LztTransfer lztTransfer = lztTransferService.getByTxnSeqno(tradeTxnSeqno);
        if (lztTransfer != null) {
            payChannelType = lztTransfer.getPayChannelType();
        }
        // 外部转账记录
        LztSalaryTransfer lztSalaryTransfer = lztSalaryTransferService.getByTxnSeqno(tradeTxnSeqno);
        if (lztSalaryTransfer != null) {
            payChannelType = lztSalaryTransfer.getPayChannelType();
        }

        LztReceipt lztReceipt = new LztReceipt();
        lztReceipt.setMerId(merId);
        lztReceipt.setTxnSeqno(StringUtils.N_TO_10("HZ_"));
        lztReceipt.setTotalAmount(totalAmount);
        lztReceipt.setTradeTxnSeqno(tradeTxnSeqno);
        lztReceipt.setTradeBillType(tradeBillType);
        lztReceipt.setMemo(memo);

        if (!"易宝".equals(payChannelType)) {
            Merchant merchant = merchantService.getById(merId);
            MerchantPayInfo payInfo = merchant.getPayInfo();
            ReceiptProduceResult receiptProduceResult = lztService.receiptProduce(payInfo.getOidPartner(), payInfo.getPriKey(),
                    lztReceipt.getTxnSeqno(), totalAmount, tradeBillType, memo, tradeTxnSeqno);
            lztReceipt.setReceiptAccpTxno(receiptProduceResult.getReceipt_accp_txno());
            lztReceipt.setToken(receiptProduceResult.getToken());
            lztReceipt.setTradeAccpTxno(receiptProduceResult.getTrade_accp_txno());
        }
        save(lztReceipt);

        // 提现记录
        if (lztWithdrawal != null) {
            lztWithdrawal.setReceiptStatus(1);
            lztWithdrawal.setReceiptToken(lztReceipt.getToken());
            lztWithdrawal.setReceiptAccpTxno(lztReceipt.getReceiptAccpTxno());
            lztWithdrawalService.updateById(lztWithdrawal);
        }
        // 转账记录
        if (lztTransferMorepyee != null) {
            lztTransferMorepyee.setReceiptStatus(1);
            lztTransferMorepyee.setReceiptToken(lztReceipt.getToken());
            lztTransferMorepyee.setReceiptAccpTxno(lztReceipt.getReceiptAccpTxno());
            lztTransferMorepyeeService.updateById(lztTransferMorepyee);
        }
        // 外部转账记录
        if (lztTransfer != null) {
            lztTransfer.setReceiptStatus(1);
            lztTransfer.setReceiptToken(lztReceipt.getToken());
            lztTransfer.setReceiptAccpTxno(lztReceipt.getReceiptAccpTxno());
            lztTransferService.updateById(lztTransfer);
        }
        if (lztSalaryTransfer != null) {
            lztSalaryTransfer.setReceiptStatus(1);
            lztSalaryTransfer.setReceiptToken(lztReceipt.getToken());
            lztSalaryTransfer.setReceiptAccpTxno(lztReceipt.getReceiptAccpTxno());
            lztSalaryTransferService.updateById(lztSalaryTransfer);
        }
        return lztReceipt;
    }

    @Override
    public ReceiptDownloadResult download(String tradeTxnSeqno) {
        ReceiptDownloadResult result = new ReceiptDownloadResult();
        // 提现记录
        LztWithdrawal lztWithdrawal = lztWithdrawalService.getByTxnSeqno(tradeTxnSeqno);
        if (lztWithdrawal != null && StringUtils.isEmpty(lztWithdrawal.getReceiptZip())) {
            LztAcct lztAcct = lztAcctService.getByUserId(lztWithdrawal.getUserId());
            result = degreePayService.receiptDownload(lztAcct, lztWithdrawal.getReceiptAccpTxno(), lztWithdrawal.getTxnSeqno(), lztWithdrawal.getReceiptToken(), "WITHDRAW", DateTimeUtils.format(lztWithdrawal.getCreateTime(), DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN));
            lztWithdrawal.setReceiptZip(result.getReceipt_sum_file());
            lztWithdrawalService.updateById(lztWithdrawal);
        }
        if (lztWithdrawal != null) {
            result.setReceipt_sum_file(lztWithdrawal.getReceiptZip());
        }
        // 转账记录
        LztTransferMorepyee lztTransferMorepyee = lztTransferMorepyeeService.getByTxnSeqno(tradeTxnSeqno);
        if (lztTransferMorepyee != null && StringUtils.isEmpty(lztTransferMorepyee.getReceiptZip())) {
            LztAcct lztAcct = lztAcctService.getByUserId(lztTransferMorepyee.getPayerId());
            result = degreePayService.receiptDownload(lztAcct, lztTransferMorepyee.getReceiptAccpTxno(), lztTransferMorepyee.getTxnSeqno(), lztTransferMorepyee.getReceiptToken(), "TRANSFER", DateTimeUtils.format(lztTransferMorepyee.getCreateTime(), DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN));
            lztTransferMorepyee.setReceiptZip(result.getReceipt_sum_file());
            lztTransferMorepyeeService.updateById(lztTransferMorepyee);
        }
        if (lztTransferMorepyee != null) {
            result.setReceipt_sum_file(lztTransferMorepyee.getReceiptZip());
        }
        // 批量代付
        LztSalaryTransfer lztSalaryTransfer = lztSalaryTransferService.getByTxnSeqno(tradeTxnSeqno);
        if(lztSalaryTransfer != null && StringUtils.isEmpty(lztSalaryTransfer.getReceiptZip())){
            LztAcct lztAcct = lztAcctService.getByUserId(lztSalaryTransfer.getPayerId());
            result = degreePayService.receiptDownload(lztAcct, lztSalaryTransfer.getReceiptAccpTxno(), lztSalaryTransfer.getTxnSeqno(), lztSalaryTransfer.getReceiptToken(), "PAY", DateTimeUtils.format(lztSalaryTransfer.getCreateTime(), DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN));
            lztSalaryTransfer.setReceiptZip(result.getReceipt_sum_file());
            lztSalaryTransferService.updateById(lztSalaryTransfer);
        }
        if (lztSalaryTransfer != null) {
            result.setReceipt_sum_file(lztSalaryTransfer.getReceiptZip());
        }
        // 外部转账记录
        LztTransfer lztTransfer = lztTransferService.getByTxnSeqno(tradeTxnSeqno);
        if (lztTransfer != null && StringUtils.isEmpty(lztTransfer.getReceiptZip())) {
            LztAcct lztAcct = lztAcctService.getByUserId(lztTransfer.getPayerId());
            result = degreePayService.receiptDownload(lztAcct, lztTransfer.getReceiptAccpTxno(), lztTransfer.getTxnSeqno(), lztTransfer.getReceiptToken(), "PAY", DateTimeUtils.format(lztTransfer.getCreateTime(), DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN));
            lztTransfer.setReceiptZip(result.getReceipt_sum_file());
            lztTransferService.updateById(lztTransfer);
        }
        if (lztTransfer != null) {
            result.setReceipt_sum_file(lztTransfer.getReceiptZip());
        }
        return result;
    }



    public static void decodeBase64File(String base64Code, String targetPath) {
        // 输出流
        FileOutputStream out =null;
        // 将base 64 转为字节数字
        byte[] buffer = new byte[0];
        try {
            buffer = new BASE64Decoder().decodeBuffer(base64Code);
            // 创建输出流
            out = new FileOutputStream(targetPath);
            // 输出
            out.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
