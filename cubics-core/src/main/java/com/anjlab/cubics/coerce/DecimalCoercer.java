package com.anjlab.cubics.coerce;

import java.io.Serializable;
import java.math.BigDecimal;

import com.anjlab.cubics.Coercer;


public class DecimalCoercer implements Coercer<BigDecimal>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6933690905668640280L;

    public BigDecimal coerce(String s) {
        return new BigDecimal(s);
    }

}
