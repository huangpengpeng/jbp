package com.jbp.common.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAccountResponse implements Serializable {

    private String account;

    private String capaName;
}
