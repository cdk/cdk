/* $RCSfile$ 
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * @cdk.module test-atomtype
 */
public class PathToolsTest extends NewCDKTestCase {
    private static Molecule molecule;

    @BeforeClass
    public static void setUp() {
        molecule = MoleculeFactory.makeAlphaPinene();
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
    	Iterator<IAtom> atoms = atomContainer.atoms();
    	while (atoms.hasNext()) {
    		Assert.assertNull(atoms.next().getProperty(CDKConstants.VISITED));
    	}
    	Iterator<IBond> bonds = atomContainer.bonds();
    	while (bonds.hasNext()) {
    		Assert.assertNull(bonds.next().getProperty(CDKConstants.VISITED));
    	}
    }
    
    @Test
    public void testGetShortestPath_IAtomContainer_IAtom_IAtom() throws Exception {
        IAtomContainer atomContainer = null;
        IAtom start = null;
        IAtom end = null;
        List path = null;
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
    	IMolecule testMolecule = new Molecule();
    	reader.read(testMolecule);

    	ArrayList<IAtom> path = (ArrayList<IAtom>)PathTools.getShortestPath(testMolecule, 
    		testMolecule.getAtom(0), testMolecule.getAtom(9)
    	);
    	Assert.assertEquals(10, path.size());

    	path = (ArrayList<IAtom>)PathTools.getShortestPath(testMolecule, 
    		testMolecule.getAtom(1), testMolecule.getAtom(9)
    	);
    	Assert.assertEquals(9, path.size());

    	path = (ArrayList<IAtom>)PathTools.getShortestPath(testMolecule,
    		testMolecule.getAtom(9), testMolecule.getAtom(0)
    	);
    	Assert.assertEquals(10, path.size());
    }

    @Test
    public void testGetPathsOfLength_IAtomContainer_IAtom_int() throws Exception {
        IAtomContainer atomContainer = null;
        IAtom start = null;
        List paths = null;
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
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
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("c12ccccc1cccc2");

        IAtom start = atomContainer.getAtom(0);
        IAtom end = atomContainer.getAtom(2);
        List paths = PathTools.getAllPaths(atomContainer, start, end);

        Assert.assertEquals(3, paths.size());

        List path1 = (List) paths.get(0);
        List path2 = (List) paths.get(1);
        List path3 = (List) paths.get(2);

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
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
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
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("CCCC");
        Assert.assertEquals(2, PathTools.getMolecularGraphRadius(atomContainer));
        atomContainer = sp.parseSmiles("C1C(N)CC1");
        Assert.assertEquals(2, PathTools.getMolecularGraphRadius(atomContainer));
        atomContainer = sp.parseSmiles("c12ccccc1cccc2");
        Assert.assertEquals(3, PathTools.getMolecularGraphRadius(atomContainer));
    }

    @Test
    public void testGetMolecularGraphDiameter_IAtomContainer() throws Exception {
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
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
    public void testDepthFirstTargetSearch_IAtomContainer_IAtom_IAtom_IAtomContainer() {
    	Assert.fail("Missing JUnit test");
    }
    @Test
    public void testBreadthFirstSearch_IAtomContainer_List_IMolecule() {
    	Assert.fail("Missing JUnit test");
    }
    @Test
    public void testBreadthFirstSearch_IAtomContainer_List_IMolecule_int() {
    	Assert.fail("Missing JUnit test");
    }
}

