package com.jbp.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.constants.IntegralConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.user.UserIntegral;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.UserIntegralDao;
import com.jbp.service.service.UserIntegralRecordService;
import com.jbp.service.service.UserIntegralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class UserIntegralServiceImpl extends ServiceImpl<UserIntegralDao, UserIntegral> implements UserIntegralService {

    private static final Logger logger = LoggerFactory.getLogger(UserIntegralServiceImpl.class);

    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private UserIntegralRecordService userIntegralRecordService;


    @Override
    public UserIntegral add(Integer uid, String type) {
        UserIntegral userIntegral = UserIntegral.builder()
                .uid(uid)
                .type(type)
                .integral(BigDecimal.ZERO)
                .status(IntegralConstants.INTEGRAL_STATUS_INIT)
                .createTime(DateTimeUtils.getNow())
                .updateTime(DateTimeUtils.getNow())
                .build();
        save(userIntegral);
        return userIntegral;
    }

    @Override
    public UserIntegral getByUser(Integer uid, String type) {
        return getOne(new QueryWrapper<UserIntegral>().lambda().eq(UserIntegral::getUid, uid).eq(UserIntegral::getType, type));
    }

    @Override
    public void increase(Integer uid, String integralType, String externalNo, String title, BigDecimal integral, String postscript) {
        if (ArithmeticUtils.gte(BigDecimal.ZERO, integral)) {
            throw new CrmebException(StrUtil.format("增加用户积分值不能小于0，uid = {}, type={}, integral={}", uid, integralType, integral));
        }
        UserIntegral user = getByUser(uid, integralType);
        Boolean execute = transactionTemplate.execute(e -> {
            UserIntegral userIntegral = user;
            if (user == null) {
                userIntegral = add(uid, integralType);
            }
            userIntegral.setIntegral(userIntegral.getIntegral().add(integral));
            userIntegral.setUpdateTime(DateTimeUtils.getNow());
            updateById(userIntegral);
            // 添加明细
            userIntegralRecordService.add(uid, integralType, externalNo, 1, title, integral,
                    userIntegral.getIntegral(), "", postscript);
            if (ArithmeticUtils.lessEquals(userIntegral.getIntegral(), BigDecimal.ZERO)) {
                return Boolean.FALSE;
            }

            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format(StrUtil.format("增加用户事务错误，uid = {}, type={}, integral={}", uid, integralType, integral)));
        }
    }

    @Override
    public void reduce(Integer uid, String integralType, String externalNo, String title, BigDecimal integral, String postscript) {
        if (ArithmeticUtils.gte(BigDecimal.ZERO, integral)) {
            throw new CrmebException(StrUtil.format("减少用户积分值不能小于0，uid = {}, type={}, integral={}", uid, integralType, integral));
        }
        UserIntegral userIntegral = getByUser(uid, integralType);
        if (userIntegral == null) {
            throw new CrmebException(StrUtil.format("减少用户积分账户不存在，uid = {}, type={}, integral={}", uid, integralType, integral));
        }
        if (ArithmeticUtils.less(userIntegral.getIntegral(), integral)) {
            throw new CrmebException(StrUtil.format("减少用户积分不足，uid = {}, type={}, integral={}", uid, integralType, integral));
        }
        Boolean execute = transactionTemplate.execute(e -> {
            userIntegral.setIntegral(userIntegral.getIntegral().subtract(integral));
            userIntegral.setUpdateTime(DateTimeUtils.getNow());
            updateById(userIntegral);
            // 添加明细
            userIntegralRecordService.add(uid, integralType, externalNo, 2, title, integral,
                    userIntegral.getIntegral(), "", postscript);
            if (ArithmeticUtils.less(userIntegral.getIntegral(), BigDecimal.ZERO)) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format(StrUtil.format("减少用户事务错误，uid = {}, type={}, integral={}", uid, integralType, integral)));
        }
    }

    @Override
    public void transferToPlatform(Integer uid, String integralType, String externalNo, String title, BigDecimal integral, String postscript) {

    }
}
