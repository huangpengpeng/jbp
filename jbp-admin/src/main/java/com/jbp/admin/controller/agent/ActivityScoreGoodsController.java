package com.jbp.admin.controller.agent;

import com.jbp.service.service.agent.ActivityScoreGoodsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/activity/score/goods")
@Api(tags = "积分活动商品管理")
public class ActivityScoreGoodsController {

    @Autowired
    private ActivityScoreGoodsService activityScoreGoodsService;
}
