/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.database;

import java.sql.*;
import java.io.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.exception.*;

//The next for Xindice
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

/**
 * @author Yong Zhang <yz237@cam.ac.uk>
 */
public class XindiceReader implements ChemObjectReader {

    private String collection;
    private String xpath = null;
    
    public XindiceReader(String collection) {
        this.collection = collection;
    }

    public void setQuery(String xpath) {
        this.xpath = xpath;
    }

    public ChemObject read(ChemObject object) throws CDKException {
        if (object instanceof SetOfMolecules) {
            return (ChemObject)readSetOfMolecules();
        } else {
            throw new CDKException("Only supported Molecule.");
        }
    }
    
    private SetOfMolecules readSetOfMolecules() throws CDKException{
        SetOfMolecules mols = null;
        CMLReader cmlr;
        StringReader reader;
        Collection col = null;

        try {
            String driver = "org.apache.xindice.client.xmldb.DatabaseImpl";
            Class c = Class.forName(driver);
            Database database = (Database)c.newInstance();
            DatabaseManager.registerDatabase(database);

            String dbname = database.getName();
            col = DatabaseManager.getCollection("xmldb:xindice:///db/" + this.collection);
            
            XPathQueryService service = (XPathQueryService)col.getService("XPathQueryService", "1.0");
            ResourceSet resultSet = service.query(xpath);
            ResourceIterator results = resultSet.getIterator();

            while (results.hasMoreResources()) {
                Resource resource = results.nextResource();
                String CMLString = new String((String)resource.getContent());
                reader = new StringReader(CMLString);
                cmlr = new CMLReader(reader);
                mols.addMolecule(getMolecule((ChemFile)cmlr.read(new ChemFile())));
            }
        } catch (XMLDBException eXML) {
            System.err.println(
                "In getResult(); XML:DB Exception occured " + eXML.errorCode + " " + 
                eXML.getMessage());
        } catch (Exception e) {
            System.err.println("Other Exception occured " + e.getMessage());
        } finally {
            if (col != null) {
                try {
                  col.close();
                } catch (Exception eCol) {
                  System.err.println("XML:DB Exception occured " + eCol.getMessage());
                }
            }
        }
        return mols;
    }
        
    private Molecule getMolecule(ChemFile cf) {
        ChemSequence cs = cf.getChemSequence(0);
        ChemModel cm = cs.getChemModel(0);
        SetOfMolecules som = cm.getSetOfMolecules();
        return som.getMolecule(0);
    }
}
