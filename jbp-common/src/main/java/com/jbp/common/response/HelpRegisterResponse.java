package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="HelpRegisterResponse对象", description="帮注册响应对象")
public class HelpRegisterResponse implements Serializable {

    public HelpRegisterResponse(Integer pId, Integer rId, Integer node) {
        this.pId = pId;
        this.rId = rId;
        this.node = node;
    }

    private Integer pId;

    private Integer rId;

    private Integer node;

}
