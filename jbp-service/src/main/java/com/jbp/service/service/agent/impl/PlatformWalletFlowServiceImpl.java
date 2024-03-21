package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.model.agent.PlatformWalletFlow;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.common.vo.PlatformWalletFlowVo;
import com.jbp.service.dao.agent.PlatformWalletFlowDao;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.PlatformWalletFlowService;
import com.jbp.service.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class PlatformWalletFlowServiceImpl extends ServiceImpl<PlatformWalletFlowDao, PlatformWalletFlow> implements PlatformWalletFlowService {
    @Resource
    WalletConfigService walletConfigService;

    @Override
    public PlatformWalletFlow add(Integer type, String operate, String action, String externalNo, String postscript,
                                  BigDecimal amt, BigDecimal orgBalance, BigDecimal tagBalance) {
        PlatformWalletFlow platformWalletFlow = new PlatformWalletFlow(type, action, operate, StringUtils.N_TO_10("PW_"), externalNo, postscript, amt, orgBalance, tagBalance);
        save(platformWalletFlow);
        return platformWalletFlow;
    }

    private void getRequestTimeWhere(LambdaQueryWrapper<PlatformWalletFlow> lqw, String dateLimit) {
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        lqw.between(StringUtils.isNotEmpty(dateLimit), PlatformWalletFlow::getGmtCreated, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
    }

    @Override
    public PageInfo<PlatformWalletFlow> pageList(Integer type, String dateLimit, String externalNo, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<PlatformWalletFlow> walletLambdaQueryWrapper = new LambdaQueryWrapper<PlatformWalletFlow>()
                .eq(!ObjectUtil.isNull(type), PlatformWalletFlow::getWalletType, type)
                .eq(StringUtils.isNotEmpty(externalNo), PlatformWalletFlow::getExternalNo, externalNo);
        getRequestTimeWhere(walletLambdaQueryWrapper, dateLimit);
        walletLambdaQueryWrapper.orderByDesc(PlatformWalletFlow::getId);
        Page<PlatformWalletFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<PlatformWalletFlow> list = list(walletLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        list.forEach(e -> {
            WalletConfig walletConfig = walletConfigService.getByType(e.getWalletType());
            e.setTypeName(walletConfig != null ? walletConfig.getName() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public List<PlatformWalletFlowVo> excel(Integer type, String dateLimit, String externalNo) {
        Long id = 0L;
        List<PlatformWalletFlowVo> result = Lists.newArrayList();
        do {
            LambdaQueryWrapper<PlatformWalletFlow> walletLambdaQueryWrapper = new LambdaQueryWrapper<PlatformWalletFlow>()
                    .eq(!ObjectUtil.isNull(type), PlatformWalletFlow::getWalletType, type)
                    .eq(StringUtils.isNotEmpty(externalNo), PlatformWalletFlow::getExternalNo, externalNo);
            getRequestTimeWhere(walletLambdaQueryWrapper, dateLimit);
            walletLambdaQueryWrapper.orderByDesc(PlatformWalletFlow::getId);
            walletLambdaQueryWrapper.gt(PlatformWalletFlow::getId, id).last("LIMIT 1000");
            List<PlatformWalletFlow> fundClearingList = list(walletLambdaQueryWrapper);
            if (CollectionUtils.isEmpty(fundClearingList)) {
                break;
            }
            fundClearingList.forEach(e -> {
                WalletConfig walletConfig = walletConfigService.getByType(e.getWalletType());
                e.setTypeName(walletConfig != null ? walletConfig.getName() : "");
                PlatformWalletFlowVo platformWalletFlowVo = new PlatformWalletFlowVo();
                BeanUtils.copyProperties(e, platformWalletFlowVo);
                result.add(platformWalletFlowVo);
            });
            id = fundClearingList.get(0).getId();
        } while (true);
        return result;
    }


}
