package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ClearingUserImportDto;
import com.jbp.common.model.agent.ClearingBonusFlow;
import com.jbp.common.model.agent.ClearingUser;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ClearingPreUserRequest;

import java.math.BigDecimal;
import java.util.List;

public interface ClearingUserService extends IService<ClearingUser> {

    /**
     * 导入结算名单
     */
    Boolean importUserList(Long clearingId, List<ClearingUserImportDto> list);

    /**
     * 预设名单
     */
    Boolean preImportUser(ClearingPreUserRequest request);

    /**
     * 初始化结算名单
     */
    Boolean create(Long clearingId);

    /**
     * 批量删除
     */
    Boolean del4Clearing(Long clearingId);

    /**
     * 删除预设名单
     */
    Boolean delPerUser();

    /**
     * 获取预设名单
     */
    List<ClearingUser> getPerUserList();

    /**
     * 删除
     */
    Boolean del(Long id);


    List<ClearingUser> getByClearing(Long clearingId);


    PageInfo<ClearingUser> pageList(Integer uid, String account, Long clearingId, PageParamRequest pageParamRequest);





}
