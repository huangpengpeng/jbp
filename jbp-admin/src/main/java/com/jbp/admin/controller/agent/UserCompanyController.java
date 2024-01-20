package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.agent.UserCompany;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserCompanyRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCompanyService;
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
@RequestMapping("api/admin/agent/user/company")
@Api(tags = "分公司区域")
public class UserCompanyController {
    @Resource
    private UserCompanyService userCompanyService;
    @Resource
    private SystemAttachmentService systemAttachmentService;
    @Resource
    private UserService userService;
    @PreAuthorize("hasAuthority('agent:user:company:page')")
    @ApiOperation(value = "分公司区域用户列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/page")
    public CommonResult<CommonPage<UserCompany>> page(UserCompanyRequest request, PageParamRequest pageParamRequest) {
        User user = userService.getByAccount(request.getAccount());
        if (ObjectUtil.isNull(user)) {
            if (ObjectUtil.isNull(request.getAccount()) || request.getAccount().equals("")) {
                user = new User();
            } else {
                return CommonResult.failed("账户信息错误");
            }
        }
        return CommonResult.success(CommonPage.restPage(userCompanyService.pageList(user.getId(), request.getProvince(), request.getCity(), request.getArea(), pageParamRequest)));
    }
    @PreAuthorize("hasAuthority('agent:user:company:add')")
    @ApiOperation(value = "分公司用户新增", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/add")
    public CommonResult add(String account, String province, String city, String area, String address, String companyName,
                            String licenseNo, String licenseUrl) {
        if (StringUtils.isAnyBlank(account, province, city, area, address, companyName)) {
            return CommonResult.failed("请填写完整信息");
        }
        User user = userService.getByAccount(account);
        if (user == null) {
            return CommonResult.failed("账户不存在");
        }
        UserCompany userCompany = userCompanyService.getByCity(province, city);
        if (userCompany != null) {
            return CommonResult.failed("该市级地址已开通分公司");
        }
        String cdnUrl = systemAttachmentService.getCdnUrl();
        userCompany = UserCompany.builder().uid(user.getId()).companyName(companyName).licenseNo(systemAttachmentService.clearPrefix(licenseNo,cdnUrl)).licenseUrl(systemAttachmentService.clearPrefix(licenseUrl,cdnUrl))
                .province(province).city(city).area(area).address(address).status(UserCompany.Constants.已开通.toString()).build();
        userCompanyService.save(userCompany);
        return CommonResult.success();
    }
    @PreAuthorize("hasAuthority('agent:user:company:del')")
    @ApiOperation(value = "公司用户删除", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/del")
    public CommonResult edit(Long id) {
        userCompanyService.removeById(id);
        return CommonResult.success();
    }
}
