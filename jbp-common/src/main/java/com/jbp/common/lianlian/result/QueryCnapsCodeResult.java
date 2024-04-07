package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class QueryCnapsCodeResult {

    private String ret_code;
    private String ret_msg;

    //  银行编码
    private String bank_code;

    private List<CardListInfo> card_list;
}
