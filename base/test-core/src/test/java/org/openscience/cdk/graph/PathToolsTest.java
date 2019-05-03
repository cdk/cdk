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
package org.openscience.cdk.graph;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;

/**
 * @cdk.module test-core
 */
public class PathToolsTest extends CDKTestCase {

    private static IAtomContainer molecule;
    private static SmilesParser   sp;

    @BeforeClass
    public static void setUp() {
        molecule = TestMoleculeFactory.makeAlphaPinene();
        sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    }

    @Test
    public void testBreadthFirstTargetSearch_IAtomContainer_List_IAtom_int_int() {
        org.openscience.cdk.interfaces.IAtom atom1 = molecule.getAtom(0);
        org.openscience.cdk.interfaces.IAtom atom2 = molecule.getAtom(8);
        List<IAtom> sphere = new ArrayList<IAtom>();
        sphere.add(atom1);
        int length = PathTools.breadthFirstTargetSearch(molecule, sphere, atom2, 0, 3);
        //logger.debug("PathLengthTest->length: " + length);
        Assert.assertEquals(3, length);
    }

    @Test
    public void testResetFlags_IAtomContainer() throws Exception {
        IAtomContainer atomContainer = new AtomContainer();
        IAtom atom1 = new Atom("C");
        atom1.setFlag(CDKConstants.VISITED, true);
        IAtom atom2 = new Atom("C");
        atom2.setFlag(CDKConstants.VISITED, true);
        IBond bond1 = new Bond(atom1, atom2, Order.SINGLE);
        atomContainer.addAtom(atom1);
        atomContainer.addAtom(atom2);
        atomContainer.addBond(bond1);

        PathTools.resetFlags(atomContainer);

        // now assume that no VISITED is set
        Iterator<IAtom> atoms = atomContainer.atoms().iterator();
        while (atoms.hasNext()) {
            Assert.assertNull(atoms.next().getProperty(CDKConstants.VISITED));
        }
        Iterator<IBond> bonds = atomContainer.bonds().iterator();
        while (bonds.hasNext()) {
            Assert.assertNull(bonds.next().getProperty(CDKConstants.VISITED));
        }
    }

    @Test
    public void testGetShortestPath_IAtomContainer_IAtom_IAtom() throws Exception {
        IAtomContainer atomContainer = null;
        IAtom start = null;
        IAtom end = null;
        List<IAtom> path = null;
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        atomContainer = sp.parseSmiles("CCCC");
        start = atomContainer.getAtom(0);
        end = atomContainer.getAtom(3);
        path = PathTools.getShortestPath(atomContainer, start, end);
        Assert.assertEquals(4, path.size());

        atomContainer = sp.parseSmiles("CC(N)CC");
        start = atomContainer.getAtom(0);
        end = atomContainer.getAtom(2);
        path = PathTools.getShortestPath(atomContainer, start, end);
        Assert.assertEquals(3, path.size());

        atomContainer = sp.parseSmiles("C1C(N)CC1");
        start = atomContainer.getAtom(0);
        end = atomContainer.getAtom(2);
        path = PathTools.getShortestPath(atomContainer, start, end);
        Assert.assertEquals(3, path.size());
    }

    @Test
    public void testGetShortestPath_Middle() throws Exception {
        String filename = "data/mdl/shortest_path_test.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer testMolecule = new AtomContainer();
        reader.read(testMolecule);

        List<IAtom> path = PathTools.getShortestPath(testMolecule, testMolecule.getAtom(0), testMolecule.getAtom(9));
        Assert.assertEquals(10, path.size());

        path = PathTools.getShortestPath(testMolecule, testMolecule.getAtom(1), testMolecule.getAtom(9));
        Assert.assertEquals(9, path.size());

        path = PathTools.getShortestPath(testMolecule, testMolecule.getAtom(9), testMolecule.getAtom(0));
        Assert.assertEquals(10, path.size());
    }

    @Test
    public void testGetPathsOfLength_IAtomContainer_IAtom_int() throws Exception {
        IAtomContainer atomContainer = null;
        IAtom start = null;
        List<List<IAtom>> paths = null;
        atomContainer = sp.parseSmiles("c1cc2ccccc2cc1");
        start = atomContainer.getAtom(0);
        paths = PathTools.getPathsOfLength(atomContainer, start, 1);
        Assert.assertEquals(2, paths.size());

        atomContainer = sp.parseSmiles("Cc1cc2ccccc2cc1");
        start = atomContainer.getAtom(0);
        paths = PathTools.getPathsOfLength(atomContainer, start, 1);
        Assert.assertEquals(1, paths.size());
    }

    @Test
    public void testGetAllPaths_IAtomContainer_IAtom_IAtom() throws Exception {
        IAtomContainer atomContainer = sp.parseSmiles("c12ccccc1cccc2");

        IAtom start = atomContainer.getAtom(0);
        IAtom end = atomContainer.getAtom(2);
        List<List<IAtom>> paths = PathTools.getAllPaths(atomContainer, start, end);

        Assert.assertEquals(3, paths.size());

        List<IAtom> path1 = paths.get(0);
        List<IAtom> path2 = paths.get(1);
        List<IAtom> path3 = paths.get(2);

        Assert.assertEquals(start, path1.get(0));
        Assert.assertEquals(atomContainer.getAtom(1), path1.get(1));
        Assert.assertEquals(end, path1.get(2));

        Assert.assertEquals(start, path2.get(0));
        Assert.assertEquals(atomContainer.getAtom(5), path2.get(1));
        Assert.assertEquals(atomContainer.getAtom(4), path2.get(2));
        Assert.assertEquals(atomContainer.getAtom(3), path2.get(3));
        Assert.assertEquals(end, path2.get(4));
        Assert.assertNotNull(path3);
    }

    @Test
    public void testGetVertexCountAtDistance_IAtomContainer_int() throws Exception {
        IAtomContainer atomContainer = sp.parseSmiles("c12ccccc1cccc2");
        Assert.assertEquals(11, PathTools.getVertexCountAtDistance(atomContainer, 1));
        Assert.assertEquals(14, PathTools.getVertexCountAtDistance(atomContainer, 2));
    }

    @Test
    public void testGetInt2DColumnSum_arrayintint() {
        int[][] start = new int[2][2];
        start[0][0] = 5;
        start[0][1] = 3;
        start[1][0] = 1;
        start[1][1] = 2;

        Assert.assertEquals(8, PathTools.getInt2DColumnSum(start)[0]);
        Assert.assertEquals(3, PathTools.getInt2DColumnSum(start)[1]);
    }

    @Test
    public void testGetMolecularGraphRadius_IAtomContainer() throws Exception {
        IAtomContainer atomContainer = sp.parseSmiles("CCCC");
        Assert.assertEquals(2, PathTools.getMolecularGraphRadius(atomContainer));
        atomContainer = sp.parseSmiles("C1C(N)CC1");
        Assert.assertEquals(2, PathTools.getMolecularGraphRadius(atomContainer));
        atomContainer = sp.parseSmiles("c12ccccc1cccc2");
        Assert.assertEquals(3, PathTools.getMolecularGraphRadius(atomContainer));
    }

    @Test
    public void testGetMolecularGraphDiameter_IAtomContainer() throws Exception {
        IAtomContainer atomContainer = sp.parseSmiles("CCCC");
        Assert.assertEquals(3, PathTools.getMolecularGraphDiameter(atomContainer));
        atomContainer = sp.parseSmiles("C1C(N)CC1");
        Assert.assertEquals(3, PathTools.getMolecularGraphDiameter(atomContainer));
        atomContainer = sp.parseSmiles("c12ccccc1cccc2");
        Assert.assertEquals(5, PathTools.getMolecularGraphDiameter(atomContainer));
    }

    @Test
    public void testComputeFloydAPSP_arrayintint() {
        int[][] start = new int[5][5]; // default to all zeros
        start[0][1] = 1;
        start[1][2] = 1;
        start[1][4] = 1;
        start[3][4] = 1;
        start[1][0] = 1;
        start[2][1] = 1;
        start[4][1] = 1;
        start[4][3] = 1;

        int[][] floydAPSP = PathTools.computeFloydAPSP(start);
        Assert.assertEquals(5, floydAPSP.length);
        Assert.assertEquals(5, floydAPSP[0].length);

        Assert.assertEquals(1, floydAPSP[0][1]);
        Assert.assertEquals(2, floydAPSP[0][2]);
        Assert.assertEquals(3, floydAPSP[0][3]);
        Assert.assertEquals(2, floydAPSP[0][4]);
        Assert.assertEquals(1, floydAPSP[1][2]);
        Assert.assertEquals(2, floydAPSP[1][3]);
        Assert.assertEquals(1, floydAPSP[1][4]);
        Assert.assertEquals(3, floydAPSP[2][3]);
        Assert.assertEquals(2, floydAPSP[2][4]);
        Assert.assertEquals(1, floydAPSP[3][4]);
    }

    @Test
    public void testComputeFloydAPSP_arraydoubledouble() {
        double[][] start = new double[5][5]; // default to all zeros
        start[0][1] = 1.0;
        start[1][2] = 1.0;
        start[1][4] = 2.0;
        start[3][4] = 1.0;
        start[1][0] = 1.0;
        start[2][1] = 1.0;
        start[4][1] = 2.0;
        start[4][3] = 1.0;

        int[][] floydAPSP = PathTools.computeFloydAPSP(start);
        Assert.assertEquals(5, floydAPSP.length);
        Assert.assertEquals(5, floydAPSP[0].length);

        Assert.assertEquals(1, floydAPSP[0][1]);
        Assert.assertEquals(2, floydAPSP[0][2]);
        Assert.assertEquals(3, floydAPSP[0][3]);
        Assert.assertEquals(2, floydAPSP[0][4]);
        Assert.assertEquals(1, floydAPSP[1][2]);
        Assert.assertEquals(2, floydAPSP[1][3]);
        Assert.assertEquals(1, floydAPSP[1][4]);
        Assert.assertEquals(3, floydAPSP[2][3]);
        Assert.assertEquals(2, floydAPSP[2][4]);
        Assert.assertEquals(1, floydAPSP[3][4]);
    }

    @Test
    public void testDepthFirstTargetSearch_IAtomContainer_IAtom_IAtom_IAtomContainer() throws Exception {
        IAtomContainer molecule = sp.parseSmiles("C(COF)(Br)NC");
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            atom.setFlag(CDKConstants.VISITED, false);
        }

        IAtomContainer paths = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom root = molecule.getAtom(0);
        IAtom target = null;

        atoms = molecule.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            if (atom.getSymbol().equals("F")) {
                target = atom;
                break;
            }
        }

        boolean status = PathTools.depthFirstTargetSearch(molecule, root, target, paths);
        Assert.assertTrue(status);
        Assert.assertEquals(3, paths.getAtomCount());
        Assert.assertEquals(target, paths.getAtom(2));
    }

    @Test
    public void testBreadthFirstSearch_IAtomContainer_List_IAtomContainer() throws Exception {
        IAtomContainer atomContainer;
        IAtom start;
        atomContainer = sp.parseSmiles("CCCC");
        PathTools.resetFlags(atomContainer);
        start = atomContainer.getAtom(0);
        List<IAtom> sphere = new ArrayList<IAtom>();
        sphere.add(start);
        IAtomContainer result = atomContainer.getBuilder().newInstance(IAtomContainer.class);
        PathTools.breadthFirstSearch(atomContainer, sphere, result);
        Assert.assertEquals(4, result.getAtomCount());
    }

    @Test
    public void testBreadthFirstSearch_IAtomContainer_List_IAtomContainer_int() throws Exception {
        IAtomContainer atomContainer;
        IAtom start;
        atomContainer = sp.parseSmiles("CCCC");
        PathTools.resetFlags(atomContainer);
        start = atomContainer.getAtom(0);
        List<IAtom> sphere = new ArrayList<IAtom>();
        sphere.add(start);
        IAtomContainer result = atomContainer.getBuilder().newInstance(IAtomContainer.class);
        PathTools.breadthFirstSearch(atomContainer, sphere, result, 1);
        Assert.assertEquals(2, result.getAtomCount());

        result = atomContainer.getBuilder().newInstance(IAtomContainer.class);
        PathTools.resetFlags(atomContainer);
        PathTools.breadthFirstSearch(atomContainer, sphere, result, 2);
        Assert.assertEquals(3, result.getAtomCount());

        result = atomContainer.getBuilder().newInstance(IAtomContainer.class);
        PathTools.resetFlags(atomContainer);
        PathTools.breadthFirstSearch(atomContainer, sphere, result, 3);
        Assert.assertEquals(4, result.getAtomCount());
    }

    @Test
    public void testFindClosestByBond() throws InvalidSmilesException {
        IAtomContainer container = sp.parseSmiles("CCN(CSCP)CCCOF");
        IAtom queryAtom = null;
        for (IAtom atom : container.atoms()) {
            if (atom.getSymbol().equals("N")) {
                queryAtom = atom;
                break;
            }
        }
        IAtom[] closestAtoms = PathTools.findClosestByBond(container, queryAtom, 2);
        for (IAtom atom : closestAtoms) {
            Assert.assertEquals("C", atom.getSymbol());
        }
    }

    @Test
    public void testGetPathsOfLengthUpto() throws InvalidSmilesException {
        IAtomContainer container = sp.parseSmiles("CCCC");
        List<List<IAtom>> paths = PathTools.getPathsOfLengthUpto(container, container.getAtom(0), 2);
        Assert.assertEquals(3, paths.size());

        container = sp.parseSmiles("C(C)CCC");
        paths = PathTools.getPathsOfLengthUpto(container, container.getAtom(0), 2);
        Assert.assertEquals(4, paths.size());
    }

    @Test
    public void testGetLimitedPathsOfLengthUpto() throws InvalidSmilesException {
        IAtomContainer container = sp.parseSmiles("CCCC");
        List<List<IAtom>> paths = PathTools.getPathsOfLengthUpto(container, container.getAtom(0), 2);
        Assert.assertEquals(3, paths.size());

        container = sp.parseSmiles("C(C)CCC");
        paths = PathTools.getPathsOfLengthUpto(container, container.getAtom(0), 2);
        Assert.assertEquals(4, paths.size());
    }

    @Test(expected = CDKException.class)
    public void testGetLimitedPathsOfLengthUpto_Exception() throws CDKException {
        IAtomContainer container = sp
                .parseSmiles("[B]1234[B]567[B]89%10[B]%11%12%13[B]%14%15%16[B]11([B]%17%18%19[B]%20%21%22[B]22%23[B]%24%25%26[B]%27%28%29[B]55([B]%30%31%32[B]88%33[B]%34%35%36[B]%37%38%39[B]%11%11([B]%40%41%42[B]%14%14%43[B]%44%45%46[B]%17%17([B]%47%48%49[B]%50%51%52[B]%20%20([B]%53%54%55[B]%24%24([B]%56%57%58[B]%27%27%59[B]%60%61%62[B]%30%30([B]%63%64%65[B]%34%34([B]%66%67%68[B]%37%37%69[B]%70%71%72[B]%40%40([B]%73%74%75[B]%44%44([B]%47%47%76[B]%77%78%79[B]%80%81%82[B]%50%50([B]%53%53%83[B]%84%85%86[B]%56%56([B]%87%88%89[B]%60%60([B]%63%63%90[B]%91%92%93[B]%66%66([B]%94%95%96[B]%70%70([B]%73%73%97[B]%77%77([B]%98%99%100[B]%80%80%101[B]%84%84([B]%87%87%102[B]%91%91([B]%94%98([B]%95%70%73%77%99)[B]%100%80%84%87%91)[B]%88%60%63%92%102)[B]%81%50%53%85%101)[B]%74%44%47%78%97)[B]%67%37%71%66%96)[B]%64%34%68%90%93)[B]%57%27%61%56%89)[B]%54%24%58%83%86)[B]%48%51%76%79%82)[B]%41%14%45%40%75)[B]%38%11%42%69%72)[B]%318%35%30%65)[B]%285%32%59%62)[B]%212%25%20%55)[B]%18%22%17%49%52)[B]%151%19%43%46)[B]9%12%33%36%39)[B]36%23%26%29)[B]47%10%13%16");
        PathTools.getLimitedPathsOfLengthUpto(container, container.getAtom(0), 8, 150);
    }
}
