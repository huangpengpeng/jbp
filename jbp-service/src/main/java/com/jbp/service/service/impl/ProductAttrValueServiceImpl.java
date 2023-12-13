package com.jbp.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.service.dao.ProductAttrValueDao;
import com.jbp.service.service.ProductAttrValueService;
import com.jbp.common.constants.Constants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.product.ProductAttrValue;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * ProductAttrValueServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValue>
        implements ProductAttrValueService {

    @Resource
    private ProductAttrValueDao dao;

    /**
     * 根据商品id删除AttrValue
     * @param productId 商品id
     * @param type 类型区分是是否添加营销
     * @reture 删除结果
     */
    @Override
    public boolean removeByProductId(Integer productId,int type) {
        LambdaQueryWrapper<ProductAttrValue> lambdaQW = Wrappers.lambdaQuery();
        lambdaQW.eq(ProductAttrValue::getProductId, productId).eq(ProductAttrValue::getType,type);
        return dao.delete(lambdaQW) > 0;
    }

    /**
     * @param productId 商品id
     * @param attrId    属性id
     * @return 商品属性
     */
    @Override
    public ProductAttrValue getByProductIdAndAttrId(Integer productId, Integer attrId, Integer type) {
        LambdaQueryWrapper<ProductAttrValue> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductAttrValue::getProductId, productId);
        lqw.eq(ProductAttrValue::getType, type);
        lqw.eq(ProductAttrValue::getId, attrId);
        lqw.eq(ProductAttrValue::getIsDel, false);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 根据id、类型查询
     *
     * @param id   ID
     * @param type 类型
     * @return ProductAttrValue
     */
    @Override
    public ProductAttrValue getByIdAndProductIdAndType(Integer id, Integer productId, Integer type) {
        LambdaQueryWrapper<ProductAttrValue> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductAttrValue::getId, id);
        lqw.eq(ProductAttrValue::getProductId, productId);
        lqw.eq(ProductAttrValue::getType, type);
        lqw.eq(ProductAttrValue::getIsDel, false);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 添加(退货)/扣减库存
     *
     * @param id            商品属性规格id
     * @param num           数量
     * @param operationType 类型：add—添加，sub—扣减，refund-退款添加库存
     * @param type          活动类型 0=商品
     * @return Boolean
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String operationType, Integer type, Integer version) {
        UpdateWrapper<ProductAttrValue> updateWrapper = new UpdateWrapper<>();
        if (operationType.equals(Constants.OPERATION_TYPE_QUICK_ADD)) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            if (type > 0) {
                updateWrapper.setSql(StrUtil.format("quota = quota + {}", num));
            }
        }
        if (operationType.equals(Constants.OPERATION_TYPE_ADD)) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
            if (type > 0) {
                updateWrapper.setSql(StrUtil.format("quota = quota + {}", num));
            }
        }
        if (operationType.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            updateWrapper.setSql(StrUtil.format("stock = stock - {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales + {}", num));
            if(type == 0 || type == 4) { // 普通商品或者视频号商品
                // 扣减时加乐观锁保证库存不为负
                updateWrapper.last(StrUtil.format("and (stock - {} >= 0)", num));
            }else if (type > 0) {
                updateWrapper.setSql(StrUtil.format("quota = quota - {}", num));
                // 扣减时加乐观锁保证库存不为负
                updateWrapper.last(StrUtil.format("and (quota - {} >= 0)", num));
            }
        }
        updateWrapper.setSql("version = version + 1");
        updateWrapper.eq("id", id);
        updateWrapper.eq("type", type);
        updateWrapper.eq("version", version);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新商品attrValue失败，attrValueId = " + id);
        }
        return update;
    }

    /**
     * 删除商品规格属性值
     *
     * @param productId 商品id
     * @param type      商品类型
     * @return Boolean
     */
    @Override
    public Boolean deleteByProductIdAndType(Integer productId, Integer type) {
        LambdaUpdateWrapper<ProductAttrValue> luw = Wrappers.lambdaUpdate();
        luw.set(ProductAttrValue::getIsDel, true);
        luw.eq(ProductAttrValue::getProductId, productId);
        luw.eq(ProductAttrValue::getType, type);
        return update(luw);
    }

    /**
     * 获取商品规格列表
     *
     * @param productId 商品id
     * @param type      商品类型
     * @return List
     */
    @Override
    public List<ProductAttrValue> getListByProductIdAndType(Integer productId, Integer type) {
        LambdaQueryWrapper<ProductAttrValue> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductAttrValue::getProductId, productId);
        lqw.eq(ProductAttrValue::getType, type);
        lqw.eq(ProductAttrValue::getIsDel, false);
        return dao.selectList(lqw);
    }

    @Override
    public List<ProductAttrValue> getByProductIdAndAttrIdList(Integer productId, List<Integer> attrIdList, Integer type) {
        LambdaQueryWrapper<ProductAttrValue> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductAttrValue::getProductId, productId);
        lqw.eq(ProductAttrValue::getType, type);
        lqw.in(ProductAttrValue::getId, attrIdList);
        lqw.eq(ProductAttrValue::getIsDel, false);
        return dao.selectList(lqw);
    }
}

