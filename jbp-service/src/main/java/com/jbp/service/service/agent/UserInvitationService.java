package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.UserInvitation;

import java.util.List;

public interface UserInvitationService extends IService<UserInvitation> {

    UserInvitation getByUser(Integer uId);

    Integer getPid(Integer uId);

    List<UserUpperDto> getAllUpper(Integer uId);

    List<UserUpperDto> getNoMountAllUpper(Integer uId);

    Boolean hasChild(Integer uId, Integer pId);

    void validBand(Integer uId, Integer pId);

    UserInvitation band(Integer uId, Integer pId, Boolean ifM, Boolean ifForce);

    /**
     * 存在上下级关系没有层级关系的明细记录列表
     * @return
     */
    List<UserInvitation> getNoFlowList();
}
