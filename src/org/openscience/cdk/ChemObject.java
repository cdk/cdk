/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

import org.openscience.cdk.event.*;
import java.util.*;

/**
 * The base class for all chemical objects in this cdk. It provides methods
 * for adding listeners and for their notification of events, as well a 
 * a hash table for administration of physical or chemical properties
 */

public class ChemObject implements java.io.Serializable, Cloneable
{
	/** Vector for listener administration. */
	protected Vector chemObjects = new Vector();
	/** 
	  * A hashtable for the storage of physical properties 
	  * of this ChemObject. 
	  */
	protected Hashtable physicalProperties = new Hashtable();
	/** 
	  * A hashtable for the storage of any kind of properties 
	  * of this ChemObject. 
	  */
	protected Hashtable properties = new Hashtable();
	/** 
	  * A hashtable for the storage of the remarks.
	  */
	protected Hashtable remarks = new Hashtable();
	/** You will frequently have to use some flags on a ChemObject.
	 * For example, if you want to draw a molecule and see
	 * if you've already drawn an atom, or in a ring search to 
	 * check whether a vertex has been visited in a graph traversal.
	 * Use these flags while addressing particular positions in the
	 * flag array with self-defined constants (flags[VISITED] = true).
	 * 100 flags per object should be more than enough.
	 */
	public boolean[] flags = new boolean[100];

	/** Array of multipurpose vectors. Handle like described for the
	  * flags above.
	  */
	public Vector[] pointers;

    protected String id = ""; 
    
    /**
     * Constructs a new ChemObject.
     */
	public ChemObject()
	{
		super();
		init();
	}
	
	/**
	 * Use this to add yourself to this ChemObject as a listener. 
	 * In order to do so, you must implement the ChemObjectListener Interface.
	 *
	 * @param   col  You, the ChemObjectListener
     *
     * @see     #removeListener
	 */
	public void addListener(ChemObjectListener col)
	{
		if (!chemObjects.contains(col))
		{
			chemObjects.addElement(col);
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
     *
     * @see     #addListener
	 */
	public void removeListener(ChemObjectListener col)
	{
		if (chemObjects.contains(col))
		{
			chemObjects.removeElement(col);
		}
	}

	/**
	 * This should be triggered by an method that changes the content
	 * of an object to that the registered listeners can react to it.
	 *
	 */
	protected void notifyChanged()
	{
		for (int f = 0; f < chemObjects.size(); f++)
		{
			((ChemObjectListener)chemObjects.elementAt(f)).stateChanged(new ChemObjectChangeEvent(this));
		}
	}


	/**
	 * Sets a physical property for a ChemObject.
	 *
	 * @param   description  An object description of the property (most likely a unique string)
	 * @param   property  An object with the property itself
     *
     * @see     #getPhysicalProperty
	 */
	public void setPhysicalProperty(Object description, Object property)
	{
		physicalProperties.put(description, property);

	}


	/**
	 * Returns a physical property for the ChemObject.
	 *
	 * @param   description  An object description of the property (most likely a unique string)
	 * @return  The object containing the property
     *
     * @see     #setPhysicalProperty
	 */
	public Object getPhysicalProperty(Object description) {
		return physicalProperties.get(description);
	
	}

	/**
	 * Sets a property for a ChemObject.
	 *
	 * @param   description  An object description of the property (most likely a unique string)
	 * @param   property  An object with the property itself
     *
     * @see     #getProperty
	 */
	public void setProperty(Object description, Object property) {
		properties.put(description, property);
	}

	/**
	 * Returns a property for the ChemObject.
	 *
	 * @param   description  An object description of the property (most likely a unique string)
	 * @return  The object containing the property. Returns null if propert is not set.
     *
     * @see     #setProperty
	 */
	public Object getProperty(Object description) {
		return properties.get(description);
	}

    /**
     * Returns a Map with the ChemObject's properties.
     *
     * @return  The object's properties
     */
    public Map getProperties() {
        return properties;
    }
    
	/**
	 * Sets a remark for this ChemObject.
	 *
	 * @param   description  An object description of the remark (most likely an Integer or an unique ID string)
	 * @param   remark  An object with the remark (most likely a String)
     *
     * @see     #getRemark
	 */
	public void setRemark(Object description, Object remark) {
		remarks.put(description, remark);
	}

	/**
	 * Returns a remark for this ChemObject.
	 *
	 * @param   description  An object description of the remark (most likely an Integer or an unique ID string)
	 * @return  The object containing the remark
     *
     * @see     #setRemark
	 */
	public Object getRemark(Object description) {
		return remarks.get(description);
	}

	/**
	 * Initializes all the service fields, vectors, hashtables, etc..
	 *
	 */
	public void init()
	{
		/** Vector for listener administration */
		chemObjects = new Vector();
		/** 
		  * A hashtable for the storage of physical properties 
		  * of this ChemObject. 
		  */
		physicalProperties = new Hashtable();
		/** 
		  * A hashtable for the storage of any kind of properties 
		  * of this ChemObject. 
		  */
		properties = new Hashtable();
		/** 
		  * A hashtable for the storage of the remarks.
		  */
		remarks = new Hashtable();
		/** You will frequently have to use some flags on a ChemObject
		 * for example if you want to draw a molecule and see
		 * if you've already drawn an atom, or in a ring search to 
		 * check whether a vertex has been visited in a graph traversal.
		 * Use these flags while addressing particular positions in the
		 * flag array with self-defined constants (flags[VISITED] = true).
		 * 10 flags per object should be more than enough.
		 */
		flags = new boolean[100];		
	}
	
	/**
	 * Clones this object.
	 *
	 * @return  The cloned object
	 */
	public Object clone()
	{
		Object o = null;
		try
		{
			o = super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace(System.err);
		}
		((ChemObject)o).flags = new boolean[100];
		for (int f = 0; f < flags.length; f++)
		{
			((ChemObject)o).flags[f] = flags[f];	
		}
		return o;
	}

    /**
     * Returns the identifier (ID) of this object.
     *
     * @see setID
     */
    public String getID() {
        return this.id;
    }
    
    /**
     * Sets the identifier (ID) of this object.
     *
     * @see getID
     */
    public void setID(String id) {
        this.id = id;
    }
}



