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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 * 
 */
package org.openscience.cdk.database;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.UnsupportedChemObjectException;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.formats.IChemFormat;

/**
 *  Writer that is used to store molecules in JDBC databases.
 *
 * @cdk.module orphaned
 */
public class DBWriter {

	private org.openscience.cdk.tools.LoggingTool logger;

	private Connection con;
	private StringWriter writer;
	private CMLWriter cmlw;

	public DBWriter(Connection con) {
		this.logger = new org.openscience.cdk.tools.LoggingTool(this);
		this.con = con;
	}

    public IChemFormat getFormat() {
        return new IChemFormat() {
            public String getFormatName() {
                return "JDBC database";
            }
            public String getReaderClassName() { return null; };
            public String getWriterClassName() { return null; };
        };
    }
    
    /**
     * Flushes the output and closes this object
     */
    public void close() throws IOException {
        // FIXME: connection should be closed
    }

    public IChemObject highestSupportedChemObject() {
        return new org.openscience.cdk.SetOfMolecules();
    };

	public void write(IChemObject object) throws CDKException {
		if (object instanceof IMolecule) {
			writeMolecule((IMolecule)object);
		} else if (object instanceof ISetOfMolecules) {
			writeSetOfMolecules((ISetOfMolecules)object);
		} else {
		    throw new UnsupportedChemObjectException("Only supported SetOfMolecules and Molecule.");
		}
	}

    /**
     * Stores a Molecule in the database.
     *
     * @param   mol    The molecule to be stored
     */
    public void writeMolecule(IMolecule mol) throws CDKException {
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
	private void writeSetOfMolecules(ISetOfMolecules som) throws CDKException
	{
		for (int i = 0; i < som.getMoleculeCount(); i++)
		{
			writeMolecule(som.getMolecule(i));
		}
	}
    	
}
