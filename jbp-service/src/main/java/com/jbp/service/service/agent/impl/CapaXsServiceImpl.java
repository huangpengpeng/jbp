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
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.CapaXsDao;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.agent.CapaXsService;


@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class CapaXsServiceImpl extends ServiceImpl<CapaXsDao, CapaXs> implements CapaXsService {
    @Resource
    private SystemAttachmentService systemAttachmentService;

    @Override
    public PageInfo<CapaXs> page(PageParamRequest pageParamRequest) {
        Page<CapaXs> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list());
    }

    @Override
    public CapaXs save(String name, Long pCapaId, int rankNum, String iconUrl, String riseImgUrl, String shareImgUrl) {
        if (getByName(name) != null) {
            throw new CrmebException("星级名称不能重复");
        }
        if (getByRankNum(rankNum) != null) {
            throw new CrmebException("星级编号不能重复");
        }
        String cdnUrl = systemAttachmentService.getCdnUrl();
        CapaXs capaXs = new CapaXs(name, pCapaId, rankNum, systemAttachmentService.clearPrefix(iconUrl, cdnUrl), systemAttachmentService.clearPrefix(riseImgUrl, cdnUrl), systemAttachmentService.clearPrefix(shareImgUrl, cdnUrl));
        save(capaXs);
        return capaXs;
    }

    @Override
    public CapaXs getMinCapa() {
        return getOne(new QueryWrapper<CapaXs>().lambda().orderByAsc(CapaXs::getRankNum).last(" limit 1"));
    }

    @Override
    public CapaXs getNext(Long capaId) {
        if (capaId == null) {
            return getMinCapa();
        }
        CapaXs capa = getById(capaId);
        if (capa.getPCapaId() == null) {
            return null;
        }
        return getById(capa.getPCapaId());
    }

    @Override
    public CapaXs getByName(String name) {
        return getOne(new QueryWrapper<CapaXs>().lambda().eq(CapaXs::getName, name));
    }

    @Override
    public CapaXs getByRankNum(Integer rankNum) {
        return getOne(new QueryWrapper<CapaXs>().lambda().eq(CapaXs::getRankNum, rankNum));
    }
}