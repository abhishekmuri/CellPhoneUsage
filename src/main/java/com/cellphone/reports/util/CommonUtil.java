package com.cellphone.reports.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonUtil {
	private static final Logger logger = LogManager.getLogger(CommonUtil.class);
	
	public static Date stringToDate(String pattern, String date)  {
		try {
			return new SimpleDateFormat(pattern).parse(date);
		} catch (ParseException e) {
			logger.error("Unable to parse ", e);
		}
		return null;
	}
	
	public static String dateToString(String pattern, Date date)  {
		return new SimpleDateFormat(pattern).format(date);
	}
}
