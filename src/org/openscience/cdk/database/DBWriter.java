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

/**
 *  Writer that is used to store molecules in JDBC databases.
 **/
public class DBWriter implements ChemObjectWriter {

	private org.openscience.cdk.tools.LoggingTool logger;

	private Connection con;
	private StringWriter writer;
	private CMLWriter cmlw;

	public DBWriter(Connection con) {
		this.logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
		this.con = con;
	}


    /**
     * Flushes the output and closes this object
     */
    public void close() throws IOException {
        // FIXME: connection should be closed
    }

    public ChemObject highestSupportedChemObject() {
        return new SetOfMolecules();
    };

	public void write(ChemObject object) throws CDKException {
		if (object instanceof Molecule) {
			writeMolecule((Molecule)object);
		} else if (object instanceof SetOfMolecules) {
			writeSetOfMolecules((SetOfMolecules)object);
		} else {
		    throw new UnsupportedChemObjectException("Only supported SetOfMolecules and Molecule.");
		}
	}

    /**
     * Stores a Molecule in the database.
     *
     * @param   mol    The molecule to be stored
     */
    public void writeMolecule(Molecule mol) throws CDKException {
		PreparedStatement ps;
		// The Molecule is turned into a CML string
		writer = new StringWriter();
		cmlw = new CMLWriter(writer);
		cmlw.write(mol);
		String moleculeString = writer.toString();
		// String[] elements = {"C","H","N","O","S","P","F","Cl","Br","I"};
		// String elementFormula = SwissArmyKnife.generateElementFormula(mol, elements);
		// System.out.println(elementFormula);
		// System.out.println(elementFormula.substring(elementFormula.indexOf("C") + 1, elementFormula.indexOf("H")));

		try {
			con.setAutoCommit(false);
			logger.info("Inserting molecule into molecules...");
			ps = con.prepareStatement("INSERT INTO molecules VALUES('', ?)");
			ps.setString(1, moleculeString);
			logger.debug("SQL: " + ps.toString());
			ps.executeUpdate();
			ps.close();
			logger.info("done");
			con.commit();

			ps = con.prepareStatement("INSERT INTO chemnames VALUES('', ?, ?)");
			logger.info("Inserting molecule into chemnames...");
			ps.setString(1, (String)mol.getProperty(CDKConstants.AUTONOMNAME));
			ps.setString(2, "");
			logger.debug("SQL: " + ps.toString());
			ps.executeUpdate();
			ps.close();
			logger.info("done");
			con.commit();

			ps = con.prepareStatement("INSERT INTO indices VALUES('', ?, ?)");
			logger.info("Inserting molecule into indices...");
			ps.setString(1, (String)mol.getProperty(CDKConstants.CASRN));
			ps.setString(2, (String)mol.getProperty(CDKConstants.BEILSTEINRN));
			logger.debug("SQL: " + ps.toString());
			ps.executeUpdate();
			ps.close();
			logger.info("done");
			con.commit();

			con.setAutoCommit(true);
    	} catch(Exception exc) {
        	System.out.println("Error while trying to add molecule to table");
		logger.error("Error while trying to add molecule to table");
		exc.printStackTrace();
		logger.error(exc.toString());
	}

    }

	/**
	 * Stores a SetOfMolecules to the database.
	 *
	 * @param  som    The set of molecules to be stored
	 */
	private void writeSetOfMolecules(SetOfMolecules som) throws CDKException
	{
		for (int i = 0; i < som.getMoleculeCount(); i++)
		{
			writeMolecule(som.getMolecule(i));
		}
	}
    	
}
