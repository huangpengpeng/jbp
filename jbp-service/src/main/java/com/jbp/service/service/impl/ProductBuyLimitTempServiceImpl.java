package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.OrderRegister;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductBuyLimitTemp;
import com.jbp.common.model.user.User;
import com.jbp.service.dao.ProductBuyLimitTempDao;
import com.jbp.service.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductBuyLimitTempServiceImpl extends ServiceImpl<ProductBuyLimitTempDao, ProductBuyLimitTemp> implements ProductBuyLimitTempService {

    @Autowired
    private UserService userService;
    @Autowired
    private CapaService capaService;
    @Autowired
    private CapaXsService capaXsService;
    @Autowired
    private UserCapaService userCapaService;
    @Autowired
    private UserCapaXsService userCapaXsService;
    @Autowired
    private TeamUserService teamUserService;
    @Autowired
    private WhiteUserService whiteUserService;
    @Autowired
    private UserInvitationService userInvitationService;
    @Autowired
    private UserRelationService userRelationService;

    @Override
    public ProductBuyLimitTemp add(String name, List<Long> capaIdList, List<Long> capaXsIdList, List<Long> whiteIdList,
                                   List<Long> teamIdList, Boolean hasPartner, List<Long> pCapaIdList, Boolean hasRelation, List<Long> pCapaXsIdList) {
        ProductBuyLimitTemp temp = new ProductBuyLimitTemp( name, capaIdList,  capaXsIdList, whiteIdList,  teamIdList,  hasPartner,  pCapaIdList,  hasRelation,  pCapaXsIdList);
        temp.init();
        save(temp);
        return temp;
    }

    /**
     * 验证是否可以下单
     * @param uId 下单用户
     * @param product 下单产品
     * @param orderRegister 注册用户信息
     */
    @Override
    public void valid(Integer uId, Product product, OrderRegister orderRegister) {
        if (product.getLimitTempId() == null) {
            return;
        }

        ProductBuyLimitTemp temp = getById(product.getLimitTempId());

        if (CollectionUtils.isNotEmpty(temp.getCapaIdList())) {
            Long capaId = null;
            if (uId != null) {
                UserCapa userCapa = userCapaService.getByUser(uId);
                capaId = userCapa != null ? userCapa.getCapaId() : null;
            }
            if (orderRegister != null) {
                capaId = orderRegister.getCapaId();
            }
            if (!temp.getCapaIdList().contains(capaId)) {
                throw new CrmebException("购买商品:" + product.getName() + ", 等级权限不足");
            }
        }

        if (CollectionUtils.isNotEmpty(temp.getCapaXsIdList())) {
            Long capaXsId = null;
            if (uId != null) {
                UserCapaXs userCapaXs = userCapaXsService.getByUser(uId);
                capaXsId = userCapaXs != null ? userCapaXs.getCapaId() : null;
            }
            if (orderRegister != null) {
                capaXsId = orderRegister.getCapaXsId();
            }
            if (!temp.getCapaXsIdList().contains(capaXsId)) {
                throw new CrmebException("购买商品:" + product.getName() + ", 星级权限不足");
            }
        }

        if (CollectionUtils.isNotEmpty(temp.getWhiteIdList())) {
            List<Long> whiteIdList = Lists.newArrayList();
            if (uId != null) {
                whiteIdList = whiteUserService.getByUser(uId);
            }
            if (CollectionUtils.isEmpty(whiteIdList)) {
                throw new CrmebException("购买商品:" + product.getName() + ", 白名单权限不足");
            }
            List<Long> AnB = temp.getWhiteIdList().stream().filter(whiteIdList::contains).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(AnB)) {
                throw new CrmebException("购买商品:" + product.getName() + ", 白名单权限不足");
            }
        }


        if (CollectionUtils.isNotEmpty(temp.getTeamIdList())) {
            Integer teamId = null;
            if (uId == null) {
                TeamUser teamUser = teamUserService.getByUser(uId);
                teamId = teamUser != null ? teamUser.getId() : null;
            }
            if (orderRegister != null && StringUtils.isNotEmpty(orderRegister.getPAccount())) {
                User user = userService.getByAccount(orderRegister.getPAccount());
                TeamUser teamUser = teamUserService.getByUser(user.getId());
                teamId = teamUser != null ? teamUser.getId() : null;
            }
            if (!temp.getTeamIdList().contains(teamId)) {
                throw new CrmebException("购买商品:" + product.getName() + ", 团队权限不足");
            }
        }

        if (BooleanUtils.isTrue(temp.getHasPartner())) {
            Integer pId = null;
            if (uId != null) {
                pId = userInvitationService.getPid(uId);
            }
            if (orderRegister != null && StringUtils.isNotEmpty(orderRegister.getPAccount())) {
                User user = userService.getByAccount(orderRegister.getPAccount());
                pId = user.getId();
            }
            if (pId == null) {
                throw new CrmebException("购买商品:" + product.getName() + ", 必须存在销售上级才允许购买");
            }
        }
        if (BooleanUtils.isTrue(temp.getHasRelation())) {
            Integer rPid = null;
            if (uId != null) {
                rPid = userRelationService.getPid(uId);
            }
            if (orderRegister != null && StringUtils.isNotEmpty(orderRegister.getRAccount())) {
                User user = userService.getByAccount(orderRegister.getRAccount());
                rPid = user.getId();
            }
            if (rPid == null) {
                throw new CrmebException("购买商品:" + product.getName() + ", 必须存在服务上级才允许购买");
            }
        }

        if (CollectionUtils.isNotEmpty(temp.getPCapaIdList())) {
            Long pCapaId = null;
            if (uId != null) {
                Integer pid = userRelationService.getPid(uId);
                UserCapa userCapa = pid != null ? userCapaService.getByUser(pid) : null;
                pCapaId = userCapa == null ? null : userCapa.getCapaId();
            }
            if (orderRegister != null && StringUtils.isNotEmpty(orderRegister.getPAccount())) {
                User user = userService.getByAccount(orderRegister.getPAccount());
                UserCapa userCapa = userCapaService.getByUser(user.getId());
                pCapaId = userCapa == null ? null : userCapa.getCapaId();
            }
            if (!temp.getPCapaIdList().contains(pCapaId)) {
                throw new CrmebException("购买商品:" + product.getName() + ", 上级等级权限不足");
            }

        }
        if (CollectionUtils.isNotEmpty(temp.getPCapaXsIdList())) {
            Long pCapaXsId = null;
            if (uId != null) {
                Integer pid = userRelationService.getPid(uId);
                UserCapaXs userCapaXs = pid != null ? userCapaXsService.getByUser(pid) : null;
                pCapaXsId = userCapaXs == null ? null : userCapaXs.getCapaId();
            }
            if (orderRegister != null && StringUtils.isNotEmpty(orderRegister.getPAccount())) {
                User user = userService.getByAccount(orderRegister.getPAccount());
                UserCapaXs userCapaXs = userCapaXsService.getByUser(user.getId());
                pCapaXsId = userCapaXs == null ? null : userCapaXs.getCapaId();
            }
            if (!temp.getPCapaXsIdList().contains(pCapaXsId)) {
                throw new CrmebException("购买商品:" + product.getName() + ", 上级星级权限不足");
            }
        }

    }


}
