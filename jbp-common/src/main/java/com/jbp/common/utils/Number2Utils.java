package com.jbp.common.utils;


import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.math.NumberUtils;

public abstract class Number2Utils extends NumberUtils {

	public static boolean equals(Number arg0, Number arg1) {
		return (arg0 != null && arg0.equals(arg1));
	}

	public static boolean gt(Number arg0, Number arg1) {
		return (arg0 == null || arg1 == null) ? false : (arg0.doubleValue() > arg1.doubleValue());
	}

	public static boolean gte(Number arg0, Number arg1) {
		return (arg0 == null || arg1 == null) ? false : (arg0.doubleValue() >= arg1.doubleValue());
	}

	public static Long getLong(JSONObject jsonObject, String name) {
		if (jsonObject == null || !jsonObject.containsKey(name)) {
			return null;
		}

		if (StringUtils.equalsIgnoreCase("NULL", jsonObject.getString(name))) {
			return null;
		}
		return jsonObject.getLong(name);
	};

	public static Long getLong(JSONArray jsonArray, Integer count) {
		if (jsonArray.get(count) == null) {
			return null;
		}
		if (StringUtils.equalsIgnoreCase("NULL", jsonArray.getString(count))) {
			return null;
		}
		return jsonArray.getLong(count);
	};

	public static class TestNumber {

		public TestNumber(Long a, Long b) {
			this.a = a;
			this.b = b;
		}

		private Long a;

		private Long b;

		public Long getA() {
			return a;
		}

		public void setA(Long a) {
			this.a = a;
		}

		public Long getB() {
			return b;
		}

		public void setB(Long b) {
			this.b = b;
		}

	}
}
