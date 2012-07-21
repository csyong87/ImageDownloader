package com.cjavellana.img.downloader.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

	/**
	 * The date format in the form of <tt>dd-MMM-yyyy hh:mm:ss.SSSSS</tt> used
	 * by the image downloader application
	 */
	private static final String DATE_FORMAT = "dd-MMM-yyyy hh:mm:ss.SSSSS";

	private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

	/**
	 * Prevent Instantiation
	 */
	private DateFormatter() {
	}

	/**
	 * Formats the given <tt>date</tt> object to
	 * {@link DateFormatter#DATE_FORMAT} and returns a formatted String
	 * 
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		return sdf.format(date);
	}
}
