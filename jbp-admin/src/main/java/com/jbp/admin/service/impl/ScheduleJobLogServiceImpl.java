package com.jbp.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.admin.dao.ScheduleJobLogDao;
import com.jbp.admin.model.ScheduleJobLog;
import com.jbp.admin.service.ScheduleJobLogService;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.ScheduleJobLogSearchRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author HZW
 * @description ScheduleJobLogServiceImpl 接口实现
 * @date 2021-11-30
 */
@Service
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogDao, ScheduleJobLog> implements ScheduleJobLogService {

    @Resource
    private ScheduleJobLogDao dao;

    /**
     * 自动删除日志
     */
    @Override
    public void autoDeleteLog() {
        String beforeDate = DateUtil.offsetDay(new Date(), -9).toString("yyyy-MM-dd");
        UpdateWrapper<ScheduleJobLog> wrapper = Wrappers.update();
        wrapper.lt("create_time", beforeDate);
        dao.delete(wrapper);
    }

    /**
     * 日志分页列表
     * @param request 搜索参数
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ScheduleJobLog> findLogPageList(ScheduleJobLogSearchRequest request, PageParamRequest pageRequest) {
        Page<ScheduleJobLog> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<ScheduleJobLog> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(request.getJobId())) {
            lqw.eq(ScheduleJobLog::getJobId, request.getJobId());
        }
        if (StrUtil.isNotBlank(request.getBeanName())) {
            lqw.eq(ScheduleJobLog::getBeanName, request.getBeanName());
        }
        if (StrUtil.isNotBlank(request.getMethodName())) {
            lqw.eq(ScheduleJobLog::getMethodName, request.getMethodName());
        }
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(ScheduleJobLog::getStatus, request.getStatus());
        }
        lqw.orderByDesc(ScheduleJobLog::getLogId);
        List<ScheduleJobLog> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }
}

