package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.jbp.common.dto.UserWhiteDto;
import com.jbp.common.excel.WhiteUserExcel;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.White;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteUserRequest;
import com.jbp.common.vo.FileResultVo;
import com.jbp.service.dao.WhiteUserDao;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserCapaXsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WhiteUserServiceImpl extends ServiceImpl<WhiteUserDao, WhiteUser> implements WhiteUserService {
    @Resource
    private WhiteUserDao whiteUserDao;
    @Resource
    private UserService userService;
    @Resource
    private WhiteService whiteService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private UserCapaService userCapaService;
    @Autowired
    private UserCapaXsService userCapaXsService;
    @Autowired
    private TeamUserService teamUserService;

    @Override
    public PageInfo<WhiteUser> pageList(Integer uid, Long whiteId, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<WhiteUser> lambdaQueryWrapper = new LambdaQueryWrapper<WhiteUser>()
                .eq(!Objects.isNull(uid), WhiteUser::getUid, uid)
                .eq(!Objects.isNull(whiteId), WhiteUser::getWhiteId, whiteId)
                .orderByDesc(WhiteUser::getId);
        Page<WhiteUser> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<WhiteUser> whites = whiteUserDao.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(whites)) {
            return CommonPage.copyPageInfo(page, whites);
        }
        List<Integer> uIdList = list().stream().map(WhiteUser::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        //等级
        Map<Integer, UserCapa> capaMapList = userCapaService.getUidMap(uIdList);
        Map<Integer, UserCapaXs> capaXsMapList = userCapaXsService.getUidMap(uIdList);
        //团队
        Map<Integer, TeamUser> teamUserMapList = teamUserService.getUidMapList(uIdList);
        whites.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
            White white = whiteService.getById(e.getWhiteId());
            e.setWhiteName(white != null ? white.getName() : "");
            //等级
            UserCapa uUserCapa = capaMapList.get(e.getUid());
            e.setCapaName(uUserCapa != null ? uUserCapa.getCapaName() : "");
            UserCapaXs uUserCapaXs = capaXsMapList.get(e.getUid());
            e.setCapaXsName(uUserCapaXs!=null?uUserCapaXs.getCapaName():"");
            //团队
            TeamUser teamUser = teamUserMapList.get(e.getUid());
            e.setTeamName(teamUser != null ? teamUser.getName() : "");
        });
        return CommonPage.copyPageInfo(page, whites);
    }

    @Override
    public void add(Integer uid, Long whiteId, String ordersSn) {
        LambdaQueryWrapper<WhiteUser> lqw = new LambdaQueryWrapper<WhiteUser>()
                .eq(WhiteUser::getOrdersSn, ordersSn);
        if (list(lqw).size() > 0) {
            throw new RuntimeException(String.format("单号已存在"));
        }
        if (getByUser(uid, whiteId) != null) {
            throw new RuntimeException(String.format("用户白名单已存在"));
        }
        WhiteUser userWhite = new WhiteUser();
        userWhite.setWhiteId(whiteId);
        userWhite.setUid(uid);
        userWhite.setOrdersSn(ordersSn);
        save(userWhite);
    }

    @Override
    public String export(WhiteUserRequest request) {
        Integer uid = null;
        if (com.jbp.service.util.StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        LambdaQueryWrapper<WhiteUser> lqw = new LambdaQueryWrapper<WhiteUser>()
                .eq(!Objects.isNull(uid), WhiteUser::getUid, uid)
                .eq(!Objects.isNull(request.getWhiteId()), WhiteUser::getWhiteId, request.getWhiteId());
        List<WhiteUser> whites = whiteUserDao.selectList(lqw);
        if (CollectionUtils.isEmpty(whites)) {
            throw new CrmebException("未查询到数据");
        }
        List<Integer> uIdList = list().stream().map(WhiteUser::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);

        List<WhiteUserExcel> result = new LinkedList<>();
        for (WhiteUser whiteUser : whites) {
            WhiteUserExcel vo = new WhiteUserExcel();
            vo.setWhiteId(whiteUser.getWhiteId());
            vo.setOrdersSn(whiteUser.getOrdersSn());
            vo.setCreateTime(whiteUser.getGmtCreated());
            vo.setUid(whiteUser.getUid());
            User user = uidMapList.get(whiteUser.getUid());
            vo.setAccount(user != null ? user.getAccount() : "");
            White white = whiteService.getById(whiteUser.getWhiteId());
            vo.setWhiteName(white != null ? white.getName() : "");
            result.add(vo);
        }
        FileResultVo fileResultVo = uploadService.excelLocalUpload(result, WhiteUserExcel.class);
        log.info("白名单列表导出下载地址:" + fileResultVo.getUrl());
        return fileResultVo.getUrl();
    }

    @Override
    public Boolean batchSave(List<UserWhiteDto> userWhiteList) {
        if (CollectionUtils.isEmpty(userWhiteList)) {
            throw new RuntimeException("导入表格不能为空");
        }
        Map<String, User> userMap = Maps.newConcurrentMap();
        Map<String, White> whiteMap = Maps.newConcurrentMap();

        for (int i = 0; i < userWhiteList.size(); i++) {
            UserWhiteDto userWhiteExpress = userWhiteList.get(i);
            if (StringUtils.isAnyBlank(userWhiteExpress.getAccount(), userWhiteExpress.getWhiteName())) {
                throw new RuntimeException(String.format("第: %s, 信息不完整", i + 1));
            }
            String account = StringUtils.trim(userWhiteExpress.getAccount());
            if (userMap.get(account) == null) {
                User user = userService.getByAccount(account);
                if (user == null) {
                    throw new RuntimeException(String.format("第: %s, 账户不存在", i + 1));
                }
                userMap.put(account, user);
            }
            String whiteName = StringUtils.trim(userWhiteExpress.getWhiteName());
            if (whiteMap.get(whiteName) == null) {
                White white = whiteService.getByName(whiteName);
                if (white == null) {
                    throw new RuntimeException(String.format("第: %s, 白名单不存在", i + 1));
                }
                whiteMap.put(whiteName, white);
            }
            String ordersSn = userWhiteExpress.getOrdersSn();
            if (whiteMap.get(ordersSn) == null) {
                LambdaQueryWrapper<WhiteUser> lqw = new LambdaQueryWrapper<WhiteUser>()
                        .eq(WhiteUser::getOrdersSn, ordersSn);
                if (list(lqw).size() > 0) {
                    throw new RuntimeException(String.format("第: %s, 单号已存在", i + 1));
                }
            }
        }

        transactionTemplate.execute(s -> {
            List<WhiteUser> batchSaveList = new ArrayList<>();
            for (int i = 0; i < userWhiteList.size(); i++) {
                UserWhiteDto userWhiteExpress = userWhiteList.get(i);
                String account = StringUtils.trim(userWhiteExpress.getAccount());
                String whiteName = StringUtils.trim(userWhiteExpress.getWhiteName());
                White white = whiteMap.get(whiteName);
                User user = userMap.get(account);
                if (getByUser(user.getId(), white.getId()) == null) {
                    WhiteUser userWhite = WhiteUser.builder().uid(user.getId()).whiteId(white.getId()).ordersSn(userWhiteExpress.getOrdersSn()).build();
                    batchSaveList.add(userWhite);
                }
            }
            /*批量添加*/
            saveBatch(batchSaveList);
            return Boolean.TRUE;
        });
        return true;
    }

    @Override
    public void deleteByWhite(Long id) {
        remove(new LambdaQueryWrapper<WhiteUser>().eq(WhiteUser::getWhiteId, id));
    }

    @Override
    public WhiteUser getByUser(Integer uid, Long whiteId) {
        return getOne(new LambdaQueryWrapper<WhiteUser>().eq(WhiteUser::getUid, uid).eq(WhiteUser::getWhiteId, whiteId));
    }

    @Override
    public List<Long> getByUser(Integer uid) {
        List<WhiteUser> list = list(new LambdaQueryWrapper<WhiteUser>().eq(WhiteUser::getUid, uid));
        return ListUtils.emptyIfNull(list).stream().map(WhiteUser::getWhiteId).collect(Collectors.toList());
    }

    @Override
    public List<WhiteUser> getByUserList(Integer uid) {
        List<WhiteUser> list = list(new LambdaQueryWrapper<WhiteUser>().eq(WhiteUser::getUid, uid));
        return list;
    }
}
