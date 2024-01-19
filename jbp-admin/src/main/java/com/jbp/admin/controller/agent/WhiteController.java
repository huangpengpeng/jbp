package com.jbp.admin.controller.agent;

import com.jbp.common.model.user.White;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.WhiteService;
import com.jbp.service.service.agent.LimitTempService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/white")
@Api(tags = "白名单管理")
public class WhiteController {
    @Autowired
    private WhiteService whiteService;
    @Resource
    private LimitTempService limitTempService;

    @RequestMapping(value = "/page/list", method = RequestMethod.GET)
    @ApiOperation("白名单列表")
    public CommonResult<CommonPage<White>> getList(@ModelAttribute @Validated WhiteRequest request,
                                                   @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(whiteService.pageList(request, pageParamRequest)));
    }

    @ApiOperation("新增白名单")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult addWhiten(@RequestBody WhiteRequest white) {
        if (whiteService.addWhiten(white)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @GetMapping(value = "/delete/{id}")
    @ApiOperation("白名单删除")
    public CommonResult delete(@PathVariable("id") Long id) {
        whiteService.delete(id);
        return CommonResult.success();
    }

    @GetMapping("/list")
    @ApiOperation("列表")
    public CommonResult<List<White>> list(String name) {
        limitTempService.list();
        return CommonResult.success(whiteService.getByNameList(name));
    }
}
