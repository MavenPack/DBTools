package com.yeild.common.dbtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.yeild.common.Utils.ConvertUtils;

public class DBConf {
	private static Logger logger = Logger.getLogger(DBConf.class);
	public static String dbConfName="db.properties";
	public static String dbConfPath=null;
    private static Properties dbDefProperties = null;
    private static Properties dbProperties = null;
    public static String dbPoolUsed="proxool";
    public static String dbNameDefault="postgre";

    public static void loadDbConfig(String confPath) throws IOException {
    	if(dbDefProperties == null) {
    		InputStream inputStream = DBConf.class.getClassLoader().getResourceAsStream(dbConfName);
    		if(inputStream == null) {
    			throw new IOException("default db config file not found.");
    		}
    		dbDefProperties = new Properties();
    		dbDefProperties.load(inputStream);
    	}
    	dbConfPath = confPath;
    	FileInputStream confInputStream = null;
    	try {
    		File confFile = new File(dbConfPath + dbConfName);
    		confInputStream = new FileInputStream(confFile);
    		dbProperties = new Properties();
    		dbProperties.load(confInputStream);
    	} catch (IOException e) {
    		logger.debug("Could not find config file for path " + dbConfPath + dbConfName);
    		logger.debug("Use default db config.");
		} finally {
			if(confInputStream != null) {
				try{
					confInputStream.close();
				} catch (Exception e2) {}
			}
		}
		dbPoolUsed = getConfValue("database.pool", "proxool");
		dbNameDefault = getConfValue("database.default", "postgre");
    }
    
    private static String getConfValue(String key, String defValue) {
    	return dbProperties == null ? dbDefProperties.getProperty(key, defValue) : dbProperties.getProperty(key, dbDefProperties.getProperty(key, defValue));
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
        return getConfValue(dbname+"."+name, defaultValue);
    }
    
    public static String getDbPoolConf(String name) {
        return getDbPoolConf(null, name, null);
    }
    
    public static String getDbPoolConf(String dbname, String name) {
    	return getDbPoolConf(dbname, name, null);
    }
    
    public static String getDbPoolConf(String dbname, String name, String defaultValue) {
    	return getConfValue((dbname==null?"":(dbname+"."))+dbPoolUsed+"."+name
    			, dbname==null?defaultValue:getConfValue(dbPoolUsed+"."+name, defaultValue));
    }
    public static int getDbPoolConf(String name, int defaultValue) {
        return getDbPoolConf(null, name, defaultValue);
    }
    public static int getDbPoolConf(String dbname, String name, int defaultValue) {
    	return ConvertUtils.parseInt(getDbPoolConf(dbname, name), defaultValue);
    }
    public static boolean getDbPoolConf(String name, boolean defaultValue) {
        return getDbPoolConf(null, name, defaultValue);
    }
    public static boolean getDbPoolConf(String dbname, String name, boolean defaultValue) {
    	return ConvertUtils.parseBoolean(getDbPoolConf(dbname, name), defaultValue);
    }
}
