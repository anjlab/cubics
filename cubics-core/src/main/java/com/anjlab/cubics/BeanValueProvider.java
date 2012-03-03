package com.anjlab.cubics;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * POJO value provider.
 * 
 * Fact attributes are POJO fields.
 * 
 * @author dmitrygusev
 *
 * @param <T> Type of the POJO class.
 * 
 */
public class BeanValueProvider<T> implements FactValueProvider<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5493839842292837419L;
	
	private Class<T> beanClass;
	private transient Map<String, Method> methods;
	
	/**
	 * 
	 * @param beanClass {@link Class} of the fact class.
	 */
	public BeanValueProvider(Class<T> beanClass) {
		this.beanClass = beanClass;
	}

	private Map<String, Method> getMethods() {
		if (methods == null) {
			readMethods();
		}
		return methods;
	}
	
	private void readMethods() {
		this.methods = new HashMap<String, Method>();
		for (Method method : beanClass.getMethods()) {
			String methodName = method.getName().toLowerCase(); 
			if (processMethod(method, methodName, "get") 
					|| processMethod(method, methodName, "is")); 
		}
	}

	private boolean processMethod(Method method, String methodName, String prefix) {
		if (methodName.startsWith(prefix) && methodName.length() > prefix.length()) {
			methods.put(methodName.substring(prefix.length()), method);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
     * @see anjlab.cubics.FactValueProvider#getValue(java.lang.String, T)
     */
	public Object getValue(String property, T instance) {
		try {
			return getMethods().get(property.toLowerCase()).invoke(instance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
