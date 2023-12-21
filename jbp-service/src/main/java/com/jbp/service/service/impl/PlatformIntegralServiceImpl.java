package com.jbp.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.user.PlatformIntegral;
import com.jbp.common.model.user.PlatformIntegralRecord;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.PlatformIntegralDao;
import com.jbp.service.dao.PlatformIntegralRecordDao;
import com.jbp.service.service.PlatformIntegralRecordService;
import com.jbp.service.service.PlatformIntegralService;
import com.jbp.service.service.UserIntegralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.unit.DataUnit;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class PlatformIntegralServiceImpl extends ServiceImpl<PlatformIntegralDao, PlatformIntegral> implements PlatformIntegralService {
    private static final Logger logger = LoggerFactory.getLogger(PlatformIntegralServiceImpl.class);
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    PlatformIntegralRecordService platformIntegralRecordService;
    @Resource
    private UserIntegralService userIntegralService;

    @Override
    public PlatformIntegral get(String type) {
        return lambdaQuery()
                .eq(PlatformIntegral::getType,type)
                .one();
    }

    @Override
    public PlatformIntegral add(String type) {
        PlatformIntegral platformIntegral=PlatformIntegral.builder()
                .type(type)
                .integral(BigDecimal.ZERO)
                .createTime(DateTimeUtils.getNow())
                .updateTime(DateTimeUtils.getNow())
                .build();
        save(platformIntegral);
        return platformIntegral;
    }

    @Override
    public void increase(String integralType, String externalNo, String title, BigDecimal integral, String postscript) {
        if (ArithmeticUtils.gte(BigDecimal.ZERO, integral)) {
            throw new CrmebException(StrUtil.format("增加平台积分值不能小于0， type={}, integral={}",  integralType, integral));
        }
        PlatformIntegral platform = get(integralType);
        Boolean execute= transactionTemplate.execute(e->{
            PlatformIntegral platformIntegral=platform;
            if(platformIntegral==null){
                platformIntegral=add(integralType);
            }
            platformIntegral.setIntegral(platformIntegral.getIntegral().add(integral));
            platformIntegral.setUpdateTime(DateTimeUtils.getNow());
            updateById(platformIntegral);
            //添加明细
            platformIntegralRecordService.add(integralType,externalNo,1,title,integral,
                    platformIntegral.getIntegral(),postscript  );
            if (ArithmeticUtils.lessEquals(platformIntegral.getIntegral(), BigDecimal.ZERO)) {
                return Boolean.FALSE;
            }

         return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format(StrUtil.format("增加平台事务错误， type={}, integral={}",  integralType, integral)));
        }

    }

    @Override
    public void reduce(String integralType, String externalNo, String title, BigDecimal integral, String postscript) {
        if (ArithmeticUtils.gte(BigDecimal.ZERO, integral)) {
            throw new CrmebException(StrUtil.format("减少平台积分值不能小于0， type={}, integral={}", integralType, integral));
        }
        PlatformIntegral platformIntegral = get(integralType);
        if (platformIntegral == null) {
            platformIntegral = add(integralType);
        }
        if (ArithmeticUtils.less(platformIntegral.getIntegral(), integral)) {
            increase(integralType, externalNo, title, integral,  "减少平台积分时不足初始化增加");
        }
        Boolean execute= transactionTemplate.execute(e->{
            PlatformIntegral finalPlatformIntegral =  get(integralType);
            finalPlatformIntegral.setIntegral(finalPlatformIntegral.getIntegral().subtract(integral));
            finalPlatformIntegral.setUpdateTime(DateTimeUtils.getNow());
            updateById(finalPlatformIntegral);
            //添加明细
            platformIntegralRecordService.add(integralType,externalNo,2,title,integral,
                    finalPlatformIntegral.getIntegral(),postscript);
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format(StrUtil.format("减少平台事务错误， type={}, integral={}", integralType, integral)));
        }
    }

    @Override
    public void transferToUser(Integer uid, String integralType, String externalNo, String title, BigDecimal integral, String postscript) {
        Boolean execute=transactionTemplate.execute(e-> {
            reduce(integralType, externalNo, title, integral, postscript);
            userIntegralService.increase(uid, integralType, externalNo, title, integral, postscript);
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format(StrUtil.format("转账给用户错误， type={}, uid={}", integralType, uid)));
        }
    }
}
