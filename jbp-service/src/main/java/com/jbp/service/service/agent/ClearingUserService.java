package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.dto.ClearingUserImportDto;
import com.jbp.common.model.agent.ClearingUser;

import java.math.BigDecimal;
import java.util.List;

public interface ClearingUserService extends IService<ClearingUser> {

    /**
     * 导入结算名单
     */
    Boolean importUserList(Long clearingId, List<ClearingUserImportDto> list);

    /**
     * 初始化结算名单
     */
    Boolean init(Long clearingId);

    /**
     * 查询执行进度
     */
    BigDecimal progress(Long clearingId);

    /**
     * 批量删除
     */
    Boolean del4Clearing(Long clearingId);

    /**
     * 删除
     */
    Boolean del(Long id);

    /**
     * 新增
     */
    Boolean add(String account);

    /**
     * 编辑
     */
    Boolean edit(Long id, Long capaId, Long capaXsId);

}