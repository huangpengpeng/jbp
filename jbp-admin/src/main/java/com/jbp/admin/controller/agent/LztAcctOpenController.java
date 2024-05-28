package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctOpen;
import com.jbp.common.model.agent.LztPayChannel;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.YopBankApplyRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.FileResultVo;
import com.jbp.service.service.LztService;
import com.jbp.service.service.YopService;
import com.jbp.service.service.agent.LztAcctOpenService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztPayChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/lzt/acct/open")
@Api(tags = "来账通-开户申请")
public class LztAcctOpenController {

    @Resource
    private YopService yopService;
    @Resource
    private LztAcctOpenService lztAcctOpenService;
    @Resource
    private LztPayChannelService lztPayChannelService;


    @ApiOperation(value = "图片上传")
    @RequestMapping(value = "/yop/image", method = RequestMethod.POST)
    public CommonResult<String> image(MultipartFile multipart) {
        return CommonResult.success(yopService.upload(multipart));
    }

    @PreAuthorize("hasAuthority('agent:lzt:acct:open:apply')")
    @ApiOperation(value = "连连个人开户")
    @GetMapping(value = "/apply")
    public CommonResult<LztAcctOpen> apply(Integer merId, String userId, String userType, String returnUrl, String businessScope) {
        if (ObjectUtil.isEmpty(merId)) {
            SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
            merId = systemAdmin.getMerId();
        }
        LztPayChannel lztPayChannel = lztPayChannelService.getByMer(merId, "连连");
        LztAcctOpen lztAcctOpen = lztAcctOpenService.apply(userId, userType, returnUrl, businessScope, lztPayChannel);
        return CommonResult.success(lztAcctOpen);
    }




    @ApiOperation(value = "易宝个人开户")
    @PostMapping(value = "/yop/apply")
    public CommonResult<LztAcctOpen> yopApply(@RequestBody YopBankApplyRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        LztPayChannel lztPayChannel = lztPayChannelService.getByMer(systemAdmin.getMerId(), "易宝");
        LztAcctOpen lztAcctOpen = lztAcctOpenService.yopApply(request.getSignName(), request.getId_card(), request.getFrontUrl(), request.getBackUrl(), request.getMobile(), request.getProvince(), request.getCity(), request.getDistrict(),
                request.getAddress(), request.getBankCardNo(), request.getBankCode(), lztPayChannel);
        return CommonResult.success(lztAcctOpen);
    }


    @PreAuthorize("hasAuthority('agent:lzt:acct:open:page')")
    @ApiOperation(value = "开户记录列表")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<LztAcctOpen>> page(String userId, String status, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        return CommonResult.success(CommonPage.restPage(lztAcctOpenService.pageList(merId, userId, status, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:lzt:acct:open:del')")
    @ApiOperation(value = "开户记录删除")
    @GetMapping(value = "/del")
    public CommonResult del(Long id) {
        lztAcctOpenService.del(id);
        return CommonResult.success();
    }
}
