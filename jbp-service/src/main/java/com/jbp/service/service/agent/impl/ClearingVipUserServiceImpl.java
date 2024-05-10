package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ClearingVipUser;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.ClearingVipUserDao;
import com.jbp.service.service.agent.ClearingVipUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingVipUserServiceImpl extends UnifiedServiceImpl<ClearingVipUserDao, ClearingVipUser> implements ClearingVipUserService {
    @Override
    public ClearingVipUser create(Integer uid, String accountNo, Long level, String levelName,
                                  Integer commType, String commName, BigDecimal maxAmount, String rule, String description) {
        ClearingVipUser user = new ClearingVipUser(uid, accountNo, level, levelName, commType, commName, maxAmount, rule, description);
        save(user);
        return user;
    }

    @Override
    public ClearingVipUser getByUser(Integer uid, Long level, Integer commType) {
        return getOne(new LambdaQueryWrapper<ClearingVipUser>().eq(ClearingVipUser::getUid, uid).eq(ClearingVipUser::getLevel, level).eq(ClearingVipUser::getCommType, commType));
    }

    @Override
    public PageInfo<ClearingVipUser> pageList(Integer uid,Integer status,Long level,String levelName,Integer commType,PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<ClearingVipUser> queryWrapper = new LambdaQueryWrapper<ClearingVipUser>()
                .eq(!ObjectUtil.isNull(uid), ClearingVipUser::getUid, uid)
                .eq(!ObjectUtil.isNull(status), ClearingVipUser::getStatus, status)
                .eq(!ObjectUtil.isNull(level), ClearingVipUser::getLevel, level)
                .eq(!ObjectUtil.isEmpty(levelName), ClearingVipUser::getLevelName, levelName)
                .eq(!ObjectUtil.isNull(commType), ClearingVipUser::getCommType, commType)
                .orderByDesc(ClearingVipUser::getId);
        Page<ClearingVipUser> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ClearingVipUser> list = list(queryWrapper);
        return CommonPage.copyPageInfo(page, list);

    }
}
