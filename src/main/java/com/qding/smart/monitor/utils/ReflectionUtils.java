package com.qding.smart.monitor.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

public class ReflectionUtils {
	
	//只包含公共方法
	private static final Map<Class<?>, Method[]> allMethodsCache =
										new ConcurrentReferenceHashMap<Class<?>, Method[]>(256);
	
	public static Set<Method> getUniqueAllMethods(Class<?> clazz){
		return new HashSet<>(Arrays.asList(getAllMethods(clazz)));
	}
	
	public static Method[] getAllMethods(Class<?> clazz){
		
		Method[] result = allMethodsCache.get(clazz);
		if(result == null){
			final List<Method> methods = new ArrayList<Method>(32);
			org.springframework.util.ReflectionUtils.doWithMethods(clazz, new MethodCallback() {
				@Override
				public void doWith(Method method) {
					methods.add(method);
				}
			}, null);
			
			result = methods.toArray(new Method[methods.size()]);
			allMethodsCache.put(clazz, result);
		}
		
		return result;
	}
	
	public static void doWithMethods(Class<?> clazz, MethodCallback mc, MethodFilter mf)
			throws IllegalArgumentException {

		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (mf != null && !mf.matches(method)) {
				continue;
			}
			try {
				mc.doWith(method);
			}
			catch (IllegalAccessException ex) {
				throw new IllegalStateException("Shouldn't be illegal to access method '" + method.getName() + "': " + ex);
			}
		}
		if (clazz.getSuperclass() != null) {
			doWithMethods(clazz.getSuperclass(), mc, mf);
		}
		else if (clazz.isInterface()) {
			for (Class<?> superIfc : clazz.getInterfaces()) {
				doWithMethods(superIfc, mc, mf);
			}
		}
	}
}
