package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.tank.TankStoreClerkRelation;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.TankOrderAdminListResponse;
import com.jbp.common.response.TankStoreClerkAdminListResponse;
import com.jbp.common.response.TankStoreClerkManagerListResponse;
import com.jbp.service.dao.TankStoreClerkRelationDao;
import com.jbp.service.service.TankStoreClerkRelationService;
import com.jbp.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Service
public class TankStoreClerkRelationServiceImpl extends ServiceImpl<TankStoreClerkRelationDao, TankStoreClerkRelation> implements TankStoreClerkRelationService {

    @Resource
    private TankStoreClerkRelationDao dao;
    @Resource
    private UserService userService;

    @Override
    public TankStoreClerkRelation getClerkUserId(Integer clerkUserId){
        return  dao.selectOne(new QueryWrapper<TankStoreClerkRelation>().lambda().eq(TankStoreClerkRelation::getClerkUserId,clerkUserId));
    }

    @Override
    public List<TankStoreClerkRelation> getStoreUserId(Long storeUserId) {
        return  dao.selectList(new QueryWrapper<TankStoreClerkRelation>().lambda().eq(TankStoreClerkRelation::getStoreUserId,storeUserId));
    }

    @Override
    public List<TankStoreClerkRelation> getStoreId(Long storeId) {
        return  dao.selectList(new QueryWrapper<TankStoreClerkRelation>().lambda().eq(TankStoreClerkRelation::getStoreId,storeId));

    }

    @Override
    public List<TankStoreClerkManagerListResponse> getClerkList() {
        return dao.getClerkList(userService.getInfo().getId());
    }

    @Override
    public PageInfo<TankStoreClerkAdminListResponse> getAdminPageList(String username, String name, PageParamRequest pageParamRequest) {
        Page<TankStoreClerkAdminListResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<TankStoreClerkAdminListResponse> activateInfoResponses = dao.getAdminPageList(username,name);

        return CommonPage.copyPageInfo(page, activateInfoResponses);
    }

}
