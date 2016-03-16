/**
 * 
 */
package com.mdcl.gmonitor.timer;

import java.util.HashMap;
import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author irin
 *
 */
public class QuartzManager {
	private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();
	private static String JOB_GROUP_NAME = "GMONIOTR_JOBGROUP_NAME";
	private static String TRIGGER_GROUP_NAME = "GMONITOR_TRIGGERGROUP_NAME";


	/**
	 * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
	 * 
	 * @param jobName
	 * 			任务名
	 * 
	 * @param jobClass
	 *  		任务
	 *  
	 * @param item_map
	 * 			监控集群参数信息，动态传递给任务
	 */
	
	public static void addJob(String jobName, String jobClass, Map<String, String> item_map,String output) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			JobDetail jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, Class.forName(jobClass));// 任务名，任务组，任务执行类
			jobDetail.getJobDataMap().put("cluster_id", item_map.get("cluster_id"));
			jobDetail.getJobDataMap().put("scheduler", item_map.get("scheduler"));
			jobDetail.getJobDataMap().put("cluster_locators", item_map.get("cluster_locators"));
			jobDetail.getJobDataMap().put("output", output);
			
			// 触发器
			CronTrigger trigger = new CronTrigger(jobName, TRIGGER_GROUP_NAME);// 触发器名,触发器组
			trigger.setCronExpression(String.valueOf(item_map.get("scheduler")));// 触发器时间设定
			sched.scheduleJob(jobDetail, trigger);
			// 启动
			if (!sched.isShutdown()){
				sched.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	/**
	 * 添加一个定时任务
	 * 
	 * @param jobName
	 * 			任务名
	 * 
	 * @param jobGroupName
	 * 			任务组名
	 * 
	 * @param triggerName
	 * 			触发器名
	 * 
	 * @param triggerGroupName
	 * 			触发器组名
	 * 
	 * @param jobClass
	 * 			任务
	 * 
	 * @param cluster_info
	 * 			监控集群参数信息，动态传递给任务
	 */
	public static void addJob(String jobName, String jobGroupName,
			String triggerName, String triggerGroupName, String jobClass, HashMap<String,String> cluster_info,String output){
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			JobDetail jobDetail = new JobDetail(jobName, jobGroupName, Class.forName(jobClass));// 任务名，任务组，任务执行类
			jobDetail.getJobDataMap().put("cluster_id", cluster_info.get("cluster_id"));
			jobDetail.getJobDataMap().put("scheduler", cluster_info.get("scheduler"));
			jobDetail.getJobDataMap().put("cluster_locators", cluster_info.get("cluster_locators"));
			jobDetail.getJobDataMap().put("output", output);
			
			// 触发器
			CronTrigger trigger = new CronTrigger(triggerName, triggerGroupName);// 触发器名,触发器组
			trigger.setCronExpression(String.valueOf(cluster_info.get("scheduler")));// 触发器时间设定
			sched.scheduleJob(jobDetail, trigger);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
	 * 
	 * @param jobName
	 * 			任务名
	 * 
	 * @param cluster_info
	 * 			监控集群参数信息，动态传递给任务
	 */
	@SuppressWarnings("rawtypes")
	public static void modifyJobTime(String jobName, HashMap<String,String> cluster_info,String outpout) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			CronTrigger trigger = (CronTrigger) sched.getTrigger(jobName, TRIGGER_GROUP_NAME);
			if(trigger == null) {
				return;
			}
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(String.valueOf(cluster_info.get("scheduler")))) {
				JobDetail jobDetail = sched.getJobDetail(jobName, JOB_GROUP_NAME);
				Class objJobClass = jobDetail.getJobClass();
				String jobClass = objJobClass.getName();
				removeJob(jobName);

				addJob(jobName, jobClass, cluster_info,outpout);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 修改一个任务的触发时间
	 * 
	 * @param triggerName
	 * 			触发器名
	 * 
	 * @param triggerGroupName
	 * 			触发器组
	 * 
	 * @param cluster_info
	 * 			监控集群参数信息，动态传递给任务
	 */
	@SuppressWarnings("rawtypes")
	public static void modifyJobTime(String triggerName,
			String triggerGroupName, HashMap cluster_info) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerName, triggerGroupName);
			if(trigger == null) {
				return;
			}
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(String.valueOf(cluster_info.get("scheduler")))) {
				CronTrigger ct = (CronTrigger) trigger;
				// 修改时间
				ct.setCronExpression(String.valueOf(cluster_info.get("scheduler")));
				// 重启触发器
				sched.resumeTrigger(triggerName, triggerGroupName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
	 *
	 * @param jobName
	 * 			任务名
	 */
	public static void removeJob(String jobName) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.pauseTrigger(jobName, TRIGGER_GROUP_NAME);// 停止触发器
			sched.unscheduleJob(jobName, TRIGGER_GROUP_NAME);// 移除触发器
			sched.deleteJob(jobName, JOB_GROUP_NAME);// 删除任务
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 移除一个任务
	 *
	 * @param jobName
	 * 			任务名
	 * 
	 * @param jobGroupName
	 * 			任务组名
	 * 
	 * @param triggerName
	 * 			触发器名
	 * 
	 * @param triggerGroupName
	 * 			触发器组名
	 */
	public static void removeJob(String jobName, String jobGroupName,
			String triggerName, String triggerGroupName) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.pauseTrigger(triggerName, triggerGroupName);// 停止触发器
			sched.unscheduleJob(triggerName, triggerGroupName);// 移除触发器
			sched.deleteJob(jobName, jobGroupName);// 删除任务
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 启动所有定时任务
	 * 
	 */
	public static void startJobs() {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 关闭所有定时任务
	 * 
	 */
	public static void shutdownJobs() {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			if(!sched.isShutdown()) {
				sched.shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
