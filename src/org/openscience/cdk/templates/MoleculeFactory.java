/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.templates;

import java.io.FileInputStream;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.tools.LoggingTool;

/**
 * This class contains methods for generating simple organic molecules.
 *
 * @cdk.keyword templates
 */
public class MoleculeFactory {

    private static LoggingTool logger = null;
    
    static {
        logger = new LoggingTool(MoleculeFactory.class);
    }
    
	public static Molecule makeAlphaPinene() {
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9
		mol.addAtom(new Atom("C")); // 10

		mol.addBond(0, 1, 2.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 0, 1.0); // 6
		mol.addBond(0, 6, 1.0); // 7
		mol.addBond(3, 7, 1.0); // 8
		mol.addBond(5, 7, 1.0); // 9
		mol.addBond(7, 8, 1.0); // 10
		mol.addBond(7, 9, 1.0); // 11
		configureAtoms(mol);
		return mol;
	}

	/**
	 * Generate an Alkane (chain of carbons with no hydrogens) of a given length.
	 *
     * <p>This method was written by Stephen Tomkinson.
     *
	 * @param chainLength The number of carbon atoms to have in the chain.
	 * @return A molecule containing a bonded chain of carbons.
	 *
	 * @cdk.created 2003-08-15
	 */
  public static Molecule makeAlkane(int chainLength)
  {
    Molecule currentChain = new Molecule();

    //Add the initial atom
    currentChain.addAtom(new Atom("C"));

    //Add further atoms and bonds as needed, a pair at a time.
    for (int atomCount = 1; atomCount < chainLength; atomCount ++) {
        currentChain.addAtom(new Atom("C"));
        currentChain.addBond(atomCount, atomCount - 1, 1);
    }  

    return currentChain;
  }

	public static Molecule makeEthylCyclohexane()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8

		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 0, 1.0); // 6
		mol.addBond(0, 6, 1.0); // 7
		mol.addBond(6, 7, 1.0); // 8
		return mol;
	}


	public static Molecule makeCyclohexene()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6

		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 0, 2.0); // 6
		return mol;
	}

	public static Molecule makeCyclohexane()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6

		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 0, 1.0); // 6
		return mol;
	}

	public static Molecule makeCyclobutane()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4

		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 0, 1.0); // 4
		return mol;
	}

	public static Molecule makePropylCycloPropane()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 4
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 0, 1.0); // 3
		mol.addBond(2, 3, 1.0); // 4
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 4
		
		return mol;
	}
	
	
	public static Molecule makeBiphenyl()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1		
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9
		mol.addAtom(new Atom("C")); // 10
		mol.addAtom(new Atom("C")); // 11
	
		
		mol.addBond(0, 1, 2.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 2.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 2.0); // 5
		mol.addBond(5, 0, 1.0); // 6
	
		mol.addBond(0, 6, 1.0); // 7
		mol.addBond(6, 7, 1.0); // 8
		mol.addBond(7, 8, 2.0); // 5
		mol.addBond(8, 9, 1.0); // 6
		mol.addBond(9, 10, 2.0); // 7
		mol.addBond(10, 11, 1.0); // 8
		mol.addBond(11, 6, 2.0); // 5
		return mol;
	}
	

	public static Molecule makePhenylEthylBenzene()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1		
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9
		mol.addAtom(new Atom("C")); // 10
		mol.addAtom(new Atom("C")); // 11
		mol.addAtom(new Atom("C")); // 12
		mol.addAtom(new Atom("C")); // 13
		
		mol.addBond(0, 1, 2.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 2.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 2.0); // 5
		mol.addBond(5, 0, 1.0); // 6
	
		mol.addBond(0, 6, 1.0); // 7
		mol.addBond(6, 7, 1.0); // 8
		mol.addBond(7, 8, 1.0); // 5
		mol.addBond(8, 9, 1.0); // 6
		mol.addBond(9, 10, 2.0); // 7
		mol.addBond(10, 11, 1.0); // 8
		mol.addBond(11, 12, 2.0); // 5
		mol.addBond(12, 13, 1.0);
		mol.addBond(13, 8, 2.0); // 5
		return mol;
	}

	
	/* build a molecule from 4 condensed triangles */
	public static Molecule make4x3CondensedRings()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 0, 1.0); // 3
		mol.addBond(2, 3, 1.0); // 4
		mol.addBond(1, 3, 1.0); // 5
		mol.addBond(3, 4, 1.0); // 6
		mol.addBond(4, 2, 1.0); // 7
		mol.addBond(4, 5, 1.0); // 8
		mol.addBond(5, 3, 1.0); // 9
		
		return mol;
	}
	
	public static Molecule makeSpiroRings()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9

		
		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 6, 1.0); // 6
		mol.addBond(6, 0, 1.0); // 7
		mol.addBond(6, 7, 1.0); // 8
		mol.addBond(7, 8, 1.0); // 9
		mol.addBond(8, 9, 1.0); // 10
		mol.addBond(9, 6, 1.0); // 11
		return mol;
	}
	

	public static Molecule makeBicycloRings()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7

		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 0, 1.0); // 6
		mol.addBond(6, 0, 1.0); // 7
		mol.addBond(6, 7, 1.0); // 8
		mol.addBond(7, 3, 1.0); // 9
		return mol;
	}

	public static Molecule makeFusedRings()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9

		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 0, 1.0); // 6
		mol.addBond(5, 6, 1.0); // 7
		mol.addBond(6, 7, 1.0); // 8
		mol.addBond(7, 4, 1.0); // 9
		mol.addBond(8, 0, 1.0); // 10
		mol.addBond(9, 1, 1.0); // 11
		mol.addBond(9, 8, 1.0); // 11
		return mol;
	}

	public static Molecule makeMethylDecaline()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9
		mol.addAtom(new Atom("C")); // 10

		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 0, 1.0); // 6
		mol.addBond(5, 6, 1.0); // 7
		mol.addBond(6, 7, 1.0); // 8RingSet
		mol.addBond(7, 8, 1.0); // 9
		mol.addBond(8, 9, 1.0); // 10
		mol.addBond(9, 0, 1.0); // 11
		mol.addBond(5, 10, 1.0); // 12
		return mol;

	}

	public static Molecule makeEthylPropylPhenantren()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9
		mol.addAtom(new Atom("C")); // 10
		mol.addAtom(new Atom("C")); // 11
		mol.addAtom(new Atom("C")); // 12
		mol.addAtom(new Atom("C")); // 13
		mol.addAtom(new Atom("C")); // 14
		mol.addAtom(new Atom("C")); // 15
		mol.addAtom(new Atom("C")); // 16
		mol.addAtom(new Atom("C")); // 17
		mol.addAtom(new Atom("C")); // 18
		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 2.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 2.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 6, 2.0); // 6
		mol.addBond(6, 7, 1.0); // 8
		mol.addBond(7, 8, 2.0); // 9
		mol.addBond(8, 9, 1.0); // 10
		mol.addBond(9, 0, 2.0); // 11		
		mol.addBond(9, 4, 1.0); // 12
		mol.addBond(8, 10, 1.0); // 12
		mol.addBond(10, 11, 2.0); // 12
		mol.addBond(11, 12, 1.0); // 12
		mol.addBond(12, 13, 2.0); // 12
		mol.addBond(13, 7, 1.0); // 12
		mol.addBond(3, 14, 1.0); // 12
		mol.addBond(14, 15, 1.0); // 12
		mol.addBond(12, 16, 1.0); // 12		
		mol.addBond(16, 17, 1.0); // 12
		mol.addBond(17, 18, 1.0); // 12	
		configureAtoms(mol);
		return mol;
	}

	public static Molecule makeAzulene()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9
		
		mol.addBond(0, 1, 2.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 2.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 2.0); // 5
		mol.addBond(5, 6, 1.0); // 6
		mol.addBond(6, 7, 2.0); // 8
		mol.addBond(7, 8, 1.0); // 9
		mol.addBond(8, 9, 2.0); // 10
		mol.addBond(9, 5, 1.0); // 11
		mol.addBond(9, 0, 1.0); // 12
		
		return mol;
	}

	public static Molecule makeIndole()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("N")); // 8

		
		mol.addBond(0, 1, 2.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 2.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 2.0); // 5
		mol.addBond(5, 6, 1.0); // 6
		mol.addBond(6, 7, 2.0); // 8
		mol.addBond(7, 8, 1.0); // 9
		mol.addBond(0, 5, 1.0); // 11
		mol.addBond(8, 0, 1.0); // 12
		
		return mol;
	}

	
	public static Molecule makePyrrole()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("N")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 2.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 0, 2.0); // 5
		
		return mol;
	}

	public static Molecule makeThiazole()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("N")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("S")); // 3
		mol.addAtom(new Atom("C")); // 4
		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 2.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 0, 2.0); // 5
		
		return mol;
	}	

	
	public static Molecule makeSingleRing()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
//		mol.addAtom(new Atom("C")); // 6
//		mol.addAtom(new Atom("C")); // 7
//		mol.addAtom(new Atom("C")); // 8
//		mol.addAtom(new Atom("C")); // 9
		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 0, 1.0); // 6
//		mol.addBond(5, 6, 1.0); // 7
//		mol.addBond(6, 7, 1.0); // 8
//		mol.addBond(7, 4, 1.0); // 9
//		mol.addBond(8, 0, 1.0); // 10
//		mol.addBond(9, 1, 1.0); // 11		
		
			
		return mol;
	}
	

	public static Molecule makeDiamantane()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9
		mol.addAtom(new Atom("C")); // 10
		mol.addAtom(new Atom("C")); // 11
		mol.addAtom(new Atom("C")); // 12
		mol.addAtom(new Atom("C")); // 13
		
		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 0, 1.0); // 6
		mol.addBond(5, 6, 1.0); // 7
		mol.addBond(6, 9, 1.0); // 8
		mol.addBond(1, 7, 1.0); // 9
		mol.addBond(7, 9, 1.0); // 10
		mol.addBond(3, 8, 1.0); // 11		
		mol.addBond(8, 9, 1.0); // 12
		mol.addBond(0, 10, 1.0); // 13
		mol.addBond(10, 13, 1.0); // 14
		mol.addBond(2, 11, 1.0); // 15
		mol.addBond(11, 13, 1.0); // 16
		mol.addBond(4, 12, 1.0); // 17		
		mol.addBond(12, 13, 1.0); // 18		
				
		return mol;
	}


	public static Molecule makeBranchedAliphatic()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9
		mol.addAtom(new Atom("C")); // 10
		mol.addAtom(new Atom("C")); // 11
		mol.addAtom(new Atom("C")); // 12
		mol.addAtom(new Atom("C")); // 13
		mol.addAtom(new Atom("C")); // 14
		mol.addAtom(new Atom("C")); // 15
		mol.addAtom(new Atom("C")); // 16
		mol.addAtom(new Atom("C")); // 17
		mol.addAtom(new Atom("C")); // 18
		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 2.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(2, 6, 1.0); // 6
		mol.addBond(6, 7, 1.0); // 7
		mol.addBond(7, 8, 1.0); // 8
		mol.addBond(6, 9, 1.0); // 9
		mol.addBond(6, 10, 1.0); // 10
		mol.addBond(10, 11, 1.0); // 11
		mol.addBond(8, 12, 3.0); // 12
		mol.addBond(12, 13, 1.0); // 13
		mol.addBond(11, 14, 1.0); // 14
		mol.addBond(9, 15, 1.0);
		mol.addBond(15, 16, 2.0);
		mol.addBond(16, 17, 2.0);
		mol.addBond(17, 18, 1.0);
		
		
		return mol;
	}

	public static Molecule makeBenzene()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 2.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 2.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 0, 2.0); // 6
		return mol;
	}
	
	public static Molecule makeQuinone()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("O")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("O")); // 7
		
		mol.addBond(0, 1, 2.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 2.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 6, 2.0); // 6
		mol.addBond(6, 1, 1.0); // 7
		mol.addBond(4, 7, 2.0); // 8
		return mol;
	}
	
	
	public static org.openscience.cdk.interfaces.Molecule loadMolecule(String inFile)
	{
		MDLReader mr = null;
		ChemFile chemFile = null;
		org.openscience.cdk.interfaces.IChemSequence chemSequence = null;
		org.openscience.cdk.interfaces.IChemModel chemModel = null;
		org.openscience.cdk.interfaces.SetOfMolecules setOfMolecules = null;
		org.openscience.cdk.interfaces.Molecule molecule = null;
		try
		{
			FileInputStream fis = new FileInputStream(inFile);
			mr = new MDLReader(fis);
			chemFile = (ChemFile)mr.read((ChemObject)new ChemFile());
			fis.close();
			chemSequence = chemFile.getChemSequence(0);
			chemModel = chemSequence.getChemModel(0);
			setOfMolecules = chemModel.getSetOfMolecules();
			molecule = setOfMolecules.getMolecule(0);
			for (int i = 0; i < molecule.getAtomCount(); i++)
			{
				molecule.getAtomAt(i).setPoint2d(null);
			}
		}
		catch(Exception exc)
		{
			// we just return null if something went wrong
            logger.error("An exception occured while loading a molecule: " + inFile);
            logger.debug(exc);
		}
		
		return molecule;
	}

	private static void configureAtoms(Molecule mol)
	{
		try
		{
            IsotopeFactory.getInstance(mol.getBuilder()).configureAtoms(mol);
		}
		catch(Exception exc)
		{
            logger.error("Could not configure molecule!");
            logger.debug(exc);
		}
	}

}

