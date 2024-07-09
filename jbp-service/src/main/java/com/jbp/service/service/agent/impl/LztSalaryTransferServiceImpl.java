package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztLianLianBankCode;
import com.jbp.common.model.agent.LztSalaryTransfer;
import com.jbp.common.model.agent.LztTransfer;
import com.jbp.common.request.agent.LztSalaryTransferRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.dao.agent.LztLianLianBankCodeDao;
import com.jbp.service.dao.agent.LztSalaryTransferDao;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztSalaryTransferService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztSalaryTransferServiceImpl extends ServiceImpl<LztSalaryTransferDao, LztSalaryTransfer> implements LztSalaryTransferService {

    @Resource
    private LztLianLianBankCodeDao lztLianLianBankCodeDao;
    @Resource
    private LztAcctService lztAcctService;

    @Override
    public void create(LztAcct payer, List<LztSalaryTransferRequest> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("表格不能为空");
        }

        List<LztSalaryTransfer> salaryTransferList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        for (LztSalaryTransferRequest salary : list) {
            salary.setBankAcctName(StringUtils.trim(salary.getBankAcctName()));
            salary.setBankAcctNo(StringUtils.trim(salary.getBankAcctNo()));
            salary.setBankName(StringUtils.trim(salary.getBankName()));
            salary.setTime(StringUtils.trim(salary.getTime()));
            if (salary.getAmt() == null || ArithmeticUtils.lessEquals(salary.getAmt(), BigDecimal.ZERO)) {
                throw new RuntimeException("金额错误:" + salary.getBankAcctNo());
            }
            if (map.get(salary.getBankAcctNo()) != null) {
                throw new RuntimeException("重复发放:" + salary.getBankAcctNo());
            } else {
                map.put(salary.getBankAcctNo(), salary.getBankName());
            }
            if (StringUtils.isAnyEmpty(salary.getBankAcctName(), salary.getBankAcctNo(), salary.getBankName(), salary.getTime())) {
                throw new RuntimeException("导入数据存在空行");
            }
            LztLianLianBankCode lztLianLianBankCode = lztLianLianBankCodeDao.selectOne(new LambdaQueryWrapper<LztLianLianBankCode>().eq(LztLianLianBankCode::getBankName, salary.getBankName()));
            if (lztLianLianBankCode == null) {
                throw new RuntimeException("收款银行不存在:" + salary.getBankAcctNo());
            }
            salary.setBankCode(lztLianLianBankCode.getBankCode());
            String txnSeqno = com.jbp.service.util.StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通外部代发2.getPrefix());
            BigDecimal feeAmount = lztAcctService.getFee("代付", payer.getUserId(), salary.getAmt());
            BigDecimal amt = salary.getAmt().add(feeAmount);
            LztSalaryTransfer salaryTransfer = new LztSalaryTransfer(payer.getMerId(), payer.getUserId(), payer.getUsername(), txnSeqno, amt, feeAmount, "BANKACCT_PRI", salary.getBankAcctNo(), salary.getBankName(), salary.getBankCode(), salary.getBankAcctName(), "服务费", payer.getPayChannelType(), salary.getTime());
            salaryTransferList.add(salaryTransfer);
        }
        // 保存数据
        saveBatch(salaryTransferList);
    }






}
