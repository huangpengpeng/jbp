package com.jbp.admin.task.user;

import cn.hutool.core.date.DateUtil;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户星级任务
 */
@Component("UserCapaXsTask")
public class UserCapaXsTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(UserCapaXsTask.class);

    @Autowired
    private UserInvitationService invitationService;
    @Autowired
    private UserCapaXsService userCapaXsService;
    @Autowired
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 每天凌晨1点执行
     */
    public void refreshUserCapaXs() {
        // cron : 0 0 1 * * ?
        logger.info("---UserCapaXsTask refreshUserCapaXs------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        if (StringUtils.isNotEmpty(stringRedisTemplate.opsForValue().get("refreshUserCapaXs"))){
            logger.info("---UserCapaXsTask refreshUserCapaXs-----未执行完成忽略本次", DateUtil.date());
            return;
        }
        stringRedisTemplate.opsForValue().set("refreshUserCapaXs","1");
        try {
            // 查询没下级的用户
            List<User> list = userService.getNoChild();
            // 所有的底层用户往上一次升级
            for (User user : list) {
                // 所有的上级
                List<UserUpperDto> allUpper = invitationService.getAllUpper(user.getId());
                if(CollectionUtils.isEmpty(allUpper)){
                    userCapaXsService.riseCapaXs(user.getId());
                }
                // 升星
                for (UserUpperDto upperDto : allUpper) {
                    userCapaXsService.riseCapaXs(upperDto.getUId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserCapaXsTask.refreshUserCapaXs" + " | msg : " + e.getMessage());
        }finally {
            stringRedisTemplate.delete("refreshUserCapaXs");
        }
    }
}
