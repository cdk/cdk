/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import java.util.*;
import java.io.*;
import javax.vecmath.*;

/* This class is based on Dan Gezelter's XYZReader from Jmol */
public class XYZReader implements ChemObjectReader {

    private BufferedReader input;
  
    public XYZReader(Reader input) {
        this.input = new BufferedReader(input);
    }
    
    public ChemObject read(ChemObject object) throws UnsupportedChemObjectException {
    		if (object instanceof ChemFile) {
            return (ChemObject)readChemFile();
        } else {
            throw new UnsupportedChemObjectException("Only supported is ChemFile.");
        }
    }
    
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
                m.setTitle(info);
                
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
    
}
