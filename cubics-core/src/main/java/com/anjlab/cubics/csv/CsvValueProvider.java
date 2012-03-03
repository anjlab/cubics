package com.anjlab.cubics.csv;

import com.anjlab.cubics.Coercer;
import com.anjlab.cubics.FactValueProvider;

/**
 * CSV entry value provider.
 * 
 * @author dmitrygusev
 *
 */
public class CsvValueProvider implements FactValueProvider<CsvEntry> {

    /**
     * 
     */
    private static final long serialVersionUID = -7333745085284505068L;
    
    private CsvDefinition definition;
    
    public CsvValueProvider(CsvDefinition definition) {
        this.definition = definition;
    }

    public Object getValue(String columnName, CsvEntry instance) {
        int index = definition.getColumnIndex(columnName);
        String value = instance.getValue(index);
        Coercer<?> coercer = definition.getCoercer(columnName);
        return coercer.coerce(value);
    }
}
