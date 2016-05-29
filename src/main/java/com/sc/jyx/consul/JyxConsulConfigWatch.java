package com.sc.jyx.consul;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.google.common.io.BaseEncoding;
import com.sc.jyx.consul.util.ConsulKV;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @ClassName: JyxConsulConfigWatch 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author xw
 * @date 2016年5月29日 上午11:56:26 
 *
 */
@Slf4j
public class JyxConsulConfigWatch {

	private final ConsulClient consul;
	private AtomicBoolean running = new AtomicBoolean(false);
	private HashMap<String, Long> consulIndexes = new HashMap<String, Long>();

	public JyxConsulConfigWatch(ConsulClient consul) {
		super();
		this.consul = consul;
	}

	@PostConstruct
	public void start() {
		this.running.compareAndSet(false, true);
	}

	@Scheduled(fixedDelayString = "${spring.cloud.consul.config.watch.delay:30000}")
	public void watchConfigKeyValues(){
		log.debug("consul KV 监控");
		final  Map<String, Map<String, List<Object>>> map = ConsulKV.configMap;
		if(map.size() > 0){
			Set<String> set = map.keySet();
			for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
				String key = iterator.next();
				Map<String, List<Object>> map1 = map.get(key);
				if(map1 != null){
					Set<String> set1 = map1.keySet();
					for (Iterator<String> iterator1 = set1.iterator(); iterator1.hasNext();) {
						String fieldName = iterator1.next();
						List<Object> listBean = map1.get(fieldName);
						Long currentIndex = this.consulIndexes.get(key);
						if (currentIndex == null) {
							currentIndex = -1L;
						}
						Response<GetValue> response = this.consul.getKVValue(key, new QueryParams(2, currentIndex));
						if(response != null){
							Long newIndex = response.getConsulIndex();
							GetValue getValue = response.getValue();
							if (newIndex != null && !newIndex.equals(currentIndex) && getValue != null) {
								//更新内存参数
								//System.out.println("更新KeyValues内存");
								String value = getValueDecode(getValue.getValue());
								log.debug("key 【" + key + "】更新为：" + value);
								for (Object bean : listBean) {
									Class<?> cls = bean.getClass();
									try{
										Field f=cls.getDeclaredField(fieldName); 
										f.setAccessible(true); 
										f.set(bean, value);
									}catch (Exception e) {
										e.printStackTrace();
									}
								}
								this.consulIndexes.put(key, newIndex);
							}
						}
					}
				}
				
			}
		}

	}
	
	private String getValueDecode(String value){
		if(StringUtils.isEmpty(value)){
			return null;
		}else {
			return new String(BaseEncoding.base64().decode(value));
		}
	}
	
}
