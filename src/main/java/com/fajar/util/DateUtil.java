package com.fajar.util;

public class DateUtil {
	
	public static Integer[] getMonths(){
		return new Integer[] {
			31,28,31,30,31,30,31,31,30,31,30,31
		} ;
	}
	
	public static String getFullFirstDate(int month, int year) {
		return year+"-"+StringUtil.addZeroBefore(month)+"-01";
	}
	
	public static String getFullLastDate(int month, int year) {
		String date = "";
		Integer day = getMonths()[month-1];
		boolean kabisat = year % 4 == 0;
		if(kabisat && month == 2) {
			day = 29;
		}
		date = year+"-"+StringUtil.addZeroBefore(month)+"-"+day;
		return date;
	}

}
