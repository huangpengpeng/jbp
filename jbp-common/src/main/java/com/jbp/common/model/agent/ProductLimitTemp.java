package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.model.user.User;
import com.jbp.common.mybatis.LongListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_product_limit_temp", autoResultMap = true)
@ApiModel(value="ProductLimitTemp对象", description="商品购买限制模版")
public class ProductLimitTemp extends BaseModel {

    private static final long serialVersionUID = 1L;

    public ProductLimitTemp(String name, String type, List<Long> capaIdList, List<Long> capaXsIdList, List<Long> whiteIdList, List<Long> teamIdList, Boolean hasPartner, List<Long> pCapaIdList, List<Long> pCapaXsIdList, Boolean hasRelation, List<Long> rCapaIdList, List<Long> rCapaXsIdList) {
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
    private String name;

    @ApiModelProperty(value = "显示  购买")
    private String type;

    @ApiModelProperty(value = "自己等级ID")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> capaIdList;

    @ApiModelProperty(value = "自己星级ID")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> capaXsIdList;

    @ApiModelProperty(value = "白名单ID")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> whiteIdList;

    @ApiModelProperty(value = "团队ID")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> teamIdList;

    @ApiModelProperty(value = "要求必须有上级")
    private Boolean  hasPartner;

    @ApiModelProperty(value = "上级等级")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> pCapaIdList;

    @ApiModelProperty(value = "上级星级")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> pCapaXsIdList;

    @ApiModelProperty(value = "要求必须有服务上级")
    private Boolean  hasRelation;

    @ApiModelProperty(value = "服务上级等级")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> rCapaIdList;

    @ApiModelProperty(value = "服务上级星级")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> rCapaXsIdList;


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
