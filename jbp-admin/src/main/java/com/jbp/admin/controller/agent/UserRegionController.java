package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserRegion;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserRegionRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserRegionService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/region")
@Api(tags = "用户区域")
public class UserRegionController {

    @Resource
    private UserRegionService userRegionService;
    @Resource
    private UserService userService;
    @PreAuthorize("hasAuthority('agent:user:region:page')")
    @ApiOperation(value = "区域用户列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/page")
    public CommonResult<CommonPage<UserRegion>> page(UserRegionRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(userRegionService.pageList(uid, request.getProvince(), request.getCity(), request.getArea(), pageParamRequest)));
    }
    @PreAuthorize("hasAuthority('agent:user:region:add')")
    @ApiOperation(value = "区域用户新增", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/add")
    public CommonResult add(String account, String province, String city, String area, String address) {
        if (StringUtils.isAnyBlank(account, province, city, area, address)) {
            return CommonResult.failed("请填写完整信息");
        }
        User user = userService.getByAccount(account);
        if (ObjectUtil.isNull(user)) {
            return CommonResult.success("账户不存在");
        }
        UserRegion userRegion = userRegionService.getByArea(province, city, area, UserRegion.Constants.已开通.toString());
        if (!ObjectUtil.isNull(userRegion)) {
            return CommonResult.success("该区域存已经被其他用户开通");
        }
        userRegion = UserRegion.builder().uid(user.getId()).province(province).city(city).area(area).address(address).status(UserRegion.Constants.已开通.toString()).build();
        userRegionService.save(userRegion);
        return CommonResult.success();
    }
    @PreAuthorize("hasAuthority('agent:user:region:del')")
    @ApiOperation(value = "区域用户删除", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/del")
    public CommonResult userRegionEdit(Long id) {
        userRegionService.removeById(id);
        return CommonResult.success();
    }
}
