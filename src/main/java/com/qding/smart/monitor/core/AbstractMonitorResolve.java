package com.qding.smart.monitor.core;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMonitorResolve implements MonitorResolve{

	private List<Class<? extends Annotation>> annotationClass = new ArrayList<Class<? extends Annotation>>();
	
	public List<Class<? extends Annotation>> getAnnotationClass() {
		
		return annotationClass;
	}
}
