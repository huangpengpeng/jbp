package com.jbp.admin.controller.platform;

import com.jbp.common.model.article.ArticlePlate;
import com.jbp.common.request.ArticlePlateRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.ArticlePlateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/admin/platform/article/plate")
@Api(tags = "文章板块管理")
public class ArticlePlateController {

    @Autowired
    private ArticlePlateService articlePlateService;

    @ApiOperation(value = "文章板块列表")
    @GetMapping("/list")
    public CommonResult<List<ArticlePlate>> getList() {
        return CommonResult.success(articlePlateService.list());
    }

    @ApiOperation(value = "添加文章板块")
    @PostMapping("/add")
    public CommonResult<String> add(@RequestBody @Validated ArticlePlateRequest request) {
        if (articlePlateService.add(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "删除文章板块")
    @PostMapping("/delete/{id}")
    public CommonResult<String> del(@PathVariable(value = "id")Integer id) {
        if (articlePlateService.delete(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

}
