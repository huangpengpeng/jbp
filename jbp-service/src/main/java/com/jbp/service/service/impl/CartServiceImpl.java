package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.service.dao.CartDao;
import com.jbp.service.service.*;
import com.jbp.common.constants.DateConstants;
import com.jbp.common.constants.ProductConstants;
import com.jbp.common.constants.RedisConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.cat.Cart;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductAttrValue;
import com.jbp.common.model.user.User;
import com.jbp.common.request.CartNumRequest;
import com.jbp.common.request.CartRequest;
import com.jbp.common.request.CartResetRequest;
import com.jbp.common.response.CartInfoResponse;
import com.jbp.common.response.CartMerchantResponse;
import com.jbp.common.utils.RedisUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * StoreCartServiceImpl 接口实现
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
public class CartServiceImpl extends ServiceImpl<CartDao, Cart> implements CartService {

    @Resource
    private CartDao dao;

    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private ProductRelationService productRelationService;

    /**
     * 列表
     *
     * @param isValid 是否失效
     * @return List<CartMerchantResponse>
     */
    @Override
    public List<CartMerchantResponse> getList(boolean isValid) {
        Integer userId = userService.getUserIdException();
        //带 StoreCart 类的多条件查询
        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Cart::getUid, userId);
        lqw.eq(Cart::getStatus, isValid);
        lqw.orderByDesc(Cart::getId);
        List<Cart> cartList = dao.selectList(lqw);
        if (CollUtil.isEmpty(cartList)) {
            return CollUtil.newArrayList();
        }

        List<Integer> merIdList = cartList.stream().map(Cart::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMerIdMapByIdList(merIdList);
        List<CartMerchantResponse> responseList = CollUtil.newArrayList();
        merIdList.forEach(merId -> {
            CartMerchantResponse merchantResponse = new CartMerchantResponse();
            merchantResponse.setMerId(merId);
            merchantResponse.setMerName(merchantMap.get(merId).getName());
            List<Cart> merCartList = cartList.stream().filter(e -> e.getMerId().equals(merId)).collect(Collectors.toList());
            List<CartInfoResponse> infoResponseList = merCartList.stream().map(storeCart -> {
                CartInfoResponse cartInfoResponse = new CartInfoResponse();
                BeanUtils.copyProperties(storeCart, cartInfoResponse);
                // 获取商品信息
                Product product = productService.getCartByProId(storeCart.getProductId());
                cartInfoResponse.setImage(product.getImage());
                cartInfoResponse.setProName(product.getName());
                if (!isValid) {// 失效商品直接掠过
                    cartInfoResponse.setAttrStatus(false);
                    return cartInfoResponse;
                }
                // 获取对应的商品规格信息(只会有一条信息)
                ProductAttrValue attrValue = productAttrValueService.getByProductIdAndAttrId(storeCart.getProductId(),
                        storeCart.getProductAttrUnique(), ProductConstants.PRODUCT_TYPE_NORMAL);
                // 规格不存在即失效
                if (ObjectUtil.isNull(attrValue)) {
                    cartInfoResponse.setAttrStatus(false);
                    return cartInfoResponse;
                }
                if (StrUtil.isNotBlank(attrValue.getImage())) {
                    cartInfoResponse.setImage(attrValue.getImage());
                }
                cartInfoResponse.setSku(attrValue.getSku());
                cartInfoResponse.setPrice(attrValue.getPrice());
                cartInfoResponse.setAttrId(attrValue.getId());
                cartInfoResponse.setAttrStatus(attrValue.getStock() > 0);
                cartInfoResponse.setStock(attrValue.getStock());
                return cartInfoResponse;
            }).collect(Collectors.toList());
            merchantResponse.setCartInfoList(infoResponseList);
            responseList.add(merchantResponse);
        });
        return responseList;
    }

    /**
     * 购物车数量
     *
     * @param request 请求参数
     * @return Map<String, Integer>
     */
    @Override
    public Map<String, Integer> getUserCount(CartNumRequest request) {
        Integer userId = userService.getUserIdException();
        Map<String, Integer> map = new HashMap<>();
        int num;
        if (request.getType().equals("total")) {
            num = getUserCountByStatus(userId, request.getNumType());
        } else {
            num = getUserSumByStatus(userId, request.getNumType());
        }
        map.put("count", num);
        return map;
    }

    /**
     * 添加购物车
     *
     * @param request 新增购物车参数
     * @return 新增结果
     */
    @Override
    public Boolean add(CartRequest request) {
        // 判断商品正常
        Product product = productService.getById(request.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel() || !product.getIsShow()) {
            throw new CrmebException("未找到匹配的商品");
        }
        ProductAttrValue attrValue = productAttrValueService.getByProductIdAndAttrId(product.getId(),
                request.getProductAttrUnique(), ProductConstants.PRODUCT_TYPE_NORMAL);
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException("未找到相应的商品SKU");
        }
        if (attrValue.getStock() < request.getCartNum()) {
            throw new CrmebException("货物库存不足");
        }

        // 普通商品部分(只有普通商品才能添加购物车)
        // 是否已经有同类型商品在购物车，有则添加数量没有则新增
        User user = userService.getInfo();
        Cart forUpdateStoreCart = getByUniqueAndUid(request.getProductAttrUnique(), user.getId());
        Boolean execute = transactionTemplate.execute(e -> {
            if (ObjectUtil.isNotNull(forUpdateStoreCart)) { // 购物车添加数量
                forUpdateStoreCart.setCartNum(forUpdateStoreCart.getCartNum() + request.getCartNum());
                boolean updateResult = updateById(forUpdateStoreCart);
                if (!updateResult) throw new CrmebException("添加购物车失败");
            } else {// 新增购物车数据
                Cart storeCart = new Cart();
                BeanUtils.copyProperties(request, storeCart);
                storeCart.setUid(user.getId());
                storeCart.setMerId(product.getMerId());
                if (dao.insert(storeCart) <= 0) throw new CrmebException("添加购物车失败");
            }
            return Boolean.TRUE;
        });
        if (execute) {
            purchaseStatistics(request.getProductId(), request.getCartNum());
        }
        return execute;
    }

    /**
     * 新增商品至购物车
     *
     * @param cartListRequest 购物车参数
     * @return 添加后的成功标识
     */
    @Override
    public Boolean batchAdd(List<CartRequest> cartListRequest) {
        if (CollUtil.isEmpty(cartListRequest)) {
            throw new CrmebException("购物车数据为空");
        }
        List<Cart> updateCartList = new ArrayList<>();
        List<Cart> addCartList = new ArrayList<>();
        User currentUser = userService.getInfo();
        cartListRequest.forEach(cartRequest -> {
            // 判断商品正常
            Product product = productService.getById(cartRequest.getProductId());
            if (ObjectUtil.isNull(product) || product.getIsDel() || !product.getIsShow()) {
                throw new CrmebException("未找到匹配的商品");
            }
            ProductAttrValue attrValue = productAttrValueService.getByProductIdAndAttrId(cartRequest.getProductId(),
                    cartRequest.getProductAttrUnique(), ProductConstants.PRODUCT_TYPE_NORMAL);
            if (ObjectUtil.isNull(attrValue)) {
                throw new CrmebException("未找到相应的商品SKU");
            }
            if (attrValue.getStock() < cartRequest.getCartNum()) {
                throw new CrmebException("货物库存不足");
            }
            // 普通商品部分(只有普通商品才能添加购物车)
            // 是否已经有同类型商品在购物车，有则添加数量没有则新增
            Cart forUpdateStoreCart = getByUniqueAndUid(cartRequest.getProductAttrUnique(), currentUser.getId());
            if (ObjectUtil.isNotNull(forUpdateStoreCart)) { // 购物车添加数量
                forUpdateStoreCart.setCartNum(forUpdateStoreCart.getCartNum() + cartRequest.getCartNum());
                updateCartList.add(forUpdateStoreCart);
            } else {// 新增购物车数据
                Cart cart = new Cart();
                BeanUtils.copyProperties(cartRequest, cart);
                cart.setUid(currentUser.getId());
                cart.setMerId(product.getMerId());
                addCartList.add(cart);
            }
        });

        Boolean execute = transactionTemplate.execute(exec -> {
            saveBatch(addCartList);
            updateBatchById(updateCartList);
            return Boolean.TRUE;
        });
        if (execute) {
            cartListRequest.forEach(cart -> purchaseStatistics(cart.getProductId(), cart.getCartNum()));
        }
        return execute;
    }

    /**
     * 加购商品统计
     *
     * @param productId 商品id
     * @param num 加购数量
     */
    private void purchaseStatistics(Integer productId, Integer num) {
        String todayStr = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        // 商品加购量统计(每日/商城)
        redisUtil.incrAndCreate(RedisConstants.PRO_ADD_CART_KEY + todayStr, num);
        // 商品加购量统计(每日/个体)
        redisUtil.incrAndCreate(StrUtil.format(RedisConstants.PRO_PRO_ADD_CART_KEY, todayStr, productId), num);
    }

    /**
     * 获取购物车信息
     *
     * @param productAttrUnique 商品规制值
     * @param uid               uid
     * @return StoreCart
     */
    private Cart getByUniqueAndUid(Integer productAttrUnique, Integer uid) {
        LambdaQueryWrapper<Cart> lqw = Wrappers.lambdaQuery();
        lqw.eq(Cart::getProductAttrUnique, productAttrUnique);
        lqw.eq(Cart::getUid, uid);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 删除购物车信息
     *
     * @param ids 待删除id
     * @return 删除结果状态
     */
    @Override
    public Boolean deleteCartByIds(List<Integer> ids) {
        return dao.deleteBatchIds(ids) > 0;
    }

    /**
     * 检测商品是否有效 更新购物车商品状态
     *
     * @param productId 商品id
     * @return 跟新结果
     */
    @Override
    public Boolean productStatusNotEnable(Integer productId) {
        LambdaUpdateWrapper<Cart> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Cart::getStatus, false);
        wrapper.eq(Cart::getProductId, productId);
        wrapper.eq(Cart::getStatus, true);
        return update(wrapper);
    }

    /**
     * 购物车重选
     *
     * @param resetRequest 重选数据
     * @return 重选结果
     */
    @Override
    public Boolean resetCart(CartResetRequest resetRequest) {
        Cart cart = getById(resetRequest.getId());
        if (ObjectUtil.isNull(cart)) throw new CrmebException("购物车不存在");
        // 判断商品正常
        Product product = productService.getById(resetRequest.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel() || !product.getIsShow()) {
            throw new CrmebException("未找到匹配的商品");
        }
        ProductAttrValue attrValue = productAttrValueService.getByProductIdAndAttrId(product.getId(),
                resetRequest.getUnique(), ProductConstants.PRODUCT_TYPE_NORMAL);
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException("未找到相应的商品SKU");
        }
        if (attrValue.getStock() < resetRequest.getNum()) {
            throw new CrmebException("货物库存不足");
        }
        cart.setCartNum(resetRequest.getNum());
        cart.setProductAttrUnique(resetRequest.getUnique());
        cart.setStatus(true);
        cart.setMerId(product.getMerId());
        boolean updateResult = dao.updateById(cart) > 0;
        if (!updateResult) throw new CrmebException("重选添加购物车失败");
        purchaseStatistics(cart.getProductId(), resetRequest.getNum());
        return updateResult;
    }

    /**
     * 对应sku购物车生效
     *
     * @param skuIdList skuIdList
     * @return Boolean
     */
    @Override
    public Boolean productStatusNoEnable(List<Integer> skuIdList) {
        LambdaUpdateWrapper<Cart> lqw = new LambdaUpdateWrapper<>();
        lqw.set(Cart::getStatus, true);
        lqw.in(Cart::getProductAttrUnique, skuIdList);
        return update(lqw);
    }

    /**
     * 删除商品对应的购物车
     *
     * @param productId 商品id
     */
    @Override
    public Boolean productDelete(Integer productId) {
        LambdaUpdateWrapper<Cart> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Cart::getProductId, productId);
        return dao.delete(wrapper) > 0;
    }

    /**
     * 通过id和uid获取购物车信息
     *
     * @param id  购物车id
     * @param uid 用户uid
     * @return StoreCart
     */
    @Override
    public Cart getByIdAndUid(Integer id, Integer uid) {
        LambdaQueryWrapper<Cart> lqw = Wrappers.lambdaQuery();
        lqw.eq(Cart::getId, id);
        lqw.eq(Cart::getUid, uid);
        lqw.eq(Cart::getStatus, true);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 修改购物车商品数量
     *
     * @param id     购物车id
     * @param number 数量
     */
    @Override
    public Boolean updateCartNum(Integer id, Integer number) {
        if (number <= 0 || number > 999)
            throw new CrmebException("加购数不能小于1且大于999");
        Cart storeCart = getById(id);
        if (ObjectUtil.isNull(storeCart)) throw new CrmebException("当前购物车不存在");
        if (storeCart.getCartNum().equals(number)) {
            return Boolean.TRUE;
        }
        ProductAttrValue attrValue = productAttrValueService.getByProductIdAndAttrId(storeCart.getProductId(),
                storeCart.getProductAttrUnique(), ProductConstants.PRODUCT_TYPE_NORMAL);
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException("未找到相应的商品SKU");
        }
        if (attrValue.getStock() < number) {
            throw new CrmebException("货物库存不足");
        }
        storeCart.setCartNum(number);
        return updateById(storeCart);
    }

    /**
     * 购物车移入收藏
     * @param ids 购物车id列表
     * @return Boolean
     */
    @Override
    public Boolean toCollect(List<Integer> ids) {
        Integer userId = userService.getUserIdException();
        List<Cart> cartList = findListByIds(ids);
        if (CollUtil.isEmpty(cartList)) {
            throw new CrmebException("购物车不存在");
        }
        if (cartList.size() != ids.size()) {
            throw new CrmebException("购物车数据异常，请重新勾选");
        }
        List<Integer> proIdList = cartList.stream().map(Cart::getProductId).distinct().collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            deleteCartByIds(ids);
            productRelationService.deleteByUidAndProIdList(userId, proIdList);
            productRelationService.batchAdd(userId, proIdList);
            return Boolean.TRUE;
        });
    }

    /**
     * 通过用户id删除
     * @param uid 用户ID
     */
    @Override
    public Boolean deleteByUid(Integer uid) {
        LambdaUpdateWrapper<Cart> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Cart::getUid, uid);
        return remove(wrapper);
    }

    private List<Cart> findListByIds(List<Integer> ids) {
        LambdaQueryWrapper<Cart> lqw = Wrappers.lambdaQuery();
        lqw.in(Cart::getId, ids);
        return dao.selectList(lqw);
    }

    ///////////////////////////////////////////////////////////////////自定义方法

    /**
     * 购物车商品数量（条数）
     *
     * @param userId Integer 用户id
     * @param status Boolean 商品类型：true-有效商品，false-无效商品
     * @return Integer
     */
    private Integer getUserCountByStatus(Integer userId, Boolean status) {
        //购物车商品种类数量
        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Cart::getUid, userId);
        lqw.eq(Cart::getStatus, status);
        return dao.selectCount(lqw);
    }

    /**
     * 购物车购买商品总数量
     *
     * @param userId Integer 用户id
     * @param status 商品类型：true-有效商品，false-无效商品
     * @return Integer
     */
    private Integer getUserSumByStatus(Integer userId, Boolean status) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("ifnull(sum(cart_num), 0) as cart_num");
        queryWrapper.eq("uid", userId);
        queryWrapper.eq("status", status);
        Cart cart = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(cart)) {
            return 0;
        }
        return cart.getCartNum();
    }
}

