package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.TeamItem;
import com.jbp.common.request.PageParamRequest;

public interface TeamItemService extends IService<TeamItem> {

    PageInfo<TeamItem> pageList(Integer tid, String name, PageParamRequest pageParamRequest);

    void add(Integer tid, String name);

    void edit(Integer id, Integer tid, String name);
}
