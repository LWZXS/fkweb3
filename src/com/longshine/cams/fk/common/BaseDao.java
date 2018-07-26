package com.longshine.cams.fk.common;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.rowset.CachedRowSetImpl;

@SuppressWarnings("restriction")
public class BaseDao {
	// 系统日志对象
	protected Log logger;
	protected Log getLogger(){return logger;}
	// 数据库连接池对象
	protected DataSource datasource;
	// 限制缓存行集对象的最大行数
	protected int max_cached_rows;
	public BaseDao(){
		this.logger = LogFactory.getLog(this.getClass());
	}
	
	/**提交的数据库语句中第一个查询字段必须是count(1)，也只有一个查询量，统计符合条件的记录数
	 * @param v_sql 待执行的统计SQL
	 * @return 返回记录条数，不存在则返回0，执行异常时返回-1
	 */
	protected int countSQLRecords(String v_sql){
		Connection conn = null;
		Statement st = null;
		ResultSet result = null;
		int rec_num = 0;
		try{
			conn = this.datasource.getConnection();
			if(conn != null){
				st = conn.createStatement();
				result = st.executeQuery(v_sql);
				while(result.next()){
					rec_num = result.getInt(1);
					break;
				}
			}
		}catch(Exception e){
			logger.warn("Select Exception(" + v_sql + "),Exception:" + e);
			rec_num = 0;
		}finally{
			this.closeConnectionResource(result,st,conn);
			result = null;
			st = null;
			conn = null;
		}
		return rec_num;
	}
	
	/**根据提供的SQL语句，将从数据库中的执行结果作为缓存行集返回给调用方
	 * @param v_sql 待执行的SQL语句
	 * @return 返回缓存的行集给调用方
	 * @throws SQLException 数据库语句执行异常
	 */
	protected CachedRowSet querySQL(String v_sql) throws SQLException{
		CachedRowSet crs = null;
		Connection conn = null;
		Statement st = null;
		ResultSet result = null;
		try{
			conn = this.datasource.getConnection();
			if(conn != null){
				st = conn.createStatement();
				String sql = v_sql.replace("${MAX_CACHE_ROWS}", String.valueOf(this.max_cached_rows));
				result = st.executeQuery(sql);
				crs = new CachedRowSetImpl();
				crs.setMaxRows(this.max_cached_rows);
				crs.populate(result);
			}
		}catch(SQLException e1){
			throw e1;
		}
		catch(Exception e){
			logger.warn("Select Exception(" + v_sql + "),Exception:" + e);
		}finally{
			this.closeConnectionResource(result,st,conn);
			result = null;
			st = null;
			conn = null;
		}
		return crs;
	}
	
	/**根据提供的SQL语句，将从数据库中的执行结果作为缓存行集返回给调用方
	 * @param v_sql 待执行的SQL语句
	 * @return 返回执行结果影响的行数
	 * @throws SQLException 数据库语句执行异常
	 */
	protected int queryUpdateSQL(String v_sql) throws SQLException{
		Connection conn = null;
		Statement st = null;
		int i = 0;;
		try{
			conn = this.datasource.getConnection();
			if(conn != null){
				st = conn.createStatement();
				i = st.executeUpdate(v_sql);
			}
		}catch(SQLException e1){
			throw e1;
		}
		catch(Exception e){
			logger.warn("Select Exception(" + v_sql + "),Exception:" + e);
		}finally{
			this.closeConnectionResource(st,conn);
			st = null;
			conn = null;
		}
		return i;
	}
	
	/**调用时，调用方将ResultSet,Statement,Connection对象作为可变个数参数传入，本方法逐个对象调用close方法进行关闭操作
	 * 执行顺序以传入的顺序执行close操作
	 * @param v_resources 可变个数的sql资源对象
	 */
	protected void closeConnectionResource(Object ...v_resources){
		for(int i = 0; i < v_resources.length; i++){
			try{
				if(v_resources[i] != null){
					Class<?> res_class = v_resources[i].getClass();
					Method method = res_class.getMethod("close");
					if(method != null)
						method.invoke(v_resources[i]);
				}
			}catch(Exception e){
				logger.warn("closeConnectionResource function failed. Exception:" + e);
			}
		}
	}
}
