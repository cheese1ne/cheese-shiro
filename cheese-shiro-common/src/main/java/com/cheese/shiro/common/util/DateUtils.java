package com.cheese.shiro.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具
 * @author sobann
 */
public class DateUtils {
    private static ThreadLocal<SimpleDateFormat> DATE_FORMAT = ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS"));

    public static String getNow(){
        return DATE_FORMAT.get().format(new Date());
    }

    public static String format(Date date){
        if(date==null){
            return null;
        }
        return DATE_FORMAT.get().format(date);
    }

}
