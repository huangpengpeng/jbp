package com.jbp.common.model.pay;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 收款公司信息【统计公司能用的收款信息】
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "pay_company_info", autoResultMap = true)
@ApiModel(value = "PayCompanyInfo对象", description = "付款公司资料信息")
public class PayCompanyInfo extends BaseModel {



    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公司名称")
    private String name;

    @ApiModelProperty(value = "信用代码")
    private String creditCode;

    @ApiModelProperty(value = "营业执照")
    private String businessLicenseUrl;

    @ApiModelProperty(value = "开户许可")
    private String accountUrl;

    @ApiModelProperty(value = "法人名称")
    private String legalName;

    @ApiModelProperty(value = "法人手机")
    private String legalMobile;

    @ApiModelProperty(value = "法人正")
    private String legalFrontUrl;

    @ApiModelProperty(value = "法人反")
    private String legalBackUrl;

    @ApiModelProperty(value = "联系人名称")
    private String contactsName;

    @ApiModelProperty(value = "联系人手机")
    private String contactsMobile;

    @ApiModelProperty(value = "状态 启用 停用  作废")
    private String status;

    @ApiModelProperty(value = "备注信息")
    private String remark;
}
