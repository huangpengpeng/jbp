package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_message")
@ApiModel(value = "Message对象", description = "消息对象")
public class UserMessage extends BaseModel {

    @ApiModelProperty("用户id")
    private Integer uid;

    @ApiModelProperty("消息id")
    private Long messageId;

}
