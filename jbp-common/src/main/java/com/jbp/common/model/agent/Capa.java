package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Maps;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.RiseConditionListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 市场等级
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_capa", autoResultMap = true)
@ApiModel(value="Capa对象", description="平台等级")
public class Capa extends BaseModel {

    private static final long serialVersionUID = -3068573610140753926L;

    public Capa(String name, Long pCapaId, int rankNum, String iconUrl, String riseImgUrl, String shareImgUrl) {
        this.name = name;
        this.pCapaId = pCapaId;
        this.rankNum = rankNum;
        this.iconUrl = iconUrl;
        this.riseImgUrl = riseImgUrl;
        this.shareImgUrl = shareImgUrl;
    }

    @ApiModelProperty("等级名字")
    @TableField("name")
    private String name;

    @ApiModelProperty("下一个级别")
    @TableField(value = "p_capa_id", updateStrategy = FieldStrategy.IGNORED)
    private Long pCapaId;

    @ApiModelProperty("数字等级")
    @TableField("rank_num")
    private int rankNum;

    @ApiModelProperty("等级图标地址")
    @TableField("icon_url")
    private String iconUrl;

    @ApiModelProperty("升级提醒图片")
    @TableField("rise_img_url")
    private String riseImgUrl;

    @ApiModelProperty("邀请图片")
    @TableField("share_img_url")
    private String shareImgUrl;

    @ApiModelProperty("计算表达式")
    @TableField(value = "parser", updateStrategy = FieldStrategy.IGNORED)
    private String parser;

    @ApiModelProperty("升级条件")
    @TableField(typeHandler = RiseConditionListHandler.class, updateStrategy = FieldStrategy.IGNORED)
    private List<RiseCondition> conditionList;


    public Map<String, Boolean> initParser() {
        Map<String, Boolean> map = Maps.newConcurrentMap();
        List<String> conditionNames = this.getConditionNames();
        if (conditionNames != null) {
            for (String conditionName : conditionNames) {
                map.put(conditionName, false);
            }
        }
        return map;
    }

    public List<String> getConditionNames() {
        if (StringUtils.isNotEmpty(this.parser)) {
            String parserStr = this.parser;
            String replace = parserStr.replace("(", "");
            replace = replace.replace(")", "");
            replace = replace.replace("&&", " ");
            replace = replace.replace("||", " ");
            String[] split = replace.split(" ");
            return Arrays.stream(split).collect(Collectors.toList());
        }
        return null;
    }

    public Boolean parser(Map<String, Boolean> map) {
        String parserStr = this.parser;
        if (StringUtils.isNotEmpty(this.parser)) {
            ExpressionParser parser = new SpelExpressionParser();
            List<String> conditionNames = getConditionNames();
            for (String conditionName : conditionNames) {
                parserStr = parserStr.replace(conditionName, map.get(conditionName).toString());
            }
            return (Boolean) parser.parseExpression(parserStr).getValue();
        }
        return false;
    }


}
