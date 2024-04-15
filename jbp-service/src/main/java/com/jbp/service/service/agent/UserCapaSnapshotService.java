package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.PlatformWallet;
import com.jbp.common.model.agent.UserCapaSnapshot;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

public interface UserCapaSnapshotService extends IService<UserCapaSnapshot> {

    List<UserCapaSnapshot> getByDescription(String description);

    PageInfo<UserCapaSnapshot> pageList(Integer uid, Long capaId, String type, PageParamRequest pageParamRequest);

    UserCapaSnapshot getByFirst(Integer uid, Long capaId);
}
