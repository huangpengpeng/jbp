package com.jbp.common.lianlian.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CardListInfo {

    // 大额行号。
    private String cnaps_code;

    //  开户支行名称全称。
    private String brabank_name;
}
