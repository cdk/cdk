/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.internet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Vector;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.UnsupportedChemObjectException;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.tools.DataFeatures;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.dadml.DATABASE;
import org.openscience.dadml.DBDEF;
import org.openscience.dadml.DBLIST;
import org.openscience.dadml.FIELD;
import org.openscience.dadml.INDEX;
import org.openscience.dadml.filereaders.DBDEFFileReader;
import org.openscience.dadml.filereaders.DBLISTFileReader;
import org.openscience.dadml.tools.DBDEFInfo;

/**
 * Reads a molecule from a DADML super database.
 *
 * <p>Database Access Definition Markup Language (DADML) is an XML
 * application that makes it possible to define how databases can be accessed
 * via URLs.
 *
 * @author           Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created      2001-12-18
 *
 * @cdk.keyword      internet
 * @cdk.keyword      database
 * @cdk.builddepends dadml.jar
 * @cdk.require      dadml
 */
public class DADMLReader {

    private String superdb;

    private LoggingTool logger;
    private URI query;
    
    /**
     * Contructs a new DADMLReader that can read Molecule from the internet.
     *
     * @param   superdb DADML super database to look up structure from
     */
    public DADMLReader(String superdb) {
        logger = new LoggingTool(this);
        this.superdb = superdb;
        this.query = null;
    }

    public IChemFormat getFormat() {
        return new IChemFormat() {
            public String getFormatName() {
                return "DADML network";
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
        throw new CDKException("This Reader does not read from a Reader but from internet nodes");
    }

    /**
     * Sets the query.
     *
     * @param   indexType   Index type (e.g. CAS-NUMBER)
     * @param   value   Index of molecule to download (e.g. 50-00-0)
     */
    public void setQuery(String indexType, String value) {
        try {
            this.query = new URI("dadml://any/" + indexType + "?" + value);
        } catch (URISyntaxException exception) {
            logger.error("Serious error: ", exception.getMessage());
            logger.debug(exception);
        }
    }

    /**
     * Sets the query in the form of an URI.
     * An example URI is: <code>dadml://any/CAS-NUMBER?50-00-0</code>.
     *
     * @param   query     URI query.
     */
    public void setQuery(URI query) throws URISyntaxException {
        this.query = query;
    }

    /**
     * Takes an object which subclasses IChemObject, e.g.Molecule, and will read this
     * (from file, database, internet etc). If the specific implementation does not
     * support a specific IChemObject it will throw an Exception.
     *
     * @param   object  The object that subclasses IChemObject
     * @return   The IChemObject read
     * @exception   UnsupportedChemObjectException
     */
    public ChemObject read(ChemObject object) throws UnsupportedChemObjectException {
        if (object instanceof Molecule) {
            return (ChemObject)readMolecule();
        } else {
            throw new UnsupportedChemObjectException("Only supported is Molecule.");
        }
    }

    /**
     * Read a Molecule from a DADML super database.
     *
     * @return The Molecule that was read
     */
    private org.openscience.cdk.interfaces.IMolecule readMolecule() {
    	org.openscience.cdk.interfaces.IMolecule molecule = null;
        try {
            URL resource = this.resolveLink(query);
            // this has to be reformulated
            molecule = this.downloadURL(resource);
        } catch (Exception exception) {
            logger.error("File Not Found: ", exception.getMessage());
            logger.debug(exception);
        }
        return molecule;
    }
    
    /**
     * Resolved the given DADML URI into a URL from which content can possibly be
     * downloaded.
     *
     * @param dadmlRI The DADML URI to be resolved.
     */
    public URL resolveLink(URI dadmlRI) {
        Vector links = resolveLinks(dadmlRI);
        if (links.size() > 0) {
            return ((DADMLResult)links.elementAt(0)).getURL();
        } // else
        return null;
    }
    
    /**
     * Resolved the given DADML URI into a list of URLs from which content can possibly be
     * downloaded. The URL classes returned are of type <code>DADMLResult</code>.
     *
     * @param dadmlRI The DADML URI to be resolved.
     *
     * @see   org.openscience.cdk.internet.DADMLResult
     */
    public Vector resolveLinks(URI dadmlRI) {
        logger.debug("Resolving URI: ", dadmlRI);
        Vector links = new Vector();
        
        boolean found = false; // this is true when a structure is downloaded
        boolean done = false;  // this is true when all URLS have been tested
        
        String indexType = dadmlRI.getPath().substring(1);
        String index = dadmlRI.getQuery();
        
        DBLIST dblist = new DBLIST();
        try {
            logger.info("Downloading DADML super database: ", this.superdb);
            // Proxy authorization has to be ported from Chemistry Development Kit (CDK)
            // for now, do without authorization
            DBLISTFileReader reader = new DBLISTFileReader();
            dblist = reader.read(this.superdb);
        } catch (Exception supererror) {
            logger.error("Exception while reading super db: ", supererror.getMessage());
            logger.debug(supererror);
        }
        Enumeration dbases = dblist.databases();
        while (!found && !done && dbases.hasMoreElements()) {
            DATABASE database = (DATABASE)dbases.nextElement();
            String dburl = database.getURL() + database.getDefinition();
            DBDEF dbdef = new DBDEF();
            // Proxy authorization has to be ported from Chemistry Development Kit (CDK)
            // for now, do without authorization
            try {
                logger.info("Downloading: ", dburl);
                // do without authorization
                DBDEFFileReader reader = new DBDEFFileReader();
                dbdef = reader.read(dburl);
            } catch (Exception deferror) {
                System.err.println(deferror.toString());
            }
            if (DBDEFInfo.hasINDEX(dbdef, indexType)) {
                // oke, find a nice URL to use for download
                logger.debug("Trying: ", dbdef.getTITLE());
                Enumeration fields = dbdef.fields();
                while (fields.hasMoreElements()) {
                    FIELD field = (FIELD)fields.nextElement();
                    String mime = field.getMIMETYPE();
                    String ftype = field.getTYPE();
                    if ((mime.equals("chemical/x-mdl-mol") ||
                         mime.equals("chemical/x-pdb") ||
                         mime.equals("chemical/x-cml")) &&
                         (ftype.equals("3DSTRUCTURE") ||
                          ftype.equals("2DSTRUCTURE"))) {
                        logger.info("Accepted: ", field.getMIMETYPE(), ",", field.getTYPE());
                        Enumeration indices = field.getINDEX();
                        while (indices.hasMoreElements()) {
                            INDEX ind = (INDEX)indices.nextElement();
                            if (ind.getTYPE().equals(indexType)) {
                                // here is the URL composed
                                String url = dbdef.getURL() + ind.getACCESS_PREFIX() + index + ind.getACCESS_SUFFIX();
                                logger.debug("Adding to resolved links: ", url);
                                try {
                                    links.add(new DADMLResult(new URL(url), field));
                                } catch (MalformedURLException exception) {
                                    logger.error("Malformed URL: ", exception.getMessage());
                                    logger.debug(exception);
                                }
                           }
                        }
                    } else {
                        // reject other mime types && type structures
                        logger.info("Rejected: ", field.getMIMETYPE(), ",", field.getTYPE());
                    }
                }
            } else {
                logger.warn("Database does not have indexType: ", indexType);
            }
        }
        return links;
    }

    private org.openscience.cdk.interfaces.IMolecule downloadURL(URL resource) {
    	org.openscience.cdk.interfaces.IMolecule molecule = new Molecule();
        logger.debug("Downloading from URL: ", resource);
        try {
            URLConnection connection = resource.openConnection();
            BufferedReader bufReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );
            IChemObjectReader reader = new ReaderFactory().createReader(bufReader);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            logger.debug("#sequences: ", chemFile.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence chemSequence = chemFile.getChemSequence(0);
            logger.debug("#models in sequence: ", chemSequence.getChemModelCount());
            org.openscience.cdk.interfaces.IChemModel chemModel = chemSequence.getChemModel(0);
            org.openscience.cdk.interfaces.IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
            logger.debug("#mols in model: ", moleculeSet.getMoleculeCount());
            molecule = moleculeSet.getMolecule(0);
        } catch (UnsupportedChemObjectException exception) {
            logger.error("Unsupported IChemObject type: ", exception.getMessage());
            logger.debug(exception);
        } catch (FileNotFoundException exception) {
            logger.error("File not found: ", exception.getMessage());
            logger.debug(exception);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            logger.debug(exception);
        }
        return molecule;
    }

    public void close() throws IOException {
    }
}
