package com.jbp.service.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.CapaRiseCondition;

import java.util.List;

public interface CapaRiseConditionService extends IService<CapaRiseCondition> {

    List<CapaRiseCondition> getByType(Integer type);

    CapaRiseCondition add(Integer type, String name, String description, JSONObject value);
}
