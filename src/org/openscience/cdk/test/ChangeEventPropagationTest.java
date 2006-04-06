/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
 * 
 */

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;


/**
 * Checks the propagation of ChangeEvents through a 
 * nested set of objects.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.ChemFile
 */
public class ChangeEventPropagationTest extends CDKTestCase {

    public ChangeEventPropagationTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(ChangeEventPropagationTest.class);
    }
    
    public void testPropagation() {
        ChemFile cf = new ChemFile();
	ChemSequence cs = new ChemSequence();
	ChemModel cm = new ChemModel();
	SetOfMolecules som = new SetOfMolecules();
        Molecule mol = new Molecule();
	Atom a1 = new Atom("C");
	Atom a2 = new Atom("C");
	Bond b1 = new Bond(a1, a2);
	mol.addAtom(a1);
	mol.addAtom(a2);
	mol.addBond(b1);
	som.addMolecule(mol);
	cm.setSetOfMolecules(som);
	cs.addChemModel(cm);
	cf.addChemSequence(cs);
	TestListener ts = new TestListener();
	cf.addListener(ts);
	a2.setSymbol("N");
	assertTrue(ts.changedObject instanceof Atom);
	assertTrue(((Atom)ts.changedObject).getSymbol().equals("N"));
    }

    class TestListener implements IChemObjectListener
    {
	    ChemObject changedObject = null;
	    
	    public void stateChanged(IChemObjectChangeEvent evt)
	    {
		    changedObject = (ChemObject)evt.getSource();
	    }
    }
}
