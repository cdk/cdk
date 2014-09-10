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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * @cdk.module test-signature
 * @author maclean
 *
 */
public class SignatureQuotientGraphTest extends AbstractSignatureTest {

    @Test
    public void isConnectedTest() {
        IAtomContainer singleBond = builder.newInstance(IAtomContainer.class);
        singleBond.addAtom(builder.newInstance(IAtom.class, "C"));
        singleBond.addAtom(builder.newInstance(IAtom.class, "C"));
        singleBond.addBond(0, 1, IBond.Order.SINGLE);
        SignatureQuotientGraph quotientGraph = new SignatureQuotientGraph(singleBond);
        Assert.assertTrue(quotientGraph.isConnected(0, 1));
    }

    public void checkParameters(SignatureQuotientGraph qGraph, int expectedVertexCount, int expectedEdgeCount,
            int expectedLoopEdgeCount) {
        Assert.assertEquals(expectedVertexCount, qGraph.getVertexCount());
        Assert.assertEquals(expectedEdgeCount, qGraph.getEdgeCount());
        Assert.assertEquals(expectedLoopEdgeCount, qGraph.numberOfLoopEdges());
    }

    @Test
    public void testCubane() {
        IAtomContainer cubane = AbstractSignatureTest.makeCubane();
        SignatureQuotientGraph qGraph = new SignatureQuotientGraph(cubane);
        checkParameters(qGraph, 1, 1, 1);
    }

    @Test
    public void testCuneaneAtHeight1() {
        IAtomContainer cuneane = AbstractSignatureTest.makeCuneane();
        SignatureQuotientGraph qGraph = new SignatureQuotientGraph(cuneane, 1);
        checkParameters(qGraph, 1, 1, 1);
    }

    @Test
    public void testCuneaneAtHeight2() {
        IAtomContainer cuneane = AbstractSignatureTest.makeCuneane();
        SignatureQuotientGraph qGraph = new SignatureQuotientGraph(cuneane, 2);
        checkParameters(qGraph, 3, 5, 3);
    }

    @Test
    public void testPropellane() {
        IAtomContainer propellane = AbstractSignatureTest.makePropellane();
        SignatureQuotientGraph qGraph = new SignatureQuotientGraph(propellane);
        checkParameters(qGraph, 2, 2, 1);
    }

    @Test
    public void testTwistane() {
        IAtomContainer twistane = AbstractSignatureTest.makeTwistane();
        SignatureQuotientGraph qGraph = new SignatureQuotientGraph(twistane);
        checkParameters(qGraph, 3, 4, 2);
    }

    @Test
    public void testC7H16Isomers() {
        IAtomContainer c7H16A = AbstractSignatureTest.makeC7H16A();
        IAtomContainer c7H16B = AbstractSignatureTest.makeC7H16B();
        IAtomContainer c7H16C = AbstractSignatureTest.makeC7H16C();
        SignatureQuotientGraph qGraphA = new SignatureQuotientGraph(c7H16A, 1);
        SignatureQuotientGraph qGraphB = new SignatureQuotientGraph(c7H16B, 1);
        SignatureQuotientGraph qGraphC = new SignatureQuotientGraph(c7H16C, 1);
        checkParameters(qGraphA, 4, 7, 1);
        checkParameters(qGraphB, 4, 5, 0);
        checkParameters(qGraphC, 4, 7, 1);
    }

    @Test
    public void testAromatic() {
        IAtomContainer benzene = AbstractSignatureTest.makeBenzene();
        SignatureQuotientGraph qGraph = new SignatureQuotientGraph(benzene);
        checkParameters(qGraph, 1, 1, 1);
    }

}
