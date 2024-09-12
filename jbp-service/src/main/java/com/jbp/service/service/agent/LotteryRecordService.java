package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LotteryRecord;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LotteryRecordEditRequest;
import org.apache.poi.ss.formula.functions.T;

import java.util.Date;
import java.util.List;

public interface LotteryRecordService extends IService<LotteryRecord> {
    PageInfo<LotteryRecord> pageList(Integer uid, Integer prizeType, Date startTime, Date endTime, PageParamRequest pageParamRequest);

    boolean edit(LotteryRecordEditRequest request);

    List<LotteryRecord> getFrontListByLotteryId(Integer id);
}
