package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.tank.TankStoreRelation;
import com.jbp.common.response.TankStoreRelationListResponse;
import com.jbp.service.dao.TankStoreRelationDao;
import com.jbp.service.service.TankStoreRelationService;
import com.jbp.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Service
public class TankStoreRelationServiceImpl extends ServiceImpl<TankStoreRelationDao, TankStoreRelation> implements TankStoreRelationService {

    @Resource
    private TankStoreRelationDao dao;
    @Resource
    private UserService userService;


    @Override
    public  TankStoreRelation getStoreUserId(Long userId){
        return  dao.selectOne(new QueryWrapper<TankStoreRelation>().lambda().eq(TankStoreRelation::getStoreUserId,userId));
    }

    @Override
    public List<TankStoreRelation> getTankUserId(Integer userId) {
        return  dao.selectList(new QueryWrapper<TankStoreRelation>().lambda().eq(TankStoreRelation::getTankUserId,userId));
    }

    @Override
    public List<TankStoreRelationListResponse> getRelationList() {
        return dao.getRelationList(userService.getInfo().getId());
    }


}
