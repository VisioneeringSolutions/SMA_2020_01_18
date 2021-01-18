package com.vspl.music.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtil {
	
	public static final String[] janToDecMonthArr = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

	public static SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat monthDayYearFormater = new SimpleDateFormat("MMM d,y");
	public static SimpleDateFormat dateFormater = new SimpleDateFormat("MMMM");
	public static SimpleDateFormat dateTimeFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss");
	public static SimpleDateFormat dateMonthFormater = new SimpleDateFormat("MMM");
	
	
	public static TimeZone localTimeZone = TimeZone.getTimeZone("JST");

	
	public static String monthName(Date date) {
		return dateFormater.format(date);
	}
	
	public static String shortMonthName(Date date){
		return dateMonthFormater.format(date).toUpperCase();
	}

	public static String currMonth() {
		Calendar cal = Calendar.getInstance();
		String month = new SimpleDateFormat("MMM").format(cal.getTime());
		return month.toUpperCase();
	}

	public static String miliesToDate(long time) {
		Long dMiles = time;
		Locale locale = Locale.getDefault();
		TimeZone localTimeZone = TimeZone.getTimeZone("Asia/Tokyo");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
		dateFormat.setTimeZone(localTimeZone);
		Date date = new Date(dMiles);
		return dateFormat.format(date);
	}

	public static Date subtractDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	public static Date addDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, +1);
		return cal.getTime();
	}

	public static java.sql.Date sqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static Date currentDate() {
		Calendar calendar = GregorianCalendar.getInstance();
		return calendar.getTime();
	}

	public static String formatedCurrentDate() {
		Locale locale = Locale.getDefault();
		TimeZone localTimeZone = TimeZone.getTimeZone("Asia/Tokyo");
		DateFormat timeformater = new SimpleDateFormat("yyyy-MM-dd", locale);
		timeformater.setTimeZone(localTimeZone);
		Date rightNow = new Date();
		return timeformater.format(rightNow);
	}
	
	
	public static String formatedCurrentDateInFormat() {              // nikitaa
		Locale locale = Locale.getDefault();
		TimeZone localTimeZone = TimeZone.getTimeZone("Asia/Tokyo");
		DateFormat timeformater = new SimpleDateFormat("dd-MM-yyyy", locale);
		timeformater.setTimeZone(localTimeZone);
		Date rightNow = new Date();
		return timeformater.format(rightNow);
	}

	public static String formatedCurrentTime() {
		Locale locale = Locale.getDefault();
		TimeZone localTimeZone = TimeZone.getTimeZone("Asia/Tokyo");
		DateFormat timeformater = new SimpleDateFormat("HH:mm:ss", locale);
		timeformater.setTimeZone(localTimeZone);
		Date rightNow = new Date();
		return timeformater.format(rightNow);
	}
	
	public static String formatedCurrentDateTime() {
		Locale locale = Locale.getDefault();
		TimeZone localTimeZone = TimeZone.getTimeZone("Asia/Tokyo");
		DateFormat dateTimeFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
		dateTimeFormater.setTimeZone(localTimeZone);
		Date rightNow = new Date();
		return dateTimeFormater.format(rightNow);
	}

	public static Date yesterday() {
	    final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1);
	    return cal.getTime();
	}
	
	public static String formattedPreviousDay() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		String formatted = format1.format(cal.getTime());
		return formatted;
	}
	
	public static int currentYear() {
		Calendar calendar = GregorianCalendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}

	public static int currentDay() {
		Calendar calendar = GregorianCalendar.getInstance();
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static int year(String date) {
		return getCalender(date).get(Calendar.YEAR);
	}

	public static int month(String date) {
		return getCalender(date).get(Calendar.MONTH);
	}

	public static int day(String date) {
		return getCalender(date).get(Calendar.DAY_OF_MONTH);
	}

	public static Calendar getCalender(String date) {
		String[] dataArray = date.split("-");
		Calendar calendar = new GregorianCalendar(Integer.parseInt(dataArray[0]), Integer.parseInt(dataArray[1]) - 1,
				Integer.parseInt(dataArray[2]));
		return calendar;
	}
	
	public static String currentSession() {
		Calendar cal = Calendar.getInstance();
		int curr = cal.get(Calendar.YEAR);
		if (cal.get(Calendar.MONTH) < 3) {
			cal.add(Calendar.YEAR, -1);
			int prev = cal.get(Calendar.YEAR);
			return prev + "-" + curr;
		}
		cal.add(Calendar.YEAR, 1); // to get next year add +1
		int next = cal.get(Calendar.YEAR);
		return curr + "-" + next;
	}

	public static String previousSession() {
		Calendar cal = Calendar.getInstance();
		int curr = cal.get(Calendar.YEAR) - 1;
		cal.add(Calendar.YEAR, 0);
		int next = cal.get(Calendar.YEAR);
		return curr + "-" + next;
	}

	public static int currentMonth() {
		Calendar calendar = GregorianCalendar.getInstance();
		return calendar.get(Calendar.MONTH) + 1;
	}
	
	private static Calendar getCalendarForNow(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	
	private static void setTimeToBeginningOfDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	private static void setTimeToEndofDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
	}
	
	public static Date monthStartDate(Date date) {
		Calendar calendar = getCalendarForNow(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		setTimeToBeginningOfDay(calendar);
		return calendar.getTime();
	}

	public static Date monthEndDate(Date date) {
		Calendar calendar = getCalendarForNow(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		setTimeToEndofDay(calendar);
		return calendar.getTime();
	}
	
	public static String getDayNameFromDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("EEEE"); 
		String finalDay=format.format(date);
		return finalDay;
	}
	
	public static int getDiffOfTwoDate(String date1, String date2) throws ParseException {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date1Value = myFormat.parse(date1);
	    Date date2Value = myFormat.parse(date2);
	    long diff = date1Value.getTime() - date2Value.getTime();
	    int dayDiff = Integer.valueOf((diff/(1000*60*60*24))+"");
		return dayDiff;
	}
	
	public static String dateOfSunday(String date1) throws ParseException {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date1Value = myFormat.parse(date1);
		Calendar calendar = getCalendarForNow(date1Value);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.SUNDAY);
		return myFormat.format(calendar.getTime());
	}
	
	public static String formatAsDDMMYYY(String date) throws ParseException {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = simpleDateFormat.parse(date);
        return new SimpleDateFormat("dd/MM/yyyy").format(myDate);
	}
	
	// For setting time of current date

	@SuppressWarnings("deprecation")
	public static Date setTimeToCurrentDate(String t1) {
		DateFormat timeformater = new SimpleDateFormat("HH:mm");
		timeformater.setTimeZone(localTimeZone);
		Date time1 = null;
		try {
			time1 = timeformater.parse(t1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//System.out.println("time1 --- " + time1);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		// cal.setTimeZone(localTimeZone);
		cal.set(Calendar.HOUR_OF_DAY, time1.getHours());
		cal.set(Calendar.MINUTE, time1.getMinutes());
		cal.set(Calendar.SECOND, 0);
		cal.setTimeZone(TimeZone.getTimeZone("IST"));
		return cal.getTime();
	}
	
}
