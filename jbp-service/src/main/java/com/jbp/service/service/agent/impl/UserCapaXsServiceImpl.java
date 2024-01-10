package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.agent.UserCapaXsSnapshot;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.UserCapaXsDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaXsService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserCapaXsSnapshotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserCapaXsServiceImpl extends ServiceImpl<UserCapaXsDao, UserCapaXs> implements UserCapaXsService {

    @Resource
    private CapaXsService capaXsService;
    @Resource
    private UserCapaXsSnapshotService snapshotService;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private UserService userService;

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
		if (capaXsId == null) {
			remove(new QueryWrapper<UserCapaXs>().lambda().eq(UserCapaXs::getUid, uid));
			// 记录快照
			UserCapaXsSnapshot snapshot = UserCapaXsSnapshot.builder().uid(uid).capaId(capaXsId).type("删除")
					.remark(remark).description(description).build();
			snapshotService.save(snapshot);
			return getByUser(uid);
		}
		UserCapaXs userCapaXs = getByUser(uid);
		// 等级相同无须处理
		if (userCapaXs != null && NumberUtil.compare(capaXsId, userCapaXs.getCapaId()) == 0) {
			return getByUser(uid);
		}
		String type = "";
		// 新增等级
		if (userCapaXs == null) {
			userCapaXs = UserCapaXs.builder().uid(uid).capaId(capaXsId).build();
			type = UserCapaXsSnapshot.Constants.升级.toString();
		} else {
			type = NumberUtil.compare(userCapaXs.getCapaId(), capaXsId) > 0 ? UserCapaXsSnapshot.Constants.降级.toString()
					: UserCapaXsSnapshot.Constants.升级.toString();
			userCapaXs.setCapaId(capaXsId);
		}
		saveOrUpdate(userCapaXs);
		// 记录快照
		UserCapaXsSnapshot snapshot = UserCapaXsSnapshot.builder().uid(uid).capaId(capaXsId).type(type).remark(remark)
				.description(description).build();
		snapshotService.save(snapshot);
		return getByUser(uid);
	}

    @Override
    public PageInfo<UserCapaXs> pageList(Integer uid, Long capaId, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserCapaXs> userCapaXsLambdaQueryWrapper = new LambdaQueryWrapper<UserCapaXs>();
        userCapaXsLambdaQueryWrapper.eq(!ObjectUtil.isNull(uid), UserCapaXs::getUid, uid);
        userCapaXsLambdaQueryWrapper.eq(!ObjectUtil.isNull(capaId), UserCapaXs::getCapaId, capaId);
        Page<WalletConfig> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserCapaXs> list = list(userCapaXsLambdaQueryWrapper);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
            CapaXs capaXs = capaXsService.getById(e.getCapaId());
            e.setCapaName(capaXs.getName());
            e.setCapaUrl(capaXs.getIconUrl());
        });
        return CommonPage.copyPageInfo(page, list);

    }

}
