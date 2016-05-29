/**  
 * @Title: ConfigMap.java 
 */ 

package com.sc.jyx.consul.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * @ClassName: ConfigMap 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author xw
 * @date 2016年5月11日 下午10:28:21 
 *  
 */
public class ConsulKV {
	
	public static Map<String, Map<String, List<Object>>> configMap = 
			new HashMap<String, Map<String, List<Object>>>();

	public static Map<String, Map<String, List<Object>>> serviceMap = 
			new HashMap<String,Map<String, List<Object>>>();
	
}
