package com.rmsi.android.mast.util;

/**
 * Contains various string helper function
 */

public class StringUtility {
    /**
     * Check given string and returns true if null or empty, otherwise false.
     * @param str String to check
     */
    public static boolean isEmpty(String str){
        return str == null || str.equals("");
    }

    /**
     * Checks string for null and empty and if true, returns empty value, otherwise provided string will be returned.
     * @param str String to check
     */
    public static String empty(String str){
        if(isEmpty(str)){
            return "";
        }
        return str;
    }
}
