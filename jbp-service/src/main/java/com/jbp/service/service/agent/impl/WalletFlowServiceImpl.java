package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.common.vo.WalletFlowVo;
import com.jbp.service.dao.agent.WalletFlowDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.WalletFlowService;
import com.jbp.service.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class WalletFlowServiceImpl extends ServiceImpl<WalletFlowDao, WalletFlow> implements WalletFlowService {
    @Resource
    WalletConfigService walletConfigService;
    @Resource
    UserService userService;

    @Override
    public WalletFlow add(Integer uid, Integer type, BigDecimal amt, String operate, String action, String externalNo,
                          BigDecimal orgBalance, BigDecimal tagBalance, String postscript) {
        WalletFlow walletFlow = new WalletFlow(uid, type, action, operate, StringUtils.N_TO_10("WF_"),
                externalNo, postscript, amt, orgBalance, tagBalance);
        save(walletFlow);
        return walletFlow;
    }

    @Override
    public PageInfo<WalletFlow> pageList(Integer uid, Integer type, String dateLimit, String externalNo, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<WalletFlow> walletLambdaQueryWrapper = new LambdaQueryWrapper<WalletFlow>()
                .eq(!ObjectUtil.isNull(uid), WalletFlow::getUid, uid)
                .eq(!ObjectUtil.isNull(type), WalletFlow::getWalletType, type)
                .eq(StringUtils.isNotEmpty(externalNo), WalletFlow::getExternalNo, externalNo);
        getRequestTimeWhere(walletLambdaQueryWrapper, dateLimit);
        walletLambdaQueryWrapper.orderByDesc(WalletFlow::getId);
        Page<WalletFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<WalletFlow> list = list(walletLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(WalletFlow::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            WalletConfig walletConfig = walletConfigService.getByType(e.getWalletType());
            e.setTypeName(walletConfig != null ? walletConfig.getName() : "");
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    private void getRequestTimeWhere(LambdaQueryWrapper<WalletFlow> lqw, String dateLimit) {
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        lqw.between(StringUtils.isNotEmpty(dateLimit), WalletFlow::getGmtCreated, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
    }


    @Override
    public PageInfo<WalletFlow> pageWalletList(Integer uid, Integer type, String action, PageParamRequest pageParamRequest) {

        LambdaQueryWrapper<WalletFlow> walletLambdaQueryWrapper = new LambdaQueryWrapper<WalletFlow>()
                .eq(!ObjectUtil.isNull(uid), WalletFlow::getUid, uid)
                .eq(!ObjectUtil.isNull(type), WalletFlow::getWalletType, type)
                .eq(!ObjectUtil.isEmpty(action), WalletFlow::getAction, action)
                .orderByDesc(WalletFlow::getId);
        Page<WalletFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<WalletFlow> list = list(walletLambdaQueryWrapper);

        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public List<WalletFlow> details(Integer uid, String action) {
        LambdaQueryWrapper<WalletFlow> wrapper = new LambdaQueryWrapper<WalletFlow>()
                .eq(StringUtils.isNotEmpty(action), WalletFlow::getAction, action)
                .eq(WalletFlow::getUid, uid)
                .orderByDesc(WalletFlow::getId);
        return list(wrapper);
    }

    @Override
    public List<WalletFlowVo> excel(Integer uid, Integer type, String dateLimit, String externalNo) {
        Long id = 0L;
        List<WalletFlowVo> result = Lists.newArrayList();
        do {
            LambdaQueryWrapper<WalletFlow> walletLambdaQueryWrapper = new LambdaQueryWrapper<WalletFlow>()
                    .eq(!ObjectUtil.isNull(uid), WalletFlow::getUid, uid)
                    .eq(!ObjectUtil.isNull(type), WalletFlow::getWalletType, type)
                    .eq(StringUtils.isNotEmpty(externalNo), WalletFlow::getExternalNo, externalNo);
            getRequestTimeWhere(walletLambdaQueryWrapper, dateLimit);
            walletLambdaQueryWrapper.orderByDesc(WalletFlow::getId);
            walletLambdaQueryWrapper.orderByDesc(WalletFlow::getId);
            walletLambdaQueryWrapper.gt(WalletFlow::getId, id).last("LIMIT 1000");
            List<WalletFlow> list = list(walletLambdaQueryWrapper);
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(list)) {
                break;
            }
            List<Integer> uIdList = list.stream().map(WalletFlow::getUid).collect(Collectors.toList());
            Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
            list.forEach(e -> {
                WalletConfig walletConfig = walletConfigService.getByType(e.getWalletType());
                e.setTypeName(walletConfig != null ? walletConfig.getName() : "");
                User user = uidMapList.get(e.getUid());
                e.setAccount(user != null ? user.getAccount() : "");
                WalletFlowVo walletFlowVo = new WalletFlowVo();
                BeanUtils.copyProperties(e, walletFlowVo);
                result.add(walletFlowVo);
            });
            id = list.get(list.size() - 1).getId();
        } while (true);
        return result;
    }
}
