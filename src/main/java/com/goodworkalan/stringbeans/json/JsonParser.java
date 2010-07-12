package com.goodworkalan.stringbeans.json;

import static com.goodworkalan.stringbeans.StringBeanException._;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Parse a JSON character sequence.
 *
 * @author Alan Gutierrez
 */
public class JsonParser {
    /** The character sequence. */
    private final CharSequence chars;
    
    /** The index. */
    private int index;
    
    /** The current line. */
    private int line = 1;
    
    /** The current character. */
    private int character = 1;
    
    /**
     * Parses the given sequence of characters.
     * 
     * @param chars The character sequence.
     */
    public JsonParser(CharSequence chars) {
        this.chars = chars;
    }

    /**
     * Read an object or list from the JSON character sequence.
     * 
     * @return An object or array.
     */
    public Object read() {
        skipwhite("[{", "objectOrArray");
        if (chars.charAt(index) == '[') {
            return array();
        }
        return object();
    }
    
    /**
     * Read an array from the JSON character sequence.
     * 
     * @return An array.
     */
    public List<Object> array() {
        List<Object> list = new ArrayList<Object>();
        skipwhite("[", "array");
        do {
            forward();
            list.add(anything());
            skipwhite(",]", "endOfArray");
        } while (chars.charAt(index) == ',');
        index++;
        return list;
    }
    
    /**
     * Read a map from the JSON character sequence.
     * 
     * @return A map.
     */
    public Map<String, Object> object() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        skipwhite("{", "map");
        do {
            forward();
            skipwhite("\"", "mapKey");
            String key = string();
            skipwhite(":", "mapSeparator");
            forward();
            map.put(key, anything());
            skipwhite(",}", "endOfMap");
        } while (chars.charAt(index) == ',');
        index++;
        return map;
    }

    /**
     * Read an object value or array element from the chracater stream.
     * 
     * @return An instance of any type.
     */
    private Object anything() {
        skipwhite("-tfn0123456789\"{[", "anything");
        switch (chars.charAt(index)) {
        case 't':
            read("true");
            return true;
        case 'f':
            read("false");
            return false;
        case 'n':
            read("null");
            return null;
        case '"':
            return string();
        case '[':
            return array();
        case '{':
            return object();
        }
        return number();
    }

    /**
     * Read a JSON number from the character stream.
     * 
     * @return A JSON number.
     * @exception IllegalArgumentException
     *                If a number cannot be read from the character stream.
     * @exception IndexOutOfBoundsException
     *                If the end of the character stream is reached
     *                unexpectedly.
     */
    private String string() {
        StringBuilder string = new StringBuilder();
        char ch;
        while ((ch = forward()) != '"') {
            if (ch == '\\') {
                switch (forward()) {
                case '"':
                    string.append('"');
                    break;
                case '\\':
                    string.append('\\');
                    break;
                case '/':
                    string.append('/');
                    break;
                case 'b':
                    string.append("\b");
                    break;
                case 'f':
                    string.append("\f");
                    break;
                case 'n':
                    string.append("\n");
                    break;
                case 'r':
                    string.append("\r");
                    break;
                case 't':
                    string.append("\t");
                    break;
                case 'u':
                    char[] unicode = new char[4];
                    for (int i = 0, stop = unicode.length; i < stop; i++) {
                        unicode[i] = forward(); 
                    }
                    string.append((char) Integer.parseInt(new String(unicode), 16));
                    break;
                }
            } else {
                string.append(ch);
            }
        }
        forward();
        return string.toString();
    }
    
    /**
     * Throw an <code>IllegalArgumentException</code> message for a missing
     * expected type in the JSON character stream.
     * 
     * @param type
     *            The message code for the type.
     */
    private void expecting(String type) {
        throw new IllegalArgumentException(_(JsonParser.class, "expecting", _(JsonParser.class, type), line, character));
    }

    /**
     * Read a JSON number from the character stream.
     * 
     * @return A JSON number.
     * @exception IllegalArgumentException
     *                If a number cannot be read from the character stream.
     * @exception IndexOutOfBoundsException
     *                If the end of the character stream is reached
     *                unexpectedly.
     */
    private Number number() {
        StringBuilder number = new StringBuilder();
        char ch = chars.charAt(index);
        number.append(ch);
        if (ch == '-') {
            ch = forward();
            if (!Character.isDigit(ch)) {
                expecting("digit");
            }
            number.append(ch);
        }
        if (ch == '0') {
            ch = forward();
            if (ch != '.') {
                expecting("decimalPoint");
            }
        } else {
            if (!Character.isDigit(ch)) {
                expecting("digit");
            }
            while (Character.isDigit(ch = forward())) {
                number.append(ch);
            }
        }
        boolean whole = true;
        if (ch == '.') {
            whole = false;
            number.append(ch);
            while (Character.isDigit(ch = forward())) {
                number.append(ch);
            }
        }
        if ("eE".indexOf(ch) != -1) {
            whole = false;
            number.append(ch);
            while (Character.isDigit(ch = forward())) {
                number.append(ch);
            }
        }
        if (whole) {
            return Long.parseLong(number.toString());
        }
        return Double.parseDouble(number.toString());
    }

    /**
     * Read a JSON literal from the character stream asserting that it does
     * exist in the stream.
     * 
     * @param literal
     *            The literal.
     * @exception IllegalArgumentException
     *                If a literal cannot be read from the character stream.
     * @exception IndexOutOfBoundsException
     *                If the end of the character stream is reached
     *                unexpectedly.
     */
    private void read(String literal) {
        for (int i = 0, stop = literal.length(); i < stop; i++) {
            if (chars.charAt(index) != literal.charAt(i)) {
                throw new IllegalArgumentException(_(JsonParser.class, "expecting", "value", line, character));
            }
            forward();
        }
    }
    
    /**
     * Skip white space until one of the given characters is located.
     * 
     * @param sought
     *            The characters sought.
     * @param type
     *            The message code for the type sought.
     */
    private void skipwhite(String sought, String type) {
        while (Character.isWhitespace(chars.charAt(index))) {
            if (chars.charAt(index) == '\n') {
                line++;
                character = 0;
            }
            character++;
            index++;
        }
        if (sought.indexOf(chars.charAt(index)) == -1) {
            throw new IllegalArgumentException(_(JsonParser.class, "unexpected", _(JsonParser.class, type), line, character));
        }
    }

    /**
     * Move the index forward and update the line and character position.
     * 
     * @return The character at the new position.
     */
    private char forward() {
        index++;
        character++;
        return chars.charAt(index);
    }
}
