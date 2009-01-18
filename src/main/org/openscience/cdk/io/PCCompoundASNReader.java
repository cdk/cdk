/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PubChemASNFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Reads an object from ASN formated input for PubChem Compound entries. The following
 * bits are supported: atoms.aid, atoms.element, bonds.aid1, bonds.aid2. Additionally,
 * it extracts the InChI and canonical SMILES properties.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword file format, PubChem Compound ASN
 */
@TestClass("org.openscience.cdk.io.PCCompoundASNReaderTest")
public class PCCompoundASNReader extends DefaultChemObjectReader {

    private BufferedReader input;
    private LoggingTool logger;
    
    IMolecule molecule = null;
    Map atomIDs = null;

    /**
     * Construct a new reader from a Reader type object.
     *
     * @param input reader from which input is read
     */
    public PCCompoundASNReader(Reader input) {
        this.input = new BufferedReader(input);
        logger = new LoggingTool(this);
    }

    public PCCompoundASNReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public PCCompoundASNReader() {
        this(new StringReader(""));
    }
    
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return PubChemASNFormat.getInstance();
    }
    
    @TestMethod("testSetReader_Reader")
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    @TestMethod("testSetReader_InputStream")
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
		}
    Class superClass = classObject.getSuperclass();
    if (superClass != null) return this.accepts(superClass);
		return false;
	}

    public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IChemFile) {
        	try {
        		return (IChemObject)readChemFile((IChemFile)object);
        	} catch (IOException e) {
        		throw new CDKException("An IO Exception occured while reading the file.", e);
        	} catch (CDKException e) {
        		throw e;
        	} catch (Exception e) {
        		throw new CDKException("An error occured.", e);
        	}
        } else {
            throw new CDKException("Only supported is reading of ChemFile objects.");
        }
    }

    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }

    // private procedures

    private IChemFile readChemFile(IChemFile file) throws Exception {
        IChemSequence chemSequence = file.getBuilder().newChemSequence();
        IChemModel chemModel = file.getBuilder().newChemModel();
        IMoleculeSet moleculeSet = file.getBuilder().newMoleculeSet();
        molecule = file.getBuilder().newMolecule();
        atomIDs = new HashMap();
        
        String line = input.readLine();
        while (input.ready() && line != null) {
        	if (line.indexOf("{") != -1) {
        		processBlock(line);
        	} else {
        		logger.warn("Skipping non-block: " + line); 
        	}
        	line = input.readLine();
        }
        moleculeSet.addAtomContainer(molecule);
        chemModel.setMoleculeSet(moleculeSet);
        chemSequence.addChemModel(chemModel);
        file.addChemSequence(chemSequence);
        return file;
    }

    
	private void processBlock(String line) throws Exception {
    	String command = getCommand(line);
    	if (command.equals("atoms")) {
            // parse frame by frame
    		logger.debug("ASN atoms found");
    		processAtomBlock();
    	} else if (command.equals("bonds")) {
    		// ok, that fine
    		logger.debug("ASN bonds found");
    		processBondBlock();
    	} else if (command.equals("props")) {
    		// ok, that fine
    		logger.debug("ASN props found");
    		processPropsBlock();
    	} else if (command.equals("PC-Compound ::=")) {
    		// ok, that fine
    		logger.debug("ASN PC-Compound found");
        } else {
        	logger.warn("Skipping block: " + command);
        	skipBlock();
        }
	}

	private void processPropsBlock() throws Exception {
		String line = input.readLine();
        while (input.ready() && line != null) {
        	if (line.indexOf("{") != -1) {
        		processPropsBlockBlock();
        	} else if (line.indexOf("}")!= -1) {
    			return;
    		} else {
    			logger.warn("Skipping non-block: " + line); 
        	}
        	line = input.readLine();
        }
	}

	private void processPropsBlockBlock() throws Exception {
		String line = input.readLine();
		URN urn = null;
        while (input.ready() && line != null) {
        	if (line.indexOf("urn") != -1) {
        		urn = extractURN();
        	} else if (line.indexOf("value") != -1) {
        		logger.debug("Found a prop value line: " + line);
        		if (line.indexOf(" sval") != -1) {
        			logger.debug("Label: " + urn.label);
        			logger.debug("Name: " + urn.name);
        			if ("InChI".equals(urn.label)) {
        				String value = getQuotedValue(line.substring(line.indexOf("value sval")+10));
        				molecule.setProperty(CDKConstants.INCHI, value);
        			} else if ("SMILES".equals(urn.label) &&
        					"Canonical".equals(urn.name)) {
        				String value = getQuotedValue(line.substring(line.indexOf("value sval")+10));
        				molecule.setProperty(CDKConstants.SMILES, value);
        			}
        		}
        	} else if (line.indexOf("}")!= -1) {
    			return;
    		} else {
    			logger.warn("Skipping non-block: " + line); 
        	}
        	line = input.readLine();
        }
	}

	private URN extractURN() throws Exception {
		URN urn = new URN();
		String line = input.readLine();
        while (input.ready() && line != null) {
        	if (line.indexOf("name") != -1) {
        		urn.name = getQuotedValue(line.substring(line.indexOf("name")+4));
        	} else if (line.indexOf("label") != -1) {
        		urn.label = getQuotedValue(line.substring(line.indexOf("label")+4));
        	} else if (line.indexOf("}")!= -1 && line.indexOf("\"")==-1) {
        		// ok, don't return if it also has a "
    			return urn;
    		} else {
    			logger.warn("Ignoring URN statement: " + line); 
        	}
        	line = input.readLine();
        }
        return urn;
	}

	private void processAtomBlock() throws Exception {
		String line = input.readLine();
        while (input.ready() && line != null) {
        	if (line.indexOf("{") != -1) {
        		processAtomBlockBlock(line);
        	} else if (line.indexOf("}")!= -1) {
    			return;
    		} else {
    			logger.warn("Skipping non-block: " + line); 
        	}
        	line = input.readLine();
        }
	}

	private void processBondBlock() throws Exception {
		String line = input.readLine();
        while (input.ready() && line != null) {
        	if (line.indexOf("{") != -1) {
        		processBondBlockBlock(line);
        	} else if (line.indexOf("}")!= -1) {
    			return;
    		} else {
    			logger.warn("Skipping non-block: " + line); 
        	}
        	line = input.readLine();
        }
	}

	private IAtom getAtom(int i) {
		if (molecule.getAtomCount() <= i) {
			molecule.addAtom(molecule.getBuilder().newAtom());
		}
		return molecule.getAtom(i);
	}
	
	private IBond getBond(int i) {
		if (molecule.getBondCount() <= i) {
			molecule.addBond(molecule.getBuilder().newBond());
		}
		return molecule.getBond(i);
	}
	
	private void processAtomBlockBlock(String line) throws Exception {
		String command = getCommand(line);
		if (command.equals("aid")) {
			// assume this is the first block in the atom block
			logger.debug("ASN atoms aid found");
			processAtomAIDs();
		} else if (command.equals("element")) {
			// assume this is the first block in the atom block
			logger.debug("ASN atoms element found");
			processAtomElements();
		} else {
			logger.warn("Skipping atom block block: " + command);
			skipBlock();
		}
	}
	
	private void processBondBlockBlock(String line) throws Exception {
		String command = getCommand(line);
		if (command.equals("aid1")) {
			// assume this is the first block in the atom block
			logger.debug("ASN bonds aid1 found");
			processBondAtomIDs(0);
		} else if (command.equals("aid2")) {
			// assume this is the first block in the atom block
			logger.debug("ASN bonds aid2 found");
			processBondAtomIDs(1);
		} else {
			logger.warn("Skipping atom block block: " + command);
			skipBlock();
		}
	}
	
	private void processAtomAIDs() throws Exception {
		String line = input.readLine();
		int atomIndex = 0;
        while (input.ready() && line != null) {
        	if (line.indexOf("}") != -1) {
        		// done
        		return;
        	} else {
//        		logger.debug("Found an atom ID: " + line);
//        		logger.debug("  index: " + atomIndex);
        		IAtom atom = getAtom(atomIndex);
        		String id = getValue(line);
        		atom.setID(id);
        		atomIDs.put(id, atom);
        		atomIndex++;
        	}
        	line = input.readLine();
        }
	}

	private void processBondAtomIDs(int pos) throws Exception {
		String line = input.readLine();
		int bondIndex = 0;
        while (input.ready() && line != null) {
        	if (line.indexOf("}") != -1) {
        		// done
        		return;
        	} else {
//        		logger.debug("Found an atom ID: " + line);
//        		logger.debug("  index: " + atomIndex);
        		IBond bond = getBond(bondIndex);
        		String id = getValue(line);
        		IAtom atom = (IAtom)atomIDs.get(id);
        		if (atom == null) {
        			throw new CDKException("File is corrupt: atom ID does not exist " + id);
        		}
        		bond.setAtom(atom, pos);
        		bondIndex++;
        	}
        	line = input.readLine();
        }
	}

	private void processAtomElements() throws Exception {
		String line = input.readLine();
		int atomIndex = 0;
        while (input.ready() && line != null) {
        	if (line.indexOf("}") != -1) {
        		// done
        		return;
        	} else {
//        		logger.debug("Found symbol: " + toSymbol(getValue(line)));
//        		logger.debug("  index: " + atomIndex);
        		IAtom atom = getAtom(atomIndex);
        		atom.setSymbol(toSymbol(getValue(line)));
        		atomIndex++;
        	}
        	line = input.readLine();
        }
	}

	private String toSymbol(String value) {
		if (value.length() == 1) return value.toUpperCase();
		return value.substring(0,1).toUpperCase() + value.substring(1);
	}

	private void skipBlock() throws IOException {
		String line = input.readLine();
		int openBrackets = 0;
        while (line != null) {
//    		logger.debug("SkipBlock: line=" + line);
    		if (line.indexOf('{') != -1) {
        		openBrackets++;
        	}
//    		logger.debug(" #open brackets: " + openBrackets);
        	if (line.indexOf('}') != -1) {
        		if (openBrackets == 0) return;
        		openBrackets--;
        	}
        	line = input.readLine();
        }
	}

    private String getCommand(String line) {
    	StringBuffer buffer = new StringBuffer();
    	int i = 0;
    	boolean foundBracket = false;
    	while (i<line.length() && !foundBracket) {
    		char currentChar = line.charAt(i);
    		if (currentChar == '{') {
    			foundBracket = true;
    		} else {
    			buffer.append(currentChar);
    		}
    		i++;
    	}
    	return foundBracket ? buffer.toString().trim() : null;
    }
    
    private String getValue(String line) {
    	StringBuffer buffer = new StringBuffer();
    	int i = 0;
    	boolean foundComma = false;
    	boolean preWS = true;
    	while (i<line.length() && !foundComma) {
    		char currentChar = line.charAt(i);
    		if (Character.isWhitespace(currentChar)) {
    			if (!preWS) buffer.append(currentChar);
    		} else if (currentChar == ',') {
    			foundComma = true;
    		} else {
    			buffer.append(currentChar);
    			preWS = false;
    		}
    		i++;
    	}
    	return buffer.toString();
    }

    private String getQuotedValue(String line) throws Exception {
    	StringBuffer buffer = new StringBuffer();
    	int i = 0;
//    	logger.debug("QV line: " + line);
    	boolean startQuoteFound = false;
    	while (line != null) {
    		while (i<line.length()) {
    			char currentChar = line.charAt(i);
    			if (currentChar == '"') {
    				if (startQuoteFound) { 
    					return buffer.toString();
    				} else {
    					startQuoteFound = true;
    				}
    			} else if (startQuoteFound){
    				buffer.append(currentChar);
    			}
    			i++;
    		}
    		line = input.readLine();
    		i = 0;
    	}
    	return null;
    }
    
    class URN {
    	String name = null;
    	String label = null;
    }
}
