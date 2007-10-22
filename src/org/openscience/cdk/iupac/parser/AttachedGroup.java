/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 *   Copyright (C) 2003  University of Manchester
 *   Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) Project
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 *   (or see http://www.gnu.org/copyleft/lesser.html)
 */

package org.openscience.cdk.iupac.parser;

import java.util.Iterator;
import java.util.Vector;

/**
 * Holds data on a specific functional group or substituent.
 *
 * @author  Stephen Tomkinson
 * @cdk.svnrev  $Revision$
 * @cdk.require ant1.6
 */
public class AttachedGroup {
    /** The name of the group */
    private String name = "";
    /** The collection of locations the group is attached to */
    private Vector locations = new Vector();
    /** The length of the substituent chain */
    private int length = 0;
    
    /** Creates a new instance of FunctionalGroup */
    public AttachedGroup() {
    }
    
    /** Creates a new instance of AttachedGroup with a Sting denoting the functional group */
    public AttachedGroup (Vector locations, String name)
    {
        setLocations (locations);
        setName (name);
    }
    
    /** Creates a new instance of AttachedGroup with an int denoting the length of the substituent chain */
    public AttachedGroup (Vector locations, int length)
    {
        setLocations (locations);
        setLength (length);
    }
    
    /** Getter for property name.
     * @return Value of property name.
     *
     */
    public java.lang.String getName() {
        return name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     *
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    /** Getter for property locations.
     * @return Value of property locations.
     *
     */
    public java.util.Vector getLocations() {
        return locations;
    }
    
    /** Setter for property locations.
     * @param locations New value of property locations.
     *
     */
    public void setLocations(java.util.Vector locations) {
        this.locations = locations;
    }
    
    /**
     * A debug string which represents the contents of the class.
     */
    public String toString ()
    {
        String returnString = name + ": ";
        
        Iterator locationsIterator = locations.iterator();
        while (locationsIterator.hasNext())
        {
            Token locationToken = (Token) locationsIterator.next();
            returnString += locationToken.image + " ";
        }
        
        return returnString;
    }
    
    /** Getter for property length.
     * @return Value of property length.
     *
     */
    public int getLength()
    {
        return length;
    }
    
    /** Setter for property length.
     * @param length New value of property length.
     *
     */
    public void setLength(int length)
    {
        this.length = length;
    }   
}
