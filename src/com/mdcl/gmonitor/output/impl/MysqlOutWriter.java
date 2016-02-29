/**
 * 
 */
package com.mdcl.gmonitor.output.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mdcl.gmonitor.output.ResultTransformerOutputWriter;
import com.mdcl.gmonitor.utils.DBManager;

/**
 * @author irin
 *
 */
public class MysqlOutWriter implements ResultTransformerOutputWriter{
	private static final Log log = LogFactory.getLog(MysqlOutWriter.class);

	@Override
	public void doWrite(List<Map<String,String>> results) throws Exception {
		if(results != null && results.size()>0){
			Connection conn = DBManager.getConn();
			conn.setAutoCommit(false);     
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = df.format(new Date());
			
			try{
				String sql = "INSERT INTO DETAIL(ID,ITEM_NAME,ITEM_VALUE,ITEM_TYPE,SCAN_TIME,CLUSTER_ID) VALUES (?,?,?,?,?,?)";
				PreparedStatement prst = conn.prepareStatement(sql);
				
				for(Map<String,String> dataMap : results){
					for (Map.Entry<String, String> entry : dataMap.entrySet()) {
						String item_name = entry.getKey();
						
						if(item_name.equals("type") || item_name.equals("cluster_id")){
							continue;
						}
						
						String item_value = entry.getValue();
						String uuid = UUID.randomUUID().toString();
						String item_type = dataMap.get("type");
						String cluster_id = dataMap.get("cluster_id");
						
						prst.setString(1, uuid);
						prst.setString(2, item_name);
						prst.setString(3, item_value);
						prst.setString(4, item_type);
						prst.setString(5, currentTime);
						prst.setString(6, cluster_id);
						
						prst.addBatch();
					}
				}
				
				prst.executeBatch();
				conn.commit(); 
				log.info(currentTime+" 数据保存成功!");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DBManager.closeConn(conn);
			}
		}
	}
}
