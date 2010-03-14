package com.goodworkalan.stringbeans;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLParser extends Parser {
    private final Stringer stringer;
    
    public XMLParser(Stringer stringer) {
        this.stringer = stringer;
    }

    public Object parse(InputStream in) {
        XMLReader xr;
        try {
            xr = XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            throw new StringBeanException(XMLParser.class, "createXMLReader", e);
        }
        XMLParserHandler handler = new XMLParserHandler(stringer);
        xr.setContentHandler(handler);
        try {
            xr.parse(new InputSource(in));
        } catch (IOException e) {
            throw new StringBeanException(XMLParser.class, "parseException", e);
        } catch (SAXException e) {
            throw new StringBeanException(XMLParser.class, "parseException", e);
        }
        return handler.getObject();
    }
}
