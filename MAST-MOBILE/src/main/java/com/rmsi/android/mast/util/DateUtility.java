package com.rmsi.android.mast.util;

import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.DATE;

/**
 * Date and time utilities
 */
public class DateUtility {

    /**
     * Formats provided date string into yyyy-mm-dd
     *
     * @param dateString Date string to format
     * @return
     */
    public static String formatDateString(String dateString) {
        if (StringUtility.isEmpty(dateString)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String result = "";

        try {
            calendar.setTime(sdf.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatDate(calendar.getTime());
    }

    /**
     * Checks if provided date is in future. If date in null, false will be returned
     * @param date Date to test
     */
    public static boolean isDateInFuture(Date date){
        if(date == null)
            return false;
        Calendar cal = Calendar.getInstance();
        return date.after(cal.getTime());
    }

    /**
     * Returns date created from provided string. If string is null or empty, current date and time will be returned.
     *
     * @param dateString Date string in format  yyyy-mm-dd
     * @return
     */
    public static Date getDate(String dateString) {
        Calendar calendar = Calendar.getInstance();

        if (StringUtility.isEmpty(dateString)) {
            return calendar.getTime();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            calendar.setTime(sdf.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTime();
    }

    /**
     * Returns current date in string format.
     */
    public static String getCurrentStringDate() {
        Calendar calender = Calendar.getInstance();
        return formatDate(calender.getTime());
    }

    /**
     * Returns current date.
     */
    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * Formats provided date to the string
     * @param date Date to format
     * @return
     */
    public static String formatDate(Date date) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);

        int day = calender.get(Calendar.DAY_OF_MONTH);
        String strday = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
        int month = calender.get(Calendar.MONTH) + 1; // adding 1 as month starts with 0
        String strmonth = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
        int year = calender.get(YEAR);

        return year + "-" + strmonth + "-" + strday;
    }

    /**
     * Returns date from DatePicker object
     * @param datePicker DatePicker object
     * @return
     */
    public static Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    /**
     * Returns formatted date string from DatePicker object
     * @param datePicker DatePicker object
     * @return
     */
    public static String getStringDateFromDatePicker(DatePicker datePicker){
        return formatDate(getDateFromDatePicker(datePicker));
    }

    /** Returns dates difference in years. */
    public static int getDiffYears(Date first, Date last) {
        Calendar a = Calendar.getInstance();
        a.setTime(first);
        Calendar b = Calendar.getInstance();
        b.setTime(last);

        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }
}
