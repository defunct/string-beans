package com.goodworkalan.stringbeans;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.ContentHandler;

public class XMLSerializerBuilder {
    private final Properties properties = new Properties();
    
    private final Map<String, Object> parameters = new HashMap<String, Object>();
    
    private final Map<String, Object> attributes = new HashMap<String, Object>();
    
    public XMLSerializerBuilder() {
//        properties.setProperty(OutputKeys.ENCODING, "ISO-8859-1");
        properties.setProperty(OutputKeys.INDENT, "yes");
        properties.setProperty(OutputKeys.METHOD, "xml");
        properties.setProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        attributes.put("indent-number", 2);
    }
    
    public Properties getProperties() {
        return properties;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public ContentHandler newSerializer(OutputStream out) {
        String charsetName = properties.getProperty(OutputKeys.ENCODING);
        if (charsetName == null) {
            charsetName = "UTF-8";
        }
        Writer writer;
        try {
            writer = new OutputStreamWriter(out, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new StringBeanException(XMLSerializerBuilder.class, "OutputStreamWriter", e);
        }
        StreamResult streamResult = new StreamResult(writer);
        SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        for (Map.Entry<String, Object> attribute : attributes.entrySet()) {
            tf.setAttribute(attribute.getKey(), attribute.getValue());
        }
        TransformerHandler handler;
        try {
            handler = tf.newTransformerHandler();
        } catch (TransformerConfigurationException e) {
            throw new StringBeanException(XMLSerializerBuilder.class, "newTransformerHandler", e);
        }
        for (Object name : properties.keySet()) {
            String key = name.toString();
            handler.getTransformer().setOutputProperty(key, properties.getProperty(key));
        }
        handler.setResult(streamResult);
        return handler;
    }
}
