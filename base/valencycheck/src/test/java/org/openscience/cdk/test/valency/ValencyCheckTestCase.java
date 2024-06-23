/* Copyright (C) 2022 NextMove Software
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
package org.openscience.cdk.test.valency;

import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

public class ValencyCheckTestCase extends CDKTestCase {

    /**
     * Convenience method that perceives atom types (CDK scheme) and
     * adds explicit hydrogens accordingly. It does not create 2D or 3D
     * coordinates for the new hydrogens.
     *
     * @param container to which explicit hydrogens are added.
     */
    protected void addExplicitHydrogens(IAtomContainer container) throws Exception {
        addImplicitHydrogens(container);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(container);
    }

    /**
     * Convenience method that perceives atom types (CDK scheme) and
     * adds implicit hydrogens accordingly. It does not create 2D or 3D
     * coordinates for the new hydrogens.
     *
     * @param container to which implicit hydrogens are added.
     */
    protected void addImplicitHydrogens(IAtomContainer container) throws Exception {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
        int atomCount = container.getAtomCount();
        String[] originalAtomTypeNames = new String[atomCount];
        for (int i = 0; i < atomCount; i++) {
            IAtom atom = container.getAtom(i);
            IAtomType type = matcher.findMatchingAtomType(container, atom);
            originalAtomTypeNames[i] = atom.getAtomTypeName();
            atom.setAtomTypeName(type.getAtomTypeName());
        }
        CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(container.getBuilder());
        hAdder.addImplicitHydrogens(container);
        // reset to the original atom types
        for (int i = 0; i < atomCount; i++) {
            IAtom atom = container.getAtom(i);
            atom.setAtomTypeName(originalAtomTypeNames[i]);
        }
    }

}
