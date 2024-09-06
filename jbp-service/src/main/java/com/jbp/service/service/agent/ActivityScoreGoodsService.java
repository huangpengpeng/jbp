package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.ActivityScoreGoods;
import io.swagger.models.auth.In;

import java.util.List;
import java.util.Map;

public interface ActivityScoreGoodsService extends IService<ActivityScoreGoods> {

    public Integer getProductNumber(List<Integer> productId, List<Integer> uid,String startTime,String endTime,Integer activityId);

}
