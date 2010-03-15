package com.goodworkalan.stringbeans;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParserHandler extends DefaultHandler {
    private final ObjectStack objectStack;

    private final StringBuilder characters = new StringBuilder();

    public XMLParserHandler(Stringer stringer, MetaObject metaRoot, Object root) {
        this.objectStack = new ObjectStack(stringer, metaRoot, root);
    }
    
    public Object getObject() {
        return objectStack.getLastPopped();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        String className = attributes.getValue("class");
        objectStack.push(localName, className);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (objectStack.isScalar()) {
            characters.append(ch, start, length);
        }
    }

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
