package com.jbp.front;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.yop.result.WechatAlipayTutelagePayResult;
import com.jbp.service.service.YopService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WhDemo {

    public static void demo() {
        List<Map<String, Object>> mapList = SqlRunner.db().selectList(" select * from bank_flow where 1=1 ");

        BigDecimal balance = BigDecimal.valueOf(320027.15);
        for (Map<String, Object> map : mapList) {
            Integer id = MapUtils.getInteger(map, "id");
            String time = MapUtils.getString(map, "time");
            Date date = DateTimeUtils.parseDate(time);
            String newTime = DateTimeUtils.format(DateTimeUtils.addYears(date, -1), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN);

            BigDecimal amt = BigDecimal.valueOf(MapUtils.getDouble(map, "amt"));
            String type = MapUtils.getString(map, "type");

            if ("进".equals(type)) {
                balance = amt.add(balance);

            } else {
                balance = balance.subtract(amt);
            }
            if (ArithmeticUtils.lessEquals(balance, BigDecimal.ZERO)) {
                SqlRunner.db().delete("delete bank_flow where id = {0}", id);
            } else {
                SqlRunner.db().delete("update bank_flow  set balance = {0}, time={1} where  id = {2}", balance, time, id);
            }
        }


    }

}
