/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.openscience.cdk.event.ChemObjectChangeEvent;

/**
 * The base class for all chemical objects in this cdk. It provides methods
 * for adding listeners and for their notification of events, as well a 
 * a hash table for administration of physical or chemical properties
 *
 * @cdk.module core
 */
public class ChemObject implements java.io.Serializable, Cloneable {
    
	/** Vector for listener administration. */
    private Vector chemObjects;
	/** 
	  * A hashtable for the storage of any kind of properties 
	  * of this ChemObject. 
	  */
    private Hashtable properties;
	/** You will frequently have to use some flags on a ChemObject.
	 * For example, if you want to draw a molecule and see
	 * if you've already drawn an atom, or in a ring search to 
	 * check whether a vertex has been visited in a graph traversal.
	 * Use these flags while addressing particular positions in the
	 * flag array with self-defined constants (flags[VISITED] = true).
	 * 100 flags per object should be more than enough.
	 */
    private boolean[] flags;

	/** 
     * Array of multipurpose vectors. Handle like described for the
	 * flags above.
	 */
    private Vector[] pointers;

    /**
     * The ID is null by default.
     */
    private String identifier; 
    
    /**
     * Constructs a new ChemObject.
     */
	public ChemObject() {
		flags = new boolean[CDKConstants.MAX_FLAG_INDEX + 1];
		chemObjects = null;
        properties = null;
        pointers = null;
        identifier = null;
	}
	
    /*
     * Lazy creation of chemObjects Vector.
     */
    private Vector lazyChemObjects()
    {
	if (chemObjects == null)
	    {
		chemObjects = new Vector();
	    }
	return chemObjects;
    }

	/**
	 * Use this to add yourself to this ChemObject as a listener. 
	 * In order to do so, you must implement the ChemObjectListener Interface.
	 *
	 * @param   col  the ChemObjectListener
     * @see     #removeListener
	 */
	public void addListener(ChemObjectListener col)
	{
	    Vector listeners = lazyChemObjects();

		if (!listeners.contains(col))
		{
			listeners.addElement(col);
		}
		// Should we through an exception if col is already in here or
		// just silently ignore it?
	}


	/**
	 * Use this to remove a ChemObjectListener from the
	 * ListenerList of this ChemObject. It will then not be notified
	 * of change in this object anymore.
	 *
	 * @param   col  The ChemObjectListener to be removed
     * @see     #addListener
	 */
	public void removeListener(ChemObjectListener col)
	{
	    Vector listeners = lazyChemObjects();

		if (listeners.contains(col))
		{
			listeners.removeElement(col);
		}
	}

	/**
	 * This should be triggered by an method that changes the content
	 * of an object to that the registered listeners can react to it.
	 *
	 */
	protected void notifyChanged() {
	    Vector listeners = lazyChemObjects();
	    for (int f = 0; f < listeners.size(); f++) {
		((ChemObjectListener)listeners.elementAt(f)).stateChanged(new ChemObjectChangeEvent(this));
	    }
	}

    /*
     * Lazy creation of properties hash.
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
	 * Sets a property for a ChemObject.
	 *
	 * @param   description  An object description of the property (most likely a unique string)
	 * @param   property  An object with the property itself
     * @see     #getProperty
     * @see     #removeProperty
	 */
	public void setProperty(Object description, Object property) {
		lazyProperties().put(description, property);
	}

	/**
	 * Removes a property for a ChemObject.
	 *
	 * @param   description  The object description of the property (most likely a unique string)
     * @see     #setProperty
     * @see     #getProperty
	 */
	public void removeProperty(Object description) {
		lazyProperties().remove(description);
	}

	/**
	 * Returns a property for the ChemObject.
	 *
	 * @param   description  An object description of the property (most likely a unique string)
	 * @return  The object containing the property. Returns null if propert is not set.
     * @see     #setProperty
     * @see     #removeProperty
	 */
	public Object getProperty(Object description) {
		return lazyProperties().get(description);
	}

    /**
     * Returns a Map with the ChemObject's properties.
     *
     * @return  The object's properties as an Hashtable
     * @see     #setProperties
     */
    public Hashtable getProperties() {
        return lazyProperties();
    }
	
	/**
	 * Clones this <code>ChemObject</code> by applying a deep copy.
	 *
	 * @return  The cloned object
	 */
	public Object clone() {
		Object clone = null;
		try {
			clone = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace(System.err);
		}
        // clone the flags
		((ChemObject)clone).flags = new boolean[CDKConstants.MAX_FLAG_INDEX + 1];
		for (int f = 0; f < flags.length; f++) {
			((ChemObject)clone).flags[f] = flags[f];
		}
		return clone;
	}

    /**
     * Compare a ChemObject with this ChemObject.
     *
     * @param  object Object of type AtomType
     * @return        Return true, if the atomtypes are equal
     */
    public boolean compare(Object object) {
        if (!(object instanceof ChemObject)) {
            return false;
        }
        ChemObject chemObj = (ChemObject)object;
        if (identifier == chemObj.identifier) {
            return true;
        }
        return false;
    }

    /**
     * Returns the identifier (ID) of this object.
     *
     * @return a String representing the ID value
     * @see #setID
     */
    public String getID() {
        return this.identifier;
    }
    
    /**
     * Sets the identifier (ID) of this object.
     *
     * @param identifier  a String representing the ID value
     * @see #getID
     */
    public void setID(String identifier) {
        this.identifier = identifier;
    }
    
    /**
     * Sets the value of some flag.
     * 
     * @param  flag_type  Flag to set
     * @param  flag_value Value to assign to flag
     * @see    #getFlag
     */
    public void setFlag(int flag_type, boolean flag_value) {
        flags[flag_type] = flag_value;
    }
    
    /**
     * Returns the value of some flag.
     *
     * @param  flag_type  Flag to retrieve the value of
     * @return true if the flag <code>flag_type</code> is set
     * @see    #setFlag
     */
    public boolean getFlag(int flag_type) {
        return flags[flag_type];
    }

    /*
     * Lazy creation of pointers array.
     */
    private Vector[] lazyPointers()
    {
	if (pointers == null)
	    {
		pointers = new Vector[CDKConstants.MAX_POINTER_INDEX + 1];
	    }
	return pointers;
    }

    /**
     * Sets the value of some pointer.
     * 
     * @param  pointer_type  Pointer to set
     * @param  pointer_value Value to assign to pointer
     * @see    #getPointer
     */
    public void setPointer(int pointer_type, Vector pointer_value) {
        lazyPointers()[pointer_type] = pointer_value;
    }
    
    /**
     * Returns the value of some pointer.
     *
     * @param  pointer_type  Pointer to retrieve the value of
     * @return a Vector with pointers for the given type
     * @see    #setPointer
     */
    public Vector getPointer(int pointer_type) {
        return lazyPointers()[pointer_type];
    }
    
	/**
	 * Sets the properties of this object.
     *
     * @param properties  a Hashtable specifying the property values
     * @see #getProperties
	 */
	public void setProperties(Hashtable properties) {
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            lazyProperties().put(key, properties.get(key));
        }
	}


    /**
     * Clones this <code>ChemObject</code>, but preserves references to <code>Object</code>s.
     *
     * @return    Shallow copy of this ChemObject
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

}



