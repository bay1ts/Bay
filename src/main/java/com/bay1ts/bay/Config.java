package com.bay1ts.bay;

/**
 * Created by chenu on 2016/10/14.
 */
public class Config {
    // TODO: 2016/10/15 处理 访问静态 目录中的目录,指定的welcome文件不存在的问题
    public static String welcomeFile="index.html";
    public static int port=5678;
    public static int sessionExpireSecond=3600;
    public static boolean enableSessionStore=false;//redis store
    public static String redisLocate="127.0.0.1";
    public static int redisPort=2333;
    public static String redisUsername="";
    public static String redisPassword="";

    public int port(){
        return port;
    }


}
