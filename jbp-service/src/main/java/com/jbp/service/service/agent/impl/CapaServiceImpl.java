package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.RiseConditionRequest;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.condition.ConditionChain;
import com.jbp.service.dao.agent.CapaDao;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.agent.CapaService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class CapaServiceImpl extends ServiceImpl<CapaDao, Capa> implements CapaService {
    @Resource
    private SystemAttachmentService systemAttachmentService;
    @Resource
    private ConditionChain conditionChain;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public PageInfo<Capa> page(PageParamRequest pageParamRequest) {
        Page<Capa> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list());
    }

    @Override
    public Capa save(String name, Long pCapaId, int rankNum, String iconUrl, String riseImgUrl, String shareImgUrl) {
        if (getByName(name) != null) {
            throw new CrmebException("等级名称不能重复");
        }
        if (getByRankNum(rankNum) != null) {
            throw new CrmebException("数字等级不能重复");
        }
        String cdnUrl = systemAttachmentService.getCdnUrl();
        Capa capa = new Capa(name, pCapaId, rankNum, systemAttachmentService.clearPrefix(iconUrl, cdnUrl), systemAttachmentService.clearPrefix(riseImgUrl, cdnUrl), systemAttachmentService.clearPrefix(shareImgUrl, cdnUrl));
        Boolean execute = transactionTemplate.execute(e -> {
            save(capa);
            if (ObjectUtils.isNotEmpty(pCapaId)) {
                if (capa.getId()>pCapaId){
                    throw new CrmebException("请设置下个等级比本等级较大");
                }
            }
            return Boolean.TRUE;
        });
        return capa;
    }

    @Override
    public Capa saveRiseCondition(RiseConditionRequest request) {
        Capa capa = getById(request.getCapaId());
        capa.setParser(request.getParser());
        List<String> conditionNames = capa.getConditionNames();
        List<RiseCondition> saveConditionList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(conditionNames)){
            List<RiseCondition> conditionList = request.getConditionList();
            for (RiseCondition riseCondition : conditionList) {
                if (conditionNames.contains(riseCondition.getName())) {
                    conditionChain.valid(riseCondition);
                    saveConditionList.add(riseCondition);
                }
            }
        }
        capa.setConditionList(saveConditionList);
        updateById(capa);
        return capa;
    }

    @Override
    public Capa getMinCapa() {
        return getOne(new QueryWrapper<Capa>().lambda().orderByAsc(Capa::getRankNum).last(" limit 1"));
    }

    @Override
    public Capa getMaxCapa() {
        return getOne(new QueryWrapper<Capa>().lambda().orderByDesc(Capa::getRankNum).last(" limit 1"));
    }

    @Override
    public Capa getNext(Long capaId) {
        if (capaId == null) {
            return getMinCapa();
        }
        Capa capa = getById(capaId);
        if (capa.getPCapaId() == null) {
            return null;
        }
        return getById(capa.getPCapaId());
    }

    @Override
    public List<Capa> getPre(Long capaId) {
        if (capaId == null) {
            return Lists.newArrayList();
        }
        return list(new LambdaQueryWrapper<Capa>().eq(Capa::getPCapaId, capaId));
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
    public List<Capa> getList() {
        return list(new QueryWrapper<Capa>().lambda().orderByAsc(Capa::getId));
    }

    @Override
    public Map<Long, Capa> getCapaMap() {
        List<Capa> list = list();
        return FunctionUtil.keyValueMap(list, Capa::getId);
    }

    @Override
    public List<Capa> getMaxCapaList(Long capaId) {
        Capa capa = getOne(new QueryWrapper<Capa>().lambda().eq(Capa::getId, capaId));
        return list(new QueryWrapper<Capa>().lambda().ge(Capa::getRankNum, capa.getRankNum()));
    }
}
