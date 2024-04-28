package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.result.AcctInfoResult;
import com.jbp.common.lianlian.result.OpenacctApplyResult;
import com.jbp.common.lianlian.result.UserInfoResult;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctOpen;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.LztAcctOpenDao;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztAcctOpenService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztAcctOpenServiceImpl extends ServiceImpl<LztAcctOpenDao, LztAcctOpen> implements LztAcctOpenService {

    @Resource
    private LztService lztService;
    @Resource
    private MerchantService merchantService;
    @Resource
    private LztAcctService lztAcctService;

    @Override
    public LztAcctOpen apply(Integer merId, String userId, String userType, String returnUrl, String businessScope) {
        if (has(userId)) {
            throw new CrmebException("当前账户已存在，建议使用企业全拼加序号");
        }
        Merchant merchant = merchantService.getById(merId);
        final MerchantPayInfo payInfo = merchant.getPayInfo();
        if (payInfo == null || StringUtils.isEmpty(payInfo.getOidPartner()) || StringUtils.isEmpty(payInfo.getPriKey())) {
            throw new CrmebException("当前商户号配置缺少，请联系管理员");
        }
        String txnSeqno = StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通开通子商户.getPrefix());
        String flagChnl = "H5";
        String code = LianLianPayConfig.UserType.getCode(userType);
        String notifyUrl = "/api/publicly/payment/callback/lianlian/lzt/" + txnSeqno;
        OpenacctApplyResult result = lztService.createUser(payInfo.getOidPartner(), payInfo.getPriKey(), txnSeqno,
                userId, code, notifyUrl, returnUrl, "H5", businessScope);
        LztAcctOpen lztAcctOpen = new LztAcctOpen(merId, userId, txnSeqno, result.getAccp_txno(),
                userType, flagChnl, DateTimeUtils.getNow(), result.getGateway_url());
        save(lztAcctOpen);
        return lztAcctOpen;
    }


    @Override
    public void refresh(String accpTxno) {
        LztAcctOpen lztAcctOpen = getByAccpTxno(accpTxno);
        if (lztAcctOpen == null) {
            return;
        }
        Merchant merchant = merchantService.getById(lztAcctOpen.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        UserInfoResult result = lztService.queryUserInfo(payInfo.getOidPartner(), payInfo.getPriKey(), lztAcctOpen.getUserId());
        lztAcctOpen.setQueryRet(result);
        lztAcctOpen.setStatus(LianLianPayConfig.UserStatus.getName(result.getUser_status()));
        lztAcctOpen.setRetMsg(result.getRemark());
        updateById(lztAcctOpen);
        if (lztAcctOpen.getStatus().equals(LianLianPayConfig.UserStatus.正常.name())) {
            LztAcct lztAcct = lztAcctService.getByUserId(lztAcctOpen.getUserId());
            if (lztAcct == null) {
                lztAcctService.create(lztAcctOpen.getMerId(), lztAcctOpen.getUserId(), lztAcctOpen.getUserType(), result.getOid_userno(), result.getUser_name(), result.getBank_account());
            }
        }
    }

    @Override
    public void del(Long id) {
        LztAcctOpen lztAcctOpen = getById(id);
        if (lztAcctOpen == null || lztAcctOpen.getStatus().equals(LianLianPayConfig.UserStatus.正常.name())) {
            throw new RuntimeException("当前记录已经开户完成不允许删除, 请联系管理员处理");
        }
        Merchant merchant = merchantService.getById(lztAcctOpen.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        UserInfoResult result = lztService.queryUserInfo(payInfo.getOidPartner(), payInfo.getPriKey(), lztAcctOpen.getUserId());
        if(result != null && "0000".equals(result.getRet_code())){
            lztAcctOpen.setStatus(LianLianPayConfig.UserStatus.getName(result.getUser_status()));
            lztAcctOpen.setRetMsg(result.getRet_msg());
            updateById(lztAcctOpen);
        }
        if (lztAcctOpen.getStatus().equals(LianLianPayConfig.UserStatus.正常.name())) {
            throw new RuntimeException("当前记录已经开户完成不允许删除, 请联系管理员处理");
        }
        removeById(id);
    }

    @Override
    public LztAcctOpen getByTxnSeqno(String txnSeqno) {
        return getOne(new QueryWrapper<LztAcctOpen>().lambda().eq(LztAcctOpen::getTxnSeqno, txnSeqno));
    }

    @Override
    public LztAcctOpen getByAccpTxno(String accpTxno) {
        return getOne(new QueryWrapper<LztAcctOpen>().lambda().eq(LztAcctOpen::getAccpTxno, accpTxno));
    }

    @Override
    public Boolean has(String userId) {
        return CollectionUtils.isNotEmpty(list(new QueryWrapper<LztAcctOpen>().lambda().eq(LztAcctOpen::getUserId, userId)));
    }


	@Override
	public PageInfo<LztAcctOpen> pageList(Integer merId, String userId, String status,
			PageParamRequest pageParamRequest) {
		LambdaQueryWrapper<LztAcctOpen> lqw = new LambdaQueryWrapper<LztAcctOpen>()
				.eq(StringUtils.isNotEmpty(userId), LztAcctOpen::getUserId, userId)
				.eq(StringUtils.isNotEmpty(status), LztAcctOpen::getStatus, status)
				.eq(merId != null && merId > 0, LztAcctOpen::getMerId, merId).orderByDesc(LztAcctOpen::getId);

		Page<LztAcctOpen> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
		List<LztAcctOpen> list = list(lqw);
		if (CollectionUtils.isEmpty(list)) {
			return CommonPage.copyPageInfo(page, list);
		}

		List<Integer> merIdList = list.stream().map(LztAcctOpen::getMerId).collect(Collectors.toList());
		Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
		list.forEach(s -> {
			Merchant merchant = merchantMap.get(s.getMerId());
			if (merchant == null) {
				s.setMerName("平台");
			} else {
				s.setMerName(merchant.getName());
			}
			// 检查第三方开户状态
			if (!s.getStatus().equals(LianLianPayConfig.UserStatus.正常.name())) {
				MerchantPayInfo payInfo = merchant.getPayInfo();
				AcctInfoResult acctInfoResult = lztService.queryAcct(payInfo.getOidPartner(), payInfo.getPriKey(),
						s.getUserId(), LianLianPayConfig.UserType.getCode(s.getUserType()));

				if (CollectionUtils.isNotEmpty(acctInfoResult.getAcctinfo_list())) {
					String extStatus = acctInfoResult.getAcctinfo_list().get(0).getAcct_state();
					if (extStatus.equals(LianLianPayConfig.UserStatus.正常.getCode())) {
						refresh(s.getAccpTxno());
					}
				}
			}
		});

		return CommonPage.copyPageInfo(page, list);
	}
}
