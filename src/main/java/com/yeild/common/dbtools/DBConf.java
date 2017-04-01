package com.yeild.common.dbtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.yeild.common.Utils.ConvertUtils;

public class DBConf {
	public static String dbConfName="db.properties";
	public static String dbConfPath=null;
    private static Properties dbProperties = null;
    public static String dbPoolUsed="proxool";
    public static String dbNameDefault="postgre";

    public static void loadDbConfig(String confPath) throws IOException {
    	dbConfPath = confPath;
    	InputStream confInputStream = null;
		try {
			File confFile = new File(dbConfPath + dbConfName);
			confInputStream = new FileInputStream(confFile);
			dbProperties = new Properties();
			dbProperties.load(confInputStream);
			dbPoolUsed = dbProperties.getProperty("database.pool", "proxool");
			dbNameDefault = dbProperties.getProperty("database.default", "postgre");
		} catch (IOException e) {
			throw e;
		} finally {
			if (confInputStream != null) {
				try {
					confInputStream.close();
				} catch (IOException e) {
				}
			}
		}
    }
    
    public static String getDbConf(String name) {
        return getDbConfWithName(dbNameDefault, name);
    }
    
    public static String getDbConf(String name, String defaultValue) {
        return getDbConfWithName(dbNameDefault, name, defaultValue);
    }
    
    public static String getDbConfWithName(String dbname, String name) {
        return getDbConfWithName(dbname, name, null);
    }
    
    public static String getDbConfWithName(String dbname, String name, String defaultValue) {
        if (dbProperties == null) {
            return defaultValue;
        }
        return dbProperties.getProperty(dbname+"."+name, defaultValue);
    }
    
    public static String getDbPoolConf(String name) {
        return getDbPoolConf(name, null);
    }
    
    public static String getDbPoolConf(String name, String defaultValue) {
        if (dbProperties == null) {
            return defaultValue;
        }
        return dbProperties.getProperty(dbPoolUsed+"."+name, defaultValue);
    }
    public static int getDbPoolConf(String name, int defaultValue) {
        return ConvertUtils.parseInt(getDbPoolConf(name), defaultValue);
    }
    public static boolean getDbPoolConf(String name, boolean defaultValue) {
        return ConvertUtils.parseBoolean(getDbPoolConf(name), defaultValue);
    }
}
