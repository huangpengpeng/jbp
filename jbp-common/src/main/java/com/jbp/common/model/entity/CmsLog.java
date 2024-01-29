package com.jbp.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@TableName("eb_cms_log")
@NoArgsConstructor
public class CmsLog extends BaseModel {

    private static final long serialVersionUID = -2429497664515698520L;
    @ApiModelProperty("账户名称")
    @TableField("manager")
    private String manager;
    @ApiModelProperty("标题")
    @TableField("title")
    private String title;
    @ApiModelProperty("内容")
    @TableField("content")
    private String content;
    @ApiModelProperty("IP地址")
    @TableField("ip")
    private String ip;

    public CmsLog(String manager, String title, String content, String ip) {
        this.manager = manager;
        this.title = title;
        this.content = content;
        this.ip = ip;
    }


    public enum TITLE_ENUM {
        提现订单, 佣金订单, 钱包管理
    }
}
