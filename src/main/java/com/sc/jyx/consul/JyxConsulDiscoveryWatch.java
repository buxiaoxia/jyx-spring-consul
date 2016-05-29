package com.sc.jyx.consul;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import com.sc.jyx.consul.util.ConsulKV;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @ClassName: JyxConsulDiscoveryWatch 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author xw
 * @date 2016年5月29日 上午11:56:37 
 *
 */
@Slf4j
public class JyxConsulDiscoveryWatch {

	private LoadBalancerClient loadBalancer;
	private AtomicBoolean running = new AtomicBoolean(false);

	public JyxConsulDiscoveryWatch(LoadBalancerClient loadBalancer) {
		super();
		this.loadBalancer = loadBalancer;
	}

	@PostConstruct
	public void start() {
		this.running.compareAndSet(false, true);
	}

	@Scheduled(fixedDelayString = "${spring.cloud.consul.discovery.catalogServicesWatchDelay:30000}")
	public void watchServices(){
		log.debug("consul services 监控");
		final  Map<String, Map<String, List<Object>>> map = ConsulKV.serviceMap;
		if(map.size() > 0){
			Set<String> set = map.keySet();
			for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
				String serviceName = iterator.next();
				Map<String, List<Object>> map1 = map.get(serviceName);
				if(map1 != null){
					Set<String> set1 = map1.keySet();
					for (Iterator<String> iterator1 = set1.iterator(); iterator1.hasNext();) {
						String fieldName = iterator1.next();
						List<Object> listBean = map1.get(fieldName);
						String serviceHost = loadBalancer.choose(serviceName).getUri().toString();
						if(StringUtils.hasLength(serviceHost)){
							//更新内存参数
							log.debug("service 【" + serviceName + "】更新为：" + serviceHost);
							for (Object bean : listBean) {
								Class<?> cls = bean.getClass();
								try{
									Field f=cls.getDeclaredField(fieldName); 
									f.setAccessible(true); 
									f.set(bean, serviceHost);
								}catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}

			}
		}

	}

}
