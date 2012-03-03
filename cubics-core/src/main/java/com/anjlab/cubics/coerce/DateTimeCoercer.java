package com.anjlab.cubics.coerce;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.anjlab.cubics.Coercer;


public class DateTimeCoercer implements Coercer<Date>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4542606657120016331L;
    
    private String pattern;
    private DateFormat format;
    
    public DateTimeCoercer(String pattern) {
        this.pattern = pattern;
        this.format = new SimpleDateFormat(pattern);
    }
    
    public Date coerce(String s) {
        try {
            return format.parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(
                    "Couldn't coerce '" + s + "' to date using '" + pattern +  "' format pattern.", e);
        }
    }
}
