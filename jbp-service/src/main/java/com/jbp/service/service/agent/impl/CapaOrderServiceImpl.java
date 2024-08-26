package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.util.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.CapaOrder;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.CapaOrderRequest;
import com.jbp.service.dao.agent.CapaOrderDao;
import com.jbp.service.service.agent.CapaOrderService;
import com.jbp.service.service.agent.CapaService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class CapaOrderServiceImpl extends ServiceImpl<CapaOrderDao, CapaOrder> implements CapaOrderService {

    @Resource
    private CapaService capaService;
    @Override
    public CapaOrder getByCapaId(Integer capaId) {
        return getOne(new QueryWrapper<CapaOrder>().lambda().eq(CapaOrder::getCapaId, capaId));
    }

    @Override
    public PageInfo<CapaOrder> getList(PageParamRequest pageParamRequest) {
        Page<CapaOrder> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<CapaOrder> list = list();
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        Map<Long, Capa> capaMap = capaService.getCapaMap();
        list.forEach(e->{
            Capa capa = capaMap.get(e.getCapaId());
            e.setCapaName(capa != null ? capa.getName() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Boolean edit(CapaOrderRequest request) {
        if (ObjectUtil.isNull(request.getIfCompany()) || ObjectUtil.isNull(request.getIfSupply()) || ObjectUtil.isNull(request.getId())) {
            throw new CrmebException("参数不全！");
        }
        CapaOrder capaOrder = getById(request.getId());
        if (capaOrder == null) {
            throw new CrmebException("id不存在！");
        }
        BeanUtils.copyProperties(request, capaOrder);
        return updateById(capaOrder);
    }

    @Override
    public CapaOrder getCapaOrderByUser(Integer capaId) {
        CapaOrder capaOrder = getOne(new QueryWrapper<CapaOrder>().lambda().eq(CapaOrder::getCapaId, capaId));
        if (capaOrder == null) {
            throw new CrmebException("该等级未设置订货管理信息！");
        }
        return capaOrder;
    }
}
