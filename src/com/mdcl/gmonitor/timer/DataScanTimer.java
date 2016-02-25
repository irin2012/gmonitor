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
		System.out.println("【系统启动】开始(每1分钟采集一次数据)..."); 
		List<HashMap> clusters_list = GetProps.getInstance().getClusters_list();
		System.out.println(clusters_list);
		if(clusters_list != null && !clusters_list.isEmpty()){
			for(int i = 0; i < clusters_list.size(); i++){
				HashMap cluster_info_map = clusters_list.get(i);
				QuartzManager.addJob(String.valueOf(cluster_info_map.get("cluster_id")), "com.mdcl.gmonitor.datascan.ScanJMXInfo",cluster_info_map);  
			}
		}
	}
}
