/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.isomorphism.matchers;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;

/**
 *@cdk.module    extra
 */
public class QueryAtomContainerCreator {

    /**
     *  Creates a QueryAtomContainer with SymbolQueryAtom's and
     *  OrderQueryBond's.
     *
     *@param  container  The AtomContainer that stands as model
     *@return            The new QueryAtomContainer created from container.
     */
    public static QueryAtomContainer createBasicQueryContainer(IAtomContainer container) {
        QueryAtomContainer queryContainer = new QueryAtomContainer();
        IAtom[] atoms = container.getAtoms();
        for (int i = 0; i < atoms.length; i++) {
            queryContainer.addAtom(new SymbolQueryAtom(atoms[i]));
        }
        IBond[] bonds = container.getBonds();
        for (int i = 0; i < bonds.length; i++) {
            int index1 = container.getAtomNumber(bonds[i].getAtomAt(0));
            int index2 = container.getAtomNumber(bonds[i].getAtomAt(1));
            if (bonds[i].getFlag(CDKConstants.ISAROMATIC)) {
                queryContainer.addBond(new AromaticQueryBond((IQueryAtom) queryContainer.getAtomAt(index1),
                                      (IQueryAtom) queryContainer.getAtomAt(index2),
                                      1.5));
            } else {
                queryContainer.addBond(new OrderQueryBond((IQueryAtom) queryContainer.getAtomAt(index1),
                                      (IQueryAtom) queryContainer.getAtomAt(index2),
                                      bonds[i].getOrder()));
            }
        }
        return queryContainer;
    }

    
    /**
     *  Creates a QueryAtomContainer with SymbolAncChargeQueryAtom's and
     *  OrderQueryBond's.
     *
     *@param  container  The AtomContainer that stands as model
     *@return            The new QueryAtomContainer created from container.
     */
    public static QueryAtomContainer createSymbolAndChargeQueryContainer(IAtomContainer container) {
        QueryAtomContainer queryContainer = new QueryAtomContainer();
        IAtom[] atoms = container.getAtoms();
        for (int i = 0; i < atoms.length; i++) {
            queryContainer.addAtom(new SymbolAndChargeQueryAtom(atoms[i]));
        }
        IBond[] bonds = container.getBonds();
        for (int i = 0; i < bonds.length; i++) {
            int index1 = container.getAtomNumber(bonds[i].getAtomAt(0));
            int index2 = container.getAtomNumber(bonds[i].getAtomAt(1));
            if (bonds[i].getFlag(CDKConstants.ISAROMATIC)) {
                queryContainer.addBond(new AromaticQueryBond((IQueryAtom) queryContainer.getAtomAt(index1),
                                      (IQueryAtom) queryContainer.getAtomAt(index2),
                                      1.5));
            } else {
                queryContainer.addBond(new OrderQueryBond((IQueryAtom) queryContainer.getAtomAt(index1),
                                      (IQueryAtom) queryContainer.getAtomAt(index2),
                                      bonds[i].getOrder()));
            }
        }
        return queryContainer;
    }    
    

    /**
     *  Creates a QueryAtomContainer with AnyAtoms / Aromatic Atoms and OrderQueryBonds / AromaticQueryBonds.
     *  It uses the CDKConstants.ISAROMATIC flag to determine the aromaticity of container.
     *
     *@param  container    The AtomContainer that stands as model
     *@param  aromaticity  True = use aromaticity flags to create AtomaticAtoms and AromaticQueryBonds
     *@return              The new QueryAtomContainer created from container
     */
    public static QueryAtomContainer createAnyAtomContainer(IAtomContainer container, boolean aromaticity) {
        QueryAtomContainer queryContainer = new QueryAtomContainer();
        IAtom[] atoms = container.getAtoms();

        for (int i = 0; i < atoms.length; i++) {
        	if (aromaticity && atoms[i].getFlag(CDKConstants.ISAROMATIC)) {
                queryContainer.addAtom(new AromaticAtom());
            } else {
                queryContainer.addAtom(new AnyAtom());
            }
        }

        IBond[] bonds = container.getBonds();
        for (int i = 0; i < bonds.length; i++) {
            int index1 = container.getAtomNumber(bonds[i].getAtomAt(0));
            int index2 = container.getAtomNumber(bonds[i].getAtomAt(1));
            if (aromaticity && bonds[i].getFlag(CDKConstants.ISAROMATIC)) {
                queryContainer.addBond(new AromaticQueryBond((IQueryAtom) queryContainer.getAtomAt(index1),
                        (IQueryAtom) queryContainer.getAtomAt(index2),
                        1.5));
            } else {
                queryContainer.addBond(new OrderQueryBond((IQueryAtom) queryContainer.getAtomAt(index1),
                        (IQueryAtom) queryContainer.getAtomAt(index2),
                        bonds[i].getOrder()));
            }
        }
        return queryContainer;
    }

}

