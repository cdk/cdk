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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import signature.AbstractVertexSignature;
import signature.ColoredTree;

/**
 * @cdk.module test-signature
 * @author maclean
 *
 */
class MoleculeFromSignatureBuilderTest extends AbstractSignatureTest {

    String signatureForAtom(IAtomContainer atomContainer, int atomIndex) {
        MoleculeSignature molSig = new MoleculeSignature(atomContainer);
        return molSig.signatureStringForVertex(atomIndex);
    }

    String canonicalSignature(IAtomContainer atomContainer) {
        MoleculeSignature molSig = new MoleculeSignature(atomContainer);
        return molSig.toCanonicalString();
    }

    IAtomContainer reconstruct(String signature) {
        ColoredTree tree = AbstractVertexSignature.parse(signature);
        MoleculeFromSignatureBuilder builder = new MoleculeFromSignatureBuilder(SilentChemObjectBuilder.getInstance());
        builder.makeFromColoredTree(tree);
        return builder.getAtomContainer();
    }

    void ccBondTest(IBond.Order order) {
        IAtomContainer cc = builder.newInstance(IAtomContainer.class);
        cc.addAtom(builder.newInstance(IAtom.class, "C"));
        cc.addAtom(builder.newInstance(IAtom.class, "C"));
        cc.addBond(0, 1, order);
        String signature = signatureForAtom(cc, 0);
        IAtomContainer reconstructed = reconstruct(signature);
        Assertions.assertEquals(2, reconstructed.getAtomCount());
        Assertions.assertEquals(1, reconstructed.getBondCount());
        Assertions.assertEquals(order, reconstructed.getBond(0).getOrder());
    }

    IAtomContainer makeRing(int ringSize) {
        IAtomContainer ring = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < ringSize; i++) {
            ring.addAtom(builder.newInstance(IAtom.class, "C"));
            if (i > 0) {
                ring.addBond(i - 1, i, IBond.Order.SINGLE);
            }
        }
        ring.addBond(0, ringSize - 1, IBond.Order.SINGLE);
        return ring;
    }

    void ringTest(int ringSize) {
        IAtomContainer ring = makeRing(ringSize);
        String signature = canonicalSignature(ring);
        IAtomContainer reconstructedRing = reconstruct(signature);
        Assertions.assertEquals(ringSize, reconstructedRing.getAtomCount());
    }

    @Test
    void singleCCBondTest() {
        ccBondTest(IBond.Order.SINGLE);
    }

    @Test
    void doubleCCBondTest() {
        ccBondTest(IBond.Order.DOUBLE);
    }

    @Test
    void tripleCCBondTest() {
        ccBondTest(IBond.Order.TRIPLE);
    }

    @Test
    void triangleRingTest() {
        ringTest(3);
    }

    @Test
    void squareRingTest() {
        ringTest(4);
    }

    @Test
    void pentagonRingTest() {
        ringTest(5);
    }

    @Test
    void hexagonRingTest() {
        ringTest(5);
    }

    @Test
    void makeGraphTest() {
        MoleculeFromSignatureBuilder builder = new MoleculeFromSignatureBuilder(SilentChemObjectBuilder.getInstance());
        builder.makeGraph();
        Assertions.assertNotNull(builder.getAtomContainer());
    }

    @Test
    void makeVertexTest() {
        MoleculeFromSignatureBuilder builder = new MoleculeFromSignatureBuilder(SilentChemObjectBuilder.getInstance());
        builder.makeGraph();
        builder.makeVertex("C");
        IAtomContainer product = builder.getAtomContainer();
        Assertions.assertEquals(1, product.getAtomCount());
    }

    @Test
    void makeEdgeTest_singleBond() {
        MoleculeFromSignatureBuilder builder = new MoleculeFromSignatureBuilder(SilentChemObjectBuilder.getInstance());
        builder.makeGraph();
        builder.makeVertex("C");
        builder.makeVertex("C");
        builder.makeEdge(0, 1, "C", "C", "");

        IAtomContainer product = builder.getAtomContainer();
        Assertions.assertEquals(2, product.getAtomCount());
        Assertions.assertEquals(1, product.getBondCount());
        Assertions.assertEquals(IBond.Order.SINGLE, product.getBond(0).getOrder());
    }

    @Test
    void makeEdgeTest_doubleBond() {
        MoleculeFromSignatureBuilder builder = new MoleculeFromSignatureBuilder(SilentChemObjectBuilder.getInstance());
        builder.makeGraph();
        builder.makeVertex("C");
        builder.makeVertex("C");
        builder.makeEdge(0, 1, "C", "C", "=");

        IAtomContainer product = builder.getAtomContainer();
        Assertions.assertEquals(2, product.getAtomCount());
        Assertions.assertEquals(1, product.getBondCount());
        Assertions.assertEquals(IBond.Order.DOUBLE, product.getBond(0).getOrder());
    }

    @Test
    void makeEdgeTest_tripleBond() {
        MoleculeFromSignatureBuilder builder = new MoleculeFromSignatureBuilder(SilentChemObjectBuilder.getInstance());
        builder.makeGraph();
        builder.makeVertex("C");
        builder.makeVertex("C");
        builder.makeEdge(0, 1, "C", "C", "#");

        IAtomContainer product = builder.getAtomContainer();
        Assertions.assertEquals(2, product.getAtomCount());
        Assertions.assertEquals(1, product.getBondCount());
        Assertions.assertEquals(IBond.Order.TRIPLE, product.getBond(0).getOrder());
    }

    @Test
    void makeEdgeTest_aromaticBond() {
        MoleculeFromSignatureBuilder builder = new MoleculeFromSignatureBuilder(SilentChemObjectBuilder.getInstance());
        builder.makeGraph();
        builder.makeVertex("C");
        builder.makeVertex("C");
        builder.makeEdge(0, 1, "C", "C", "p");

        IAtomContainer product = builder.getAtomContainer();
        Assertions.assertEquals(2, product.getAtomCount());
        Assertions.assertEquals(1, product.getBondCount());
        IBond bond = product.getBond(0);
        Assertions.assertEquals(IBond.Order.SINGLE, bond.getOrder());
        Assertions.assertTrue(bond.getFlag(CDKConstants.ISAROMATIC));
    }

    @Test
    void getAtomContainerTest() {
        MoleculeFromSignatureBuilder builder = new MoleculeFromSignatureBuilder(SilentChemObjectBuilder.getInstance());
        builder.makeGraph();
        Assertions.assertNotNull(builder.getAtomContainer());
    }

}
