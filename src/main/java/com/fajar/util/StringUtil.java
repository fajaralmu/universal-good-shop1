package com.fajar.util;

import java.util.Random;

public class StringUtil {
	
	public static String generateRandomNumber(int length) {
		Random rand = new Random();
		String random = "";
		if(length<1) {
			length=1;
		}
		for (int i = 0; i < length; i++) {
			Integer n = rand.nextInt(9);
			random+=n;
		}
		return random;
	}
	
	public static void main(String[] xxx) {
		for(int i=1;i<=611;i++) {
			System.out.println("update `transaction` set code= '"+generateRandomNumber(10)+"' where id="+i+";");
		}
	}
	
	public static String addZeroBefore(Integer number) {
		return number < 10 ? "0"+number:number.toString();
	}

}
