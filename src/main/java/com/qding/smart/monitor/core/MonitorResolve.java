package com.qding.smart.monitor.core;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinitionHolder;

import com.qding.smart.monitor.domain.MonitorBeanMetaData;

/**
 * @desc：解析器
 * @author: qd-ankang
 * @date: 2017-11-27 下午5:23:10
 */
public interface MonitorResolve {

	/**
	 * @Description: 对需要监控bean or method 进行解析 
	 */
	void resolve(final BeanDefinitionHolder holder);
	
	/**
	 * 
	 * @Description: 获取元数据 
	 */
	List<MonitorBeanMetaData> getMetaDatas();
	
	/**
	 * @Description: 需要代理的bean 
	 */
	String[] getBeanNames();
	
}
