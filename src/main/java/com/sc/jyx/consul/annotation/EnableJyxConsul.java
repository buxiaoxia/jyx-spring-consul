package com.sc.jyx.consul.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.sc.jyx.consul.JyxConsulConfiguration;
/**
 * 
 * @ClassName: EnableJyxConsul 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author xw
 * @date 2016年5月29日 上午11:57:27 
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(JyxConsulConfiguration.class)
@Documented
public @interface EnableJyxConsul {

}
