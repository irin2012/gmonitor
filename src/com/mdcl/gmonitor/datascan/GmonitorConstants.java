/**
 * 
 */
package com.mdcl.gmonitor.datascan;

/**
 * @author irin
 *
 */
public class GmonitorConstants {
	  // CONSTANTS FOR MBEAN OBJNAME
	  public static final String OBJECT_DOMAIN_NAME_GEMFIRE = "GemFire";
	  public static final String OBJECT_NAME_MEMBER = OBJECT_DOMAIN_NAME_GEMFIRE + ":type=Member,member=*";
//	  public static final String OBJECT_NAME_MEMBER_MANAGER = OBJECT_DOMAIN_NAME_GEMFIRE + ":service=Manager,type=Member,*";
	  public static final String OBJECT_NAME_SYSTEM_DISTRIBUTED = OBJECT_DOMAIN_NAME_GEMFIRE + ":service=System,type=Distributed";
	  public static final String OBJECT_NAME_REGION_DISTRIBUTED = OBJECT_DOMAIN_NAME_GEMFIRE + ":service=Region,type=Distributed,*";
	  public static final String OBJECT_NAME_CACHESERVER = OBJECT_DOMAIN_NAME_GEMFIRE + ":service=CacheServer,type=Member,*";
//	  public static final String OBJECT_NAME_DISKSTORE = OBJECT_DOMAIN_NAME_GEMFIRE + ":service=DiskStore,type=Member,*";
//	  public static final String OBJECT_NAME_LOCATOR = OBJECT_DOMAIN_NAME_GEMFIRE + ":service=Locator,type=Member,*";
//	  public static final String OBJECT_NAME_REGION = OBJECT_DOMAIN_NAME_GEMFIRE + ":service=Region,type=Member,*";
	  
	  public static final String[] ITEMS_ARR = {
			  "OBJECT_NAME_MEMBER",
			  "OBJECT_NAME_SYSTEM_DISTRIBUTED",
			  "OBJECT_NAME_REGION_DISTRIBUTED",
			  "OBJECT_NAME_CACHESERVER"};
}
