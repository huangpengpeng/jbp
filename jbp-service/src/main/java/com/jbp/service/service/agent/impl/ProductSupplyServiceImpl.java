package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductSupply;
import com.jbp.common.request.agent.ProductSupplyAddRequest;
import com.jbp.service.dao.agent.ProductSupplyDao;
import com.jbp.service.service.agent.ProductSupplyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ProductSupplyServiceImpl extends ServiceImpl<ProductSupplyDao, ProductSupply> implements ProductSupplyService{

    @Override
    public Boolean add(ProductSupplyAddRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            throw new CrmebException("供应商名称不能为空");
        }
        if (null != this.getOne(new QueryWrapper<ProductSupply>().lambda().eq(ProductSupply::getName, request.getName()))) {
            throw new CrmebException("供应商名称已存在");
        }
        ProductSupply productSupply = new ProductSupply();
        productSupply.setName(request.getName());
        return save(productSupply);
    }
}
