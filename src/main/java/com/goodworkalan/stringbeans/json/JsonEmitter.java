package com.goodworkalan.stringbeans.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import com.goodworkalan.reflective.getter.Getter;
import com.goodworkalan.reflective.getter.Getters;
import com.goodworkalan.stringbeans.AbstractEmitter;
import com.goodworkalan.stringbeans.Converter;
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
    
    /**
     * Do nothing method solely to satisfy coverage in switch statement in
     * switch statement fall throughs.
     */
    private void greetingsCobertura() {
    }

    /**
     * Convert the given string into a JSON quoted string surrounded by double
     * quotes.
     * 
     * @param value
     *            The value.
     * @return The JSON quoted string.
     */
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

    /** A regular expression to match a Java identifier. */
    private final static Pattern IDENTIFIER = Pattern.compile("[_$\\w&&[\\^d]][_$\\w]+");

    /**
     * Write given bean object to the given output stream.
     * 
     * @param writer
     *            The character output stream.
     * @param bean
     *            The bean object.
     */
    @Override
    protected void emitBean(Writer writer, Object bean) throws IOException {
        try {
            writer.write('{');
            indent++;
            String separator = "\n";
            for (Getter getter : Getters.getGetters(bean.getClass()).values()) {
                writer.write(separator);
                indent(writer);
                writer.write(quote(getter.getName()));
                writer.write(':');
                emitObject(writer, get(getter, bean));
                separator = ",\n";
            }
            writer.write('\n');
            indent--;
            indent(writer);
            writer.write('}');
        } catch (IOException e) {
            throw new StringBeanException(JsonEmitter.class, "bean");
        }
    }

    /**
     * Write the given map to the given output stream is a JSON object. If an
     * entry key is a valid Java identifier, it will not be quoted in the
     * output.
     * 
     * @param writer
     *            The output stream.
     * @param map
     *            The map.
     * @throws IOException
     *             For any I/O error.
     */
    @Override
    protected void emitMap(Writer writer, Map<?, ?> map) throws IOException {
        try {
            writer.write('{');
            indent++;
            String separator = "\n";
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = entry.getKey().toString();
                writer.write(separator);
                indent(writer);
                writer.write(quote(key));
                writer.write(':');
                emitObject(writer, entry.getValue());
                separator = ",\n";
            }
            writer.write('\n');
            indent--;
            indent(writer);
            writer.write('}');
        } catch (IOException e) {
            throw new StringBeanException(JsonEmitter.class, "bean");
        }
    }

    /**
     * Emit a collection to the the given output stream as a JSON array.
     * 
     * @param writer
     *            The output stream.
     * @param collection
     *            The collection.
     * @throws IOException
     *             For any I/O exception.
     */
    @Override
    protected void emitCollection(Writer writer, Collection<?> collection) throws IOException {
        writer.write('[');
        indent++;
        String separator = "\n";
        for (Object object : collection) {
            writer.write(separator);
            indent(writer);
            emitObject(writer, object);
            separator = ",\n";
        }
        writer.write('\n');
        indent--;
        indent(writer);
        writer.write(']');
    }

    /**
     * Emit a scalar object to the given writer using the <code>Diffuser</code>
     * in the <code>Converter</code> to convert the scalar object into a
     * primitive or <code>String</code>. If the object is a <code>String</code>
     * or a <code>Character</code> it is written as a quoted JSON string,
     * otherwise it is written using the <code>toString</code> method of the
     * primitive.
     * 
     * @param writer
     *            The output stream.
     * @param object
     *            The scalar object.
     * @throws IOException
     *             For any I/O error.
     */
    @Override
    protected void emitScalar(Writer writer, Object object) throws IOException {
        object = converter.diffuser.diffuse(object);
        if (object instanceof String) {
            writer.write(quote(object.toString()));
        } else if (object instanceof Character) {
            writer.write(quote(Character.toString((Character) object)));
        } else {
            writer.write(object.toString());
        }
    }

    /**
     * Emit a JSON null value 
     */
    @Override
    protected void emitNull(Writer writer) throws IOException {
        writer.write("null");
    }

    /**
     * Emit the given object to the given output stream as a JSON string.
     * 
     * @param writer
     *            The output stream.
     * @param object
     *            The object.
     * @throws IOException
     *             For any I/O error.
     */
    public void emit(Writer writer, Object object) throws IOException {
        emitObject(writer, object);
    }
}
