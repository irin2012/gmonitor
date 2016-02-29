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
	@SuppressWarnings("rawtypes")
	public static void main(String[] args){
		List<HashMap> clusters_list = GetProps.getInstance().getClusters_list();
		if(clusters_list != null && !clusters_list.isEmpty()){
			for(HashMap cluster_info_map:clusters_list){
				QuartzManager.addJob(String.valueOf(cluster_info_map.get("cluster_id")),
						"com.mdcl.gmonitor.datascan.ScanJMXInfo",cluster_info_map,"com.mdcl.gmonitor.output.impl.MysqlOutWriter");  
			}
		}
	}
}
