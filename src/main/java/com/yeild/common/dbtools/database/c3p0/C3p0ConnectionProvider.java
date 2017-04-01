package com.yeild.common.dbtools.database.c3p0;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.yeild.common.dbtools.DBApp;
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
		String driverClass = DBApp.getDbConf("driver");
		try {
			Class.forName(driverClass);
			pooledDataSource.setDriverClass(driverClass);
		} catch (ClassNotFoundException e2) {
			logger.error(e2.getMessage()+" could not load, using default driver");
		} catch (PropertyVetoException e) {
		}
		pooledDataSource.setUser(DBApp.getDbConfWithName(dbname, "username"));
		pooledDataSource.setPassword(DBApp.getDbConfWithName(dbname, "password"));
		pooledDataSource.setJdbcUrl(DBApp.getDbConfWithName(dbname, "serverURL"));
		pooledDataSource.setPreferredTestQuery(DBApp.getDbConfWithName(dbname, "testSQL", DbConnectionManager.getTestSQL(driverClass)));
		
		pooledDataSource.setAcquireIncrement(DBApp.getDbPoolConf("acquireIncrement", 1));
		pooledDataSource.setAcquireRetryAttempts(DBApp.getDbPoolConf("acquireRetryAttempts", 3));
		pooledDataSource.setAcquireRetryDelay(DBApp.getDbPoolConf("acquireRetryDelay", 3));
		pooledDataSource.setInitialPoolSize(DBApp.getDbPoolConf("initialPoolSize", 3));
		pooledDataSource.setMinPoolSize(DBApp.getDbPoolConf("minPoolSize", 3));
		pooledDataSource.setMaxPoolSize(DBApp.getDbPoolConf("maxPoolSize", 15));
		pooledDataSource.setMaxStatements(DBApp.getDbPoolConf("maxStatements", 0));
		pooledDataSource.setMaxStatementsPerConnection(DBApp.getDbPoolConf("maxStatementsPerConnection", 0));
		pooledDataSource.setMaxIdleTime(DBApp.getDbPoolConf("maxIdleTime", 3600));
		pooledDataSource.setIdleConnectionTestPeriod(DBApp.getDbPoolConf("idleConnectionTestPeriod", 60));
		pooledDataSource.setTestConnectionOnCheckout(DBApp.getDbPoolConf("testConnectionOnCheckout", false));
		pooledDataSource.setTestConnectionOnCheckin(DBApp.getDbPoolConf("testConnectionOnCheckin", false));
		pooledDataSource.setCheckoutTimeout(DBApp.getDbPoolConf("checkoutTimeout", 5));
		pooledDataSource.setUnreturnedConnectionTimeout(DBApp.getDbPoolConf("unreturnedConnectionTimeout", pooledDataSource.getMaxIdleTime()+10));
		pooledDataSource.setDebugUnreturnedConnectionStackTraces(DBApp.getDbPoolConf("debugUnreturnedConnectionStackTraces", false));
		pooledDataSource.setNumHelperThreads(DBApp.getDbPoolConf("numHelperThreads", 3));
		
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
