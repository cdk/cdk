/* MDLWriter.java
 * 
 * $RCSfile$ 
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
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


import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import java.io.*;
import java.util.*;
import java.text.*;
import javax.vecmath.*;



/**
 * Writes an array of molecules to a MDL molfile.
 */

public class MDLWriter implements CDKConstants
{
	static BufferedWriter writer;
	
	
	/**
	 * Contructs a new MDLWriter that can write an array of Molecules to a given OutputStream
	 *
	 * @param   out  The OutputStream to write to
	 */
	public MDLWriter(FileOutputStream out)
	{
		try
		{
			writer = new BufferedWriter(new OutputStreamWriter(out));
		}
		catch (Exception exc)
		{
		}
	}
	
	
	/**
	 * Takes an object subclassed from ChemObject, e.g.Molecule and tries to 
	 * save it. If the specific implementation does not support a specific 
	 * ChemObject it will throw an Exception.
	 *
	 * @param   object  The object subclssed from ChemObject
	 * @exception   UnsupportedChemObjectException  
	 */
	public void write(ChemObject object) throws UnsupportedChemObjectException 
	{
		if (object instanceof SetOfMolecules)
		{
		    writeSetOfMolecules((SetOfMolecules)object);
		} 
		else if (object instanceof Molecule) 
		{
		    writeMolecule((Molecule)object);
		} 
		else 
		{
		    throw new UnsupportedChemObjectException("Only supported are ChemFile and Molecule.");
		}
	}
	
	

	/**
	 * Writes an array of Molecules to an OutputStream in MDL sdf format
	 *
	 * @param   molecules  Array of Molecules that is written to an OutputStream 
	 */
	private void  writeSetOfMolecules(SetOfMolecules som)
	{
		Molecule[] molecules = som.getMolecules();
		writeMolecule(molecules[0]);
		for (int i = 1; i <= som.getMoleculeCount() - 1; i++)
		{
			try
			{
			    writer.write("$$$$");
				writer.newLine();
				writeMolecule(molecules[i]);
			}
			catch (Exception exc)
			{
			}
		}
		try
		{		
			writer.close();
		}
		catch (Exception exc)
		{
		}		
	}
	
	

	

	/**
	 * Writes a Molecule to an OutputStream in MDL sdf format
	 *
	 * @param   molecule  Molecule that is written to an OutputStream 
	 */
	public static void writeMolecule(Molecule molecule)
	{
		int Bonorder, stereo;
		String line = "";
		int index = -1;
		int rows = 4;
		String title = molecule.getTitle();
		if (title == null) title = "";
		do
		{
			index = title.indexOf("\n",index + 1);
			rows--;
		}
		while (index != -1);
		try
		{
			writer.write(title);
			for(int i = 0; i < rows; i++)
			{
				writer.newLine();
			}
		    line += formatMDLInt(molecule.getAtomCount(), 3);
		    line += formatMDLInt(molecule.getBondCount(), 3);
		    line += "  0  0  0  0  0  0  0  0  1 V2000";
			writer.write(line);
			writer.newLine();
		    for (int f = 0; f < molecule.getAtomCount(); f++)
		    {
				Atom atom = molecule.getAtomAt(f);
		        line = "";
		        line += formatMDLFloat((float) atom.getX2D());
		        line += formatMDLFloat((float) atom.getY2D());
				line += " 0 ";                 // z coordinate handeled here
		        line += formatMDLString(molecule.getAtomAt(f).getElement().getSymbol(), 3);
		        line += " 0  0  0  0  0  0  0  0  0  0  0  0";
			    writer.write(line);
			    writer.newLine();
		    }
			for (int g = 0; g < molecule.getBondCount(); g++)
			{ 
				Bond bond = molecule.getBondAt(g);
				if (bond.getAtoms().length != 2) 
				{ 
					System.out.println("keine 2 Atome");
				}
				else
				{
					line = "";
					line += formatMDLInt(molecule.getAtomNumber(bond.getAtomAt(0)) + 1,3);
					line += formatMDLInt(molecule.getAtomNumber(bond.getAtomAt(1)) + 1,3);
					line += formatMDLInt(bond.getOrder(),3);
					line += " 0  0  0  0 ";
					writer.write(line);
					writer.newLine();
				}
			}
			writer.write("M END");
			writer.newLine();
		}
		catch (Exception exc)
		{
			exc.printStackTrace();     
		}
	}
			
			
			




	


	/**
	 * Formats an int to fit into the connectiontable and changes it to a String
	 *
	 * @param   i  The int to be formated
	 * @param   l  Length of the String
	 * @return     The String to be written into the connectiontable
	 */
    private static String formatMDLInt(int i, int l)
    {
        String s = "", fs = "";
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setParseIntegerOnly(true);
        nf.setMinimumIntegerDigits(1);
        nf.setMaximumIntegerDigits(l);
        nf.setGroupingUsed(false);
        s = nf.format(i);
        l = l - s.length();
        for (int f = 0; f < l; f++)
            fs += " ";
        fs += s;
        return fs;
    }
	
	


	/**
	 * Formats a float to fit into the connectiontable and changes it to a String
	 *
	 * @param   fl  The float to be formated
	 * @return      The String to be written into the connectiontable
	 */
    private static String formatMDLFloat(float fl)
    {
        String s = "", fs = "";
        int l;
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumIntegerDigits(1);
        nf.setMaximumIntegerDigits(4);
        nf.setMinimumFractionDigits(4);
        nf.setMaximumFractionDigits(4);
        nf.setGroupingUsed(false);
        s = nf.format(fl);
        l = 10 - s.length();
        for (int f = 0; f < l; f++)
            fs += " ";
        fs += s;
        return fs;
    }



	/**
	 * Formats a String to fit into the connectiontable
	 *
	 * @param   s    The String to be formated
	 * @param   le   The length of the String
	 * @return       The String to be written in the connectiontable
	 */
    private static String formatMDLString(String s, int le)
    {
        s = s.trim();
        if (s.length() > le)
            return s.substring(0, le);
        int l;
        l = le - s.length();
        for (int f = 0; f < l; f++)
            s += " ";
        return s;
    }

}
