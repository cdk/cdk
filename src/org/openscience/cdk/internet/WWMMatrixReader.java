/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.internet;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.renderer.MoleculeListViewer;
import org.openscience.cdk.renderer.MoleculeViewer2D;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import javax.vecmath.*;

/**
 * @author Yong Zhang <yz237@cam.ac.uk>        // Xindice part
 * @author Egon Willighagen <elw38@cam.ac.uk>
 */
public class WWMMatrixReader implements ChemObjectReader {

    String server = "wwmm.cam.ac.uk:8080";
    
    String index = "IChI";
	String query = "C4,";

    private org.openscience.cdk.tools.LoggingTool logger;

	public WWMMatrixReader() {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
	}
    
	public WWMMatrixReader(String server) {
        this();
        this.server = server;
	}
    
	/**
	 * Sets the query.
	 *
	 * @param   index   Index type (e.g. IChI)
	 * @param   value   Index of molecule to download (e.g. 'C4,')
	 */
	public void setQuery(String index, String value) {
		this.index = index;
		this.query = value;
	}
    
    
    public ChemObject read(ChemObject object) throws UnsupportedChemObjectException {
        if (object instanceof Molecule) {
		    return (ChemObject)readMolecule(server, query);
		} else {
		    throw new UnsupportedChemObjectException("Only supported is Molecule.");
		}
    }
    
    public static void main(String[] args) throws Exception {
        WWMMatrixReader wwmm = new WWMMatrixReader();
        if (args.length != 2) {
            System.out.println("WWMMatrixReader <server> <IChI>");
            System.out.println();
            System.out.println("   e.g. wwmm.ch.cam.ac.uk:8080 'C4,'");
            System.exit(1);
        }
        String server = args[0];
        String ichi = args[1];
        System.out.println("Server: " + server);
        System.out.println("IChI  : " + ichi);
        
        Molecule m = wwmm.readMolecule(server, ichi);
        if (!GeometryTools.has2DCoordinates(m)) {
			StructureDiagramGenerator sdg = new StructureDiagramGenerator();
			try {
				sdg.setMolecule(new Molecule(m));
				sdg.generateCoordinates(new Vector2d(0, 1));
				m = sdg.getMolecule();
			} catch (Exception exc) {
				System.out.println("Molecule has no coordinates and cannot generate those.");
				System.exit(1);
			}            
        }
        
        MoleculeListViewer moleculeListViewer = new MoleculeListViewer();
        MoleculeViewer2D mv = new MoleculeViewer2D(m);
        moleculeListViewer.addStructure(mv, ichi);
    }
        
    private Molecule readMolecule(String server, String ichi) throws Exception {
        String xpath = URLEncoder.encode("//molecule[./identifier/basic='" + ichi + "']");
        String colname = URLEncoder.encode("//wwmm/g2");
        
        URL url = new URL("http://" + server + "/Bob/QueryXindice");
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        
        PrintWriter out = new PrintWriter(
        connection.getOutputStream());
        out.print("detailed=on");
        out.print("&");
        out.print("xmlOnly=on");
        out.print("&");
        out.print("colName=" + colname);
        out.print("&");
        out.print("xpathString=" + xpath);
        out.print("&");
        out.println("query=Query");
        out.close();
        
        // now read the CML file into a data structure
        BufferedReader in = new BufferedReader(
            new InputStreamReader(
            connection.getInputStream()));
        CMLReader reader = new CMLReader(in);
        ChemFile cf = (ChemFile)reader.read((ChemObject)new ChemFile());
        logger.debug("#sequences: " + cf.getChemSequenceCount());
        ChemSequence chemSequence = cf.getChemSequence(0);
        logger.debug("#models in sequence: " + chemSequence.getChemModelCount());
        ChemModel chemModel = chemSequence.getChemModel(0);
        SetOfMolecules setOfMolecules = chemModel.getSetOfMolecules();
        logger.debug("#mols in model: " + setOfMolecules.getMoleculeCount());
        Molecule m = setOfMolecules.getMolecule(0);
        
        in.close();
        
        return m;
    }
    
    /**
     * This method will support direct access to the Xindice database later.
    private void getResult(String DB, String xpath) throws XMLDBException {

        Collection col = null;

        try {
            String driver = "org.apache.xindice.client.xmldb.DatabaseImpl";
            Class c = Class.forName(driver);
            Database database = (Database)c.newInstance();
            DatabaseManager.registerDatabase(database);

            String dbname = database.getName();
            col = DatabaseManager.getCollection("xmldb:xindice:///db/" + DB);

            XPathQueryService service = (XPathQueryService)col.getService(
                                                "XPathQueryService", "1.0");
            ResourceSet resultSet = service.query(xpath);
            ResourceIterator results = resultSet.getIterator();

            // get the results
            while (results.hasMoreResources()) {
                Resource reso = results.nextResource();
                // outbr.write((String)reso.getContent() + "\n\n");
            }
        } catch (XMLDBException eXML) {
            out.println(
                    "In getResult(); XML:DB Exception occured " + 
                    eXML.errorCode + " " + eXML.getMessage());
        } catch (Exception e) {
            out.println("Other Exception occured " + e.toString());
        } finally {
            // close collection
            if (col != null) {
                try {
                    col.close();
                } catch (Exception eCol) {
                    out.println(
                            "XML:DB Exception occured " + eCol.getMessage());
                }
            }
        }
    } */
}
