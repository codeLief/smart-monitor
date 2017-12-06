package com.qding.smart.monitor.domain;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MonitorBeanMetaData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Class<?> beanClass;
	
	private Map<Class<? extends Annotation>, Set<Method>> methods = new HashMap<Class<? extends Annotation>, Set<Method>>(1);
 	
	private Set<Method> uniqueAllMethods = new HashSet<Method>();
	
	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	public Map<Class<? extends Annotation>, Set<Method>> getMethods() {
		return methods;
	}

	
	public Set<Method> getUniqueAllMethods() {
		return uniqueAllMethods;
	}

	public void buildMethods(){
		if(this.methods.size() > 1){
			for (Set<Method> methods : this.methods.values()) {
				this.uniqueAllMethods.addAll(methods);
			}
		}else{
			this.uniqueAllMethods.addAll(this.methods.values().iterator().next());
		}
	}
}
