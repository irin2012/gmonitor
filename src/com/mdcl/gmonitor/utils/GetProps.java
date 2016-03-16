/**
 * 
 */
package com.mdcl.gmonitor.utils;

import java.util.HashMap;
import java.util.PropertyResourceBundle;

/**
 * @author irin
 *
 */
public class GetProps {

	private static GetProps _instance;
	private static String configFilePath = "items";
	private HashMap<String,String> items_map;
	String[] cluster_Name_arr;
	

	public String[] getCluster_Name_arr() {
		return cluster_Name_arr;
	}

	public void setCluster_Name_arr(String[] cluster_Name_arr) {
		this.cluster_Name_arr = cluster_Name_arr;
	}

	public HashMap<String, String> getItems_map() {
		return items_map;
	}

	public void setItems_map(HashMap<String, String> items_map) {
		this.items_map = items_map;
	}

	public static GetProps getInstance()
    {
        if(_instance == null)
            _instance = new GetProps();
        return _instance;
    }

	private GetProps(){
        PropertyResourceBundle configBundle = (PropertyResourceBundle)PropertyResourceBundle.getBundle(configFilePath);
        
        cluster_Name_arr = configBundle.getString("Cluster_Name").split(",");
        if(cluster_Name_arr != null){
        	items_map = new HashMap<String,String>();
        	for(String prefix:cluster_Name_arr){
        		items_map.put(prefix+"_OBJECT_NAME_MEMBER_CACHE", configBundle.getString(prefix+"_OBJECT_NAME_MEMBER_CACHE"));
        		items_map.put(prefix+"_OBJECT_NAME_MEMBER_LOCATOR", configBundle.getString(prefix+"_OBJECT_NAME_MEMBER_LOCATOR"));
        		items_map.put(prefix+"_OBJECT_NAME_SYSTEM_DISTRIBUTED", configBundle.getString(prefix+"_OBJECT_NAME_SYSTEM_DISTRIBUTED"));
        		items_map.put(prefix+"_OBJECT_NAME_REGION_DISTRIBUTED", configBundle.getString(prefix+"_OBJECT_NAME_REGION_DISTRIBUTED"));
        		items_map.put(prefix+"_OBJECT_NAME_CACHESERVER", configBundle.getString(prefix+"_OBJECT_NAME_CACHESERVER"));
        		items_map.put(prefix+"_cluster_id", prefix);
        		items_map.put(prefix+"_scheduler", configBundle.getString(prefix+"_Scheduler"));
        		items_map.put(prefix+"_cluster_locators", configBundle.getString(prefix+"_Cluster_Locators"));
        	}
        }
    }
    
    public static void main(String args[])
    {
        System.out.println("T=="+GetProps.getInstance().getItems_map());
    }
}
