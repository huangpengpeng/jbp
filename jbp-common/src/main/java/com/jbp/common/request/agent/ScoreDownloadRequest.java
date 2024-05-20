package com.jbp.common.request.agent;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ScoreDownloadRequest对象", description = "业绩下载")
public class ScoreDownloadRequest {

    @ApiModelProperty("账户列表 可以为空 复选框多个账号换行")
    private List<String> accountList;

    @ApiModelProperty("等级列表 可以为空 多选下拉选")
    private List<Long> capaIdList;

    @ApiModelProperty("星级列表  可以为空 多选下拉选")
    private List<Long> capaIdXsList;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("开始时间 不能为空")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("结束时间 不能为空")
    private Date endTime;

}
