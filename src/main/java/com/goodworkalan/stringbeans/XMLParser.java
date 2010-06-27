package com.goodworkalan.stringbeans;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.goodworkalan.stash.Stash;

/**
 * Parse a String Beans XML document to create a String Beans object graph.
 * 
 * @author Alan Gutierrez
 */
public class XMLParser {
    /** The String Beans configuration. */
    private final Converter converter;

    /**
     * The heterogeneous container of unforeseen participants in the
     * construction of the object.
     */
    private final Stash stash;

    /**
     * Whether to ignore members defined in the XML that are missing as members
     * in Java Bean type.
     */
    private final boolean ignoreMissing;

    /**
     * Create an XML parser using the given String Beans configuration.
     * 
     * @param converter
     *            The String Beans configuration.
     * @param ignoreMissing
     *            Whether to ignore members defined in the XML that are missing
     *            as members in Java Bean type.
     */
    public XMLParser(Converter converter, boolean ignoreMissing) {
        this.converter = converter;
        this.stash = new Stash();
        this.ignoreMissing = ignoreMissing;
    }

    /**
     * Get the heterogeneous container of unforeseen participants in the
     * construction of the object.
     * 
     * @return The heterogeneous container of unforeseen participants in the
     *         construction of the object.
     */
    public Stash getStash() {
        return stash;
    }

    /**
     * Parse the given XML document using the given root class as the root type
     * in String Beans object graph.
     * 
     * @param <T>
     *            The root type of the object graph.
     * @param rootClass
     *            The root class of the object graph.
     * @param in
     *            The XML input stream.
     * @return The root object of the parsed object graph.
     */
    public <T> T parse(Class<T> rootClass, InputStream in) {
        XMLReader xr;
        try {
            xr = XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            throw new StringBeanException(XMLParser.class, "createXMLReader", e);
        }
        Object[] reference = new Object[1];
        XMLParserHandler handler = new XMLParserHandler(converter, stash, new MetaRoot<T>(rootClass), reference, ignoreMissing);
        xr.setContentHandler(handler);
        try {
            xr.parse(new InputSource(in));
        } catch (IOException e) {
            throw new StringBeanException(XMLParser.class, "parseException", e);
        } catch (SAXException e) {
            throw new StringBeanException(XMLParser.class, "parseException", e);
        }
        return rootClass.cast(reference[0]);
    }
}
