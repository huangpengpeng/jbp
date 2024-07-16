package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserCapaSnapshot;
import com.jbp.common.model.agent.UserCapaXsSnapshot;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

public interface UserCapaXsSnapshotService extends IService<UserCapaXsSnapshot> {

    List<UserCapaXsSnapshot> getByDescription(String description);

    PageInfo<UserCapaXsSnapshot> pageList(Integer uid, Long capaId, String type, PageParamRequest pageParamRequest);

    String export(Integer uid, Long capaId, String type);
}
