package com.qding.smart.monitor.scan;

import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.qding.smart.monitor.core.AbstractMonitorRegistry;
import com.qding.smart.monitor.core.AbstractMonitorResolve;
import com.qding.smart.monitor.utils.ReflectionUtils;

/**
 * @author: qd-ankang
 * @date: 2017-11-27 下午5:23:10
 */
public class MonitorScanner extends ClassPathBeanDefinitionScanner {

	protected final Log logger = LogFactory.getLog(MonitorScanner.class);

	private AbstractMonitorResolve resolve;
	
	private AbstractMonitorRegistry monitorRegistry;
	
	public MonitorScanner(BeanDefinitionRegistry registry) {
		super(registry, false);
	}
	public void init(String monitorRegistryClazz, String annotationClazz){
		
		notNull(monitorRegistryClazz, "Property 'monitorRegistryClazz' is required");
		notNull(annotationClazz, "Property 'annotationClazz' is required");
		try {
			
			logger.info("monitorRegistry class is " + monitorRegistryClazz);
			
			monitorRegistry = (AbstractMonitorRegistry) BeanUtils.instantiate(Class.forName(monitorRegistryClazz));
			
			monitorRegistry.init();
			
			logger.info("An annotation that needs to be scanned :" + annotationClazz);
			
			resolve = (AbstractMonitorResolve) monitorRegistry.build( 
						StringUtils.tokenizeToStringArray(annotationClazz, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
		
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("build MonitorOwner fail");
		}catch (ClassNotFoundException | BeanInstantiationException e) {
			throw new BeanInitializationException("init monitorRegistry fail class:" + monitorRegistryClazz);
		}
	}
	
	public void registerFilters() {

		notNull(this.resolve, "MonitorOwner is uninitialized!");
		
		//Include
		for (TypeFilter typeFilter : monitorRegistry.getIncludeTypeFilters()) {
			addIncludeFilter(typeFilter);
		}
		
		//Exclude
		for (TypeFilter typeFilter : monitorRegistry.getExcludeTypeFilters()) {
			addExcludeFilter(typeFilter);
		}
		
		//Include Annotation
		for (Class<? extends Annotation>  annotationClass: resolve.getAnnotationClass()) {
			addIncludeFilter(new AnnotationTypeFilter(annotationClass));
		}
		
		// Include Advice
        addIncludeFilter(new AssignableTypeFilter(monitorRegistry.getAdviceClass()));
		
		// Include Monitor
		addIncludeFilter(new TypeFilter() {
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
					throws IOException {
				
				ClassMetadata classMetadata = metadataReader.getClassMetadata();

				if(classMetadata.isInterface()
						|| classMetadata.isAbstract()
						|| classMetadata.isAnnotation()){
					
					return false;
				}
				
				String className = classMetadata.getClassName();
				Class<?> beanClass = null;
				try {
					beanClass = Class.forName(className);
				} catch (ClassNotFoundException e) {
				}
				if (beanClass == null) {

					return false;
				}
				
				Set<Method> allMethods = ReflectionUtils.getUniqueAllMethods(beanClass);
				for (Method method : allMethods) {
						
					for (Class<? extends Annotation>  annotationClass: resolve.getAnnotationClass()) {
						if(method.isAnnotationPresent(annotationClass)) return Boolean.TRUE;
					}
					
				}
				return false;
			}
		});
	}

	@Override
	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
		if (beanDefinitions.isEmpty()) {
			logger.warn("No monitor class or method found in '"+ Arrays.toString(basePackages)
							+ "' package. Please check your configuration.");
		} else {
			
			ManagedList<BeanReference> advices = new ManagedList<>();
			for (BeanDefinitionHolder holder : beanDefinitions) {
				
				if(holder.getBeanDefinition() instanceof ScannedGenericBeanDefinition){
					final ScannedGenericBeanDefinition beanDefinition = (ScannedGenericBeanDefinition)holder.getBeanDefinition();
					
					AnnotationMetadata metadata = beanDefinition.getMetadata();
					String className = metadata.getClassName();
					if (StringUtils.hasText(className)
							&& !metadata.isInterface()
							&& !metadata.isAbstract()
							&& !metadata.isAnnotation()) {
						try {
							Class<?> clazz = ClassUtils.forName(className, getClass().getClassLoader());
							if(monitorRegistry.getAdviceClass().isAssignableFrom(clazz)
									&& !clazz.isAnnotationPresent(Deprecated.class)){//Exclude marker Deprecated
								
								getRegistry().registerBeanDefinition(holder.getBeanName(), beanDefinition);
								logger.info("register methodMonitorAdvice : " + beanDefinition.getBeanClassName());
								advices.add(new RuntimeBeanReference(holder.getBeanName()));
								continue;
							}
						}catch (Throwable ex) {
							// no class
						}
					}
				}
				resolve.resolve(holder);
			}
			
			monitorRegistry.buildInception(getRegistry(), advices);
		}
		
		return beanDefinitions;
	}

	// 如果注册过的bean会被spring过滤掉
	@Override
	protected boolean checkCandidate(String beanName,
			BeanDefinition beanDefinition) throws IllegalStateException {

		return true;
	}
}
