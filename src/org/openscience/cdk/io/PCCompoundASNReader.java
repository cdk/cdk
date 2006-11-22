/* $Revision: 7001 $ $Author: kaihartmann $ $Date: 2006-09-20 21:12:37 +0200 (Wed, 20 Sep 2006) $
 *
 * Copyright (C) 2006  Egon Willighagen <egonw@users.sf.net>
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
import java.util.StringTokenizer;

import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PubChemASNFormat;
import org.openscience.cdk.io.formats.XYZFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Reads an object from ASN formated input for PubChem Compound entries.
 *
 * @cdk.module io
 *
 * @cdk.keyword file format, PubChem Compound ASN
 */
public class PCCompoundASNReader extends DefaultChemObjectReader {

    private BufferedReader input;
    private LoggingTool logger;

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
    
    public IResourceFormat getFormat() {
        return PubChemASNFormat.getInstance();
    }
    
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
		}
		return false;
	}

    public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IChemFile) {
        	try {
        		return (IChemObject)readChemFile((IChemFile)object);
        	} catch (IOException e) {
        		throw new CDKException("An IO Exception occured while reading the file.", e);
        	}
        } else {
            throw new CDKException("Only supported is reading of ChemFile objects.");
        }
    }

    public void close() throws IOException {
        input.close();
    }

    // private procedures

    private IChemFile readChemFile(IChemFile file) throws IOException {
        IChemSequence chemSequence = file.getBuilder().newChemSequence();
        IChemModel chemModel = file.getBuilder().newChemModel();
        IMoleculeSet moleculeSet = file.getBuilder().newMoleculeSet();
        IMolecule molecule = file.getBuilder().newMolecule();
        
        String line = input.readLine();
        while (input.ready() && line != null) {
        	String command = getCommand(line);
        	if (command.equals("atoms")) {
                // parse frame by frame
        		System.out.println("ASN atoms found");
        	} else if (command.equals("bonds")) {
        		// ok, that fine
        		System.out.println("ASN bonds found");
        	} else if (command.equals("PC-Compound")) {
        		// ok, that fine
        		System.out.println("ASN PC-Compound found");
            } else {
            	skipBlock();
            }
        }
        moleculeSet.addAtomContainer(molecule);
        chemModel.setMoleculeSet(moleculeSet);
        file.addChemSequence(chemSequence);
        return file;
    }

	private void skipBlock() throws IOException {
		String line = input.readLine();
		boolean foundEndBracket = false;
        while (line != null) {
        	if (line.indexOf('}') != -1) return;
        }
	}

    private String getCommand(String line) {
    	StringBuffer buffer = new StringBuffer();
    	int i = 0;
    	boolean foundBracket = false;
    	while (i<line.length() && !foundBracket) {
    		char currentChar = line.charAt(i);
    		if (Character.isWhitespace(line.charAt(i))) {
    			// skip
    		} else if (currentChar == '{') {
    			foundBracket = true;
    		} else {
    			buffer.append(currentChar);
    		}
    	}
    	return buffer.toString();
    }
    
}
