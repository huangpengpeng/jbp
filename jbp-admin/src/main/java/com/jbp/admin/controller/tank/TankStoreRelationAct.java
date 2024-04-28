package com.jbp.admin.controller.tank;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.order.ThreeBackOneInit;
import com.jbp.common.model.tank.TankStoreRelation;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.TankStoreRelationAdminListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankStoreRelationService;
import com.jbp.service.service.ThreeBackOneInitService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserInvitationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/admin/tank/tankStoreRelation")
@Api(tags = "店主管理")
public class TankStoreRelationAct {


    @Resource
    private TankStoreRelationService tankStoreRelationService;
    @Resource
    private UserService userService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private ThreeBackOneInitService threeBackOneInitService;


    @PreAuthorize("hasAuthority('tank:tankStoreRelation:list')")
    @ApiOperation(value = "店主管理", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<TankStoreRelationAdminListResponse>> list(String username, String storeusername, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(tankStoreRelationService.getAdminPageList(username, storeusername, pageParamRequest)));

    }


    @PreAuthorize("hasAuthority('tank:tankStoreRelation:save')")
    @ApiOperation(value = "增加舱主", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult save(Long userId, Long storeUserId) {


        TankStoreRelation tankStoreRelation = tankStoreRelationService.getStoreUserId(storeUserId);
        if (tankStoreRelation != null) {
            throw new RuntimeException("店主已经被绑定，增加失败");
        }
        TankStoreRelation tankStoreRelation2 = new TankStoreRelation();
        tankStoreRelation2.setTankUserId(userId);
        tankStoreRelation2.setStoreUserId(storeUserId);
        tankStoreRelation2.setCreatedTime(new Date());

        tankStoreRelationService.save(tankStoreRelation2);

        return CommonResult.success();
    }


    @PreAuthorize("hasAuthority('tank:tankStoreRelation:delete')")
    @ApiOperation(value = "删除舱主店主", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult delete(HttpServletRequest request, Long id) {

        tankStoreRelationService.removeById(id);

        return CommonResult.success();
    }


    public void aa_3968() {

        String name = "jymall";

        List<User> userList = userService.list(new QueryWrapper<>());
        List<ThreeBackOneInit> list = new ArrayList<>();
        int i = 0;
        for (User user : userList) {

            StringBuilder stringBuilder = new StringBuilder("SELECT count(1) + IFNULL(u2.ext,0) as c FROM "+name+".orders AS o \n" +
                    "\tLEFT JOIN "+name+".USER AS u ON u.`id`=o.`userId`\n" +
                    "  LEFT JOIN "+name+".USER AS u2 ON u2.id=u.parentId \n" +
                    "\tWHERE u.`parentId`=" +user.getId() + " AND (o.`orderCapacityId`IN (23,24,28) OR o.`riseCapacityId` IN(23,24,28))\n" +
                    "\t\t\tAND o.`status` IN (201,301,401,402,501,285) AND o.`payTime` <='2024-05-01'  and ( o.shareCapaId >22 || o.shareCapaId is null )\n" +
                    "and (o.commissionModel !='兑换无佣金' or o.commissionModel is null)\n" +
                    "\t\t\tAND o.id IN (\n" +
                    "\t\t\t\tSELECT og.orderId FROM "+name+".ordergoods AS og WHERE og.goodsId IN(156,191,196,239,241,2005,285,287,2017,2024,2026,2055,2067,2102)\n" +
                    "\t\t\t)\n" +
                    "\t"
            );

            StringBuilder stringBuilder2 = new StringBuilder("\t\tSELECT IFNULL(SUM(ogi.count),0) AS c1 FROM "+name+".ordergoodinit AS ogi LEFT JOIN USER AS ug ON ug.id=ogi.userId\n" +
                    "\t\tWHERE ug.parentId=" +user.getId() + " AND ogi.goodsId IN (156,191,196,239,241,2005,285,287,2017,2024,2026,2055,2067,2102)"
            );

            Map<String, Object> maps = SqlRunner.db().selectOne(stringBuilder.toString());
            Map<String, Object> maps2 = SqlRunner.db().selectOne(stringBuilder2.toString());


            if((Double.valueOf(String.valueOf(maps.get("c"))).intValue() + Double.valueOf(String.valueOf(maps2.get("c1"))).intValue()) >0){
                ThreeBackOneInit threeBackOneInit = new ThreeBackOneInit();
                threeBackOneInit.setUid(user.getId());
                threeBackOneInit.setAmt(new BigDecimal(3968));
                threeBackOneInit.setNumber(Double.valueOf(String.valueOf(maps.get("c"))).intValue() + Double.valueOf(String.valueOf(maps2.get("c1"))).intValue());
                list.add(threeBackOneInit);
            }

            i++;
            log.info("3968"+i);
        }

        threeBackOneInitService.saveBatch(list);

    }




    public void aa_15866() {

        String name = "jymall";
    int i = 0;
        List<User> userList = userService.list(new QueryWrapper<>());
        List<ThreeBackOneInit> list = new ArrayList<>();
        for (User user : userList) {


            StringBuilder stringBuilder = new StringBuilder("SELECT count(1) + IFNULL(u2.ext,0) as c FROM "+name+".orders AS o \n" +
                    "\tLEFT JOIN "+name+".USER AS u ON u.`id`=o.`userId`\n" +
                    "  LEFT JOIN "+name+".USER AS u2 ON u2.id=u.parentId \n" +
                    "\tWHERE u.`parentId`=" +user.getId() + " AND (o.`orderCapacityId`IN (24) OR o.`riseCapacityId` IN(24))\n" +
                    "\t\t\tAND o.`status` IN (201,301,401,402,501) AND o.`payTime` <='2024-05-01'  and ( o.shareCapaId >22 || o.shareCapaId is null )\n" +
                    "\t\t\tAND o.id IN (\n" +
                    "\t\t\t\tSELECT og.orderId FROM "+name+".ordergoods AS og WHERE og.goodsId IN(274,275,286,2004,2018,2021,2025,2045,2046,2047,2048,2060,2101)\n" +
                    "\t\t\t)"
            );

            StringBuilder stringBuilder2 = new StringBuilder("\t\tSELECT IFNULL(SUM(ogi.count),0) AS c1 FROM "+name+"ordergoodinit AS ogi LEFT JOIN USER AS ug ON ug.id=ogi.userId\n" +
                    "\t\tWHERE ug.parentId=" +user.getId() + " AND ogi.goodsId IN (274,275,286,2004,2018,2021,2025,2045,2046,2047,2048,2060,2101)"
            );

            Map<String, Object> maps = SqlRunner.db().selectOne(stringBuilder.toString());
            Map<String, Object> maps2 = SqlRunner.db().selectOne(stringBuilder2.toString());
            if((Double.valueOf(String.valueOf(maps.get("c"))).intValue() + Double.valueOf(String.valueOf(maps2.get("c1"))).intValue()) >0) {
                ThreeBackOneInit threeBackOneInit = new ThreeBackOneInit();
                threeBackOneInit.setUid(user.getId());
                threeBackOneInit.setAmt(new BigDecimal(15866));
                threeBackOneInit.setNumber(Double.valueOf(String.valueOf(maps.get("c"))).intValue() + Double.valueOf(String.valueOf(maps2.get("c1"))).intValue());
                list.add(threeBackOneInit);
            }
            i++;
            log.info("15866"+i);
        }

        threeBackOneInitService.saveBatch(list);

    }


    public void aa_3174_4() {

        String name = "jymall";
int i=0;
        List<User> userList = userService.list(new QueryWrapper<>());
        List<ThreeBackOneInit> list = new ArrayList<>();
        for (User user : userList) {


            StringBuilder stringBuilder = new StringBuilder("SELECT count(1) + IFNULL(u2.ext,0) as c FROM "+name+".orders AS o \n" +
                    "\tLEFT JOIN "+name+".USER AS u ON u.`id`=o.`userId`\n" +
                    "  LEFT JOIN "+name+".USER AS u2 ON u2.id=u.parentId \n" +
                    "\tWHERE u.`parentId`=" +user.getId() + " AND (o.`orderCapacityId`IN (23,24,28) OR o.`riseCapacityId` IN(23,24,28,35))\n" +
                    "\t\t\tAND o.`status` IN (201,301,401,402,501,285) AND o.`payTime` <='2024-05-01'  and ( o.shareCapaId >22 || o.shareCapaId is null )\n" +
                    "and (o.commissionModel !='兑换无佣金' or o.commissionModel is null)\n" +
                    "\t\t\tAND o.id IN (\n" +
                    "\t\t\t\tSELECT og.orderId FROM "+name+".ordergoods AS og WHERE og.goodsId IN(2110,2123)\n" +
                    "\t\t\t)\n" +
                    "\t"
            );

            StringBuilder stringBuilder2 = new StringBuilder("\t\tSELECT IFNULL(SUM(ogi.count),0) AS c1 FROM " + name + ".ordergoodinit AS ogi LEFT JOIN " + name + ".USER AS ug ON ug.id=ogi.userId\n" +
                    "\t\tWHERE ug.parentId=" +user.getId() + " AND ogi.goodsId IN (439,453)"
            );

            Map<String, Object> maps = SqlRunner.db().selectOne(stringBuilder.toString());
            Map<String, Object> maps2 = SqlRunner.db().selectOne(stringBuilder2.toString());
            if((Double.valueOf(String.valueOf(maps.get("c"))).intValue() + Double.valueOf(String.valueOf(maps2.get("c1"))).intValue()) >0) {
                ThreeBackOneInit threeBackOneInit = new ThreeBackOneInit();
                threeBackOneInit.setUid(user.getId());
                threeBackOneInit.setAmt(new BigDecimal(3174.4));
                threeBackOneInit.setNumber(Double.valueOf(String.valueOf(maps.get("c"))).intValue() + Double.valueOf(String.valueOf(maps2.get("c1"))).intValue());
                list.add(threeBackOneInit);
            }
            i++;
            log.info("3174.4"+i);
        }

        threeBackOneInitService.saveBatch(list);

    }



    public void aa_12692_8() {

        String name = "jymall";

        int i = 0;
        List<User> userList = userService.list(new QueryWrapper<>());
        List<ThreeBackOneInit> list = new ArrayList<>();
        for (User user : userList) {


            StringBuilder stringBuilder = new StringBuilder("SELECT count(1) + IFNULL(u2.ext,0) as c FROM "+name+".orders AS o \n" +
                    "\tLEFT JOIN "+name+".USER AS u ON u.`id`=o.`userId`\n" +
                    "  LEFT JOIN "+name+".USER AS u2 ON u2.id=u.parentId \n" +
                    "\tWHERE u.`parentId`=" +user.getId() + " AND (o.`orderCapacityId`IN (24) OR o.`riseCapacityId` IN(24))\n" +
                    "\t\t\tAND o.`status` IN (201,301,401,402,501) AND o.`payTime` <='2024-05-01'  and ( o.shareCapaId >22 || o.shareCapaId is null )\n" +
                    "\t\t\tAND o.id IN (\n" +
                    "\t\t\t\tSELECT og.orderId FROM "+name+".ordergoods AS og WHERE og.goodsId IN(2111,2115)\n" +
                    "\t\t\t)\n" +
                    "\t"
            );

            StringBuilder stringBuilder2 = new StringBuilder("\t\tSELECT IFNULL(SUM(ogi.count),0) AS c1 FROM "+name+".ordergoodinit AS ogi LEFT JOIN USER AS ug ON ug.id=ogi.userId\n" +
                    "\t\tWHERE ug.parentId=" +user.getId() + " AND ogi.goodsId IN (2111,2115)"
            );

            Map<String, Object> maps = SqlRunner.db().selectOne(stringBuilder.toString());
            Map<String, Object> maps2 = SqlRunner.db().selectOne(stringBuilder2.toString());
            if((Double.valueOf(String.valueOf(maps.get("c"))).intValue() + Double.valueOf(String.valueOf(maps2.get("c1"))).intValue()) >0) {
                ThreeBackOneInit threeBackOneInit = new ThreeBackOneInit();
                threeBackOneInit.setUid(user.getId());
                threeBackOneInit.setAmt(new BigDecimal(12692.8));
                threeBackOneInit.setNumber(Double.valueOf(String.valueOf(maps.get("c"))).intValue() + Double.valueOf(String.valueOf(maps2.get("c1"))).intValue());
                list.add(threeBackOneInit);
            }
            i++;
            log.info("12692.8"+i);
        }

        threeBackOneInitService.saveBatch(list);

    }




    public void aa_11880() {

        String name = "jymall";

        int i = 0;
        List<User> userList = userService.list(new QueryWrapper<>());
        List<ThreeBackOneInit> list = new ArrayList<>();
        for (User user : userList) {


            StringBuilder stringBuilder = new StringBuilder("SELECT count(1) + IFNULL(u2.ext,0) as c FROM "+name+".orders AS o \n" +
                    "\tLEFT JOIN "+name+".USER AS u ON u.`id`=o.`userId`\n" +
                    "  LEFT JOIN "+name+".USER AS u2 ON u2.id=u.parentId \n" +
                    "\tWHERE u.`parentId`=" +user.getId() + " AND (o.`orderCapacityId`IN (24) OR o.`riseCapacityId` IN(24))\n" +
                    "\t\t\tAND o.`status` IN (201,301,401,402,501) AND o.`payTime` <='2024-05-01'  and ( o.shareCapaId >22 || o.shareCapaId is null )\n" +
                    "\t\t\tAND o.id IN (\n" +
                    "\t\t\t\tSELECT og.orderId FROM "+name+".ordergoods AS og WHERE og.goodsId IN(281,2002,2056)\n" +
                    "\t\t\t)\n" +
                    "\t"
            );

            StringBuilder stringBuilder2 = new StringBuilder("\t\tSELECT IFNULL(SUM(ogi.count),0) AS c1 FROM "+name+".ordergoodinit AS ogi LEFT JOIN USER AS ug ON ug.id=ogi.userId\n" +
                    "\t\tWHERE ug.parentId=" +user.getId() + " AND ogi.goodsId IN (281,2002,2056)"
            );

            Map<String, Object> maps = SqlRunner.db().selectOne(stringBuilder.toString());
            Map<String, Object> maps2 = SqlRunner.db().selectOne(stringBuilder2.toString());
            if((Double.valueOf(String.valueOf(maps.get("c"))).intValue() + Double.valueOf(String.valueOf(maps2.get("c1"))).intValue()) >0) {
                ThreeBackOneInit threeBackOneInit = new ThreeBackOneInit();
                threeBackOneInit.setUid(user.getId());
                threeBackOneInit.setAmt(new BigDecimal(11880));
                threeBackOneInit.setNumber(Double.valueOf(String.valueOf(maps.get("c"))).intValue() + Double.valueOf(String.valueOf(maps2.get("c1"))).intValue());
                list.add(threeBackOneInit);
            }
            i++;
            log.info("11880"+i);
        }

        threeBackOneInitService.saveBatch(list);

    }


}
