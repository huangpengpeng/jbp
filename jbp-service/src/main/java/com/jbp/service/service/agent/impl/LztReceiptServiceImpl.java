package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.lianlian.result.ReceiptDownloadResult;
import com.jbp.common.lianlian.result.ReceiptProduceResult;
import com.jbp.common.model.agent.LztReceipt;
import com.jbp.common.model.agent.LztTransfer;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.model.agent.LztWithdrawal;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.agent.LztReceiptDao;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztReceiptService;
import com.jbp.service.service.agent.LztTransferMorepyeeService;
import com.jbp.service.service.agent.LztTransferService;
import com.jbp.service.service.agent.LztWithdrawalService;
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
    private LztService lztService;
    @Resource
    private MerchantService merchantService;
    @Resource
    private LztWithdrawalService lztWithdrawalService;
    @Resource
    private LztTransferMorepyeeService lztTransferMorepyeeService;

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
        LztReceipt lztReceipt = new LztReceipt();
        lztReceipt.setMerId(merId);
        lztReceipt.setTxnSeqno(StringUtils.N_TO_10("HZ_"));
        lztReceipt.setTotalAmount(totalAmount);
        lztReceipt.setTradeTxnSeqno(tradeTxnSeqno);
        lztReceipt.setTradeBillType(tradeBillType);
        lztReceipt.setMemo(memo);

        Merchant merchant = merchantService.getById(merId);
        MerchantPayInfo payInfo = merchant.getPayInfo();
        ReceiptProduceResult receiptProduceResult = lztService.receiptProduce(payInfo.getOidPartner(), payInfo.getPriKey(),
                lztReceipt.getTxnSeqno(), totalAmount, tradeBillType, memo, tradeTxnSeqno);

        lztReceipt.setReceiptAccpTxno(receiptProduceResult.getReceipt_accp_txno());
        lztReceipt.setToken(receiptProduceResult.getToken());
        lztReceipt.setTradeAccpTxno(receiptProduceResult.getTrade_accp_txno());
        save(lztReceipt);

        // 提现记录
        LztWithdrawal lztWithdrawal = lztWithdrawalService.getByTxnSeqno(tradeTxnSeqno);
        if(lztWithdrawal != null){
            lztWithdrawal.setReceiptStatus(1);
            lztWithdrawal.setReceiptToken(lztReceipt.getToken());
            lztWithdrawal.setReceiptAccpTxno(lztReceipt.getReceiptAccpTxno());
            lztWithdrawalService.updateById(lztWithdrawal);
        }
        // 转账记录
        LztTransferMorepyee lztTransferMorepyee = lztTransferMorepyeeService.getByTxnSeqno(tradeTxnSeqno);
        if(lztTransferMorepyee != null){
            lztTransferMorepyee.setReceiptStatus(1);
            lztTransferMorepyee.setReceiptToken(lztReceipt.getToken());
            lztTransferMorepyee.setReceiptAccpTxno(lztReceipt.getReceiptAccpTxno());
            lztTransferMorepyeeService.updateById(lztTransferMorepyee);
        }
        // 外部转账记录
        LztTransfer lztTransfer = lztTransferService.getByTxnSeqno(tradeTxnSeqno);
        if(lztTransfer != null){
            lztTransfer.setReceiptStatus(1);
            lztTransfer.setReceiptToken(lztReceipt.getToken());
            lztTransfer.setReceiptAccpTxno(lztReceipt.getReceiptAccpTxno());
            lztTransferService.updateById(lztTransfer);
        }
        return lztReceipt;
    }

    @Override
    public ReceiptDownloadResult download(String tradeTxnSeqno) {
        ReceiptDownloadResult result = new ReceiptDownloadResult();
        // 提现记录
        LztWithdrawal lztWithdrawal = lztWithdrawalService.getByTxnSeqno(tradeTxnSeqno);
        if (lztWithdrawal != null && StringUtils.isEmpty(lztWithdrawal.getReceiptZip())) {
            Merchant merchant = merchantService.getById(lztWithdrawal.getMerId());
            MerchantPayInfo payInfo = merchant.getPayInfo();
            result = lztService.receiptDownload(payInfo.getOidPartner(), payInfo.getPriKey(),
                    lztWithdrawal.getReceiptAccpTxno(), lztWithdrawal.getReceiptToken());

            lztWithdrawal.setReceiptZip(result.getReceipt_sum_file());
            lztWithdrawalService.updateById(lztWithdrawal);
        }
        if(lztWithdrawal != null){
            result.setReceipt_sum_file(lztWithdrawal.getReceiptZip());
        }
        // 转账记录
        LztTransferMorepyee lztTransferMorepyee = lztTransferMorepyeeService.getByTxnSeqno(tradeTxnSeqno);
        if (lztTransferMorepyee != null && StringUtils.isEmpty(lztTransferMorepyee.getReceiptZip())) {
            Merchant merchant = merchantService.getById(lztTransferMorepyee.getMerId());
            MerchantPayInfo payInfo = merchant.getPayInfo();
            result = lztService.receiptDownload(payInfo.getOidPartner(), payInfo.getPriKey(),
                    lztTransferMorepyee.getReceiptAccpTxno(), lztTransferMorepyee.getReceiptToken());
            lztTransferMorepyee.setReceiptZip(result.getReceipt_sum_file());
            lztTransferMorepyeeService.updateById(lztTransferMorepyee);
        }
        if(lztTransferMorepyee != null){
            result.setReceipt_sum_file(lztTransferMorepyee.getReceiptZip());
        }
        // 外部转账记录
        LztTransfer lztTransfer = lztTransferService.getByTxnSeqno(tradeTxnSeqno);
        if (lztTransfer != null && StringUtils.isEmpty(lztTransfer.getReceiptZip())) {
            Merchant merchant = merchantService.getById(lztTransfer.getMerId());
            MerchantPayInfo payInfo = merchant.getPayInfo();
            result = lztService.receiptDownload(payInfo.getOidPartner(), payInfo.getPriKey(),
                    lztTransfer.getReceiptAccpTxno(), lztTransfer.getReceiptToken());
            lztTransfer.setReceiptZip(result.getReceipt_sum_file());
            lztTransferService.updateById(lztTransfer);
        }
        if(lztTransfer != null){
            result.setReceipt_sum_file(lztTransferMorepyee.getReceiptZip());
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
