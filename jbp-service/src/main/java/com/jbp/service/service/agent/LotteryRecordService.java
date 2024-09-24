package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LotteryRecord;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LotteryRecordEditRequest;
import com.jbp.common.request.agent.LotteryRecordFrontRequest;
import com.jbp.common.request.agent.LotteryRecordSearchRequest;
import com.jbp.common.vo.MyRecord;

import java.util.Date;
import java.util.List;

public interface LotteryRecordService extends IService<LotteryRecord> {
    PageInfo<LotteryRecord> pageList(Integer uid, Integer prizeType, Date startTime, Date endTime, PageParamRequest pageParamRequest);

    boolean edit(LotteryRecordEditRequest request);

    List<LotteryRecord> getFrontList(Integer id, Integer uid);

    String export(Integer uid, Integer prizeType, Date startTime, Date endTime);

    Boolean address(LotteryRecordFrontRequest request);

    LotteryRecord noAddress(Integer uid);
}
