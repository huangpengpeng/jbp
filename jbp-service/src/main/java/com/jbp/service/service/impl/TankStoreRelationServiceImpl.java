package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.tank.TankStoreRelation;
import com.jbp.service.dao.TankStoreRelationDao;
import com.jbp.service.service.TankStoreRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Service
public class TankStoreRelationServiceImpl extends ServiceImpl<TankStoreRelationDao, TankStoreRelation> implements TankStoreRelationService {

    @Resource
    private TankStoreRelationDao dao;



    @Override
    public  TankStoreRelation getStoreUserId(Long userId){
        return  dao.selectOne(new QueryWrapper<TankStoreRelation>().lambda().eq(TankStoreRelation::getStoreUserId,userId));
    }

    @Override
    public List<TankStoreRelation> getTankUserId(Long userId) {
        return  dao.selectList(new QueryWrapper<TankStoreRelation>().lambda().eq(TankStoreRelation::getTankUserId,userId));
    }




}
