package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.UserCapaSnapshot;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.UserCapaSnapshotDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.UserCapaSnapshotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserCapaSnapshotServiceImpl extends ServiceImpl<UserCapaSnapshotDao, UserCapaSnapshot> implements UserCapaSnapshotService {
    @Resource
    UserService userService;
    @Resource
    CapaService capaService;


    @Override
    public List<UserCapaSnapshot> getByDescription(String description) {
        return list(new QueryWrapper<UserCapaSnapshot>().lambda().like(UserCapaSnapshot::getDescription, description));
    }

    @Override
    public PageInfo<UserCapaSnapshot> pageList(Integer uid, Long capaId, String type, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserCapaSnapshot> userCapaSnapshotLambdaQueryWrapper = new LambdaQueryWrapper<UserCapaSnapshot>()
                .eq(!ObjectUtil.isNull(uid), UserCapaSnapshot::getUid, uid)
                .eq(!ObjectUtil.isNull(capaId), UserCapaSnapshot::getCapaId, capaId)
                .eq(!ObjectUtil.isNull(type) && !type.equals(""), UserCapaSnapshot::getType, type);
        Page<UserCapaSnapshot> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserCapaSnapshot> list = list(userCapaSnapshotLambdaQueryWrapper);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
            Capa capa = capaService.getById(e.getCapaId());
            e.setCapaName(capa.getName());
            e.setCapaUrl(capa.getIconUrl());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}