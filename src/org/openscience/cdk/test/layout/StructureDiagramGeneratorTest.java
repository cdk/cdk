/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.test.layout;

import org.openscience.cdk.controller.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.templates.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import javax.vecmath.Vector2d;
import javax.vecmath.Point2d;
import junit.framework.*;

public class StructureDiagramGeneratorTest extends TestCase {
    
	MoleculeListViewer moleculeListViewer = null;

    public StructureDiagramGeneratorTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(StructureDiagramGeneratorTest.class);
    }
	
    public void runVisualTests() {
		moleculeListViewer = new MoleculeListViewer();
		showIt(MoleculeFactory.loadMolecule("data/mdl/reserpine.mol"), "Reserpine");
		showIt(MoleculeFactory.loadMolecule("data/mdl/four-ring-5x10.mol"), "5x10 condensed four membered rings");
		showIt(MoleculeFactory.loadMolecule("data/mdl/six-ring-4x4.mol"), "4x4 condensed six membered rings");
		showIt(MoleculeFactory.loadMolecule("data/mdl/polycarpol.mol"), "Polycarpol");
		showIt(MoleculeFactory.makeAlphaPinene(), "alpha-Pinene");
		showIt(MoleculeFactory.makeBiphenyl(), "Biphenyl");
		showIt(MoleculeFactory.make4x3CondensedRings(), "4x3CondensedRings");
		showIt(MoleculeFactory.makePhenylEthylBenzene(), "PhenylEthylBenzene");
		showIt(MoleculeFactory.makeSpiroRings(), "Spiro");
		showIt(MoleculeFactory.makeMethylDecaline(), "Methyldecaline");
		showIt(MoleculeFactory.makeBranchedAliphatic(), "Branched aliphatic");
		showIt(MoleculeFactory.makeDiamantane(), "Diamantane - A Problem! - Solve it! :-)");
		showIt(MoleculeFactory.makeEthylCyclohexane(), "Ethylcyclohexane");	
		showIt(MoleculeFactory.makeBicycloRings(), "Bicyclo-[2.2.2]-octane");
	}

	private boolean showIt(Molecule molecule, String name) {
		MoleculeViewer2D mv = new MoleculeViewer2D();
		try {
			mv.setAtomContainer(generateCoordinates(molecule));
			moleculeListViewer.addStructure(mv, name);
		} catch(Exception exc) {
			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
			return false;
		}
		return true;
	}

    public AtomContainer generateCoordinates(Molecule m) throws Exception {
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule(m);
        sdg.generateCoordinates(new Vector2d(0,1));
        return sdg.getMolecule();
    }
    
	public static void main(String[] args) {
		StructureDiagramGeneratorTest sdg = new StructureDiagramGeneratorTest("StructureDiagramGeneratorTest");
        sdg.runVisualTests();
	}

    public void testAlphaPinene() throws Exception {
        Molecule m = MoleculeFactory.makeAlphaPinene();
        AtomContainer ac = generateCoordinates(m);
    }
    
    public void testBiphenyl() throws Exception {
        Molecule m = MoleculeFactory.makeBiphenyl();
        AtomContainer ac = generateCoordinates(m);
    }
    
    public void test4x3CondensedRings() throws Exception {
        Molecule m = MoleculeFactory.make4x3CondensedRings();
        AtomContainer ac = generateCoordinates(m);
    }
    
    public void testPhenylEthylBenzene() throws Exception {
        Molecule m = MoleculeFactory.makePhenylEthylBenzene();
        AtomContainer ac = generateCoordinates(m);
    }
    
    public void testSpiroRings() throws Exception {
        Molecule m = MoleculeFactory.makeSpiroRings();
        AtomContainer ac = generateCoordinates(m);
    }
    
    public void testMethylDecaline() throws Exception {
        Molecule m = MoleculeFactory.makeMethylDecaline();
        AtomContainer ac = generateCoordinates(m);
    }
    
    public void testBranchedAliphatic() throws Exception {
        Molecule m = MoleculeFactory.makeBranchedAliphatic();
        AtomContainer ac = generateCoordinates(m);
    }
    
    public void testDiamantane() throws Exception {
        Molecule m = MoleculeFactory.makeDiamantane();
        AtomContainer ac = generateCoordinates(m);
    }
    
    public void testEthylCyclohexane() throws Exception {
        Molecule m = MoleculeFactory.makeEthylCyclohexane();	
        AtomContainer ac = generateCoordinates(m);
    }
    
    public void testBicycloRings() throws Exception {
        Molecule m = MoleculeFactory.makeBicycloRings();
        AtomContainer ac = generateCoordinates(m);
    }
    
	public void testBenzene() throws Exception{
		SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("c1ccccc1");
        AtomContainer ac = generateCoordinates(mol);
	}
    
	public void testBug780545() throws Exception{
        Molecule mol = new Molecule();
        mol.addAtom(new Atom("C"));
        AtomContainer ac = generateCoordinates(mol);
	}
}

