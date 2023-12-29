package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.UserCapa;

import java.util.List;

public interface UserCapaService extends IService<UserCapa> {

    UserCapa getByUser(Integer uid);

    UserCapa saveOrUpdateCapa(Integer uid, Long capaId, String remark, String description);

    /**
     * 往上查询满足等级的用户列表
     * @param uid 当前用户
     * @param capaIds 指定等级
     * @param num 人数
     */
    List<UserCapa> getUpperList(Integer uid, List<Long> capaIds, Integer num);

}
