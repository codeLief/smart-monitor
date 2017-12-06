package com.qding.smart.monitor.aspect;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.aop.support.StaticMethodMatcherPointcut;

import com.qding.smart.monitor.domain.MonitorBeanMetaData;

public class MonitorNameMatchMethodPointcut extends StaticMethodMatcherPointcut {

	private List<MonitorBeanMetaData> metaDatas;
	
	public MonitorNameMatchMethodPointcut(List<MonitorBeanMetaData> metaDatas) {
		this.metaDatas = metaDatas;
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		
		for (MonitorBeanMetaData meta : metaDatas) {
			if(meta.getBeanClass().equals(targetClass)){
				meta.getUniqueAllMethods();
				for (Method markerMethod : meta.getUniqueAllMethods()) {
					if(markerMethod.equals(method)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
