/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.structgen;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.vecmath.Vector2d;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * @cdk.module test-structgen
 */
public class SingleStructureRandomGeneratorTest {

    String                         mf;
    SingleStructureRandomGenerator ssrg;

    public SingleStructureRandomGeneratorTest() throws Exception {
        System.out.println("Instantiating MoleculeListViewer");
        System.out.println("Instantiating SingleStructureRandomGenerator");
        ssrg = new SingleStructureRandomGenerator();
        System.out.println("Assining unbonded set of atoms");
        AtomContainer ac = getBunchOfUnbondedAtoms();
        mf = MolecularFormulaManipulator.getString(MolecularFormulaManipulator.getMolecularFormula(ac));
        System.out.println("Molecular Formula is: " + mf);
        ssrg.setAtomContainer(ac);
    }

    private boolean showIt(IAtomContainer molecule, String name) throws Exception {
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule((IAtomContainer) molecule.clone());
        sdg.generateCoordinates(new Vector2d(0, 1));
        return true;
    }

    private AtomContainer getBunchOfUnbondedAtoms() {
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        fixCarbonHCount(molecule);
        molecule.removeAllElectronContainers();
        return (AtomContainer) molecule;
    }

    private void fixCarbonHCount(IAtomContainer mol) {
        /*
         * the following line are just a quick fix for this particluar
         * carbon-only molecule until we have a proper hydrogen count
         * configurator
         */
        double bondCount = 0;
        org.openscience.cdk.interfaces.IAtom atom;
        for (int f = 0; f < mol.getAtomCount(); f++) {
            atom = mol.getAtom(f);
            bondCount = mol.getBondOrderSum(atom);
            if (bondCount > 4) System.out.println("bondCount: " + bondCount);
            atom.setImplicitHydrogenCount(4 - (int) bondCount
                    - (atom.getCharge() == null ? 0 : atom.getCharge().intValue()));
        }
    }

    class MoreAction extends AbstractAction {

        private static final long serialVersionUID = -7405706755621468840L;

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                IAtomContainer ac = ssrg.generate();
                showIt(ac, "Randomly generated for " + mf);
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
