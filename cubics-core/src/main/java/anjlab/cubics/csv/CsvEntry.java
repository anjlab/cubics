package anjlab.cubics.csv;

import java.io.Serializable;

public class CsvEntry implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4993605220337255338L;
    
    private String[] values;
    
    /**
     * 
     * @param s Comma-separated values.
     * @param separator Separator char.
     */
    public CsvEntry(String s, String separator) {
       values = s.split(separator);
       for (int i = 0; i < values.length; i++) {
           String value = values[i];
           if (value.length() > 1 && value.startsWith("\"") && value.endsWith("\"")) {
               values[i] = value.substring(1, value.length() - 1);
           }
       }
    }

    /**
     * Uses comma (,) as a separator char.  
     * @param s Comma-separated values.
     */
    public CsvEntry(String s) {
        this(s, ",");
    }
    
    public String getValue(int columnIndex) {
        return values[columnIndex];
    }

}
