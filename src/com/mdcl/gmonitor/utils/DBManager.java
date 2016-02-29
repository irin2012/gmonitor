/**
 * 
 */
package com.mdcl.gmonitor.utils;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
 
import javax.sql.DataSource;
 
/**
 * @author irin
 *
 */
public class DBManager {
	private static final Log log = LogFactory.getLog(DBManager.class);
    private static final String configFile = "dbcp.properties";
 
    private static DataSource dataSource;
 
    static {
        Properties dbProperties = new Properties();
        try {
            dbProperties.load(DBManager.class.getClassLoader()
                    .getResourceAsStream(configFile));
            dataSource = BasicDataSourceFactory.createDataSource(dbProperties);
 
            Connection conn = getConn();
            DatabaseMetaData mdm = conn.getMetaData();
            log.info("Connected to " + mdm.getDatabaseProductName() + " "
                    + mdm.getDatabaseProductVersion());
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            log.error("初始化连接池失败：" + e);
        }
    }
 
    private DBManager() {
    }
 
    /**
     * 获取链接，用完后记得关闭
     * 
     * @see {@link DBManager#closeConn(Connection)}
     * @return
     */
    public static final Connection getConn() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            log.error("获取数据库连接失败：" + e);
        }
        return conn;
    }
 
    /**
     * 关闭连接
     * 
     * @param conn
     *            需要关闭的连接
     */
    public static void closeConn(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            log.error("关闭数据库连接失败：" + e);
        }
    }
}
