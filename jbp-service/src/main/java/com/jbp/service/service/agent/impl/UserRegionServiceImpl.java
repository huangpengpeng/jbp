package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserRegion;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.UserRegionMapper;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserRegionService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class UserRegionServiceImpl extends ServiceImpl<UserRegionMapper, UserRegion> implements UserRegionService {

    @Resource
    private UserRegionMapper mapper;
    @Resource
    private UserService userService;

    @Override
    public UserRegion getByUser(Long uid) {
        return getOne(new QueryWrapper<UserRegion>().lambda().eq(UserRegion::getUid, uid));
    }

    @Override
    public UserRegion getByArea(String province, String city, String area, String status) {
        return getOne(new QueryWrapper<UserRegion>().lambda()
                .eq(UserRegion::getProvince, province).eq(UserRegion::getCity, city)
                .eq(UserRegion::getArea, area).eq(UserRegion::getStatus, status));
    }

    @Override
    public PageInfo<UserRegion> pageList(Integer uid, String province, String city, String area, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserRegion> userRegionLambdaQueryWrapper = new LambdaQueryWrapper<UserRegion>()
                .eq(!ObjectUtil.isNull(uid), UserRegion::getUid, uid)
                .eq(!ObjectUtil.isNull(province) && !province.equals(""), UserRegion::getProvince, province)
                .eq(!ObjectUtil.isNull(city) && !city.equals(""), UserRegion::getCity, city)
                .eq(!ObjectUtil.isNull(area) && !area.equals(""), UserRegion::getArea, area)
                .orderByDesc(UserRegion::getUid);
        Page<UserRegion> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserRegion> list = list(userRegionLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(UserRegion::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }


}
