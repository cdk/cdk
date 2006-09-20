/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software;!you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option)!any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source sode files, and to any copyright notice that you may distribute
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.tools.DataFeatures;
import org.openscience.cdk.tools.LoggingTool;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

/**
 * Reader that can read molecules from a Xindice database as set up
 * as in a World Wide Molecular Matrix node
 * 
 * @author Yong Zhang <yz237@cam.ac.uk>
 *
 * @cdk.keyword      database, Xindice
 * @cdk.builddepends xmldb.jar
 * @cdk.builddepends xindice.jar
 * @cdk.require      xindice
 */
public class XindiceReader {

    private String collection;
    private String xpath = null;
    
    private LoggingTool logger;

    public XindiceReader(String collection) {
        logger = new LoggingTool(this);
        
        while (collection.startsWith("/")) {
            collection = collection.substring(1);
        }
        this.collection = collection;
    }

    public IChemFormat getFormat() {
        return new IChemFormat() {
           public String getFormatName() {
                return "Xindice database";
            }
            public String getMIMEType() { return null; };
            public String getPreferredNameExtension() { return null; };
            public String[] getNameExtensions() { return new String[0]; };
            public String getReaderClassName() { return null; };
            public String getWriterClassName() { return null; }
			public boolean isXMLBased() { return true; };
			public int getSupportedDataFeatures() { return DataFeatures.NONE; };
			public int getRequiredDataFeatures() { return DataFeatures.NONE; };
       };
    }
    
    public void setReader(Reader input) throws CDKException {
        throw new CDKException("This Reader does not read from a Reader but from a XIndice database");
    }

    public void setQuery(String xpath) {
        logger.info("Xindice query set to " + xpath);
        this.xpath = xpath;
   }

    public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IMoleculeSet) {
            return readMoleculeSet((IMoleculeSet)object);
        } else {
            throw new CDKException("Only supported is MoleculeSet.");
        }
    }
    
    private IMoleculeSet readMoleculeSet(IMoleculeSet mols) throws CDKException {
        Collection col = null;
        try {
            String driver = "org.apache.xindice.client.xmldb.DatabaseImpl";
            Class c = Class.forName(driver);
            Database database = (Database)c.newInstance();
            DatabaseManager.registerDatabase(database);

            String collectionRL = "xmldb:xindice:///db/" + this.collection;
            logger.debug("Looking at collection: " + collectionRL);
            col = DatabaseManager.getCollection(collectionRL);
            
            XPathQueryService service = (XPathQueryService)col.getService("XPathQueryService", "1.0");
            ResourceSet resultSet = service.query(xpath);
            ResourceIterator results = resultSet.getIterator();

            while (results.hasMoreResources()) {
                Resource resource = results.nextResource();
                String CMLString = (String)resource.getContent();
                InputStream reader = new ByteArrayInputStream(CMLString.getBytes());
                CMLReader cmlr = new CMLReader(reader);
                mols.addMolecule(getMolecule((IChemFile)cmlr.read(mols.getBuilder().newChemFile())));
            }
            logger.info("Retrieved " + mols.getMoleculeCount() + " molecules");
        } catch (XMLDBException eXML) {
            throw new CDKException(
                "In getResult(); XML:DB Exception occured " + eXML.errorCode + " " + 
                eXML.getMessage());
        } catch (Exception e) {
            throw new CDKException("Other Exception occured " + e.getMessage());
        } finally {
            if (col != null) {
                try {
                  col.close();
                } catch (Exception eCol) {
                  logger.error("XML:DB Exception occured " + eCol.getMessage());
		  logger.debug(eCol);
                }
            }
        }
        return mols;
    }
        
    private IMolecule getMolecule(IChemFile cf) {
    	IChemSequence cs = cf.getChemSequence(0);
        IChemModel cm = cs.getChemModel(0);
        IMoleculeSet som = cm.getMoleculeSet();
        return som.getMolecule(0);
    }

    public void close() throws IOException {
    }
}
