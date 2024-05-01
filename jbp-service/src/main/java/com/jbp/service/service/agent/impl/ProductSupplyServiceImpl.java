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

import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ProductSupplyServiceImpl extends ServiceImpl<ProductSupplyDao, ProductSupply> implements ProductSupplyService{


    /**
     * 获取供应商列表
     *
     * @return List
     */
    @Override
    public List<ProductSupply> supplyList() {
        return this.baseMapper.selectList(null);
    }

    /**
     * 添加供应商
     *
     * @param request
     * @return true
     */
    @Override
    public Boolean add(ProductSupplyAddRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            throw new CrmebException("供应商名称不能为空");
        }
        if (null != this.getOne(new QueryWrapper<ProductSupply>().eq("name", request.getName()))) {
            throw new CrmebException("供应商名称已存在");
        }
        ProductSupply productSupply = new ProductSupply();
        productSupply.setName(request.getName());
        baseMapper.insert(productSupply);
        return true;
    }
}
