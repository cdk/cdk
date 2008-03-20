/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk;

/**
 * Represents the concept of a fragment with free valences.
 * An example use would be a set of templates with amino acid
 * residues in a protein generator.
 *
 * @author     egonw
 * @cdk.svnrev  $Revision$
 * @cdk.created    2002-08-14
 *
 * @cdk.keyword    fragment
 * @cdk.keyword    free valence
 */
public class Fragment extends AtomContainer implements java.io.Serializable, Cloneable  
{

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -1559884858290354341L;

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
	notifyChanged();
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
	notifyChanged();
    }
    
    public String toString() {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("Fragment{").append(hashCode());
    	if (getTitle() != null) {
    		buffer.append(", T=").append(getTitle());
    	}
    	if (getFreeValences() > -1) {
    		buffer.append(", FV=").append(getFreeValences());
    	}
    	buffer.append(super.toString());
    	buffer.append('}');
    	return buffer.toString();
    }
}


