package com.anjlab.cubics.csv;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.anjlab.cubics.Coercer;
import com.anjlab.cubics.coerce.StringCoercer;


public class CsvDefinition implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3826004103415723707L;
    
    private Map<String, CsvColumn> columns;
    private int index;
    
    public CsvDefinition() {
        this.columns = new HashMap<String, CsvColumn>();
        this.index = 0;
    }
    
    public int getColumnIndex(String columnName) {
        assertColumnExists(columnName);
        return columns.get(columnName).getIndex();
    }

    public Coercer<?> getCoercer(String columnName) {
        assertColumnExists(columnName);
        return columns.get(columnName).getCoercer();
    }

    private void assertColumnExists(String columnName) {
        if (!columns.containsKey(columnName)) {
            throw new RuntimeException(
                    "This CSV definition doesn't have column with name '" + columnName + "'");
        }
    }

    /**
     * Add new column definition. 
     * 
     * Column index will depend on order in which the column was added to this definition.
     *  
     * @param columnName Column name.
     * @param coercer Column value coercer.
     */
    public void addColumn(String columnName, Coercer<?> coercer) {
        if (this.columns.containsKey(columnName)) {
            throw new IllegalStateException(
                    "This CSV definition already contains column with name '" + columnName + "'");
        }
        
        this.columns.put(columnName, new CsvColumn(columnName, index++, coercer));
    }
    
    /**
     * Short version of {@link #addColumn(String, Coercer)} which uses
     * {@link StringCoercer} as a second parameter.
     * 
     * @param columnName Column name.
     */
    public void addColumn(String columnName) {
        addColumn(columnName, new StringCoercer());
    }

    /**
     * Update column indices by the order the appear in <code>header</header>.
     * 
     * Use this method if column order differs from order in which columns were added to this definition. 
     * 
     * @param header CSV header.
     * @param separator Separator char.
     */
    public void updateColumnIndices(String header, String separator) {
        String[] headers = header.split(separator);
        for (int i = 0; i < headers.length; i++) {
            String columnName = headers[i];
            CsvColumn column = this.columns.get(columnName);
            if (column != null) {
                column.setIndex(i);
            }
        }
    }
}
