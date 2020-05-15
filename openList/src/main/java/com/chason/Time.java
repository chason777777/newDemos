package com.chason;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/7/11
 */
public class Time {
    public static void main(String[] args) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));
    }
}
