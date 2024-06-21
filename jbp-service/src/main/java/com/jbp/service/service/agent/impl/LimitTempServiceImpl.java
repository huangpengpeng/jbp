package com.jbp.service.service.agent.impl;

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
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.WhiteService;
import com.jbp.service.service.agent.*;
import com.jbp.service.util.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LimitTempServiceImpl extends ServiceImpl<LimitTempDao, LimitTemp> implements LimitTempService {


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
    @Resource
    private OrderDetailService orderDetailService;


    @PostConstruct
    public void loadingTempCache() {
        LambdaQueryWrapper<LimitTemp> lqw = Wrappers.lambdaQuery();
        List<LimitTemp> list = list(lqw);
        list.forEach(temp -> redisUtil.hset(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST, temp.getId().toString(), temp));
    }


    private void async(LimitTemp temp) {
        redisUtil.hset(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST, temp.getId().toString(), temp);
    }

    private void asyncBlank(String key) {
        redisUtil.hmDelete(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST, key, null);
    }

    @Override
    public LimitTemp add(String name, String type, List<Long> capaIdList, List<Long> capaXsIdList,
                         List<Long> whiteIdList, List<Long> teamIdList, Boolean hasPartner,
                         List<Long> pCapaIdList, List<Long> pCapaXsIdList, Boolean hasRelation,
                         List<Long> rCapaIdList, List<Long> rCapaXsIdList, Boolean hasBuyLimit,
                         int buyLimitNum, Date buyLimitStartTime, Date buyLimitEndTime, Integer oneTimeNum, String description,Integer oneTimeNumMin) {
        LimitTemp temp = new LimitTemp(name, type, capaIdList, capaXsIdList, whiteIdList, teamIdList, hasPartner,
                pCapaIdList, pCapaXsIdList, hasRelation, rCapaIdList, rCapaXsIdList, hasBuyLimit, buyLimitNum, buyLimitStartTime, buyLimitEndTime, oneTimeNum,
                description,oneTimeNumMin);
        temp.init();
        save(temp);
        async(temp);
        return temp;
    }

    @Override
    public List<Long> hasLimits(Long capaId, Long capaXsId, List<Long> whiteIdList, List<Long> teamIdList, Integer pId, Integer rId) {
        Long pCapaId, pCapaXsId, rCapaId, rCapaXsId;
        List<Long> list = Lists.newArrayList();
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
        loadingTempCache();
        if (hmget == null || hmget.size() == 0) {
            loadingTempCache();
        }
        hmget = redisUtil.hmget(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST);
        if (hmget == null || hmget.size() == 0) {
            return list;
        }
        // 遍历返回用户满足的条件
        hmget.forEach((k, v) -> {
            Long tempId = Long.valueOf(k.toString());
            if (v != null) {
                LimitTemp temp = (LimitTemp) v;
                if (temp.check(capaId, capaXsId, whiteIdList, teamIdList, pId, pCapaId, pCapaXsId, rId, rCapaId, rCapaXsId)) {
                    list.add(tempId);
                }
            }
        });
        list.add(-1L);
        return list;
    }

    @Override
    public void validBuy(Integer uid, Long capaId, Long capaXsId, List<Long> whiteIdList,
                         List<Long> teamIdList, Integer pId, Integer rId, List<Product> productList, Map<Integer, Integer> productNumMap) {
        if (CollectionUtils.isEmpty(productList)) {
            return;
        }
        productList = productList.stream().filter(p -> p.getBuyLimitTempId() != null).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(productList)) {
            return;
        }
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
        for (Product product : productList) {
            Object o = redisUtil.hmget(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST).get(product.getBuyLimitTempId().toString());
            if (o != null) {
                LimitTemp temp = (LimitTemp) o;
                if (!temp.check(capaId, capaXsId, whiteIdList, teamIdList, pId, pCapaId, pCapaXsId, rId, rCapaId, rCapaXsId)) {
                    throw new CrmebException(product.getName() + ":无购买权限");
                }
                // 开启限购
                if (BooleanUtils.isTrue(temp.getHasBuyLimit()) && uid != null) {
                    // 查询指定时间购买成功的订单
                    Integer buySuccessCount = orderDetailService.getBuySuccessCount(uid, product.getId(), temp.getBuyLimitStartTime(), temp.getBuyLimitEndTime());
                    buySuccessCount = buySuccessCount + productNumMap.get(product.getId());
                    if (buySuccessCount > temp.getBuyLimitNum()) {
                        throw new CrmebException(product.getName() + ":已购买超出限购数量");
                    }
                }
                if (temp.getOneTimeNum() != null) {
                    if (productNumMap.get(product.getId()).intValue() > temp.getOneTimeNum().intValue()) {
                        throw new CrmebException(product.getName() + ":单次购买已超出限购数量:" + temp.getOneTimeNum());
                    }
                }

                if(temp.getOneTimeNumMin() != null){
                    if (productNumMap.get(product.getId()).intValue() < temp.getOneTimeNumMin().intValue()) {
                        throw new CrmebException(product.getName() + ":单次购买小于限购数量:" + temp.getOneTimeNumMin());
                    }
                }

            }
        }
    }

    @Override
    public PageInfo<LimitTemp> pageList(String name, String type, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<LimitTemp> limitTempLambdaQueryWrapper = new LambdaQueryWrapper<LimitTemp>()
                .like(StringUtils.isNotEmpty(name), LimitTemp::getName, name)
                .like(StringUtils.isNotEmpty(type), LimitTemp::getType, type)
                .orderByDesc(LimitTemp::getGmtModify);
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
    public void update(Long id, String name, String type, List<Long> capaIdList, List<Long> capaXsIdList, List<Long> whiteIdList,
                       List<Long> teamIdList, Boolean hasPartner, List<Long> pCapaIdList, List<Long> pCapaXsIdList,
                       Boolean hasRelation, List<Long> rCapaIdList, List<Long> rCapaXsIdList, Boolean hasBuyLimit,
                       int buyLimitNum, Date buyLimitStartTime, Date buyLimitEndTime, Integer oneTimeNum, String description,Integer oneTimeNumMin) {
        LimitTemp temp = new LimitTemp(name, type, capaIdList, capaXsIdList, whiteIdList, teamIdList, hasPartner,
                pCapaIdList, pCapaXsIdList, hasRelation, rCapaIdList, rCapaXsIdList, hasBuyLimit, buyLimitNum, buyLimitStartTime,
                buyLimitEndTime, oneTimeNum, description,oneTimeNumMin);
        temp.setId(id);
        temp.init();
        updateById(temp);
        async(temp);
    }

    @Override
    public LimitTempResponse details(Integer id) {
        LimitTemp limitTemp = getById(id);
        LimitTempResponse limitTempRequest = new LimitTempResponse();
        limitTempRequest.setName(limitTemp.getName());
        limitTempRequest.setId(limitTemp.getId());
        limitTempRequest.setType(limitTemp.getType());
        limitTempRequest.setHasBuyLimit(limitTemp.getHasBuyLimit());
        limitTempRequest.setBuyLimitNum(limitTemp.getBuyLimitNum());
        limitTempRequest.setBuyLimitStartTime(limitTemp.getBuyLimitStartTime());
        limitTempRequest.setBuyLimitEndTime(limitTemp.getBuyLimitEndTime());
        limitTempRequest.setOneTimeNum(limitTemp.getOneTimeNum());
        if (limitTemp.getCapaIdList().size() > 0) {
            limitTempRequest.setCapaIdList(capaService.listByIds(limitTemp.getCapaIdList()));
        }

        if (limitTemp.getCapaXsIdList().size() > 0) {
            limitTempRequest.setCapaXsIdList(capaXsService.listByIds(limitTemp.getCapaXsIdList()));

        }
        if (limitTemp.getWhiteIdList().size() > 0) {
            limitTempRequest.setWhiteIdList(whiteService.listByIds(limitTemp.getWhiteIdList()));

        }
        if (limitTemp.getTeamIdList().size() > 0) {
            limitTempRequest.setTeamIdList(teamService.listByIds(limitTemp.getTeamIdList()));

        }
        limitTempRequest.setHasPartner(limitTemp.getHasPartner());
        if (limitTemp.getPCapaIdList().size() > 0) {
            limitTempRequest.setPCapaIdList(capaService.listByIds(limitTemp.getPCapaIdList()));

        }
        if (limitTemp.getPCapaXsIdList().size() > 0) {
            limitTempRequest.setPCapaXsIdList(capaXsService.listByIds(limitTemp.getPCapaXsIdList()));

        }
        limitTempRequest.setHasRelation(limitTemp.getHasRelation());
        if (limitTemp.getRCapaIdList().size() > 0) {
            limitTempRequest.setRCapaIdList(capaService.listByIds(limitTemp.getRCapaIdList()));

        }
        if (limitTemp.getRCapaXsIdList().size() > 0) {
            limitTempRequest.setRCapaXsIdList(capaXsService.listByIds(limitTemp.getRCapaXsIdList()));

        }
        limitTempRequest.setDescription(limitTemp.getDescription());
        return limitTempRequest;
    }


}
