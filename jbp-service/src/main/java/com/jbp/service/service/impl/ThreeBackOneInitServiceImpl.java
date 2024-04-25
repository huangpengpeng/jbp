package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.order.OrderExt;
import com.jbp.common.model.order.ThreeBackOneInit;
import com.jbp.service.dao.OrderExtDao;
import com.jbp.service.dao.ThreeBackOneInitDao;
import com.jbp.service.service.OrderExtService;
import com.jbp.service.service.ThreeBackOneInitService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ThreeBackOneInitServiceImpl extends ServiceImpl<ThreeBackOneInitDao, ThreeBackOneInit> implements ThreeBackOneInitService {

}
