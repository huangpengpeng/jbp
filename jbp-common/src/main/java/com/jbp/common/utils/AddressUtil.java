package com.jbp.common.utils;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressUtil {


    public static List<Map<String,String>> addressResolution(String address){
        /*
         * java.util.regex是一个用正则表达式所订制的模式来对字符串进行匹配工作的类库包。它包括两个类：Pattern和Matcher Pattern
         *    一个Pattern是一个正则表达式经编译后的表现模式。 Matcher
         *    一个Matcher对象是一个状态机器，它依据Pattern对象做为匹配模式对字符串展开匹配检查。
         *    首先一个Pattern实例订制了一个所用语法与PERL的类似的正则表达式经编译后的模式，然后一个Matcher实例在这个给定的Pattern实例的模式控制下进行字符串的匹配工作。
         */
        String regex="(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
        Matcher m= Pattern.compile(regex).matcher(address);
        String province=null,city=null,county=null,town=null,village=null;
        List<Map<String,String>> table=new ArrayList<Map<String,String>>();
        Map<String,String> row=null;
        while(m.find()){
            row=new LinkedHashMap<String,String>();
            province=m.group("province");
            row.put("province", province==null?"":province.trim());
            city=m.group("city");
            row.put("city", city==null?"":city.trim());
            county=m.group("county");
            row.put("county", county==null?"":county.trim());
            town=m.group("town");
            row.put("town", town==null?"":town.trim());
            village=m.group("village");
            row.put("village", village==null?"":village.trim());
            table.add(row);
        }
        return table;
    }

    public static Map<String, String> getAddress(String address) {
        Map<String, String> map = Maps.newConcurrentMap();
        if (StringUtils.isEmpty(address)) {
            return map;
        }

        address = address.replace("null","");
        List<Map<String, String>> table = addressResolution(address);
        map.put("province", table.get(0).get("province"));
        map.put("city", table.get(0).get("city"));
        map.put("district", table.get(0).get("county"));
        map.put("street", table.get(0).get("town"));
        map.put("detail", table.get(0).get("village"));
        return map;
    }

    public static void main(String[] args) {

        Map<String, String> map = getAddress("江苏省泰州市兴化市昭阳街道板桥路213--4鞋动力店");
        System.out.println(map.get("province"));
        System.out.println(map.get("city"));
        System.out.println(map.get("district"));
        System.out.println(map.get("street")+map.get("detail"));
        System.out.println(map.get("detail"));
    }

}
