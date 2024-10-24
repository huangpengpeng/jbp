package com.jbp.common.yop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public abstract class Response implements Serializable {
    private String errorMessage;
    public abstract boolean validate();
}
