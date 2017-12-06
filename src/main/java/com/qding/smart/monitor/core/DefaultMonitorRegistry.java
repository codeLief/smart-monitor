package com.qding.smart.monitor.core;

import org.aopalliance.intercept.MethodInterceptor;

import com.qding.smart.monitor.aspect.MethodMonitorInception;

public class DefaultMonitorRegistry extends AbstractMonitorRegistry {

	@Override
	public Class<? extends MonitorResolve> getOwnerClass() {
		
		return DefaultMonitorResolve.class;
	}

	@Override
	public Class<? extends MethodInterceptor> getInceptionClass() {
		
		return MethodMonitorInception.class;
	}
}
