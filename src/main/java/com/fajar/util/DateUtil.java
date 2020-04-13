package com.fajar.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	static final SimpleDateFormat SIMPLEDA_DATE_FORMAT = new SimpleDateFormat();
	
	static final Calendar cal() { return Calendar.getInstance(); }
	
	public static Integer[] getMonths(){
		return new Integer[] {
			31,28,31,30,31,30,31,31,30,31,30,31
		} ;
	}
	
	public static String formatDate(Date date, String pattern) {
		SIMPLEDA_DATE_FORMAT.applyPattern(pattern);
		return SIMPLEDA_DATE_FORMAT.format(date);
	}
	
	public static String getTimeGreeting() {
		int hour = cal().get(Calendar.HOUR_OF_DAY);
		String time = "Morning";
		if(hour>=3 &&hour < 11) {
			time = "Morning";
		}else if(hour>=11 && hour<18) {
			time = "Afternoon";
		}else {
			time = "Evening";
		}
			 
		return time;
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
