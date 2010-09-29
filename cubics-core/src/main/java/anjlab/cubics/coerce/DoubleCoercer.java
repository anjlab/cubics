package anjlab.cubics.coerce;

import java.io.Serializable;

import anjlab.cubics.Coercer;

public class DoubleCoercer implements Coercer<Double>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -662695314977486848L;

    public Double coerce(String s) {
        return Double.valueOf(s);
    }

}
