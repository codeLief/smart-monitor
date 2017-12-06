package com.qding.smart.monitor.aspect;

import java.lang.reflect.Method;

import org.springframework.core.Ordered;

public interface MethodMonitorAdvice extends Ordered{
	
	void before(Class<?> targetClass, Method method, Object[] args);
	
	void after(Class<?> targetClass, Method method, Object result);
}
