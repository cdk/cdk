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
import java.util.StringTokenizer;

import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;

/**
 * Reads an object from XYZ formated input
 *
 * <p>This class is based on Dan Gezelter's XYZReader from Jmol
 *
 * @cdkPackage io
 *
 * @keyword file format, XYZ
 */
public class XYZReader extends DefaultChemObjectReader {

    private BufferedReader input;

    /* 
     * construct a new reader from a Reader type object
     *
     * @param input reader from which input is read
     */
    public XYZReader(Reader input) {
        this.input = new BufferedReader(input);
    }

    public String getFormatName() {
        return "XYZ";
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
        if (object instanceof ChemFile) {
            return (ChemObject)readChemFile();
        } else {
            throw new CDKException("Only supported is reading of ChemFile objects.");
        }
    }

    // private procedures

    /**
     *  Private method that actually parses the input to read a ChemFile
     *  object.
     *
     * @return A ChemFile containing the data parsed from input.
     */
    private ChemFile readChemFile() {
        ChemFile file = new ChemFile();
        ChemSequence chemSequence = new ChemSequence();
        
        int number_of_atoms = 0;
        StringTokenizer tokenizer;
        
        try {
            String line = input.readLine();
            while (input.ready() && line != null) {
                // parse frame by frame
                tokenizer = new StringTokenizer(line, "\t ,;");
                
                String token = tokenizer.nextToken();
                number_of_atoms = Integer.parseInt(token);
                String info = input.readLine();
                
                ChemModel chemModel = new ChemModel();
                SetOfMolecules setOfMolecules = new SetOfMolecules();
                
                Molecule m = new Molecule();
                m.setProperty(CDKConstants.TITLE ,info);

                for (int i = 0; i < number_of_atoms; i++) {
                    line = input.readLine();
                    if (line == null) break;
                    if (line.startsWith("#")) {
                        // skip comment in file
                    } else {
                        double x = 0.0f, y = 0.0f, z = 0.0f;
                        double charge = 0.0f;
                        tokenizer = new StringTokenizer(line, "\t ,;");
                        int fields = tokenizer.countTokens();
                        
                        if (fields < 4) {
                            // this is an error but cannot throw exception
                        } else {                    
                            String atomtype = tokenizer.nextToken();                    
                            x = (new Double(tokenizer.nextToken())).doubleValue();
                            y = (new Double(tokenizer.nextToken())).doubleValue();
                            z = (new Double(tokenizer.nextToken())).doubleValue();
                            
                            if (fields == 8) 
                                charge = (new Double(tokenizer.nextToken())).doubleValue();

                            Atom atom = new Atom(atomtype, new Point3d(x,y,z));
                            atom.setCharge(charge);
                            m.addAtom(atom);
                        }
                    }
                }

                setOfMolecules.addMolecule(m);
                chemModel.setSetOfMolecules(setOfMolecules);
                chemSequence.addChemModel(chemModel);
                line = input.readLine();
            }
            file.addChemSequence(chemSequence);
        } catch (IOException e) {
            // should make some noise now
            file = null;
        }
        return file;
    }
    
    public void close() throws IOException {
        input.close();
    }
}
