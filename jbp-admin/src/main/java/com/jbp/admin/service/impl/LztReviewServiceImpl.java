package com.jbp.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.jbp.admin.service.LztReviewService;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.dto.LztReviewDto;
import com.jbp.common.lianlian.result.QueryWithdrawalResult;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztTransfer;
import com.jbp.common.model.agent.LztWithdrawal;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.DegreePayService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztTransferService;
import com.jbp.service.service.agent.LztWithdrawalService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztReviewServiceImpl implements LztReviewService {

    @Resource
    private LztWithdrawalService lztWithdrawalService;
    @Resource
    private LztTransferService lztTransferService;
    @Resource
    private DegreePayService degreePayService;
    @Resource
    private LztAcctService lztAcctService;

    @SneakyThrows
    @Override
    public List<LztReviewDto> list(Integer merId, Boolean ifDraw, Boolean ifPay, String status) {
        List<String> statuss = Lists.newArrayList(LianLianPayConfig.TxnStatus.交易失败.getName(), LianLianPayConfig.TxnStatus.交易关闭.getName(), LianLianPayConfig.TxnStatus.交易成功.getName());
        List<LztReviewDto> list = Lists.newArrayList();
        // 提现
        if (BooleanUtils.isTrue(ifDraw)) {
            LambdaQueryWrapper<LztWithdrawal> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LztWithdrawal::getMerId, merId);
            if ("复核".equals(status)) {
                wrapper.in(LztWithdrawal::getTxnStatus, Lists.newArrayList(LianLianPayConfig.TxnStatus.待确认.getName(), LianLianPayConfig.TxnStatus.交易处理中.getName()));
            }
            if ("成功".equals(status)) {
                wrapper.eq(LztWithdrawal::getTxnStatus, LianLianPayConfig.TxnStatus.交易成功.getName());
            }
            if ("交易中".equals(status)) {
                wrapper.in(LztWithdrawal::getTxnStatus, Lists.newArrayList(LianLianPayConfig.TxnStatus.交易处理中.getName(), LianLianPayConfig.TxnStatus.预付完成.getName(), "已复核"));
            }
            if ("取消".equals(status)) {
                wrapper.in(LztWithdrawal::getTxnStatus, Lists.newArrayList(LianLianPayConfig.TxnStatus.交易失败.getName(), LianLianPayConfig.TxnStatus.交易关闭.getName()));
            }
            List<LztWithdrawal> withdrawalList = lztWithdrawalService.list(wrapper);

            for (LztWithdrawal lztWithdrawal : withdrawalList) {
                if (!statuss.contains(lztWithdrawal.getTxnStatus())) {
                    LztAcct lztAcct = lztAcctService.getByUserId(lztWithdrawal.getUserId());
                    QueryWithdrawalResult result = degreePayService.queryWithdrawal(lztAcct, lztWithdrawal.getTxnSeqno());
                    if(result != null && "订单查询无记录".equals(result.getRet_msg())){
                        lztWithdrawal.setTxnStatus(LianLianPayConfig.TxnStatus.交易关闭.getName());
                        lztWithdrawal.setQueryRet(result);
                        lztWithdrawalService.updateById(lztWithdrawal);
                    }else{
                        LztWithdrawal query = lztWithdrawalService.callBack(result);
                        if(query != null){
                            lztWithdrawal = query;
                        }
                    }
                }
                if ("复核".equals(status) && !LianLianPayConfig.TxnStatus.待确认.getName().equals(lztWithdrawal.getTxnStatus())) {
                    continue;
                }
                LztReviewDto lztReview = LztReviewDto.builder()
                        .id(lztWithdrawal.getId())
                        .type("提现")
                        .txnSeqno(lztWithdrawal.getTxnSeqno())
                        .createTime(DateTimeUtils.format(lztWithdrawal.getGmtCreated(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN))
                        .userId(lztWithdrawal.getUserId())
                        .username(lztWithdrawal.getUsername())
                        .amt(lztWithdrawal.getAmt().toString())
                        .feeAmount(lztWithdrawal.getFeeAmount() == null ? "0" : lztWithdrawal.getFeeAmount().toString())
                        .updateTime(DateTimeUtils.format(lztWithdrawal.getGmtModify(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN))
                        .status(statusConversion(lztWithdrawal.getTxnStatus()))

                        .build();
                list.add(lztReview);
            }
        }

        // 代付
        if (BooleanUtils.isTrue(ifPay)) {
            LambdaQueryWrapper<LztTransfer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LztTransfer::getMerId, merId);
            if ("复核".equals(status)) {
                wrapper.in(LztTransfer::getTxnStatus, Lists.newArrayList(LianLianPayConfig.TxnStatus.待确认.getName(), LianLianPayConfig.TxnStatus.交易处理中.getName()));
            }
            if ("成功".equals(status)) {
                wrapper.eq(LztTransfer::getTxnStatus, LianLianPayConfig.TxnStatus.交易成功.getName());
            }
            if ("交易中".equals(status)) {
                wrapper.in(LztTransfer::getTxnStatus, Lists.newArrayList(LianLianPayConfig.TxnStatus.交易处理中.getName(), LianLianPayConfig.TxnStatus.预付完成.getName(), "已复核"));
            }
            if ("取消".equals(status)) {
                wrapper.in(LztTransfer::getTxnStatus, Lists.newArrayList(LianLianPayConfig.TxnStatus.交易失败.getName(), LianLianPayConfig.TxnStatus.交易关闭.getName()));
            }
            List<LztTransfer> transferList = lztTransferService.list(wrapper);
            for (LztTransfer lztTransfer : transferList) {
                if (!statuss.contains(lztTransfer.getTxnStatus())) {
                    LztAcct lztAcct = lztAcctService.getByUserId(lztTransfer.getPayerId());
                    QueryWithdrawalResult result = degreePayService.transferQuery(lztAcct, lztTransfer.getTxnSeqno());
                    if(result != null && "订单查询无记录".equals(result.getRet_msg())){
                        lztTransfer.setTxnStatus(LianLianPayConfig.TxnStatus.交易关闭.getName());
                        lztTransfer.setQueryRet(result);
                        lztTransferService.updateById(lztTransfer);
                    }

                    if(result != null && result.getTxn_status() != null){
                        if (LianLianPayConfig.TxnStatus.交易成功.getCode().equals(result.getTxn_status())) {
                            lztTransfer.setFinishTime(DateTimeUtils.parseDate(result.getFinish_time(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
                        }
                        lztTransfer.setTxnStatus(LianLianPayConfig.TxnStatus.getName(result.getTxn_status()));
                        lztTransfer.setQueryRet(result);
                        lztTransferService.updateById(lztTransfer);
                    }
                }
                if ("复核".equals(status) && !LianLianPayConfig.TxnStatus.待确认.getName().equals(lztTransfer.getTxnStatus())) {
                    continue;
                }
                LztReviewDto lztReview = LztReviewDto.builder()
                        .id(lztTransfer.getId())
                        .type("代付")
                        .txnSeqno(lztTransfer.getTxnSeqno())
                        .createTime(DateTimeUtils.format(lztTransfer.getGmtCreated(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN))
                        .userId(lztTransfer.getBankAcctNo())
                        .username(lztTransfer.getBankAcctName())
                        .amt(lztTransfer.getAmt().toString())
                        .status(statusConversion(lztTransfer.getTxnStatus()))
                        .feeAmount(lztTransfer.getFeeAmount() == null ? "0" : lztTransfer.getFeeAmount().toString())
                        .updateTime(DateTimeUtils.format(lztTransfer.getGmtModify(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN))
                        .build();
                list.add(lztReview);
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            list = list.stream().sorted(Comparator.comparing(LztReviewDto::getUpdateTime).reversed()).collect(Collectors.toList());
        }
        return list;
    }


    public  static String statusConversion(String status){
        if (LianLianPayConfig.TxnStatus.交易成功.getName().equals(status)) {
           return "成功";
        }
        if (Lists.newArrayList(LianLianPayConfig.TxnStatus.交易处理中.getName()).contains(status)) {
            return "交易中";
        }
        return status;
    }
}
