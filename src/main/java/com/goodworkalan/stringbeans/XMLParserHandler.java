package com.goodworkalan.stringbeans;

import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX handler that converts an XML document in the String Beans XML format
 * into a String Beans object graph.
 *
 * @author Alan Gutierrez
 */
public class XMLParserHandler extends DefaultHandler {
    /** The object stack. */
    private final ObjectStack objectStack;

    /** The character buffer. */
    private final StringBuilder characters = new StringBuilder();

    /**
     * Create a SAX handler that reads a String Beans XML document.
     * 
     * @param converter
     *            The String Beans definition.
     * @param participants
     *            The heterogeneous container of unforeseen participants in the
     *            construction of the object.
     * @param metaRoot
     *            The meta root object.
     * @param root
     *            The root object.
     * @param ignoreMissing
     *            Whether to ignore elements defined in the document that are
     *            not defined as members in the deserialized Java Bean.
     */
    public XMLParserHandler(Converter converter, Map<Object, Object> participants, MetaObject metaRoot, Object root, boolean ignoreMissing) {
        this.objectStack = new ObjectStack(converter, participants, metaRoot, root, ignoreMissing);
    }

    /**
     * Get the object graph created by the last parsed document handled by this
     * handler.
     * 
     * @return The object graph.
     */
    public Object getObject() {
        return objectStack.getLastPopped();
    }

    /**
     * Start an XML element.
     * 
     * @param uri
     *            The namespace URI.
     * @param localName
     *            The element local name.
     * @param qName
     *            The qualified element name.
     * @param attributes
     *            The element attributes.
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        String className = attributes.getValue("class");
        objectStack.push(localName, className);
    }

    /**
     * Gather the text content into the character buffer.
     * 
     * @param ch
     *            The SAX parser character buffer.
     * @param start
     *            The start index of the character range.
     * @param length
     *            The character range.
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        if (objectStack.isScalar()) {
            characters.append(ch, start, length);
        }
    }

    /**
     * End an XML element.
     * 
     * @param uri
     *            The namespace URI.
     * @param localName
     *            The element local name.
     * @param qName
     *            The qualified element name.
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        if (objectStack.isScalar()) {
            objectStack.pop(characters.toString());
            characters.setLength(0);
        } else {
            objectStack.pop();
        }
    }
}
