package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.ProductProfitConfigDao;
import com.jbp.service.product.profit.ProductProfitEnum;
import com.jbp.service.service.agent.ProductProfitConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
@Slf4j
public class ProductProfitConfigServiceImpl extends ServiceImpl<ProductProfitConfigDao, ProductProfitConfig> implements ProductProfitConfigService {

//    @PostConstruct
    public void init(){
        for (ProductProfitEnum value : ProductProfitEnum.values()) {
            if (getByType(value.getType()) == null) {
                add(value.getType(), value.getName(), value.getDesc());
            }
        }
    }
    @Override
    public ProductProfitConfig add(Integer type, String name, String description) {
        ProductProfitConfig config = new ProductProfitConfig(type, name, description);
        save(config);
        return config;
    }

    @Override
    public ProductProfitConfig getByType(Integer type) {
        return getOne(new QueryWrapper<ProductProfitConfig>().lambda().eq(ProductProfitConfig::getType, type));
    }

    @Override
    public PageInfo<ProductProfitConfig> pageList(PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<ProductProfitConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ProductProfitConfig::getGmtModify);
        Page<ProductProfitConfig> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ProductProfitConfig> list = list(wrapper);
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Boolean open(Integer type) {
        UpdateWrapper<ProductProfitConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ProductProfitConfig::getIfOpen, true).eq(ProductProfitConfig::getType, type);
        return update(updateWrapper);
    }

    @Override
    public Boolean close(Integer type) {
        UpdateWrapper<ProductProfitConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ProductProfitConfig::getIfOpen, false).eq(ProductProfitConfig::getType, type);
        return update(updateWrapper);
    }

    @Override
    public List<ProductProfitConfig> getOpenList() {
        return list(new QueryWrapper<ProductProfitConfig>().lambda().eq(ProductProfitConfig::getIfOpen, true));
    }
}
