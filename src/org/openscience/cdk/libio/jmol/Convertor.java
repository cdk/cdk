/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.libio.jmol;

import org.jmol.api.ModelAdapter;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;

/**
 * @author        egonw
 * @cdk.created   2004-04-25
 * @cdk.module    libio
 * @cdk.keyword   adapter, Jmol
 *
 * @cdk.builddepends jmolApis.jar
 */
public class Convertor {

    public Convertor() {
    }

    public ChemModel convert(ModelAdapter modelAdapter) throws CDKException {
        ChemModel model = new ChemModel();
        SetOfMolecules moleculeSet = new SetOfMolecules();
        Molecule molecule = new Molecule();
        AtomIterator atomIter = modelAdapter.getAtomIterator();
        while (atomIter.hasNext()) {
            Atom atom = new Atom(atomIter.getElementSymbol());
            atom.setAtomicNumber(atomIter.getElementNumber());
            atom.setID(atomIter.getUniqueID());
            atom.setX3D(atomIter.getX());
            atom.setY3D(atomIter.getY());
            atom.setZ3D(atomIter.getZ());
            atom.setCharge(atomIter.getAtomicCharge());
            molecule.addAtom(atom);
        }
        BondIterator bondIter = modelAdapter.getAtomIterator();
        while (bondIter.hasNext()) {
            Bond bond = new Bond();
            // not sure how to retrieve the Atom IDs...
            bond.setOrder(bondIter.getOrder());
            molecule.addBond(bond);
        }
        return model;
    };

    public ModelAdapter convert(ChemModel model) {
        // I need something like the CdkModelAdapter from Jmol here
        return null;
    }
}

