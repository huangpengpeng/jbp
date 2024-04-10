package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.tank.TankStore;
import com.jbp.service.dao.TankStoreDao;
import com.jbp.service.service.TankStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Service
public class TankStoreServiceImpl extends ServiceImpl<TankStoreDao, TankStore> implements TankStoreService {

    @Resource
    private TankStoreDao dao;

    @Override
    public TankStore getName(String name) {
        return  dao.selectOne(new QueryWrapper<TankStore>().lambda().eq(TankStore::getName,name));
    }

    @Override
    public List<TankStore> getStoreUserId(Long userId) {
        return  dao.selectList(new QueryWrapper<TankStore>().lambda().eq(TankStore::getUserId,userId));
    }




}
