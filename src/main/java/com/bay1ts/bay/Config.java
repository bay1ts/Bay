package com.bay1ts.bay;

import com.bay1ts.bay.route.match.DoRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.Properties;

public class Config {
    private static Logger logger= LoggerFactory.getLogger(Config.class);
    private final static Config INSTANCE=new Config();
    private Config(){

    }
    //属性值为默认值
    private  String welcomeFile = "index.html";
    private  int port = 5677;
    private  int sessionExpireSecond = 3600;
    private  boolean enableSessionStore = false;//redis store
    private  String redisLocate = "127.0.0.1";
    private  int redisPort = 2333;
    private  String redisPassword = "toor";

    public static Config builder(){
        return INSTANCE;
    }
    public static Config instance(){
        return INSTANCE;
    }
    public Config port(int port){
        this.port=port;
        return INSTANCE;
    }
    public Config enableRedisSession(boolean enable){
        this.enableSessionStore=enable;
        return INSTANCE;
    }
    public Config redisIP(String ip) {
        if (this.enableSessionStore){
            this.redisLocate=ip;
            return INSTANCE;
        }else {
            throw new RuntimeException("please enable redis session store!");
        }
    }
    public Config redisPort(int port){
        if (this.enableSessionStore){
            this.redisPort=port;
            return INSTANCE;
        }else {
            throw new RuntimeException("please enable redis session store!");
        }
    }
    public Config redisPassword(String  password){
        if (this.enableSessionStore){
            this.redisPassword=password;
            return INSTANCE;
        }else {
            throw new RuntimeException("please enable redis session store!");
        }
    }
    public Config sessionExpireSecond(int time){
        this.sessionExpireSecond=time;
        return INSTANCE;
    }

    //放弃了 配置文件的方式
//    private static void updateConfig() throws IOException {
//        logger.debug("updating config from conf.properties");
//        Properties properties=new Properties();
//        properties.load(DoRoute.class.getClass().getClassLoader().getResourceAsStream("conf.properties"));
//        welcomeFile=properties.getProperty("welcomeFile");
//        port=Integer.valueOf(properties.getProperty("port"));
//        sessionExpireSecond=Integer.valueOf(properties.getProperty("sessionExpireSecond"));
//        enableSessionStore=Boolean.valueOf(properties.getProperty("enableSessionStore"));
//        redisLocate=properties.getProperty("redisLocate");
//        redisUsername=properties.getProperty("redisUsername");
//        redisPassword=properties.getProperty("redisPassword");
//    }

    public String getWelcomeFile() {
        return welcomeFile;
    }

    public  int getPort() {
        return port;
    }

    public int getSessionExpireSecond() {
        return sessionExpireSecond;
    }

    public boolean isEnableSessionStore() {
        return enableSessionStore;
    }

    public String getRedisLocate() {
        return redisLocate;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public String getRedisPassword() {
        return redisPassword;
    }
}