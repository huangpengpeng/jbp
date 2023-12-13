package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.service.dao.ProductDao;
import com.jbp.service.service.*;
import com.jbp.service.util.ProductUtils;
import com.jbp.common.config.CrmebConfig;
import com.jbp.common.constants.Constants;
import com.jbp.common.constants.CouponConstants;
import com.jbp.common.constants.ProductConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.coupon.CouponProduct;
import com.jbp.common.model.coupon.CouponUser;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantInfo;
import com.jbp.common.model.product.*;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.*;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.LoginUserVo;
import com.jbp.common.vo.MyRecord;
import com.jbp.common.vo.OnePassUserInfoVo;
import com.jbp.common.vo.SimpleProductVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
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
public class ProductServiceImpl extends ServiceImpl<ProductDao, Product>
        implements ProductService {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Resource
    private ProductDao dao;

    @Autowired
    private ProductAttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private ProductDescriptionService productDescriptionService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private CouponProductService couponProductService;
    @Autowired
    private ProductCouponService productCouponService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private MerchantInfoService merchantInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private CouponService couponService;
    @Autowired
    private ProductUtils productUtils;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private OnePassService onePassService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductReplyService productReplyService;

    /**
     * 获取产品列表Admin
     *
     * @param request          筛选参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<AdminProductListResponse> getAdminList(ProductSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        //带 Product 类的多条件查询
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Product::getMerId, admin.getMerId());
        setAdminListWrapperByType(lqw, request.getType(), admin.getMerId());
        //关键字搜索
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.and(i -> i.like(Product::getName, keywords)
                    .or().apply(StrUtil.format(" find_in_set('{}', keyword)", keywords)));
        }
        lqw.apply(StrUtil.isNotBlank(request.getCateId()), "FIND_IN_SET ('" + request.getCateId() + "', cate_id)");
        if (ObjectUtil.isNotNull(request.getCategoryId())) {
            lqw.eq(Product::getCategoryId, request.getCategoryId());
        }
        lqw.orderByDesc(Product::getSort).orderByDesc(Product::getId);
        Page<Product> productPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Product> products = dao.selectList(lqw);
        if (CollUtil.isEmpty(products)) {
            return CommonPage.copyPageInfo(productPage, CollUtil.newArrayList());
        }
        List<AdminProductListResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            AdminProductListResponse productResponse = new AdminProductListResponse();
            BeanUtils.copyProperties(product, productResponse);
            // 收藏数
            productResponse.setCollectCount(productRelationService.getCollectCountByProductId(product.getId()));
            productResponses.add(productResponse);
        }
        // 多条sql查询处理分页正确
        return CommonPage.copyPageInfo(productPage, productResponses);
    }

    /**
     * 根据id集合获取商品简单信息
     *
     * @param productIds id集合
     * @return 商品信息
     */
    @Override
    public List<SimpleProductVo> getSimpleListInIds(List<Integer> productIds) {
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        lqw.select(Product::getId, Product::getName, Product::getImage);
        lqw.in(Product::getId, productIds);
        lqw.eq(Product::getIsDel, false);
        List<Product> selectList = dao.selectList(lqw);
        return selectList.stream().map(e -> {
            SimpleProductVo vo = new SimpleProductVo();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 新增产品
     *
     * @param request 新增产品request对象
     * @return 新增结果
     */
    @Override
    public Boolean save(ProductAddRequest request) {
        // 多规格需要校验规格参数
        if (!request.getSpecType()) {
            if (request.getAttrValue().size() > 1) {
                throw new CrmebException("单规格商品属性值不能大于1");
            }
        }
        if (request.getIsSub()) {
            request.getAttrValue().forEach(av -> {
                int brokerageRatio = av.getBrokerage() + av.getBrokerageTwo();
                if (brokerageRatio > crmebConfig.getRetailStoreBrokerageRatio()) {
                    throw new CrmebException(StrUtil.format("一二级返佣比例之和范围为 0~{}", crmebConfig.getRetailStoreBrokerageRatio()));
                }
            });
        }

        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();

        Merchant merchant = merchantService.getByIdException(loginUserVo.getUser().getMerId());

        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setId(null);
        product.setMerId(loginUserVo.getUser().getMerId());

        String cdnUrl = systemAttachmentService.getCdnUrl();
        //主图
        product.setImage(systemAttachmentService.clearPrefix(product.getImage(), cdnUrl));
        //轮播图
        product.setSliderImage(systemAttachmentService.clearPrefix(product.getSliderImage(), cdnUrl));
        // 展示图
        if (StrUtil.isNotBlank(product.getFlatPattern())) {
            product.setFlatPattern(systemAttachmentService.clearPrefix(product.getFlatPattern(), cdnUrl));
        }

        List<ProductAttrValueAddRequest> attrValueAddRequestList = request.getAttrValue();
        //计算价格
        ProductAttrValueAddRequest minAttrValue = attrValueAddRequestList.stream().min(Comparator.comparing(ProductAttrValueAddRequest::getPrice)).get();
        product.setPrice(minAttrValue.getPrice());
        product.setOtPrice(minAttrValue.getOtPrice());
        product.setCost(minAttrValue.getCost());
        product.setStock(attrValueAddRequestList.stream().mapToInt(ProductAttrValueAddRequest::getStock).sum());

        product.setAuditStatus(ProductConstants.AUDIT_STATUS_EXEMPTION);
        product.setIsAudit(false);
        product.setIsShow(false);

        List<ProductAttrAddRequest> addRequestList = request.getAttr();
        List<ProductAttr> attrList = addRequestList.stream().map(e -> {
            ProductAttr attr = new ProductAttr();
            BeanUtils.copyProperties(e, attr);
            attr.setId(null);
            attr.setType(ProductConstants.PRODUCT_TYPE_NORMAL);
            return attr;
        }).collect(Collectors.toList());

        List<ProductAttrValue> attrValueList = attrValueAddRequestList.stream().map(e -> {
            ProductAttrValue attrValue = new ProductAttrValue();
            BeanUtils.copyProperties(e, attrValue);
            attrValue.setId(null);
            attrValue.setSku(getSku(e.getAttrValue()));
            attrValue.setQuota(0);
            attrValue.setQuotaShow(0);
            attrValue.setType(ProductConstants.PRODUCT_TYPE_NORMAL);
            attrValue.setImage(systemAttachmentService.clearPrefix(e.getImage(), cdnUrl));
            return attrValue;
        }).collect(Collectors.toList());

        // 处理富文本
        ProductDescription spd = new ProductDescription();
        spd.setDescription(StrUtil.isNotBlank(request.getContent()) ? systemAttachmentService.clearPrefix(request.getContent(), cdnUrl) : "");
        spd.setType(ProductConstants.PRODUCT_TYPE_NORMAL);

        Boolean execute = transactionTemplate.execute(e -> {
            if (merchant.getProductSwitch()) {// 开启商品审核
                product.setAuditStatus(ProductConstants.AUDIT_STATUS_WAIT);
            }
            save(product);

            attrList.forEach(attr -> attr.setProductId(product.getId()));
            attrValueList.forEach(value -> value.setProductId(product.getId()));
            attrService.saveBatch(attrList);
            productAttrValueService.saveBatch(attrValueList, 100);

            spd.setProductId(product.getId());
            productDescriptionService.deleteByProductId(product.getId(), ProductConstants.PRODUCT_TYPE_NORMAL);
            productDescriptionService.save(spd);

            if (CollUtil.isNotEmpty(request.getCouponIds())) {
                List<ProductCoupon> couponList = new ArrayList<>();
                for (Integer couponId : request.getCouponIds()) {
                    ProductCoupon spc = new ProductCoupon();
                    spc.setProductId(product.getId());
                    spc.setCouponId(couponId);
                    spc.setAddTime(CrmebDateUtil.getNowTime());
                    couponList.add(spc);
                }
                productCouponService.saveBatch(couponList);
            }
            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 商品sku
     *
     * @param attrValue json字符串
     * @return sku
     */
    private String getSku(String attrValue) {
        LinkedHashMap<String, String> linkedHashMap = JSONObject.parseObject(attrValue, LinkedHashMap.class, Feature.OrderedField);
        Iterator<Map.Entry<String, String>> iterator = linkedHashMap.entrySet().iterator();
        List<String> strings = CollUtil.newArrayList();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            strings.add(next.getValue());
        }
        return String.join(",", strings);
    }

    /**
     * 更新商品信息
     *
     * @param ProductRequest 商品参数
     * @return 更新结果
     */
    @Override
    public Boolean update(ProductAddRequest ProductRequest) {
        if (ObjectUtil.isNull(ProductRequest.getId())) {
            throw new CrmebException("商品ID不能为空");
        }

        if (!ProductRequest.getSpecType()) {
            if (ProductRequest.getAttrValue().size() > 1) {
                throw new CrmebException("单规格商品属性值不能大于1");
            }
        }
        if (ProductRequest.getIsSub()) {
            ProductRequest.getAttrValue().forEach(av -> {
                int brokerageRatio = av.getBrokerage() + av.getBrokerageTwo();
                if (brokerageRatio > crmebConfig.getRetailStoreBrokerageRatio()) {
                    throw new CrmebException(StrUtil.format("一二级返佣比例之和范围为 0~{}", crmebConfig.getRetailStoreBrokerageRatio()));
                }
            });
        }

        Product tempProduct = getById(ProductRequest.getId());
        if (ObjectUtil.isNull(tempProduct)) {
            throw new CrmebException("商品不存在");
        }
        if (tempProduct.getIsRecycle() || tempProduct.getIsDel()) {
            throw new CrmebException("商品已删除");
        }
        if (tempProduct.getIsShow()) {
            throw new CrmebException("请先下架商品，再进行修改");
        }
        if (tempProduct.getIsAudit()) {
            throw new CrmebException("审核中的商品无法修改");
        }

        Product product = new Product();
        BeanUtils.copyProperties(ProductRequest, product);
        product.setAuditStatus(tempProduct.getAuditStatus());
        Merchant merchant = merchantService.getByIdException(tempProduct.getMerId());

        String cdnUrl = systemAttachmentService.getCdnUrl();
        //主图
        product.setImage(systemAttachmentService.clearPrefix(product.getImage(), cdnUrl));
        //轮播图
        product.setSliderImage(systemAttachmentService.clearPrefix(product.getSliderImage(), cdnUrl));

        List<ProductAttrValueAddRequest> attrValueAddRequestList = ProductRequest.getAttrValue();
        //计算价格
        ProductAttrValueAddRequest minAttrValue = attrValueAddRequestList.stream().min(Comparator.comparing(ProductAttrValueAddRequest::getPrice)).get();
        product.setPrice(minAttrValue.getPrice());
        product.setOtPrice(minAttrValue.getOtPrice());
        product.setCost(minAttrValue.getCost());
        product.setStock(attrValueAddRequestList.stream().mapToInt(ProductAttrValueAddRequest::getStock).sum());

        // attr部分
        List<ProductAttrAddRequest> addRequestList = ProductRequest.getAttr();
        List<ProductAttr> attrAddList = CollUtil.newArrayList();
        List<ProductAttr> attrUpdateList = CollUtil.newArrayList();
        addRequestList.forEach(e -> {
            ProductAttr attr = new ProductAttr();
            BeanUtils.copyProperties(e, attr);
            if (ObjectUtil.isNull(attr.getId())) {
                attr.setProductId(product.getId());
                attr.setType(ProductConstants.PRODUCT_TYPE_NORMAL);
                attrAddList.add(attr);
            } else {
                attr.setIsDel(false);
                attrUpdateList.add(attr);
            }
        });

        // attrValue部分
        List<ProductAttrValue> attrValueAddList = CollUtil.newArrayList();
        List<ProductAttrValue> attrValueUpdateList = CollUtil.newArrayList();
        attrValueAddRequestList.forEach(e -> {
            ProductAttrValue attrValue = new ProductAttrValue();
            BeanUtils.copyProperties(e, attrValue);
            attrValue.setSku(getSku(e.getAttrValue()));
            attrValue.setImage(systemAttachmentService.clearPrefix(e.getImage(), cdnUrl));
            attrValue.setVersion(0);
            if (ObjectUtil.isNull(attrValue.getId()) || attrValue.getId().equals(0)) {
                attrValue.setId(null);
                attrValue.setProductId(product.getId());
                attrValue.setQuota(0);
                attrValue.setQuotaShow(0);
                attrValue.setType(ProductConstants.PRODUCT_TYPE_NORMAL);
                attrValueAddList.add(attrValue);
            } else {
                attrValue.setProductId(product.getId());
                attrValue.setIsDel(false);
                attrValueUpdateList.add(attrValue);
            }
        });

        // 处理富文本
        ProductDescription spd = new ProductDescription();
        spd.setDescription(ProductRequest.getContent().length() > 0 ? systemAttachmentService.clearPrefix(ProductRequest.getContent(), cdnUrl) : "");
        spd.setType(ProductConstants.PRODUCT_TYPE_NORMAL);
        spd.setProductId(product.getId());

        Boolean execute = transactionTemplate.execute(e -> {
            if (!merchant.getProductSwitch() && product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_EXEMPTION)) {
                product.setAuditStatus(ProductConstants.AUDIT_STATUS_EXEMPTION);
            } else {
                product.setAuditStatus(ProductConstants.AUDIT_STATUS_WAIT);
            }

            product.setIsAudit(false);
            dao.updateById(product);

            // 先删除原用attr+value
            attrService.deleteByProductIdAndType(product.getId(), ProductConstants.PRODUCT_TYPE_NORMAL);
            productAttrValueService.deleteByProductIdAndType(product.getId(), ProductConstants.PRODUCT_TYPE_NORMAL);

            if (CollUtil.isNotEmpty(attrAddList)) {
                attrService.saveBatch(attrAddList);
            }
            if (CollUtil.isNotEmpty(attrUpdateList)) {
                attrService.saveOrUpdateBatch(attrUpdateList);
            }

            if (CollUtil.isNotEmpty(attrValueAddList)) {
                productAttrValueService.saveBatch(attrValueAddList);
            }
            if (CollUtil.isNotEmpty(attrValueUpdateList)) {
                productAttrValueService.saveOrUpdateBatch(attrValueUpdateList);
            }

            productDescriptionService.deleteByProductId(product.getId(), ProductConstants.PRODUCT_TYPE_NORMAL);
            productDescriptionService.save(spd);

            if (CollUtil.isNotEmpty(ProductRequest.getCouponIds())) {
                productCouponService.deleteByProductId(product.getId());
                List<ProductCoupon> couponList = new ArrayList<>();
                for (Integer couponId : ProductRequest.getCouponIds()) {
                    ProductCoupon spc = new ProductCoupon();
                    spc.setProductId(product.getId());
                    spc.setCouponId(couponId);
                    spc.setAddTime(CrmebDateUtil.getNowTime());
                    couponList.add(spc);
                }
                productCouponService.saveBatch(couponList);
            } else {
                productCouponService.deleteByProductId(product.getId());
            }
            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 商品详情（管理端）
     *
     * @param id 商品id
     * @return ProductInfoResponse
     */
    @Override
    public ProductInfoResponse getInfo(Integer id) {
        Product Product = dao.selectById(id);
        if (ObjectUtil.isNull(Product)) {
            throw new CrmebException("未找到对应商品信息");
        }

        ProductInfoResponse ProductResponse = new ProductInfoResponse();
        BeanUtils.copyProperties(Product, ProductResponse);

        List<ProductAttr> attrList = attrService.getListByProductIdAndType(Product.getId(), ProductConstants.PRODUCT_TYPE_NORMAL);
        ProductResponse.setAttr(attrList);

        List<ProductAttrValue> attrValueList = productAttrValueService.getListByProductIdAndType(Product.getId(), ProductConstants.PRODUCT_TYPE_NORMAL);
        List<AttrValueResponse> valueResponseList = attrValueList.stream().map(e -> {
            AttrValueResponse valueResponse = new AttrValueResponse();
            BeanUtils.copyProperties(e, valueResponse);
            return valueResponse;
        }).collect(Collectors.toList());
        ProductResponse.setAttrValue(valueResponseList);

        ProductDescription sd = productDescriptionService.getByProductIdAndType(Product.getId(), ProductConstants.PRODUCT_TYPE_NORMAL);
        if (ObjectUtil.isNotNull(sd)) {
            ProductResponse.setContent(ObjectUtil.isNull(sd.getDescription()) ? "" : sd.getDescription());
        }

        // 获取已关联的优惠券
        List<ProductCoupon> productCouponList = productCouponService.getListByProductId(Product.getId());
        if (CollUtil.isNotEmpty(productCouponList)) {
            List<Integer> ids = productCouponList.stream().map(ProductCoupon::getCouponId).collect(Collectors.toList());
            SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
            if (systemAdmin.getMerId() > 0) {
                ProductResponse.setCouponIds(ids);
            } else {
                ProductResponse.setCouponList(couponService.findSimpleListByIdList(ids));
            }
        }
        return ProductResponse;
    }

    /**
     * 根据商品tabs获取对应类型的产品数量
     *
     * @return List
     */
    @Override
    public List<ProductTabsHeaderResponse> getTabsHeader() {
        List<ProductTabsHeaderResponse> headers = new ArrayList<>();
        ProductTabsHeaderResponse header1 = new ProductTabsHeaderResponse(0, 1);
        ProductTabsHeaderResponse header2 = new ProductTabsHeaderResponse(0, 2);
        ProductTabsHeaderResponse header3 = new ProductTabsHeaderResponse(0, 3);
        ProductTabsHeaderResponse header4 = new ProductTabsHeaderResponse(0, 4);
        ProductTabsHeaderResponse header5 = new ProductTabsHeaderResponse(0, 5);
        ProductTabsHeaderResponse header6 = new ProductTabsHeaderResponse(0, 6);
        ProductTabsHeaderResponse header7 = new ProductTabsHeaderResponse(0, 7);
        headers.add(header1);
        headers.add(header2);
        headers.add(header3);
        headers.add(header4);
        headers.add(header5);
        headers.add(header6);
        headers.add(header7);
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();

        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        for (ProductTabsHeaderResponse h : headers) {
            lqw.clear();
            lqw.select(Product::getId);
            lqw.eq(Product::getMerId, systemAdmin.getMerId());
            setAdminListWrapperByType(lqw, h.getType(), systemAdmin.getMerId());
            List<Product> products = dao.selectList(lqw);
            h.setCount(products.size());
        }
        return headers;
    }

    /**
     * 商户端商品列表table类型查询条件
     *
     * @param type  商品列表table类型
     * @param merId 商户ID
     */
    private void setAdminListWrapperByType(LambdaQueryWrapper<Product> lqw, Integer type, Integer merId) {
        switch (type) {
            case 1:
                //出售中（已上架）
                lqw.eq(Product::getIsShow, true);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                lqw.in(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_SUCCESS, ProductConstants.AUDIT_STATUS_EXEMPTION);
                break;
            case 2:
                //仓库中（未上架）
                lqw.eq(Product::getIsShow, false);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                lqw.eq(Product::getIsAudit, false);
                lqw.in(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_WAIT, ProductConstants.AUDIT_STATUS_EXEMPTION, ProductConstants.AUDIT_STATUS_SUCCESS);
                break;
            case 3:
                //已售罄
                lqw.le(Product::getStock, 0);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                lqw.in(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_SUCCESS, ProductConstants.AUDIT_STATUS_EXEMPTION);
                break;
            case 4:
                //警戒库存
                MerchantInfo merchantInfo = merchantInfoService.getByMerId(merId);
                lqw.le(Product::getStock, ObjectUtil.isNotNull(merchantInfo) ? merchantInfo.getAlertStock() : 0);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                lqw.in(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_SUCCESS, ProductConstants.AUDIT_STATUS_EXEMPTION);
                break;
            case 5:
                //回收站
                lqw.eq(Product::getIsRecycle, true);
                lqw.eq(Product::getIsDel, false);
                break;
            case 6:
                //待审核
                lqw.eq(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_WAIT);
                lqw.eq(Product::getIsAudit, true);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                break;
            case 7:
                //审核失败
                lqw.eq(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_FAIL);
                lqw.eq(Product::getIsAudit, false);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                break;
            default:
                break;
        }
    }

    /**
     * 根据其他平台url导入产品信息
     *
     * @param url 待导入平台url
     * @param tag 1=淘宝，2=京东，3=苏宁，4=拼多多， 5=天猫
     * @return ProductRequest
     */
    @Override
    public ProductRequest importProductFromUrl(String url, int tag) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        Merchant merchant = merchantService.getByIdException(admin.getMerId());
        if (merchant.getCopyProductNum() <= 0) {
            throw new CrmebException("商户复制商品数量不足");
        }

        ProductRequest productRequest = null;
        try {
            switch (tag) {
                case 1:
                    productRequest = productUtils.getTaobaoProductInfo(url,tag);
                    break;
                case 2:
                    productRequest = productUtils.getJDProductInfo(url,tag);
                    break;
                case 3:
                    productRequest = productUtils.getSuningProductInfo(url,tag);
                    break;
                case 4:
                    productRequest = productUtils.getPddProductInfo(url,tag);
                    break;
                case 5:
                    productRequest = productUtils.getTmallProductInfo(url,tag);
                    break;
            }
        } catch (Exception e) {
            throw new CrmebException("确认URL和平台是否正确，以及平台费用是否足额" + e.getMessage());
        }
        Boolean sub = merchantService.subCopyProductNum(merchant.getId());
        if (!sub) {
            LOGGER.error("扣除商户复制条数异常：商户ID = {}", merchant.getId());
        }
        return productRequest;
    }

    /**
     * 商品回收/删除
     *
     * @param request 删除参数
     * @return Boolean
     */
    @Override
    public Boolean deleteProduct(ProductDeleteRequest request) {
        Product product = getById(request.getId());
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException("商品不存在");
        }
        if (ProductConstants.PRODUCT_DELETE_TYPE_RECYCLE.equals(request.getType()) && product.getIsRecycle()) {
            throw new CrmebException("商品已存在回收站");
        }

        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, product.getId());
        if (ProductConstants.PRODUCT_DELETE_TYPE_DELETE.equals(request.getType())) {
            wrapper.set(Product::getIsDel, true);
        } else {
            wrapper.set(Product::getIsRecycle, true);
        }
        return transactionTemplate.execute(e -> {
            update(wrapper);
            if (request.getType().equals("recycle")) {
                cartService.productStatusNotEnable(request.getId());
            } else {
                cartService.productDelete(request.getId());
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 恢复已删除的商品
     *
     * @param productId 商品id
     * @return 恢复结果
     */
    @Override
    public Boolean restoreProduct(Integer productId) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, productId);
        wrapper.set(Product::getIsRecycle, false);
        wrapper.set(Product::getIsShow, false);
        return update(wrapper);
    }

    /**
     * 添加/扣减库存
     *
     * @param id   商品id
     * @param num  数量
     * @param type 类型：add—添加，sub—扣减
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String type) {
        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
        if (type.equals(Constants.OPERATION_TYPE_QUICK_ADD)) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
        }
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
        }
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            updateWrapper.setSql(StrUtil.format("stock = stock - {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales + {}", num));
            // 扣减时加乐观锁保证库存不为负
            updateWrapper.last(StrUtil.format(" and (stock - {} >= 0)", num));
        }
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新普通商品库存失败,商品id = " + id);
        }
        return update;
    }

    /**
     * 下架
     *
     * @param id 商品id
     */
    @Override
    public Boolean offShelf(Integer id) {
        Product product = getById(id);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException("商品不存在");
        }
        if (!product.getIsShow()) {
            return true;
        }

        product.setIsShow(false);

        return transactionTemplate.execute(e -> {
            dao.updateById(product);
            cartService.productStatusNotEnable(id);
            // 商品下架时，清除用户收藏
            productRelationService.deleteByProId(product.getId());
            return Boolean.TRUE;
        });
    }

    /**
     * 上架
     *
     * @param id 商品id
     * @return Boolean
     */
    @Override
    public Boolean putOnShelf(Integer id) {
        Product product = getById(id);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException("商品不存在");
        }
        if (product.getIsShow()) {
            return true;
        }
        if (product.getIsAudit()) {
            throw new CrmebException("商品审核中无法上架");
        }
        if (!product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_EXEMPTION) && !product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_SUCCESS)) {
            throw new CrmebException("商品状态异常无法上架");
        }
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        Merchant merchant = merchantService.getById(loginUserVo.getUser().getMerId());
        if (!merchant.getIsSwitch()) {
            throw new CrmebException("打开商户开关后方能上架商品");
        }
        product.setIsShow(true);
        // 获取商品skuid
        List<ProductAttrValue> skuList = productAttrValueService.getListByProductIdAndType(id, ProductConstants.PRODUCT_TYPE_NORMAL);
        List<Integer> skuIdList = skuList.stream().map(ProductAttrValue::getId).collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            dao.updateById(product);
            if (CollUtil.isNotEmpty(skuIdList)) {
                cartService.productStatusNoEnable(skuIdList);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 首页商品列表
     *
     * @param pageParamRequest 分页参数
     * @param cid 一级商品分类id，全部传0
     * @return CommonPage
     */
    @Override
    public PageInfo<Product> getIndexProduct(Integer cid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getMerId, Product::getImage, Product::getName, Product::getUnitName,
                Product::getPrice, Product::getOtPrice, Product::getSales, Product::getFicti);
        getForSaleWhere(lqw);
        lqw.gt(Product::getStock, 0);
        if (cid > 0) {
            List<ProductCategory> categoryList = productCategoryService.getThirdCategoryByFirstId(cid, 0);
            if (CollUtil.isEmpty(categoryList)) {
                return new PageInfo<>();
            }
            List<Integer> cidList = categoryList.stream().map(ProductCategory::getId).collect(Collectors.toList());
            lqw.in(Product::getCategoryId, cidList);
        }
        lqw.orderByDesc(Product::getRank);
        lqw.orderByDesc(Product::getId);
        Page<Product> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Product> productList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 获取出售中商品的Where条件
     */
    private void getForSaleWhere(LambdaQueryWrapper<Product> lqw) {
        lqw.eq(Product::getIsDel, false);
        lqw.eq(Product::getIsRecycle, false);
        lqw.eq(Product::getIsShow, true);
        lqw.in(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_SUCCESS, ProductConstants.AUDIT_STATUS_EXEMPTION);
    }

    /**
     * 获取商品移动端列表
     *
     * @param request     筛选参数
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductFrontResponse> findH5List(ProductFrontSearchRequest request, PageParamRequest pageRequest) {
        Map<String, Object> map = new HashMap<>();
        if (ObjectUtil.isNotNull(request.getCid()) && request.getCid() > 0) {
            map.put("categoryId", request.getCid());
        }
        if (StrUtil.isNotBlank(request.getKeyword())) {
            String keyword = URLUtil.decode(request.getKeyword());
            map.put("keywords", keyword);
        }
        if (ObjectUtil.isNotNull(request.getMaxPrice())) {
            map.put("maxPrice", request.getMaxPrice());
        }
        if (ObjectUtil.isNotNull(request.getMinPrice())) {
            map.put("minPrice", request.getMinPrice());
        }
        if (ObjectUtil.isNotNull(request.getBrandId())) {
            map.put("brandId", request.getBrandId());
        }
        if (ObjectUtil.isNotNull(request.getIsSelf())) {
            map.put("isSelf", request.getIsSelf() ? 1 : 0);
        }
        // 排序部分
        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            if (request.getSalesOrder().equals(Constants.SORT_DESC)) {
                map.put("lastStr", " order by (p.sales + p.ficti) desc, p.rank desc, p.sort desc, p.id desc");
            } else {
                map.put("lastStr", " order by (p.sales + p.ficti) asc, p.rank desc, p.sort desc, p.id desc");
            }
        } else if (StrUtil.isNotBlank(request.getPriceOrder())){
            if (request.getPriceOrder().equals(Constants.SORT_DESC)) {
                map.put("lastStr", " order by p.price desc, p.rank desc, p.sort desc, p.id desc");
            } else {
                map.put("lastStr", " order by p.price asc, p.rank desc, p.sort desc, p.id desc");
            }
        } else {
            map.put("lastStr", " order by p.rank desc, p.sort desc, p.id desc");
        }
        Page<Product> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        List<ProductFrontResponse> responseList = dao.findH5List(map);
        responseList.forEach(e -> {
            // 评论总数
            Integer sumCount = productReplyService.getCountByScore(e.getId(), ProductConstants.PRODUCT_REPLY_TYPE_ALL);
            // 好评总数
            Integer goodCount = productReplyService.getCountByScore(e.getId(), ProductConstants.PRODUCT_REPLY_TYPE_GOOD);
            String replyChance = "0";
            if (sumCount > 0 && goodCount > 0) {
                replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
            }
            e.setReplyNum(sumCount);
            e.setPositiveRatio(replyChance);
        });
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 获取移动端商品详情
     *
     * @param id 商品id
     * @return Product
     */
    @Override
    public Product getH5Detail(Integer id) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getMerId, Product::getImage, Product::getName, Product::getSliderImage,
                Product::getOtPrice, Product::getStock, Product::getSales, Product::getPrice, Product::getIntro,
                Product::getFicti, Product::getBrowse, Product::getUnitName, Product::getGuaranteeIds);
        lqw.eq(Product::getId, id);
        getForSaleWhere(lqw);
        Product product = dao.selectOne(lqw);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException(StrUtil.format("没有找到ID： {} 的商品", id));
        }

        ProductDescription sd = productDescriptionService.getByProductIdAndType(product.getId(), ProductConstants.PRODUCT_TYPE_NORMAL);
        if (ObjectUtil.isNotNull(sd)) {
            product.setContent(StrUtil.isBlank(sd.getDescription()) ? "" : sd.getDescription());
        }
        return product;
    }

    /**
     * 获取购物车商品信息
     *
     * @param productId 商品编号
     * @return Product
     */
    @Override
    public Product getCartByProId(Integer productId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getImage, Product::getName);
        lqw.eq(Product::getId, productId);
        return dao.selectOne(lqw);
    }

    /**
     * 根据日期获取新增商品数量
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getNewProductByDate(String date) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId);
        lqw.eq(Product::getIsDel, 0);
        lqw.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 获取所有未删除的商品
     *
     * @return List<Product>
     */
    @Override
    public List<Product> findAllProductByNotDelete() {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getMerId);
        lqw.eq(Product::getIsDel, 0);
        return dao.selectList(lqw);
    }

    /**
     * 模糊搜索商品名称
     *
     * @param productName 商品名称
     * @param merId       商户Id
     * @return List
     */
    @Override
    public List<Product> likeProductName(String productName, Integer merId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId);
        lqw.like(Product::getName, productName);
        lqw.eq(Product::getIsDel, 0);
        if (!merId.equals(0)) {
            lqw.eq(Product::getMerId, merId);
        }
        return dao.selectList(lqw);
    }

    /**
     * 销售中（上架）商品数量
     *
     * @return Integer
     */
    @Override
    public Integer getOnSaleNum(Integer merId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(Product::getMerId, merId);
        }
        getForSaleWhere(lqw);
        return dao.selectCount(lqw);
    }

    /**
     * 强制下架商户所有商品
     *
     * @param merchantId 商户ID
     * @return Boolean
     */
    @Override
    public Boolean forcedRemovalAll(Integer merchantId) {
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getIsShow, false);
        wrapper.set(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_WAIT);
        wrapper.eq(Product::getMerId, merchantId);
        wrapper.eq(Product::getIsDel, false);
        wrapper.ne(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_FAIL);
        boolean update = update(wrapper);
        if (!update) {
            return update;
        }
        LambdaQueryWrapper<Product> query = Wrappers.lambdaQuery();
        query.select(Product::getId);
        query.eq(Product::getMerId, merchantId);
        query.eq(Product::getIsDel, false);
        List<Product> productList = dao.selectList(query);
        productList.forEach(product -> {
            // 更新购物车数据
            cartService.productStatusNotEnable(product.getId());
            // 商品强制下架时，清除用户收藏
            productRelationService.deleteByProId(product.getId());
        });
        return true;
    }

    /**
     * 平台端商品分页列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformProductListResponse> getPlatformPageList(ProductSearchRequest request, PageParamRequest pageParamRequest) {
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("type", request.getType());
        if (ObjectUtil.isNotNull(request.getCategoryId())) {
            ProductCategory category = productCategoryService.getById(request.getCategoryId());
            if (category.getLevel().equals(3)) {
                map.put("categoryIds", request.getCategoryId());
            } else {
                List<ProductCategory> categoryList = productCategoryService.findAllChildListByPid(category.getId(), category.getLevel());
                List<String> cateIdList = categoryList.stream().filter(e -> e.getLevel().equals(3)).map(e -> e.getId().toString()).collect(Collectors.toList());
                String categoryIds = String.join(",", cateIdList);
                map.put("categoryIds", categoryIds);
            }
        }
        if (ObjectUtil.isNotNull(request.getMerId())) {
            map.put("merId", request.getMerId());
        }
        if (ObjectUtil.isNotNull(request.getIsSelf())) {
            map.put("self", request.getIsSelf());
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            map.put("keywords", keywords);
        }
        Page<Product> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<PlatformProductListResponse> proList = dao.getPlatformPageList(map);
        return CommonPage.copyPageInfo(page, proList);
    }

    /**
     * 商品审核
     *
     * @param request 审核参数
     * @return Boolean
     */
    @Override
    public Boolean audit(ProductAuditRequest request) {
        if (request.getAuditStatus().equals("fail") && StrUtil.isEmpty(request.getReason())) {
            throw new CrmebException("审核拒绝请填写拒绝原因");
        }
        Product product = getByIdException(request.getId());
        if (!product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT)) {
            throw new CrmebException("商品并非等待审核状态");
        }
        if (!product.getIsAudit()) {
            throw new CrmebException("商品未进入审核流程中");
        }
        if (request.getAuditStatus().equals("fail")) {
            product.setAuditStatus(ProductConstants.AUDIT_STATUS_FAIL);
            product.setReason(request.getReason());
        } else {
            // 审核成功
            product.setAuditStatus(ProductConstants.AUDIT_STATUS_SUCCESS);
            // 免审店铺商品回归免审状态
            Merchant merchant = merchantService.getByIdException(product.getMerId());
            if (!merchant.getProductSwitch()) {
                product.setAuditStatus(ProductConstants.AUDIT_STATUS_EXEMPTION);
            }
        }
        product.setIsAudit(false);
        product.setIsShow(false);
        return updateById(product);
    }

    /**
     * 强制下加商品
     *
     * @param request 商品id参数
     * @return Boolean
     */
    @Override
    public Boolean forceDown(ProductForceDownRequest request) {
        String ids = request.getIds();
        List<Integer> idList = Stream.of(ids.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getIsShow, false);
        wrapper.set(Product::getIsAudit, false);
        wrapper.set(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_WAIT);
        wrapper.in(Product::getId, idList);
        boolean update = update(wrapper);
        if (update) {
            idList.forEach(id -> {
                // 修改购物车状态
                cartService.productStatusNotEnable(id);
                // 商品强制下架时，清除用户收藏
                productRelationService.deleteByProId(id);
            });
        }
        return update;
    }

    /**
     * 是否有商品使用对应的商户商品分类
     *
     * @param id 商户商品分类id
     * @return Boolean
     */
    @Override
    public Boolean isExistStoreCategory(Integer id) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId);
        lqw.eq(Product::getIsDel, false);
        lqw.apply(" find_in_set({0}, cate_id)", id);
        lqw.last(" limit 1");
        Product Product = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(Product);
    }

    /**
     * 商品增加浏览量
     *
     * @param proId 商品id
     * @return Boolean
     */
    @Override
    public Boolean addBrowse(Integer proId) {
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.setSql("browse = browse + 1");
        wrapper.eq(Product::getId, proId);
        return update(wrapper);
    }

    /**
     * 获取商户推荐商品
     *
     * @param merId 商户id
     * @param num   查询商品数量
     * @return List
     */
    @Override
    public List<ProMerchantProductResponse> getRecommendedProductsByMerId(Integer merId, Integer num) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getMerId, Product::getImage, Product::getName,
                Product::getPrice, Product::getSales, Product::getFicti);
        lqw.eq(Product::getMerId, merId);
        getForSaleWhere(lqw);
        lqw.orderByDesc(Product::getSort);
        lqw.last("limit " + num);
        List<Product> productList = dao.selectList(lqw);
        if (CollUtil.isEmpty(productList)) {
            return CollUtil.newArrayList();
        }
        return productList.stream().map(product -> {
            ProMerchantProductResponse response = new ProMerchantProductResponse();
            BeanUtils.copyProperties(product, response);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 商户商品列表
     *
     * @param request          搜索参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public PageInfo<Product> findMerchantProH5List(MerchantProductSearchRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        // id、名称、图片、价格、销量
        lqw.select(Product::getId, Product::getName, Product::getImage, Product::getPrice, Product::getOtPrice,
                Product::getSales, Product::getFicti, Product::getUnitName, Product::getStock);

        getForSaleWhere(lqw);
        lqw.eq(Product::getMerId, request.getMerId());
        if (StrUtil.isNotBlank(request.getKeyword())) {
            String keyword = URLUtil.decode(request.getKeyword());
            lqw.and(i -> i.like(Product::getName, keyword)
                    .or().like(Product::getKeyword, keyword));
        }
        if (ObjectUtil.isNotNull(request.getCid()) && request.getCid() > 0) {
            lqw.apply(StrUtil.format(" find_in_set({}, cate_id)", request.getCid()));
        }
        if (ObjectUtil.isNotNull(request.getMaxPrice())) {
            lqw.le(Product::getPrice, request.getMaxPrice());
        }
        if (ObjectUtil.isNotNull(request.getMinPrice())) {
            lqw.ge(Product::getPrice, request.getMinPrice());
        }
        // 排序部分
        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            if (request.getSalesOrder().equals(Constants.SORT_DESC)) {
                lqw.last(" order by (sales + ficti) desc, sort desc, id desc");
            } else {
                lqw.last(" order by (sales + ficti) asc, sort desc, id desc");
            }
        } else {
            if (StrUtil.isNotBlank(request.getPriceOrder())) {
                if (request.getPriceOrder().equals(Constants.SORT_DESC)) {
                    lqw.orderByDesc(Product::getPrice);
                } else {
                    lqw.orderByAsc(Product::getPrice);
                }
            }

            lqw.orderByDesc(Product::getSort);
            lqw.orderByDesc(Product::getId);
        }
        Page<Product> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Product> productList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 判断商品是否使用品牌
     *
     * @param brandId 品牌id
     * @return Boolean
     */
    @Override
    public Boolean isUseBrand(Integer brandId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId);
        lqw.eq(Product::getIsDel, false);
        lqw.eq(Product::getBrandId, brandId);
        lqw.last("limit 1");
        Product Product = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(Product);
    }

    /**
     * 判断商品是否使用平台分类
     *
     * @param categoryId 平台分类id
     * @return Boolean
     */
    @Override
    public Boolean isUsePlatformCategory(Integer categoryId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId);
        lqw.eq(Product::getIsDel, false);
        lqw.eq(Product::getCategoryId, categoryId);
        lqw.last("limit 1");
        Product Product = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(Product);
    }

    /**
     * 查询使用服务保障的商品列表
     *
     * @param gid 服务保障id
     * @return List
     */
    @Override
    public List<Product> findUseGuarantee(Integer gid) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getMerId);
        lqw.eq(Product::getIsDel, false);
        lqw.apply(" find_in_set({0}, guarantee_ids)", gid);
        return dao.selectList(lqw);
    }

    /**
     * 判断商品是否使用服务保障
     *
     * @param gid 服务保障id
     * @return Boolean
     */
    @Override
    public Boolean isUseGuarantee(Integer gid) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId);
        lqw.eq(Product::getIsDel, false);
        lqw.apply(" find_in_set({0}, guarantee_ids)", gid);
        lqw.last("limit 1");
        Product Product = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(Product);
    }

    /**
     * 获取待审核商品数量
     */
    @Override
    public Integer getAwaitAuditNum(Integer merId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.eq(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_WAIT);
        lqw.eq(Product::getIsAudit, true);
        lqw.eq(Product::getIsRecycle, false);
        lqw.eq(Product::getIsDel, false);
        setAdminListWrapperByType(lqw, 6, merId);
        if (merId > 0) {
            lqw.eq(Product::getMerId, merId);
        }
        return dao.selectCount(lqw);
    }

    /**
     * 下架商品商品
     *
     * @param merId 商户id
     */
    @Override
    public Boolean downByMerId(Integer merId) {
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getIsShow, false);
        wrapper.eq(Product::getMerId, merId);
        wrapper.eq(Product::getIsShow, true);
        return update(wrapper);
    }

    /**
     * 优惠券商品列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<Product> getCouponProList(CouponProductSearchRequest request, PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        CouponUser couponUser = couponUserService.getById(request.getUserCouponId());
        if (ObjectUtil.isNull(couponUser) || !couponUser.getUid().equals(userId)
                || couponUser.getStatus() > CouponConstants.STORE_COUPON_USER_STATUS_USABLE) {
            throw new CrmebException("优惠券不存在或不是未使用状态");
        }
        List<Integer> pidList = null;
        if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            List<CouponProduct> cpList = couponProductService.findByCid(couponUser.getCouponId());
            if (CollUtil.isEmpty(cpList)) {
                throw new CrmebException("优惠券对应商品不存在");
            }
            pidList = cpList.stream().map(CouponProduct::getPid).collect(Collectors.toList());
        }
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        // id、名称、图片、价格、销量
        lqw.select(Product::getId, Product::getName, Product::getImage, Product::getPrice, Product::getOtPrice,
                Product::getSales, Product::getFicti, Product::getUnitName, Product::getStock, Product::getMerId);
        getForSaleWhere(lqw);
        if (StrUtil.isNotBlank(request.getKeyword())) {
            lqw.and(i -> i.like(Product::getName, request.getKeyword())
                    .or().like(Product::getKeyword, request.getKeyword()));
        }
        if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_MERCHANT)) {
            lqw.eq(Product::getMerId, couponUser.getMerId());
        }
        if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            if (CollUtil.isNotEmpty(pidList)) {
                lqw.in(Product::getId, pidList);
            }
        }
        lqw.orderByDesc(Product::getSort);
        lqw.orderByDesc(Product::getId);
        Page<Product> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Product> productList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 平台端获取商品表头数量
     *
     * @return List
     */
    @Override
    public List<ProductTabsHeaderResponse> getPlatformTabsHeader() {
        List<ProductTabsHeaderResponse> headers = new ArrayList<>();
        ProductTabsHeaderResponse header1 = new ProductTabsHeaderResponse(0, 1);
        ProductTabsHeaderResponse header2 = new ProductTabsHeaderResponse(0, 2);
        ProductTabsHeaderResponse header6 = new ProductTabsHeaderResponse(0, 6);
        ProductTabsHeaderResponse header7 = new ProductTabsHeaderResponse(0, 7);
        headers.add(header1);
        headers.add(header2);
        headers.add(header6);
        headers.add(header7);
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        for (ProductTabsHeaderResponse h : headers) {
            lqw.clear();
            lqw.select(Product::getId);
            setAdminListWrapperByType(lqw, h.getType(), 0);
            List<Product> Products = dao.selectList(lqw);
            h.setCount(Products.size());
        }
        return headers;
    }

    /**
     * 平台端商品编辑
     *
     * @param request 商品编辑参数
     * @return Boolean
     */
    @Override
    public Boolean platUpdate(ProductPlatUpdateRequest request) {
        Product product = getByIdException(request.getId());
        if (product.getFicti().equals(request.getFicti()) && product.getRank().equals(request.getRank())) {
            return Boolean.TRUE;
        }
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getFicti, request.getFicti());
        wrapper.set(Product::getRank, request.getRank());
        wrapper.eq(Product::getId, request.getId());
        return update(wrapper);
    }

    /**
     * 是否有商品使用运费模板
     *
     * @return Boolean
     */
    @Override
    public Boolean isUseShippingTemplateId(Integer templateId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.eq(Product::getTempId, templateId);
        lqw.eq(Product::getIsDel, false);
        return dao.selectCount(lqw) > 0;
    }

    /**
     * 商品提审
     *
     * @param id 商品ID
     * @return Boolean
     */
    @Override
    public Boolean submitAudit(Integer id) {
        Product product = getByIdException(id);
        if (product.getIsAudit()) {
            throw new CrmebException("商品已经在审核中");
        }
        if (!product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT)) {
            throw new CrmebException("商品不在待提审状态");
        }
        if (product.getIsRecycle()) {
            throw new CrmebException("回收站商品无法进行此类操作");
        }
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getIsAudit, true);
        wrapper.set(Product::getIsShow, false);
        wrapper.eq(Product::getId, id);
        return update(wrapper);
    }

    /**
     * 快捷添加库存
     *
     * @param request 添加库存参数
     * @return Boolean
     */
    @Override
    public Boolean quickAddStock(ProductAddStockRequest request) {
        Product product = getByIdException(request.getId());
        if (product.getIsAudit()) {
            throw new CrmebException("审核中商品无法编辑");
        }
        List<ProductAttrValueAddStockRequest> valueStockList = request.getAttrValueList();
        List<Integer> attrIdList = valueStockList.stream().map(ProductAttrValueAddStockRequest::getId).distinct().collect(Collectors.toList());
        if (attrIdList.size() != valueStockList.size()) {
            throw new CrmebException("有重复的商品规格属性ID");
        }
        List<ProductAttrValue> valueList = productAttrValueService.getByProductIdAndAttrIdList(request.getId(), attrIdList, ProductConstants.PRODUCT_TYPE_NORMAL);
        if (CollUtil.isEmpty(valueList) || valueList.size() != attrIdList.size()) {
            throw new CrmebException("商品规格属性ID数组数据异常，请刷新后再试");
        }
        for (ProductAttrValueAddStockRequest value : valueStockList) {
            for (ProductAttrValue attrValue : valueList) {
                if (attrValue.getId().equals(value.getId())) {
                    value.setVersion(attrValue.getVersion());
                    break;
                }
            }
        }
        int totalStock = valueStockList.stream().mapToInt(ProductAttrValueAddStockRequest::getStock).sum();
        return transactionTemplate.execute(e -> {
            operationStock(product.getId(), totalStock, Constants.OPERATION_TYPE_QUICK_ADD);
            valueStockList.forEach(valueStock -> {
                productAttrValueService.operationStock(valueStock.getId(), valueStock.getStock(),
                        Constants.OPERATION_TYPE_QUICK_ADD, ProductConstants.PRODUCT_TYPE_NORMAL, valueStock.getVersion());
            });
            return Boolean.TRUE;
        });
    }

    /**
     * 商品免审编辑
     *
     * @param request 商品免审编辑参数
     * @return Boolean
     */
    @Override
    public Boolean reviewFreeEdit(ProductReviewFreeEditRequest request) {
        Product product = getByIdException(request.getId());
        if (product.getIsShow()) {
            throw new CrmebException("上架商品无法编辑");
        }
        if (product.getIsAudit()) {
            throw new CrmebException("审核中商品无法编辑");
        }
        List<ProductAttrValueReviewFreeEditRequest> attrValueRequestList = request.getAttrValue();
        List<ProductAttrValue> attrValueList = productAttrValueService.getListByProductIdAndType(product.getId(), ProductConstants.PRODUCT_TYPE_NORMAL);
        if (CollUtil.isEmpty(attrValueList) || attrValueList.size() != attrValueRequestList.size()) {
            throw new CrmebException("商品规格属性数量不一致");
        }
        attrValueList.forEach(attrValue -> {
            for (ProductAttrValueReviewFreeEditRequest attrValueRequest : attrValueRequestList) {
                if (attrValueRequest.getId().equals(attrValue.getId())) {
                    attrValue.setPrice(attrValueRequest.getPrice());
                    attrValue.setStock(attrValueRequest.getStock());
                    attrValue.setVersion(0);
                    break;
                }
            }
        });

        ProductAttrValue minAttrValue = attrValueList.stream().min(Comparator.comparing(ProductAttrValue::getPrice)).get();
        Product tempProduct = new Product();
        tempProduct.setId(product.getId());
        tempProduct.setPrice(minAttrValue.getPrice());
        tempProduct.setStock(attrValueList.stream().mapToInt(ProductAttrValue::getStock).sum());
        if (!product.getCateId().equals(request.getCateId())) {
            tempProduct.setCateId(request.getCateId());
        }
        return transactionTemplate.execute(e -> {
            boolean update = updateById(tempProduct);
            if (!update) {
                LOGGER.error("免审编辑商品失败，商品id = {}", tempProduct.getId());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            update = productAttrValueService.updateBatchById(attrValueList, 100);
            if (!update) {
                LOGGER.error("免审编辑商品规格属性失败，商品id = {}", tempProduct.getId());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 获取复制商品配置
     * @return copyType 复制类型：1：一号通
     *         copyNum 复制条数(一号通类型下有值)
     */
    @Override
    public MyRecord copyConfig() {
        String copyType = systemConfigService.getValueByKey("system_product_copy_type");
        if (StrUtil.isBlank(copyType)) {
            throw new CrmebException("请先进行采集商品配置");
        }
        int copyNum = 0;
        if (copyType.equals("1")) {// 一号通
            OnePassUserInfoVo info = onePassService.info();
            copyNum = Optional.ofNullable(info.getCopy().getNum()).orElse(0);
        }
        MyRecord record = new MyRecord();
        record.set("copyType", copyType);
        record.set("copyNum", copyNum);
        return record;
    }

    /**
     * 复制平台商品
     * @param url 商品链接
     * @return MyRecord
     */
    @Override
    public MyRecord copyProduct(String url) {
        // 校验当前商户的copy余量
        SystemAdmin currentMerchantAdmin = SecurityUtil.getLoginUserVo().getUser();
        Merchant currentMerchant = merchantService.getByIdException(currentMerchantAdmin.getMerId());
        if(currentMerchant.getCopyProductNum() <= 0){
            throw new CrmebException("当前商户采集商品数量不足");
        }
        JSONObject jsonObject = onePassService.copyGoods(url);
        ProductResponseForCopyProduct productResponseForCopyProduct = ProductUtils.onePassCopyTransition(jsonObject);
        MyRecord record = new MyRecord();
        return record.set("info", productResponseForCopyProduct);
    }

    /**
     * 获取商品Map
     * @param proIdList 商品id列表
     * @return Map
     */
    @Override
    public Map<Integer, Product> getMapByIdList(List<Integer> proIdList) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getName, Product::getPrice, Product::getImage, Product::getIsShow, Product::getIsDel);
        lqw.in(Product::getId, proIdList);
        List<Product> productList = dao.selectList(lqw);
        Map<Integer, Product> productMap = CollUtil.newHashMap();
        productList.forEach(e -> {
            productMap.put(e.getId(), e);
        });
        return productMap;
    }

    private Product getByIdException(Integer id) {
        Product Product = getById(id);
        if (ObjectUtil.isNull(Product) || Product.getIsDel()) {
            throw new CrmebException("商品不存在");
        }
        return Product;
    }

}

