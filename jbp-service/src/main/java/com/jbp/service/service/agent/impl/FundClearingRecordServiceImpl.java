package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.excel.FundClearingRecordExcel;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.FundClearingRecord;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.FundClearingRecordRequest;
import com.jbp.common.request.agent.FundClearingRecordTotalRequest;
import com.jbp.common.response.FundClearingRecordResponse;
import com.jbp.common.response.FundClearingRecordSelfResponse;
import com.jbp.common.vo.FileResultVo;
import com.jbp.service.dao.agent.FundClearingRecordDao;
import com.jbp.service.service.UploadService;
import com.jbp.service.service.agent.FundClearingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
@Slf4j
public class FundClearingRecordServiceImpl extends ServiceImpl<FundClearingRecordDao, FundClearingRecord> implements FundClearingRecordService {
    @Resource
    private FundClearingRecordDao fundClearingRecordDao;
    @Resource
    private UploadService uploadService;

    @Override
    public FundClearingRecord add(User user, User orderUser, String externalNo, String commName, BigDecimal commAmt,
                                  Integer productId, String productName, BigDecimal price, BigDecimal score, Integer quantity,
                                  BigDecimal rewardValue, String rewardType, String description) {
        FundClearingRecord record = new FundClearingRecord(user.getId(), user.getAccount(), user.getNickname(), orderUser.getId(),
                orderUser.getAccount(), orderUser.getNickname(),
                externalNo, commName, commAmt, productId, productName, price, score, quantity, rewardValue, rewardType, description);
        save(record);
        return record;
    }

    @Override
    public FundClearingRecord create(User user, User orderUser, OrderDetail orderDetail, String commName, BigDecimal commAmt, BigDecimal score, BigDecimal rewardValue, String rewardType, String description) {
        return add(user, orderUser, orderDetail.getOrderNo(), commName, commAmt,
                orderDetail.getProductId(), orderDetail.getProductName(), orderDetail.getPayPrice(), score, orderDetail.getPayNum(),
                rewardValue, rewardType, description);
    }

    @Override
    public boolean refund(String externalNo) {
        UpdateWrapper<FundClearingRecord> wrapper = new UpdateWrapper<FundClearingRecord>();
        wrapper.lambda().set(FundClearingRecord::getStatus, FundClearingRecord.Constants.取消.name())
                .eq(FundClearingRecord::getExternalNo, externalNo).eq(FundClearingRecord::getStatus, FundClearingRecord.Constants.正常.name());
        return update(wrapper);
    }

    @Override
    public PageInfo<FundClearingRecord> pageList(FundClearingRecordRequest request, PageParamRequest pageParamRequest) {
        Page<FundClearing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<FundClearingRecord> list = fundClearingRecordDao.pageList(request.getOrderAccount(),request.getAccount(),request.getStartCreateTime(),request.getEndCreateTime(), request.getOrderList());
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public void updateCancel(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new CrmebException("请选择佣金统计记录");
        }
        List<FundClearingRecord> list = list(new QueryWrapper<FundClearingRecord>().lambda().in(FundClearingRecord::getId, ids).in(FundClearingRecord::getStatus, FundClearingRecord.Constants.正常.name()));
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        List<List<FundClearingRecord>> partition = Lists.partition(list, 100);
        for (List<FundClearingRecord> subList : partition) {
            for (FundClearingRecord record : subList) {
                record.setStatus(FundClearingRecord.Constants.取消.name());
            }
            updateBatchById(subList);
        }
    }

    @Override
    public PageInfo<FundClearingRecordResponse> total(FundClearingRecordTotalRequest request, PageParamRequest pageParamRequest) {
        Page<FundClearing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<FundClearingRecordResponse> list = fundClearingRecordDao.total(request.getAccount(), request.getOrderAccount(), request.getMonth());
        return CommonPage.copyPageInfo(page, list);

    }

    @Override
    public FundClearingRecordSelfResponse selfTotal(Integer uid) {
        FundClearingRecordSelfResponse response = new FundClearingRecordSelfResponse();
        List<FundClearingRecordResponse> responses = fundClearingRecordDao.selfTotal(uid);
        BigDecimal bigDecimal = fundClearingRecordDao.selfTotalAmount(uid);
        response.setList(responses);
        response.setAmount(bigDecimal);
        return response;
    }

    @Override
    public PageInfo<FundClearingRecord> detail(Integer uid, String day,PageParamRequest pageParamRequest) {
        Page<FundClearing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<FundClearingRecord> list = fundClearingRecordDao.detail(uid,day);
        return CommonPage.copyPageInfo(page, list);

    }

    @Override
    public String export(FundClearingRecordRequest request) {
        List<FundClearingRecord> list = fundClearingRecordDao.pageList(request.getOrderAccount(),request.getAccount(),request.getStartCreateTime(),request.getEndCreateTime(), request.getOrderList());
        if (CollectionUtils.isEmpty(list)) {
            throw new CrmebException("未查询到列表数据");
        }
        log.info("订单导出运营中心明细数据查询完成...");
        LinkedList<FundClearingRecordExcel> result = new LinkedList<>();
        for (FundClearingRecord record : list) {
            FundClearingRecordExcel vo = new FundClearingRecordExcel();
            BeanUtils.copyProperties(record, vo);
            vo.setStatus(record.getStatus().equals(FundClearingRecord.Constants.取消.name()) ? "已退款" : "未退款");
            result.add(vo);
        }
        FileResultVo fileResultVo = uploadService.excelLocalUpload(result, FundClearingRecordExcel.class);
        log.info("运营中心明细导出下载地址:" + fileResultVo.getUrl());
        return fileResultVo.getUrl();
    }


}
