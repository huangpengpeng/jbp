package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.excel.UserCapaXsSnapshotExcel;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.model.agent.UserCapaXsSnapshot;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.FileResultVo;
import com.jbp.service.dao.agent.UserCapaXsSnapshotDao;
import com.jbp.service.service.UploadService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaXsService;
import com.jbp.service.service.agent.UserCapaXsSnapshotService;
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
public class UserCapaXsSnapshotServiceImpl extends ServiceImpl<UserCapaXsSnapshotDao, UserCapaXsSnapshot> implements UserCapaXsSnapshotService {

    @Resource
    private UserService userService;
    @Resource
    private CapaXsService capaXsService;
    @Resource
    private UploadService uploadService;

    @Override
    public List<UserCapaXsSnapshot> getByDescription(String description) {
        return list(new QueryWrapper<UserCapaXsSnapshot>().lambda().like(UserCapaXsSnapshot::getDescription, description));
    }

    @Override
    public PageInfo<UserCapaXsSnapshot> pageList(Integer uid, Long capaId, String type, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserCapaXsSnapshot> userCapaXsSnapshotLambdaQueryWrapper = new LambdaQueryWrapper<UserCapaXsSnapshot>()
                .eq(!ObjectUtil.isNull(uid), UserCapaXsSnapshot::getUid, uid)
                .eq(!ObjectUtil.isNull(capaId), UserCapaXsSnapshot::getCapaId, capaId)
                .eq(!ObjectUtil.isNull(type) && !type.equals(""), UserCapaXsSnapshot::getType, type)
                .orderByDesc(UserCapaXsSnapshot::getId);
        Page<UserCapaXsSnapshot> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserCapaXsSnapshot> list = list(userCapaXsSnapshotLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(UserCapaXsSnapshot::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        Map<Long, CapaXs> capaXsMap = capaXsService.getCapaXsMap();
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
            CapaXs capaXs = capaXsMap.get(e.getCapaId());
            e.setCapaName(capaXs != null ? capaXs.getName() : "");
            e.setCapaUrl(capaXs != null ? capaXs.getIconUrl() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public String export(Integer uid, Long capaId, String type) {
        LambdaQueryWrapper<UserCapaXsSnapshot> userCapaXsSnapshotLambdaQueryWrapper = new LambdaQueryWrapper<UserCapaXsSnapshot>()
                .eq(!ObjectUtil.isNull(uid), UserCapaXsSnapshot::getUid, uid)
                .eq(!ObjectUtil.isNull(capaId), UserCapaXsSnapshot::getCapaId, capaId)
                .eq(!ObjectUtil.isNull(type) && !type.equals(""), UserCapaXsSnapshot::getType, type)
                .orderByDesc(UserCapaXsSnapshot::getId);
        List<UserCapaXsSnapshot> list = list(userCapaXsSnapshotLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            throw new CrmebException("未查询到用户星级快照数据！");
        }
        List<Integer> uIdList = list.stream().map(UserCapaXsSnapshot::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        Map<Long, CapaXs> capaXsMap = capaXsService.getCapaXsMap();

        LinkedList<UserCapaXsSnapshotExcel> result = new LinkedList<>();
        list.forEach(e -> {
            UserCapaXsSnapshotExcel vo = new UserCapaXsSnapshotExcel();
            vo.setId(e.getId());
            vo.setDescription(e.getDescription());
            vo.setRemark(e.getRemark());
            vo.setType(e.getType());
            vo.setCreatedTime(e.getGmtCreated());
            User user = uidMapList.get(e.getUid());
            vo.setAccount(user != null ? user.getAccount() : "");
            CapaXs capaXs = capaXsMap.get(e.getCapaId());
            vo.setCapaName(capaXs != null ? capaXs.getName() : "");
            result.add(vo);
        });
        FileResultVo fileResultVo = uploadService.excelLocalUpload(result, UserCapaXsSnapshotExcel.class);
        log.info("用户星级快照列表导出下载地址:" + fileResultVo.getUrl());
        return fileResultVo.getUrl();
    }
}
