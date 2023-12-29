package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.UserCapaSnapshot;

import java.util.List;

public interface UserCapaSnapshotService extends IService<UserCapaSnapshot> {

    List<UserCapaSnapshot> getByDescription(String description);
}
