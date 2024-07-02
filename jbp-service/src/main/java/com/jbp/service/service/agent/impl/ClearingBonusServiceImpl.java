package com.jbp.service.service.agent.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ClearingBonus;
import com.jbp.common.model.user.User;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ClearingBonusListResponse;
import com.jbp.service.dao.agent.ClearingBonusDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ClearingBonusService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingBonusServiceImpl extends UnifiedServiceImpl<ClearingBonusDao, ClearingBonus> implements ClearingBonusService {

    @Resource
    private ClearingBonusDao dao;
    @Resource
    private UserService userService;

    @Override
    public void insertBatchList(List<ClearingBonus> list) {
        dao.insertBatch(list);
    }

    @Override
    public void del4Clearing(Long clearingId) {
        remove(new LambdaQueryWrapper<ClearingBonus>().eq(ClearingBonus::getClearingId, clearingId));
    }

    @Override
    public List<ClearingBonus> get4Clearing(Long clearingId) {
        return list(new LambdaQueryWrapper<ClearingBonus>().eq(ClearingBonus::getClearingId, clearingId));
    }

    @Override
    public PageInfo<ClearingBonus> pageList(Integer uid, String account, String uniqueNo, Long clearingId, PageParamRequest pageParamRequest) {
        Page<ClearingBonus> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<ClearingBonus> lqw = new LambdaQueryWrapper<>();
        lqw.eq(clearingId != null, ClearingBonus::getClearingId, clearingId);
        lqw.eq(uid != null, ClearingBonus::getUid, uid);
        lqw.eq(StringUtils.isNotEmpty(uniqueNo), ClearingBonus::getUniqueNo, uniqueNo);
        lqw.eq(StringUtils.isNotEmpty(account), ClearingBonus::getAccountNo, account);
        lqw.orderByDesc(ClearingBonus::getId);
        List<ClearingBonus> list = list(lqw);
        if (list.isEmpty()){
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<Integer> uidList = list.stream().map(ClearingBonus::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uidList);
        list.forEach(e->{
            User user = uidMapList.get(e.getUid());
            e.setNickName(user!= null ? user.getNickname() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public PageInfo<ClearingBonusListResponse> getClearingList(Integer uid, PageParamRequest pageParamRequest) {
        Page<ClearingBonus> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, dao.getClearingList(uid));
    }

}

