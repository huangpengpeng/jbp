package com.jbp.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class UserRelationGplotVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户账户")
    private String uAccount;

    @ApiModelProperty("用户昵称")
    private String uNickName;

    @ApiModelProperty("等级名称")
    private Long ucapaId;

    @ApiModelProperty("层级")
    private Integer level;

    @ApiModelProperty("注册时间")
    private Date createTime;

    @ApiModelProperty("位置自己往上查询，自己在上级的0  区 或者 1区")
    private Integer node;

    @ApiModelProperty(value = "一区总业绩")
    private BigDecimal totalScore = BigDecimal.ZERO;

    @ApiModelProperty(value = "二区总业绩")
    private BigDecimal totalScore2  = BigDecimal.ZERO;

    @ApiModelProperty(value = "一区各等级人数")
    List<Integer> capaSumMap;

    @ApiModelProperty(value = "二区各等级人数")
    List<Integer> capaSumMap2;

    @ApiModelProperty("节点集合")
    private List<UserRelationGplotVo> children;







}
