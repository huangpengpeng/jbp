package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.UserCapaXs;


public interface UserCapaXsService extends IService<UserCapaXs> {

    UserCapaXs getByUser(Integer uid);

    UserCapaXs saveOrUpdateCapa(Integer uid, Long capaXsId, String remark, String description);

}
