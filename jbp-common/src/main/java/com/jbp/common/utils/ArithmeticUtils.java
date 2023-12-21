package com.jbp.common.utils;

import java.math.BigDecimal;

public class ArithmeticUtils {
	private ArithmeticUtils() {
	}
	
	/**
	 * roundingMode:0 上
	 * 1下
	 */
	public static int dividend(long divide,long divisor,int roundingMode) {
		BigDecimal divideDec=BigDecimal.valueOf(divide);
		BigDecimal divisorDec=BigDecimal.valueOf(divisor);
		return divideDec.divide(divisorDec, 0, roundingMode).intValue();
	}

	public static int dividend(int dividend) {
		return dividend == 0 ? 1 : dividend;
	}

	public static long dividend(long dividend) {
		return dividend == 0 ? 1 : dividend;
	}
	
	/**
	 * 相隔数相等
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public static boolean equals(BigDecimal arg0, BigDecimal arg1) {
		return arg0.compareTo(arg1) == 0;
	}

	/**
	 * 小于
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public static boolean less(BigDecimal arg0, BigDecimal arg1) {
		return arg0.compareTo(arg1) == -1;
	}

	/**
	 * 小与或等于
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public static  boolean lessEquals(BigDecimal arg0, BigDecimal arg1){
		return arg0.compareTo(arg1) <= 0;
	}

	/**
	 * 大于等于
	 * 
	 * @return
	 */
	public static boolean gte(BigDecimal arg0, BigDecimal arg1) {
		return arg0.compareTo(arg1) >= 0;
	}

	/**
	 * 大于等于
	 * 
	 * @return
	 */
	public static boolean gt(BigDecimal arg0, BigDecimal arg1) {
		return arg0.compareTo(arg1) > 0;
	}
}
