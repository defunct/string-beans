package com.goodworkalan.stringbeans;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Map;

/**
 * Builds an object tree by pushing elements onto and popping them from a stack
 * that represents the state of the tree during an in order traversal of of a
 * diffused object tree or the parsing of a serialized tree. The stack builds an
 * infused tree by traversing a diffused or serialized tree and pushing the
 * types and names of the objects encountered onto the stack, then popping them
 * from the stack when their child elements, if any, have been pushed and
 * popped.
 * <p>
 * The {@link CollectionParser} uses this object stack to build an infused
 * object tree from a diffused object tree, by navigating the diffused tree and
 * pushing and popping the names and serialized types of the objects onto and
 * off of this stack.
 * <p>
 * Parsers can use this stack to build an infused tree from a diffused or
 * serialized tree. The XML parser is an example of a parser that maps the
 * starting and ending of elements to the pushing and popping of names and types
 * onto and and off of this stack.
 * 
 * @author Alan Gutierrez
 */
public class ObjectStack {
    /** The conversion strategies. */
    private final Converter converter;

    /**
     * A heterogeneous container of unforeseen participants in the construction
     * of the object.
     */
    private final Map<Object, Object> participants;

    /** The stack of meta objects. */
    private final LinkedList<MetaObject> metaObjectStack = new LinkedList<MetaObject>();

    /** The last object popped from the object stack. */
    private Object lastPopped;

    /** The stack of objects. */
    private final LinkedList<Object> objectStack = new LinkedList<Object>();
    
    /** The stack of names. */
    private final LinkedList<String> nameStack = new LinkedList<String>();

    /**
     * Whether to ignore properties pushed onto the stack for a Java Bean that
     * are not defined as members of the Java Bean.
     */
    private final boolean ignoreMissing;

    /**
     * Construct an object stack that builds an object graph starting at the
     * given <code>root</code> object which is maniuplated using the given
     * <code>rootMeta</code> object.
     * 
     * @param converter
     *            The conversion strategies.
     * @param participants
     *            A heterogeneous container of unforeseen participants in the
     *            construction of the object.
     * @param rootMeta
     *            The root meta object.
     * @param root
     *            The root object.
     * @param ignoreMissing
     *            Whether to ignore properties pushed onto the stack for a Java
     *            Bean that are not defined as members of the Java Bean.
     */
    public ObjectStack(Converter converter, Map<Object, Object> participants, MetaObject rootMeta, Object root, boolean ignoreMissing) {
        this.converter = converter;
        this.participants = participants;
        this.metaObjectStack.addLast(rootMeta);
        this.objectStack.addLast(root);
        this.ignoreMissing = ignoreMissing;
    }

    /**
     * Push an object onto the stack of the given <code>type</code> that will be
     * assigned to the property in the parent object with the given
     * <code>name</code>. The <code>name</code> can be null if this is the root
     * object or if the parent element is a collection. The <code>type</code>
     * can be null if the type can be determined by reflecting upon the parent
     * object.
     * 
     * @param name
     *            The property name or <code>null</code> if this is the root of
     *            the object tree or the parent is a collection.
     * @param type
     *            The type of the object to push onto the stack or null if the
     *            type can be determined by reflecting upon the parent.
     * @return <code>true</code> if the object is actually pushed onto the
     *         stack, <code>false</code> if the property cannot be found in the
     *         parent and this <code>ObjectStack</code> is configured to ignore
     *         missing properties.
     */
    private boolean push(String name, Class<?> type) {
        MetaObject metaObject;
        Type propertyType = metaObjectStack.getLast().getPropertyType(name);
        if (propertyType == null) {
            if (ignoreMissing) {
                return false;
            }
            throw new StringBeanException(ObjectStack.class, "doesNotExist");
        } 
        if (type == null) {
            metaObject = converter.getMetaObject(propertyType);
        } else {
            Class<?> propertyClass = (propertyType instanceof Class<?>)
                                   ? (Class<?>) propertyType
                                   : (Class<?>) ((ParameterizedType) propertyType).getRawType();
            if (!propertyClass.isAssignableFrom(type)) {
                throw new StringBeanException(ObjectStack.class, "pushIsNotAssignableFrom");
            }
            metaObject = converter.getMetaObject(type);
        }
        if (!metaObject.isScalar()) {
            Object newObject = metaObject.newStackInstance();
            objectStack.add(newObject);
        }
        nameStack.addLast(name);
        metaObjectStack.add(metaObject);
        lastPopped = null;
        return true;
    }

    /**
     * Push a <code>MetaObject</code> for an object of the given
     * <code>className</code> onto the stack that will be assigned to property
     * with the given <code>name</code> in the parent object. Both the
     * <code>name</code> and <code>className</code> can be null.
     * <p>
     * If the type of object is a scalar object, the name and
     * <code>MetaObject</code> for the scalar object are added to the stack. The
     * actual scalar object is created by calling the <code>String</code>
     * argument version of the {@link #pop(String) pop} method.
     * <p>
     * If the type of object is a container object, a map, list or bean, the
     * <code>MetaObject</code> will create a new object on the object stack to
     * collect the properties. If the object is a list, it will create a list.
     * If it is a map or a bean, it will create a map, since actual bean object
     * will only be created when the <code>MetaObject</code> is popped.
     * <p>
     * By creating beans only when the bean is popped, we give the
     * <code>MetaObject</code> an opportunity to create the bean based on the
     * bean properties. For example, this is used by <code>UpaMetaBean</code> in
     * the String Beans JPA library to extract the bean from a database using
     * bean properties as database keys, if the bean already exists in a
     * database.
     * <p>
     * The <code>name</code> can be null for the root object in the stack or for
     * objects that are elements in a collection. The <code>className</code> can
     * be null when the class of the property can be determined by the
     * <code>MetaObject</code> implementations introspection of the
     * <code>Object</code>. That is, the <code>className</code> can be null if
     * the type can be determined through reflection on the parent
     * <code>Object</code>.
     * 
     * @param name
     *            The property name or <code>null</code> if this is the root of
     *            the object tree or the parent is a collection.
     * @param className
     *            The type of the object to push onto the stack or null if the
     *            type can be determined by reflecting upon the parent.
     * @return <code>true</code> if the object is actually pushed onto the
     *         stack, <code>false</code> if the property cannot be found in the
     *         parent and this <code>ObjectStack</code> is configured to ignore
     *         missing properties.
     */
    public boolean push(String name, String className) {
        if (className == null) {
            return push(name, (Class<?>) null);
        }
        return push(name, forName(className));
    }

    /**
     * Get whether or not the type of the last object pushed onto the stack is a
     * scalar, that is if the type of the last object pushed onto the stack will
     * be converted from a <code>String</code> or primitive type.
     * 
     * @return <code>true</code> if the last object pushed onto the stack is a
     *         scalar.
     */
    public boolean isScalar() {
        return metaObjectStack.getLast().isScalar();
    }

    /**
     * Load the class with the given <code>className</code>.
     * 
     * @param className
     *            The class name.
     * @return The class.
     * @exception StringBeanException
     *                If the class named by the given <code>className</code>
     *                cannot be found.
     */
    Class<?> forName(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new StringBeanException(ObjectStack.class, "forName", e);
        }
    }
    
    /**
     * Get the last container object pushed onto the object stack.
     * 
     * @return The last container pushed.
     */
    public Object getLastContainerPushed() {
        return objectStack.getLast();
    }

    /**
     * Pop a scalar object from the stack, converting the scalar object and
     * assigning it as property to the container below it on the stack.
     * 
     * @param string
     *            The string representation of the scalar value.
     */
    public void pop(String string) {
        MetaObject scalar = metaObjectStack.removeLast();
        String pushedName = nameStack.removeLast();
        Object value = converter.fromString(scalar.getObjectClass(), string);
        metaObjectStack.getLast().set(objectStack.getLast(), pushedName, value);
        lastPopped = value;
    }

    /**
     * Pop a container object from the top of stack, converting a possible place
     * holder container to a bean using the meta object.
     * <p>
     * If the meta object needs container properties in order to determine how
     * to build the actual bean object, then the meta object can push a place
     * holder map onto the object stack. Popping the stack will create an actual
     * bean using the place holder map.
     */
    public void pop() {
        MetaObject metaObject = metaObjectStack.removeLast();
        lastPopped = metaObject.newInstance(participants, objectStack.removeLast());
        metaObjectStack.getLast().set(objectStack.getLast(), nameStack.removeLast(), lastPopped);
    }

    /**
     * Get the last object popped off of the object stack.
     * 
     * @return THe last popped object.
     */
    public Object getLastPopped() {
        return lastPopped;
    }
}
