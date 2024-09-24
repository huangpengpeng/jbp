package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.excel.LotteryRecordExcel;
import com.jbp.common.excel.RefundOrderExcel;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.LotteryRecord;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LotteryRecordEditRequest;
import com.jbp.common.request.agent.LotteryRecordFrontRequest;
import com.jbp.common.request.agent.LotteryRecordSearchRequest;
import com.jbp.common.vo.FileResultVo;
import com.jbp.service.dao.agent.LotteryRecordDao;
import com.jbp.service.service.UploadService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.LotteryRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LotteryRecordServiceImpl extends ServiceImpl<LotteryRecordDao, LotteryRecord> implements LotteryRecordService {
    @Autowired
    private LotteryRecordDao dao;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private UserService userService;

    @Override
    public PageInfo<LotteryRecord> pageList(Integer uid, Integer prizeType, Date startTime, Date endTime, PageParamRequest pageParamRequest) {
        Page<LotteryRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<LotteryRecord> list = dao.getList(uid, prizeType, startTime, endTime);
        return CommonPage.copyPageInfo(page,list);
    }

    @Override
    public boolean edit(LotteryRecordEditRequest request) {
        LotteryRecord lotteryRecord = getById(request.getId());
        if (lotteryRecord==null){
            throw new CrmebException("中奖记录不存在");
        }
        lotteryRecord.setRealName(request.getRealName());
        lotteryRecord.setUserPhone(request.getUserPhone());
        lotteryRecord.setProvince(request.getProvince());
        lotteryRecord.setCity(request.getCity());
        lotteryRecord.setDistrict(request.getDistrict());
        lotteryRecord.setStreet(request.getStreet());
        lotteryRecord.setAddress(request.getAddress());

        String userAddressStr = request.getProvince() + request.getCity() + request.getDistrict() + request.getStreet() + request.getAddress();
        lotteryRecord.setUserAddress(userAddressStr);
        return updateById(lotteryRecord);
    }

    @Override
    public List<LotteryRecord> getFrontList(Integer id, Integer uid) {
        return dao.getFrontList(id,uid);
    }

    @Override
    public String export(Integer uid, Integer prizeType, Date startTime, Date endTime) {
        List<LotteryRecord> list = dao.getList(uid, prizeType, startTime, endTime);
        if (list.isEmpty()){
            throw new CrmebException("未查询到中奖记录数据！");
        }
        log.info("中奖记录导出数据查询完成...");
        LinkedList<LotteryRecordExcel> result = new LinkedList<>();
        for (LotteryRecord record : list) {
            LotteryRecordExcel vo = new LotteryRecordExcel();
            BeanUtils.copyProperties(record, vo);
            if (record.getPrizeType() == 1) {
                vo.setPrizeType("谢谢参与");
            }
            if (record.getPrizeType() == 2) {
                vo.setPrizeType("普通奖品");
            }
            result.add(vo);
        }
        FileResultVo fileResultVo = uploadService.excelLocalUpload(result, LotteryRecordExcel.class);
        log.info("中奖记录导出下载地址:" + fileResultVo.getUrl());
        return fileResultVo.getUrl();

    }

    @Override
    public Boolean address(LotteryRecordFrontRequest request) {
        Integer uid = userService.getUserId();
        if (uid == null) {
            throw new CrmebException("请先登录！");
        }
        LotteryRecord lotteryRecord = getById(request.getId());
        if (lotteryRecord==null){
            throw new CrmebException("中奖记录不存在");
        }
        lotteryRecord.setRealName(request.getRealName());
        lotteryRecord.setUserPhone(request.getUserPhone());
        lotteryRecord.setProvince(request.getProvince());
        lotteryRecord.setCity(request.getCity());
        lotteryRecord.setDistrict(request.getDistrict());
        lotteryRecord.setStreet(request.getStreet());
        lotteryRecord.setAddress(request.getAddress());

        String userAddressStr = request.getProvince() + request.getCity() + request.getDistrict() + request.getStreet() + request.getAddress();
        lotteryRecord.setUserAddress(userAddressStr);
        return updateById(lotteryRecord);
    }

    @Override
    public LotteryRecord noAddress(Integer uid) {
        return dao.getLast(uid);
    }


}
