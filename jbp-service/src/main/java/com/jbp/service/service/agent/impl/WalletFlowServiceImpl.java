package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.WalletFlowDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.WalletFlowService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

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
        WalletFlow walletFlow = new WalletFlow(uid, type, action, operate, StringUtils.N_TO_10("UPW_"), externalNo, postscript, amt, orgBalance, tagBalance);
        save(walletFlow);
        return walletFlow;
    }

    @Override
    public PageInfo<WalletFlow> pageList(Integer uid, Integer type, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<WalletFlow> walletLambdaQueryWrapper = new LambdaQueryWrapper<WalletFlow>()
                .eq(!ObjectUtil.isNull(uid), WalletFlow::getUid, uid)
                .eq(!ObjectUtil.isNull(type), WalletFlow::getWalletType, type);
        Page<WalletFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<WalletFlow> list = list(walletLambdaQueryWrapper);
        list.forEach(e -> {
            e.setTypeName(walletConfigService.getByType(e.getWalletType()).getName());
            e.setAccount(userService.getById(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }


}
