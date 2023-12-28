package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.b2b.Wallet;
import com.jbp.common.model.b2b.WalletFlow;
import com.jbp.service.dao.WalletDao;
import com.jbp.service.service.WalletService;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl extends ServiceImpl<WalletDao, Wallet> implements WalletService {
}
