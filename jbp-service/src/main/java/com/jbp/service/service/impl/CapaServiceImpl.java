package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.b2b.Capa;
import com.jbp.common.model.b2b.CapaCondition;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.b2b.CapaDao;
import com.jbp.service.service.CapaConditionService;
import com.jbp.service.service.CapaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CapaServiceImpl extends ServiceImpl<CapaDao, Capa> implements CapaService {

    @Resource
    private CapaConditionService capaConditionService;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public PageInfo<Capa> page(PageParamRequest pageParamRequest) {
        Page<Capa> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list());
    }

    @Override
    public Capa getMinCapa() {
        return getOne(new QueryWrapper<Capa>().lambda().orderByAsc(Capa::getRankNum).last(" limit 1"));
    }

    @Override
    public Capa getNext(Long capaId) {
        if(capaId == null){
            return getMinCapa();
        }
        Capa capa = getById(capaId);
        if(capa.getPCapaId() == null){
            return null;
        }
        return getById(capa.getPCapaId());
    }

    @Override
    public Capa getByName(String name) {
        return getOne(new QueryWrapper<Capa>().lambda().eq(Capa::getName, name));
    }

    @Override
    public Capa getByRankNum(Integer rankNum) {
        return getOne(new QueryWrapper<Capa>().lambda().eq(Capa::getRankNum, rankNum));
    }

    @Override
    public void saveOrUpdate(Long capaId, List<CapaCondition> conditionList, String parser) {
        transactionTemplate.execute(s -> {
            // 删除
            capaConditionService.deleteByCapa(capaId);
            // 新增
            capaConditionService.saveBatch(conditionList);
            Capa capa = getById(capaId);
            capa.setParser(parser);
            updateById(capa);
            return Boolean.TRUE;
        });
    }
}
