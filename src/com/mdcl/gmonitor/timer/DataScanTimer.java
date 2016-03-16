/**
 * 
 */
package com.mdcl.gmonitor.timer;

import java.util.Map;

import com.mdcl.gmonitor.utils.GetProps;

/**
 * @author irin
 *
 */
public class DataScanTimer {
	public static void main(String[] args){
		String[] clusters_list = GetProps.getInstance().getCluster_Name_arr();
		Map<String,String> item_map = GetProps.getInstance().getItems_map();
		if(clusters_list != null && clusters_list.length>0){
			for(String cluster_info_map:clusters_list){
				QuartzManager.addJob(cluster_info_map,
						"com.mdcl.gmonitor.datascan.JMXDataCollector",item_map,"com.mdcl.gmonitor.output.impl.MysqlOutWriter");  
			}
		}
	}
}
