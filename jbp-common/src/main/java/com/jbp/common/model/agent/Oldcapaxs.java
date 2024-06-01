package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Maps;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.RiseConditionListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 市场等级
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "olduserxs", autoResultMap = true)
@ApiModel(value="oldcapaxs对象", description="")
public class Oldcapaxs extends BaseModel {

    private static final long serialVersionUID = -3068573610140753926L;

    @TableField("account")
    private String account;

    @TableField(value = "capa_id")
    private String capaId;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "rphone" )
    private String rphone;

}
