/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2004  The Chemistry Development Kit (CDK) project
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * This Reader reads files which has one SMILES string on each
 * line. For each line a molecule is generated.
 *
 * @cdk.module io
 *
 * @cdk.keyword file format, SMILES
 */
public class SMILESReader extends DefaultChemObjectReader {

    private BufferedReader input = null;
    private SmilesParser sp = null;

    /* 
     * construct a new reader from a Reader type object
     *
     * @param input reader from which input is read
     */
    public SMILESReader(Reader input) {
        this.input = new BufferedReader(input);
        sp = new SmilesParser();
    }

    public String getFormatName() {
        return "SMILES";
    }
    
    /**
     * reads the content from a XYZ input. It can only return a
     * ChemObject of type ChemFile
     *
     * @param object class must be of type ChemFile
     *
     * @see ChemFile
     */
    public ChemObject read(ChemObject object) throws CDKException {
        if (object instanceof SetOfMolecules) {
            return (ChemObject)readSetOfMolecules();
        } else if (object instanceof ChemFile) {
            ChemFile file = new ChemFile();
            ChemSequence sequence = new ChemSequence();
            ChemModel chemModel = new ChemModel();
            chemModel.setSetOfMolecules(readSetOfMolecules());
            sequence.addChemModel(chemModel);
            file.addChemSequence(sequence);
            return (ChemObject) file;
        } else {
            throw new CDKException("Only supported is reading of SetOfMolecules objects.");
        }
    }

    // private procedures

    /**
     *  Private method that actually parses the input to read a ChemFile
     *  object.
     *
     * @return A ChemFile containing the data parsed from input.
     */
    private SetOfMolecules readSetOfMolecules() {
        SetOfMolecules som = new SetOfMolecules();
        try {
            String line = input.readLine();
            while (line != null) {
                //System.out.println(line);
                try {
                    Molecule molecule = sp.parseSmiles(line);
                    som.addMolecule(molecule);
                } catch (Exception e) {
                    // should make some noise now, but for now: just skip this line
                }
                if (input.ready()) { line = input.readLine(); } else { line = null; }
            }
        } catch (Exception exc) {
            // should make some noise now
        }
        return som;
    }
    
    public void close() throws IOException {
        input.close();
    }
}
