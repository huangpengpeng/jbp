package com.jbp.common.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;


public class MyMetaObjectHandler implements MetaObjectHandler {
    private static final long serialVersionUID = 1L;

    @Override
    public void insertFill(MetaObject metaObject) {
        Date date = new Date();
        if(!StringUtils.isEmpty(metaObject.findProperty("gmtCreated", false))
            && Objects.isNull(this.getFieldValByName("gmtCreated", metaObject))){
            setFieldValByName("gmtCreated", date, metaObject);
        }
        if(!StringUtils.isEmpty(metaObject.findProperty("gmtModify", false))
                && Objects.isNull(this.getFieldValByName("gmtModify", metaObject))){
            setFieldValByName("gmtModify", date, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
