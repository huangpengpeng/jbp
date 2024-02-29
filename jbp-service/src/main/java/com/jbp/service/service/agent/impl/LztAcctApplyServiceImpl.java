package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.params.LztOpenacctApplyParams;
import com.jbp.common.lianlian.result.LztOpenacctApplyResult;
import com.jbp.common.lianlian.result.LztQueryAcctInfoResult;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.service.dao.agent.LztAcctApplyDao;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.agent.LztAcctApplyService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztAcctApplyServiceImpl extends ServiceImpl<LztAcctApplyDao, LztAcctApply> implements LztAcctApplyService {

   @Resource
   private LianLianPayService lianLianPayService;
   @Resource
   private LztAcctService lztAcctService;

    @Override
    public LztAcctApply apply(Integer merId, String userId, String shopId, String shopName, String province,
                              String city, String area, String address) {
        String txnSeqno = StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通开通银行虚拟户.getPrefix());
        String notifyUrl = "/api/publicly/payment/callback/lianlian/lzt/"+txnSeqno;
        LztOpenacctApplyParams params = new LztOpenacctApplyParams(userId, txnSeqno, notifyUrl, "USER", shopId,
                shopName, province, city, area, address);
        LztOpenacctApplyResult result = lianLianPayService.lztOpenacctApply(params);
        LztAcctApply lztAcctApply = new LztAcctApply(merId, userId, txnSeqno, result.getAccp_txno(),
                result.getGateway_url());
        save(lztAcctApply);
        return lztAcctApply;
    }

    @Override
    public LztAcctApply refresh(String userId) {
        LztAcctApply lztAcctApply = getByUserId(userId);
        LztQueryAcctInfoResult result = lianLianPayService.lztQueryAcctInfo(userId);
        lztAcctApply.setRetMsg(result.getRet_msg());
        if (CollectionUtils.isNotEmpty(result.getList())) {
            lztAcctApply.setStatus("已完成");
            lztAcctService.create(lztAcctApply.getMerId(), userId);
        }
        updateById(lztAcctApply);
        return lztAcctApply;
    }

    @Override
    public LztAcctApply getByUserId(String userId) {
        return getOne(new QueryWrapper<LztAcctApply>().lambda().eq(LztAcctApply::getLianLianAcct, userId));
    }

    @Override
    public LztAcctApply getByTxnSeqno(String txnSeqno) {
        return getOne(new QueryWrapper<LztAcctApply>().lambda().eq(LztAcctApply::getTxnSeqno, txnSeqno));
    }
}
