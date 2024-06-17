package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.UserOfflineSubsidy;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserVitalitpartner;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.response.UserVitalitpartnerResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.UserOfflineSubsidyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("api/front/userVitalitpartner")
@Api(tags = "元气合伙人")
public class UserVitalitpartnerController {

    @Autowired
    private UserVitalitpartnerService userVitalitpartnerService;
    @Autowired
    private UserService userService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserOfflineSubsidyService userOfflineSubsidyService;
    @Autowired
    private WhiteUserService whiteUserService;


    @ApiOperation(value = "元气合伙人图标")
    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    public CommonResult<String> getList( ) {

        String repetition =  systemConfigService.getValueByKey("goods_partner");
        if(StringUtils.isBlank(repetition) ||repetition.equals("'0'")){
            return CommonResult.success();
        }
        UserVitalitpartner userVitalitpartner =   userVitalitpartnerService.getOne(new QueryWrapper<UserVitalitpartner>().lambda().eq(UserVitalitpartner::getUserId, userService.getUserId()).eq(UserVitalitpartner::getEnable,true));
        return CommonResult.success(userVitalitpartner == null?"" : "https://batchatx.oss-cn-shenzhen.aliyuncs.com/2b908fbe27404ddd89348385f2af8a65");
    }

    @ApiOperation(value = "复销奖图标")
    @RequestMapping(value = "/getResellUser", method = RequestMethod.GET)
    public CommonResult<String> getResellUser( ) {

        String repetition =  systemConfigService.getValueByKey("goods_repetition");
        if(StringUtils.isBlank(repetition) ||repetition.equals("'0'")){
            return CommonResult.success("");
        }
        String goods_repetition_id_qua =  systemConfigService.getValueByKey("goods_repetition_id_qua");
        BigDecimal salse = orderService.getGoodsPrice(goods_repetition_id_qua,userService.getUserId(), DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
        if(salse.compareTo(new BigDecimal(199)) >= 0){
            return CommonResult.success("https://batchatx.oss-cn-shenzhen.aliyuncs.com/f04bbf30c1a647ddbcbda733dab9bf9b");
        }else{
            return CommonResult.success("https://batchatx.oss-cn-shenzhen.aliyuncs.com/a63031bbc6e74798991683d9e52c7799");
        }

    }


    @ApiOperation(value = "店铺图标区域图标")
    @RequestMapping(value = "/getShopImage", method = RequestMethod.GET)
    public CommonResult<UserVitalitpartnerResponse> getShopImage() {

        String repetition = systemConfigService.getValueByKey("user_shop_image");
        if (StringUtils.isBlank(repetition) || repetition.equals("'0'")) {
            return CommonResult.success();
        }

        List<String> list = new ArrayList<>();
        UserVitalitpartnerResponse response = new UserVitalitpartnerResponse();
        User user = userService.getInfo();
        if (user.getOpenShop()) {
            list.add("https://fnyhdf.oss-cn-shenzhen.aliyuncs.com/319940525c414d5e95542616404c0013");
        }
        UserOfflineSubsidy userOfflineSubsidy = userOfflineSubsidyService.getOne(new QueryWrapper<UserOfflineSubsidy>().lambda().eq(UserOfflineSubsidy::getStatus, "已开通").eq(UserOfflineSubsidy::getArea, "").eq(UserOfflineSubsidy::getUid, user.getId()));
        if (userOfflineSubsidy != null) {
            list.add("https://fnyhdf.oss-cn-shenzhen.aliyuncs.com/e9ff7c1ca4694b14905088f4a36f634b");
            response.setCity(userOfflineSubsidy.getCity());
        }
        UserOfflineSubsidy userOfflineSubsidy2 = userOfflineSubsidyService.getOne(new QueryWrapper<UserOfflineSubsidy>().lambda().eq(UserOfflineSubsidy::getStatus, "已开通").ne(UserOfflineSubsidy::getArea, "").eq(UserOfflineSubsidy::getUid, user.getId()));
        if (userOfflineSubsidy2 != null) {
            list.add("https://fnyhdf.oss-cn-shenzhen.aliyuncs.com/85b9f50620c84249972809e0cd7b4e48");
            response.setCity(userOfflineSubsidy2.getCity());
            response.setArea(userOfflineSubsidy2.getArea());
        }

        WhiteUser whiteUser = whiteUserService.getOne(new QueryWrapper<WhiteUser>().lambda().eq(WhiteUser::getUid, user.getId())
                .apply("white_id=(select id from eb_white where name='合伙人')"));
        if (whiteUser != null) {
            list.add("crmebimage/public/content/2024/06/14/d18da5fd0790431d805022f9811e2965wf5wsvv3fv.png");
        }
        response.setList(list);
        return CommonResult.success(response);
    }
}



