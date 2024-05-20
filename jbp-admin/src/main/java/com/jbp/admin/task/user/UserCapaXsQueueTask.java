package com.jbp.admin.task.user;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.constants.TaskConstants;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.utils.RedisUtil;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户星级队列任务
 */
@Component("UserCapaXsQueueTask")
public class UserCapaXsQueueTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(UserCapaXsQueueTask.class);

    @Autowired
    private UserCapaXsService userCapaXsService;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserInvitationService invitationService;


    public void UserCapaXsQueueTask() {
        logger.info("---升星队列执行------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        try {
            String redisKey = TaskConstants.TASK_CAPA_XS_USER_MASSAGE;
            Long size = redisUtil.getListSize(redisKey);
            logger.info("UserCapaXsTask.refreshUserCapaQueueXs | size:" + size);
            for (int i = 0; i < size; i++) {
                Object data = redisUtil.getRightPop(redisKey, 10L);
                if (ObjectUtil.isNull(data)) {
                    continue;
                }

                List<UserUpperDto> list = invitationService.getAllUpper(Integer.valueOf(data.toString()));
                UserUpperDto userUpperDto2 = new UserUpperDto();
                userUpperDto2.setPId(Integer.valueOf(data.toString()));
                list.add(userUpperDto2);
                for (UserUpperDto userUpperDto : list) {
                    userCapaXsService.riseCapaXs(userUpperDto.getPId());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserCapaXsTask.refreshUserCapaQueueXs" + " | msg : " + e.getMessage());
        }
    }
}
