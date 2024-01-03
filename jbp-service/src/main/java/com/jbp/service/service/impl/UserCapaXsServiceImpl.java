package com.jbp.service.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.agent.UserCapaXsSnapshot;
import com.jbp.service.dao.agent.UserCapaXsDao;
import com.jbp.service.service.CapaXsService;
import com.jbp.service.service.UserCapaXsService;
import com.jbp.service.service.UserCapaXsSnapshotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

@Service
public class UserCapaXsServiceImpl extends ServiceImpl<UserCapaXsDao, UserCapaXs> implements UserCapaXsService {

    @Resource
    private CapaXsService capaXsService;
    @Resource
    private UserCapaXsSnapshotService snapshotService;
    @Resource
    private TransactionTemplate transactionTemplate;


    @Override
    public UserCapaXs getByUser(Integer uid) {
        UserCapaXs userCapaXs = getOne(new QueryWrapper<UserCapaXs>().lambda().eq(UserCapaXs::getUid, uid));
        if (userCapaXs != null) {
            CapaXs capaXs = capaXsService.getById(userCapaXs.getCapaId());
            userCapaXs.setCapaName(capaXs.getName());
            userCapaXs.setCapaUrl(capaXs.getIconUrl());
        }
        return userCapaXs;
    }

    @Override
    public UserCapaXs saveOrUpdateCapa(Integer uid, Long capaXsId, String remark, String description) {
        transactionTemplate.execute(s -> {
            if (capaXsId == null) {
                remove(new QueryWrapper<UserCapaXs>().lambda().eq(UserCapaXs::getUid, uid));
                // 记录快照
                UserCapaXsSnapshot snapshot = UserCapaXsSnapshot.builder().uid(uid).capaId(capaXsId).type("删除").remark(remark).description(description).build();
                snapshotService.save(snapshot);
                return Boolean.TRUE;
            }
            UserCapaXs userCapaXs = getByUser(uid);
            // 等级相同无须处理
            if (userCapaXs != null && NumberUtil.compare(capaXsId, userCapaXs.getCapaId()) == 0) {
                return Boolean.TRUE;
            }
            String type = "";
            // 新增等级
            if (userCapaXs == null) {
                userCapaXs = UserCapaXs.builder().uid(uid).capaId(capaXsId).build();
                type = UserCapaXsSnapshot.Constants.升级.toString();
            } else {
                type = NumberUtil.compare(userCapaXs.getCapaId(), capaXsId) > 0 ?
                        UserCapaXsSnapshot.Constants.降级.toString() : UserCapaXsSnapshot.Constants.升级.toString();
                userCapaXs.setCapaId(capaXsId);
            }
            saveOrUpdate(userCapaXs);
            // 记录快照
            UserCapaXsSnapshot snapshot = UserCapaXsSnapshot.builder().uid(uid).capaId(capaXsId).type(type).remark(remark).description(description).build();
            snapshotService.save(snapshot);
            return Boolean.TRUE;
        });
        return getByUser(uid);
    }

}
