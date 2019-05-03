/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.interfaces;

import java.util.Map;

/**
 * The base class for all chemical objects in this cdk. It provides methods for
 * adding listeners and for their notification of events, as well a a hash
 * table for administration of physical or chemical properties
 *
 *@author        egonw
 * @cdk.githash
 *@cdk.module    interfaces
 */
public interface IChemObject extends ICDKObject {

    /**
     * Use this to add yourself to this IChemObject as a listener. In order to do
     * so, you must implement the ChemObjectListener Interface.
     *
     * @param  col  the ChemObjectListener
     * @see         #removeListener
     */
    public void addListener(IChemObjectListener col);

    /**
     * Returns the number of ChemObjectListeners registered with this object.
     *
     * @return    the number of registered listeners.
     */
    public int getListenerCount();

    /**
     * Use this to remove a ChemObjectListener from the ListenerList of this
     * IChemObject. It will then not be notified of change in this object anymore.
     *
     * @param  col  The ChemObjectListener to be removed
     * @see         #addListener
     */
    public void removeListener(IChemObjectListener col);

    /**
     * Set a flag to use or not use notification. By default it should be set
     * to true.
     *
     * @param bool if true, then notification messages are sent.
     * @see        #getNotification()
     */
    public void setNotification(boolean bool);

    /**
     * Returns the flag that indicates whether notification messages are sent around.
     *
     * @return true if messages are sent.
     * @see    #setNotification(boolean)
     */
    public boolean getNotification();

    /**
     * This should be triggered by an method that changes the content of an object
     * to that the registered listeners can react to it.
     */
    public void notifyChanged();

    /**
     * This should be triggered by an method that changes the content of an object
     * to that the registered listeners can react to it. This is a version of
     * notifyChanged() which allows to propagate a change event while preserving
     * the original origin.
     *
     * @param  evt  A ChemObjectChangeEvent pointing to the source of where
     *		        the change happend
     */
    public void notifyChanged(IChemObjectChangeEvent evt);

    /**
     * Sets a property for a IChemObject.
     *
     * @param  description  An object description of the property (most likely a
     *                      unique string)
     * @param  property     An object with the property itself
     * @see                 #getProperty
     * @see                 #removeProperty
     */
    public void setProperty(Object description, Object property);

    /**
     * Removes a property for a IChemObject.
     *
     * @param  description  The object description of the property (most likely a
     *                      unique string)
     * @see                 #setProperty
     * @see                 #getProperty
     */
    public void removeProperty(Object description);

    /**
     * Returns a property for the IChemObject - the object is automatically
     * cast to the required type. This does however mean if the wrong type is
     * provided then a runtime ClassCastException will be thrown.
     *
     *
     * <pre>{@code
     *
     *     IAtom atom = new Atom("C");
     *     atom.setProperty("number", 1); // set an integer property
     *
     *     // access the property and automatically cast to an int
     *     Integer number = atom.getProperty("number");
     *
     *     // if the method is in a chain or needs to be nested the type
     *     // can be provided
     *     methodAcceptingInt(atom.getProperty("number", Integer.class));
     *
     *     // the type cannot be checked and so...
     *     String number = atom.getProperty("number"); // ClassCastException
     *
     *     // if the type is provided a more meaningful error is thrown
     *     atom.getProperty("number", String.class); // IllegalArgumentException
     *
     * }</pre>
     * @param  description  An object description of the property (most likely a
     *                      unique string)
     * @param  <T>          generic return type
     * @return              The object containing the property. Returns null if
     *                      property is not set.
     * @see                 #setProperty
     * @see                 #getProperty(Object, Class)
     * @see                 #removeProperty
     */
    public <T> T getProperty(Object description);

    /**
     * Access a property of the given description and cast the specified class.
     * 
     * <pre>{@code
     *
     *     IAtom atom = new Atom("C");
     *     atom.setProperty("number", 1); // set an integer property
     *
     *     // access the property and automatically cast to an int
     *     Integer number = atom.getProperty("number");
     *
     *     // if the method is in a chain or needs to be nested the type
     *     // can be provided
     *     methodAcceptingInt(atom.getProperty("number", Integer.class));
     *
     *     // the type cannot be checked and so...
     *     String number = atom.getProperty("number"); // ClassCastException
     *
     *     // if the type is provided a more meaningful error is thrown
     *     atom.getProperty("number", String.class); // IllegalArgumentException
     *
     * }</pre>
     * @param description description of a property (normally a string)
     * @param c           type of the value to be returned
     * @param <T>         generic type (of provided class)
     * @return the value stored for the specified description.
     * @see #getProperty(Object)
     * @see #addProperties(java.util.Map)
     */
    public <T> T getProperty(Object description, Class<T> c);

    /**
     *  Returns a Map with the IChemObject's properties.
     *
     *@return    The object's properties as an Map
     *@see       #addProperties
     */
    public Map<Object, Object> getProperties();

    /**
     * Returns the identifier (ID) of this object.
     *
     * @return    a String representing the ID value
     * @see       #setID
     */
    public String getID();

    /**
     * Sets the identifier (ID) of this object.
     *
     * @param  identifier  a String representing the ID value
     * @see                #getID
     */
    public void setID(String identifier);

    /**
     * Sets the value of some flag. The flag is a mask from a given
     * CDKConstant (e.g. {@link org.openscience.cdk.CDKConstants#ISAROMATIC}
     * or {@link org.openscience.cdk.CDKConstants#VISITED}). The flags are
     * intrinsic internal properties and should not be used to store custom
     * values, please use {@link #setProperty(Object, Object)}.
     *
     * <pre>{@code
     * // set this chem object to be aromatic
     * chemObject.setFlag(CDKConstants.ISAROMATIC, Boolean.TRUE);
     * // ...
     * // indicate we have visited this chem object
     * chemObject.setFlag(CDKConstants.VISITED, Boolean.TRUE);
     * }</pre>
     *
     * @param  mask   flag to set the value for
     * @param  value  value to assign to flag
     * @see           #getFlag
     * @see           org.openscience.cdk.CDKConstants
     */
    public void setFlag(int mask, boolean value);

    /**
     * Returns the value of a given flag. The flag is a mask from a given
     * CDKConstant (e.g. {@link org.openscience.cdk.CDKConstants#ISAROMATIC}).
     *
     * <pre>{@code
     * if(chemObject.getFlag(CDKConstants.ISAROMATIC)){
     *     // handle aromatic flag on this chem object
     * }
     * }</pre>
     *
     * @param  mask  flag to retrieve the value of
     * @return       true if the flag <code>flag_type</code> is set
     * @see          #setFlag
     * @see          org.openscience.cdk.CDKConstants
     */
    public boolean getFlag(int mask);

    /**
     * Set the properties of this object to the provided map (shallow copy). Any
     * existing properties are removed.
     *
     * @param properties map key-value pairs
     */
    public void setProperties(Map<Object, Object> properties);

    /**
     * Add properties to this object, duplicate keys will replace any existing
     * value.
     *
     * @param  properties  a Map specifying the property values
     * @see                #getProperties
     */
    public void addProperties(Map<Object, Object> properties);

    /**
     * Sets the whole set of flags. This set will iteratively invoke
     * {@link #setFlag(int, boolean)} for each value in the array and
     * use {@link org.openscience.cdk.CDKConstants#FLAG_MASKS} to look
     * up the correct mask. If only a single flag is being set it is a lot
     * faster to use {@link #setFlag(int, boolean)}.
     *
     * @param  newFlags    the new flags to set.
     * @see                #setFlag(int, boolean)
     * @see                #getFlags
     */
    public void setFlags(boolean[] newFlags);

    /**
     * Returns the whole set of flags. This method will create a new array on
     * each invocation and it is recommend you use {@link #getFlagValue()}
     * if you need all the flags. For individual flags please use {@link #getFlag(int)}
     *
     * @return    the flags.
     * @see       #setFlags
     * @see       #getFlag(int)
     * @see       #getFlagValue()
     */
    public boolean[] getFlags();

    /**
     * Access the internal value used to store the flags. The flags are stored
     * on a single numeric value and are set/cleared.
     *
     * @return numeric representation of the flags
     */
    public Number getFlagValue();

    /**
     * Returns a one line description of this IChemObject.
     *
     * @return a String representation of this object
     */
    @Override
    public String toString();

    /**
     * Returns a deep clone of this IChemObject.
     *
     * @return Object the clone of this IChemObject.
     * @throws CloneNotSupportedException if the IChemObject cannot be cloned
     */
    public Object clone() throws CloneNotSupportedException;

}
