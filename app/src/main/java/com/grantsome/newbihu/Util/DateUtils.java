package com.grantsome.newbihu.Util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by tom on 2017/4/28.
 */

public class DateUtils {

    public static String getDataDescription(String formatDate){
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        //获取yyyy  mm  dd xx:xx:xx
        String[] data = formatDate.split("-");
        //获取 xx:xx:xx
        String time = data[2].substring(3);
        //获取 dd
        data[2] = data[2].substring(0,2);

        int dy = gregorianCalendar.get(Calendar.YEAR) - Integer.parseInt(data[0]);
        int dm = gregorianCalendar.get(Calendar.MONTH) + 1 - Integer.parseInt(data[1]);
        int dd = gregorianCalendar.get(Calendar.DAY_OF_MONTH) - Integer.parseInt(data[2]);
        if(dy==0&&dm==0){
            switch (dd){
                case 0:
                    return "今天" +time;
                case 1:
                    return "昨天" +time;
                case 2:
                    return "前天" +time;
                default:
                    return formatDate;
            }
        }
        return formatDate;
    }
}
