/* $RCSfile$ 
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.smiles.SmilesGenerator;

/**
 * Writes the SMILES strings to a plain text file.
 *
 * @cdkPackage extra
 *
 * @keyword file format
 */
public class SMILESWriter extends DefaultChemObjectWriter {

    private org.openscience.cdk.tools.LoggingTool logger;
    static BufferedWriter writer;

    /**
     * Contructs a new SMILESWriter that can write a list of SMILES to a Writer
     *
     * @param   out  The Writer to write to
     */
    public SMILESWriter(Writer out) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        try {
            writer = new BufferedWriter(out);
        } catch (Exception exc) {
        }
    }

    public String getFormatName() {
        return "SMILES";
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
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }

    /**
     * Writes the content from object to output.
     *
     * @param   object  ChemObject of which the data is outputted.
     */
	public void write(ChemObject object) throws CDKException {
		if (object instanceof SetOfMolecules) {
		    writeSetOfMolecules((SetOfMolecules)object);
		} else if (object instanceof Molecule) {
		    writeMolecule((Molecule)object);
		} else {
		    throw new CDKException("Only supported is writing of ChemFile and Molecule objects.");
		}
	}

    public ChemObject highestSupportedChemObject() {
        return new SetOfMolecules();
    }

	/**
	 * Writes a list of molecules to an OutputStream
	 *
	 * @param   som  SetOfMolecules that is written to an OutputStream
	 */
	public void  writeSetOfMolecules(SetOfMolecules som)
	{
		Molecule[] molecules = som.getMolecules();
		writeMolecule(molecules[0]);
		for (int i = 1; i <= som.getMoleculeCount() - 1; i++) {
			try {
				writeMolecule(molecules[i]);
			} catch (Exception exc) {
			}
		}
	}

    /**
     * Writes the content from molecule to output.
     *
     * @param   molecule  Molecule of which the data is outputted.
     */
    public void writeMolecule(Molecule molecule) {
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
