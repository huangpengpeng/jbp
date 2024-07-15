package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.model.order.OrderPayChannel;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.dao.OrderPayChannelDao;
import com.jbp.service.service.OrderPayChannelService;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class PayChannelServiceImpl extends ServiceImpl<OrderPayChannelDao, OrderPayChannel> implements OrderPayChannelService {

    @Setter
    private Map<String, Integer> currentIndex = Maps.newConcurrentMap(); // 上一次选择的渠道
    @Setter
    private Map<String, Integer> currentWeight= Maps.newConcurrentMap();// 当前调度的权值
    @Setter
    private Map<String, Integer> maxWeight= Maps.newConcurrentMap(); // 最大权重
    @Setter
    private Map<String, Integer> gcdWeight= Maps.newConcurrentMap(); //所有服务器权重的最大公约数
    @Setter
    private Map<String, Integer>  serverCount= Maps.newConcurrentMap(); //服务器数量
    @Setter
    private Map<String, List<OrderPayChannel>> serverList= Maps.newConcurrentMap(); //服务器集合

//    @PostConstruct
    public void init() {
        List<OrderPayChannel> list = list(new LambdaQueryWrapper<OrderPayChannel>().gt(OrderPayChannel::getWeight, 0));
        serverList = FunctionUtil.valueMap(list, OrderPayChannel::getPayMethod);
        serverList.forEach((k,v)->{
            serverCount.put(k, v.size());
            currentIndex.put(k, -1);
            currentWeight.put(k, 0);
            maxWeight.put(k, getMaxWeightForServers(v));
            gcdWeight.put(k, getGCDForServers(v));
        });
    }

    public void refresh() {
        List<OrderPayChannel> list = list(new LambdaQueryWrapper<OrderPayChannel>().gt(OrderPayChannel::getWeight, 0));
        List<OrderPayChannel> orgList = Lists.newArrayList();
        serverList.forEach((k,v)->{
            orgList.addAll(v);
        });

        if (list.size() != orgList.size()) {
            init();
        }
        Map<String, Integer> map = FunctionUtil.keyValueMap(orgList, OrderPayChannel::getMerchantNo, OrderPayChannel::getWeight);
        Boolean ifRefresh = false;
        for (OrderPayChannel orderPayChannel : list) {
            Integer weight = map.get(orderPayChannel.getMerchantNo());
            if (weight == null) {
                ifRefresh = true;
                break;
            }
            if (weight.intValue() != orderPayChannel.getWeight()) {
                ifRefresh = true;
                break;
            }
        }
        if (ifRefresh) {
            init();
        }
    }

    /**
     *  算法流程：
     *  假设有一组服务器 S = {S0, S1, …, Sn-1}
     *  有相应的权重，变量currentIndex表示上次选择的服务器
     *  权值currentWeight初始化为0，currentIndex初始化为-1 ，当第一次的时候返回 权值取最大的那个服务器，
     *  通过权重的不断递减 寻找 适合的服务器返回，直到轮询结束，权值返回为0
     */
    @Override
    public OrderPayChannel getServer(String payMethod) {
        refresh();
        if (serverList.get(payMethod) == null || serverList.get(payMethod).size() == 0) {
            return null;
        }
        while (true) {
            Integer serverCountMath = serverCount.get(payMethod);
            Integer currentIndexMath = (currentIndex.get(payMethod) + 1) % serverCountMath;
            currentIndex.put(payMethod, currentIndexMath);
            if (currentIndexMath == 0) {
                Integer currentWeightMath = currentWeight.get(payMethod) - gcdWeight.get(payMethod);
                currentWeight.put(payMethod, currentWeightMath);
                if (currentWeightMath.intValue() <= 0) {
                    currentWeight.put(payMethod, maxWeight.get(payMethod));
                    if (currentWeight.get(payMethod).intValue() == 0)
                        return null;
                }
            }
            if (serverList.get(payMethod).get(currentIndex.get(payMethod).intValue()).getWeight() >= currentWeight.get(payMethod)) {
                return serverList.get(payMethod).get(currentIndex.get(payMethod).intValue());
            }
        }
    }




    /**
     * 返回最大公约数
     * @param a
     * @param b
     * @return
     */
    private static int gcd(int a, int b) {
        BigInteger b1 = new BigInteger(String.valueOf(a));
        BigInteger b2 = new BigInteger(String.valueOf(b));
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }


    /**
     * 返回所有服务器权重的最大公约数
     * @param serverList
     * @return
     */
    private static int getGCDForServers(List<OrderPayChannel> serverList ) {
        int w = 0;
        for (int i = 0, len = serverList.size(); i < len - 1; i++) {
            if (w == 0) {
                w = gcd(serverList.get(i).getWeight(), serverList.get(i + 1).getWeight());
            } else {
                w = gcd(w, serverList.get(i + 1).getWeight());
            }
        }
        return w;
    }


    /**
     * 返回所有服务器中的最大权重
     * @param serverList
     * @return
     */
    public static int getMaxWeightForServers(List<OrderPayChannel> serverList) {
        int w = 0;//服务器的最大权重
        for (int i = 0, len = serverList.size(); i < len - 1; i++) {
            if (w == 0) {
                w = Math.max(serverList.get(i).getWeight(), serverList.get(i + 1).getWeight());
            } else {
                w = Math.max(w, serverList.get(i + 1).getWeight());
            }
        }
        return w;
    }

}
