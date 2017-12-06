package com.qding.smart.monitor.core;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.core.type.filter.TypeFilter;

import com.qding.smart.monitor.aspect.MethodMonitorAdvice;
import com.qding.smart.monitor.aspect.MonitorNameMatchMethodPointcut;

/**
 * @author: qd-ankang
 * @date: 2017-11-27 下午5:23:10
 */
public abstract class AbstractMonitorRegistry {

	private MonitorResolve owner = null;
	
	private List<TypeFilter> includeTypeFilters = new ArrayList<>();
	
	private List<TypeFilter> excludeTypeFilters = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	public synchronized MonitorResolve build(String...annotationClazz) throws InstantiationException, IllegalAccessException{
		
		if(owner != null){
			return owner;
		}
		AbstractMonitorResolve owner = (AbstractMonitorResolve)BeanUtils.instantiate(getOwnerClass());
		if(annotationClazz == null || annotationClazz.length == 0){
			
			throw new IllegalArgumentException("annotationClazz is not available." + annotationClazz);
		}
		for (String clazz : annotationClazz) {
			try {
				owner.getAnnotationClass().add((Class<? extends Annotation>) Class.forName(clazz));
			} catch (ClassNotFoundException e) {
				
				throw new IllegalArgumentException("annotationClazz is not available." + clazz);
			}
		}
		this.owner = owner;
		return owner;
	}

	public void buildInception(final BeanDefinitionRegistry registry, ManagedList<BeanReference> advices){
		
		BeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClassName(getInceptionClass().getName());
		beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
		beanDefinition.getPropertyValues().add("expandAdvices", advices);
		beanDefinition.getPropertyValues().add("pointcut", new MonitorNameMatchMethodPointcut(owner.getMetaDatas()));
		String beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry);		
		registry.registerBeanDefinition(beanName, beanDefinition);
	}
	
	public void init(){
		
	}
	
	public MonitorResolve getOwner() {
		return owner;
	} 
	
	protected void addIncludeFilter(TypeFilter typeFileter){
		if(typeFileter != null){
			includeTypeFilters.add(typeFileter);
		}
	}
	protected void addExcludeFilter(TypeFilter typeFileter){
		if(typeFileter != null){
			excludeTypeFilters.add(typeFileter);
		}
	}
	
	public List<TypeFilter> getIncludeTypeFilters() {
		return includeTypeFilters;
	}

	public List<TypeFilter> getExcludeTypeFilters() {
		return excludeTypeFilters;
	}

	public final Class<? extends MethodMonitorAdvice> getAdviceClass(){
		
		return MethodMonitorAdvice.class;
	}
	
	protected Class<?> getAutoProxyClass(){
		
		return BeanNameAutoProxyCreator.class;
	}
	
	public abstract Class<? extends MonitorResolve> getOwnerClass();
	
	public abstract Class<? extends MethodInterceptor> getInceptionClass();
}
