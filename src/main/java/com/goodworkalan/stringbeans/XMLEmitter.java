package com.goodworkalan.stringbeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.goodworkalan.utility.Primitives;

public class XMLEmitter extends AbstractEmitter<ContentHandler, SAXException> {
    public XMLEmitter(Converter converter) {
        super(converter);
    }

    private void startElement(ContentHandler handler, String name, String...attr) throws SAXException {
        AttributesImpl attributes = new AttributesImpl();
        for (int i = 0; i < attr.length; i += 2) {
            attributes.addAttribute("", attr[i], attr[i], "CDATA", attr[i + 1]);
        }
        handler.startElement("", name, name, attributes);
    }
    
    private void endElement(ContentHandler handler, String name) throws SAXException {
        handler.endElement("", name, name);
    }
    
    @Override
    protected void emitMap(ContentHandler handler, Map<?, ?> map) throws SAXException {
        Class<?> type = map.getClass();
        if (SortedMap.class.isAssignableFrom(type) || !Map.class.isAssignableFrom(type)) {
            startElement(handler, "dictionary", "type", type.getCanonicalName());
        } else {
            startElement(handler, "dictionary");
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            if (entry.getValue() == null) {
                startElement(handler, "entry", "name", key, "null", "true");
            } else {
                startElement(handler, "entry", "name", key);
                emitObject(handler, entry.getValue());
            }
            endElement(handler, "entry");
        }
        endElement(handler, "dictionary");
    }
    
    @Override
    protected void emitBean(ContentHandler handler, MetaObject metaObject, Object object) throws SAXException {
        Class<?> type = metaObject.getObjectClass();
        if (metaObject.getObjectClass().equals(type)) {
            startElement(handler, "bean");
        } else {
            startElement(handler, "bean", "class", object.getClass().getName());
        }
        for (ObjectBucket bucket : metaObject.buckets(object)) {
            if (bucket.getValue() == null) {
                startElement(handler, bucket.getName(), "null", "true");
            } else {
                if (!Primitives.box((Class<?>) bucket.getPropertyType()).equals(bucket.getValue().getClass())) {
                    startElement(handler, bucket.getName(), "class", bucket.getValue().getClass().getCanonicalName());
                } else {
                    startElement(handler, bucket.getName());
                }
                emitObject(handler, bucket.getValue());
            }
            endElement(handler, bucket.getName());
        }
        endElement(handler, "bean");
    }
    
    @Override
    protected void emitCollection(ContentHandler handler, Collection<?> collection) throws SAXException {
        Class<?> type = collection.getClass();
        if (ArrayList.class.equals(type)) {
            startElement(handler, "series");
        } else {
            startElement(handler, "series", "type", type.getCanonicalName());
        }
        for (Object o : collection) {
            if (o == null) {
                startElement(handler, "item", "null", "true");
            } else {
                startElement(handler, "item");
                emitObject(handler, o);
            }
            endElement(handler, "item");
        }
        endElement(handler, "series");
    }
    
    @Override
    protected void emitScalar(ContentHandler handler, Object object) throws SAXException {
        char[] chars = converter.toString(object).toCharArray();
        handler.characters(chars, 0, chars.length);
    }
    
    @Override
    protected void emitNull(ContentHandler handler) {
    }

    public void emit(ContentHandler handler, Object object) throws SAXException {
        handler.startDocument();
        emitObject(handler, object);
        handler.endDocument();
    }
}
