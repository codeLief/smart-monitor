package com.qding.smart.monitor.aspect;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.StopWatch;

import com.qding.smart.monitor.domain.WatchMethodDomain;
import com.qding.smart.monitor.utils.DateUtil;

public class WatchMethodMonitorAdvice implements MethodMonitorAdvice {

	protected final Log logger = LogFactory.getLog(WatchMethodMonitorAdvice.class);
	
	private static final ThreadLocal<LinkedHashMap<Method, WatchMethodDomain>> watchMethods =
													new NamedThreadLocal<LinkedHashMap<Method, WatchMethodDomain>>("Watch method");
	
	@Override
	public void before(Class<?> targetClass, Method method, Object[] args) {
		
		try {
			WatchMethodDomain domain = new WatchMethodDomain();
			domain.setTargetClass(targetClass.getName());
			domain.setMethod(method);
			domain.setArgs(args);
			StopWatch clock = new StopWatch();
			domain.setClock(clock);
			domain.setMainMethod(isFirst());
			watchMethods.get().put(method, domain);
			clock.start();
		} catch (Exception e) {
			logger.error("Watch method exception");
			clean();
		} 
	}

	@Override
	public void after(Class<?> targetClass, Method method, Object result) {
		
		try {
			WatchMethodDomain domain = watchMethods.get().get(method);
			
			domain.getClock().stop();
			if(domain.isMainMethod()){
				printWatch("success");
			}
		} catch (Exception e) {
			logger.error("Watch method exception");
		} 
	}
	
	private void printWatch(String status){
		
		Collection<WatchMethodDomain> mds = watchMethods.get().values();
		
		if(mds.size() > 0){
			StringBuilder logMsg = new StringBuilder("\n\nMethod execute " + status + " report -------- " + DateUtil.formatDatetime(new Date()) + " ----------------------------");

			Iterator<WatchMethodDomain> mdsIterator = mds.iterator();
			WatchMethodDomain mainMethod = mdsIterator.next();
			
			logMsg.append("\nService   : ").append(mainMethod.getTargetClass());
			logMsg.append("\nMethod    : ").append(mainMethod.getMethod().getName());
			
			WatchMethodDomain maxCostTime = null;
			
			long maxTime = 0;
			
			while (mdsIterator.hasNext()) {
				WatchMethodDomain watchMethodDomain = mdsIterator.next();
				
				if(watchMethodDomain.getClock().isRunning()){
					watchMethodDomain.getClock().stop();
				}
					logMsg.append("\n            |__")
					  .append(watchMethodDomain.getMethod().getName())
					  .append("   Cost Time :");
				long time = watchMethodDomain.getClock().getTotalTimeMillis();
				if(time > maxTime){
					maxTime = time;
					maxCostTime = watchMethodDomain;
				}
				logMsg.append(time).append(" ms");
			}
			logMsg.append("\nTotal Cost: ").append(mainMethod.getClock().getTotalTimeMillis()).append(" ms");
			if(maxCostTime != null){
				logMsg.append("\nMax   Cost: ").append(maxCostTime.getMethod().getName()).append("  ")
				  .append(maxCostTime.getClock().getTotalTimeMillis()).append(" ms");
			}
			logMsg.append("\n--------------------------------------------------------------------------------\n");
			logger.info(logMsg);
			clean();
		}
	}
	private void clean(){
		watchMethods.remove();
	}
	private boolean isFirst(){
		
		if(watchMethods.get() == null){
			watchMethods.set(new LinkedHashMap<Method, WatchMethodDomain>());
			return true;
		}
		return false;
	}
	@Override
	public int getOrder() {
		
		return LOWEST_PRECEDENCE;
	}
}
