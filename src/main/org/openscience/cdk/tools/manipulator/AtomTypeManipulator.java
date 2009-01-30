/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
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
 */
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;


/**
 * Class with utilities for the <code>AtomType</code> class.
 * - changed 21/7/05 by cho: add properties for mmff94 atom type 
 *
 * @author     mfe4
 * @author     egonw
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.tools.manipulator.AtomTypeManipulatorTest")
public class AtomTypeManipulator {
	
	/**
	 * Method that assign properties to an atom given a particular atomType.
	 * An <code>IllegalArgumentException</code> is thrown if the given <code>IAtomType</code>
	 * is null. <b>This method overwrites non-null values.</b>
	 *
	 * @param  atom     Atom to configure
	 * @param  atomType AtomType. Must not be null.
	 */
    @TestMethod("testConfigure_IAtom_IAtomType")
    public static void configure(IAtom atom, IAtomType atomType) {
        if (atomType == null) {
            throw new IllegalArgumentException("The IAtomType was null.");
        }

        atom.setSymbol(atomType.getSymbol());
        atom.setAtomTypeName(atomType.getAtomTypeName());
        atom.setMaxBondOrder(atomType.getMaxBondOrder());
        atom.setBondOrderSum(atomType.getBondOrderSum());
        atom.setCovalentRadius(atomType.getCovalentRadius());
        atom.setValency(atomType.getValency());
        atom.setFormalCharge(atomType.getFormalCharge());
        atom.setHybridization(atomType.getHybridization());
        atom.setFormalNeighbourCount(atomType.getFormalNeighbourCount());
        atom.setFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR, atomType.getFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR));
        atom.setFlag(CDKConstants.IS_HYDROGENBOND_DONOR, atomType.getFlag(CDKConstants.IS_HYDROGENBOND_DONOR));
        Object constant = atomType.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT);
        if (constant != null) {
            atom.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, constant);
        }
        atom.setFlag(CDKConstants.ISAROMATIC, atomType.getFlag(CDKConstants.ISAROMATIC));

        Object color = atomType.getProperty("org.openscience.cdk.renderer.color");
        if (color != null) {
            atom.setProperty("org.openscience.cdk.renderer.color", color);
        }
        if (atomType.getAtomicNumber() != CDKConstants.UNSET) atom.setAtomicNumber(atomType.getAtomicNumber());
        if (atomType.getExactMass() != CDKConstants.UNSET) atom.setExactMass(atomType.getExactMass());        
    }

    /**
     * Method that assign properties to an atom given a particular atomType.
     * An {@link IllegalArgumentException} is thrown if the given {@link IAtomType}
     * is null. <b>This method only sets null values.</b>
     *
     * @param  atom     Atom to configure
     * @param  atomType AtomType. Must not be null.
     */
    @TestMethod("testConfigureUnsetProperties_DontOverwriterSetProperties,testConfigureUnsetProperties")
    public static void configureUnsetProperties(IAtom atom, IAtomType atomType) {
        if (atomType == null) {
            throw new IllegalArgumentException("The IAtomType was null.");
        }

        if (atom.getSymbol() == CDKConstants.UNSET && atomType.getSymbol() != CDKConstants.UNSET) atom.setSymbol(atomType.getSymbol());
        if (atom.getAtomTypeName() == CDKConstants.UNSET && atomType.getAtomTypeName() != CDKConstants.UNSET) atom.setAtomTypeName(atomType.getAtomTypeName());
        if (atom.getMaxBondOrder() == CDKConstants.UNSET && atomType.getMaxBondOrder() != CDKConstants.UNSET) atom.setMaxBondOrder(atomType.getMaxBondOrder());
        if (atom.getBondOrderSum() == CDKConstants.UNSET && atomType.getBondOrderSum() != CDKConstants.UNSET) atom.setBondOrderSum(atomType.getBondOrderSum());
        if (atom.getCovalentRadius() == CDKConstants.UNSET && atomType.getCovalentRadius() != CDKConstants.UNSET) atom.setCovalentRadius(atomType.getCovalentRadius());
        if (atom.getValency() == CDKConstants.UNSET && atomType.getValency() != CDKConstants.UNSET) atom.setValency(atomType.getValency());
        if (atom.getFormalCharge() == CDKConstants.UNSET && atomType.getFormalCharge() != CDKConstants.UNSET) atom.setFormalCharge(atomType.getFormalCharge());
        if (atom.getHybridization() == CDKConstants.UNSET && atomType.getHybridization() != CDKConstants.UNSET) atom.setHybridization(atomType.getHybridization());
        if (atom.getFormalNeighbourCount() == CDKConstants.UNSET && atomType.getFormalNeighbourCount() != CDKConstants.UNSET) atom.setFormalNeighbourCount(atomType.getFormalNeighbourCount());
        if (atom.getAtomicNumber() == CDKConstants.UNSET && atomType.getAtomicNumber() != CDKConstants.UNSET) atom.setAtomicNumber(atomType.getAtomicNumber());
        if (atom.getExactMass() == CDKConstants.UNSET && atomType.getExactMass() != CDKConstants.UNSET) atom.setExactMass(atomType.getExactMass());        
    }

}

