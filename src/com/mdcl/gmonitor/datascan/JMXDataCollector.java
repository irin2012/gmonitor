/**
 * 
 */
package com.mdcl.gmonitor.datascan;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mdcl.gmonitor.output.ResultTransformerOutputWriter;
import com.mdcl.gmonitor.utils.GetProps;

/**
 * @author irin
 *
 */
public class JMXDataCollector implements Job{
	private static final Log log = LogFactory.getLog(JMXDataCollector.class);
	private  String cluster_id = "";
	private  String cluster_locators = "";
	private  String output = "";
	
	/**
	 * @param args
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		cluster_id = String.valueOf(context.getJobDetail().getJobDataMap().get("cluster_id"));
		cluster_locators = String.valueOf(context.getJobDetail().getJobDataMap().get("cluster_locators"));
		output = String.valueOf(context.getJobDetail().getJobDataMap().get("output"));
		
		
		List<List<Map<String, String>>> rsList = new ArrayList<List<Map<String, String>>>();
		JMXConnector conn = null;
		conn = getJMXConnector(cluster_id);
		try {
			if (conn != null) {
				MBeanServerConnection mbsc = null;
				try {
					mbsc = conn.getMBeanServerConnection();

					for (String item_define : GmonitorConstants.ITEMS_ARR) {
						List<Map<String, String>> tmpList = getJMXData(mbsc, item_define,"Dept3_Person_Mobile");
						rsList.add(tmpList);
					}
					if (rsList != null && !rsList.isEmpty()) {
						for (List<Map<String, String>> tmpList : rsList) {
							System.out.println(tmpList);
						}
					}
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		    		log.info("开始收集集群：【 "+cluster_id+" 】监控数据："+sdf.format(new Date()));
//		            log.info(rsList);		
		            ResultTransformerOutputWriter outwriter = (ResultTransformerOutputWriter) Class.forName(output).newInstance();
		            outwriter.doWrite(rsList);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				log.error("Exception: 链接不上集群【 Dept3_Person_Mobile 】的Locator");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 采集JMX数据
	 * 
	 * @param mbsc	jmx链接上下文
	 * @param item_define	监控指标配置文件标识，后续将指标存储在数据库需要修改
	 * @param cluster_id	集群标识，后续将指标存储在数据库需要修改
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> getJMXData(MBeanServerConnection mbsc, String item_define,String cluster_id)
			throws Exception {
		List<Map<String, String>> rsList = new ArrayList<Map<String, String>>();
		Map<String,String> item_map = GetProps.getInstance().getItems_map();
		Class<?> cls = Class.forName("com.mdcl.gmonitor.datascan.GmonitorConstants");
		Object obj = cls.newInstance();
		Field field = cls.getField(item_define);
		String objName_Str = field.get(obj).toString();
		ObjectName objName = new ObjectName(objName_Str);
		
		String items_prefix = cluster_id + "_" + item_define;

		String[] itemsArr = null;
		Set<ObjectName> mbeans = mbsc.queryNames(objName, null);
		for (ObjectName mbeanName : mbeans) {
			if (item_define.equals("OBJECT_NAME_MEMBER")) {
				boolean isLocator = Boolean.parseBoolean(mbsc.getAttribute(mbeanName, "Locator").toString());
				if (isLocator) {
					 itemsArr = item_map.get(items_prefix+"_LOCATOR").split(",");
				} else {
					itemsArr = item_map.get(items_prefix+"_CACHE").split(",");
				}
			} else {
				itemsArr = item_map.get(items_prefix).split(",");
			}
			if (itemsArr != null) {
				Map<String, String> tmpMap = new HashMap<String, String>();
				for (String item : itemsArr) {
					if(mbsc.getAttribute(mbeanName, item) instanceof   String[]){
						String [] tmpArr = (String[]) mbsc.getAttribute(mbeanName, item);
						tmpMap.put(item,StringUtils.join(tmpArr, ","));
					}else{
						tmpMap.put(item, mbsc.getAttribute(mbeanName, item).toString());
					}
				}
				tmpMap.put("ObjName", mbeanName.toString());
				tmpMap.put("type", mbeanName.toString());
				tmpMap.put("cluster_id",cluster_id);
				rsList.add(tmpMap);
			}
		}

		return rsList;
	}

	/**
	 * 获取cluster链接obj，每个集群会有2个以上的locator； 连接时如果遇到超时或locator宕机时需要遍历链接其他locator
	 * 
	 * @param cluster_id
	 *            集群ID，用于扩展开发动态多集群监控
	 * @return
	 */
	public JMXConnector getJMXConnector(String cluster_id) {
		String cluster_locator_str = cluster_locators;
		JMXServiceURL serviceURL = null;
		JMXConnector conn = null;

		String jmxURL = "";
		String[] locatorsArr = cluster_locator_str.split(",");
		Map<String, Object> env = new HashMap<String, Object>();
		// String[] credentials = new String[] { "role" , "pwd" };
		// env.put("jmx.remote.credentials", credentials);
		try {
			if (locatorsArr != null) {
				for (String locator : locatorsArr) {
					jmxURL = "service:jmx:rmi:///jndi/rmi://" + locator + "/jmxrmi";
					try {
						serviceURL = new JMXServiceURL(jmxURL);
						conn = JMXConnectorFactory.connect(serviceURL, env);
						break;
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		} catch (Exception e) {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				conn = null;
			}
			e.printStackTrace();
		}
		return conn;
	}
}
