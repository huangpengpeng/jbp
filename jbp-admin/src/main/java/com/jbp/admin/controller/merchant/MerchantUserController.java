package com.jbp.admin.controller.merchant;


import com.jbp.common.page.CommonPage;
import com.jbp.common.request.MerchantUserSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserAdminDetailResponse;
import com.jbp.common.response.UserResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商户端用户控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/user")
@Api(tags = "商户端用户控制器")
@Validated
public class MerchantUserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('merchant:user:page:list')")
    @ApiOperation(value = "商户端用户分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserResponse>> getList(@ModelAttribute @Validated MerchantUserSearchRequest request,
                                                          @ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<UserResponse> userCommonPage = CommonPage.restPage(userService.getMerchantPage(request, pageParamRequest));
        return CommonResult.success(userCommonPage);
    }

    @PreAuthorize("hasAuthority('merchant:user:detail')")
    @ApiOperation(value = "用户详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<UserAdminDetailResponse> detail(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(userService.getAdminDetail(id));
    }
}



