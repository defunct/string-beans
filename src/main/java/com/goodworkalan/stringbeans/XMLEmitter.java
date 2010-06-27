package com.goodworkalan.stringbeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.goodworkalan.reflective.getter.Getter;
import com.goodworkalan.reflective.getter.Getters;
import com.goodworkalan.utility.Primitives;

/**
 * Writes a String Beans object graph to an SAX content handler.
 * 
 * @author Alan Gutierrez
 */
public class XMLEmitter extends AbstractEmitter<ContentHandler, SAXException> {
    /**
     * Create an XML emitter using the given String Beans configuration.
     * 
     * @param converter
     *            The String Beans configuration.
     */
    public XMLEmitter(Converter converter) {
        super(converter);
    }

    /**
     * Start an XML element in the output stream.
     * 
     * @param handler
     *            The content handler.
     * @param name
     *            The element name.
     * @param attr
     *            A list of name value pairs.
     * @throws SAXException
     *             For any error, any error at all.
     */
    private void startElement(ContentHandler handler, String name, String...attr) throws SAXException {
        AttributesImpl attributes = new AttributesImpl();
        for (int i = 0; i < attr.length; i += 2) {
            attributes.addAttribute("", attr[i], attr[i], "CDATA", attr[i + 1]);
        }
        handler.startElement("", name, name, attributes);
    }

    /**
     * End an XML element.
     * 
     * @param handler
     *            The content handler.
     * @param name
     *            The element name.
     * @throws SAXException
     *             For any error, any error at all.
     */
    private void endElement(ContentHandler handler, String name) throws SAXException {
        handler.endElement("", name, name);
    }

    /**
     * Emit a map as a map element that surrounds entry elements. The entry
     * element contains the value and indicates the key using a key attribute.
     * If the value is null a null attribute is added to the entry element.
     * 
     * @param handler
     *            The XML content handler.
     * @param map
     *            The map.
     * @throws SAXException
     *             For any error, any error at all.
     */
    @Override
    protected void emitMap(ContentHandler handler, Map<?, ?> map) throws SAXException {
        Class<?> type = map.getClass();
        if (SortedMap.class.isAssignableFrom(type) || !Map.class.isAssignableFrom(type)) {
            startElement(handler, "map", "type", type.getCanonicalName());
        } else {
            startElement(handler, "map");
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
        endElement(handler, "map");
    }

    /**
     * Emit a map as a bean element that surrounds elements named for the bean
     * members using the Java Bean name for the member. The value is written as
     * a child of the member. If the value is null a null attribute is added to
     * the member element.
     * 
     * @param handler
     *            The XML content handler.
     * @param metaObject
     *            The meta object.
     * @param object
     *            The object.
     * @throws SAXException
     *             For any error, any error at all.
     */
    @Override
    protected void emitBean(ContentHandler handler, Object object) throws SAXException {
        MetaObject metaObject = converter.getMetaObject(object.getClass());
        Class<?> type = metaObject.getObjectClass();
        if (metaObject.getObjectClass().equals(type)) {
            startElement(handler, "bean");
        } else {
            startElement(handler, "bean", "class", object.getClass().getName());
        }
        for (Getter getter : Getters.getGetters(metaObject.getObjectClass()).values()) {
            if ("class".equals(getter.getName())) {
                continue;
            }
            Object value = get(getter, object);
            if (value == null) {
                startElement(handler, getter.getName(), "null", "true");
            } else {
                if (!Primitives.box(getter.getType()).equals(value.getClass())) {
                    startElement(handler, getter.getName(), "class", value.getClass().getCanonicalName());
                } else {
                    startElement(handler, getter.getName());
                }
                emitObject(handler, value);
            }
            endElement(handler, getter.getName());
        }
        endElement(handler, "bean");
    }

    /**
     * Emit a collection as a collection element that surrounds item elements.
     * If the value is null a null attribute is added to the item element.
     * 
     * @param handler
     *            The XML content handler.
     * @param collection
     *            The collection.
     * @throws SAXException
     *             For any error, any error at all.
     */
    @Override
    protected void emitCollection(ContentHandler handler, Collection<?> collection) throws SAXException {
        Class<?> type = collection.getClass();
        if (ArrayList.class.equals(type)) {
            startElement(handler, "collection");
        } else {
            startElement(handler, "collection", "type", type.getCanonicalName());
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
        endElement(handler, "collection");
    }

    /**
     * Emit a scalar as a string after converting the scalar to a string or
     * primitive using the <code>Diffuser</code> of the String Beans
     * <code>Converter</code> configuration object.
     * 
     * @param handler
     *            The XML content handler.
     * @param collection
     *            The collection.
     * @throws SAXException
     *             For any error, any error at all.
     */
    @Override
    protected void emitScalar(ContentHandler handler, Object object) throws SAXException {
        char[] chars = converter.toString(object).toCharArray();
        handler.characters(chars, 0, chars.length);
    }

    /**
     * This method is not actually used by the emitter, since null is indicated
     * by setting a null attribute on the element.
     * 
     * @param handler
     *            The content handler.
     */
    @Override
    protected void emitNull(ContentHandler handler) {
    }

    /**
     * Emit the given String Beans object graph as a String Beans XML document
     * to the the given SAX <code>ContentHandler</code>.
     * 
     * @param handler
     *            The XML content handler.
     * @param object
     *            THe object graph.
     * @throws SAXException
     *             For any error, any error at all.
     */
    public void emit(ContentHandler handler, Object object) throws SAXException {
        handler.startDocument();
        emitObject(handler, object);
        handler.endDocument();
    }
}
