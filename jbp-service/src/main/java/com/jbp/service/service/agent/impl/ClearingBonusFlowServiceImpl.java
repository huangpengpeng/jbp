package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ClearingBonus;
import com.jbp.common.model.agent.ClearingBonusFlow;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.ClearingBonusFlowDao;
import com.jbp.service.service.agent.ClearingBonusFlowService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingBonusFlowServiceImpl extends UnifiedServiceImpl<ClearingBonusFlowDao, ClearingBonusFlow> implements ClearingBonusFlowService {

    @Resource
    private ClearingBonusFlowDao dao;

    @Override
    public void insertBatchList(List<ClearingBonusFlow> list) {
        dao.insertBatch(list);
    }

    @Override
    public void del4Clearing(Long clearingId) {
        remove(new LambdaQueryWrapper<ClearingBonusFlow>().eq(ClearingBonusFlow::getClearingId, clearingId));
    }

    @Override
    public PageInfo<ClearingBonusFlow> pageList(Integer uid, String account, Long clearingId, PageParamRequest pageParamRequest) {
        Page<ClearingBonusFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<ClearingBonusFlow> lqw = new LambdaQueryWrapper<>();
        lqw.eq(clearingId != null, ClearingBonusFlow::getClearingId, clearingId);
        lqw.eq(uid != null, ClearingBonusFlow::getUid, uid);
        lqw.eq(StringUtils.isNotEmpty(account), ClearingBonusFlow::getAccountNo, account);
        lqw.orderByDesc(ClearingBonusFlow::getId);
        return CommonPage.copyPageInfo(page, list(lqw));
    }
}
