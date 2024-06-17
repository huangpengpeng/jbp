package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserRelationFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.UserRelationGplotVo;
import java.util.List;
import java.util.Map;

public interface UserRelationFlowService extends IService<UserRelationFlow> {

    void clear(Integer uid);

    void refresh(Integer uId);

    PageInfo<UserRelationFlow> pageList(Integer uid, Integer pid, Integer level, PageParamRequest pageParamRequest);

    UserRelationGplotVo gplot(Integer uid,Integer uid0,Integer uid1,Map<Integer, User> uidMapList, Map<Integer, UserCapa> uidCapaMap);

    UserRelationGplotVo gplotInfo(Integer uid);

    List<Integer> selectByCapa(Integer uId);
}
