/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.interfaces;

import java.util.Map;

/**
 * Represents the concept of a stereo element in the molecule. Stereo elements can be
 * that of quadrivalent atoms, cis/trans isomerism around double bonds, but also include
 * axial and helical stereochemistry.
 *
 * @cdk.module interfaces
 * @cdk.githash
 *
 * @author      egonw
 * @cdk.keyword stereochemistry
 */
public interface IStereoElement extends ICDKObject {

    /**
     * Map the atoms/bonds in this instance to a new stereo element using the
     * provided atom/bond mapping. This allows the stereo element to be transferred
     * between a cloned or aligned (i.e. isomorphic) chemical graph.
     * <p/>
     * If no mapping is found for a given atom or bond it is replaced with a null
     * reference. However the provided atom and bonds maps must not be null.
     *
     * @param atoms non-null atom mapping, used to convert the original atoms to their mapped
     *              counterparts
     * @param bonds non-null bond mapping, used to convert the original bonds to their mapped
     *              counterparts
     * @return a new stereo element in the same configuration but with atoms/bonds
     *         replaced with their mapped equivalence.
     */
    public IStereoElement map(Map<IAtom, IAtom> atoms, Map<IBond, IBond> bonds);

}





