package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.common.request.PageParamRequest;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.math.BigDecimal;

public interface RelationScoreService extends IService<RelationScore> {
    RelationScore getByUser(Integer uId, Integer node);

    PageInfo<RelationScore> pageList(Integer uid, PageParamRequest pageParamRequest);

    Boolean save(Integer uid, int node);

    Boolean edit(Long id,BigDecimal usableScore, BigDecimal usedScore, int node);
}
