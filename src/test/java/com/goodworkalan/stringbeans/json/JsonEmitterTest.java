package com.goodworkalan.stringbeans.json;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.goodworkalan.stringbeans.Converter;

/**
 * Unit tests for the {@link JsonEmitter} class.
 *
 * @author Alan Gutierrez
 */
public class JsonEmitterTest {
    /** Test most things. */
    @Test
    public void everything() throws IOException {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("a", "/</\\\"\b\f\t\r\n\u001B\u0080\u00a1\u2000\u2101");
        map.put("b", 1);
        map.put("c", 0.1);
        map.put("d", 1.1);
        map.put("e", Arrays.asList("a", "b"));
        Map<String, Object> subMap = new HashMap<String, Object>();
        subMap.put("a", true);
        subMap.put("b", false);
        subMap.put("c", null);
        map.put("f", subMap);
        map.put("g", 'g');
        Person person = new Person();
        person.firstName = "Alan";
        person.lastName = "Gutierrez";
        map.put("h", person);
        map.put("i", 10.1);
        map.put("j", -1.1);
        Converter converter = new Converter();
        converter.setBean(Person.class);
        JsonEmitter emitter = new JsonEmitter(converter);
        StringWriter writer = new StringWriter();
        emitter.emit(writer, map);
        map = new JsonParser(writer.toString()).object();
        assertEquals(map.get("a"), "/</\\\"\b\f\t\r\n\u001B\u0080\u00a1\u2000\u2101");
        assertEquals((long) (Long) map.get("b"), 1);
        assertEquals((double) (Double) map.get("c"), 0.1);
        assertEquals((double) (Double) map.get("d"), 1.1);
        assertEquals(map.get("g"), "g");
        Map<?, ?> bean = (Map<?, ?>) map.get("h");
        assertEquals(bean.get("firstName"), "Alan");
        assertEquals(bean.get("lastName"), "Gutierrez");
        Map<?, ?> literals = (Map<?, ?>) map.get("f");
        assertEquals(literals.get("a"), Boolean.TRUE);
        assertEquals(literals.get("b"), Boolean.FALSE);
        assertNull(literals.get("c"));
        assertEquals(map.get("i"),  10.1);
        assertEquals(map.get("j"),  -1.1);
    }
    
    /** Test writing a bean not registered as a bean. */
    @Test
    public void unbean() throws IOException {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        Person person = new Person();
        person.firstName = "Alan";
        person.lastName = "Gutierrez";
        map.put("a", person);
        JsonEmitter emitter = new JsonEmitter(new Converter());
        StringWriter writer = new StringWriter();
        emitter.emit(writer, map);
        map = new JsonParser(writer.toString()).object();
        assertEquals(map.get("a").getClass(), String.class);
    }
    
    /** Test reading array. */
    @Test
    public void readList() {
        List<?> list = (List<?>) new JsonParser("[1]").read();
        assertEquals((long) (Long) list.get(0), 1);
    }
    
    /** Test reading object. */
    @Test
    public void readObject() {
        Map<?, ?> map = (Map<?, ?>) new JsonParser("{\"a\":1}").read();
        assertEquals((long) (Long) map.get("a"), 1);
    }
}
