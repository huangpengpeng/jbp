package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.LimitTemp;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.product.Product;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.LimitTempResponse;
import com.jbp.common.utils.RedisUtil;
import com.jbp.service.dao.agent.LimitTempDao;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.WhiteService;
import com.jbp.service.service.agent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LimitTempServiceImpl extends ServiceImpl<LimitTempDao, LimitTemp> implements LimitTempService {

    @Autowired
    private LimitTempDao dao;
    @Autowired
    private UserCapaService userCapaService;
    @Autowired
    private UserCapaXsService userCapaXsService;
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private CapaService capaService;
    @Resource
    private CapaXsService capaXsService;
    @Resource
    private TeamService teamService;
    @Resource
    private WhiteService whiteService;


    @PostConstruct
    public void loadingTempCache() {
        if (redisUtil.exists(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST)) {
            Long hashSize = redisUtil.getHashSize(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST);
            if (hashSize > 0) {
                return;
            }
        }
        LambdaQueryWrapper<LimitTemp> lqw = Wrappers.lambdaQuery();
        List<LimitTemp> list = dao.selectList(lqw);
        list.forEach(temp -> redisUtil.hset(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST, temp.getId().toString(), temp));
    }


    private void async(LimitTemp temp) {
        redisUtil.hset(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST, temp.getId().toString(), temp);
    }

    private void asyncBlank(String key) {
        redisUtil.hmDelete(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST, key, null);
    }

    @Override
    public LimitTemp add(String name, String type, List<Long> capaIdList, List<Long> capaXsIdList, List<Long> whiteIdList, List<Long> teamIdList, Boolean hasPartner, List<Long> pCapaIdList, List<Long> pCapaXsIdList, Boolean hasRelation, List<Long> rCapaIdList, List<Long> rCapaXsIdList, String description) {
        LimitTemp temp = new LimitTemp(name, type, capaIdList, capaXsIdList, whiteIdList, teamIdList, hasPartner, pCapaIdList, pCapaXsIdList, hasRelation, rCapaIdList, rCapaXsIdList, description);
        temp.init();
        save(temp);
        async(temp);
        return temp;
    }


    @Override
    public void validBuy(Long capaId, Long capaXsId, List<Long> whiteIdList, List<Long> teamIdList, Integer pId, Integer rId, Product product) {
        if (product.getBuyLimitTempId() == null) {
            return;
        }
        final List<Long> limits = hasLimits(capaId, capaXsId, whiteIdList, teamIdList, pId, rId);
        if (limits != null && limits.contains(product.getBuyLimitTempId())) {
            return;
        }
        throw new CrmebException("无购买权限");
    }


    @Override
    public List<Long> hasLimits(Long capaId, Long capaXsId, List<Long> whiteIdList, List<Long> teamIdList, Integer pId, Integer rId) {
        Long pCapaId, pCapaXsId, rCapaId, rCapaXsId;
        if (pId != null) {
            UserCapa pUserCapa = userCapaService.getByUser(pId);
            pCapaId = pUserCapa == null ? null : pUserCapa.getCapaId();
            UserCapaXs pUserCapaXs = userCapaXsService.getByUser(pId);
            pCapaXsId = pUserCapaXs == null ? null : pUserCapaXs.getCapaId();
        } else {
            pCapaXsId = null;
            pCapaId = null;
        }
        if (rId != null) {
            UserCapa rUserCapa = userCapaService.getByUser(rId);
            rCapaId = rUserCapa == null ? null : rUserCapa.getCapaId();
            UserCapaXs rUserCapaXs = userCapaXsService.getByUser(rId);
            rCapaXsId = rUserCapaXs == null ? null : rUserCapaXs.getCapaId();
        } else {
            rCapaXsId = null;
            rCapaId = null;
        }
        Map<Object, Object> hmget = redisUtil.hmget(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST);
        if (hmget == null || hmget.size() == 0) {
            loadingTempCache();
        }
        hmget = redisUtil.hmget(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST);
        if (hmget == null || hmget.size() == 0) {
            return null;
        }
        // 遍历返回用户满足的条件
        List<Long> list = Lists.newArrayList();
        hmget.forEach((k, v) -> {
            Long tempId = (Long) k;
            if (v != null) {
                LimitTemp temp = (LimitTemp) v;
                if (temp.check(capaId, capaXsId, whiteIdList, teamIdList, pId, pCapaId, pCapaXsId, rId, rCapaId, rCapaXsId)) {
                    list.add(tempId);
                }
            }
        });
        return list;
    }

    @Override
    public PageInfo<LimitTemp> pageList(String name, String type, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<LimitTemp> limitTempLambdaQueryWrapper = new LambdaQueryWrapper<LimitTemp>()
                .like(!ObjectUtil.isNull(name) && !name.equals(""), LimitTemp::getName, name)
                .like(!ObjectUtil.isNull(type) && !type.equals(""), LimitTemp::getType, type);
        Page<LimitTemp> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list(limitTempLambdaQueryWrapper));
    }

    @Override
    public LimitTemp getByName(String name) {
        LambdaQueryWrapper<LimitTemp> limitTempLambdaQueryWrapper = new LambdaQueryWrapper<LimitTemp>()
                .eq(LimitTemp::getName, name);
        return getOne(limitTempLambdaQueryWrapper);
    }

    @Override
    public void update(Long id, String name, String type, List<Long> capaIdList, List<Long> capaXsIdList, List<Long> whiteIdList, List<Long> teamIdList, Boolean hasPartner, List<Long> pCapaIdList, List<Long> pCapaXsIdList, Boolean hasRelation, List<Long> rCapaIdList, List<Long> rCapaXsIdList, String description) {
        LimitTemp temp = new LimitTemp(name, type, capaIdList, capaXsIdList, whiteIdList, teamIdList, hasPartner, pCapaIdList, pCapaXsIdList, hasRelation, rCapaIdList, rCapaXsIdList, description);
        temp.setId(id);
        temp.init();
        updateById(temp);
        async(temp);
    }

    @Override
    public LimitTempResponse details(Integer id) {
        LimitTemp limitTemp = getById(id);
        LimitTempResponse limitTempRequest = new LimitTempResponse();
        limitTempRequest.setName(limitTempRequest.getName());
        limitTempRequest.setType(limitTempRequest.getType());
        limitTempRequest.setCapaIdList(capaService.listByIds(limitTemp.getCapaIdList()));
        limitTempRequest.setCapaXsIdList(capaXsService.listByIds(limitTemp.getCapaIdList()));
        limitTempRequest.setWhiteIdList(whiteService.listByIds(limitTemp.getWhiteIdList()));
        limitTempRequest.setTeamIdList(teamService.listByIds(limitTemp.getTeamIdList()));
        limitTempRequest.setHasPartner(limitTemp.getHasPartner());
        limitTempRequest.setPCapaIdList(capaService.listByIds(limitTemp.getPCapaIdList()));
        limitTempRequest.setPCapaXsIdList(capaXsService.listByIds(limitTemp.getPCapaXsIdList()));
        limitTempRequest.setHasRelation(limitTemp.getHasRelation());
        limitTempRequest.setRCapaIdList(capaService.listByIds(limitTemp.getRCapaIdList()));
        limitTempRequest.setRCapaXsIdList(capaXsService.listByIds(limitTemp.getRCapaXsIdList()));
        limitTempRequest.setDescription(limitTempRequest.getDescription());
        return limitTempRequest;

    }
}
