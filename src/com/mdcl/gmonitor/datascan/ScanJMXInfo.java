/**
 * 
 */
package com.mdcl.gmonitor.datascan;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * @author irin
 *
 */
public class ScanJMXInfo  implements Job{
	public  String cluster_id = "";
	public  String cluster_locators = "";
	public  String summary_Items = "";
	public  String locator_Items = "";
	public  String cacheServer_Items = "";
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		cluster_id = String.valueOf(context.getJobDetail().getJobDataMap().get("cluster_id"));
		cluster_locators = String.valueOf(context.getJobDetail().getJobDataMap().get("cluster_locators"));
		summary_Items = String.valueOf(context.getJobDetail().getJobDataMap().get("summary_Items"));
		locator_Items = String.valueOf(context.getJobDetail().getJobDataMap().get("locator_Items"));
		cacheServer_Items = String.valueOf(context.getJobDetail().getJobDataMap().get("cacheServer_Items"));
		
		System.out.println("cluster_id==="+cluster_id);
		
		List<Map> rsList = new ArrayList<Map>();
		
		JMXConnector connector = null;
        connector = getJMXServiceURL(cluster_id);
        
        if(connector != null){
            MBeanServerConnection mbsc = null;
    		try {
    			mbsc = connector.getMBeanServerConnection();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
             
            Set MBeanset = null;
    		try {
    			MBeanset = mbsc.queryMBeans(null, null);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            Iterator MBeansetIterator = MBeanset.iterator();
            
            List<String> objList = new ArrayList<String>();
            while (MBeansetIterator.hasNext()) { 
                ObjectInstance objectInstance = (ObjectInstance)MBeansetIterator.next();
                ObjectName objectName = objectInstance.getObjectName();
                String canonicalName = objectName.getCanonicalName();
                if(canonicalName.indexOf("GemFire") != -1)
                	objList.add(canonicalName);
            }
            
            for(int i= 0; i < objList.size(); i++){
            	String objName = objList.get(i);
            	if(objName.indexOf("type=Distributed") != -1){
            		Map<String, String> tmpMap = null;
    				try {
    					tmpMap = getMonitorInfo(mbsc,objName,"summary",cluster_id);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
            		if( tmpMap!=null  && !tmpMap.isEmpty())
            			rsList.add(tmpMap);
            	}else if(objName.indexOf("type=Member") != -1 && objName.indexOf("service=") == -1){
            		Map<String, String> tmpMap = null;
    				try {
    					tmpMap = getMonitorInfo(mbsc,objName,"member",cluster_id);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
            		if( tmpMap!=null  && !tmpMap.isEmpty())
            			rsList.add(tmpMap);
            	}
            }
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    		System.out.println("开始收集集群：【 "+cluster_id+" 】监控数据："+sdf.format(new Date()));
            System.out.println(rsList);		
        	System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
        }else{
        	System.out.println("Exception: 链接不上集群【 "+cluster_id+" 】的Locator");
        }
	}
	
	/**
	 * 获取cluster链接obj，每个集群会有2个以上的locator；
	 * 连接时如果遇到超时或locator宕机时需要遍历链接其他locator
	 * 
	 * @param cluster_id 集群ID，用于扩展开发动态多集群监控
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JMXConnector getJMXServiceURL(String cluster_id){
		String cluster_locator_str = cluster_locators;
		JMXServiceURL serviceURL = null;
		JMXConnector connector = null;
		
		String jmxURL = "";
		String[] locatorsArr = cluster_locator_str.split(",");
		Map map = new HashMap();
        //String[] credentials = new String[] { "role" , "pwd" };
        //map.put("jmx.remote.credentials", credentials);
        
		if(locatorsArr != null){
			for(int i = 0; i < locatorsArr.length; i++){
				jmxURL = "service:jmx:rmi:///jndi/rmi://"+locatorsArr[i]+"/jmxrmi";
				try {
					serviceURL = new JMXServiceURL(jmxURL);
					connector = JMXConnectorFactory.connect(serviceURL, map);
					break;
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		
		return connector;
	}

	/**
	 * 获取集群监控数据入口方法
	 * 
	 * @param mbsc
	 * @param objStr
	 * @param ins_type
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  Map getMonitorInfo(MBeanServerConnection  mbsc,String objStr,String ins_type,String cluster_id) throws Exception{
		Map<String,String> rsMap = new HashMap<String,String>();
		ObjectName runtimeObjName = new ObjectName(objStr);
		
		String monitor_type = "";
		String items = "";
		
		if(ins_type.equals("summary")){ 
			monitor_type = "summary"; 
			items = summary_Items;
		}else{
			boolean ifLocator = (boolean) mbsc.getAttribute(runtimeObjName, "Locator");
			if(ifLocator){
				monitor_type = "locator";
				items = locator_Items;
			}else{
				monitor_type = "cacheserver";
				items = cacheServer_Items;
			}
		}
		
		rsMap = getInstanceInfo(mbsc,objStr,items);
		rsMap.put("type", monitor_type);
		rsMap.put("cluster_id",cluster_id);
		
		return rsMap;
	}
	
	/**
	 * 获得集群监控节点的实际数据
	 * 
	 * @param mbsc
	 * @param objStr
	 * @param items
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public  Map getInstanceInfo(MBeanServerConnection  mbsc,String objStr,String items) throws Exception{
		Map<String,String> rsMap = new HashMap<String,String>();
		ObjectName runtimeObjName = new ObjectName(objStr);
		
		String[] itemsArr = items.split(",");
		if(itemsArr != null){
			for(int i = 0; i < itemsArr.length; i++){
				rsMap.put(itemsArr[i], String.valueOf(mbsc.getAttribute(runtimeObjName, itemsArr[i])));
			}
		}
		
		return rsMap;
	}
}
