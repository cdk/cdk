/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.io;

import org.openscience.cdk.exception.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.*;
import java.io.*;
import java.util.*;
import javax.vecmath.*;

/**
 * Reads a molecule from an MDL Molfile or SDF file.
 *
 * References:
 *   <a href="http://cdk.sf.net/biblio.html#DAL92">DAL92</a>
 *
 * @author     steinbeck
 * @created    October 2, 2000
 *
 * @keyword file format, MDL molfile
 * @keyword file format, SDF
 */
public class MDLReader implements ChemObjectReader {

	boolean debug = false;
	BufferedReader input;

    IsotopeFactory elemfact;

	/**
	 * Contructs a new MDLReader that can read Molecule from a given InputStream
	 *
	 * @param   in  The InputStream to read from
	 */
	public MDLReader(InputStream in) {
		this(new InputStreamReader(in));
	}

	/**
	 * Contructs a new MDLReader that can read Molecule from a given InputStream
	 *
	 * @param   in  The Reader to read from
	 */
	public MDLReader(Reader in) {
		input = new BufferedReader(in);
        try {
            elemfact = IsotopeFactory.getInstance();
        } catch (Exception e) {
			// cannot load ElementFactory data
        }
	}


	/**
	 * Takes an object which subclasses ChemObject, e.g.Molecule, and will read this
	 * (from file, database, internet etc). If the specific implementation does not
	 * support a specific ChemObject it will throw an Exception.
	 *
	 * @param   object  The object that subclasses ChemObject
	 * @return   The ChemObject read
	 * @exception   UnsupportedChemObjectException
	 */
    public ChemObject read(ChemObject object) throws UnsupportedChemObjectException
	{
		if (object instanceof ChemFile)
		{
		    return (ChemObject)readChemFile();
		} 
		else if (object instanceof Molecule) 
		{
		    return (ChemObject)readMolecule();
		} 
		else 
		{
		    throw new UnsupportedChemObjectException("Only supported are ChemFile and Molecule.");
		}
    }
	
	/**
	 * Read a ChemFile from a file in MDL sd format
	 *
	 * @return The ChemFile that was read from the MDL file.    
	 */
	private ChemFile readChemFile()
	{
		ChemFile chemFile = new ChemFile();
		ChemSequence chemSequence = new ChemSequence();
		ChemModel chemModel = new ChemModel();
		SetOfMolecules setOfMolecules = new SetOfMolecules();
        Molecule m = readMolecule();
        if (m != null) {
            setOfMolecules.addMolecule(m);
        }
		String str;
		try {
			do {
				str = new String(input.readLine());
				if (str.equals("$$$$")) {
                    m = readMolecule();
                    /** if reading of molecule fails, skip the molecule
                        and continue **/
                    if (m != null) {
                        setOfMolecules.addMolecule(m);
                    }
                }
			}
			while (input.ready());
		} catch (Exception exc) {
            // exc.printStackTrace();
		}
		try {
			input.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		chemModel.setSetOfMolecules(setOfMolecules);
		chemSequence.addChemModel(chemModel);
		chemFile.addChemSequence(chemSequence);

		return chemFile;
	}



	/**
	 * Read a Molecule from a file in MDL sd format
	 *
	 * @return The Molecule that was read from the MDL file.    
	 */
	private Molecule readMolecule()
	{
	    int atoms = 0, bonds = 0, atom1 = 0, atom2 = 0, order = 0, stereo = 0;
	    double x=0, y=0, z=0;
	    int[][] conMat = new int[0][0];
	    String help;
	    Molecule molecule = new Molecule();
		Bond bond;
		Atom atom;
		
	    try
	    {
	        String title = new String(input.readLine()+"\n"+input.readLine()+"\n"+input.readLine());
		    molecule.setProperty(CDKConstants.TITLE, title);
	        StringBuffer strBuff = new StringBuffer(input.readLine());
	        strBuff.insert(3, " ");
	        StringTokenizer strTok = new StringTokenizer(strBuff.toString());
	        atoms = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
			if (debug) System.out.println("Atomcount: " + atoms);
	        bonds = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
			if (debug) System.out.println("Bondcount: " + bonds);
	        for (int f = 0; f < atoms; f++)
	        {
	            strBuff = new StringBuffer(input.readLine());
	            strTok = new StringTokenizer(strBuff.toString().trim());
	            x = new Double(strTok.nextToken()).doubleValue();
	            y = new Double(strTok.nextToken()).doubleValue();
	            z = new Double(strTok.nextToken()).doubleValue();
				if (debug) System.out.println("Coordinates: " + x + "; " + y + "; " + z);
				atom = new Atom(strTok.nextToken(), new Point3d(x, y, z));
				atom.setPoint2D(new Point2d(x, y));
                elemfact.configure(atom);
	            molecule.addAtom(atom);
	        }
	        for (int f = 0; f < bonds; f++)
	        {
	            strBuff = new StringBuffer(input.readLine());
	            strBuff.insert(3, " ");
	            strBuff.insert(7, " ");
	            strBuff.insert(11, " ");
	            strBuff.insert(15, " ");
	            strTok = new StringTokenizer(strBuff.toString());
	            atom1 = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
	            atom2 = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
	            order = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
	            stereo = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
				if (debug) System.out.println("Bond: " + atom1 + " - " + atom2 + "; order " + order);
	            if (stereo == 1)
	            {
	                // MDL up bond
	                stereo = CDKConstants.STEREO_BOND_UP;
	            }
	            else if (stereo == 6)
	            {
	                // MDL down bond
					stereo = CDKConstants.STEREO_BOND_DOWN;
	            }
				molecule.addBond(atom1 - 1, atom2 - 1, order, stereo);
	        }
	    }
	    catch (Exception e)
	    {
	        System.err.println("Error while reading MDL Molfile.");
	        System.err.println("Reason for failure: ");
			e.printStackTrace();
            molecule = null;
	    }
		return molecule;
	}
}

