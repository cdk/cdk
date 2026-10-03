/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your container code files, and to any copyright notice that you may distribute
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
package org.openscience.cdk.smsd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomRef;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.AtomContainerLegacy;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;

class TargetPropertiesRegressionTest {
    private static final class SerializableLegacyContainer extends AtomContainerLegacy {
        private static final long serialVersionUID = 1L;
    }

    @Test
    void readonlyNeighborViewsSurviveSerialization() throws Exception {
        AtomContainerLegacy molecule = new SerializableLegacyContainer();
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("O"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        TargetProperties original = new TargetProperties(molecule);
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        try (java.io.ObjectOutputStream output = new java.io.ObjectOutputStream(bytes)) {
            output.writeObject(original);
        }
        TargetProperties restored;
        try (java.io.ObjectInputStream input = new java.io.ObjectInputStream(
                new java.io.ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (TargetProperties) input.readObject();
        }
        IAtom first = restored.getAtom(0), second = restored.getAtom(1);
        Assertions.assertEquals(java.util.Collections.singletonList(second), restored.getNeighbors(first));
        Assertions.assertEquals(IBond.Order.DOUBLE, restored.getBond(first, second).getOrder());
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> restored.getNeighbors(first).set(0, first));
    }
    @Test
    void indexesLargeSparseTargetsAndBothBondDirections() {
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        for (int i = 0; i < 4096; i++) {
            molecule.addAtom(new Atom("C"));
            if (i > 0) molecule.addBond(i - 1, i, IBond.Order.SINGLE);
        }
        TargetProperties target = new TargetProperties(molecule);
        Assertions.assertEquals(4096, target.getAtomCount());
        Assertions.assertEquals(1, target.countNeighbors(molecule.getAtom(0)));
        Assertions.assertEquals(2, target.countNeighbors(molecule.getAtom(2048)));
        for (int i = 1; i < 4096; i++) {
            Assertions.assertSame(molecule.getBond(i - 1), target.getBond(molecule.getAtom(i - 1), molecule.getAtom(i)));
            Assertions.assertSame(molecule.getBond(i - 1), target.getBond(molecule.getAtom(i), molecule.getAtom(i - 1)));
        }
        Assertions.assertNull(target.getBond(molecule.getAtom(0), molecule.getAtom(4095)));
        Assertions.assertNull(target.getBond(molecule.getAtom(0), new Atom("C")));
        Assertions.assertNull(target.getAtom(-1));
        Assertions.assertNull(target.getAtom(4096));
    }
    @Test
    void rejectsParallelBondsInsteadOfChoosingOneOrder() {
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TargetProperties(molecule));
    }

    @Test
    void rejectsMissingOrDuplicateAtomsIncludingReferences() {
        Assertions.assertThrows(NullPointerException.class, () -> new TargetProperties(null));
        AtomContainerLegacy molecule = new AtomContainerLegacy() {};
        IAtomContainer missingAtom = org.mockito.Mockito.mock(IAtomContainer.class);
        org.mockito.Mockito.when(missingAtom.getAtomCount()).thenReturn(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TargetProperties(missingAtom));
        IAtom atom = new Atom("C");
        molecule.setAtoms(new IAtom[]{atom, new AtomRef(new AtomRef(atom))});
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TargetProperties(molecule));
    }

    @Test
    void rejectsForeignEndpointsSelfLoopsAndMulticentreBonds() {
        AtomContainerLegacy molecule = new AtomContainerLegacy() {};
        IAtom a = new Atom("C"), b = new Atom("C"), c = new Atom("C");
        molecule.setAtoms(new IAtom[]{a, b});
        molecule.setBonds(new IBond[]{new Bond(a, c, IBond.Order.SINGLE)});
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TargetProperties(molecule));
        molecule.setBonds(new IBond[]{new Bond(a, a, IBond.Order.SINGLE)});
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TargetProperties(molecule));
        molecule.setAtoms(new IAtom[]{a, b, c});
        molecule.setBonds(new IBond[]{new Bond(new IAtom[]{a, b, c}, IBond.Order.SINGLE)});
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TargetProperties(molecule));
        IAtomContainer missingBond = org.mockito.Mockito.mock(IAtomContainer.class);
        org.mockito.Mockito.when(missingBond.bonds()).thenReturn(java.util.Collections.singletonList(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TargetProperties(missingBond));
    }

    @Test
    void preservesReadonlyTopologyAfterContainerChanges() {
        AtomContainerLegacy molecule = new AtomContainerLegacy() {};
        IAtom a = new Atom("C"), b = new Atom("C");
        molecule.setAtoms(new IAtom[]{a, b});
        IBond bond = new Bond(a, b, IBond.Order.SINGLE);
        molecule.addBond(bond);
        TargetProperties target = new TargetProperties(molecule);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> target.getNeighbors(a).clear());
        molecule.removeBond(bond);
        Assertions.assertEquals(1, target.countNeighbors(a));
        Assertions.assertEquals(java.util.Collections.singletonList(b), target.getNeighbors(a));
        Assertions.assertSame(bond, target.getBond(a, b));
        Assertions.assertSame(bond, target.getBond(new AtomRef(new AtomRef(a)), new AtomRef(b)));
    }

    @Test
    void buildsAdjacencyWithoutPerAtomContainerScans() {
        AtomContainerLegacy molecule = new AtomContainerLegacy() {
            @Override
            public int getConnectedBondsCount(IAtom atom) {
                throw new AssertionError("Snapshot must derive degrees from bonds");
            }
            @Override
            public java.util.List<IAtom> getConnectedAtomsList(IAtom atom) {
                throw new AssertionError("Snapshot must derive neighbors from bonds");
            }
        };
        IAtom a = new Atom("C"), b = new Atom("C");
        molecule.setAtoms(new IAtom[]{a, b});
        molecule.addBond(new Bond(a, b, IBond.Order.SINGLE));
        TargetProperties target = new TargetProperties(molecule);
        Assertions.assertEquals(1, target.countNeighbors(a));
        Assertions.assertEquals(java.util.Collections.singletonList(b), target.getNeighbors(a));
    }

}
