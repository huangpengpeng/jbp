package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.ProductCommConfigDao;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.agent.ProductCommConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 佣金统一配置
 *
 * 支持的佣金不是有用户决定的，是有开发的佣金策略决定。用户只能决定是否用该佣金
 */
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
@Slf4j
public class ProductCommConfigServiceImpl extends ServiceImpl<ProductCommConfigDao, ProductCommConfig> implements ProductCommConfigService {


    /**
     * 获取佣金策略信息, 保存佣金配置 以供用户开启关闭
     * 多平台佣金名称不能暴露不在自动生成 开发者自己去数据库添加
     */
//    @PostConstruct
    public void init() {
        for (ProductCommEnum value : ProductCommEnum.values()) {
            if (getByType(value.getType()) == null) {
                add(value.getType(), value.getName(), value.getIfWhole(), value.getDesc());
            }
        }
    }

    @Override
    public ProductCommConfig add(Integer type, String name, Boolean ifWhole, String desc) {
        ProductCommConfig productCommConfig = new ProductCommConfig(type, name, ifWhole, desc);
        save(productCommConfig);
        return productCommConfig;
    }

    @Override
    public ProductCommConfig getByType(Integer type) {
        return getOne(new QueryWrapper<ProductCommConfig>().lambda().eq(ProductCommConfig::getType, type));
    }

    @Override
    public PageInfo<ProductCommConfig> pageList(PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<ProductCommConfig> wrapper = new LambdaQueryWrapper<>();
        Page<ProductCommConfig> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ProductCommConfig> list = list(wrapper);
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Boolean open(Integer type) {
        UpdateWrapper<ProductCommConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ProductCommConfig::getIfOpen, true).eq(ProductCommConfig::getType, type);
        return update(updateWrapper);
    }

    @Override
    public Boolean close(Integer type) {
        UpdateWrapper<ProductCommConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ProductCommConfig::getIfOpen, false).eq(ProductCommConfig::getType, type);
        return update(updateWrapper);
    }

    @Override
    public List<ProductCommConfig> getOpenList() {
        return list(new QueryWrapper<ProductCommConfig>().lambda().eq(ProductCommConfig::getIfOpen, true));
    }
}
