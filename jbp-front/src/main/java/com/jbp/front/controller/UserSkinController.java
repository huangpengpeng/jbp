package com.jbp.front.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.user.UserSkin;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.UserSkinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/front/user/skin")
@Api(tags = "用户皮肤检测信息控制器")
public class UserSkinController {

    @Autowired
    private UserSkinService userSkinService;
    @Autowired
    private UserService userService;

    @ApiOperation(value = "获取当前用户皮肤检测信息")
    @RequestMapping(value = "/getSkinInfo", method = RequestMethod.GET)
    public CommonResult<List<UserSkin>> getSkinInfo() {
        Integer uid = userService.getUserId();
        if (ObjectUtil.isNull(uid)){
            return CommonResult.failed("用户未登录!");
        }
        List<UserSkin> list = userSkinService.list(new QueryWrapper<UserSkin>().lambda().eq(UserSkin::getUid,uid ));
        return CommonResult.success(list);
    }
}
