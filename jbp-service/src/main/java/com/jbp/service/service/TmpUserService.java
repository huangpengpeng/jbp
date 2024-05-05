package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.user.TmpUser;

public interface TmpUserService extends IService<TmpUser> {

    void create();

    void repairMobile();

    TmpUser getByOrgId(Integer orgId);


}
