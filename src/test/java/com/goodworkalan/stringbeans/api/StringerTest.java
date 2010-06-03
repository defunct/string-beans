package com.goodworkalan.stringbeans.api;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import com.goodworkalan.stringbeans.Converter;
import com.goodworkalan.stringbeans.XMLEmitter;
import com.goodworkalan.stringbeans.XMLSerializerBuilder;
import com.goodworkalan.stringbeans.XMLParser;

public class StringerTest {
    @Test
    public void testConversion() throws SAXException {
        Person person = new Person();
        
        person.firstName = "Thomas";
        person.lastName = "Paine";
        person.birthYear = 1759;
        
        Converter converter = new Converter();
        converter.isBean(Person.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLEmitter emitter = new XMLEmitter(converter); 
            
        emitter.emit(new XMLSerializerBuilder().newSerializer(out), person);

        XMLParser parser = new XMLParser(converter, true);
        
        person = parser.parse(Person.class, new ByteArrayInputStream(out.toByteArray()));
        
        assertEquals(person.firstName, "Thomas");
        assertEquals(person.lastName, "Paine");
        assertEquals(person.birthYear, 1759);
    }
}
