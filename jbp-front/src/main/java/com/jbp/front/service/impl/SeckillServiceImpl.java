package com.jbp.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.*;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.order.*;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductAttr;
import com.jbp.common.model.product.ProductAttrValue;
import com.jbp.common.model.product.ProductGuarantee;
import com.jbp.common.model.seckill.SeckillActivity;
import com.jbp.common.model.seckill.SeckillActivityTime;
import com.jbp.common.model.seckill.SeckillProduct;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserAddress;
import com.jbp.common.model.user.UserIntegralRecord;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.*;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.RedisUtil;
import com.jbp.common.vo.*;
import com.jbp.front.service.FrontOrderService;
import com.jbp.front.service.SeckillService;
import com.jbp.service.service.*;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 秒杀服务实现类
 *
 * @Author 指缝de阳光
 * @Date 2022/12/26 14:36
 * @Version 1.0
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private final Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);

    @Autowired
    private SeckillActivityService seckillActivityService;
    @Autowired
    private SeckillActivityTimeService seckillActivityTimeService;
    @Autowired
    private SeckillProductService seckillProductService;
    @Autowired
    private ProductAttrService productAttrService;
    @Autowired
    private ProductGuaranteeService productGuaranteeService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserAddressService userAddressService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderExtService orderExtService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private FrontOrderService frontOrderService;
    @Autowired
    private ProductRelationService productRelationService;


    /**
     * 获取首页秒杀信息
     * @return
     */
    @Override
    public List<SeckillProduct> getIndexInfo() {
        DateTime dateTime = DateUtil.date();
        String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
        String hmStr = dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM);
        List<Integer> acvitityIdList = seckillActivityTimeService.findActivityByDateTime(Integer.valueOf(dateStr), Integer.valueOf(hmStr));
        if (CollUtil.isEmpty(acvitityIdList)) {
            return null;
        }
        List<SeckillActivity> activityList = seckillActivityService.findByIdListAndOpen(acvitityIdList, true);
        if (CollUtil.isEmpty(activityList)) {
            return null;
        }
        List<Integer> aidList = activityList.stream().map(SeckillActivity::getId).collect(Collectors.toList());
        return seckillProductService.getIndexList(aidList);
    }

    /**
     * 秒杀时段信息
     */
    @Override
    public List<SeckillFrontTimeResponse> activityTimeInfo() {
        DateTime dateTime = DateUtil.date();
        String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);

        DateTime tomorrow = DateUtil.tomorrow();
        String tomorrowDateStr = tomorrow.toString(DateConstants.DATE_FORMAT_NUM);

        List<SeckillFrontTimeResponse> responsesList = CollUtil.newArrayList();
        List<SeckillActivityTime> todayTimeList = seckillActivityTimeService.findFrontByDate(Integer.valueOf(dateStr));
        if (CollUtil.isNotEmpty(todayTimeList)) {
            String todayStr = dateTime.toString(DateConstants.DATE_FORMAT_DATE);
            Integer hm = Integer.valueOf(dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM));
            todayTimeList.forEach(time -> {
                SeckillFrontTimeResponse response = new SeckillFrontTimeResponse();
                response.setDate(todayStr);
                response.setStartTime(getTimeIntervalTimeStr(String.valueOf(time.getStartTime())));
                response.setEndTime(getTimeIntervalTimeStr(String.valueOf(time.getEndTime())));
                if (hm < time.getStartTime()) {
                    response.setStatus(2);
                }
                if (hm > time.getEndTime()) {
                    response.setStatus(0);
                }
                if (hm >= time.getStartTime() && hm <= time.getEndTime()) {
                    response.setStatus(1);
                }
                responsesList.add(response);
            });
        }

        List<SeckillActivityTime> tomTimeList = seckillActivityTimeService.findFrontByDate(Integer.valueOf(tomorrowDateStr));
        if (CollUtil.isNotEmpty(tomTimeList)) {
            String tomorrowStr = tomorrow.toString(DateConstants.DATE_FORMAT_DATE);
            todayTimeList.forEach(time -> {
                SeckillFrontTimeResponse response = new SeckillFrontTimeResponse();
                response.setDate(tomorrowStr);
                response.setStartTime(getTimeIntervalTimeStr(String.valueOf(time.getStartTime())));
                response.setEndTime(getTimeIntervalTimeStr(String.valueOf(time.getEndTime())));
                response.setStatus(3);
                responsesList.add(response);
            });
        }
        responsesList.forEach(this::setTimeStamp);
        return responsesList;
    }

    /**
     * 设置时间戳
     */
    private void setTimeStamp(SeckillFrontTimeResponse response) {
        String date = response.getDate();
        String start = date + " " + response.getStartTime() + ":00";
        String end = date + " " + response.getEndTime() + ":00";
        response.setStartTimeStamp(DateUtil.parse(start).getTime());
        response.setEndTimeStamp(DateUtil.parse(end).getTime());
    }

    /**
     * 秒杀商品列表
     * @param request 搜索参数
     * @param pageRequest 分页参数
     * @return
     */
    @Override
    public PageInfo<SeckillProductFrontResponse> getProductList(SeckillProductFrontSearchRequest request, PageParamRequest pageRequest) {
        String dateStr = DateUtil.parse(request.getDate()).toString(DateConstants.DATE_FORMAT_NUM);
        Integer startTime = getIntervalTimeInt(request.getStartTime());
        Integer endTime = getIntervalTimeInt(request.getEndTime());
        List<Integer> seckillIdList = seckillActivityTimeService.findActivityByDateAndTime(Integer.valueOf(dateStr), startTime, endTime);
        if (CollUtil.isEmpty(seckillIdList)) {
            return new PageInfo<>();
        }
        PageInfo<SeckillProduct> pageInfo = seckillProductService.getFrontPage(seckillIdList, pageRequest);
        List<SeckillProduct> seckillProductList = pageInfo.getList();
        if (CollUtil.isEmpty(seckillProductList)) {
            return new PageInfo<>();
        }
        List<Integer> merIdList = seckillProductList.stream().map(SeckillProduct::getMerId).collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        List<SeckillProductFrontResponse> responseList = seckillProductList.stream().map(product -> {
            SeckillProductFrontResponse response = new SeckillProductFrontResponse();
            BeanUtils.copyProperties(product, response);
            BigDecimal divide = new BigDecimal(String.valueOf((product.getQuotaShow() - product.getQuota()))).divide(new BigDecimal(product.getQuotaShow().toString()), 2, BigDecimal.ROUND_HALF_UP);
            int range = divide.multiply(new BigDecimal("100")).intValue();
            response.setPayRange(range + "%");
            response.setIsSelf(merchantMap.get(product.getMerId()).getIsSelf());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 获取秒杀商品详情
     * @param id 秒杀商品ID
     * @return 秒杀商品详情
     * 1。查询redis
     * 2. redis没有查询数据库
     * 3. 如果处于秒杀中存到redis，如果秒杀结束清除缓存
     * 4. 查询其他内容
     * 5. 组装返回
     *
     * 备注：
     * 1.redis按秒杀数据存储，拿到后组装格式返回
     * 2.商品下架、商品强制下架、秒杀活动结束、编辑商品、活动编辑时清除缓存
     * 3.缓存内容区分商品信息，商品库存信息两部分分开存储
     * 4.商品库存信息分为两部分保存：a.商品总库存,b.商品sku库存
     * String seckillInfoKey = StrUtil.format(RedisConstants.SECKILL_PRODUCT_INFO_KEY, id);
     *         if (redisUtil.exists(seckillInfoKey)) {
     *             // 获取秒杀商品信息，组装ProductDetailResponse
     *             Object o = redisUtil.get(seckillInfoKey);
     *         }
     */
//    @Override
    public ProductDetailResponse getProductDetailAwaitAbandoned(Integer id) {
        SeckillProduct seckillProduct = seckillProductService.getFrontDetail(id);
        Product product = new Product();
        BeanUtils.copyProperties(seckillProduct, product);
        product.setPrice(seckillProduct.getSeckillPrice());
        product.setOtPrice(seckillProduct.getPrice());
        product.setStock(seckillProduct.getQuota());
        product.setSales(seckillProduct.getQuotaShow() - seckillProduct.getQuota());
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        productDetailResponse.setProductInfo(product);
        if (StrUtil.isNotBlank(product.getGuaranteeIds())) {
            productDetailResponse.setGuaranteeList(productGuaranteeService.findByIdList(CrmebUtil.stringToArray(seckillProduct.getGuaranteeIds())));
        }
        // 获取商品规格
        List<ProductAttr> attrList = productAttrService.getListByProductIdAndType(seckillProduct.getId(), ProductConstants.PRODUCT_TYPE_SECKILL);
        // 根据制式设置attr属性
        productDetailResponse.setProductAttr(attrList);
        // 根据制式设置sku属性
        LinkedHashMap<String, ProductAttrValueResponse> skuMap = new LinkedHashMap<>();
        List<ProductAttrValue> productAttrValueList = productAttrValueService.getListByProductIdAndType(seckillProduct.getId(), ProductConstants.PRODUCT_TYPE_SECKILL);
        for (ProductAttrValue productAttrValue : productAttrValueList) {
            ProductAttrValueResponse atr = new ProductAttrValueResponse();
            BeanUtils.copyProperties(productAttrValue, atr);
            atr.setStock(productAttrValue.getQuota());
            atr.setSales(productAttrValue.getQuotaShow() - productAttrValue.getQuota());
            skuMap.put(atr.getSku(), atr);
        }
        productDetailResponse.setProductValue(skuMap);

        // 获取商户信息
        Merchant merchant = merchantService.getById(product.getMerId());
        ProductMerchantResponse merchantResponse = new ProductMerchantResponse();
        BeanUtils.copyProperties(merchant, merchantResponse);
        merchantResponse.setCollectNum(userMerchantCollectService.getCountByMerId(merchant.getId()));
        // 获取商户推荐商品
        List<ProMerchantProductResponse> merchantProductResponseList = productService.getRecommendedProductsByMerId(merchant.getId(), 4);
        merchantResponse.setProList(merchantProductResponseList);
        productDetailResponse.setMerchantInfo(merchantResponse);
        // 秒杀信息
        SeckillActivity seckillActivity = seckillActivityService.getById(seckillProduct.getActivityId());
        productDetailResponse.setOneQuota(seckillActivity.getOneQuota());

        // 秒杀时段
        List<SeckillActivityTime> timeList = seckillActivityTimeService.findByActivityId(seckillActivity.getId());
        DateTime dateTime = DateUtil.date();
        String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
        SeckillActivityTime seckillActivityTime = timeList.get(0);
        if (seckillActivityTime.getStartDate() <= Integer.parseInt(dateStr) && Integer.parseInt(dateStr) <= seckillActivityTime.getEndDate()) {
            int timeInt = Integer.parseInt(dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM));
            SeckillActivityTime activityTime = timeList.stream().filter(e -> e.getStartTime() <= timeInt && timeInt <= e.getEndTime()).findFirst().orElse(null);
            if (ObjectUtil.isNotNull(activityTime)) {// 当前商品正在秒杀中
                String startTimeStr = getTimeIntervalTimeStr(String.valueOf(activityTime.getStartTime()));
                String endTimeStr = getTimeIntervalTimeStr(String.valueOf(activityTime.getEndTime()));
                productDetailResponse.setStartTimeStamp(DateUtil.parse(dateTime.toString(DateConstants.DATE_FORMAT_DATE) + " " + startTimeStr + ":00").getTime());
                productDetailResponse.setEndTimeStamp(DateUtil.parse(dateTime.toString(DateConstants.DATE_FORMAT_DATE) + " " + endTimeStr + ":00").getTime());
            } else {// 当前商品未开始秒杀
                List<SeckillActivityTime> waitTimeList = timeList.stream().filter(e -> e.getStartTime() >= timeInt).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(waitTimeList)) {
                    SeckillActivityTime waitTime = waitTimeList.stream().min(Comparator.comparing(SeckillActivityTime::getStartTime)).get();
                    if (ObjectUtil.isNotNull(waitTime)) {
                        String startTimeStr = getTimeIntervalTimeStr(String.valueOf(waitTime.getStartTime()));
                        String endTimeStr = getTimeIntervalTimeStr(String.valueOf(waitTime.getEndTime()));
                        productDetailResponse.setStartTimeStamp(DateUtil.parse(dateTime.toString(DateConstants.DATE_FORMAT_DATE) + " " + startTimeStr + ":00").getTime());
                        productDetailResponse.setEndTimeStamp(DateUtil.parse(dateTime.toString(DateConstants.DATE_FORMAT_DATE) + " " + endTimeStr + ":00").getTime());
                    }
                }
            }
        }

        String productQuotaKey = StrUtil.format(RedisConstants.SECKILL_PRODUCT_QUOTA_KEY, seckillProduct.getId());
        if (!redisUtil.exists(productQuotaKey)) {
            // 秒杀库存存入缓存
            redisUtil.incrAndCreate(productQuotaKey, seckillProduct.getQuota());
            productAttrValueList.forEach(sku -> {
                redisUtil.incrAndCreate(StrUtil.format(RedisConstants.SECKILL_PRODUCT_SKU_QUOTA_KEY, sku.getId()), sku.getQuota());
            });
        }
        productDetailResponse.setMasterProductId(seckillProduct.getProductId());
        // 获取用户
        Integer userId = userService.getUserId();
        productDetailResponse.setUserCollect(false);
        // 用户收藏
        if (userId > 0) {
            // 查询用户是否收藏收藏
            productDetailResponse.setUserCollect(productRelationService.existCollectByUser(userId, seckillProduct.getProductId()));
        }
        return productDetailResponse;
    }

    /**
     * 获取秒杀商品详情
     * @param id 秒杀商品ID
     * @return 秒杀商品详情
     * 1。查询redis
     * 2. redis没有查询数据库
     * 3. 如果处于秒杀中存到redis，如果秒杀结束清除缓存
     * 4. 查询其他内容
     * 5. 组装返回
     *
     * 备注：
     * 1.redis按秒杀数据存储，拿到后组装格式返回
     * 2.缓存内容区分商品信息，商品库存信息两部分分开存储
     * 3.商品库存信息分为两部分保存：a.商品总库存,b.商品sku库存
     *
     * 更新缓存的节点：
     * 1.商品下架
     * 2.商品强制下架
     * 3.编辑商品
     * 4.删除商品
     * 5.秒杀活动结束
     * 7.秒杀活动关闭
     *
     */
    @Override
    public ProductDetailResponse getProductDetail(Integer id) {
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        boolean isRedis = false;// 是否需要缓存数据
        SeckillProduct seckillProduct;
        List<ProductAttr> productAttrList;
        List<ProductAttrValue> productAttrValueList;
        List<ProductGuarantee> productGuaranteeList = CollUtil.newArrayList();
        Integer oneQuota = 0;
        String seckillProductInfoKey = StrUtil.format(RedisConstants.SECKILL_PRODUCT_INFO_KEY, id);
        // 秒杀信息
        if (redisUtil.exists(seckillProductInfoKey)) {
            // 获取秒杀商品信息，组装ProductDetailResponse
            SeckillProductInfoCacheVo seckillProductInfoCacheVo = redisUtil.get(seckillProductInfoKey);
            seckillProduct = seckillProductInfoCacheVo.getSeckillProduct();
            productAttrList = seckillProductInfoCacheVo.getProductAttrList();
            productAttrValueList = seckillProductInfoCacheVo.getProductAttrValueList();
            if (StrUtil.isNotBlank(seckillProduct.getGuaranteeIds())) {
                productGuaranteeList = seckillProductInfoCacheVo.getGuaranteeList();
            }
            oneQuota = seckillProductInfoCacheVo.getOneQuota();
            String productQuotaKey = StrUtil.format(RedisConstants.SECKILL_PRODUCT_QUOTA_KEY, seckillProduct.getId());
            Integer seckillProductQuota = redisUtil.get(productQuotaKey);
            seckillProduct.setQuota(seckillProductQuota);
            productAttrValueList.forEach(value -> {
                String skuQuotaKey = StrUtil.format(RedisConstants.SECKILL_PRODUCT_SKU_QUOTA_KEY, value.getId());
                Integer skuQuota = redisUtil.get(skuQuotaKey);
                value.setQuota(skuQuota);
            });
        } else {
            seckillProduct = seckillProductService.getFrontDetail(id);
            // 获取商品规格
            productAttrList = productAttrService.getListByProductIdAndType(seckillProduct.getId(), ProductConstants.PRODUCT_TYPE_SECKILL);
            // 根据制式设置sku属性
            productAttrValueList = productAttrValueService.getListByProductIdAndType(seckillProduct.getId(), ProductConstants.PRODUCT_TYPE_SECKILL);
            if (StrUtil.isNotBlank(seckillProduct.getGuaranteeIds())) {
                productGuaranteeList = productGuaranteeService.findByIdList(CrmebUtil.stringToArray(seckillProduct.getGuaranteeIds()));
            }
            SeckillActivity seckillActivity = seckillActivityService.getById(seckillProduct.getActivityId());
            oneQuota = seckillActivity.getOneQuota();
            isRedis = true;
        }
        // 包装返回信息
        Product product = new Product();
        BeanUtils.copyProperties(seckillProduct, product);
        product.setPrice(seckillProduct.getSeckillPrice());
        product.setOtPrice(seckillProduct.getPrice());
        product.setStock(seckillProduct.getQuota());
        product.setSales(seckillProduct.getQuotaShow() - seckillProduct.getQuota());
        productDetailResponse.setProductInfo(product);
        if (CollUtil.isNotEmpty(productGuaranteeList)) {
            productDetailResponse.setGuaranteeList(productGuaranteeList);
        }
        productDetailResponse.setProductAttr(productAttrList);

        LinkedHashMap<String, ProductAttrValueResponse> skuMap = new LinkedHashMap<>();
        for (ProductAttrValue productAttrValue : productAttrValueList) {
            ProductAttrValueResponse atr = new ProductAttrValueResponse();
            BeanUtils.copyProperties(productAttrValue, atr);
            atr.setStock(productAttrValue.getQuota());
            atr.setSales(productAttrValue.getQuotaShow() - productAttrValue.getQuota());
            skuMap.put(atr.getSku(), atr);
        }
        productDetailResponse.setProductValue(skuMap);
        productDetailResponse.setOneQuota(oneQuota);

        // 秒杀时段
        List<SeckillActivityTime> timeList = seckillActivityTimeService.findByActivityId(seckillProduct.getActivityId());
        DateTime dateTime = DateUtil.date();
        String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
        SeckillActivityTime seckillActivityTime = timeList.get(0);
        if (seckillActivityTime.getStartDate() <= Integer.parseInt(dateStr) && Integer.parseInt(dateStr) <= seckillActivityTime.getEndDate()) {
            int timeInt = Integer.parseInt(dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM));
            SeckillActivityTime activityTime = timeList.stream().filter(e -> e.getStartTime() <= timeInt && timeInt <= e.getEndTime()).findFirst().orElse(null);
            if (ObjectUtil.isNotNull(activityTime)) {// 当前商品正在秒杀中
                String startTimeStr = getTimeIntervalTimeStr(String.valueOf(activityTime.getStartTime()));
                String endTimeStr = getTimeIntervalTimeStr(String.valueOf(activityTime.getEndTime()));
                productDetailResponse.setStartTimeStamp(DateUtil.parse(dateTime.toString(DateConstants.DATE_FORMAT_DATE) + " " + startTimeStr + ":00").getTime());
                productDetailResponse.setEndTimeStamp(DateUtil.parse(dateTime.toString(DateConstants.DATE_FORMAT_DATE) + " " + endTimeStr + ":00").getTime());
            } else {// 当前商品未开始秒杀
                List<SeckillActivityTime> waitTimeList = timeList.stream().filter(e -> e.getStartTime() >= timeInt).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(waitTimeList)) {
                    SeckillActivityTime waitTime = waitTimeList.stream().min(Comparator.comparing(SeckillActivityTime::getStartTime)).get();
                    if (ObjectUtil.isNotNull(waitTime)) {
                        String startTimeStr = getTimeIntervalTimeStr(String.valueOf(waitTime.getStartTime()));
                        String endTimeStr = getTimeIntervalTimeStr(String.valueOf(waitTime.getEndTime()));
                        productDetailResponse.setStartTimeStamp(DateUtil.parse(dateTime.toString(DateConstants.DATE_FORMAT_DATE) + " " + startTimeStr + ":00").getTime());
                        productDetailResponse.setEndTimeStamp(DateUtil.parse(dateTime.toString(DateConstants.DATE_FORMAT_DATE) + " " + endTimeStr + ":00").getTime());
                    }
                }
            }
        }
        // 获取商户信息
        Merchant merchant = merchantService.getById(productDetailResponse.getProductInfo().getMerId());
        ProductMerchantResponse merchantResponse = new ProductMerchantResponse();
        BeanUtils.copyProperties(merchant, merchantResponse);
        merchantResponse.setCollectNum(userMerchantCollectService.getCountByMerId(merchant.getId()));
        // 获取商户推荐商品
        List<ProMerchantProductResponse> merchantProductResponseList = productService.getRecommendedProductsByMerId(merchant.getId(), 4);
        merchantResponse.setProList(merchantProductResponseList);
        // 获取用户
        Integer userId = userService.getUserId();
        productDetailResponse.setUserCollect(false);
        // 用户收藏
        if (userId > 0) {
            merchantResponse.setIsCollect(userMerchantCollectService.isCollect(userId, merchant.getId()));
            // 查询用户是否收藏收藏
            productDetailResponse.setUserCollect(productRelationService.existCollectByUser(userId, seckillProduct.getProductId()));
        }
        productDetailResponse.setMerchantInfo(merchantResponse);
        productDetailResponse.setMasterProductId(seckillProduct.getProductId());

        if (isRedis) {
            // 缓存秒杀商品信息
            if (!redisUtil.exists(seckillProductInfoKey)) {
                SeckillProductInfoCacheVo cacheVo = new SeckillProductInfoCacheVo();
                cacheVo.setSeckillProduct(seckillProduct);
                cacheVo.setProductAttrList(productAttrList);
                cacheVo.setProductAttrValueList(productAttrValueList);
                cacheVo.setGuaranteeList(productGuaranteeList);
                cacheVo.setOneQuota(oneQuota);
                // 缓存秒杀商品数据
                redisUtil.set(seckillProductInfoKey, cacheVo);
                // 缓存秒杀库存信息
                String productQuotaKey = StrUtil.format(RedisConstants.SECKILL_PRODUCT_QUOTA_KEY, seckillProduct.getId());
                if (!redisUtil.exists(productQuotaKey)) {
                    // 秒杀库存存入缓存
                    redisUtil.incrAndCreate(productQuotaKey, seckillProduct.getQuota());
                    productAttrValueList.forEach(sku -> {
                        redisUtil.incrAndCreate(StrUtil.format(RedisConstants.SECKILL_PRODUCT_SKU_QUOTA_KEY, sku.getId()), sku.getQuota());
                    });
                }
            }
        }
        return productDetailResponse;
    }

    /**
     * 秒杀预下单校验
     * @param detailRequest 商品参数
     * @return PreMerchantOrderVo
     */
    @Override
    public PreMerchantOrderVo validatePreOrderSeckill(PreOrderDetailRequest detailRequest) {
        if (ObjectUtil.isNull(detailRequest.getProductId()) || detailRequest.getProductId() <= 0) {
            throw new CrmebException("请选择秒杀商品");
        }
        if (ObjectUtil.isNull(detailRequest.getAttrValueId())) {
            throw new CrmebException("商品规格属性值不能为空");
        }
        if (ObjectUtil.isNull(detailRequest.getProductNum()) || detailRequest.getProductNum() <= 0) {
            throw new CrmebException("购买数量必须大于0");
        }
        // 查询商品信息
        String seckillProductInfoKey = StrUtil.format(RedisConstants.SECKILL_PRODUCT_INFO_KEY, detailRequest.getProductId());
        if (!redisUtil.exists(seckillProductInfoKey)) {
            throw new CrmebException("商品信息不存在，请刷新后重新选择");
        }
        SeckillProductInfoCacheVo seckillProductInfoCacheVo = redisUtil.get(seckillProductInfoKey);
        SeckillProduct seckillProduct = seckillProductInfoCacheVo.getSeckillProduct();
        Product product = productService.getById(seckillProduct.getProductId());
        if (ObjectUtil.isNull(seckillProduct) || seckillProduct.getIsDel()) {
            throw new CrmebException("商品信息不存在，请刷新后重新选择");
        }
        if (!seckillProduct.getIsShow()) {
            throw new CrmebException("商品已下架，请刷新后重新选择");
        }
        String productQuotaKey = StrUtil.format(RedisConstants.SECKILL_PRODUCT_QUOTA_KEY, seckillProduct.getId());
        Integer seckillProductQuota = redisUtil.get(productQuotaKey);
        seckillProduct.setQuota(seckillProductQuota);
        if (seckillProduct.getQuota() < detailRequest.getProductNum()) {
            throw new CrmebException("商品库存不足，请刷新后重新选择");
        }
        // 查询秒杀活动
        SeckillActivity seckillActivity = seckillActivityService.getById(seckillProduct.getActivityId());
        if (ObjectUtil.isNull(seckillActivity) || seckillActivity.getIsDel() || seckillActivity.getIsOpen().equals(0)) {
            throw new CrmebException("秒杀活动不存在");
        }
        if (seckillActivity.getStatus().equals(2)) {
            throw new CrmebException("秒杀活动已结束");
        }
        Boolean inProgressNow = seckillActivityTimeService.isInProgressNow(seckillActivity.getId());
        if (!inProgressNow) {
            throw new CrmebException("秒杀活动已结束");
        }
        if (seckillActivity.getOneQuota() > 0 && seckillActivity.getOneQuota() < detailRequest.getProductNum()) {
            throw new CrmebException(StrUtil.format("此商品单次秒杀最多可购买{}件", seckillActivity.getOneQuota()));
        }
        Integer uid = userService.getUserId();
        if (seckillActivity.getAllQuota() > 0) {
            Integer productNumCount = orderService.getProductNumCount(uid, seckillProduct.getId(), ProductConstants.PRODUCT_TYPE_SECKILL);
            if ((seckillActivity.getAllQuota() - productNumCount) < detailRequest.getProductNum()) {
                throw new CrmebException(StrUtil.format("此商品整场秒杀活动还可买{}件", seckillActivity.getAllQuota() - productNumCount));
            }
        }
        // 查询商品规格属性值信息
        List<ProductAttrValue> productAttrValueList = seckillProductInfoCacheVo.getProductAttrValueList();
        ProductAttrValue attrValue = productAttrValueList.stream().filter(e -> e.getId().equals(detailRequest.getAttrValueId())).findFirst().orElse(null);
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException("商品规格信息不存在，请刷新后重新选择");
        }
        String skuQuotaKey = StrUtil.format(RedisConstants.SECKILL_PRODUCT_SKU_QUOTA_KEY, attrValue.getId());
        Integer skuQuota = redisUtil.get(skuQuotaKey);
        attrValue.setQuota(skuQuota);
        if (attrValue.getQuota() < detailRequest.getProductNum()) {
            throw new CrmebException("商品规格库存不足，请刷新后重新选择");
        }
        Merchant merchant = merchantService.getByIdException(seckillProduct.getMerId());
        if (!merchant.getIsSwitch()) {
            throw new CrmebException("商户已关闭，请重新选择商品");
        }

        PreMerchantOrderVo merchantOrderVo = new PreMerchantOrderVo();
        merchantOrderVo.setMerId(merchant.getId());
        merchantOrderVo.setMerName(merchant.getName());
        merchantOrderVo.setFreightFee(BigDecimal.ZERO);
        merchantOrderVo.setMerCouponFee(BigDecimal.ZERO);
        merchantOrderVo.setPlatCouponFee(BigDecimal.ZERO);
        merchantOrderVo.setCouponFee(merchantOrderVo.getMerCouponFee().add(merchantOrderVo.getPlatCouponFee()));
        merchantOrderVo.setUserCouponId(0);
        merchantOrderVo.setTakeTheirSwitch(merchant.getIsTakeTheir());
        merchantOrderVo.setIsSelf(merchant.getIsSelf());
        merchantOrderVo.setWalletDeductionFee(BigDecimal.ZERO);
        merchantOrderVo.setPayGateway(product.getPayType());

        PreOrderInfoDetailVo detailVo = new PreOrderInfoDetailVo();
        detailVo.setProductId(seckillProduct.getId());
        detailVo.setProductName(seckillProduct.getName());
        detailVo.setPayGateway(product.getPayType());
        detailVo.setAttrValueId(attrValue.getId());
        detailVo.setSku(attrValue.getSku());
        detailVo.setPrice(attrValue.getPrice());
        detailVo.setPayPrice(attrValue.getPrice());
        detailVo.setPayNum(detailRequest.getProductNum());
        detailVo.setImage(StrUtil.isNotBlank(attrValue.getImage()) ? attrValue.getImage() : seckillProduct.getImage());
        detailVo.setVolume(attrValue.getVolume());
        detailVo.setWeight(attrValue.getWeight());
        detailVo.setTempId(seckillProduct.getTempId());
        detailVo.setBarCode(attrValue.getBarCode());
        detailVo.setWalletDeductionFee(BigDecimal.ZERO);
        detailVo.setMerCouponPrice(BigDecimal.ZERO);
        detailVo.setPlatCouponPrice(BigDecimal.ZERO);
        detailVo.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
        detailVo.setSubBrokerageType(0);// 秒杀不参与分佣
        detailVo.setBrokerage(0);
        detailVo.setBrokerageTwo(0);
        detailVo.setProductType(ProductConstants.PRODUCT_TYPE_SECKILL);
        List<PreOrderInfoDetailVo> infoList = CollUtil.newArrayList();
        infoList.add(detailVo);
        merchantOrderVo.setOrderInfoList(infoList);
        return merchantOrderVo;
    }

    /**
     * 秒杀创建订单库存校验
     * @param orderInfoVo 预下单信息
     * @return
     */
    @Override
    public MyRecord validateCreateOrderProductStock(PreOrderInfoVo orderInfoVo) {
        PreMerchantOrderVo merchantOrderVo = orderInfoVo.getMerchantOrderVoList().get(0);
        Merchant merchant = merchantService.getByIdException(merchantOrderVo.getMerId());
        if (!merchant.getIsSwitch()) {
            throw new CrmebException("商户已关闭，请重新下单");
        }
        PreOrderInfoDetailVo info = merchantOrderVo.getOrderInfoList().get(0);
        // 查询商品信息
        SeckillProduct seckillProduct = seckillProductService.getById(info.getProductId());
        if (ObjectUtil.isNull(seckillProduct) || seckillProduct.getIsDel()) {
            throw new CrmebException("购买的商品信息不存在");
        }
        if (!seckillProduct.getIsShow()) {
            throw new CrmebException("购买的商品已下架");
        }
        if (seckillProduct.getQuota().equals(0) || info.getPayNum() > seckillProduct.getQuota()) {
            throw new CrmebException("购买的商品库存不足");
        }
        // 查询秒杀活动
        SeckillActivity seckillActivity = seckillActivityService.getById(seckillProduct.getActivityId());
        if (ObjectUtil.isNull(seckillActivity) || seckillActivity.getIsDel() || seckillActivity.getIsOpen().equals(0)) {
            throw new CrmebException("秒杀活动不存在");
        }
        if (seckillActivity.getStatus().equals(2)) {
            throw new CrmebException("秒杀活动已结束");
        }
        Boolean inProgressNow = seckillActivityTimeService.isInProgressNow(seckillActivity.getId());
        if (!inProgressNow) {
            throw new CrmebException("秒杀活动已结束");
        }
        if (seckillActivity.getOneQuota() > 0 && seckillActivity.getOneQuota() < info.getPayNum()) {
            throw new CrmebException(StrUtil.format("此商品单次秒杀最多可购买{}件", seckillActivity.getOneQuota()));
        }
        Integer uid = userService.getUserId();
        if (seckillActivity.getAllQuota() > 0) {
            Integer productNumCount = orderService.getProductNumCount(uid, seckillProduct.getId(), ProductConstants.PRODUCT_TYPE_SECKILL);
            if ((seckillActivity.getAllQuota() - productNumCount) < info.getPayNum()) {
                throw new CrmebException(StrUtil.format("此商品整场秒杀活动还可买{}件", seckillActivity.getAllQuota() - productNumCount));
            }
        }
        // 查询商品规格属性值信息
        ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(info.getAttrValueId(), info.getProductId(), ProductConstants.PRODUCT_TYPE_SECKILL);
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException("购买的商品规格信息不存在");
        }
        if (attrValue.getQuota() < info.getPayNum()) {
            throw new CrmebException("购买的商品库存不足");
        }
        MyRecord record = new MyRecord();
        record.set("productId", info.getProductId());
        record.set("num", info.getPayNum());
        record.set("attrValueId", info.getAttrValueId());
        record.set("attrValueVersion", attrValue.getVersion());
        return record;
    }

    /**
     * 秒杀下单扣减库存
     * @param skuRecord sku参数
     */
    @Override
    public void subStock(MyRecord skuRecord) {
        seckillProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
        productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, ProductConstants.PRODUCT_TYPE_SECKILL, skuRecord.getInt("attrValueVersion"));
    }

    /**
     * 创建秒杀订单
     * @param orderRequest 下单请求对象
     * @param orderInfoVo 预下单缓存对象
     * @param user 用户信息
     */
    @Override
    public OrderNoResponse createOrder(CreateOrderRequest orderRequest, PreOrderInfoVo orderInfoVo, User user) {
        UserAddress userAddress = null;
        OrderMerchantRequest orderMerchantRequest = orderRequest.getOrderMerchantRequestList().get(0);
        if (orderMerchantRequest.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_EXPRESS)) {
            if (ObjectUtil.isNull(orderRequest.getAddressId())) {
                throw new CrmebException("请选择收货地址");
            }
            userAddress = userAddressService.getById(orderRequest.getAddressId());
            if (ObjectUtil.isNull(userAddress) || userAddress.getIsDel()) {
                throw new CrmebException("收货地址有误");
            }
        }
        if (orderMerchantRequest.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
            Merchant merchant = merchantService.getByIdException(orderMerchantRequest.getMerId());
            if (!merchant.getIsTakeTheir()) {
                throw new CrmebException("请先联系商户管理员开启门店自提");
            }
        }
        PreMerchantOrderVo merchantOrderVo = orderInfoVo.getMerchantOrderVoList().get(0);
        PreOrderInfoDetailVo detailVo = merchantOrderVo.getOrderInfoList().get(0);

        // 校验商品库存
        String seckillProductQuotaKey = StrUtil.format(RedisConstants.SECKILL_PRODUCT_QUOTA_KEY, detailVo.getProductId());
        if (!redisUtil.exists(seckillProductQuotaKey)) {
            throw new CrmebException("未找到秒杀商品缓存，请刷新后再试");
        }
        Integer productQuota = redisUtil.get(seckillProductQuotaKey);
        if (productQuota <= 0) {
            throw new CrmebException("秒杀商品已售罄");
        }
        if (productQuota - detailVo.getPayNum() < 0) {
            throw new CrmebException("秒杀商品库存不足");
        }
        String seckillProductSkuQuotaKey = StrUtil.format(RedisConstants.SECKILL_PRODUCT_SKU_QUOTA_KEY, detailVo.getAttrValueId());
        Integer productSkuQuota = redisUtil.get(seckillProductSkuQuotaKey);
        if (productSkuQuota <= 0) {
            throw new CrmebException("秒杀商品规格库存不足");
        }
        if (productSkuQuota - detailVo.getPayNum() < 0) {
            throw new CrmebException("秒杀商品规格库存不足");
        }

        MyRecord skuRecord = validateCreateOrderProductStock(orderInfoVo);

        // 计算订单各种价格
        frontOrderService.getFreightFee(orderInfoVo, userAddress);
        frontOrderService.getCouponFee(orderInfoVo, orderRequest.getOrderMerchantRequestList(), user.getId());
        if (orderRequest.getIsUseIntegral() && user.getIntegral() > 0) {// 使用积分
            frontOrderService.integralDeductionComputed(orderInfoVo, user.getIntegral());
        }


        // 平台订单
        Order order = new Order();
        String orderNo = CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_PLATFORM);
        order.setOrderNo(orderNo);
        order.setMerId(0);
        order.setUid(user.getId());
        order.setPayUid(user.getId());
        order.setTotalNum(orderInfoVo.getOrderProNum());
        order.setProTotalPrice(orderInfoVo.getProTotalFee());
        order.setTotalPostage(orderInfoVo.getFreightFee());
        order.setTotalPrice(order.getProTotalPrice().add(order.getTotalPostage()));
        order.setCouponPrice(orderInfoVo.getCouponFee());
        order.setUseIntegral(merchantOrderVo.getUseIntegral());
        order.setIntegralPrice(merchantOrderVo.getIntegralPrice());
        order.setPayPrice(order.getProTotalPrice().add(order.getTotalPostage()).subtract(order.getCouponPrice()).subtract(order.getIntegralPrice()));
        order.setPayPostage(order.getTotalPostage());
        order.setPaid(false);
        order.setCancelStatus(OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
        order.setLevel(OrderConstants.ORDER_LEVEL_PLATFORM);
        order.setType(orderInfoVo.getType());// 默认普通订单

        // 订单扩展信息
        OrderExt orderExt = orderInfoVo.getOrderExt();
        orderExt.setOrderNo(order.getOrderNo());

        // 商户订单
        List<Integer> couponIdList = CollUtil.newArrayList();
        // 秒杀只会有一个商户订单
        MerchantOrder merchantOrder = new MerchantOrder();
        merchantOrder.setOrderNo(order.getOrderNo());
        merchantOrder.setMerId(merchantOrderVo.getMerId());
        merchantOrder.setUid(user.getId());
        if (StrUtil.isNotBlank(orderMerchantRequest.getRemark())) {
            merchantOrder.setUserRemark(orderMerchantRequest.getRemark());
        }
        merchantOrder.setShippingType(orderMerchantRequest.getShippingType());
        if (merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
            merchantOrder.setUserAddress(merchantOrderVo.getMerName());
            merchantOrder.setVerifyCode(String.valueOf(CrmebUtil.randomCount(1111111111, 999999999)));
        } else {
            merchantOrder.setRealName(userAddress.getRealName());
            merchantOrder.setUserPhone(userAddress.getPhone());
            String userAddressStr = userAddress.getProvince() + userAddress.getCity() + userAddress.getDistrict() + userAddress.getStreet() + userAddress.getDetail();
            merchantOrder.setUserAddress(userAddressStr);
        }
        merchantOrder.setTotalNum(merchantOrderVo.getProTotalNum());
        merchantOrder.setProTotalPrice(merchantOrderVo.getProTotalFee());
        merchantOrder.setTotalPostage(merchantOrderVo.getFreightFee());
        merchantOrder.setTotalPrice(merchantOrder.getProTotalPrice().add(merchantOrder.getTotalPostage()));
        merchantOrder.setPayPostage(merchantOrder.getTotalPostage());
        merchantOrder.setUseIntegral(merchantOrderVo.getUseIntegral());
        merchantOrder.setIntegralPrice(merchantOrderVo.getIntegralPrice());
        merchantOrder.setCouponId(merchantOrderVo.getUserCouponId());
        if (merchantOrder.getCouponId() > 0) {
            couponIdList.add(merchantOrder.getCouponId());
        }
        merchantOrder.setCouponPrice(merchantOrderVo.getCouponFee());
        merchantOrder.setPayPrice(merchantOrder.getTotalPrice().subtract(merchantOrder.getCouponPrice()).subtract(merchantOrder.getIntegralPrice()));
        merchantOrder.setGainIntegral(0);
        merchantOrder.setType(order.getType());


        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderNo(order.getOrderNo());
        orderDetail.setMerId(merchantOrder.getMerId());
        orderDetail.setUid(user.getId());
        orderDetail.setProductId(detailVo.getProductId());
        orderDetail.setProductName(detailVo.getProductName());
        orderDetail.setImage(detailVo.getImage());
        orderDetail.setAttrValueId(detailVo.getAttrValueId());
        orderDetail.setSku(detailVo.getSku());
        orderDetail.setPrice(detailVo.getPrice());
        orderDetail.setPayNum(detailVo.getPayNum());
        orderDetail.setWeight(detailVo.getWeight());
        orderDetail.setBarCode(detailVo.getBarCode());
        orderDetail.setVolume(detailVo.getVolume());
        orderDetail.setProductType(detailVo.getProductType());
        orderDetail.setSubBrokerageType(detailVo.getSubBrokerageType());
        orderDetail.setBrokerage(detailVo.getBrokerage());
        orderDetail.setBrokerageTwo(detailVo.getBrokerageTwo());
        orderDetail.setFreightFee(detailVo.getFreightFee());
        orderDetail.setCouponPrice(detailVo.getCouponPrice());
        orderDetail.setUseIntegral(detailVo.getUseIntegral());
        orderDetail.setIntegralPrice(detailVo.getIntegralPrice());
        orderDetail.setPayPrice(BigDecimal.ZERO);
        BigDecimal detailPayPrice = orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getPayNum().toString())).add(orderDetail.getFreightFee()).subtract(orderDetail.getCouponPrice()).subtract(orderDetail.getIntegralPrice());
        if (detailPayPrice.compareTo(BigDecimal.ZERO) >= 0) {
            orderDetail.setPayPrice(detailPayPrice);
        }

        RLock lock = redissonClient.getLock("Seckill_Order_" + detailVo.getProductId() + "_" + detailVo.getAttrValueId());
        Boolean execute = false;
        boolean myLock = false;
        try {
            if (myLock = lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                Long quota = redisUtil.decr(seckillProductQuotaKey, detailVo.getPayNum());
                if (quota < 0) {
                    // 补充上面口的库存
                    redisUtil.incrAndCreate(seckillProductQuotaKey, detailVo.getPayNum());
                    throw new CrmebException("秒杀商品库存不足");
                }
                Long skuQuota = redisUtil.decr(seckillProductSkuQuotaKey, detailVo.getPayNum());
                if (skuQuota < 0) {
                    // 补充上面口的库存
                    redisUtil.incrAndCreate(seckillProductSkuQuotaKey, detailVo.getPayNum());
                    throw new CrmebException("秒杀商品SKU库存不足");
                }
                execute = transactionTemplate.execute(e -> {
                    Boolean result = false;
                    logger.info("开始扣件商品库存:order:{}", JSON.toJSONString(order));
                    if (order.getType().equals(OrderConstants.ORDER_TYPE_SECKILL)) {
                        logger.info("开始扣件商品库存 --> 秒杀商品:{}", JSON.toJSONString(skuRecord));
                        subStock(skuRecord);
                    }
                    orderService.save(order);
                    orderExtService.save(orderExt);
                    merchantOrderService.save(merchantOrder);
                    orderDetailService.save(orderDetail);
                    // 扣除用户积分
                    if (order.getUseIntegral() > 0) {
                        result = userService.updateIntegral(user.getId(), order.getUseIntegral(), Constants.OPERATION_TYPE_SUBTRACT);
                        if (!result) {
                            e.setRollbackOnly();
                            logger.error("生成订单扣除用户积分失败,预下单号：{}", orderRequest.getPreOrderNo());
                            return result;
                        }
                        UserIntegralRecord userIntegralRecord = initOrderUseIntegral(user.getId(), order.getUseIntegral(), user.getIntegral(), order.getOrderNo());
                        userIntegralRecordService.save(userIntegralRecord);
                    }

                    if (CollUtil.isNotEmpty(couponIdList)) {
                        couponUserService.useBatch(couponIdList);
                    }
                    // 生成订单日志
                    orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_CREATE, OrderStatusConstants.ORDER_LOG_MESSAGE_CREATE);
                    return Boolean.TRUE;
                });
                if (!execute) {
                    // 补充Redis中扣除的库存
                    redisUtil.incrAndCreate(seckillProductQuotaKey, detailVo.getPayNum());
                    redisUtil.incrAndCreate(seckillProductSkuQuotaKey, detailVo.getPayNum());
                }
            } else {
                logger.info("Redisson分布式锁没有获取到锁:秒杀生成订单,ThreadName :" + Thread.currentThread().getName());
            }

        } catch (Exception e) {
            logger.info("Redisson获取分布式锁异常:秒杀生成订单,e :" + e);
        } finally {
            if (myLock) {
                lock.unlock();
                logger.info(StrUtil.format("Redisson分布式锁释放锁:秒杀生成订单,ThreadName :{}", Thread.currentThread().getName()));
            }
        }
        if (!execute) {
            throw new CrmebException("订单生成失败");
        }

        String key = OrderConstants.PRE_ORDER_CACHE_PREFIX + orderRequest.getPreOrderNo();
        // 删除缓存订单
        if (redisUtil.exists(key)) {
            redisUtil.delete(key);
        }

        // 加入自动未支付自动取消队列
        redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AUTO_CANCEL_KEY, order.getOrderNo());

        OrderNoResponse response = new OrderNoResponse();
        response.setOrderNo(order.getOrderNo());
        response.setPayPrice(order.getPayPrice());
        return response;
    }

    /**
     * 获取时段时间
     */
    private Integer getIntervalTimeInt(String timeStr) {
        timeStr = timeStr.replaceAll("%3A", ":");
        if (!timeStr.contains(":")) {
            throw new CrmebException("时间参数不正确 例如:01:00,02:00");
        }
        String[] splitStart = timeStr.split(":");
        String start = splitStart[0].trim().concat(splitStart[1].trim());
        Integer startTime = Integer.valueOf(start);
        return startTime;
    }

    /**
     * 获取时段时间
     * @param timeStr 时间字符
     */
    private String getTimeIntervalTimeStr(String timeStr) {
        String time = timeStr.length() == 3 ? "0" + timeStr : timeStr;
        if (timeStr.length() == 1) {
            time = "0000";
        }
        return time.substring(0,2) + ":" + time.substring(2);
    }

    /**
     * 用户积分记录——订单抵扣
     *
     * @param uid         用户ID
     * @param useIntegral 使用的积分
     * @param integral    用户当前积分
     * @param orderNo     订单号
     * @return 用户积分记录
     */
    private UserIntegralRecord initOrderUseIntegral(Integer uid, Integer useIntegral, Integer integral, String orderNo) {
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(uid);
        integralRecord.setLinkId(orderNo);
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
        integralRecord.setTitle(StrUtil.format("订单使用{}积分进行金额抵扣", useIntegral));
        integralRecord.setIntegral(useIntegral);
        integralRecord.setBalance(integral - useIntegral);
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        return integralRecord;
    }

}
