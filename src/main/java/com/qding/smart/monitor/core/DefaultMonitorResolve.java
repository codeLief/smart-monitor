package com.qding.smart.monitor.core;

import static org.springframework.util.Assert.notNull;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

import com.qding.smart.monitor.domain.MonitorBeanMetaData;
import com.qding.smart.monitor.utils.ReflectionUtils;

/**
 * @author: qd-ankang
 * @date: 2017-11-27 下午5:23:10
 */
public class DefaultMonitorResolve extends AbstractMonitorResolve implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 需要切入的beanName
	 */
	private List<String> beanNames = new ArrayList<String>();
	
	private List<MonitorBeanMetaData> metaDatas = new ArrayList<MonitorBeanMetaData>();
	
	@Override
	public void resolve(final BeanDefinitionHolder holder) {
		
		final BeanDefinition definition = holder.getBeanDefinition();
		
		notNull(definition, "bean 'definition' is required");
		
		MonitorBeanMetaData meta = new MonitorBeanMetaData();
		
		String className = definition.getBeanClassName();
		
		try {
			
			Class<?> beanClass = Class.forName(className);
			meta.setBeanClass(beanClass);
			
			//所有方法包括继承方法
			Set<Method> allMethods = ReflectionUtils.getUniqueAllMethods(beanClass);
			
			for (Class<? extends Annotation> clazz : getAnnotationClass()) {
				
				//类是否标注 是则影响整个类方法 如果类标签和方法标签不一样，以类标签为主
				if(beanClass.isAnnotationPresent(clazz)){
					meta.getMethods().put(clazz, allMethods);
				}else{
					
					for (Method method : allMethods) {
						
						Annotation annotation = method.getAnnotation(clazz);
						
						if(annotation != null) {
							
							Set<Method> methods = meta.getMethods().get(annotation.annotationType());
							if(methods == null){
								methods = new HashSet<Method>();
							}
							methods.add(method);
							
							meta.getMethods().put(clazz, methods);
						}
					}
				}
			}
			
		} catch (ClassNotFoundException e) {
			// no class
		}
		
		meta.buildMethods();
		beanNames.add(holder.getBeanName());
		metaDatas.add(meta);
	}

	@Override
	public String[] getBeanNames() {
		
		return beanNames.toArray(new String[]{});
	}

	@Override
	public List<MonitorBeanMetaData> getMetaDatas() {
		
		return this.metaDatas;
	}
}
