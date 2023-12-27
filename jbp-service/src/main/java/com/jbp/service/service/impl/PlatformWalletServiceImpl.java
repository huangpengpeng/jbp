package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.b2b.PlatformWallet;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.PlatformWalletDao;
import com.jbp.service.service.PlatformWalletService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PlatformWalletServiceImpl extends ServiceImpl<PlatformWalletDao, PlatformWallet> implements PlatformWalletService {
    @Resource
    private PlatformWalletDao platformWalletDao;

    @Override
    public List<PlatformWallet> pageList(PlatformWallet request, PageParamRequest pageParamRequest) {
        return null;
    }
}
