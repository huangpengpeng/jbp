package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.user.User;
import com.jbp.common.request.PageParamRequest;

import java.util.List;
import java.util.Map;

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

    List<UserCapa> getInvitationUnder(Integer uid, Long capaId);

    PageInfo<UserCapa> pageList(Integer uid, Long capaId, String phone, PageParamRequest pageParamRequest);

    void riseCapa(Integer uid);

    void  asyncRiseCapa(Integer uid);

    Map<Integer, UserCapa> getUidMap(List<Integer> uIdList);
}
