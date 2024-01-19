package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductLimitTemp;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.product.Product;
import com.jbp.common.utils.RedisUtil;
import com.jbp.service.dao.agent.ProductLimitTempDao;
import com.jbp.service.service.agent.ProductLimitTempService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserCapaXsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ProductLimitTempServiceImpl extends ServiceImpl<ProductLimitTempDao, ProductLimitTemp> implements ProductLimitTempService {

    @Autowired
    private ProductLimitTempDao dao;
    @Autowired
    private UserCapaService userCapaService;
    @Autowired
    private UserCapaXsService userCapaXsService;
    @Autowired
    private RedisUtil redisUtil;


    @PostConstruct
    public void loadingTempCache() {
        if (redisUtil.exists(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST)) {
            Long hashSize = redisUtil.getHashSize(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST);
            if (hashSize > 0) {
                return;
            }
        }
        LambdaQueryWrapper<ProductLimitTemp> lqw = Wrappers.lambdaQuery();
        List<ProductLimitTemp> list = dao.selectList(lqw);
        list.forEach(temp -> redisUtil.hset(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST, temp.getId().toString(), temp));
    }


    private void async(ProductLimitTemp temp) {
        redisUtil.hset(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST, temp.getId().toString(), temp);
    }

    private void asyncBlank(String key) {
        redisUtil.hmDelete(SysConfigConstants.PRODUCT_LIMIT_TEM_LIST, key, null);
    }

    @Override
    public ProductLimitTemp add(String name, String type, List<Long> capaIdList, List<Long> capaXsIdList, List<Long> whiteIdList, List<Long> teamIdList, Boolean hasPartner, List<Long> pCapaIdList, List<Long> pCapaXsIdList, Boolean hasRelation, List<Long> rCapaIdList, List<Long> rCapaXsIdList) {
        ProductLimitTemp temp = new ProductLimitTemp(name, type, capaIdList, capaXsIdList, whiteIdList, teamIdList, hasPartner, pCapaIdList, pCapaXsIdList, hasRelation, rCapaIdList, rCapaXsIdList);
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
                ProductLimitTemp temp = (ProductLimitTemp) v;
                if (temp.check(capaId, capaXsId, whiteIdList, teamIdList, pId, pCapaId, pCapaXsId, rId, rCapaId, rCapaXsId)) {
                    list.add(tempId);
                }
            }
        });
        return list;
    }
}
