package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.ActivityScoreGoods;
import com.jbp.service.dao.agent.ActivityScoreGoodsDao;
import com.jbp.service.service.agent.ActivityScoreGoodsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class ActivityScoreGoodsServiceImpl extends ServiceImpl<ActivityScoreGoodsDao,ActivityScoreGoods> implements ActivityScoreGoodsService {


}
