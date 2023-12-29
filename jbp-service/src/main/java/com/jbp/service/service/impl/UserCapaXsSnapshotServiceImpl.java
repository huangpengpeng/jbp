package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.UserCapaXsSnapshot;
import com.jbp.service.dao.agent.UserCapaXsSnapshotDao;
import com.jbp.service.service.UserCapaXsSnapshotService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCapaXsSnapshotServiceImpl extends ServiceImpl<UserCapaXsSnapshotDao, UserCapaXsSnapshot> implements UserCapaXsSnapshotService {


    @Override
    public List<UserCapaXsSnapshot> getByDescription(String description) {
        return list(new QueryWrapper<UserCapaXsSnapshot>().lambda().like(UserCapaXsSnapshot::getDescription, description));
    }
}
