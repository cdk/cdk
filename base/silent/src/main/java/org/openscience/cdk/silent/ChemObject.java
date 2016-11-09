/*  Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.silent;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Objects;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

/**
 *  The base class for all chemical objects in this cdk. It provides methods for
 *  adding listeners and for their notification of events, as well a a hash
 *  table for administration of physical or chemical properties
 *
 *@author        steinbeck
 * @cdk.githash
 *@cdk.module    silent
 */
public class ChemObject implements Serializable, IChemObject, Cloneable {

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
     */
    private static final long   serialVersionUID = 2798134548764323328L;

    /**
     *  A hashtable for the storage of any kind of properties of this IChemObject.
     */
    private Map<Object, Object> properties;
    /**
     *  You will frequently have to use some flags on a IChemObject. For example, if
     *  you want to draw a molecule and see if you've already drawn an atom, or in
     *  a ring search to check whether a vertex has been visited in a graph
     *  traversal. Use these flags while addressing particular positions in the
     *  flag array with self-defined constants (flags[VISITED] = true). 100 flags
     *  per object should be more than enough.
     */
    private short               flags;                                  // flags are currently stored as a single short value MAX_FLAG_INDEX < 16

    /**
     *  The ID is null by default.
     */
    private String              identifier;

    /**
     *  Constructs a new IChemObject.
     */
    public ChemObject() {
        properties = null;
        identifier = null;
    }

    /**
     * Constructs a new IChemObject by copying the flags, and the
     * identifier. It does not copy the listeners and properties.
     *
     * @param chemObject the object to copy
     */
    public ChemObject(IChemObject chemObject) {
        // copy the flags
        flags = chemObject.getFlagValue().shortValue();
        // copy the identifier
        identifier = chemObject.getID();
    }

    /**
     *  Use this to add yourself to this IChemObject as a listener. In order to do
     *  so, you must implement the ChemObjectListener Interface.
     *
     *@param  col  the ChemObjectListener
     *@see         #removeListener
     */
    @Override
    public void addListener(IChemObjectListener col) {}

    /**
     *  Returns the number of ChemObjectListeners registered with this object.
     *
     *@return    the number of registered listeners.
     */
    @Override
    public int getListenerCount() {
        return 0;
    }

    /**
     *  Use this to remove a ChemObjectListener from the ListenerList of this
     *  IChemObject. It will then not be notified of change in this object anymore.
     *
     *@param  col  The ChemObjectListener to be removed
     *@see         #addListener
     */
    @Override
    public void removeListener(IChemObjectListener col) {}

    /**
     *  This should be triggered by an method that changes the content of an object
     *  to that the registered listeners can react to it.
     */
    @Override
    public void notifyChanged() {}

    /**
     *  This should be triggered by an method that changes the content of an object
     *  to that the registered listeners can react to it. This is a version of
     *  notifyChanged() which allows to propagate a change event while preserving
     *  the original origin.
     *
     *@param  evt  A ChemObjectChangeEvent pointing to the source of where
     *		the change happend
     */
    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {}

    /**
     * Lazy creation of properties hash.
     *
     * @return    Returns in instance of the properties
     */
    private Map<Object, Object> lazyProperties() {
        if (properties == null) {
            properties = new LinkedHashMap<>(4);
        }
        return properties;
    }

    /**
     *  Sets a property for a IChemObject.
     *
     *@param  description  An object description of the property (most likely a
     *      unique string)
     *@param  property     An object with the property itself
     *@see                 #getProperty
     *@see                 #removeProperty
     */
    @Override
    public void setProperty(Object description, Object property) {
        lazyProperties().put(description, property);
    }

    /**
     *  Removes a property for a IChemObject.
     *
     *@param  description  The object description of the property (most likely a
     *      unique string)
     *@see                 #setProperty
     *@see                 #getProperty
     */
    @Override
    public void removeProperty(Object description) {
        if (properties != null) {
            properties.remove(description);
            if (properties.isEmpty())
                properties = null;
        }
    }

    /**
     *  Returns a property for the IChemObject. The value will be cast to the
     *  required return type.
     *
     *@param  description  An object description of the property (most likely a
     *      unique string)
     *@return              The object containing the property. Returns null if
     *      propert is not set.
     *@see                 #setProperty
     *@see                 #removeProperty
     */
    @Override
    public <T> T getProperty(Object description) {
        if (properties == null) return null;
        // can't check the type
        @SuppressWarnings("unchecked")
        T value = (T) lazyProperties().get(description);
        return value;
    }

    /**
     * @inheritDoc
     */
    @Override
    public <T> T getProperty(Object description, Class<T> c) {
        Object value = lazyProperties().get(description);

        if (c.isInstance(value)) {

            @SuppressWarnings("unchecked")
            T typed = (T) value;
            return typed;

        } else if (value != null) {
            throw new IllegalArgumentException("attempted to access a property of incorrect type, expected "
                    + c.getSimpleName() + " got " + value.getClass().getSimpleName());
        }

        return null;

    }

    /**
     *  Returns a Map with the IChemObject's properties.
     *
     *@return    The object's properties as an Hashtable
     *@see       #addProperties
     */
    @Override
    public Map<Object, Object> getProperties() {
        return properties == null ? Collections.emptyMap()
                                  : Collections.unmodifiableMap(properties);
    }

    /**
     *  Clones this <code>IChemObject</code>. It clones the identifier, flags,
     *  properties and pointer vectors. The ChemObjectListeners are not cloned, and
     *  neither is the content of the pointer vectors.
     *
     *@return    The cloned object
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ChemObject clone = (ChemObject) super.clone();
        // clone the flags
        clone.flags = this.getFlagValue();

        // clone the properties - using the HashMap copy constructor
        // this does not deep copy all objects but this was not done
        // originally
        if (properties != null) {
            clone.properties = new HashMap<Object, Object>(getProperties());
        }

        return clone;
    }

    /**
     *  Compares a IChemObject with this IChemObject.
     *
     *@param  object  Object of type AtomType
     *@return         true if the atom types are equal
     */
    public boolean compare(Object object) {
        if (!(object instanceof IChemObject)) {
            return false;
        }
        ChemObject chemObj = (ChemObject) object;
        return Objects.equal(identifier, chemObj.identifier);
    }

    /**
     *  Returns the identifier (ID) of this object.
     *
     *@return    a String representing the ID value
     *@see       #setID
     */
    @Override
    public String getID() {
        return this.identifier;
    }

    /**
     *  Sets the identifier (ID) of this object.
     *
     *@param  identifier  a String representing the ID value
     *@see                #getID
     */
    @Override
    public void setID(String identifier) {
        this.identifier = identifier;
    }

    private boolean isPowerOfTwo(int num) {
        return (num == 1) || (num & (num-1)) == 0;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setFlag(int mask, boolean value) {
        if (mask > Short.MAX_VALUE || !isPowerOfTwo(mask))
            throw new IllegalArgumentException("setFlag() must be provided a valid CDKConstant and not used for custom properties");
        // set/unset a bit in the flags value
        if (value)
            flags |= mask;
        else
            flags &= ~(mask);

    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean getFlag(int mask) {
        return (flags & mask) != 0;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Short getFlagValue() {
        return flags; // auto-boxing
    }

    /** @inheritDoc */
    @Override
    public void setProperties(Map<Object, Object> properties) {
        this.properties = null;
        if (properties != null) addProperties(properties);
    }

    /**
     *  Sets the properties of this object.
     *
     *@param  properties  a Hashtable specifying the property values
     *@see                #getProperties
     */
    @Override
    public void addProperties(Map<Object, Object> properties) {
        if (properties == null) return;
        lazyProperties().putAll(properties);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setFlags(boolean[] flagsNew) {
        for (int i = 0; i < flagsNew.length; i++)
            setFlag(CDKConstants.FLAG_MASKS[i], flagsNew[i]);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean[] getFlags() {
        // could use a list a invoke .toArray() on the return
        boolean[] flagArray = new boolean[CDKConstants.MAX_FLAG_INDEX + 1];
        for (int i = 0; i < CDKConstants.FLAG_MASKS.length; i++) {
            int mask = CDKConstants.FLAG_MASKS[i];
            flagArray[i] = getFlag(mask);
        }
        return flagArray;
    }

    /**
     * Clones this <code>IChemObject</code>, but preserves references to <code>Object</code>s.
     *
     * @return    Shallow copy of this IChemObject
     * @see       #clone
     */
    public Object shallowCopy() {
        Object copy = null;
        try {
            copy = super.clone();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return copy;
    }

    @Override
    public IChemObjectBuilder getBuilder() {
        return SilentChemObjectBuilder.getInstance();
    }

    private boolean doNotification = true;

    @Override
    public void setNotification(boolean bool) {
        this.doNotification = bool;
    }

    @Override
    public boolean getNotification() {
        return this.doNotification;
    }

}
