/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2008-07-25 02:46:55 -0400 (Fri, 25 Jul 2008) $
 *  $Revision: 11746 $
 *
 *  Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
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
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;

/**
 * This matcher checks the hybridization state of an atom.
 *
 * @cdk.module smarts
 * @cdk.svnrev $Revision: 11746 $
 * @cdk.keyword SMARTS
 */

public class HybridizationNumberAtom extends SMARTSAtom {
    Hybridization hybridization;

    /**
     * Creates a new instance
     *
     * @param hybridizationNumber the hybridiation
     */
    public HybridizationNumberAtom(int hybridizationNumber) {
        switch (hybridizationNumber) {
            case 1:
                hybridization = IAtomType.Hybridization.SP1;
                break;
            case 2:
                hybridization = IAtomType.Hybridization.SP2;
                break;
            case 3:
                hybridization = IAtomType.Hybridization.SP3;
                break;
            case 4:
                hybridization = IAtomType.Hybridization.SP3D1;
                break;
            case 5:
                hybridization = IAtomType.Hybridization.SP3D2;
                break;
            case 6:
                hybridization = IAtomType.Hybridization.SP3D3;
                break;
            case 7:
                hybridization = IAtomType.Hybridization.SP3D4;
                break;
            case 8:
                hybridization = IAtomType.Hybridization.SP3D5;
                break;
        }
    }


    /* (non-Javadoc)
      * @see org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org.openscience.cdk.interfaces.IAtom)
      */
    public boolean matches(IAtom atom) {
        return hybridization == atom.getHybridization();
    }

    /* (non-Javadoc)
      * @see org.openscience.cdk.PseudoAtom#toString()
      */
    public String toString() {
        return ("HybridizationNumberAtom(" + hybridization.toString() + ")");
    }

}