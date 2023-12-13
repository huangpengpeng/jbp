package com.jbp.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import com.jbp.admin.service.HomeService;
import com.jbp.admin.service.UserStatisticsService;
import com.jbp.common.constants.DateConstants;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.record.ProductDayRecord;
import com.jbp.common.model.user.User;
import com.jbp.common.request.ProductRankingRequest;
import com.jbp.common.response.*;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户表 服务实现类
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
public class HomeServiceImpl implements HomeService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserVisitRecordService userVisitRecordService;
    @Autowired
    private ProductService productService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private UserStatisticsService userStatisticsService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private ProductDayRecordService productDayRecordService;
    @Autowired
    private MerchantDayRecordService merchantDayRecordService;

    /**
     * 首页数据
     * @return HomeRateResponse
     */
    @Override
    public HomeRateResponse indexMerchantDate() {
        String today = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        String yesterday = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        HomeRateResponse response = new HomeRateResponse();
        response.setSales(orderService.getPayOrderAmountByDate(systemAdmin.getMerId(), today));
        response.setYesterdaySales(orderService.getPayOrderAmountByDate(systemAdmin.getMerId(), yesterday));
        response.setOrderNum(orderService.getOrderNumByDate(systemAdmin.getMerId(), today));
        response.setYesterdayOrderNum(orderService.getOrderNumByDate(systemAdmin.getMerId(), yesterday));
        response.setFollowNum(userMerchantCollectService.getCountByMerId(systemAdmin.getMerId()));
        response.setVisitorsNum(merchantDayRecordService.getVisitorsByDate(systemAdmin.getMerId(), today));
        response.setYesterdayVisitorsNum(merchantDayRecordService.getVisitorsByDate(systemAdmin.getMerId(), yesterday));
        return response;
    }

    /**
     * 经营数据：
     * @return HomeOperatingMerDataResponse
     */
    @Override
    public HomeOperatingMerDataResponse operatingMerchantData() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        HomeOperatingMerDataResponse response = new HomeOperatingMerDataResponse();
        response.setNotShippingOrderNum(orderService.getNotShippingNum(systemAdmin.getMerId()));
        response.setAwaitVerificationOrderNum(orderService.getAwaitVerificationNum(systemAdmin.getMerId()));
        response.setRefundingOrderNum(refundOrderService.getAwaitAuditNum(systemAdmin.getMerId()));
        response.setOnSaleProductNum(productService.getOnSaleNum(systemAdmin.getMerId()));
        response.setAwaitAuditProductNum(productService.getAwaitAuditNum(systemAdmin.getMerId()));
        return response;
    }

    /**
     * 平台端首页数据
     * @return PlatformHomeRateResponse
     */
    @Override
    public PlatformHomeRateResponse indexPlatformDate() {
        String today = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        String yesterday = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
        PlatformHomeRateResponse response = new PlatformHomeRateResponse();
        response.setTodayNewUserNum(userService.getRegisterNumByDate(today));
        response.setYesterdayNewUserNum(userService.getRegisterNumByDate(yesterday));
        response.setPageviews(userVisitRecordService.getPageviewsByDate(today));
        response.setYesterdayPageviews(userVisitRecordService.getPageviewsByDate(yesterday));
        response.setTodayNewMerchantNum(merchantService.getNewNumByDate(today));
        response.setYesterdayNewMerchantNum(merchantService.getNewNumByDate(yesterday));
        response.setOrderNum(orderService.getOrderNumByDate(0, today));
        response.setYesterdayOrderNum(orderService.getOrderNumByDate(0, yesterday));
        response.setSales(orderService.getPayOrderAmountByDate(0, today));
        response.setYesterdaySales(orderService.getPayOrderAmountByDate(0, yesterday));
        response.setUserNum(userService.getTotalNum());
        response.setMerchantNum(merchantService.getAllCount());
        return response;
    }

    /**
     * 平台端首页经营数据
     * @return HomeOperatingDataResponse
     */
    @Override
    public HomeOperatingDataResponse operatingPlatformData() {
        HomeOperatingDataResponse response = new HomeOperatingDataResponse();
        response.setNotShippingOrderNum(orderService.getNotShippingNum(0));
        response.setAwaitVerificationOrderNum(orderService.getAwaitVerificationNum(0));
        response.setRefundingOrderNum(refundOrderService.getAwaitAuditNum(0));
        response.setOnSaleProductNum(productService.getOnSaleNum(0));
        response.setAwaitAuditProductNum(productService.getAwaitAuditNum(0));
        return response;
    }

    /**
     * 平台端首页获取用户渠道数据
     */
    @Override
    public List<UserChannelDataResponse> getUserChannelData() {
        List<User> userList = userService.getChannelData();
        return userList.stream().map(e -> {
            UserChannelDataResponse response = new UserChannelDataResponse();
            response.setRegisterType(e.getRegisterType());
            response.setNum(e.getPayCount());
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 商户端商品支付排行榜
     */
    @Override
    public List<ProductRankingResponse> merchantProductPayRanking() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        ProductRankingRequest request = new ProductRankingRequest();
        request.setMerId(merId);
        request.setDateLimit(DateConstants.SEARCH_DATE_LATELY_7);
        request.setSortKey("salesAmount");
        PageInfo<ProductDayRecord> pageInfo = productDayRecordService.getRanking(request);
        List<ProductDayRecord> recordList = pageInfo.getList();
        List<ProductRankingResponse> list = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(recordList)) {
            for (ProductDayRecord record : recordList) {
                Product product = productService.getById(record.getProductId());
                ProductRankingResponse response = new ProductRankingResponse();
                BeanUtils.copyProperties(record, response);
                response.setSalesAmount(record.getOrderSuccessProductFee());
                response.setProductId(product.getId());
                response.setProName(product.getName());
                response.setImage(product.getImage());
                list.add(response);
            }
        }
        return list;
    }

    /**
     * 商品浏览量排行榜
     */
    @Override
    public List<ProductRankingResponse> merchantProductPageviewRanking() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        ProductRankingRequest request = new ProductRankingRequest();
        request.setMerId(merId);
        request.setDateLimit(DateConstants.SEARCH_DATE_LATELY_7);
        request.setSortKey("pageviews");
        PageInfo<ProductDayRecord> pageInfo = productDayRecordService.getRanking(request);
        List<ProductDayRecord> recordList = pageInfo.getList();
        List<ProductRankingResponse> list = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(recordList)) {
            for (ProductDayRecord record : recordList) {
                Product product = productService.getById(record.getProductId());
                ProductRankingResponse response = new ProductRankingResponse();
                BeanUtils.copyProperties(record, response);
                response.setPageView(record.getPageView());
                response.setProductId(product.getId());
                response.setProName(product.getName());
                response.setImage(product.getImage());
                list.add(response);
            }
        }
        return list;
    }

}
