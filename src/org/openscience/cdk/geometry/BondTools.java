/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2002-2003  The Jmol Project
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.geometry;

import org.openscience.cdk.Atom;

/**
 * A set of static utility classes for geometric calculations on Bonds.
 */
public class BondTools {

    /**
     * Returns true if the two atoms are within the distance fudge
     * factor of each other.
     *
     * @keyword join-the-dots
     * @keyword bond creation
     */
    public static boolean closeEnoughToBond(Atom atom1, Atom atom2, double distanceFudgeFactor) {
        
        if (atom1 != atom2) {
            double distanceBetweenAtoms = atom1.getPoint3D().distance(atom2.getPoint3D());
            double bondingDistance = atom1.getCovalentRadius() + atom2.getCovalentRadius();
            if (distanceBetweenAtoms <= (distanceFudgeFactor *bondingDistance)) {
                return true;
            }
        }
        return false;
    }    
    
}



