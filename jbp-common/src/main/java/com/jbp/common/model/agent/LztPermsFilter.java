package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lzt_perms_filter", autoResultMap = true)
@ApiModel(value="lztPermsFilter对象", description="来账通权限过滤")
@NoArgsConstructor
public class LztPermsFilter implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "账户编号")
    @TableField(value = "userId")
    private String userId;

    @ApiModelProperty(value = "操作")
    @TableField(value = "operation")
    private String operation;
}
