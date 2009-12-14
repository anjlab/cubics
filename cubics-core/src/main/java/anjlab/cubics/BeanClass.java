package anjlab.cubics;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * POJO helper class.
 * 
 * @author dmitrygusev
 *
 * @param <T> Type of the POJO class.
 */
public class BeanClass<T> {

	private Class<T> beanClass;
	private Map<String, Method> methods;
	
	public BeanClass(Class<T> beanClass) {
		this.beanClass = beanClass;
		readMethods();
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

	public Object getValue(String property, T instance) {
		try {
			return methods.get(property.toLowerCase()).invoke(instance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
