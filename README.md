#smart-monitor

```
扫描指定包下标注annotation的类或方法，对方法进行切入， 
如果Marker类则该类下所有方法都会被切入，包含从父类继承
的方法， 如果Marker method，则只对指定method切入。因此就可以围绕
该方法做一些事情了，比如对方法性能进行监控，入参出参log输出等等一些
功能，无代码侵入。
```

一、配置方法
--------
```
在项目的spring配置文件注入该bean就可以了

<bean id="monitor" class="com.qding.smart.monitor.scan.MonitorConfig">
    <property name="basePackage" value="com.qding.smart.monitor,com.qding"/>//必要可指定多个
    <property name="annotationClazz" value="com.qding.smart.monitor.annotation.Monitor"/>//非必要，可指定多个
    <property name="monitorRegistry" value="com.qding.smart.monitor.core.DefaultMonitorRegistry"/>//非必要
    <property name="pluginSwitch" value="false"/>//非必要 注意:本类注册优先级较高
</bean>
```
二、方法围绕通知
--------

```
实现MethodMonitorAdvice接口，并将实现类包名加入扫描。basePackage
public interface MethodMonitorAdvice extends Ordered{
    
	void before(Class<?> targetClass, Method method, Object[] args);
	
	void after(Class<?> targetClass, Method method, Object result);
}
```

二、monitorRegistry重写
--------

```
继承AbstractMonitorRegistry返回自定义解析器对象，和切面对象
public class DefaultMonitorRegistry extends AbstractMonitorRegistry {

    @Override
	public Class<? extends MonitorResolve> getOwnerClass() {
		
		return DefaultMonitorResolve.class;
	}

	@Override
	public Class<? extends MethodInterceptor> getInceptionClass() {
		
		return MethodMonitorInception.class;
	}
}

```
