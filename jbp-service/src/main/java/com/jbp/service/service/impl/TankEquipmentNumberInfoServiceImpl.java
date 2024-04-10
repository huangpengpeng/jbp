package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.tank.TankEquipmentNumberInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.EquipmentNumberInfoResponse;
import com.jbp.service.dao.TankEquipmentNumberInfoDao;
import com.jbp.service.service.TankEquipmentNumberInfoService;
import com.jbp.service.service.UserService;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class TankEquipmentNumberInfoServiceImpl  extends ServiceImpl<TankEquipmentNumberInfoDao, TankEquipmentNumberInfo> implements TankEquipmentNumberInfoService {


    @Autowired
    private UserService userService;

    @Override
    public PageInfo<EquipmentNumberInfoResponse> getPageList(String type, PageParamRequest pageParamRequest) {
        Page<EquipmentNumberInfoResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        List<TankEquipmentNumberInfo> tankEquipmentNumberInfos = list(new LambdaQueryWrapper<TankEquipmentNumberInfo>()
                .eq(StringUtils.isNotEmpty(type), TankEquipmentNumberInfo::getType, type));
        List<EquipmentNumberInfoResponse> pageResponses = tankEquipmentNumberInfos.stream().map(e -> {
            EquipmentNumberInfoResponse pageResponse = new EquipmentNumberInfoResponse();
            BeanUtils.copyProperties(e, pageResponse);
            pageResponse.setUsername(userService.getById(e.getActivateId()).getNickname());
            return pageResponse;
        }).collect(Collectors.toList());

        return CommonPage.copyPageInfo(page, pageResponses);
    }
}
