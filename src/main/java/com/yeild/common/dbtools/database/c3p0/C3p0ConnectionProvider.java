package com.yeild.common.dbtools.database.c3p0;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.yeild.common.dbtools.DBConf;
import com.yeild.common.dbtools.database.ConnectionProvider;
import com.yeild.common.dbtools.database.DbConnectionManager;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

public class C3p0ConnectionProvider implements ConnectionProvider {
	private Logger logger = Logger.getLogger(getClass().getSimpleName());
	private ComboPooledDataSource pooledDataSource;
    private String dbname;
	
	public C3p0ConnectionProvider() {
		initDatasource();
	}
	
	public C3p0ConnectionProvider(String dbName) {
		setDbName(dbName);
		initDatasource();
	}
    
    public void setDbName(String dbName) {
    	this.dbname = dbName;
    }

	@Override
	public boolean isPooled() {
		return true;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return pooledDataSource.getConnection();
	}

	@Override
	public void start() {
		if(pooledDataSource == null) {
			initDatasource();
		}
	}

	@Override
	public void restart() {
		
	}

	@Override
	public void destroy() {
		try {
			pooledDataSource.close();
			DataSources.destroy(pooledDataSource);
		} catch (SQLException e) {
		}
		pooledDataSource = null;
	}
	
	public void initDatasource() {
		pooledDataSource = new ComboPooledDataSource();
		String driverClass = DBConf.getDbConf("driver");
		try {
			Class.forName(driverClass);
			pooledDataSource.setDriverClass(driverClass);
		} catch (ClassNotFoundException e2) {
			logger.error(e2.getMessage()+" could not load, using default driver");
		} catch (PropertyVetoException e) {
		}
		pooledDataSource.setUser(DBConf.getDbConfWithName(dbname, "username"));
		pooledDataSource.setPassword(DBConf.getDbConfWithName(dbname, "password"));
		pooledDataSource.setJdbcUrl(DBConf.getDbConfWithName(dbname, "serverURL"));
		pooledDataSource.setPreferredTestQuery(DBConf.getDbConfWithName(dbname, "testSQL", DbConnectionManager.getTestSQL(driverClass)));
		
		pooledDataSource.setAcquireIncrement(DBConf.getDbPoolConf("acquireIncrement", 1));
		pooledDataSource.setAcquireRetryAttempts(DBConf.getDbPoolConf("acquireRetryAttempts", 3));
		pooledDataSource.setAcquireRetryDelay(DBConf.getDbPoolConf("acquireRetryDelay", 3));
		pooledDataSource.setInitialPoolSize(DBConf.getDbPoolConf("initialPoolSize", 3));
		pooledDataSource.setMinPoolSize(DBConf.getDbPoolConf("minPoolSize", 3));
		pooledDataSource.setMaxPoolSize(DBConf.getDbPoolConf("maxPoolSize", 15));
		pooledDataSource.setMaxStatements(DBConf.getDbPoolConf("maxStatements", 0));
		pooledDataSource.setMaxStatementsPerConnection(DBConf.getDbPoolConf("maxStatementsPerConnection", 0));
		pooledDataSource.setMaxIdleTime(DBConf.getDbPoolConf("maxIdleTime", 3600));
		pooledDataSource.setIdleConnectionTestPeriod(DBConf.getDbPoolConf("idleConnectionTestPeriod", 60));
		pooledDataSource.setTestConnectionOnCheckout(DBConf.getDbPoolConf("testConnectionOnCheckout", false));
		pooledDataSource.setTestConnectionOnCheckin(DBConf.getDbPoolConf("testConnectionOnCheckin", false));
		pooledDataSource.setCheckoutTimeout(DBConf.getDbPoolConf("checkoutTimeout", 5));
		pooledDataSource.setUnreturnedConnectionTimeout(DBConf.getDbPoolConf("unreturnedConnectionTimeout", pooledDataSource.getMaxIdleTime()+10));
		pooledDataSource.setDebugUnreturnedConnectionStackTraces(DBConf.getDbPoolConf("debugUnreturnedConnectionStackTraces", false));
		pooledDataSource.setNumHelperThreads(DBConf.getDbPoolConf("numHelperThreads", 3));
		
		pooledDataSource.setConnectionCustomizerClassName(C3p0ConnectionCustomizer.class.getName());
    }
	
	@Override
	public String toString() {
		if(pooledDataSource == null) {
			return super.toString();
		}
		try {
			return "TotalConnectionCount-->"+pooledDataSource.getNumConnectionsAllUsers() + ",AvailableConnectionCount-->"
			+ pooledDataSource.getNumIdleConnectionsAllUsers() + ",ActiveConnectionCount-->" + pooledDataSource.getNumBusyConnectionsAllUsers();
		} catch (SQLException e) {
			return super.toString();
		}
	}
}
