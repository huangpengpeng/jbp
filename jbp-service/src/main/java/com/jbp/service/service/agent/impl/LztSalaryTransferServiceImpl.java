package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.result.LztTransferResult;
import com.jbp.common.lianlian.result.QueryWithdrawalResult;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LztSalaryTransferRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.LztLianLianBankCodeDao;
import com.jbp.service.dao.agent.LztSalaryPayerDao;
import com.jbp.service.dao.agent.LztSalaryTransferDao;
import com.jbp.service.service.DegreePayService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztSalaryTransferService;
import com.jbp.service.util.StringUtils;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztSalaryTransferServiceImpl extends ServiceImpl<LztSalaryTransferDao, LztSalaryTransfer> implements LztSalaryTransferService {

    @Resource
    private LztLianLianBankCodeDao lztLianLianBankCodeDao;
    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private DegreePayService degreePayService;
    @Resource
    private MerchantService merchantService;
    @Resource
    private LztSalaryPayerDao lztSalaryPayerDao;

    @Override
    public LztSalaryPayer getSalaryPayer(Integer merId) {
        return lztSalaryPayerDao.selectOne(new LambdaQueryWrapper<LztSalaryPayer>().eq(LztSalaryPayer::getMerId, merId));
    }

    @SneakyThrows
    @Override
    public LztSalaryTransfer refresh(String txnSeqno) {
        LztSalaryTransfer lztSalaryTransfer = getByTxnSeqno(txnSeqno);
        if (lztSalaryTransfer == null) {
            return null;
        }
        if (LianLianPayConfig.TxnStatus.交易成功.getName().equals(lztSalaryTransfer.getTxnStatus())) {
            return lztSalaryTransfer;
        }
        LztAcct lztAcct = lztAcctService.getByUserId(lztSalaryTransfer.getPayerId());
        QueryWithdrawalResult result = degreePayService.transferQuery(lztAcct, txnSeqno);
        if(result != null && result.getTxn_status() != null){
            if (LianLianPayConfig.TxnStatus.交易成功.getCode().equals(result.getTxn_status())) {
                lztSalaryTransfer.setFinishTime(DateTimeUtils.parseDate(result.getFinish_time(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
            }
            lztSalaryTransfer.setTxnStatus(LianLianPayConfig.TxnStatus.getName(result.getTxn_status()));
            lztSalaryTransfer.setQueryRet(result);
            updateById(lztSalaryTransfer);
        }
        return lztSalaryTransfer;
    }

    @Override
    public PageInfo<LztSalaryTransfer> pageList(Integer merId, String payerId, String txnSeqno, String bankAcctNo,
                                                String bankAcctName, String status, String time, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<LztSalaryTransfer> lqw = new LambdaQueryWrapper<LztSalaryTransfer>()
                .select(LztSalaryTransfer.class, info -> !info.getColumn().equals("receipt_zip"))
                .eq(merId != null && merId > 0,  LztSalaryTransfer::getMerId, merId)
                .eq(StringUtils.isNotEmpty(status), LztSalaryTransfer::getTxnStatus, status)
                .eq(StringUtils.isNotEmpty(payerId), LztSalaryTransfer::getPayerId, payerId)
                .eq(StringUtils.isNotEmpty(txnSeqno), LztSalaryTransfer::getTxnSeqno, txnSeqno)
                .eq(StringUtils.isNotEmpty(bankAcctNo), LztSalaryTransfer::getBankAcctNo, bankAcctNo)
                .eq(StringUtils.isNotEmpty(bankAcctName), LztSalaryTransfer::getBankAcctName, bankAcctName)
                .orderByDesc(LztSalaryTransfer::getId);
        Page<LztSalaryTransfer> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<LztSalaryTransfer> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> merIdList = list.stream().map(LztSalaryTransfer::getMerId).collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        list.forEach(s -> {
            Merchant merchant = merchantMap.get(s.getMerId());
            if (merchant == null) {
                s.setMerName("平台");
            } else {
                s.setMerName(merchant.getName());
                s = refresh(s.getTxnSeqno());
            }
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public LztSalaryTransfer getByTxnSeqno(String txnSeqno) {
        return getOne(new LambdaQueryWrapper<LztSalaryTransfer>().eq(LztSalaryTransfer::getTxnSeqno, txnSeqno));
    }

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

    @Override
    public void del(LztAcct payer, List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            throw new RuntimeException("请选择需要删除的发放记录");
        }
        List<LztSalaryTransfer> list = listByIds(idList);
        for (LztSalaryTransfer lztSalaryTransfer : list) {
            if (lztSalaryTransfer.getMerId().intValue() != payer.getMerId().intValue()) {
                throw new RuntimeException("删除权限不足");
            }
            if (!StringUtils.equals(lztSalaryTransfer.getTxnStatus(), LianLianPayConfig.TxnStatus.已创建.getName())) {
                throw new RuntimeException("只允许删除已创建的记录:" + lztSalaryTransfer.getTxnSeqno() + "状态:" + lztSalaryTransfer.getTxnStatus());
            }
        }
        removeByIds(list.stream().map(LztSalaryTransfer::getId).collect(Collectors.toList()));
    }

    @Override
    public LztSalaryTransfer send(LztAcct payer, Long id, String pwd, String random_key, String payCode, String ip) {
        if (id == null) {
            throw new RuntimeException("请选择发放记录");
        }
        LztSalaryTransfer lztSalaryTransfer = getById(id);
        if (lztSalaryTransfer.getMerId().intValue() != payer.getMerId().intValue()) {
            throw new RuntimeException("发放权限不足");
        }
        if (!StringUtils.equals(lztSalaryTransfer.getTxnStatus(), LianLianPayConfig.TxnStatus.已创建.getName())) {
            throw new RuntimeException("只允许发放已创建的记录:" + lztSalaryTransfer.getTxnSeqno() + "状态:" + lztSalaryTransfer.getTxnStatus());
        }
        lztSalaryTransfer.setTxnSeqno(payCode);
        LztTransferResult transferResult = degreePayService.transfer2(payer, "服务费", lztSalaryTransfer.getTxnSeqno(),
                lztSalaryTransfer.getAmt().add(lztSalaryTransfer.getFeeAmount()).toString(), lztSalaryTransfer.getFeeAmount().toString(), pwd, random_key, "BANKACCT_PRI",
                lztSalaryTransfer.getBankAcctNo(), lztSalaryTransfer.getBankCode(), lztSalaryTransfer.getBankAcctName(),
                "", "服务费", ip);
        lztSalaryTransfer.setOrderRet(transferResult);
        updateById(lztSalaryTransfer);
        return lztSalaryTransfer;
    }

    @Override
    public LztSalaryTransfer autoSend(LztAcct payer, Long id, String ip, String pwd, String randomKey) {
        if (id == null) {
            throw new RuntimeException("请选择发放记录");
        }
        LztSalaryTransfer lztSalaryTransfer = getById(id);
        if (lztSalaryTransfer.getMerId().intValue() != payer.getMerId().intValue()) {
            throw new RuntimeException("发放权限不足");
        }
        if (!StringUtils.equals(lztSalaryTransfer.getTxnStatus(), LianLianPayConfig.TxnStatus.已创建.getName())) {
            throw new RuntimeException("只允许发放已创建的记录:" + lztSalaryTransfer.getTxnSeqno() + "状态:" + lztSalaryTransfer.getTxnStatus());
        }
        LztTransferResult transferResult = degreePayService.transfer2(payer, "服务费", lztSalaryTransfer.getTxnSeqno(),
                lztSalaryTransfer.getAmt().toString(), lztSalaryTransfer.getFeeAmount().toString(), pwd, randomKey, "BANKACCT_PRI",
                lztSalaryTransfer.getBankAcctNo(), lztSalaryTransfer.getBankCode(), lztSalaryTransfer.getBankAcctName(),
                "", "服务费", ip);
        lztSalaryTransfer.setTxnStatus(LianLianPayConfig.TxnStatus.交易处理中.getName());
        lztSalaryTransfer.setOrderRet(transferResult);
        updateById(lztSalaryTransfer);
        return lztSalaryTransfer;
    }
}
