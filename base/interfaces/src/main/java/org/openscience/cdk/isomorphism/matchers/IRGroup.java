/* Copyright (C) 2021 John Mayfield
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Represents a single substitute structure in an {@link IRGroupList}. <P>
 * The order of attachment points is provided (first and second only, conform
 * RGFile spec). This order is relevant when the structure connects to the root
 * with more than one bond.
 * <P>
 * @author John Mayfield
 * @see org.openscience.cdk.isomorphism.matchers.IRGroupList
 * @see org.openscience.cdk.isomorphism.matchers.IRGroup
 */
public interface IRGroup {

    /**
     * Get the connection table of atoms/bonds for this Rgroup.
     * @return the connection table
     */
    IAtomContainer getGroup();

    /**
     * Get the first attachment point of the RGroup.
     * @return the first attachment point
     */
    IAtom getFirstAttachmentPoint();

    /**
     * Get the optional second attachment point of the RGroup.
     * @return the second attachment point or null.
     */
    IAtom getSecondAttachmentPoint();
}
