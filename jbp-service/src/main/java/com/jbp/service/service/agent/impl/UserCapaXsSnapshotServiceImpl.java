package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.UserCapaXsSnapshot;
import com.jbp.service.dao.agent.UserCapaXsSnapshotDao;
import com.jbp.service.service.agent.UserCapaXsSnapshotService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserCapaXsSnapshotServiceImpl extends ServiceImpl<UserCapaXsSnapshotDao, UserCapaXsSnapshot> implements UserCapaXsSnapshotService {


    @Override
    public List<UserCapaXsSnapshot> getByDescription(String description) {
        return list(new QueryWrapper<UserCapaXsSnapshot>().lambda().like(UserCapaXsSnapshot::getDescription, description));
    }
}
