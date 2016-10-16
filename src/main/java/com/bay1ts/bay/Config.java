package com.bay1ts.bay;

import com.bay1ts.bay.route.match.DoRoute;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.Properties;

public class Config {
    //属性值为默认值
    private static String welcomeFile = "index.html";
    private static int port = 5677;
    private static int sessionExpireSecond = 3600;
    private static boolean enableSessionStore = false;//redis store
    private static String redisLocate = "127.0.0.1";
    private static int redisPort = 2333;
    private static String redisUsername = "root";
    private static String redisPassword = "toor";

    //// TODO: 2016/10/15 在每一项调用配置之前都需要更新配置.  每一次获取数据都要调用update.
    private static void updateConfig() throws IOException {
        Properties properties=new Properties();
        properties.load(DoRoute.class.getClass().getClassLoader().getResourceAsStream("conf.properties"));
        welcomeFile=properties.getProperty("welcomeFile");
        port=Integer.valueOf(properties.getProperty("port"));
        sessionExpireSecond=Integer.valueOf(properties.getProperty("sessionExpireSecond"));
        enableSessionStore=Boolean.valueOf(properties.getProperty("enableSessionStore"));
        redisLocate=properties.getProperty("redisLocate");
        redisUsername=properties.getProperty("redisUsername");
        redisPassword=properties.getProperty("redisPassword");
    }

    public static String getWelcomeFile() {
        try {
            updateConfig();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 2016/10/16 log
        }finally {
            return welcomeFile;
        }
    }

    public static int getPort() {
        try {
            updateConfig();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 2016/10/16 log
        }finally {
            return port;
        }

    }

    public static int getSessionExpireSecond() {
        try {
            updateConfig();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 2016/10/16 log
        }finally {
            return sessionExpireSecond;
        }

    }

    public static boolean isEnableSessionStore() {
        try {
            updateConfig();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 2016/10/16 log
        }finally {
            return enableSessionStore;
        }

    }

    public static String getRedisLocate() {
        try {
            updateConfig();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 2016/10/16 log
        }finally {
            return redisLocate;
        }

    }

    public static int getRedisPort() {
        try {
            updateConfig();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 2016/10/16 log
        }finally {
            return redisPort;
        }

    }

    public static String getRedisUsername() {
        try {
            updateConfig();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 2016/10/16 log
        }finally {
            return redisUsername;
        }

    }

    public static String getRedisPassword() {
        try {
            updateConfig();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 2016/10/16 log
        }finally {
            return redisPassword;
        }

    }
}