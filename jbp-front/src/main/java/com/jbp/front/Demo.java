package com.jbp.front;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.request.agent.ScoreDownloadRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.agent.InvitationScoreService;

public class Demo {

    public void init(InvitationScoreService service) {

        SqlRunner.db().update("truncate table eb_score_download_excel ");
        ScoreDownloadRequest request = new ScoreDownloadRequest();
        request.setName("总代");
        request.setCapaIdXsList(Lists.newArrayList(15L));
        request.setStartTime(DateTimeUtils.parseDate("2024-08-01"));
        request.setEndTime(DateTimeUtils.parseDate("2024-09-01"));
        service.download(request);

        request.setName("总代省代");
        request.setCapaIdXsList(Lists.newArrayList(14L, 15L));
        request.setStartTime(DateTimeUtils.parseDate("2024-08-01"));
        request.setEndTime(DateTimeUtils.parseDate("2024-09-01"));
        service.download(request);
    }

}
