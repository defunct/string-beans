package com.goodworkalan.stringbeans.api;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.testng.annotations.Test;

import com.goodworkalan.stringbeans.ClassConverter;
import com.goodworkalan.stringbeans.StringConverter;
import com.goodworkalan.stringbeans.Stringer;
import com.goodworkalan.stringbeans.StringerBuilder;
import com.goodworkalan.stringbeans.XMLEmitter;
import com.goodworkalan.stringbeans.XMLEmitterBuilder;
import com.goodworkalan.stringbeans.XMLParser;

public class StringerTest {
    @Test
    public void testConversion() {
        Person person = new Person();
        
        person.firstName = "Thomas";
        person.lastName = "Paine";
        person.birthYear = 1759;
        
        Stringer stringer = new StringerBuilder()
            .bean(Person.class)
            .converter(String.class, new StringConverter())
            .converter(Class.class, new ClassConverter())
            .getInstance();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLEmitter emitter = new XMLEmitterBuilder(stringer).newXMLEmitter(out);
        
        emitter.emit(person);

        XMLParser parser = new XMLParser(stringer, true);
        
        person = parser.parse(Person.class, new ByteArrayInputStream(out.toByteArray()));
        
        assertEquals(person.firstName, "Thomas");
        assertEquals(person.lastName, "Paine");
        assertEquals(person.birthYear, 1759);
    }
}
