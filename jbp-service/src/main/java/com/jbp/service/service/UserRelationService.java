package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.UserRelation;

import java.util.List;

public interface UserRelationService extends IService<UserRelation> {

    UserRelation getByUid(Integer uId);

    Integer getPid(Integer uId);

    List<UserRelation> getByPid(Integer pId);

    UserRelation getByPid(Integer pId, Integer node);

    List<UserUpperDto> getAllUpper(Integer uId);

    Boolean hasChild(Integer uId, Integer pId);

    void validBand(Integer uId, Integer pId, Integer operateId, Integer node);

    UserRelation band(Integer uId, Integer pId, Integer operateId, Integer node);

    /**
     * 存在服务级关系没有层级关系的明细记录列表
     */
    List<UserRelation> getNoFlowList();
}
