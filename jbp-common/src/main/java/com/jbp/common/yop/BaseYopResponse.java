package com.jbp.common.yop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public abstract class BaseYopResponse implements Serializable {
    private String errorMessage;
    public abstract boolean validate();
}
