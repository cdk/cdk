/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk;

/**
 * Represents the concept of a fragment with free valences.
 * An example use would be a set of templates with amino acid
 * residues in a protein generator.
 *
 * @author     egonw
 * @created    August 14th 2002
 *
 * @keyword    fragment
 * @keyword    free valence
 */
public class Fragment extends AtomContainer implements java.io.Serializable, Cloneable  
{

    private String title;
    
    /** number of bonds this fragment can still make. */
    private int free_valences;

    /**
     *  Creates an empty Fragment.
     */
    public Fragment() {
        super();
    }

    /**
     * Returns the title of this Fragment.
     *
     * @return    The title of this Fragment
     *
     * @see    #setTitle
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the title of this Fragment.
     *
     * @param title     title of this fragment
     *
     * @see    #getTitle
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the number of free valences of this Fragment.
     *
     * @return    The title of this Fragment
     *
     * @see    #setFreeValences
     */
    public int getFreeValences() {
        return this.free_valences;
    }

    /**
     * Sets the number of free valences of this Fragment.
     * 
     * @param count the number of free valences of this fragment
     *
     * @see    #getFreeValences
     */
    public void setFreeValences(int count) {
        this.free_valences = count;
    }
}


