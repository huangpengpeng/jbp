package com.jbp.common.model.admin;
//

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_admin_ref")
@ApiModel(value="SystemAdminRef对象", description="账户关联")
public class SystemAdminRef implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "管理员ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "自己")
    @TableField("mId")
    private Integer mId;

    @ApiModelProperty(value = "关联")
    @TableField("sId")
    private Integer sId;


    @ApiModelProperty(value = "名称")
    @TableField(exist = false)
    private String name;
}
