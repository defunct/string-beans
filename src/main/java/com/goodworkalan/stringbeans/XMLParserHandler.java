package com.goodworkalan.stringbeans;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.goodworkalan.stash.Stash;

public class XMLParserHandler extends DefaultHandler {
    private final ObjectStack objectStack;

    private final StringBuilder characters = new StringBuilder();
    
    public XMLParserHandler(Stringer stringer, Stash stash, MetaObject metaRoot, Object root, boolean ignoreMissing) {
        this.objectStack = new ObjectStack(stringer, stash, metaRoot, root, ignoreMissing);
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
