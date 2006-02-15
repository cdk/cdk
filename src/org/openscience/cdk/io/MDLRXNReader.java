/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.MDLRXNFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Reads a molecule from an MDL RXN file {@cdk.cite DAL92}.
 *
 * @cdk.module io
 *
 * @author     Egon Willighagen
 * @cdk.created    2003-07-24
 *
 * @cdk.keyword    file format, MDL RXN
 */
public class MDLRXNReader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private LoggingTool logger = null;

    /**
     * Contructs a new MDLReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public MDLRXNReader(Reader in) {
        logger = new LoggingTool(this);
        input = new BufferedReader(in);
    }

    public MDLRXNReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public MDLRXNReader() {
        this(new StringReader(""));
    }
    
    public IChemFormat getFormat() {
        return new MDLRXNFormat();
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

    /**
     * Takes an object which subclasses IChemObject, e.g.Molecule, and will read
     * this (from file, database, internet etc). If the specific implementation
     * does not support a specific IChemObject it will throw an Exception.
     *
     * @param  object                              The object that subclasses
     *      IChemObject
     * @return                                     The IChemObject read
     * @exception  CDKException
     */
     public IChemObject read(IChemObject object) throws CDKException {
         if (object instanceof IReaction) {
             return (IChemObject) readReaction(object.getBuilder());
         } else if (object instanceof IChemModel) {
             IChemModel model = object.getBuilder().newChemModel();
             ISetOfReactions reactionSet = object.getBuilder().newSetOfReactions();
             reactionSet.addReaction(readReaction(object.getBuilder()));
             model.setSetOfReactions(reactionSet);
             return model;
         } else if (object instanceof IChemFile) {
             IChemFile chemFile = object.getBuilder().newChemFile();
             IChemSequence sequence = object.getBuilder().newChemSequence();
             sequence.addChemModel((IChemModel)read(object.getBuilder().newChemModel()));
             chemFile.addChemSequence(sequence);
             return chemFile;
         } else {
             throw new CDKException("Only supported are Reaction and ChemModel, and not " +
                 object.getClass().getName() + "."
             );
         }
     }
     
     public boolean accepts(IChemObject object) {
         if (object instanceof IReaction) {
             return true;
         } else if (object instanceof IChemModel) {
             return true;
         } else if (object instanceof IChemFile) {
             return true;
         }
         return false;
     }


    /**
     * Read a Reaction from a file in MDL RXN format
     *
     * @return  The Reaction that was read from the MDL file.
     */
    private IReaction readReaction(IChemObjectBuilder builder) throws CDKException {
        IReaction reaction = builder.newReaction();
        try {
            input.readLine(); // first line should be $RXN
            input.readLine(); // second line
            input.readLine(); // third line
            input.readLine(); // fourth line
        } catch (IOException exception) {
            logger.debug(exception);
            throw new CDKException("Error while reading header of RXN file", exception);
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
            throw new CDKException("Error while counts line of RXN file", exception);
        }
        
        // now read the reactants
        try {
            for (int i=1; i<=reactantCount; i++) {
                StringBuffer molFile = new StringBuffer();
                input.readLine(); // announceMDLFileLine
                String molFileLine = "";
                do {
                    molFileLine = input.readLine();
                    molFile.append(molFileLine);
                    molFile.append("\n");
                } while (!molFileLine.equals("M  END"));
                
                // read MDL molfile content
                MDLReader reader = new MDLReader(
                  new StringReader(molFile.toString()));
                IMolecule reactant = (IMolecule)reader.read(
                  builder.newMolecule()
                );
                  
                // add reactant
                reaction.addReactant(reactant);
            }
        } catch (CDKException exception) {
            // rethrow exception from MDLReader
            throw exception;
        } catch (Exception exception) {
            logger.debug(exception);
            throw new CDKException("Error while reading reactant", exception);
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
                IMolecule product = (IMolecule)reader.read(
                  builder.newMolecule());
                  
                // add reactant
                reaction.addProduct(product);
            }
        } catch (CDKException exception) {
            // rethrow exception from MDLReader
            throw exception;
        } catch (Exception exception) {
            logger.debug(exception);
            throw new CDKException("Error while reading products", exception);
        }
        
        // now try to map things, if wanted
        logger.info("Reading atom-atom mapping from file");
        // distribute all atoms over two AtomContainer's
        IAtomContainer reactingSide = builder.newAtomContainer();
        IMolecule[] molecules = reaction.getReactants().getMolecules();
        for (int i=0; i<molecules.length; i++) {
            reactingSide.add(molecules[i]);
        }
        IAtomContainer producedSide = builder.newAtomContainer();
        molecules = reaction.getProducts().getMolecules();
        for (int i=0; i<molecules.length; i++) {
            producedSide.add(molecules[i]);
        }
        
        // map the atoms
        int mappingCount = 0;
        IAtom[] reactantAtoms = reactingSide.getAtoms();
        IAtom[] producedAtoms = producedSide.getAtoms();
        for (int i=0; i<reactantAtoms.length; i++) {
            for (int j=0; j<producedAtoms.length; j++) {
                if (reactantAtoms[i].getID() != null &&
                    reactantAtoms[i].getID().equals(producedAtoms[j].getID())) {
                    reaction.addMapping(
                        builder.newMapping(reactantAtoms[i], producedAtoms[j])
                    );
                    mappingCount++;
                    break;
                }
            }
        }
        logger.info("Mapped atom pairs: " + mappingCount);
        
        return reaction;
    }
    
    public void close() throws IOException {
        input.close();
    }
}

