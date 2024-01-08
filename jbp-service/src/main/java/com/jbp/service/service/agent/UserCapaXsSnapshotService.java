package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.UserCapaXsSnapshot;

import java.util.List;

public interface UserCapaXsSnapshotService extends IService<UserCapaXsSnapshot> {

    List<UserCapaXsSnapshot> getByDescription(String description);
}
