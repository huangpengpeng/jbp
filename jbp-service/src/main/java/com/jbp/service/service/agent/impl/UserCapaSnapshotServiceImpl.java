package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.UserCapaSnapshot;
import com.jbp.service.dao.agent.UserCapaSnapshotDao;
import com.jbp.service.service.agent.UserCapaSnapshotService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserCapaSnapshotServiceImpl extends ServiceImpl<UserCapaSnapshotDao, UserCapaSnapshot> implements UserCapaSnapshotService {

    @Override
    public List<UserCapaSnapshot> getByDescription(String description) {
        return list(new QueryWrapper<UserCapaSnapshot>().lambda().like(UserCapaSnapshot::getDescription, description));
    }
}
