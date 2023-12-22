package com.jbp.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpperModel implements Serializable {

    /**
     * 自己
     */
    private Long uId;

    private String accountNo;

    /**
     * 上级
     */
    private Long pId;

    private String pAccountNo;

    /**
     * 节点
     */
    private int  node;

    /**
     * 自己是1  跟自己差一层+1
     */
    private Integer level;
}
