package com.goodworkalan.stringbeans.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import com.goodworkalan.stringbeans.AbstractEmitter;
import com.goodworkalan.stringbeans.Converter;
import com.goodworkalan.stringbeans.MetaObject;
import com.goodworkalan.stringbeans.ObjectBucket;
import com.goodworkalan.stringbeans.StringBeanException;

/**
 * Writes an object graph as JSON to a character stream.  
 *
 * @author Alan Gutierrez
 */
public class JsonEmitter extends AbstractEmitter<Writer, IOException> {
    /** The current depth of the indent. */
    private int indent;

    /**
     * Write the indent to the character stream. The indent width is 2, so the
     * indent is a string of blank characters that is twice the length of the
     * current indent.
     * 
     * @throws IOException
     *             For any I/O exception.
     */
    private void indent(Writer writer) throws IOException {
        for (int i = 0; i < indent * 2; i++) {
            writer.write(' ');
        }
    }
    
    /**
     * Create a <code>JsonEmitter</code> that uses the conversion properties in
     * the given converter to write an object graph to the given
     * <code>writer</code>.
     * 
     * @param converter
     */
    public JsonEmitter(Converter converter) {
        super(converter);
    }
    
    private void greetingsCobertura() {
    }

    private String quote(String value) {
        StringBuilder quoted = new StringBuilder();
        quoted.append('"');
        char previous = '\0';
        for (int i = 0, stop = value.length(); i < stop; i++) {
            char ch = value.charAt(i);
            switch (ch) {
            case '\\':
                greetingsCobertura();
            case '"':
                quoted.append('\\');
                quoted.append(ch);
                break;
            case '/':
                if (previous == '<') {
                    quoted.append('\\');
                }
                quoted.append(ch);
                break;
            case '\t':
                quoted.append("\\t");
                break;
            case '\b':
                quoted.append("\\b");
                break;
            case '\r':
                quoted.append("\\r");
                break;
            case '\n':
                quoted.append("\\n");
                break;
            case '\f':
                quoted.append("\\f");
                break;
            default:
                if (ch < ' ' || (ch >= '\u0080' && ch < '\u00a0') || (ch >= '\u2000' && ch < '\u2100')) {
                    quoted.append(String.format("\\u%04d", Integer.toHexString(ch)));
                } else {
                    quoted.append(ch);
                }
            }
        }
        quoted.append('"');
        return quoted.toString();
    }

    private final static Pattern identifier = Pattern.compile("[_$\\w&&[\\^d]][_$\\w]+");

    /**
     * Use the given
     * <code>metaObject<code> to obtain the name and and values of the properties  properties of the given <code>bean</code>
     * as a JSON object to the given <code>writer</code>
     * 
     * @param writer
     *            The character output stream.
     * @param metaObject
     *            The meta object.
     * @param bean
     *            The bean object.
     */
    @Override
    protected void emitBean(Writer writer, MetaObject metaObject, Object bean) throws IOException {
        try {
            writer.write('{');
            indent++;
            String separator = "\n";
            for (ObjectBucket bucket : metaObject.buckets(bean)) {
                writer.write(separator);
                indent(writer);
                if (identifier.matcher(bucket.getName()).matches()) {
                    writer.write(bucket.getName());
                } else {
                    writer.write(quote(bucket.getName()));
                }
                writer.write(':');
                indent++;
                emitObject(writer, bucket.getValue());
                indent--;
                separator = ",\n";
            }
            writer.write('}');
        } catch (IOException e) {
            throw new StringBeanException(JsonEmitter.class, "bean");
        }
    }
    
    @Override
    protected void emitMap(Writer writer, Map<?, ?> map) {
        try {
            writer.write('{');
            indent++;
            String separator = "\n";
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = entry.getKey().toString();
                writer.write(separator);
                indent(writer);
                if (identifier.matcher(key).matches()) {
                    writer.write(key);
                } else {
                    writer.write(quote(key));
                }
                writer.write(':');
                indent++;
                emitObject(writer, entry.getValue());
                indent--;
                separator = ",\n";
            }
            writer.write('}');
        } catch (IOException e) {
            throw new StringBeanException(JsonEmitter.class, "bean");
        }
    }
    
    @Override
    protected void emitCollection(Writer writer, Collection<?> collection) throws IOException {
        writer.write('[');
        indent++;
        String separator = "\n";
        for (Object object : collection) {
            writer.write(separator);
            indent(writer);
            indent++;
            emitObject(writer, object);
            indent--;
            separator = ",\n";
        }
        writer.write(']');
    }

    @Override
    protected void emitScalar(Writer writer, Object object) throws IOException {
        if (object instanceof String) {
            writer.write(quote(object.toString()));
        } else {
            writer.write(object.toString());
        }
    }

    @Override
    protected void emitNull(Writer writer) throws IOException {
        writer.write("null");
    }
    
    public void emit(Writer writer, Object object) throws IOException {
        emitObject(writer, object);
    }
}
