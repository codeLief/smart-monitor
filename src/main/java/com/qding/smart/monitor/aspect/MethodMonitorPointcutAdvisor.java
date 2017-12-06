package com.qding.smart.monitor.aspect;

import static org.springframework.util.Assert.notNull;

import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.beans.factory.InitializingBean;

public abstract class MethodMonitorPointcutAdvisor implements PointcutAdvisor, InitializingBean{

	private Pointcut pointcut;
	
	@Override
	public boolean isPerInstance() {
		
		return true;
	}

	@Override
	public Pointcut getPointcut() {
		
		return pointcut;
	}

	public void setPointcut(Pointcut pointcut) {
		this.pointcut = pointcut;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		notNull(this.pointcut, "Property 'pointcut' is required");
	}
}

