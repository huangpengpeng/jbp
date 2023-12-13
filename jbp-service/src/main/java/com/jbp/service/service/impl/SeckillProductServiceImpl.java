package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.Constants;
import com.jbp.common.constants.DateConstants;
import com.jbp.common.constants.ProductConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductDescription;
import com.jbp.common.model.seckill.SeckillActivityTime;
import com.jbp.common.model.seckill.SeckillProduct;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.SeckillProductSearchRequest;
import com.jbp.common.response.SeckillProductPageResponse;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.service.dao.SeckillProductDao;
import com.jbp.service.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
*  SeckillProductServiceImpl 接口实现
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
@Service
public class SeckillProductServiceImpl extends ServiceImpl<SeckillProductDao, SeckillProduct> implements SeckillProductService {

    @Resource
    private SeckillProductDao dao;

    @Autowired
    private SeckillActivityService seckillActivityService;
    @Autowired
    private SeckillTimeIntervalService seckillTimeIntervalService;
    @Autowired
    private SeckillActivityTimeService seckillActivityTimeService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private ProductDescriptionService productDescriptionService;



    /**
     * 获取活动秒杀商品数量
     * @param activityId 活动ID
     * @return 活动秒杀商品数量
     */
    @Override
    public Integer getCountByActivityId(Integer activityId) {
        LambdaQueryWrapper<SeckillProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(SeckillProduct::getId);
        lqw.eq(SeckillProduct::getActivityId, activityId);
        lqw.eq(SeckillProduct::getIsDel, 0);
        return dao.selectCount(lqw);
    }

    /**
     * 获取秒杀活动商品
     * @param activityId 活动ID
     */
    @Override
    public List<SeckillProduct> findByActivityId(Integer activityId) {
        LambdaQueryWrapper<SeckillProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(SeckillProduct::getActivityId, activityId);
        lqw.eq(SeckillProduct::getAuditStatus, 2);
        lqw.eq(SeckillProduct::getIsDel, 0);
        lqw.orderByDesc(SeckillProduct::getId);
        return dao.selectList(lqw);
    }

    /**
     * 获取秒杀商品分页列表
     * @param request 搜索参数
     * @param pageRequest   分页参数
     */
    @Override
    public PageInfo<SeckillProductPageResponse> getSeckillProductPage(SeckillProductSearchRequest request, PageParamRequest pageRequest) {
        Map<String, Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(request.getProName())) {
            map.put("proName", URLUtil.decode(request.getProName()));
        }
        if (ObjectUtil.isNotNull(request.getProStatus())) {
            map.put("proStatus", request.getProStatus());
        }
        if (ObjectUtil.isNotNull(request.getActivityStatus())) {
            if (request.getActivityStatus().equals(1)) {
                DateTime dateTime = DateUtil.date();
                String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
                String hmStr = dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM);
                List<SeckillActivityTime> activityTimeList = seckillActivityTimeService.findByActivityStatus(request.getActivityStatus(), Integer.valueOf(dateStr), Integer.valueOf(hmStr));
                if (CollUtil.isEmpty(activityTimeList)) {
                    return null;
                }
                String activityIdStr = activityTimeList.stream().map(e -> String.valueOf(e.getSeckillId())).distinct().collect(Collectors.joining(","));
                map.put("activityIdStr", activityIdStr);
            }
            if (request.getActivityStatus().equals(2)) {
                DateTime dateTime = DateUtil.date();
                String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
                String hmStr = dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM);
                List<SeckillActivityTime> activityTimeList = seckillActivityTimeService.findByActivityStatus(request.getActivityStatus(), Integer.valueOf(dateStr), Integer.valueOf(hmStr));
                if (CollUtil.isEmpty(activityTimeList)) {
                    return null;
                }
                String activityIdStr = activityTimeList.stream().map(e -> String.valueOf(e.getSeckillId())).distinct().collect(Collectors.joining(","));
                map.put("activityIdStr", activityIdStr);
            }
        }
        if (StrUtil.isNotBlank(request.getActivityName())) {
            map.put("activityName", URLUtil.decode(request.getActivityName()));
        }
        if (ObjectUtil.isNotNull(request.getMerStars()) && request.getMerStars() > 0) {
            map.put("merStars", request.getMerStars());
        }
        if (StrUtil.isNotBlank(request.getMerIds())) {
            map.put("merIds", request.getMerIds());
        }
        if (ObjectUtil.isNotNull(request.getAuditStatus())) {
            map.put("auditStatus", request.getAuditStatus());
        }
        Page<SeckillProduct> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        List<SeckillProductPageResponse> productList = dao.getSeckillProductPage(map);
        if (CollUtil.isNotEmpty(productList)) {
            setActivityStatus(productList);
            productList.forEach(p -> {
               p.setAttrValue(productAttrValueService.getListByProductIdAndType(p.getId(), ProductConstants.PRODUCT_TYPE_SECKILL));
            });
        }
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 强制下架
     * @param ids 商品id，英文逗号拼接
     */
    @Override
    public Boolean forceDown(String ids) {
        LambdaUpdateWrapper<SeckillProduct> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(SeckillProduct::getIsShow, 0);
        wrapper.set(SeckillProduct::getAuditStatus, 3);
        wrapper.set(SeckillProduct::getReason, "平台强制下架");
        wrapper.in(SeckillProduct::getId, CrmebUtil.stringToArray(ids));
        return update(wrapper);
    }

    /**
     * 删除
     * @param ids 商品id，英文逗号拼接
     */
    @Override
    public Boolean delete(String ids) {
        LambdaUpdateWrapper<SeckillProduct> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(SeckillProduct::getIsDel, 1);
        wrapper.in(SeckillProduct::getId, CrmebUtil.stringToArray(ids));
        return update(wrapper);
    }

    /**
     * 上架
     * @param ids 商品id，英文逗号拼接
     */
    @Override
    public Boolean up(String ids, Integer merId) {
        LambdaUpdateWrapper<SeckillProduct> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(SeckillProduct::getIsShow, 1);
        wrapper.in(SeckillProduct::getId, CrmebUtil.stringToArray(ids));
        wrapper.eq(SeckillProduct::getMerId, merId);
        return update(wrapper);
    }

    /**
     * 下架
     * @param ids 商品id，英文逗号拼接
     */
    @Override
    public Boolean down(String ids, Integer merId) {
        LambdaUpdateWrapper<SeckillProduct> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(SeckillProduct::getIsShow, 0);
        wrapper.in(SeckillProduct::getId, CrmebUtil.stringToArray(ids));
        wrapper.eq(SeckillProduct::getMerId, merId);
        return update(wrapper);
    }

    /**
     * 首页秒杀商品列表
     * @param aidList 秒杀活动ID列表
     * @return 首页秒杀商品列表
     */
    @Override
    public List<SeckillProduct> getIndexList(List<Integer> aidList) {
        LambdaQueryWrapper<SeckillProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(SeckillProduct::getId, SeckillProduct::getName, SeckillProduct::getSeckillPrice, SeckillProduct::getImage, SeckillProduct::getPrice);
        lqw.in(SeckillProduct::getActivityId, aidList);
        lqw.eq(SeckillProduct::getIsShow, 1);
        lqw.eq(SeckillProduct::getIsDel, 0);
        lqw.eq(SeckillProduct::getAuditStatus, 2);
        lqw.orderByDesc(SeckillProduct::getSort);
        lqw.last(" limit 8");
        return dao.selectList(lqw);
    }

    /**
     * 移动端秒杀商品列表
     * @param seckillIdList 秒杀ID列表
     * @param pageRequest 分页参数
     * @return
     */
    @Override
    public PageInfo<SeckillProduct> getFrontPage(List<Integer> seckillIdList, PageParamRequest pageRequest) {
        Page<SeckillProduct> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<SeckillProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(SeckillProduct::getId, SeckillProduct::getName, SeckillProduct::getPrice, SeckillProduct::getImage, SeckillProduct::getSeckillPrice, SeckillProduct::getQuota, SeckillProduct::getQuotaShow, SeckillProduct::getMerId);
        lqw.in(SeckillProduct::getActivityId, seckillIdList);
        lqw.eq(SeckillProduct::getIsShow, 1);
        lqw.eq(SeckillProduct::getIsDel, 0);
        lqw.eq(SeckillProduct::getAuditStatus, 2);
        lqw.last(" order by sort desc, (quota_show - quota) desc");
        List<SeckillProduct> productList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 获取移动端秒杀商品详情
     * @param id 秒杀商品ID
     * @return 秒杀商品详情
     */
    @Override
    public SeckillProduct getFrontDetail(Integer id) {
        LambdaQueryWrapper<SeckillProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(SeckillProduct::getId, id);
        lqw.eq(SeckillProduct::getIsShow, 1);
        lqw.eq(SeckillProduct::getIsDel, 0);
        lqw.eq(SeckillProduct::getAuditStatus, 2);
        lqw.last(" limit 1");
        SeckillProduct seckillProduct = dao.selectOne(lqw);
        if (ObjectUtil.isNull(seckillProduct)) {
            throw new CrmebException(StrUtil.format("没有找到ID： {} 的秒杀商品", id));
        }
        ProductDescription sd = productDescriptionService.getByProductIdAndType(seckillProduct.getId(), ProductConstants.PRODUCT_TYPE_SECKILL);
        if (ObjectUtil.isNotNull(sd)) {
            seckillProduct.setContent(StrUtil.isBlank(sd.getDescription()) ? "" : sd.getDescription());
        }
        return seckillProduct;
    }

    /**
     * 添加/扣减库存
     * @param id 商品id
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String type) {
        UpdateWrapper<SeckillProduct> updateWrapper = new UpdateWrapper<>();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            updateWrapper.setSql(StrUtil.format("quota = quota + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
//            updateWrapper.setSql(StrUtil.format("quota_show = quota_show + {}", num));
        }
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            updateWrapper.setSql(StrUtil.format("quota = quota - {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales + {}", num));
            // 扣减时加乐观锁保证库存不为负
            updateWrapper.last(StrUtil.format(" and (quota - {} >= 0)", num));
        }
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新秒杀商品库存失败,商品id = " + id);
        }
        return update;
    }

    /**
     * 更新活动状态
     * @param productList 商品列表
     */
    private void setActivityStatus(List<SeckillProductPageResponse> productList) {
        DateTime dateTime = DateUtil.date();
        String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
        String hmStr = dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM);
        for (SeckillProductPageResponse product : productList) {
            if (product.getActivityStatus().equals(2)) {
                continue;
            }
            Integer activityStatus = seckillActivityTimeService.getActivityStatus(product.getActivityId(), Integer.valueOf(dateStr), Integer.valueOf(hmStr));
            product.setActivityStatus(activityStatus);
        }
    }
}

