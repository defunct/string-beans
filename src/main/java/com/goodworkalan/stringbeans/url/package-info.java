/**
 * <p>
 * A list is specified by adding an integer index to a field name. If a
 * <code>Person</code> has a list of <code>PhoneNumber</code> objects, the number
 * of the phone number can be set using the form field name
 * <code>person[phoneNumbers][0][number]</code>. A second phone number can be set
 * using the form field name <code>person[phoneNumbers][1][number]</code>.
 * </p>
 * 
 * <p>
 * Indexes can be skipped. This makes it easier to program JavaScript forms that
 * add and remove nested objects from a parent object. The index of a new element
 * needs simply to be larger than any index given so far.
 * </p>
 * 
 * <p>
 * Skipped indexes are collapsed, so that the resulting collection is contiguous.
 * The following form will create person with two phone numbers, one after another.
 * </p>
 * 
 * <pre>
 * &lt;input name=&quot;person[phoneNumbers][1][number]&quot; &gt;&lt;br&gt;
 * &lt;input name=&quot;person[phoneNumbers][3][number]&quot;&gt;
 * </pre>
 * 
 * <p>
 * If you really do want to have elements <code>0</code> and <code>2</code> added
 * to the list of phone numbers as <code>null</code> values, you can explicitly set
 * them to null.
 * </p>
 * 
 * <p>
 * In the example list below, we set a null value for the first element in the
 * list, but still leave the third element unspecified, so that the list collapses
 * to a three element collection.
 * </p>
 * 
 * <pre>
 * &lt;input name=&quot;person[phoneNumbers][1][number]&quot; &gt;&lt;br&gt;
 * &lt;input name=&quot;person[phoneNumbers][3][number]&quot;&gt;
 * &lt;input name=&quot;person[phoneNumbers][0][number]&quot; type=&quot;hidden&quot;&gt;
 * </pre>
 */
package com.goodworkalan.stringbeans.url;
