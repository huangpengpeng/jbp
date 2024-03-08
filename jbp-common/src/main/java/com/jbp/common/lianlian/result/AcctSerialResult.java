package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class AcctSerialResult {

    private String ret_code;

    private String ret_msg;

    /**
     * ACCP系统分配给平台商户的唯一编号。
     */
    private String oid_partner;

    /**
     * 商户用户唯一编号。用户在商户系统中的唯一编号，要求该编号在商户系统能唯一标识用户。
     */
    private String user_id;

    /**
     * 入账总金额，表示当前查询条件下的入账总金额，单位：元。
     */
    private String total_in_amt;

    /**
     * 出账总金额，表示当前查询条件下的出账总金额，单位：元。
     */
    private String total_out_amt;

    /**
     * 当前页码，表示返回结果集第几页。
     */
    private Integer page_no;

    /**
     * 结果集总数，表示当前查询条件下的结果集数据总数。
     */
    private Integer total_num;

    /**
     * 结果集总页数，total_page=(total_num/page_size) 向上取整。
     */
    private Integer total_page;

    /**
     * 资金信息
     */
    private List<AcctBalList> acctbal_list;
}
