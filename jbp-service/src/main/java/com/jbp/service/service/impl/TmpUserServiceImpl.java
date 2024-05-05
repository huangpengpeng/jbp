package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.tank.TankStore;
import com.jbp.common.model.user.TmpUser;
import com.jbp.common.model.user.User;
import com.jbp.common.request.UserCapaTemplateRequest;
import com.jbp.service.dao.TankStoreDao;
import com.jbp.service.dao.TmpUserDao;
import com.jbp.service.service.TmpUserService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class TmpUserServiceImpl extends ServiceImpl<TmpUserDao, TmpUser> implements TmpUserService {

    @Resource
    private TmpUserDao dao;
    @Resource
    private Environment environment;
    @Resource
    private UserService userService;
    @Resource
    private UserInvitationService invitationService;


    @Override
    public void create() {
        String name = environment.getProperty("spring.application.name");
        if (StringUtils.isEmpty(name) || !name.contains("ph")) {
            return;
        }
        LambdaQueryWrapper<TmpUser> lqw = new LambdaQueryWrapper<>();
        lqw.isNull(TmpUser::getUid).last(" limit 1000");
        List<TmpUser> list = list(lqw);

        int i = 0, size = 0;
        // 执行注册
        if (!CollectionUtils.isEmpty(list)) {
            size = list.size();
            for (TmpUser tmpUser : list) {
                User user = userService.registerNoBandPater(tmpUser.getNickName(), tmpUser.getMobile(), "导入");
                tmpUser.setUid(user.getId());
                log.info("增在执行用户导入新增 总长度:{}, 当前长度:{}", size, i++);
            }
            dao.updateBatch(list);
            return;
        }
        // 执行绑定上级
        LambdaQueryWrapper<TmpUser> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(TmpUser::getIfBand, false).last(" limit 1000");
        list = list(lqw2);
        if (!CollectionUtils.isEmpty(list)) {
            size = list.size();
            i = 0;
            for (TmpUser tmpUser : list) {
                if (tmpUser.getOrgPid() != null) {
                    TmpUser pUser = getByOrgId(tmpUser.getOrgPid());
                    if (pUser != null) {
                        pUser = getByOrgId(503803);
                        Integer pId = pUser.getUid();
                        invitationService.band(tmpUser.getUid(), pId, false, true, true);
                    }
                }
                tmpUser.setIfBand(true);
                log.info("增在执行用户导入绑定 总长度:{}, 当前长度:{}", size, i++);
            }
            dao.updateBatch(list);
        }
    }

    @Override
    public void repairMobile() {
        LambdaQueryWrapper<TmpUser> lqw = new LambdaQueryWrapper<>();
        lqw.isNull(TmpUser::getMobile);
        List<TmpUser> list = list(lqw);
        for (TmpUser tmpUser : list) {
            initMobile(tmpUser);
        }
        dao.updateBatch(list);
    }

    @Override
    public TmpUser getByOrgId(Integer orgId) {
        return getOne(new QueryWrapper<TmpUser>().lambda().eq(TmpUser::getOrgId, orgId));
    }

    private void initMobile(TmpUser tmpUser) {
        Integer orgId = tmpUser.getOrgId();
        String phone = "00000000000";
        String substring = phone.substring(orgId.toString().length() - 1, phone.length() - 1);
        String mobile = orgId.toString() + substring;
        tmpUser.setMobile(mobile);
    }
}
