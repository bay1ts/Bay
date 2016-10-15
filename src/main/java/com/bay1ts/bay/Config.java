package com.bay1ts.bay;

public class Config {
    private static String welcomeFile = "index.html";
    private static int port = 5677;
    private static int sessionExpireSecond = 3600;
    private static boolean enableSessionStore = false;//redis store
    private static String redisLocate = "127.0.0.1";
    private static int redisPort = 2333;
    private static String redisUsername = "root";
    private static String redisPassword = "toor";

    //// TODO: 2016/10/15 在每一项调用配置之前都需要更新配置.  每一次获取数据都要调用update.
    private void updateConfig(){

    }

    public static String getWelcomeFile() {
        return welcomeFile;
    }

    public static int getPort() {
        return port;
    }

    public static int getSessionExpireSecond() {
        return sessionExpireSecond;
    }

    public static boolean isEnableSessionStore() {
        return enableSessionStore;
    }

    public static String getRedisLocate() {
        return redisLocate;
    }

    public static int getRedisPort() {
        return redisPort;
    }

    public static String getRedisUsername() {
        return redisUsername;
    }

    public static String getRedisPassword() {
        return redisPassword;
    }
}