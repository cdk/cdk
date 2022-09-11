/* Copyright (C) 2009-2010 maclean {gilleain.torrance@gmail.com}
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
package org.openscience.cdk.signature;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElement;

/**
 * @cdk.module test-signature
 * @author maclean
 *
 */
class AtomSignatureTest extends AbstractSignatureTest {

    private IAtomContainer atomContainer;

    private AtomSignature  atomSignature;

    @BeforeEach
    void setUp() {
        atomContainer = builder.newInstance(IAtomContainer.class);
        atomContainer.addAtom(builder.newInstance(IAtom.class, "C"));
        atomContainer.addAtom(builder.newInstance(IAtom.class, "C"));
        atomContainer.addBond(0, 1, IBond.Order.DOUBLE);
        atomSignature = new AtomSignature(0, atomContainer);
    }

    @Test
    void getIntLabelTest() {
        atomContainer.getAtom(0).setMassNumber(12);
        Assertions.assertEquals(12, atomSignature.getIntLabel(0));
    }

    @Test
    void getConnectedTest() {
        Assertions.assertEquals(1, atomSignature.getConnected(0)[0]);
    }

    @Test
    void getEdgeLabelTest() {
        Assertions.assertEquals("=", atomSignature.getEdgeLabel(0, 1));
    }

    @Test
    void getAromaticEdgeLabelTest() {
        IAtomContainer benzeneRing = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < 6; i++) {
            benzeneRing.addAtom(builder.newInstance(IAtom.class, "C"));
        }
        for (int i = 0; i < 6; i++) {
            IAtom a = benzeneRing.getAtom(i);
            IAtom b = benzeneRing.getAtom((i + 1) % 6);
            IBond bond = builder.newInstance(IBond.class, a, b);
            benzeneRing.addBond(bond);
            bond.setFlag(CDKConstants.ISAROMATIC, true);
        }

        AtomSignature signature = new AtomSignature(0, benzeneRing);
        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals("p", signature.getEdgeLabel(i, (i + 1) % 6), "Failed for " + i);
        }
    }

    @Test
    void getVertexSymbolTest() {
        Assertions.assertEquals("C", atomSignature.getVertexSymbol(0));
    }

    //    @Test
    //    public void integerInvariantsTest() {
    //        IAtomContainer isotopeChiralMol = builder.newInstance(IAtomContainer.class);
    //        isotopeChiralMol.addAtom(builder.newInstance(IAtom.class, "C"));
    //
    //        IAtom s32 = builder.newInstance(IAtom.class, "S");
    //        s32.setMassNumber(32);
    //
    //        IAtom s33 = builder.newInstance(IAtom.class, "S");
    //        s33.setMassNumber(33);
    //
    //        IAtom s34 = builder.newInstance(IAtom.class, "S");
    //        s34.setMassNumber(34);
    //
    //        IAtom s36 = builder.newInstance(IAtom.class, "S");
    //        s36.setMassNumber(36);
    //
    //        isotopeChiralMol.addAtom(s36);
    //        isotopeChiralMol.addAtom(s34);
    //        isotopeChiralMol.addAtom(s33);
    //        isotopeChiralMol.addAtom(s32);
    //
    //        isotopeChiralMol.addBond(0, 1, IBond.Order.SINGLE);
    //        isotopeChiralMol.addBond(0, 2, IBond.Order.SINGLE);
    //        isotopeChiralMol.addBond(0, 3, IBond.Order.SINGLE);
    //        isotopeChiralMol.addBond(0, 4, IBond.Order.SINGLE);
    //
    //        MoleculeSignature molSig = new MoleculeSignature(isotopeChiralMol);
    //        System.out.println(molSig.toCanonicalString());
    //    }

    @Test
    void cuneaneCubaneHeightTest() {
        IAtomContainer cuneane = AbstractSignatureTest.makeCuneane();
        IAtomContainer cubane = AbstractSignatureTest.makeCubane();
        int height = 1;
        AtomSignature cuneaneSignature = new AtomSignature(0, height, cuneane);
        AtomSignature cubaneSignature = new AtomSignature(0, height, cubane);
        String cuneaneSigString = cuneaneSignature.toCanonicalString();
        String cubaneSigString = cubaneSignature.toCanonicalString();
        Assertions.assertEquals(cuneaneSigString, cubaneSigString);
    }

    void moleculeIsCarbon3Regular(IAtomContainer molecule) {
        int i = 0;
        for (IAtom a : molecule.atoms()) {
            int count = 0;
            for (IAtom connected : molecule.getConnectedAtomsList(a)) {
                if (connected.getAtomicNumber() == IElement.C) {
                    count++;
                }
            }
            Assertions.assertEquals(3, count, "Failed for atom " + i);
            i++;
        }
    }

    @Test
    void dodecahedraneHeightTest() {
        IAtomContainer dodecahedrane = AbstractSignatureTest.makeDodecahedrane();
        moleculeIsCarbon3Regular(dodecahedrane);
        int diameter = 5;
        for (int height = 0; height <= diameter; height++) {
            allEqualAtHeightTest(dodecahedrane, height);
        }
    }

    @Test
    void allHeightsOfASymmetricGraphAreEqualTest() {
        IAtomContainer cubane = makeCubane();
        int diameter = 3;
        for (int height = 0; height <= diameter; height++) {
            allEqualAtHeightTest(cubane, height);
        }
    }

    void allEqualAtHeightTest(IAtomContainer molecule, int height) {
        Map<String, Integer> sigfreq = new HashMap<>();
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            AtomSignature atomSignature = new AtomSignature(i, height, molecule);
            String canonicalSignature = atomSignature.toCanonicalString();
            if (sigfreq.containsKey(canonicalSignature)) {
                sigfreq.put(canonicalSignature, sigfreq.get(canonicalSignature) + 1);
            } else {
                sigfreq.put(canonicalSignature, 1);
            }
        }
        Assertions.assertEquals(1, sigfreq.keySet().size());
    }

    @Test
    void convertEdgeLabelToColorTest() {
        IAtomContainer ac = makeBenzene(); // doesn't really matter
        AtomSignature atomSignature = new AtomSignature(0, ac);
        int aromaticColor = atomSignature.convertEdgeLabelToColor("p");
        Assertions.assertTrue(aromaticColor > 0);
        int singleColor = atomSignature.convertEdgeLabelToColor("");
        int doubleColor = atomSignature.convertEdgeLabelToColor("=");
        int tripleColor = atomSignature.convertEdgeLabelToColor("#");
        Assertions.assertTrue(singleColor < doubleColor);
        Assertions.assertTrue(doubleColor < tripleColor);
    }

}
