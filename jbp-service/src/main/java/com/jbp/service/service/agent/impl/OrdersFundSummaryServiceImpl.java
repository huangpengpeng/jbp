package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.excel.OrderShipmentExcel;
import com.jbp.common.excel.OrdersFundSummaryExcel;
import com.jbp.common.excel.WalletExcel;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.OrdersFundSummary;
import com.jbp.common.model.agent.Team;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.OrdersFundSummaryRequest;
import com.jbp.common.response.OrdersFundSummaryExtResponse;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.vo.FileResultVo;
import com.jbp.service.dao.agent.OrdersFundSummaryDao;
import com.jbp.service.service.OssService;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.UploadService;
import com.jbp.service.service.agent.OrdersFundSummaryService;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
@Slf4j
public class OrdersFundSummaryServiceImpl extends ServiceImpl<OrdersFundSummaryDao, OrdersFundSummary> implements OrdersFundSummaryService {


    @Resource
    private OrdersFundSummaryDao ordersFundSummaryDao;
    @Resource
    private UploadService uploadService;
    @Resource
    private OssService ossService;


    @Override
    public PageInfo<OrdersFundSummaryExtResponse> pageList(String ordersSn, String teamId, String startPayTime, String endPayTime, PageParamRequest pageParamRequest) {

        Page<OrdersFundSummary> page = PageHelper.startPage(pageParamRequest.getPage(),pageParamRequest.getLimit());
        List<OrdersFundSummaryExtResponse> list = ordersFundSummaryDao.getList(teamId,startPayTime,endPayTime,ordersSn);
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public String export(OrdersFundSummaryRequest request) {
        List<OrdersFundSummaryExtResponse> list = ordersFundSummaryDao.getList(request.getTeamId(),request.getStartPayTime(),request.getEndPayTime(),request.getOrdersSn());
        if (CollectionUtils.isEmpty(list)) {
            throw new CrmebException("未查询到订单佣金数据");
        }
        log.info("订单汇总导出佣金数据查询完成...");
        LinkedList<OrdersFundSummaryExcel> result = new LinkedList<>();
        for(OrdersFundSummaryExtResponse response : list){
            OrdersFundSummaryExcel vo = new OrdersFundSummaryExcel();
            vo.setOrdersSn(response.getOrdersSn());
            vo.setOrdersId(response.getOrdersId());
            vo.setPv(response.getPv());
            vo.setName(response.getName());
            vo.setCommAmt(response.getCommAmt());
            vo.setPayPrice(response.getPayPrice());
            result.add(vo);
        }
//        FileResultVo fileResultVo = uploadService.excelLocalUpload(result, OrdersFundSummaryExcel.class);
//        log.info("订单汇总列表导出下载地址:" + fileResultVo.getUrl());
//        return fileResultVo.getUrl();
        String s = ossService.uploadXlsx(result, OrdersFundSummaryExcel.class, "订单汇总" + DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
        return s;
    }

    @Override
    public OrdersFundSummary create(Integer ordersId, String ordersSn, BigDecimal payPrice, BigDecimal pv) {
        if (getByOrdersSn(ordersSn) != null) {
            return null;
        }
        OrdersFundSummary summary = new OrdersFundSummary(ordersId, ordersSn, payPrice, pv);
        save(summary);
        return summary;
    }

    @Override
    public OrdersFundSummary getByOrdersSn(String ordersSn) {
        return getOne(new QueryWrapper<OrdersFundSummary>().lambda().eq(OrdersFundSummary::getOrdersSn, ordersSn));
    }

    @Override
    public OrdersFundSummary increaseCommAmt(String ordersSn, BigDecimal commAmt) {
        OrdersFundSummary summary = getByOrdersSn(ordersSn);
        if (summary != null) {
            summary.setCommAmt(summary.getCommAmt().add(commAmt));
            Boolean ifSuccess = updateById(summary);
            if (BooleanUtils.isNotTrue(ifSuccess)) {
                throw new CrmebException("当前操作人数过多");
            }
        }
        return summary;
    }

    @Override
    public OrdersFundSummary reduceCommAmt(String ordersSn, BigDecimal commAmt) {
        OrdersFundSummary summary = getByOrdersSn(ordersSn);
        if (summary != null) {
            summary.setCommAmt(summary.getCommAmt().subtract(commAmt));
            Boolean ifSuccess = updateById(summary);
            if (BooleanUtils.isNotTrue(ifSuccess)) {
                throw new CrmebException("当前操作人数过多");
            }
        }
        return summary;
    }
}
