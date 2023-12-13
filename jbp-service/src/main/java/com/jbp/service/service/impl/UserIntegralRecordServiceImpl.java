package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.Constants;
import com.jbp.common.constants.DateConstants;
import com.jbp.common.constants.IntegralRecordConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserIntegralRecord;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.IntegralPageSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.IntegralRecordPageResponse;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.service.dao.UserIntegralRecordDao;
import com.jbp.service.service.UserIntegralRecordService;
import com.jbp.service.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户积分记录Service实现类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class UserIntegralRecordServiceImpl extends ServiceImpl<UserIntegralRecordDao, UserIntegralRecord> implements UserIntegralRecordService {

    private static final Logger logger = LoggerFactory.getLogger(UserIntegralRecordServiceImpl.class);

    @Resource
    private UserIntegralRecordDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 根据订单编号、uid获取记录列表
     * @param orderNo 订单编号
     * @param uid 用户uid
     * @return 记录列表
     */
    @Override
    public List<UserIntegralRecord> findListByOrderNoAndUid(String orderNo, Integer uid) {
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserIntegralRecord::getUid, uid);
        lqw.eq(UserIntegralRecord::getLinkId, orderNo);
        lqw.in(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_CREATE, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        List<UserIntegralRecord> recordList = dao.selectList(lqw);
        if (CollUtil.isEmpty(recordList)) {
            return recordList;
        }
        for (int i = 0; i < recordList.size();) {
            UserIntegralRecord record = recordList.get(i);
            if (record.getType().equals(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD)) {
                if (record.getStatus().equals(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE)) {
                    recordList.remove(i);
                    continue;
                }
            }
            i++;
        }
        return recordList;
    }

    /**
     * 积分解冻
     */
    @Override
    public void integralThaw() {
        // 查询需要解冻的积分
        List<UserIntegralRecord> thawList = findThawList();
        if (CollUtil.isEmpty(thawList)) {
            return;
        }
        for (UserIntegralRecord record : thawList) {
            // 查询对应的用户
            User user = userService.getById(record.getUid());
            if (ObjectUtil.isNull(user)) {
                continue ;
            }
            record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            // 计算积分余额
            Integer balance = user.getIntegral() + record.getIntegral();
            record.setBalance(balance);
            record.setUpdateTime(DateUtil.date());

            // 解冻
            Boolean execute = transactionTemplate.execute(e -> {
                updateById(record);
                userService.updateIntegral(record.getUid(), record.getIntegral(), Constants.OPERATION_TYPE_ADD);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("积分解冻处理—解冻出错，记录id = {}", record.getId()));
            }
        }
    }

    /**
     * H5用户积分列表
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    @Override
    public PageInfo<UserIntegralRecord> findUserIntegralRecordList(Integer uid, PageParamRequest pageParamRequest) {
        Page<UserIntegralRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(UserIntegralRecord::getId, UserIntegralRecord::getTitle, UserIntegralRecord::getType, UserIntegralRecord::getIntegral, UserIntegralRecord::getUpdateTime);
        lqw.eq(UserIntegralRecord::getUid, uid);
        lqw.eq(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        lqw.orderByDesc(UserIntegralRecord::getUpdateTime);
        List<UserIntegralRecord> integralRecordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, integralRecordList);
    }

    /**
     * 获取需要解冻的记录列表
     * @return 记录列表
     */
    private List<UserIntegralRecord> findThawList() {
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.le(UserIntegralRecord::getThawTime, System.currentTimeMillis());
        lqw.eq(UserIntegralRecord::getLinkType, IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserIntegralRecord::getType, IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        lqw.eq(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN);
        return dao.selectList(lqw);
    }

    /**
     * 根据订单号跟类型获取记录（一个订单同类型只会有一条数据）
     * @param orderNo 订单编号
     * @param type 类型：1-增加，2-扣减
     * @return UserIntegralRecord
     */
    @Override
    public UserIntegralRecord getByOrderNoAndType(String orderNo, Integer type) {
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserIntegralRecord::getLinkId, orderNo);
        lqw.eq(UserIntegralRecord::getLinkType, IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserIntegralRecord::getType, type);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 用户累计积分
     * @param uid 用户id
     * @return 用户累计积分
     */
    @Override
    public Integer getSettledIntegralByUid(Integer uid) {
        QueryWrapper<UserIntegralRecord> query = Wrappers.query();
        query.select("IFNULL(sum(integral), 0) as integral");
        query.ne("link_type", IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER_REFUND);
        query.eq("type", IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        query.eq("status", IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        query.eq("uid", uid);
        UserIntegralRecord userIntegralRecord = dao.selectOne(query);
        return userIntegralRecord.getIntegral();
    }

    /**
     * 用户冻结积分
     * @param uid 用户id
     * @return 用户冻结积分
     */
    @Override
    public Integer getFreezeIntegralByUid(Integer uid) {
        QueryWrapper<UserIntegralRecord> query = Wrappers.query();
        query.select("IFNULL(sum(integral), 0) as integral");
        query.eq("type", IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        query.in("status", IntegralRecordConstants.INTEGRAL_RECORD_STATUS_CREATE, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN);
        query.eq("uid", uid);
        UserIntegralRecord userIntegralRecord = dao.selectOne(query);
        return userIntegralRecord.getIntegral();
    }

    /**
     * 管理端查询积分记录分页列表
     * @param request 搜索条件
     * @param pageRequest 分页参数
     * @return 记录列表
     */
    @Override
    public PageInfo<IntegralRecordPageResponse> findRecordPageListByPlat(IntegralPageSearchRequest request, PageParamRequest pageRequest) {
        Page<UserIntegralRecord> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
//        if (ObjectUtil.isNotNull(request.getUid())) {
//            lqw.eq(UserIntegralRecord::getUid, request.getUid());
//        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.eq(UserIntegralRecord::getLinkId, keywords);
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException("开始时间不能大于结束时间！");
            }

            lqw.between(UserIntegralRecord::getUpdateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lqw.orderByDesc(UserIntegralRecord::getUpdateTime);
        List<UserIntegralRecord> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<Integer> uidList = list.stream().map(UserIntegralRecord::getUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);
        List<IntegralRecordPageResponse> responseList = list.stream().map(i -> {
            IntegralRecordPageResponse response = new IntegralRecordPageResponse();
            BeanUtils.copyProperties(i, response);
            // 获取用户昵称
            response.setNickName(userMap.get(i.getUid()).getNickname());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }
}

