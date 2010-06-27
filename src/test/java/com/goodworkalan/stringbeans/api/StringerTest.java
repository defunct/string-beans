package com.goodworkalan.stringbeans.api;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.testng.annotations.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.goodworkalan.stringbeans.Converter;
import com.goodworkalan.stringbeans.XMLEmitter;
import com.goodworkalan.stringbeans.XMLParser;

/**
 * Tests for XML serialization and deserialization.
 * 
 * @author Alan Gutierrez
 */
public class StringerTest {
    /** Test the parser. */
    @Test
    public void testConversion() throws SAXException {
        Person person = new Person();

        person.firstName = "Thomas";
        person.lastName = "Paine";
        person.birthYear = 1759;

        Converter converter = new Converter();
        converter.setBean(Person.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLEmitter emitter = new XMLEmitter(converter);

        emitter.emit(newSerializer(out), person);

        XMLParser parser = new XMLParser(converter, true);
        
        System.out.println(out.toString());
        
        person = parser.parse(Person.class, new ByteArrayInputStream(out.toByteArray()));
        
        assertEquals(person.firstName, "Thomas");
        assertEquals(person.lastName, "Paine");
        assertEquals(person.birthYear, 1759);
    }
    
    /**
     * Create a new transformer handler to serialize an XML document to the
     * given output stream.
     * 
     * @param out
     *            The output steam.
     * @return The SAX <code>ContentHandler</code>.
     */
    public static ContentHandler newSerializer(OutputStream out) {
        Writer writer;
        try {
            writer = new OutputStreamWriter(out, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        StreamResult streamResult = new StreamResult(writer);
        SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
        tf.setAttribute("indent-number", 2);
        TransformerHandler handler;
        try {
            handler = tf.newTransformerHandler();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
        handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
        handler.getTransformer().setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        handler.setResult(streamResult);
        return handler;
    }
}
