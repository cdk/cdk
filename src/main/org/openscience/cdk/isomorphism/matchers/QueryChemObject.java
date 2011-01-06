/* Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                    2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.isomorphism.matchers;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

/**
 * @cdk.module  isomorphism
 * @cdk.githash
 */
public class QueryChemObject {

    /**
     * List for listener administration.
     */
    private List<IChemObjectListener> chemObjectListeners;

    /**
     *  A hashtable for the storage of any kind of properties of this IChemObject.
     */
    private Map<Object, Object> properties;

    /**
     * String representing the identifier for this atom type with null as default.
     */
    private String identifier = (String) CDKConstants.UNSET;

    /**
     *  You will frequently have to use some flags on a IChemObject. For example, if
     *  you want to draw a molecule and see if you've already drawn an atom, or in
     *  a ring search to check whether a vertex has been visited in a graph
     *  traversal. Use these flags while addressing particular positions in the
     *  flag array with self-defined constants (flags[VISITED] = true). 100 flags
     *  per object should be more than enough.
     */
    private boolean[] flags;

    public QueryChemObject() {
        flags = new boolean[CDKConstants.MAX_FLAG_INDEX + 1];
        chemObjectListeners = null;
        properties = null;
        identifier = null;
    }

    /**
     *  Lazy creation of chemObjectListeners List.
     *
     *@return    List with the ChemObjects associated.
     */
    private List<IChemObjectListener> lazyChemObjectListeners()
    {
        if (chemObjectListeners == null) {
            chemObjectListeners = new ArrayList<IChemObjectListener>();
        }
        return chemObjectListeners;
    }


    /**
     *  Use this to add yourself to this IChemObject as a listener. In order to do
     *  so, you must implement the ChemObjectListener Interface.
     *
     *@param  col  the ChemObjectListener
     *@see         #removeListener
     */
    public void addListener(IChemObjectListener col)
    {
        List<IChemObjectListener> listeners = lazyChemObjectListeners();

        if (!listeners.contains(col))
        {
            listeners.add(col);
        }
        // Should we throw an exception if col is already in here or
        // just silently ignore it?
    }


    /**
     *  Returns the number of ChemObjectListeners registered with this object.
     *
     *@return    the number of registered listeners.
     */
    public int getListenerCount() {
        if (chemObjectListeners == null) {
            return 0;
        }
        return lazyChemObjectListeners().size();
    }


    /**
     *  Use this to remove a ChemObjectListener from the ListenerList of this
     *  IChemObject. It will then not be notified of change in this object anymore.
     *
     *@param  col  The ChemObjectListener to be removed
     *@see         #addListener
     */
    public void removeListener(IChemObjectListener col) {
        if (chemObjectListeners == null) {
            return;
        }
        
        List<IChemObjectListener> listeners = lazyChemObjectListeners();
        if (listeners.contains(col)) {
            listeners.remove(col);
        }
    }


    /**
     *  This should be triggered by an method that changes the content of an object
     *  to that the registered listeners can react to it.
     */
    public void notifyChanged() {
        if (getNotification() && getListenerCount() > 0) {
            List<IChemObjectListener> listeners = lazyChemObjectListeners();
            for (Object listener : listeners) {
                ((IChemObjectListener) listener).stateChanged(
                        new QueryChemObjectChangeEvent(this)
                );
            }
        }
    }


    /**
     *  This should be triggered by an method that changes the content of an object
     *  to that the registered listeners can react to it. This is a version of
     *  notifyChanged() which allows to propagate a change event while preserving
     *  the original origin.
     *
     *@param  evt  A ChemObjectChangeEvent pointing to the source of where
     *      the change happend
     */
    public void notifyChanged(IChemObjectChangeEvent evt) {
        if (getNotification() && getListenerCount() > 0) {
            List<IChemObjectListener> listeners = lazyChemObjectListeners();
            for (Object listener : listeners) {
                ((IChemObjectListener) listener).stateChanged(evt);
            }
        }
    }


    /**
     * Lazy creation of properties hash.
     *
     * @return    Returns in instance of the properties
     */
    private Map<Object, Object> lazyProperties()
    {
        if (properties == null)
        {
            properties = new HashMap<Object, Object>();
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
    public void setProperty(Object description, Object property)
    {
        lazyProperties().put(description, property);
        notifyChanged();
    }


    /**
     *  Removes a property for a IChemObject.
     *
     *@param  description  The object description of the property (most likely a
     *      unique string)
     *@see                 #setProperty
     *@see                 #getProperty
     */
    public void removeProperty(Object description)
    {
        if (properties == null) {
            return;
        }
        if (lazyProperties().remove(description) != null)
            notifyChanged();
    }

    /**
     *  Returns a property for the IChemObject.
     *
     *@param  description  An object description of the property (most likely a
     *      unique string)
     *@return              The object containing the property. Returns null if
     *      propert is not set.
     *@see                 #setProperty
     *@see                 #removeProperty
     */
    public Object getProperty(Object description)
    {
        if (properties != null) {
            return lazyProperties().get(description);
        }
        return null;
    }

    /**
     *  Returns a Map with the IChemObject's properties.
     *
     *@return    The object's properties as an Hashtable
     *@see       #setProperties
     */
    public Map<Object,Object> getProperties()
    {
        return lazyProperties();
    }

    /**
     *  Returns the identifier (ID) of this object.
     *
     *@return    a String representing the ID value
     *@see       #setID
     */
    public String getID()
    {
        return this.identifier;
    }

    /**
     *  Sets the identifier (ID) of this object.
     *
     *@param  identifier  a String representing the ID value
     *@see                #getID
     */
    public void setID(String identifier)
    {
        this.identifier = identifier;
        notifyChanged();
    }

    /**
     *  Sets the value of some flag.
     *
     *@param  flag_type   Flag to set
     *@param  flag_value  Value to assign to flag
     *@see                #getFlag
     */
    public void setFlag(int flag_type, boolean flag_value)
    {
        flags[flag_type] = flag_value;
        notifyChanged();
    }

    /**
     *  Returns the value of some flag.
     *
     *@param  flag_type  Flag to retrieve the value of
     *@return            true if the flag <code>flag_type</code> is set
     *@see               #setFlag
     */
    public boolean getFlag(int flag_type)
    {
        return flags[flag_type];
    }

    /**
     *  Sets the properties of this object.
     *
     *@param  properties  a Hashtable specifying the property values
     *@see                #getProperties
     */
    public void setProperties(Map<Object,Object> properties)
    {
        Iterator<Object> keys = properties.keySet().iterator();
        while (keys.hasNext())
        {
            Object key = keys.next();
            lazyProperties().put(key, properties.get(key));
        }
        notifyChanged();
    }

    private boolean doNotification = true;

    /**
     * Sets the whole set of flags.
     *
     * @param  flagsNew    the new flags.
     * @see                #getFlags
     */
    public void setFlags(boolean[] flagsNew){
        flags=flagsNew;
    }

    /**
     * Returns the whole set of flags.
     *
     *@return    the flags.
     *@see       #setFlags
     */
    public boolean[] getFlags(){
        return(flags);
    }

    public void setNotification(boolean bool) {
        this.doNotification = bool;
    }

    public boolean getNotification() {
        return this.doNotification;
    }

     public IChemObjectBuilder getBuilder() {
         throw new IllegalAccessError();
     }

     public boolean matches(IAtom atom) {
         // TODO Auto-generated method stub
         return false;
     }

     @Override
     public Object clone() throws CloneNotSupportedException {
         // TODO Auto-generated method stub
         return super.clone();
     }

     class QueryChemObjectChangeEvent extends EventObject
     implements IChemObjectChangeEvent {

        private static final long serialVersionUID = 8060005185140623245L;

        /** {@inheritDoc} */
         public QueryChemObjectChangeEvent(Object source) {
             super(source);
         }
     }
}