/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.SMILESFormat;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Writes the SMILES strings to a plain text file.
 *
 * @cdk.module  smiles
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword file format, SMILES
 */
@TestClass("org.openscience.cdk.io.SMILESWriterTest")
public class SMILESWriter extends DefaultChemObjectWriter {

    private LoggingTool logger;
    private BufferedWriter writer;

    /**
     * Contructs a new SMILESWriter that can write a list of SMILES to a Writer
     *
     * @param   out  The Writer to write to
     */
    public SMILESWriter(Writer out) {
    	logger = new LoggingTool(this);
    	try {
    		if (out instanceof BufferedWriter) {
                writer = (BufferedWriter)out;
            } else {
                writer = new BufferedWriter(out);
            }
        } catch (Exception exc) {
        }
    }

    public SMILESWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }

    public SMILESWriter() {
        this(new StringWriter());
    }

    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return SMILESFormat.getInstance();
    }

    public void setWriter(Writer out) throws CDKException {
    	if (out instanceof BufferedWriter) {
            writer = (BufferedWriter)out;
        } else {
            writer = new BufferedWriter(out);
        }
    }

    public void setWriter(OutputStream output) throws CDKException {
    	setWriter(new OutputStreamWriter(output));
    }

    /**
     * Contructs a new SMILESWriter that can write an list of SMILES to a given OutputStream
     *
     * @param   out  The OutputStream to write to
     */
    public SMILESWriter(FileOutputStream out) {
        this(new OutputStreamWriter(out));
    }

    /**
     * Flushes the output and closes this object
     */
    @TestMethod("testClose")
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }

	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IMoleculeSet.class.equals(interfaces[i])) return true;
			if (IMolecule.class.equals(interfaces[i])) return true;
		}
		return false;
	}

    /**
     * Writes the content from object to output.
     *
     * @param   object  IChemObject of which the data is outputted.
     */
	public void write(IChemObject object) throws CDKException {
		if (object instanceof IMoleculeSet) {
		    writeMoleculeSet((IMoleculeSet)object);
		} else if (object instanceof IMolecule) {
		    writeMolecule((IMolecule)object);
		} else {
		    throw new CDKException("Only supported is writing of ChemFile and Molecule objects.");
		}
	}

	/**
	 * Writes a list of molecules to an OutputStream
	 *
	 * @param   som  MoleculeSet that is written to an OutputStream
	 */
	public void  writeMoleculeSet(IMoleculeSet som)
	{
		writeMolecule(som.getMolecule(0));
		for (int i = 1; i <= som.getMoleculeCount() - 1; i++) {
			try {
				writeMolecule(som.getMolecule(i));
			} catch (Exception exc) {
			}
		}
	}

    /**
     * Writes the content from molecule to output.
     *
     * @param   molecule  Molecule of which the data is outputted.
     */
    public void writeMolecule(IMolecule molecule) {
        SmilesGenerator sg = new SmilesGenerator();
        String smiles = "";
        try {
            smiles = sg.createSMILES(molecule);
            logger.debug("Generated SMILES: " + smiles);
            writer.write(smiles);
            writer.newLine();
            writer.flush();
            logger.debug("file flushed...");
        } catch(Exception exc) {
            logger.error("Error while writing Molecule: ", exc.getMessage());
            logger.debug(exc);
        }
    }
}
