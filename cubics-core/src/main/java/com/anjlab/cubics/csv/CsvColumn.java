package com.anjlab.cubics.csv;

import java.io.Serializable;

import com.anjlab.cubics.Coercer;


public class CsvColumn implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8076670171478319602L;
    
    private String name;
    private int index;
    private Coercer<?> coercer;
    
    public CsvColumn(String name, int index, Coercer<?> coercer) {
        this.name = name;
        this.index = index;
        this.coercer = coercer;
    }

    public String getName() {
        return name;
    }
    
    public int getIndex() {
        return index;
    }

    void setIndex(int index) {
        this.index = index;
    }

    public Coercer<?> getCoercer() {
        return coercer;
    }
}
