/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.io;

import org.openscience.cdk.exception.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.setting.*;
import java.io.*;
import java.util.*;
import javax.vecmath.*;

/**
 * Reads a molecule from an MDL RXN file.
 *
 * <p>References:
 *   <a href="http://cdk.sf.net/biblio.html#DAL92">DAL92</a>
 *
 * @author     Egon Willighagen
 * @created    2003-07-24
 *
 * @keyword    file format, MDL RXN
 */
public class MDLRXNReader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private org.openscience.cdk.tools.LoggingTool logger = null;

    /**
     * Contructs a new MDLReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public MDLRXNReader(Reader in) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        input = new BufferedReader(in);
    }

    public String getFormatName() {
        return "MDL RXN";
    }

    /**
     * Takes an object which subclasses ChemObject, e.g.Molecule, and will read
     * this (from file, database, internet etc). If the specific implementation
     * does not support a specific ChemObject it will throw an Exception.
     *
     * @param  object                              The object that subclasses
     *      ChemObject
     * @return                                     The ChemObject read
     * @exception  CDKException
     */
     public ChemObject read(ChemObject object) throws CDKException {
         if (object instanceof Reaction) {
             return (ChemObject) readReaction();
         } else if (object instanceof ChemModel) {
             ChemModel model = new ChemModel();
             SetOfReactions reactionSet = new SetOfReactions();
             reactionSet.addReaction(readReaction());
             model.setSetOfReactions(reactionSet);
             return model;
         } else {
             throw new CDKException("Only supported are Reaction and ChemModel, and not " +
                 object.getClass().getName() + "."
             );
         }
     }
     
     public boolean accepts(ChemObject object) {
         if (object instanceof Reaction) {
             return true;
         } else if (object instanceof ChemModel) {
             return true;
         }
         return false;
     }


    /**
     * Read a Reaction from a file in MDL RXN format
     *
     * @return  The Reaction that was read from the MDL file.
     */
    private Reaction readReaction() throws CDKException {
        Reaction reaction = new Reaction();
        try {
            String firstLine = input.readLine(); // should be $RXN
            String secondLine = input.readLine();
            String thirdLine = input.readLine();
            String fourthLine = input.readLine();
        } catch (IOException exception) {
            logger.debug(exception);
            throw new CDKException("Error while reading header of RXN file");
        }

        int reactantCount = 0;
        int productCount = 0;
        try {
            String countsLine = input.readLine();
            /* this line contains the number of reactants
               and products */
            StringTokenizer tokenizer = new StringTokenizer(countsLine);
            reactantCount = Integer.valueOf(tokenizer.nextToken()).intValue();
            logger.info("Expecting " + reactantCount + " reactants in file");
            productCount = Integer.valueOf(tokenizer.nextToken()).intValue();
            logger.info("Expecting " + productCount + " products in file");
        } catch (Exception exception) {
            logger.debug(exception);
            throw new CDKException("Error while counts line of RXN file");
        }
        
        // now read the reactants
        try {
            for (int i=1; i<=reactantCount; i++) {
                StringBuffer molFile = new StringBuffer();
                String announceMDLFileLine = input.readLine();
                String molFileLine = "";
                do {
                    molFileLine = input.readLine();
                    molFile.append(molFileLine);
                    molFile.append("\n");
                } while (!molFileLine.equals("M  END"));
                
                // read MDL molfile content
                MDLReader reader = new MDLReader(
                  new StringReader(molFile.toString()));
                Molecule reactant = (Molecule)reader.read(
                  new Molecule());
                  
                // add reactant
                reaction.addReactant(reactant);
            }
        } catch (CDKException exception) {
            // rethrow exception from MDLReader
            throw exception;
        } catch (Exception exception) {
            logger.debug(exception);
            throw new CDKException("Error while reading reactant");
        }
        
        // now read the products
        try {
            for (int i=1; i<=productCount; i++) {
                StringBuffer molFile = new StringBuffer();
                String announceMDLFileLine = input.readLine();
                String molFileLine = "";
                do {
                    molFileLine = input.readLine();
                    molFile.append(molFileLine);
                    molFile.append("\n");
                } while (!molFileLine.equals("M  END"));
                
                // read MDL molfile content
                MDLReader reader = new MDLReader(
                  new StringReader(molFile.toString()));
                Molecule product = (Molecule)reader.read(
                  new Molecule());
                  
                // add reactant
                reaction.addProduct(product);
            }
        } catch (CDKException exception) {
            // rethrow exception from MDLReader
            throw exception;
        } catch (Exception exception) {
            logger.debug(exception);
            throw new CDKException("Error while reading products");
        }
        
        // now try to map things, if wanted
        logger.info("Reading atom-atom mapping from file");
        // distribute all atoms over two AtomContainer's
        AtomContainer reactingSide = new AtomContainer();
        Molecule[] molecules = reaction.getReactants();
        for (int i=0; i<molecules.length; i++) {
            reactingSide.add(molecules[i]);
        }
        AtomContainer producedSide = new AtomContainer();
        molecules = reaction.getProducts();
        for (int i=0; i<molecules.length; i++) {
            producedSide.add(molecules[i]);
        }
        
        // map the atoms
        int mappingCount = 0;
        Atom[] reactantAtoms = reactingSide.getAtoms();
        Atom[] producedAtoms = producedSide.getAtoms();
        for (int i=0; i<reactantAtoms.length; i++) {
            for (int j=0; j<producedAtoms.length; j++) {
                if (reactantAtoms[i].getID().length() > 0 &&
                    reactantAtoms[i].getID().equals(producedAtoms[j].getID())) {
                    reaction.addMapping(
                        new Mapping(reactantAtoms[i], producedAtoms[j])
                    );
                    mappingCount++;
                    break;
                }
            }
        }
        logger.info("Mapped atoms: " + mappingCount);
        
        return reaction;
    }
    
    public void close() throws IOException {
        input.close();
    }
}

