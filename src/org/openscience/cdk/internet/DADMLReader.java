/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
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
 *  */
package org.openscience.cdk.internet;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.exception.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.vecmath.*;
import org.openscience.dadml.*;
import org.openscience.dadml.filereaders.*;
import org.openscience.dadml.tools.*;

/**
 * Reads a molecule from a DADML super database.
 *
 * <p>Database Access Definition Markup Language (DADML) is an XML
 * application that makes it possible to define how databases can be accessed
 * via URLs.
 *
 * @author     egonw
 * @created    December 18th, 2001
 * @keyword    internet
 * @keyword    database
 */
public class DADMLReader implements ChemObjectReader {

    String superdb;
    String index = "CAS-NUMBER";
	String casno = "50-00-0";

    private org.openscience.cdk.tools.LoggingTool logger;
    private static final String sax2parser = "org.apache.xerces.parsers.SAXParser";

	/**
	 * Contructs a new DADMLReader that can read Molecule from the internet
	 *
	 * @param   supersb DADML super database to look up structure from
	 */
	public DADMLReader(String superdb) {
		logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
		this.superdb = superdb;
	}

	/**
	 * Sets the query
	 *
	 * @param   index   Index type (e.g. CAS-NUMBER)
	 * @param   value   Index of molecule to download (e.g. 50-00-0)
	 */
	public void setQuery(String index, String value) {
		this.index = index;
		this.casno = value;
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
    public ChemObject read(ChemObject object) throws UnsupportedChemObjectException 	{
        if (object instanceof Molecule) {
		    return (ChemObject)readMolecule();
		} else {
		    throw new UnsupportedChemObjectException("Only supported is Molecule.");
		}
    }

	/**
	 * Read a Molecule from a DADML super database
	 *
	 * @return The Molecule that was read
	 */
	private Molecule readMolecule() {
	    boolean found = false; // this is true when a structure is downloaded
	    boolean done = false;  // this is true when all URLS have been tested

		Molecule molecule = new Molecule();
		DBLIST dblist = new DBLIST();
        try {
		    logger.info("Downloading DADML super database: " + this.superdb);
		    // Proxy authorization has to be ported from JChemPaint
			// for now, do without authorization
			dblist = DBLISTFileReader.read(superdb, sax2parser);
		} catch (Exception supererror) {
		    logger.error(supererror.toString());
		}
		Enumeration dbases = dblist.databases();
        while (!found && !done && dbases.hasMoreElements()) {
		    DATABASE db = (DATABASE)dbases.nextElement();
		    String dburl = db.getURL()+db.getDefinition();
		    DBDEF dbdef = new DBDEF();
		    // Proxy authorization has to be ported from JChemPaint
			// for now, do without authorization
		    try {
			    logger.info("Downloading: " + dburl);
			    // do without authorization
			    dbdef = DBDEFFileReader.read(dburl, sax2parser);
		    } catch (Exception deferror) {
			    System.err.println(deferror.toString());
		    }
		    if (DBDEFInfo.hasINDEX(dbdef, index)) {
			  // oke, find a nice URL to use for download
			  logger.debug("Trying: " + dbdef.getTITLE());
			  Enumeration fields = dbdef.fields();
			  while (fields.hasMoreElements()) {
			    FIELD f = (FIELD)fields.nextElement();
			    String mime = f.getMIMETYPE();
			    String ftype = f.getTYPE();
			    if ((mime.equals("chemical/x-mdl-mol") ||
				      mime.equals("chemical/x-pdb") ||
				      mime.equals("chemical/x-cml")) &&
					  (ftype.equals("3DSTRUCTURE") ||
					   ftype.equals("2DSTRUCTURE"))) {
				  logger.info("Accepted: " + f.getMIMETYPE() + "," + f.getTYPE());
				  Enumeration indices = f.getINDEX();
				  while (indices.hasMoreElements()) {
				    INDEX ind = (INDEX)indices.nextElement();
				    if (ind.getTYPE().equals(index)) {
					  String url = dbdef.getURL() + ind.getACCESS_PREFIX() + casno +
					                     ind.getACCESS_SUFFIX();
					  logger.info("Downloading: " + url);
					  try {
					    URL u = new URL(url);
						// this has to be reformulated
					    molecule = this.downloadURL(u, mime);
					  } catch (MalformedURLException mue) {
					    logger.error("Malformed URL" + mue);
					  } catch (Exception fnfe) {
					    logger.error("File Not Found." + fnfe);
					  }
				    }
				  }
			    } else {
                  // reject other mime types && type structures
				  logger.info("Rejected: " + f.getMIMETYPE() + "," + f.getTYPE());
			    }
			  }
		   }
		}
		return molecule;
	}

	private Molecule downloadURL(URL u, String mime) {
        Molecule m = new Molecule();
		try {
		    URLConnection connection = u.openConnection();
	        BufferedReader br = new BufferedReader(
                 new InputStreamReader(connection.getInputStream()));
			if (mime.equals("chemical/x-cml")) 	{
                CMLReader reader = new CMLReader(br);
                ChemFile cf = (ChemFile)reader.read((ChemObject)new ChemFile());
				logger.debug("#sequences: " + cf.getChemSequenceCount());
                ChemSequence chemSequence = cf.getChemSequence(0);
				logger.debug("#models in sequence: " + chemSequence.getChemModelCount());
                ChemModel chemModel = chemSequence.getChemModel(0);
                SetOfMolecules setOfMolecules = chemModel.getSetOfMolecules();
				logger.debug("#mols in model: " + setOfMolecules.getMoleculeCount());
                m = setOfMolecules.getMolecule(0);
			} else if (mime.equals("chemical/x-mdl-mol")) {
                MDLReader reader = new MDLReader(br);
                m = (Molecule)reader.read((ChemObject)m);
			}
	    } catch (UnsupportedChemObjectException e) {
	        logger.error("Unsupported ChemObject type: " + e.toString());
	    } catch (FileNotFoundException e) {
	        logger.error("File not found: " + e.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return m;
	}

}
