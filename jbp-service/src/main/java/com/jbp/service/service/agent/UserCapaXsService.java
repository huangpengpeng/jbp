package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.request.PageParamRequest;

import java.util.List;


public interface UserCapaXsService extends IService<UserCapaXs> {

    UserCapaXs getByUser(Integer uid);

    UserCapaXs saveOrUpdateCapa(Integer uid, Long capaXsId, String remark, String description);

    PageInfo<UserCapaXs> pageList(Integer uid, Long capaId, PageParamRequest pageParamRequest);
}
