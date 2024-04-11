package com.jbp.service.service.agent.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.dto.ClearingUserImportDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ClearingFinal;
import com.jbp.common.model.agent.ClearingUser;
import com.jbp.common.model.user.User;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.service.dao.agent.ClearingUserDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ClearingFinalService;
import com.jbp.service.service.agent.ClearingUserService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingUserServiceImpl extends UnifiedServiceImpl<ClearingUserDao, ClearingUser> implements ClearingUserService {

    @Resource
    private ClearingUserDao clearingUserDao;
    @Resource
    private UserService userService;
    @Resource
    private ClearingFinalService clearingFinalService;

    @Override
    public Boolean importUserList(Long clearingId, List<ClearingUserImportDto> list) {
        // 检查名单
        if (clearingId == null) {
            throw new CrmebException("结算信息不能为空");
        }
        ClearingFinal clearingFinal = clearingFinalService.getById(clearingId);
        if (clearingFinal == null || !clearingFinal.getStatus().equals(ClearingFinal.Constants.待结算.name())) {
            throw new CrmebException("名单只能在待结算状态下导入");
        }
        if (CollectionUtils.isEmpty(list)) {
            throw new CrmebException("名单不能为空");
        }
        // 批量插入用户
        List<ClearingUser> insetBatchList = Lists.newArrayList();
        Map<String, ClearingUserImportDto> userMap = Maps.newConcurrentMap();
        for (ClearingUserImportDto dto : list) {
            if (StringUtils.isAnyEmpty(dto.getAccount(), dto.getLevelName()) || dto.getLevel() == null) {
                throw new CrmebException("表格不能有空格");
            }
            if (userMap.get(dto.getAccount()) != null) {
                throw new CrmebException(dto.getAccount() + "重复");
            }
            User user = userService.getByAccount(dto.getAccount());
            if (user == null) {
                throw new CrmebException(dto.getAccount() + "账号不存在");
            }
            ClearingUser clearingUser = ClearingUser.builder().clearingId(clearingId).uid(user.getId()).build();
            insetBatchList.add(clearingUser);
        }
        List<List<ClearingUser>> partition = Lists.partition(insetBatchList, 500);
        for (List<ClearingUser> clearingUsers : partition) {
            clearingUserDao.insertBatch(clearingUsers);
        }
        return true;
    }

    @Override
    public Boolean init(Long clearingId) {
        return null;
    }

    @Override
    public BigDecimal progress(Long clearingId) {
        return null;
    }

    @Override
    public Boolean del4Clearing(Long clearingId) {
        return null;
    }

    @Override
    public Boolean del(Long id) {
        return null;
    }

    @Override
    public Boolean add(String account) {
        return null;
    }

    @Override
    public Boolean edit(Long id, Long capaId, Long capaXsId) {
        return null;
    }
}
