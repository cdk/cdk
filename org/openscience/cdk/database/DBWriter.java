/* DBWriter.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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
 * 
 */

package org.openscience.cdk.database;

import java.sql.*;
import java.util.*;
import java.io.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.*;



public class DBWriter implements ChemObjectWriter
{
	Connection con;
	StringWriter writer;
	CMLWriter cmlw;
	
	public DBWriter(Connection con)
	{
		this.con = con;
	}
	
	/**
	 *
	 *
	 * @param object 
	 */
	public void write(ChemObject object) throws UnsupportedChemObjectException 
	{
		if (object instanceof Molecule)
		{
			writeMolecule((Molecule)object);
		}
		else if (object instanceof SetOfMolecules) 
		{
			writeSetOfMolecules((SetOfMolecules)object);
		}
		else 
		{
		    throw new UnsupportedChemObjectException("Only supported SetOfMolecules and Molecule.");
		}
	}

    /**
     * Stores a Molecule to the database.
     *
     * @param   co The molecule to be stored
     */
    public void writeMolecule(Molecule mol) throws UnsupportedChemObjectException
    {
		PreparedStatement ps;
		//The Molecule is turned into a CML string
		writer = new StringWriter();
		cmlw = new CMLWriter(writer);
		cmlw.write(mol);
		String moleculeString = writer.toString();
		String[] elements = {"C","H","N","O","S","P","F","Cl","Br","I"};
		String elementFormula = SwissArmyKnife.generateElementFormula(mol, elements);
		System.out.println(elementFormula);
		System.out.println(elementFormula.substring(elementFormula.indexOf("C") + 1, elementFormula.indexOf("H")));
				
		try
		{
			con.setAutoCommit(false);
			ps = con.prepareStatement("INSERT INTO molecules VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, mol.getAutonomName());
			ps.setString(2, mol.getCasRN());
			ps.setString(3, mol.getBeilsteinRN());
			ps.setInt(4, new Integer(elementFormula.substring(elementFormula.indexOf("C") + 1, elementFormula.indexOf("H"))).intValue());
			ps.setInt(5, new Integer(elementFormula.substring(elementFormula.indexOf("H") + 1, elementFormula.indexOf("N"))).intValue());
			ps.setInt(6, new Integer(elementFormula.substring(elementFormula.indexOf("N") + 1, elementFormula.indexOf("O"))).intValue());
			ps.setInt(7, new Integer(elementFormula.substring(elementFormula.indexOf("O") + 1, elementFormula.indexOf("S"))).intValue());
			ps.setInt(8, new Integer(elementFormula.substring(elementFormula.indexOf("S") + 1, elementFormula.indexOf("P"))).intValue());
			ps.setInt(9, new Integer(elementFormula.substring(elementFormula.indexOf("P") + 1, elementFormula.indexOf("F"))).intValue());
			ps.setInt(10, new Integer(elementFormula.substring(elementFormula.indexOf("F") + 1, elementFormula.indexOf("Cl"))).intValue());
			ps.setInt(11, new Integer(elementFormula.substring(elementFormula.indexOf("Cl") + 2, elementFormula.indexOf("Br"))).intValue());
			ps.setInt(12, new Integer(elementFormula.substring(elementFormula.indexOf("Br") + 2, elementFormula.indexOf("I"))).intValue());
			ps.setInt(13, new Integer(elementFormula.substring(elementFormula.indexOf("I") + 1)).intValue());
			ps.setBytes(14, moleculeString.getBytes());
			ps.executeUpdate();
			ps.close();
			con.commit();
			con.setAutoCommit(true);
    	}
		catch(Exception exc)
    	{
        	System.out.println("Error while trying to add molecule to table");
			exc.printStackTrace();  
    	}
		
//	
//		for (int i = 0; i < mol.getChemNames().size(); i++)
//		{
//	    	try
//	    	{
//				ps = con.prepareStatement("INSERT INTO chemnames VALUES(?,?)");
//				ps.setBytes(1, moleculeString.getBytes());
//				ps.setString(2, mol.getChemName(i));
//				ps.execute();
//				ps.close();
//	    	}
//			catch(Exception exc)
//	    	{
//	        	System.out.println("Error while trying to add molecule to table");
//	        	System.out.println(exc);   
//	    	}
//		}
    }
	
	/**
	 * Stores a SetOfMolecules to the database.
	 *
	 * @param   co The molecule to be stored
	 */
	private void writeSetOfMolecules(SetOfMolecules som) throws UnsupportedChemObjectException
	{
		for (int i = 0; i < som.getMoleculeCount(); i++)
		{
			writeMolecule(som.getMolecule(i));
		}
	}
    	
}