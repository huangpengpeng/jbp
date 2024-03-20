package com.jbp.common.kqbill.utils;

import java.net.URLEncoder;

public class Demo {
   public static String appendParam(String returns, String paramId, String paramValue) {
       if (returns != "") {
           if (paramValue != "") {
               returns += "&" + paramId + "=" + paramValue;
           }
       } else {
           if (paramValue != "") {
               returns = paramId + "=" + paramValue;
           }
       }
       return returns;
   }








}
