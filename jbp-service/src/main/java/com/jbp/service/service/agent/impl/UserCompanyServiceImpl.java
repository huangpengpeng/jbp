package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserCapaSnapshot;
import com.jbp.common.model.agent.UserCompany;
import com.jbp.common.model.agent.UserRegion;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.UserCompanyMapper;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class UserCompanyServiceImpl extends ServiceImpl<UserCompanyMapper, UserCompany> implements UserCompanyService {

    @Resource
    private UserCompanyMapper mapper;
    @Resource
    private UserService userService;

    @Override
    public UserCompany getByCity(String province, String city) {
        return getOne(new QueryWrapper<UserCompany>().lambda().eq(UserCompany::getProvince, province).eq(UserCompany::getCity, city));
    }

    @Override
    public PageInfo<UserCompany> pageList(Integer uid, String province, String city, String area, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserCompany> userRegionLambdaQueryWrapper = new LambdaQueryWrapper<UserCompany>()
                .eq(!ObjectUtil.isNull(uid), UserCompany::getUid, uid)
                .eq(!ObjectUtil.isNull(province) && !province.equals(""), UserCompany::getProvince, province)
                .eq(!ObjectUtil.isNull(city) && !city.equals(""), UserCompany::getCity, city)
                .eq(!ObjectUtil.isNull(area) && !area.equals(""), UserCompany::getArea, area)
                .orderByDesc(UserCompany::getUid);

        Page<UserCompany> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserCompany> list = list(userRegionLambdaQueryWrapper);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
