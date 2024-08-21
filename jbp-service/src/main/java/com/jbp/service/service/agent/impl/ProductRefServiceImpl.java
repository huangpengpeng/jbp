package com.jbp.service.service.agent.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.constants.ProductConstants;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductRef;
import com.jbp.common.request.agent.ProductRefRequest;
import com.jbp.service.dao.agent.ProductRefDao;
import com.jbp.service.service.ProductService;
import com.jbp.service.service.agent.ProductRefService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ProductRefServiceImpl extends ServiceImpl<ProductRefDao, ProductRef> implements ProductRefService {

    @Resource
    private ProductService productService;
    @Override
    public Boolean add(ProductRefRequest request) {
        List<ProductRef> refAddList = CollUtil.newArrayList();
        List<ProductRef> refUpdateList = CollUtil.newArrayList();
        if (CollectionUtils.isNotEmpty(request.getProductRefInfoList())){
            request.getProductRefInfoList().forEach(e -> {
                ProductRef ref = new ProductRef();
                BeanUtils.copyProperties(e, ref);
                ref.setRefProductId(request.getRefProductId());
                if (ObjectUtil.isNull(ref.getId())) {
                    refAddList.add(ref);
                }else {
                    refUpdateList.add(ref);
                }
            });
        }
        // 删除当前商品原有的套组
        remove(new QueryWrapper<ProductRef>().lambda().eq(ProductRef::getRefProductId, request.getRefProductId()));
        if (CollUtil.isNotEmpty(refAddList)){
            saveBatch(refAddList);
        }
        if (CollUtil.isNotEmpty(refUpdateList)){
            saveOrUpdateBatch(refUpdateList);
        }
        return true;
    }

    @Override
    public List<ProductRef> getList(Integer refProductId) {
        return list(new QueryWrapper<ProductRef>().lambda().eq(ProductRef::getRefProductId, refProductId));
    }

    @Override
    public List<Product> getNoRef() {
        List<Product> productList = productService.list(new QueryWrapper<Product>().lambda().eq(Product::getIsRecycle, false).eq(Product::getIsDel, false)
                .eq(Product::getIsAudit, false).in(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_SUCCESS, ProductConstants.AUDIT_STATUS_EXEMPTION));
        List<ProductRef> list = list();
        if (CollectionUtils.isEmpty(list)){
            list = Lists.newArrayList();
        }
        List<Integer> refProductIdList = list.stream().map(ProductRef::getRefProductId).collect(Collectors.toList());
        return productList.stream().filter(p -> !refProductIdList.contains(p.getId())).collect(Collectors.toList());
    }
}
