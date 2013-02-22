package com.google.jplurk;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

public class DateTimeTest extends TestCase {

    public void testCreateFromString() throws Exception {
        String[] inputSamples = new String[] { 
                "Sat, 09 Jan 2010 08:22:53 GMT",
                "Sat, 09 Jan 2010 08:22:52 GMT",
                "Mon, 28 Dec 2009 02:24:28 GMT" };
        
        for (String jsTime : inputSamples) {
            Date converted = DateTime.create(jsTime).toCalendar().getTime();

            String toJsTime = DateTime.JS_INPUT_FORMAT.format(converted);
            assertEquals(DateTime.create(toJsTime).toCalendar().getTime(),
                    converted);
        }
    }
    
    public void testCreateFromYYYYmmdd() throws Exception {
        Calendar calendar = DateTime.create(2009, 4, 1).toCalendar();
        assertEquals(2009, calendar.get(Calendar.YEAR));
        assertEquals(4, calendar.get(Calendar.MONTH) + 1);
        assertEquals(1, calendar.get(Calendar.DATE));

        calendar = DateTime.create(2007, 4, 1, 23, 59, 31).toCalendar();
        assertEquals(2007, calendar.get(Calendar.YEAR));
        assertEquals(4, calendar.get(Calendar.MONTH) + 1);
        assertEquals(1, calendar.get(Calendar.DATE));
        assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(59, calendar.get(Calendar.MINUTE));
        assertEquals(31, calendar.get(Calendar.SECOND));
    }
    
    
}
