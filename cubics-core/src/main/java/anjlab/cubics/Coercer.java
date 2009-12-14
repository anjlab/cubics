package anjlab.cubics;

/**
 * Responsible for coercing values from their string representation to instance of type <code>T</code>.
 *    
 * @author dmitrygusev
 *
 * @param <T> Type of coerced value.
 */
public interface Coercer<T> {

	public T coerce(String s);

}
