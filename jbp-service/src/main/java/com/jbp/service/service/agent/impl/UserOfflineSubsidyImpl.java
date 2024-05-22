package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserOfflineSubsidy;
import com.jbp.common.model.agent.UserRegion;
import com.jbp.common.model.city.CityRegion;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserOfflineSubsidyAddRequest;
import com.jbp.common.request.agent.UserOfflineSubsidyEditRequest;
import com.jbp.common.utils.AddressUtil;
import com.jbp.service.dao.agent.UserOfflineSubsidyDao;
import com.jbp.service.service.CityRegionService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserOfflineSubsidyService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class UserOfflineSubsidyImpl extends ServiceImpl<UserOfflineSubsidyDao, UserOfflineSubsidy> implements UserOfflineSubsidyService{

    @Autowired
    private UserService userService;
    @Autowired
    private CityRegionService cityRegionService;

    @Override
    public PageInfo<UserOfflineSubsidy> pageList(Integer uid, String province, String city, String area, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserOfflineSubsidy> lqw = new LambdaQueryWrapper<UserOfflineSubsidy>()
                .eq(!ObjectUtil.isNull(uid), UserOfflineSubsidy::getUid, uid)
                .eq(!ObjectUtil.isNull(province) && !province.equals(""), UserOfflineSubsidy::getProvince, province)
                .eq(!ObjectUtil.isNull(city) && !city.equals(""), UserOfflineSubsidy::getCity, city)
                .eq(!ObjectUtil.isNull(area) && !area.equals(""), UserOfflineSubsidy::getArea, area)
                .orderByDesc(UserOfflineSubsidy::getUid);
        Page<UserRegion> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserOfflineSubsidy> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(UserOfflineSubsidy::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
            e.setNickname(user != null ? user.getNickname() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Boolean add(UserOfflineSubsidyAddRequest request) {

        if (StringUtils.isBlank(request.getAccount()) || request.getProvinceId() == null || request.getCityId() == null) {
            throw new CrmebException("请填写完整信息");
        }
        User user = userService.getByAccount(request.getAccount());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("账户不存在");
        }
        CityRegion province = cityRegionService.getByRegionId(request.getProvinceId());
        CityRegion city = cityRegionService.getByRegionId(request.getCityId());
        CityRegion area = new CityRegion();
        if (request.getAreaId() != null) {
            area = cityRegionService.getByRegionId(request.getAreaId());
        }
            List<UserOfflineSubsidy> list = this.list(new QueryWrapper<UserOfflineSubsidy>().eq("uid", user.getId()).eq("status", "已开通"));
            if (!CollectionUtils.isEmpty(list)) {
                throw new CrmebException("该用户已有开通区域");
            }
            UserOfflineSubsidy userOfflineSubsidy = getByArea(province.getRegionName(), city.getRegionName(),
                    request.getAreaId() != null ? area.getRegionName() : "",
                    UserOfflineSubsidy.Constants.已开通.toString());
            if (!ObjectUtil.isNull(userOfflineSubsidy)) {
                throw new CrmebException("该区域已经被其他用户开通");
            }
            userOfflineSubsidy = UserOfflineSubsidy.builder().uid(user.getId()).provinceId(request.getProvinceId()).
                    province(province.getRegionName()).city(city.getRegionName()).
                    cityId(request.getCityId()).status(UserRegion.Constants.已开通.toString()).build();
            userOfflineSubsidy.setArea(request.getAreaId() != null ? area.getRegionName() : "");
            userOfflineSubsidy.setAreaId(request.getAreaId() != null ? request.getAreaId() : 0);

            this.save(userOfflineSubsidy);
            return true;
    }

    @Override
    public Boolean edit(UserOfflineSubsidyEditRequest request) {
        if (StringUtils.isBlank(request.getStatus()) || request.getProvinceId() == null || request.getCityId() == null){
            throw new CrmebException("请填写完整信息");
        }
        CityRegion province = cityRegionService.getByRegionId(request.getProvinceId());
        CityRegion city = cityRegionService.getByRegionId(request.getCityId());
        CityRegion area = new CityRegion();
        if (request.getAreaId() != null) {
            area = cityRegionService.getByRegionId(request.getAreaId());
        }
        if (request.getStatus().equals(UserOfflineSubsidy.Constants.已开通.toString())){
            UserOfflineSubsidy userOfflineSubsidy = getByArea(province.getRegionName(), city.getRegionName(),
                    request.getAreaId() != null ? area.getRegionName() : "",
                    UserOfflineSubsidy.Constants.已开通.toString());
            if (!ObjectUtil.isNull(userOfflineSubsidy)) {
                throw new CrmebException("该区域已经被其他用户开通");
            }
        }
        UserOfflineSubsidy userOfflineSubsidy = this.getOne(new QueryWrapper<UserOfflineSubsidy>().lambda().eq(UserOfflineSubsidy::getId, request.getId()));
        userOfflineSubsidy.setProvinceId(request.getProvinceId());
        userOfflineSubsidy.setProvince(province.getRegionName());
        userOfflineSubsidy.setCityId(request.getCityId());
        userOfflineSubsidy.setCity(city.getRegionName());
        userOfflineSubsidy.setAreaId(request.getAreaId() != null ? request.getAreaId() : 0);
        userOfflineSubsidy.setArea(request.getAreaId() != null ? area.getRegionName() : "");
        userOfflineSubsidy.setStatus(request.getStatus());
        return updateById(userOfflineSubsidy);
    }
    @Override
    public UserOfflineSubsidy getByArea(String province, String city, String area, String status) {
        return getOne(new QueryWrapper<UserOfflineSubsidy>().lambda()
                .eq(UserOfflineSubsidy::getProvince, province).eq(UserOfflineSubsidy::getCity, city)
                .eq(UserOfflineSubsidy::getArea, area).eq(UserOfflineSubsidy::getStatus, status));
    }
}
