/**
 * 
 */
package com.mdcl.gmonitor.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PropertyResourceBundle;

/**
 * @author irin
 *
 */
public class GetProps {

	private static GetProps _instance;
	private static String configFilePath = "items";
	@SuppressWarnings("rawtypes")
	private List<HashMap> clusters_list;
	

	@SuppressWarnings("rawtypes")
	public List<HashMap> getClusters_list() {
		return clusters_list;
	}

	@SuppressWarnings("rawtypes")
	public void setClusters_list(List<HashMap> clusters_list) {
		this.clusters_list = clusters_list;
	}


	public static GetProps getInstance()
    {
        if(_instance == null)
            _instance = new GetProps();
        return _instance;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private GetProps(){
        PropertyResourceBundle configBundle = (PropertyResourceBundle)PropertyResourceBundle.getBundle(configFilePath);
        
        String[] cluster_Name_arr = configBundle.getString("Cluster_Name").split(",");
        List<HashMap> tmpList = new ArrayList<HashMap>();
        if(cluster_Name_arr != null){
        	for(int i = 0;i < cluster_Name_arr.length;i++){
        		String prefix = cluster_Name_arr[i];
        		HashMap tmpMap = new HashMap();
        		String scheduler = configBundle.getString(prefix+"_Scheduler");
        		String cluster_locators = configBundle.getString(prefix+"_Cluster_Locators");
                String summary_Items = configBundle.getString(prefix+"_Summary_Items");
                String locator_Items = configBundle.getString(prefix+"_Locator_Items");
                String cacheServer_Items = configBundle.getString(prefix+"_CacheServer_Items");
               
        		tmpMap.put("cluster_id", prefix);
        		tmpMap.put("scheduler", scheduler);
        		tmpMap.put("cluster_locators", cluster_locators);
        		tmpMap.put("summary_Items", summary_Items);
        		tmpMap.put("locator_Items", locator_Items);
        		tmpMap.put("cacheServer_Items", cacheServer_Items);
        		
        		tmpList.add(tmpMap);
        	}
        }
        clusters_list = tmpList;
    }
    
    public static void main(String args[])
    {
        System.out.println("T=="+GetProps.getInstance().getClusters_list());
    }
}