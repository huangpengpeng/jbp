package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.request.PageParamRequest;

import java.util.List;
import java.util.Map;


public interface UserCapaXsService extends IService<UserCapaXs> {

    UserCapaXs getByUser(Integer uid);

    UserCapaXs saveOrUpdateCapa(Integer uid, Long capaXsId, Boolean ifFake, String remark, String description);

    PageInfo<UserCapaXs> pageList(Integer uid, Long capaId, PageParamRequest pageParamRequest);

    void riseCapaXs(Integer uid);

    List<UserCapaXs> getRelationUnder(Integer uid, Long capaId);

    List<UserCapaXs> getInvitationUnder(Integer uid, Long capaId);

    Map<Integer, UserCapaXs> getUidMap(List<Integer> uIdList);

    void del(Integer uid, String description, String remark);

}
