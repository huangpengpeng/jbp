package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.jbp.common.model.user.UserVitalitpartner;
import com.jbp.common.result.CommonResult;
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
        if(repetition.equals("0")){
            return CommonResult.success();
        }


        UserVitalitpartner userVitalitpartner =   userVitalitpartnerService.getOne(new QueryWrapper<UserVitalitpartner>().lambda().eq(UserVitalitpartner::getUserId, userService.getUserId()).eq(UserVitalitpartner::getEnable,true));

      return CommonResult.success(userVitalitpartner == null?"" : "https://batchatx.oss-cn-shenzhen.aliyuncs.com/2b908fbe27404ddd89348385f2af8a65");
    }





    @ApiOperation(value = "复销奖图标")
    @RequestMapping(value = "/getResellUser", method = RequestMethod.GET)
    public CommonResult<String> getResellUser( ) {


        String repetition =  systemConfigService.getValueByKey("goods_repetition");

        if(repetition.equals("0")){
            return CommonResult.success("");
        }

        String repetitionId =  systemConfigService.getValueByKey("goods_repetition_id");
        StringBuilder stringBuilder =new StringBuilder();

        String name =environment.getProperty("historyOrder.name");
        if(name.equals("jymall")){
             stringBuilder = new StringBuilder(" SELECT IFNULL(SUM(o.`payPrice`),0) as c FROM " + name + ".orders AS o\n" +
                    "        WHERE  o.`payTime` IS NOT NULL\n" +
                    "        and o.`status` IN ( 201,301,401,402,501 )\n" +
                    "        AND o.platform in('商城', '订货')\n" +
                    "        AND o.id IN (\n" +
                    "                SELECT g.orderId FROM " + name + ".ordergoods AS g WHERE 1=1  AND ( g.goodsId IN(190,207,228,237,276,279,280,2010,2016,2028,2032,2035,2044,2054,2059,2061,2062,2063,2064,2065,2066,2068,2069,2070,2071,2073,2074,2077,2079,2080,2081,2089,2090,2095,2096,2085,2097,2098,2100,2103,2114,2116,2117,2118,2119,2120,2124,2125) OR g.`refGoodsId` IN (190,207,228,237,276,279,280,2010,2016,2028,2032,2035,2044,2054,2059,2061,2062,2063,2064,2065,2066,2068,2069,2070,2071,2073,2074,2077,2079,2080,2081,2089,2090,2095,2096,2085,2097,2098,2100,2103,2114,2116,2117,2118,2119,2120,2124,2125) )\n" +
                    "\t\t)\n" +
                    "        and   DATE_FORMAT( o.`payTime`,'%Y-%m') = DATE_FORMAT(now(),'%Y-%m')\n" +
                    "        and o.userId  = '" + userService.getUserId() + "'"         );
        }else {
             stringBuilder = new StringBuilder(" SELECT IFNULL(SUM(o.`payPrice`),0) as c FROM " + name + ".orders AS o\n" +
                    "        WHERE  o.`payTime` IS NOT NULL\n" +
                    "        and o.`status` IN ( 201,301,401,402,501 )\n" +
                    "        AND o.platform in('商城', '订货')\n" +
                    "        AND o.id IN (\n" +
                    "                SELECT g.orderId FROM " + name + ".ordergoods AS g WHERE 1=1  AND ( g.goodsId IN(190,207,228,236,237,276,279,280,316,322,332,336,339,350,365,368,371,372,373,374,375,376,378,379,380,381,382,385,386,394,395,397,398,399,407,408,415,418,403,421,422,424,429,436,444,446,447,449,450,454,455) OR g.`refGoodsId` IN (190,207,228,236,237,276,279,280,316,322,332,336,339,350,365,368,371,372,373,374,375,376,378,379,380,381,382,385,386,394,395,397,398,399,407,408,415,418,403,421,422,424,429,436,444,446,447,449,450,454,455) )\n" +
                    "\t\t)\n" +
                    "        and   DATE_FORMAT( o.`payTime`,'%Y-%m') = DATE_FORMAT(now(),'%Y-%m')\n" +
                    "        and o.userId  = '" + userService.getUserId() + "'"
            );
        }


        Map<String, Object> maps = SqlRunner.db().selectOne(stringBuilder.toString());

        BigDecimal salse = new BigDecimal(orderService.getGoodsPrice(repetitionId));

        if((new BigDecimal(maps.get("c").toString()).add(salse)).compareTo(new BigDecimal(199)) == 1){
            return CommonResult.success("https://batchatx.oss-cn-shenzhen.aliyuncs.com/f04bbf30c1a647ddbcbda733dab9bf9b");
        }else{
            return CommonResult.success("https://batchatx.oss-cn-shenzhen.aliyuncs.com/a63031bbc6e74798991683d9e52c7799");
        }





    }



}



