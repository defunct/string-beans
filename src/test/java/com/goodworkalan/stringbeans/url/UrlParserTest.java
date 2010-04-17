package com.goodworkalan.stringbeans.url;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.goodworkalan.stringbeans.StringBeanException;
import com.goodworkalan.stringbeans.Stringer;
import com.goodworkalan.stringbeans.StringerBuilder;

public class UrlParserTest {
    @Test
    public void setString() {
        Map<String, String> form = new HashMap<String, String>();
        form.put("string", "Hello, World!");
        Widget widget = newUrlParser().create(Widget.class, form);
        assertEquals(widget.string, "Hello, World!");
    }
    
    @Test
    public void resetString() {
        Widget widget = new Widget();
        widget.string = "Foo.";
        Map<String, String> form = new HashMap<String, String>();
        form.put("string", "Hello, World!");
        newUrlParser().populate(widget, form);
        assertEquals(widget.string, "Hello, World!");
    }

    @Test
    public void setNumber() {
        Map<String, String> form = new HashMap<String, String>();
        form.put("number", "1");
        Widget widget = newUrlParser().create(Widget.class, form);
        assertEquals(widget.number, 1);
    }

    @Test
    public void setWidgetString() {
        Map<String, String> form = new HashMap<String, String>();
        form.put("widget[string]", "Hello, World!");
        Widget widget = newUrlParser().create(Widget.class, form);
        assertEquals(widget.widget.string, "Hello, World!");
    }
    

    @Test
    public void setCollectionWidgetString() {
        Map<String, String> form = new LinkedHashMap<String, String>();
        form.put("list[-2][string]", "Hello, World!");
        form.put("list[5][string]", "Hello, Nurse!");
        Widget widget = newUrlParser().create(Widget.class, form);
        assertEquals(widget.list.get(0).string, "Hello, World!");
        assertEquals(widget.list.get(1).string, "Hello, Nurse!");
    }
    
    @Test
    public void stringSpecifiedTwice() {
        Map<String, String> form = new LinkedHashMap<String, String>();
        form.put("widget.string", "Hello, World!");
        form.put("widget[string]", "Hello, Nurse!");
        Widget widget = newUrlParser().create(Widget.class, form);
        assertEquals(widget.widget.string, "Hello, World!");
    }
    
    @Test(expectedExceptions=StringBeanException.class)
    public void badPath() {
        try {
            Map<String, String> form = new LinkedHashMap<String, String>();
            form.put("#", "Hello, World!");
            Widget widget = newUrlParser().create(Widget.class, form);
            assertEquals(widget.widget.string, "Hello, World!");
        } catch (StringBeanException e) {
            assertEquals(e.getMessage(), "Unable to parse URL path: #.");
            throw e;
        }
    }
    
    @Test(expectedExceptions=StringBeanException.class)
    public void unexpectedCollection() {
        try {
            Map<String, String> form = new LinkedHashMap<String, String>();
            form.put("list[-1].string", "Hello, World!");
            form.put("list.foo.string", "Hello, World!");
            Widget widget = newUrlParser().create(Widget.class, form);
            assertEquals(widget.widget.string, "Hello, World!");
        } catch (StringBeanException e) {
            assertEquals(e.getMessage(), "Expecting container type class com.goodworkalan.stringbeans.url.StringToObjectMap but got class com.goodworkalan.stringbeans.url.SparseCollection.");
            throw e;
        }
    }
    
    
    @Test(expectedExceptions=StringBeanException.class)
    public void unexpectedMap() {
        try {
            Map<String, String> form = new LinkedHashMap<String, String>();
            form.put("list.foo.string", "Hello, World!");
            form.put("list[-1].string", "Hello, World!");
            Widget widget = newUrlParser().create(Widget.class, form);
            assertEquals(widget.widget.string, "Hello, World!");
        } catch (StringBeanException e) {
            assertEquals(e.getMessage(), "Expecting container type class com.goodworkalan.stringbeans.url.SparseCollection but got class com.goodworkalan.stringbeans.url.StringToObjectMap.");
            throw e;
        }
    }
    
    private UrlParser newUrlParser() {
        Stringer stringer = new StringerBuilder().bean(Widget.class).getInstance();
        UrlParser parser = new UrlParser(stringer);
        return parser;
    }
}
