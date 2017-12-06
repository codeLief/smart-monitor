package com.qding.smart.monitor.domain;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.util.StopWatch;


public class WatchMethodDomain implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String targetClass;
	
	private Method method;
	
	private StopWatch clock;
	
	private String parentClass;
	
	private String parentMehod;
	
	private Object[] args;
	
	private Object result;
	
	private boolean mainMethod;
	
	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public StopWatch getClock() {
		return clock;
	}

	public void setClock(StopWatch clock) {
		this.clock = clock;
	}
	
	public String getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}

	public String getParentClass() {
		return parentClass;
	}

	public void setParentClass(String parentClass) {
		this.parentClass = parentClass;
	}

	public String getParentMehod() {
		return parentMehod;
	}

	public void setParentMehod(String parentMehod) {
		this.parentMehod = parentMehod;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public boolean isMainMethod() {
		return mainMethod;
	}

	public void setMainMethod(boolean mainMethod) {
		this.mainMethod = mainMethod;
	}
}
