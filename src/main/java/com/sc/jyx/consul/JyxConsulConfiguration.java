package com.sc.jyx.consul;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecwid.consul.v1.ConsulClient;

@Configuration
@ConditionalOnProperty(name = "spring.cloud.consul.config.enabled", matchIfMissing = true)
public class JyxConsulConfiguration {

	@Autowired
	private ConsulClient consulClient;

	@Autowired
	private LoadBalancerClient loadBalancer;

	@Bean
	public JyxConsulProcessor jyxConsulProcessor(){
		return new JyxConsulProcessor();
	}
	
	@Bean
	public JyxConsulConfigWatch jyxConsulConfigWatch(){
		return new JyxConsulConfigWatch(consulClient);
	}

	@Bean
	public JyxConsulDiscoveryWatch jyxConsulDiscoveryWatch(){
		return new JyxConsulDiscoveryWatch(loadBalancer);
	}
	
}
