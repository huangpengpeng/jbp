package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaSnapshot;
import com.jbp.service.dao.agent.UserCapaDao;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserCapaSnapshotService;
import com.jbp.service.service.agent.UserInvitationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
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
		UserCapa userCapa = getByUser(uid);
		// 等级相同无须处理
		if (userCapa != null && NumberUtil.compare(capaId, userCapa.getCapaId()) == 0) {
			return userCapa;
		}
		// 新增等级
		String type = "";
		if (userCapa == null) {
			userCapa = UserCapa.builder().uid(uid).capaId(capaId).build();
			type = UserCapaSnapshot.Constants.升级.toString();
		} else {
			type = NumberUtil.compare(userCapa.getCapaId(), capaId) > 0 ? UserCapaSnapshot.Constants.降级.toString()
					: UserCapaSnapshot.Constants.升级.toString();
			userCapa.setCapaId(capaId);
		}
		saveOrUpdate(userCapa);
		// 记录快照
		UserCapaSnapshot snapshot = UserCapaSnapshot.builder().uid(uid).capaId(capaId).type(type).remark(remark)
				.description(description).build();
		snapshotService.save(snapshot);

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
}
