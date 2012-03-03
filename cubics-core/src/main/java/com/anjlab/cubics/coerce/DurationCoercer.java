package com.anjlab.cubics.coerce;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.anjlab.cubics.Coercer;


public class DurationCoercer implements Coercer<Double>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -329572640965081787L;

    private static final Calendar calendar;
    static {
        calendar = Calendar.getInstance();
        //  Calendar.getTimeInMillis() returns UTC milliseconds from the epoch, 
        //  both Calendar and DateFormat should use UTC time zone.
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private String pattern;
    private DateFormat format;
    private TimeUnit timeUnit;
    
    public DurationCoercer(String pattern, TimeUnit timeUnit) {
        this.pattern = pattern;
        this.format = new SimpleDateFormat(pattern);
        this.format.setTimeZone(calendar.getTimeZone());
        this.timeUnit = timeUnit;
    }
    
    public Double coerce(String s) {
        try {
            Date date = format.parse(s);
            calendar.setTime(date);
            long timeInMillis = calendar.getTimeInMillis();
            switch (timeUnit) {
            case SECONDS:
                return timeInMillis / 1000d;
            case MINUTES:
                return timeInMillis / 1000d / 60d;
            case HOURS:
                return timeInMillis / 1000d / 60d / 60d;
            case DAYS:
                return timeInMillis / 1000d / 60d / 60d / 24d;
            }
            throw new RuntimeException("Unsupported TimeUnit '" + timeUnit + "'. Use one of: SECONDS, MINUTES, HOURS, DAYS.");
        } catch (ParseException e) {
            throw new RuntimeException(
                    "Couldn't coerce '" + s + "' to date using '" + pattern +  "' format pattern.", e);
        }
    }
}
