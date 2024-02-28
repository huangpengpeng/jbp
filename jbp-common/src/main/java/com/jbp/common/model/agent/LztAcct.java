package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.lianlian.result.AcctInfo;
import com.jbp.common.lianlian.result.LztQueryAcctInfo;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.LztQueryAcctInfoListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lzt_acct", autoResultMap = true)
@ApiModel(value="LztAcct对象", description="来账通账户")
@NoArgsConstructor
public class LztAcct extends BaseModel {

    public LztAcct(Integer merId, String lianLianAcct, String lianLianAcctName) {
        this.merId = merId;
        this.lianLianAcct = lianLianAcct;
        this.lianLianAcctName = lianLianAcctName;
    }

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "连连账户")
    private String lianLianAcct;

    @ApiModelProperty(value = "连连账户名称")
    private String lianLianAcctName;

    @ApiModelProperty(value = "连连账户信息")
    @TableField(exist = false)
    private List<AcctInfo> acctInfoList;

    @ApiModelProperty(value = "银行账户信息")
    @TableField(exist = false)
    private List<LztQueryAcctInfo> bankAcctInfoList;

}
