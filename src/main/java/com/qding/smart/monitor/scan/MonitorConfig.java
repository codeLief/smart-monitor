package com.qding.smart.monitor.scan;

import static org.springframework.util.Assert.notNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import com.qding.smart.monitor.annotation.Monitor;
import com.qding.smart.monitor.core.DefaultMonitorRegistry;

/**
 * @desc:扫描指定包下标注annotation的类或方法，对方法进行切入，
 * 		  如果Marker类则该类下所有方法都会被切入，包含从父类继承的方法，
 * 		 如果Marker method，则只对指定method切入，由MethodMonitorInception代理。
 * 			<bean id="monitor" class="com.qding.smart.monitor.scan.MonitorConfig">
 *		    	<property name="basePackage" value="com.qding.smart.monitor,com.qding"/>//必要可指定多个
 *		    	<property name="annotationClazz" value="com.qding.smart.monitor.annotation.Monitor"/>//非必要，可指定多个
 *		    	<property name="monitorRegistry" value="com.qding.smart.monitor.core.DefaultMonitorRegistry"/>//非必要
 *		    	<property name="pluginSwitch" value="false"/>//非必要 注意:本类注册优先级较高
 *		    </bean>
 * @author: ankang
 * @date: 2017-11-27 下午5:23:10
 */
public class MonitorConfig implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware {

	protected final Log logger = LogFactory.getLog(MonitorConfig.class);
	
	/**
	 * 需要扫描的包  多个逗号或者分号隔开
	 * 必须要包含的有：
	 * 		1、标注 annotation的bean
	 * 		2、扩展MethodMonitorAdvice的实现类
	 */
	private String basePackage;
	
	/**
	 * 需要扫描的annotationClass 多个逗号或者分号隔开
	 */
	private String annotationClazz = Monitor.class.getName();
	
	/**
	 * 注册器
	 */
	private String monitorRegistry = DefaultMonitorRegistry.class.getName();
	
	/**
	 * 插件开关
	 */
	private boolean pluginSwitch = Boolean.TRUE; 
	
	private ApplicationContext applicationContext;
	
	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(this.pluginSwitch){
			
			notNull(this.basePackage, "Property 'basePackage' is required");
			notNull(this.annotationClazz, "Property 'annotationClazz' is required");
			notNull(this.monitorRegistry, "Property 'monitorRegistry' is required");
		}
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		
		if(this.pluginSwitch){
			logger.info("Monitor plugin start");
			MonitorScanner scanner = new MonitorScanner(registry);
			scanner.setResourceLoader(this.applicationContext);
			scanner.init(this.monitorRegistry, this.annotationClazz);
			scanner.registerFilters();
			scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
		}
	}

	public void setAnnotationClazz(String annotationClazz) {
		if(!StringUtils.isEmpty(annotationClazz)){
			StringBuffer sb = new StringBuffer(this.annotationClazz);
			sb.append(",").append(annotationClazz);
			this.annotationClazz = sb.toString();
		}
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public void setMonitorRegistry(String monitorRegistry) {
		this.monitorRegistry = monitorRegistry;
	}

	public void setPluginSwitch(boolean pluginSwitch) {
		
		this.pluginSwitch = pluginSwitch;
	}
}
