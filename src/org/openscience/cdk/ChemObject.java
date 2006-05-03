/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

/**
 *  The base class for all chemical objects in this cdk. It provides methods for
 *  adding listeners and for their notification of events, as well a a hash
 *  table for administration of physical or chemical properties
 *
 *@author        steinbeck
 *@cdk.module    data
 */
public class ChemObject implements java.io.Serializable, org.openscience.cdk.interfaces.IChemObject
{

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 2798134548764323328L;

	/**
	 *  Vector for listener administration.
	 */
	private Vector chemObjectListeners;
	/**
	 *  A hashtable for the storage of any kind of properties of this IChemObject.
	 */
	private Hashtable properties;
	/**
	 *  You will frequently have to use some flags on a IChemObject. For example, if
	 *  you want to draw a molecule and see if you've already drawn an atom, or in
	 *  a ring search to check whether a vertex has been visited in a graph
	 *  traversal. Use these flags while addressing particular positions in the
	 *  flag array with self-defined constants (flags[VISITED] = true). 100 flags
	 *  per object should be more than enough.
	 */
	private boolean[] flags;

	/**
	 *  The ID is null by default.
	 */
	private String identifier;


	/**
	 *  Constructs a new IChemObject.
	 */
	public ChemObject()
	{
		flags = new boolean[CDKConstants.MAX_FLAG_INDEX + 1];
		chemObjectListeners = null;
		properties = null;
		identifier = null;
	}


	/**
	 *  Lazy creation of chemObjectListeners Vector.
	 *
	 *@return    Vector with the ChemObjects associated.
	 */
	private Vector lazyChemObjectListeners()
	{
		if (chemObjectListeners == null) {
			chemObjectListeners = new Vector();
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
		Vector listeners = lazyChemObjectListeners();

		if (!listeners.contains(col))
		{
			listeners.addElement(col);
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
        
		Vector listeners = lazyChemObjectListeners();
		if (listeners.contains(col)) {
			listeners.removeElement(col);
		}
	}


	/**
	 *  This should be triggered by an method that changes the content of an object
	 *  to that the registered listeners can react to it.
	 */
	public void notifyChanged() {
        if (getNotification() && getListenerCount() > 0) {
            Vector listeners = lazyChemObjectListeners();
            for (int f = 0; f < listeners.size(); f++) {
                ((IChemObjectListener) listeners.elementAt(f)).stateChanged(
                    new org.openscience.cdk.event.ChemObjectChangeEvent(this)
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
	 *		the change happend
	 */
	public void notifyChanged(IChemObjectChangeEvent evt) {
        if (getListenerCount() > 0) {
            Vector listeners = lazyChemObjectListeners();
            for (int f = 0; f < listeners.size(); f++) {
                ((IChemObjectListener) listeners.elementAt(f)).stateChanged(evt);
            }
        }
	}


	/**
	 * Lazy creation of properties hash.
	 *
	 * @return    Returns in instance of the properties
	 */
	private Hashtable lazyProperties()
	{
		if (properties == null)
		{
			properties = new Hashtable();
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
        lazyProperties().remove(description);
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
	public Hashtable getProperties()
	{
		return lazyProperties();
	}

	/**
	 *  Clones this <code>IChemObject</code>. It clones the identifier, flags,
	 *  properties and pointer vectors. The ChemObjectListeners are not cloned, and
	 *  neither is the content of the pointer vectors.
	 *
	 *@return    The cloned object
	 */
	public Object clone() throws CloneNotSupportedException
	{
		Object clone = null;
		try
		{
			clone = super.clone();
		} catch (CloneNotSupportedException e)
		{
			e.printStackTrace(System.err);
		}
		// clone the flags
		((ChemObject) clone).flags = new boolean[CDKConstants.MAX_FLAG_INDEX + 1];
		for (int f = 0; f < flags.length; f++)
		{
			((ChemObject) clone).flags[f] = flags[f];
		}
		// clone the properties
		if (properties != null)
		{
			Hashtable clonedHashtable = new Hashtable();
			Enumeration keys = properties.keys();
			while (keys.hasMoreElements())
			{
				Object key = keys.nextElement();
				if (key instanceof org.openscience.cdk.interfaces.IChemObject)
				{
					key = ((ChemObject) key).clone();
				}
				Object value = properties.get(key);
				if (value instanceof org.openscience.cdk.interfaces.IChemObject)
				{
					value = ((ChemObject) value).clone();
				}
				clonedHashtable.put(key, value);
			}
			((ChemObject) clone).properties = clonedHashtable;
		}
		// delete all listeners
		((ChemObject) clone).chemObjectListeners = null;
		return clone;
	}


	/**
	 *  Compare a IChemObject with this IChemObject.
	 *
	 *@param  object  Object of type AtomType
	 *@return         Return true, if the atomtypes are equal
	 */
	public boolean compare(Object object)
	{
		if (!(object instanceof org.openscience.cdk.interfaces.IChemObject))
		{
			return false;
		}
		ChemObject chemObj = (ChemObject) object;
		if (identifier == chemObj.identifier)
		{
			return true;
		}
		return false;
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
	public void setProperties(Hashtable properties)
	{
		Enumeration keys = properties.keys();
		while (keys.hasMoreElements())
		{
			Object key = keys.nextElement();
			lazyProperties().put(key, properties.get(key));
		}
		notifyChanged();
	}
  
  
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

	/**
     * Clones this <code>IChemObject</code>, but preserves references to <code>Object</code>s.
     *
	 * @return    Shallow copy of this IChemObject
	 * @see       #clone
	 */
	public Object shallowCopy()
	{
		Object copy = null;
		try
		{
			copy = super.clone();
		} catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
		return copy;
	}
	
	public IChemObjectBuilder getBuilder() {
		return DefaultChemObjectBuilder.getInstance();
	}

	private boolean doNotification = true;
	
	public void setNotification(boolean bool) {
		this.doNotification = bool;
	}

	public boolean getNotification() {
		return this.doNotification;
	}

}


