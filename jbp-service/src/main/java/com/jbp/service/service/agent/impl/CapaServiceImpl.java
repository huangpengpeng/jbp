package com.jbp.service.service.agent.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.CapaDao;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.agent.CapaService;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class CapaServiceImpl extends ServiceImpl<CapaDao, Capa> implements CapaService {
    @Resource
    private SystemAttachmentService systemAttachmentService;

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
        save(capa);
        return capa;
    }

    @Override
    public Capa getMinCapa() {
        return getOne(new QueryWrapper<Capa>().lambda().orderByAsc(Capa::getRankNum).last(" limit 1"));
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
    public Capa getByName(String name) {
        return getOne(new QueryWrapper<Capa>().lambda().eq(Capa::getName, name));
    }

    @Override
    public Capa getByRankNum(Integer rankNum) {
        return getOne(new QueryWrapper<Capa>().lambda().eq(Capa::getRankNum, rankNum));
    }

}
