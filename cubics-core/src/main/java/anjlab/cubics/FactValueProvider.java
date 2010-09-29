package anjlab.cubics;

import java.io.Serializable;

/**
 * Provides access to fact attributes values.
 * 
 * @author dmitrygusev
 *
 * @param <T> Type of the fact class.
 */
public interface FactValueProvider<T> extends Serializable {

    /**
     * Returns value of attribute of underlying fact instance.
     *  
     * @param attribute Name of the fact's attribute.
     * @param instance The fact.
     * @return Returns the value.
     */
    public abstract Object getValue(String attribute, T instance);

}