package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.jbp.common.model.user.UserVitalitpartner;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;


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
    private Environment environment;
    @Autowired
    private OrderService orderService;


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
    public CommonResult<String> getShopImage() {

        String repetition =  systemConfigService.getValueByKey("user_shop_image");
        if(StringUtils.isBlank(repetition) || repetition.equals("'0'") ){
            return CommonResult.success();
        }
//        //店补
//        https://fnyhdf.oss-cn-shenzhen.aliyuncs.com/319940525c414d5e95542616404c0013
//        //区域
//        https://fnyhdf.oss-cn-shenzhen.aliyuncs.com/85b9f50620c84249972809e0cd7b4e48
//        //市
//        https://fnyhdf.oss-cn-shenzhen.aliyuncs.com/e9ff7c1ca4694b14905088f4a36f634b

        UserVitalitpartner userVitalitpartner =   userVitalitpartnerService.getOne(new QueryWrapper<UserVitalitpartner>().lambda().eq(UserVitalitpartner::getUserId, userService.getUserId()).eq(UserVitalitpartner::getEnable,true));
        return CommonResult.success(userVitalitpartner == null?"" : "https://batchatx.oss-cn-shenzhen.aliyuncs.com/2b908fbe27404ddd89348385f2af8a65");
    }



}



