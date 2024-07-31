package com.jbp.service.service.agent.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.agent.WalletWithdraw;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.WalletWithdrawCancelRequest;
import com.jbp.common.request.agent.WalletWithdrawRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.common.vo.WalletWithdrawExcelInfoVo;
import com.jbp.common.vo.WalletWithdrawVo;
import com.jbp.service.dao.agent.WalletWithdrawDao;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.agent.ChannelCardService;
import com.jbp.service.service.agent.ChannelIdentityService;
import com.jbp.service.service.agent.WalletService;
import com.jbp.service.service.agent.WalletWithdrawService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class WalletWithdrawServiceImpl extends ServiceImpl<WalletWithdrawDao, WalletWithdraw> implements WalletWithdrawService {

    @Resource
    private WalletService walletService;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private ChannelIdentityService channelIdentityService;
    @Resource
    private ChannelCardService channelCardService;
    @Resource
    private WalletWithdrawDao walletWithdrawDao;

    @Override
    public PageInfo<WalletWithdrawVo> pageList(String account, String walletName, String status, String dateLimit, String realName,String nickName, String teamId,PageParamRequest pageParamRequest) {
        String channelName = systemConfigService.getValueByKey("pay_channel_name");
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        Page<WalletWithdrawVo> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<WalletWithdrawVo> walletWithdrawsList = walletWithdrawDao.pageList(account, walletName, status, dateLimitUtilVo.getEndTime(), dateLimitUtilVo.getStartTime(), realName, channelName,nickName,teamId);
        return CommonPage.copyPageInfo(page, walletWithdrawsList);
    }

    @Override
    public WalletWithdrawExcelInfoVo excel(String account, String walletName, String status, String realName, String dateLimit,String nickName, String teamId) {
        String channelName = systemConfigService.getValueByKey("pay_channel_name");
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        Integer id = 0;
        List<WalletWithdrawVo> voList = CollUtil.newArrayList();
        do {
            List<WalletWithdrawVo> fundClearingVos = walletWithdrawDao.excel(id, account, walletName, status, realName, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime(), channelName,nickName,teamId);
            if (CollectionUtils.isEmpty(fundClearingVos)) {
                break;
            }
            voList.addAll(fundClearingVos);
            id = fundClearingVos.get(fundClearingVos.size() - 1).getId();
        } while (true);
        return getWalletWithdrawExcelInfoVo(voList);
    }

    private static WalletWithdrawExcelInfoVo getWalletWithdrawExcelInfoVo(List<WalletWithdrawVo> voList) {
        WalletWithdrawExcelInfoVo walletWithdrawExcelInfoVo = new WalletWithdrawExcelInfoVo();
        LinkedHashMap<String, String> head = new LinkedHashMap<String, String>();
        head.put("account", "账户");
        head.put("walletName", "钱包名称");
        head.put("uniqueNo", "流水单号");
        head.put("amt", "提现金额");
        head.put("commission", "手续费");
        head.put("status", "状态");
        head.put("postscript", "附言");
        head.put("createTime", "创建时间");
        head.put("successTime", "成功时间");
        head.put("remark", "备注");
        head.put("bankName", "银行卡名称");
        head.put("bankCode", "银行卡号");
        head.put("realName", "真实姓名");
        head.put("nickName","用户昵称");
        head.put("idCardNo","身份证");
        head.put("phone","手机号");
        head.put("teamName","团队");
        JSONArray array = new JSONArray();
        head.forEach((k,v)->{
            JSONObject json = new JSONObject();
            json.put("k", k);
            json.put("v", v);
            array.add(json);
        });
        walletWithdrawExcelInfoVo.setHead(array);
        walletWithdrawExcelInfoVo.setList(voList);
        return walletWithdrawExcelInfoVo;
    }

    @Override
    public WalletWithdraw create(Integer uid, String account, Integer walletType, String walletName, BigDecimal amt, String postscript) {
        if (amt == null || ArithmeticUtils.less(amt, BigDecimal.valueOf(1))) {
            throw new CrmebException("提现金额异常不能低于1");
        }
        Wallet wallet = walletService.getByUser(uid, walletType);
        if (wallet == null || ArithmeticUtils.less(wallet.getBalance(), amt)) {
            throw new CrmebException("余额不足");
        }
        String commissionScale = systemConfigService.getValueByKey("wallet_withdraw_commission");
        BigDecimal scale = StringUtils.isEmpty(commissionScale) ? BigDecimal.ZERO : new BigDecimal(commissionScale);
        BigDecimal commission = amt.multiply(scale).setScale(2, BigDecimal.ROUND_DOWN);
        WalletWithdraw walletWithdraw = new WalletWithdraw(uid, account, walletType, walletName, amt.subtract(commission), commission, postscript);
        save(walletWithdraw);
        walletService.reduce(uid, walletType, amt, WalletFlow.OperateEnum.提现.name(), walletWithdraw.getUniqueNo(), postscript);
        return walletWithdraw;
    }

    @Override
    public WalletWithdraw getByUniqueNo(String uniqueNo) {
        return getOne(new QueryWrapper<WalletWithdraw>().lambda().eq(WalletWithdraw::getUniqueNo, uniqueNo));
    }

    @Override
    public void send(List<WalletWithdrawRequest> walletWithdrawList) {
        if (CollectionUtils.isEmpty(walletWithdrawList)) {
            throw new CrmebException("提现信息不能为空");
        }
        List<WalletWithdraw> list = Lists.newArrayList();
        Date now = DateTimeUtils.getNow();
        int i = 1;
        for (WalletWithdrawRequest withdrawRequest : walletWithdrawList) {
            if (StringUtils.isEmpty(withdrawRequest.getUniqueNo())) {
                throw new CrmebException("提现信息单号不能为空，行号:" + i);
            }
            WalletWithdraw walletWithdraw = getByUniqueNo(withdrawRequest.getUniqueNo());
            if (walletWithdraw == null) {
                throw new CrmebException("提现信息不存在，行号:" + i);
            }
            if (!walletWithdraw.getStatus().equals(WalletWithdraw.StatusEnum.待出款.toString())) {
                throw new CrmebException("提现状态不是待出库，行号:" + i);
            }
            walletWithdraw.setStatus(WalletWithdraw.StatusEnum.已出款.toString());
            walletWithdraw.setRemark(withdrawRequest.getRemark());
            walletWithdraw.setSuccessTime(now);
            list.add(walletWithdraw);
            i++;
        }
        List<List<WalletWithdraw>> partition = com.google.common.collect.Lists.partition(list, 100);
        for (List<WalletWithdraw> walletWithdraws : partition) {
            boolean ifSuccess = updateBatchById(walletWithdraws);
            if (BooleanUtils.isNotTrue(ifSuccess)) {
                throw new CrmebException("当前操作人数过多");
            }
        }
    }

    @Override
    public void cancel(WalletWithdrawCancelRequest request) {
        if (CollectionUtils.isEmpty(request.getUniqueNos())) {
            throw new CrmebException("提现单号不能为空");
        }
        List<WalletWithdraw> list = Lists.newArrayList();
        Date now = DateTimeUtils.getNow();
        int i = 1;
        for (String uniqueNo : request.getUniqueNos()) {
            if (StringUtils.isEmpty(uniqueNo)) {
                throw new CrmebException("提现信息单号不能为空，行号:" + i);
            }
            WalletWithdraw walletWithdraw = getByUniqueNo(uniqueNo);
            if (walletWithdraw == null) {
                throw new CrmebException("提现信息不存在，行号:" + i);
            }
            if (!walletWithdraw.getStatus().equals(WalletWithdraw.StatusEnum.待出款.toString())) {
                throw new CrmebException("提现状态不是待出款，行号:" + i);
            }
            walletWithdraw.setStatus(WalletWithdraw.StatusEnum.已取消.toString());
            walletWithdraw.setRemark(request.getRemark());
            walletWithdraw.setSuccessTime(now);
            list.add(walletWithdraw);
            i++;
        }
        List<List<WalletWithdraw>> partition = com.google.common.collect.Lists.partition(list, 100);
        for (List<WalletWithdraw> walletWithdraws : partition) {
            for (WalletWithdraw walletWithdraw : walletWithdraws) {
                walletService.increase(walletWithdraw.getUid(), walletWithdraw.getWalletType(),
                        walletWithdraw.getAmt().add(walletWithdraw.getCommission()), WalletFlow.OperateEnum.提现取消.toString(), walletWithdraw.getUniqueNo(), walletWithdraw.getPostscript());
            }
            Boolean ifSuccess = updateBatchById(walletWithdraws);
            if (BooleanUtils.isNotTrue(ifSuccess)) {
                throw new CrmebException("当前操作人数过多");
            }
        }
    }

}
