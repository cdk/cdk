/* DBReader.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
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
 * 
 */

package org.openscience.cdk.database;


import java.sql.*;
import java.io.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.exception.*;



public class DBReader implements ChemObjectReader
{
	Connection con;
	String query = null;
	
	public DBReader(Connection con)
	{
		this.con = con;
	}




    public ChemObject read(ChemObject object) throws UnsupportedChemObjectException
    {
	    if (object instanceof Molecule) 
	    {
	        return (ChemObject)readMolecule();
	    } 
	    else 
	    {
	        throw new UnsupportedChemObjectException("Only supported Molecule.");
	    }
    }
	
	
	private ChemObject readMolecule()
	{
		Molecule mol = null;
		CMLReader cmlr;
		StringReader reader;
		Statement st;
		ResultSet rs;
		try
		{
			con.setAutoCommit(false);
			st = con.createStatement();
			System.out.println(query);
			rs = st.executeQuery(query);
			while (rs.next())
			{
				byte[] bytes = rs.getBytes(4);
				reader = new StringReader(new String(bytes));
				cmlr = new CMLReader(reader);
				mol = getMolecule((ChemFile)cmlr.read(new ChemFile()));
				
				mol.setAutonomName(rs.getString(1));
				mol.setCasRN(rs.getString(2));
				mol.setBeilsteinRN(rs.getString(3));
			}
			rs.close();
			st.close();
			con.commit();
			con.setAutoCommit(true);
		}
	
	    catch (Exception exc)
	    {
	    	exc.printStackTrace();
	    }
		return mol;
    }
		

	private Molecule getMolecule(ChemFile cf)
	{		
		ChemSequence cs = cf.getChemSequence(0);
		ChemModel cm = cs.getChemModel(0);
		SetOfMolecules som = cm.getSetOfMolecules();
		return som.getMolecule(0);
	}


	public void setQuery(String query)
	{
		this.query = query;
	}
	
}
