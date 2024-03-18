package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.PlatformWalletFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.PlatformWalletFlowVo;
import com.jbp.service.service.agent.PlatformWalletFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/platform/wallet/flow")
@Api(tags = "平台积分详情")
public class PlatformWalletFlowController {
    @Resource
    private PlatformWalletFlowService platformWalletFlowService;

    @PreAuthorize("hasAuthority('agent:platform:wallet:flow')")
    @GetMapping("/page")
    @ApiOperation("平台积分详情列表")
    public CommonResult<CommonPage<PlatformWalletFlow>> getList(Integer type, String dateLimit, String externalNo, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(platformWalletFlowService.pageList(type, dateLimit, externalNo, pageParamRequest)));
    }
    @PreAuthorize("hasAuthority('agent:platform:wallet:excel')")
    @GetMapping("/excel")
    @ApiOperation("平台积分详情导出")
    public CommonResult<List<PlatformWalletFlowVo>> excel(Integer type, String dateLimit, String externalNo){
        if (ObjectUtil.isEmpty(type)&& ObjectUtil.isEmpty(dateLimit)&& ObjectUtil.isEmpty(externalNo)) {
            throw new CrmebException("请选择一个过滤条件");
        }
        return CommonResult.success(platformWalletFlowService.excel(type,dateLimit,externalNo));
    }
}
