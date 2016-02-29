/**
 * 
 */
package com.mdcl.gmonitor.timer;

import java.util.HashMap;
import java.util.List;

import com.mdcl.gmonitor.utils.GetProps;

/**
 * @author irin
 *
 */
public class DataScanTimer {
	public static void main(String[] args){
		List<HashMap<String,String>> clusters_list = GetProps.getInstance().getClusters_list();
		if(clusters_list != null && !clusters_list.isEmpty()){
			for(HashMap<String,String> cluster_info_map:clusters_list){
				QuartzManager.addJob(String.valueOf(cluster_info_map.get("cluster_id")),
						"com.mdcl.gmonitor.datascan.ScanJMXInfo",cluster_info_map,"com.mdcl.gmonitor.output.impl.MysqlOutWriter");  
			}
		}
	}
}
