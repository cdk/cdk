/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.interfaces;

/**
 * Represents the idea of a non-chemical atom-like entity, like Me,
 * R, X, Phe, His, etc.
 *
 * <p>This should be replaced by the mechanism explained in RFC #8.
 *
 * @cdk.module interfaces
 * @cdk.svnrev  $Revision$
 *
 * @see  IAtom
 */
public interface IPseudoAtom extends IAtom {

    /**
     * Returns the label of this PseudoAtom.
     *
     * @return The label for this PseudoAtom
     * @see    #setLabel
     */
    public String getLabel();

    /**
     * Sets the label of this PseudoAtom.
     *
     * @param label The new label for this PseudoAtom
     * @see   #getLabel
     */
    public void setLabel(String label);

}





