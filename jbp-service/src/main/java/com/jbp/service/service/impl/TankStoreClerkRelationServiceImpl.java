package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.tank.TankStoreClerkRelation;
import com.jbp.service.dao.TankStoreClerkRelationDao;
import com.jbp.service.service.TankStoreClerkRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Service
public class TankStoreClerkRelationServiceImpl extends ServiceImpl<TankStoreClerkRelationDao, TankStoreClerkRelation> implements TankStoreClerkRelationService {

    @Resource
    private TankStoreClerkRelationDao dao;


    @Override
    public TankStoreClerkRelation getClerkUserId(Long clerkUserId){
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

}
