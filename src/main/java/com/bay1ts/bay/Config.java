package com.bay1ts.bay;

/**
 * Created by chenu on 2016/10/14.
 */
public class Config {
    // TODO: 2016/10/15 处理 访问静态 目录中的目录,指定的welcome文件不存在的问题
    public static String welcomeFile="index.html";
    public static final int port=8081;
    public static final int sessionExpireSecond=3600;
    public static final boolean enableSessionStore=false;//redis store
    public static final String redisLocate="127.0.0.1";
    public static final int redisPort=2333;
    public static final String redisUsername="";
    public static final String redisPassword="";


}
