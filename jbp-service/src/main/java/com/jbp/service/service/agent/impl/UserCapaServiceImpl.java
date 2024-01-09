package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.PlatformWallet;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaSnapshot;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.UserCapaDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserCapaSnapshotService;
import com.jbp.service.service.agent.UserInvitationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserCapaServiceImpl extends ServiceImpl<UserCapaDao, UserCapa> implements UserCapaService {

    @Resource
    private CapaService capaService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private UserCapaSnapshotService snapshotService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private UserService userService;

    @Override
    public UserCapa getByUser(Integer uid) {
        UserCapa userCapa = getOne(new QueryWrapper<UserCapa>().lambda().eq(UserCapa::getUid, uid));
        if (userCapa != null) {
            Capa capa = capaService.getById(userCapa.getCapaId());
            userCapa.setCapaName(capa.getName());
            userCapa.setCapaUrl(capa.getIconUrl());
        }
        return userCapa;
    }

    @Override
    public UserCapa saveOrUpdateCapa(Integer uid, Long capaId, String remark, String description) {
        transactionTemplate.execute(s -> {
            UserCapa userCapa = getByUser(uid);
            // 等级相同无须处理
            if (userCapa != null && NumberUtil.compare(capaId, userCapa.getCapaId()) == 0) {
                return Boolean.TRUE;
            }
            // 新增等级
            String type = "";
            if (userCapa == null) {
                userCapa = UserCapa.builder().uid(uid).capaId(capaId).build();
                type = UserCapaSnapshot.Constants.升级.toString();
            } else {
                type = NumberUtil.compare(userCapa.getCapaId(), capaId) > 0 ?
                        UserCapaSnapshot.Constants.降级.toString() : UserCapaSnapshot.Constants.升级.toString();
                userCapa.setCapaId(capaId);
            }
            saveOrUpdate(userCapa);
            // 记录快照
            UserCapaSnapshot snapshot = UserCapaSnapshot.builder().uid(uid).capaId(capaId).type(type).remark(remark).description(description).build();
            snapshotService.save(snapshot);
            return Boolean.TRUE;

        });

        return getByUser(uid);
    }

    @Override
    public List<UserCapa> getUpperList(Integer uid, List<Long> capaIds, Integer num) {
        List<UserCapa> list = Lists.newArrayList();
        List<UserUpperDto> allUpper = userInvitationService.getAllUpper(uid);
        if (CollectionUtils.isEmpty(allUpper)) {
            return list;
        }
        for (UserUpperDto upper : allUpper) {
            if (list.size() == num) {
                return list;
            }
            if (upper.getPId() == null) {
                return list;
            }
            UserCapa userCapa = getByUser(upper.getPId());
            if (userCapa != null && capaIds.contains(userCapa.getCapaId())) {
                list.add(userCapa);
            }
        }
        return list;
    }

    @Override
    public PageInfo<UserCapa> pageList(Integer uid, Long capaId, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserCapa> userCapaLambdaQueryWrapper = new LambdaQueryWrapper<UserCapa>();
        userCapaLambdaQueryWrapper.eq(!ObjectUtil.isNull(uid), UserCapa::getUid, uid);
        userCapaLambdaQueryWrapper.eq(!ObjectUtil.isNull(capaId), UserCapa::getCapaId, capaId);
        Page<PlatformWallet> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserCapa> list = list(userCapaLambdaQueryWrapper);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
            Capa capa = capaService.getById(e.getCapaId());
            e.setCapaName(capa.getName());
            e.setCapaUrl(capa.getIconUrl());
        });
        return CommonPage.copyPageInfo(page, list);

    }
}
