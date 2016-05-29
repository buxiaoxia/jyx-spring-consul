package com.sc.jyx.consul;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.sc.jyx.consul.annotation.ConfSharedValue;
import com.sc.jyx.consul.annotation.JyxConsulConfig;
import com.sc.jyx.consul.annotation.JyxConsulDiscovery;
import com.sc.jyx.consul.annotation.ServiceValue;
import com.sc.jyx.consul.util.ConsulKV;

/**
 * 
 * @ClassName: JyxConsulProcessor 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author xw
 * @date 2016年5月29日 上午11:56:41 
 *
 */
public class JyxConsulProcessor implements BeanPostProcessor{


	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> classTmp = bean.getClass();
		boolean isJyxConsulConfig = classTmp.isAnnotationPresent(JyxConsulConfig.class);
		boolean isJyxConsulDiscovery = classTmp.isAnnotationPresent(JyxConsulDiscovery.class);
		if(isJyxConsulConfig){
			String confPath = "";
			String key = "";
			JyxConsulConfig confSharedPath = classTmp.getAnnotation(JyxConsulConfig.class);
			//System.out.println(confSharedPath.value());
			confPath = confSharedPath.value();
			if(!confPath.endsWith("/")){
				confPath = confPath + "/";
			}
			Field[] fields = classTmp.getDeclaredFields();
			for (Field field : fields) {
				boolean fieldFlag = field.isAnnotationPresent(ConfSharedValue.class);
				if(fieldFlag){
					ConfSharedValue confSharedValue = field.getAnnotation(ConfSharedValue.class);
					//System.out.println(confSharedValue.value());
					//						confPath += "/" + confSharedValue.value();
					key = confPath+confSharedValue.value();
					System.out.println("===========配置共享key=============："+key);
					Map<String, List<Object>> map = ConsulKV.configMap.get(key);
					if(map == null){
						map = new HashMap<String, List<Object>>();
						ConsulKV.configMap.put(key, map);
						map.put(field.getName(), new ArrayList<Object>());
						map.get(field.getName()).add(bean);
					}else{
						List<Object> list = map.get(field.getName());
						if(list == null){
							list = new ArrayList<Object>();
							list.add(bean);
							map.put(field.getName(), list);
						}else{
							list.add(bean);
						}
					}
					
					/*if(list == null){
						list = new ArrayList<Object>();
						ConsulKV.configMap.put(key, list);
					}
					list.add(bean);*/
				}
			}
		}else if(isJyxConsulDiscovery){
			Field[] fields = classTmp.getDeclaredFields();
			String service = "";
			for (Field field : fields) {
				boolean fieldFlag = field.isAnnotationPresent(ServiceValue.class);
				if(fieldFlag){
					ServiceValue serviceValue = field.getAnnotation(ServiceValue.class);
					service = serviceValue.value();
					System.out.println("===========需要注入的服务=============："+service);
					Map<String, List<Object>> map = ConsulKV.serviceMap.get(service);
					if(map == null){
						map = new HashMap<String, List<Object>>();
						ConsulKV.serviceMap.put(service, map);
						map.put(field.getName(), new ArrayList<Object>());
						map.get(field.getName()).add(bean);
					}else{
						List<Object> list = map.get(field.getName());
						if(list == null){
							list = new ArrayList<Object>();
							list.add(bean);
							map.put(field.getName(), list);
						}else{
							list.add(bean);
						}
					}
					/*List<Object> list = ConsulKV.serviceMap.get(service);
					if(list == null){
						list = new ArrayList<Object>();
						ConsulKV.serviceMap.put(service, list);
					}
					list.add(bean);*/
				}
			}
		}
		return bean;
	}

}
