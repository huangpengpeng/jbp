package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.LotteryRecord;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LotteryRecordEditRequest;
import com.jbp.service.dao.agent.LotteryRecordDao;
import com.jbp.service.service.agent.LotteryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LotteryRecordServiceImpl extends ServiceImpl<LotteryRecordDao, LotteryRecord> implements LotteryRecordService {
    @Autowired
    private LotteryRecordDao dao;

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
        lotteryRecord.setRemark(request.getRemark());
        return updateById(lotteryRecord);
    }
}
