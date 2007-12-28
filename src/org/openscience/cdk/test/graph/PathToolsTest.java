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
package org.openscience.cdk.test.graph;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-atomtype
 */
public class PathToolsTest extends CDKTestCase {
    private Molecule molecule;

    public PathToolsTest(String name) {
        super(name);
    }

    public void setUp() {
        molecule = MoleculeFactory.makeAlphaPinene();
    }

    public static Test suite() {
        return new TestSuite(PathToolsTest.class);
    }

    public void testBreadthFirstTargetSearch_IAtomContainer_Vector_IAtom_int_int() {
        org.openscience.cdk.interfaces.IAtom atom1 = molecule.getAtom(0);
        org.openscience.cdk.interfaces.IAtom atom2 = molecule.getAtom(8);
        Vector<IAtom> sphere = new Vector<IAtom>();
        sphere.addElement(atom1);
        int length = PathTools.breadthFirstTargetSearch(molecule, sphere, atom2, 0, 3);
        //logger.debug("PathLengthTest->length: " + length);
        assertEquals(3, length);
    }

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
        assertEquals(4, path.size());

        atomContainer = sp.parseSmiles("CC(N)CC");
        start = atomContainer.getAtom(0);
        end = atomContainer.getAtom(2);
        path = PathTools.getShortestPath(atomContainer, start, end);
        assertEquals(3, path.size());

        atomContainer = sp.parseSmiles("C1C(N)CC1");
        start = atomContainer.getAtom(0);
        end = atomContainer.getAtom(2);
        path = PathTools.getShortestPath(atomContainer, start, end);
        assertEquals(3, path.size());
    }

    public void testGetShortestPath_Middle() throws Exception {
    	String filename = "data/mdl/shortest_path_test.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	MDLV2000Reader reader = new MDLV2000Reader(ins);
    	IMolecule testMolecule = new Molecule();
    	reader.read(testMolecule);

    	ArrayList<IAtom> path = (ArrayList<IAtom>)PathTools.getShortestPath(testMolecule, 
    		testMolecule.getAtom(0), testMolecule.getAtom(9)
    	);
    	assertEquals(10, path.size());

    	path = (ArrayList<IAtom>)PathTools.getShortestPath(testMolecule, 
    		testMolecule.getAtom(1), testMolecule.getAtom(9)
    	);
    	assertEquals(9, path.size());

    	path = (ArrayList<IAtom>)PathTools.getShortestPath(testMolecule,
    		testMolecule.getAtom(9), testMolecule.getAtom(0)
    	);
    	assertEquals(10, path.size());
    }
        

    public void testGetPathsOfLength_IAtomContainer_IAtom_int() throws Exception {
        IAtomContainer atomContainer = null;
        IAtom start = null;
        List paths = null;
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        atomContainer = sp.parseSmiles("c1cc2ccccc2cc1");
        start = atomContainer.getAtom(0);
        paths = PathTools.getPathsOfLength(atomContainer, start, 1);
        assertEquals(2, paths.size());

        atomContainer = sp.parseSmiles("Cc1cc2ccccc2cc1");
        start = atomContainer.getAtom(0);
        paths = PathTools.getPathsOfLength(atomContainer, start, 1);
        assertEquals(1, paths.size());
    }

    public void testGetAllPaths_IAtomContainer_IAtom_IAtom() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("c12ccccc1cccc2");

        IAtom start = atomContainer.getAtom(0);
        IAtom end = atomContainer.getAtom(2);
        List paths = PathTools.getAllPaths(atomContainer, start, end);

        assertEquals(3, paths.size());

        List path1 = (List) paths.get(0);
        List path2 = (List) paths.get(1);
        List path3 = (List) paths.get(2);

        assertEquals(start, path1.get(0));
        assertEquals(atomContainer.getAtom(1), path1.get(1));
        assertEquals(end, path1.get(2));

        assertEquals(start, path2.get(0));
        assertEquals(atomContainer.getAtom(5), path2.get(1));
        assertEquals(atomContainer.getAtom(4), path2.get(2));
        assertEquals(atomContainer.getAtom(3), path2.get(3));
        assertEquals(end, path2.get(4));
        assertNotNull(path3);
    }

    public void testGetVertexCountAtDistance_IAtomContainer_int() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("c12ccccc1cccc2");
        assertEquals(11, PathTools.getVertexCountAtDistance(atomContainer, 1));
        assertEquals(14, PathTools.getVertexCountAtDistance(atomContainer, 2));
    }

    public void testGetInt2DColumnSum_arrayintint() {
    	int[][] start = new int[2][2];
    	start[0][0] = 5;
    	start[0][1] = 3;
    	start[1][0] = 1;
    	start[1][1] = 2;
    	
    	assertEquals(8, PathTools.getInt2DColumnSum(start)[0]);
    	assertEquals(3, PathTools.getInt2DColumnSum(start)[1]);
    }
    
    public void testGetMolecularGraphRadius_IAtomContainer() throws Exception {
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("CCCC");
        assertEquals(2, PathTools.getMolecularGraphRadius(atomContainer));
        atomContainer = sp.parseSmiles("C1C(N)CC1");
        assertEquals(2, PathTools.getMolecularGraphRadius(atomContainer));
        atomContainer = sp.parseSmiles("c12ccccc1cccc2");
        assertEquals(3, PathTools.getMolecularGraphRadius(atomContainer));
    }

    public void testGetMolecularGraphDiameter_IAtomContainer() throws Exception {
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("CCCC");
        assertEquals(3, PathTools.getMolecularGraphDiameter(atomContainer));
        atomContainer = sp.parseSmiles("C1C(N)CC1");
        assertEquals(3, PathTools.getMolecularGraphDiameter(atomContainer));
        atomContainer = sp.parseSmiles("c12ccccc1cccc2");
        assertEquals(5, PathTools.getMolecularGraphDiameter(atomContainer));
    }

    public void testComputeFloydAPSP_arrayintint() {
    	fail("Missing JUnit test");
    }
    public void testComputeFloydAPSP_arraydoubledouble() {
    	fail("Missing JUnit test");
    }
    public void testDepthFirstTargetSearch_IAtomContainer_IAtom_IAtom_IAtomContainer() {
    	fail("Missing JUnit test");
    }
    public void testBreadthFirstSearch_IAtomContainer_Vector_IMolecule() {
    	fail("Missing JUnit test");
    }
    public void testBreadthFirstSearch_IAtomContainer_Vector_IMolecule_int() {
    	fail("Missing JUnit test");
    }
    public void testFindClosestByBond_IAtomContainer_IAtom_int() {
    	fail("Missing JUnit test");
    }
    public void testResetFlags_IAtomContainer() {
    	fail("Missing JUnit test");
    }
}

