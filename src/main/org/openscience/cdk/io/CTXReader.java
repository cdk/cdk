/* $RCSfile: $
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.Symbols;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.formats.CTXFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.BondManipulator;

import java.io.*;

/**
 * Reader that extracts information from the IDENT, NAME, ATOMS and BONDS
 * blocks in CTX files.
 * 
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.io.CTXReaderTest")
public class CTXReader extends DefaultChemObjectReader {

    private BufferedReader input;
    private LoggingTool logger;
    
    private IChemFile file;

    public CTXReader() {
        logger = new LoggingTool(this);
        file = null;
    }

    public CTXReader(Reader input) {
        this();
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public CTXReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return CTXFormat.getInstance();
    }

    @TestMethod("testSetReader_Reader")
    public void setReader(Reader reader) throws CDKException {
        if (reader instanceof BufferedReader) {
            this.input = (BufferedReader)reader;
        } else {
            this.input = new BufferedReader(reader);
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
        	file = (IChemFile)object;
            return readChemFile();
        } else {
            throw new CDKException("Only supported is reading of ChemFile.");
        }
    }

    private IChemFile readChemFile() throws CDKException {
        IChemSequence seq = file.getBuilder().newChemSequence();
        IChemModel model = file.getBuilder().newChemModel();
        IMoleculeSet containerSet = file.getBuilder().newMoleculeSet();
        IAtomContainer container = file.getBuilder().newAtomContainer();
        
        int lineNumber = 0;
        
        try {
            String line = input.readLine();
            while (input.ready() && line != null) {
                logger.debug((lineNumber++) + ": ", line);
                String command = null;
                if (isCommand(line)) { 
                	command = getCommand(line);
                	int lineCount = getContentLinesCount(line);
            		if ("ATOMS".equals(command)) {
            			processAtomsBlock(lineCount, container);
            		} else if ("BONDS".equals(command)) {
            			processBondsBlock(lineCount, container);
            		} else if ("IDENT".equals(command)) {
            			processIdentBlock(lineCount, container);
            		} else if ("NAME".equals(command)) {
            			processNameBlock(lineCount, container);
            		} else {
            			// skip lines
            			logger.warn("Dropping block: ", command);
            			for (int i=0; i<lineCount; i++) input.readLine();
            		}
                } else {
                	logger.warn("Unexpected content at line: ", lineNumber);
                }
                line = input.readLine();
            }
            containerSet.addAtomContainer(container);
            model.setMoleculeSet(containerSet);
            seq.addChemModel(model);
            file.addChemSequence(seq);
        } catch (Exception exception) {
            String message = "Error while parsing CTX file: " + exception.getMessage();
            logger.error(message);
            logger.debug(exception);
            throw new CDKException(message, exception);
        }
        return file;
    }
    
    private void processIdentBlock(int lineCount, IAtomContainer container) throws IOException {
    	String identifier = "";
    	for (int i=0; i<lineCount; i++) {
			identifier = identifier + input.readLine().trim();
    	}
    	container.setID(identifier);
    }
    
    private void processNameBlock(int lineCount, IAtomContainer container) throws IOException {
    	String name = "";
    	for (int i=0; i<lineCount; i++) {
    		name = name + input.readLine().trim();
    	}
    	container.setProperty(CDKConstants.TITLE, name);
    }
    
	private void processAtomsBlock(int lineCount, IAtomContainer container) throws IOException {
		for (int i=0; i<lineCount; i++) {
			String line = input.readLine();
			int atomicNumber = Integer.parseInt(line.substring(7,10).trim());
			IAtom atom = container.getBuilder().newAtom();
			atom.setAtomicNumber(atomicNumber);
			atom.setSymbol(Symbols.byAtomicNumber[atomicNumber]);
			container.addAtom(atom);
		}
	}

	private void processBondsBlock(int lineCount, IAtomContainer container) throws IOException {
		for (int i=0; i<lineCount; i++) {
			String line = input.readLine();
			int atom1 = Integer.parseInt(line.substring(10,13).trim())-1;
			int atom2 = Integer.parseInt(line.substring(16,19).trim())-1;
			if (container.getBond(container.getAtom(atom1), container.getAtom(atom2)) == null) {
				IBond bond = container.getBuilder().newBond(
					container.getAtom(atom1), 
					container.getAtom(atom2)
				);
				int order = Integer.parseInt(line.substring(23).trim());
				bond.setOrder(BondManipulator.createBondOrder((double)order));
				container.addBond(bond);
			} // else: bond already present; CTX store the bonds twice
		}
	}

	private int getContentLinesCount(String line) {
		return Integer.parseInt(line.substring(18,21).trim());
	}

	private String getCommand(String line) {
		return line.substring(2,10).trim();
	}

	private boolean isCommand(String line) {
		return (line.length() > 1 && line.charAt(0) == ' ' && line.charAt(1) == '/');
	}

	@TestMethod("testClose")
  public void close() throws IOException {
        input.close();
    }
}
