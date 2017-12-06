package com.qding.smart.monitor.aspect;

import static org.springframework.util.Assert.notNull;

import java.util.ArrayList;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.OrderComparator;


/**
 * 
 * @Description: 代理类
 * @author: qd-ankang
 * @date: 2017-11-30 下午5:02:18
 */
public class MethodMonitorInception extends MethodMonitorPointcutAdvisor implements MethodInterceptor {

	protected final Log logger = LogFactory.getLog(MethodMonitorInception.class);
	
	private List<MethodMonitorAdvice> expandAdvices;
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		List<MethodMonitorAdvice> errorAdvices = new ArrayList<MethodMonitorAdvice>();
		
		for (MethodMonitorAdvice adviceBefore : expandAdvices) {
			try {
				adviceBefore.before(invocation.getThis().getClass(), invocation.getMethod(), invocation.getArguments());
			} catch (Exception e) {
				logger.error("before execute fail. MethodMonitorAdvice:" + adviceBefore.getClass());
				errorAdvices.add(adviceBefore);
			}
		}
		expandAdvices.removeAll(errorAdvices);
		
		Object result = null;
		try {
			result = invocation.proceed();
		} catch (Exception e1) {
			logger.error(String.format("proceed execute fail. targetClass:[%s], method:[%s]", invocation.getThis().getClass(), invocation.getMethod().getName()));
			throw e1;
		} finally{
			
			for (MethodMonitorAdvice adviceAfter : expandAdvices) {
				try {
					adviceAfter.after(invocation.getThis().getClass(), invocation.getMethod(), result);
				} catch (Exception e) {
					logger.error("after execute fail. MethodMonitorAdvice:" + adviceAfter.getClass());
				}
			}
			
			if(errorAdvices.size() > 0){
				expandAdvices.addAll(errorAdvices);
				compared();
			}
		}
		
		return result;
	}

	@Override
	public Advice getAdvice() {
		return this;
	}
	
	public void setExpandAdvices(List<MethodMonitorAdvice> expandAdvices) {
		this.expandAdvices = expandAdvices;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		notNull(this.expandAdvices, "Property 'expandAdvices' is required");
		compared();
	}
	
	private void compared(){
		OrderComparator.sort(expandAdvices);
	}
}
