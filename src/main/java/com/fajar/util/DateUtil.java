package com.fajar.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat();

	static final Calendar cal() {
		return Calendar.getInstance();
	}

	/**
	 * 
	 * @param year
	 * @param month starts at 0
	 * @param day
	 * @return
	 */
	public static Date getDate(int year, int month, int day) {

		Calendar cal = cal();
		cal.set(year, month, day);

		return cal.getTime();

	}

	public static Calendar cal(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static Integer[] getMonthsDay(int year) {
		boolean kabisat = year % 4 == 0;
		return new Integer[] { 31, (kabisat ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	}
	
	public static final String[] MONTH_NAMES = new String[] {
		"Januari",
		"Februari",
		"Maret",
		"April",
		"Mei",
		"Juni",
		"Juli",
		"Agustus",
		"September",
		"Oktober",
		"November",
		"Desember"
	};

	public static String formatDate(Date date, String pattern) {
		SIMPLE_DATE_FORMAT.applyPattern(pattern);
		return SIMPLE_DATE_FORMAT.format(date);
	}

	public static String getTimeGreeting() {
		int hour = cal().get(Calendar.HOUR_OF_DAY);
		String time = "Morning";
		if (hour >= 3 && hour < 11) {
			time = "Morning";
		} else if (hour >= 11 && hour < 18) {
			time = "Afternoon";
		} else {
			time = "Evening";
		}

		return time;
	}

	public static String getFullFirstDate(int month, int year) {
		return year + "-" + StringUtil.addZeroBefore(month) + "-01";
	}

	public static String getFullLastDate(int month, int year) {
		String date = "";
		Integer day = getMonthsDay(year)[month - 1];
		boolean kabisat = year % 4 == 0;
		if (kabisat && month == 2) {
			day = 29;
		}
		date = year + "-" + StringUtil.addZeroBefore(month) + "-" + day;
		return date;
	}

}
