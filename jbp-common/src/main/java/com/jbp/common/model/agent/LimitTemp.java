package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.LongListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_limit_temp", autoResultMap = true)
@ApiModel(value="LimitTemp对象", description="限制模版")
@NoArgsConstructor
public class LimitTemp extends BaseModel {

    private static final long serialVersionUID = 1L;

    public LimitTemp(String name, String type, List<Long> capaIdList, List<Long> capaXsIdList,
                     List<Long> whiteIdList, List<Long> teamIdList, Boolean hasPartner,
                     List<Long> pCapaIdList, List<Long> pCapaXsIdList, Boolean hasRelation,
                     List<Long> rCapaIdList, List<Long> rCapaXsIdList, Boolean hasBuyLimit,
                     int buyLimitNum, Date buyLimitStartTime, Date buyLimitEndTime,
                     String description) {
        this.name = name;
        this.type = type;
        this.capaIdList = capaIdList;
        this.capaXsIdList = capaXsIdList;
        this.whiteIdList = whiteIdList;
        this.teamIdList = teamIdList;
        this.hasPartner = hasPartner;
        this.pCapaIdList = pCapaIdList;
        this.pCapaXsIdList = pCapaXsIdList;
        this.hasRelation = hasRelation;
        this.rCapaIdList = rCapaIdList;
        this.rCapaXsIdList = rCapaXsIdList;
        this.description=description;
        this.hasBuyLimit=hasBuyLimit;
        this.buyLimitNum=buyLimitNum;
        this.buyLimitStartTime=buyLimitStartTime;
        this.buyLimitEndTime=buyLimitEndTime;
    }

    public void init(){
        if (getHasPartner() == null){
            setHasPartner(false);
        }
        if (getHasRelation() == null){
            setHasRelation(false);
        }
    }

    @ApiModelProperty(value = "模版名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "商品显示  商品购买  装修显示")
    @TableField("type")
    private String type;

    @ApiModelProperty(value = "自己等级ID")
    @TableField(value = "capaIdList",typeHandler = LongListHandler.class)
    private List<Long> capaIdList;

    @ApiModelProperty(value = "自己星级ID")
    @TableField(value = "capaXsIdList",typeHandler = LongListHandler.class)
    private List<Long> capaXsIdList;

    @ApiModelProperty(value = "白名单ID")
    @TableField(value = "whiteIdList",typeHandler = LongListHandler.class)
    private List<Long> whiteIdList;

    @ApiModelProperty(value = "团队ID")
    @TableField(value = "teamIdList",typeHandler = LongListHandler.class)
    private List<Long> teamIdList;

    @ApiModelProperty(value = "要求必须有上级")
    @TableField("has_partner")
    private Boolean  hasPartner;

    @ApiModelProperty(value = "上级等级")
    @TableField(value = "pCapaIdList",typeHandler = LongListHandler.class)
    private List<Long> pCapaIdList;

    @ApiModelProperty(value = "上级星级")
    @TableField(value = "pCapaXsIdList",typeHandler = LongListHandler.class)
    private List<Long> pCapaXsIdList;

    @ApiModelProperty(value = "要求必须有服务上级")
    @TableField(value = "has_relation")
    private Boolean  hasRelation;

    @ApiModelProperty(value = "服务上级等级")
    @TableField(value = "rCapaIdList",typeHandler = LongListHandler.class)
    private List<Long> rCapaIdList;

    @ApiModelProperty(value = "服务上级星级")
    @TableField(value = "rCapaXsIdList",typeHandler = LongListHandler.class)
    private List<Long> rCapaXsIdList;

    @ApiModelProperty(value = "是否限购")
    private Boolean hasBuyLimit;

    @ApiModelProperty(value = "限购数量")
    private int buyLimitNum;

    @ApiModelProperty(value = "限购开始时间")
    private Date buyLimitStartTime;

    @ApiModelProperty(value = "限购结束时间")
    private Date buyLimitEndTime;

    @ApiModelProperty(value = "说明")
    @TableField(value = "description")
    private String  description;


    public Boolean check(Long capaId, Long capaXsId, List<Long> whiteIdList, List<Long> teamIdList,
                         Integer pId, Long pCapaId, Long pCapaXsId,  Integer rId,  Long rCapaId, Long rCapaXsId) {
        if (CollectionUtils.isNotEmpty(this.capaIdList) && !this.capaIdList.contains(capaId)) {
            return false;
        }
        if (CollectionUtils.isNotEmpty(this.capaXsIdList) && !this.capaXsIdList.contains(capaXsId)) {
            return false;
        }
        if (CollectionUtils.isNotEmpty(this.whiteIdList)) {
            if (CollectionUtils.isEmpty(whiteIdList)) {
                return false;
            }
            List<Long> AnB = this.whiteIdList.stream().filter(whiteIdList::contains).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(AnB)) {
                return false;
            }
        }
        if (CollectionUtils.isNotEmpty(this.teamIdList)) {
            if (CollectionUtils.isEmpty(whiteIdList)) {
                return false;
            }
            List<Long> AnB = this.teamIdList.stream().filter(teamIdList::contains).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(AnB)) {
                return false;
            }
        }

        if (BooleanUtils.isTrue(this.hasPartner)) {
            if (pId == null) {
                return false;
            }
            if (CollectionUtils.isNotEmpty(this.pCapaIdList)) {
                if (pCapaId == null) {
                    return false;
                }
                if (!this.pCapaIdList.contains(pCapaId)) {
                    return false;
                }
            }
            if (CollectionUtils.isNotEmpty(this.pCapaXsIdList)) {
                if (pCapaXsId == null) {
                    return false;
                }
                if (!this.pCapaXsIdList.contains(pCapaXsId)) {
                    return false;
                }
            }
        }

        if (BooleanUtils.isTrue(this.hasRelation)) {
            if (rId == null) {
                return false;
            }
            if (CollectionUtils.isNotEmpty(this.rCapaIdList)) {
                if (rCapaId == null) {
                    return false;
                }
                if (!this.rCapaIdList.contains(rCapaId)) {
                    return false;
                }
            }
            if (CollectionUtils.isNotEmpty(this.rCapaXsIdList)) {
                if (rCapaXsId == null) {
                    return false;
                }
                if (!this.rCapaXsIdList.contains(rCapaXsId)) {
                    return false;
                }
            }
        }
        return true;

    }
}
