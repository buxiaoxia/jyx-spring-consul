package com.sc.jyx.consul.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @ClassName: ConfSharedValue 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author xw
 * @date 2016年5月29日 上午11:57:49 
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.FIELD)
public @interface ConfSharedValue {

	public String value();
	
}
