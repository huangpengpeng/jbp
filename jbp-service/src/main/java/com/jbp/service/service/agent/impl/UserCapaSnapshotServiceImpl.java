package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.excel.UserCapaSnapshotExcel;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.UserCapaSnapshot;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.FileResultVo;
import com.jbp.service.dao.agent.UserCapaSnapshotDao;
import com.jbp.service.service.UploadService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.UserCapaSnapshotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
@Slf4j
public class UserCapaSnapshotServiceImpl extends ServiceImpl<UserCapaSnapshotDao, UserCapaSnapshot> implements UserCapaSnapshotService {
    @Resource
    UserService userService;
    @Resource
    CapaService capaService;
    @Resource
    private UploadService uploadService;


    @Override
    public List<UserCapaSnapshot> getByDescription(String description) {
        return list(new QueryWrapper<UserCapaSnapshot>().lambda().like(UserCapaSnapshot::getDescription, description));
    }

    @Override
    public PageInfo<UserCapaSnapshot> pageList(Integer uid, Long capaId, String type, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserCapaSnapshot> userCapaSnapshotLambdaQueryWrapper = new LambdaQueryWrapper<UserCapaSnapshot>()
                .eq(!ObjectUtil.isNull(uid), UserCapaSnapshot::getUid, uid)
                .eq(!ObjectUtil.isNull(capaId), UserCapaSnapshot::getCapaId, capaId)
                .eq(!ObjectUtil.isNull(type) && !type.equals(""), UserCapaSnapshot::getType, type)
                .orderByDesc(UserCapaSnapshot::getId);
        Page<UserCapaSnapshot> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserCapaSnapshot> list = list(userCapaSnapshotLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(UserCapaSnapshot::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        Map<Long, Capa> capaMap = capaService.getCapaMap();
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
            Capa capa = capaMap.get(e.getCapaId());
            e.setCapaName(capa.getName());
            e.setCapaUrl(capa.getIconUrl());
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public UserCapaSnapshot getByFirst(Integer uid, Long capaId) {
        return getOne(new QueryWrapper<UserCapaSnapshot>().lambda().eq(UserCapaSnapshot::getCapaId, capaId).orderByAsc(UserCapaSnapshot::getId).last(" limit 1"));
    }

    @Override
    public String export(Integer uid, Long capaId, String type) {
        LambdaQueryWrapper<UserCapaSnapshot> userCapaSnapshotLambdaQueryWrapper = new LambdaQueryWrapper<UserCapaSnapshot>()
                .eq(!ObjectUtil.isNull(uid), UserCapaSnapshot::getUid, uid)
                .eq(!ObjectUtil.isNull(capaId), UserCapaSnapshot::getCapaId, capaId)
                .eq(!ObjectUtil.isNull(type) && !type.equals(""), UserCapaSnapshot::getType, type)
                .orderByDesc(UserCapaSnapshot::getId);
        List<UserCapaSnapshot> list = list(userCapaSnapshotLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            throw new CrmebException("未查询到用户等级快照数据！");
        }
        List<Integer> uIdList = list.stream().map(UserCapaSnapshot::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        Map<Long, Capa> capaMap = capaService.getCapaMap();

        LinkedList<UserCapaSnapshotExcel> result = new LinkedList<>();
        list.forEach(e -> {
            UserCapaSnapshotExcel vo = new UserCapaSnapshotExcel();
            vo.setId(e.getId());
            vo.setDescription(e.getDescription());
            vo.setRemark(e.getRemark());
            vo.setType(e.getType());
            vo.setCreatedTime(e.getGmtCreated());
            User user = uidMapList.get(e.getUid());
            vo.setAccount(user != null ? user.getAccount() : "");
            Capa capa = capaMap.get(e.getCapaId());
            vo.setCapaName(capa != null ? capa.getName() : "");
            result.add(vo);
        });
        FileResultVo fileResultVo = uploadService.excelLocalUpload(result, UserCapaSnapshotExcel.class);
        log.info("用户等级快照列表导出下载地址:" + fileResultVo.getUrl());
        return fileResultVo.getUrl();
    }
}
