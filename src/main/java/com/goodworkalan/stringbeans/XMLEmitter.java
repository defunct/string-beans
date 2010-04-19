package com.goodworkalan.stringbeans;
import static com.goodworkalan.stringbeans.Converter.box;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMLEmitter extends AbstractEmitter {
    private final ContentHandler handler;

    public XMLEmitter(Stringer stringer, ContentHandler handler) {
        super(stringer);
        this.handler = handler;
    }

    private void startElement(String name, String...attr) {
        AttributesImpl attributes = new AttributesImpl();
        for (int i = 0; i < attr.length; i += 2) {
            attributes.addAttribute("", attr[i], attr[i], "CDATA", attr[i + 1]);
        }
        try {
            handler.startElement("", name, name, attributes);
        } catch (SAXException e) {
            throw new StringBeanException(XMLEmitter.class, "startElement", e);
        }
    }
    
    private void endElement(String name) {
        try {
            handler.endElement("", name, name);
        } catch (SAXException e) {
            throw new StringBeanException(XMLEmitter.class, "endElement", e);
        }
    }
    
    @Override
    protected void emitDictionary(Class<?> type, Map<?, ?> map) {
        if (SortedMap.class.isAssignableFrom(type) || !Map.class.isAssignableFrom(type)) {
            startElement("dictionary", "type", type.getCanonicalName());
        } else {
            startElement("dictionary");
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            if (entry.getValue() == null) {
                startElement("entry", "name", key, "null", "true");
            } else {
                startElement("entry", "name", key);
                expandObject(entry.getValue());
            }
            endElement("entry");
        }
        endElement("dictionary");
    }
    
    private void emitBeanGuts(MetaObject metaObject, Object object) {
        for (ObjectBucket bucket : metaObject.buckets(object)) {
            if (bucket.getValue() == null) {
                startElement(bucket.getName(), "null", "true");
            } else {
                if (!box(bucket.getPropertyType()).equals(bucket.getValue().getClass())) {
                    startElement(bucket.getName(), "class", bucket.getValue().getClass().getCanonicalName());
                } else {
                    startElement(bucket.getName());
                }
                expandObject(bucket.getValue());
            }
            endElement(bucket.getName());
        }
    }

    @Override
    protected void emitBean(Class<?> objectClass, MetaObject metaObject, Object object) {
        if (metaObject.getObjectClass().equals(objectClass)) {
            startElement("bean");
        } else {
            startElement("bean", "class", object.getClass().getCanonicalName());
        }
        emitBeanGuts(metaObject, object);
        endElement("bean");
    }
    
    @Override
    protected void emitSeries(Class<?> type, Collection<?> collection) {
        if (ArrayList.class.equals(type)) {
            startElement("series");
        } else {
            startElement("series", "type", type.getCanonicalName());
        }
        for (Object o : collection) {
            if (o == null) {
                startElement("item", "null", "true");
            } else {
                startElement("item");
                expandObject(o);
            }
            endElement("item");
        }
        endElement("series");
    }
    
    @Override
    protected void emitScalar(Class<?> type, Object object) {
        char[] chars = object.toString().toCharArray();
        try {
            handler.characters(chars, 0, chars.length);
        } catch (SAXException e) {
            throw new StringBeanException(XMLEmitter.class, "characters", e);
        }
    }
    
    @Override
    protected void emitNull() {
    }

    public void emit(Object object) {
        try {
            handler.startDocument();
        } catch (SAXException e) {
            throw new StringBeanException(XMLEmitter.class, "startDocument", e);
        }
        expandObject(object);
        try {
            handler.endDocument();
        } catch (SAXException e) {
            throw new StringBeanException(XMLEmitter.class, "endDocument", e);
        }
    }
}
