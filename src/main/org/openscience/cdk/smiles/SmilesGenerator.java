/*  $Revision$ $Author$ $Date$
 *  
 *  Copyright (C) 2002-2007  Oliver Horlacher
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smiles;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.invariant.Canon;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IReaction;
import uk.ac.ebi.beam.Functions;
import uk.ac.ebi.beam.Graph;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Generates SMILES strings {@cdk.cite WEI88, WEI89}. It takes into account the
 * isotope and formal charge information of the atoms. In addition to this it
 * takes stereochemistry in account for both Bond's and Atom's. Via the flag 
 * useAromaticity it can be set if only SP2-hybridized atoms shall be set to 
 * lower case (default) or atoms, which are SP2 or aromatic.
 *
 * <p>Some example code:
 * <pre>
 * IMolecule benzene; // single/aromatic bonds between 6 carbons
 * SmilesGenerator sg = new SmilesGenerator();
 * String smiles = sg.createSMILES(benzene); // C1CCCCC1
 * sg.setUseAromaticityFlag(true);
 * smiles = sg.createSMILES(benzene); // c1ccccc1
 * IMolecule benzene2; // one of the two kekule structures with explicit double bond orders
 * String smiles2 = sg.createSMILES(benzene2); // C1=CC=CC=C1
 * </pre>
 * <b>Note</b>Due to the way the initial atom labeling is constructed, ensure
 * that the input molecule is appropriately configured.
 * In absence of such configuration it is possible that different forms
 * of the same molecule will not result in the same canonical SMILES.
 *
 * @author         Oliver Horlacher
 * @author         Stefan Kuhn (chiral smiles)
 * @cdk.created    2002-02-26
 * @cdk.keyword    SMILES, generator
 * @cdk.module     smiles
 * @cdk.githash
 * @cdk.bug        1793446
 */
@TestClass("org.openscience.cdk.smiles.SmilesGeneratorTest")
public final class SmilesGenerator {

    private final boolean   isomeric, canonical, aromatic;
    private final CDKToBeam converter;

    /**
     * Create the generic SMILES generator.
     * @see #generic() 
     */
    public SmilesGenerator() {
        this(false, false, false);
    }

    /**
     * Create the SMILES generator.
     *
     * @param isomeric include isotope and stereo configurations in produced
     *                 SMILES
     */
    private SmilesGenerator(boolean isomeric, boolean canonical, boolean aromatic) {
        this.isomeric  = isomeric;
        this.canonical = canonical;    
        this.aromatic  = aromatic;
        this.converter = new CDKToBeam(isomeric, aromatic);
    }

    /**
     * The generator should write aromatic (lower-case) SMILES. This option is
     * not recommended as different parsers can interpret where bonds should be
     * placed. 
     * 
     * <blockquote><pre>
     * IAtomContainer  container = ...;
     * SmilesGenerator smilesGen = SmilesGenerator.unique()
     *                                            .aromatic();
     * smilesGen.createSMILES(container);                                            
     * </pre></blockquote>
     * 
     * @return a generator for aromatic SMILES 
     */
    public SmilesGenerator aromatic() {
        return new SmilesGenerator(isomeric, canonical, true);
    } 

    /**
     * Create a generator for generic SMILES. Generic SMILES are 
     * non-canonical and useful for storing information when it is not used
     * as an index (i.e. unique keys). The generated SMILES is dependant on
     * the input order of the atoms.
     * 
     * @return a new arbitrary SMILES generator
     */
    public static SmilesGenerator generic() {
        return new SmilesGenerator(false, false, false);
    }
    
    /**
     * Convenience method for creating an isomeric generator. Isomeric SMILES
     * are non-unique but contain isotope numbers (e.g. {@code [13C]}) and 
     * stereo-chemistry.
     * 
     * @return a new isomeric SMILES generator
     */
    public static SmilesGenerator isomeric() {
        return new SmilesGenerator(true, false, false);
    }

    /**
     * Create a unique SMILES generator. Unique SMILES use a fast canonisation
     * algorithm but does not encode isotope or stereo-chemistry.
     * 
     * @return a new unique SMILES generator
     */
    public static SmilesGenerator unique() {
        return new SmilesGenerator(false, true, false);
    } 
    
    /**
     * Create a absolute SMILES generator. Unique SMILES uses the InChI to
     * canonise SMILES and encodes isotope or stereo-chemistry. The InChI
     * module is not a dependency of the SMILES module but should be present
     * on the classpath when generation absolute SMILES.
     * 
     * @return a new absolute SMILES generator
     */
    public static SmilesGenerator absolute() {
        return new SmilesGenerator(true, true, false);
    }

    /**
     * Create a SMILES string for the provided molecule.
     * 
     * @param molecule the molecule to create the SMILES of
     * @return a SMILES string
     * @throws CDKException SMILES could not be generated
     * @deprecated use #create
     */
    @Deprecated
    public String createSMILES(IAtomContainer molecule) {
        try {
            return create(molecule);
        } catch (CDKException e) {
            throw new IllegalArgumentException("SMILES could not be generated, please use the new API method 'create()'" +
                                                       "to catch the checked exception",
                                               e);
        }
    }

    /**
     * Create a SMILES string for the provided reaction.
     *
     * @param reaction the reaction to create the SMILES of
     * @return a reaction SMILES string
     * @throws CDKException SMILES could not be generated
     * @deprecated use #createReactionSMILES
     */
    @Deprecated
    public String createSMILES(IReaction reaction){
        try {
            return createReactionSMILES(reaction);
        } catch (CDKException e) {
            throw new IllegalArgumentException("SMILES could not be generated, please use the new API method 'create()'" +
                                                       "to catch the checked exception",
                                               e);
        }
    }
    
    /**
     * Generate SMILES for the provided {@code molecule}.
     *
     * @param molecule The molecule to evaluate
     * @return the SMILES string
     * @throws CDKException SMILES could not be created
     */
    @TestMethod("testCisResorcinol,testEthylPropylPhenantren,testAlanin")
    public String create(IAtomContainer molecule) throws CDKException {
        return create(molecule, new int[molecule.getAtomCount()]);
    }

    /**
     * Create a SMILES string and obtain the order which the atoms were 
     * written. The output order allows one to arrange auxiliary atom data in the
     * order that a SMILES string will be read. A simple example is seen below
     * where 2D coordinates are stored with a SMILES string. In reality a more
     * compact binary encoding would be used instead of printing the coordinates
     * as a string.
     * 
     * <blockquote><pre>
     * IAtomContainer  mol = ...;
     * SmilesGenerator sg  = SmilesGenerator.generic();
     * 
     * int   n     = mol.getAtomCount();
     * int[] order = new int[];
     *
     * // the order array is filled up as the SMILES is generated
     * String smi = sg.create(mol, order);
     * 
     * // load the coordinates array such that they are in the order the atoms
     * // are read when parsing the SMILES
     * Point2d[] coords = new Point2d[mol.getAtomCount()];
     * for (int i = 0; i < coords.length; i++)
     *     coords[order[i]] = container.getAtom(i).getPoint2d();
     * 
     * // SMILES string suffixed by the coordinates
     * String smi2d = smi + " " + Arrays.toString(coords);
     * 
     * </pre></blockquote>
     * 
     * @param molecule the molecule to write
     * @param order    array to store the output order of atoms
     * @return the SMILES string
     * @throws CDKException SMILES could not be created
     */
    public String create(IAtomContainer molecule, int[] order) throws CDKException {

        try {
            if (order.length != molecule.getAtomCount())
                throw new IllegalArgumentException("the array for storing output order should be" +
                                                           "the same length as the number of atoms");

            Graph g = converter.toBeamGraph(molecule);


            // apply the canonical labelling
            if (canonical) {

                // determine the output order
                int[] labels = labels(molecule);

                g = g.permute(labels)
                     .resonate();

                if (isomeric) {

                    // FIXME: required to ensure canonical double bond labelling
                    g.sort(new Graph.VisitHighOrderFirst());
                    
                    // canonical double-bond stereo, output be C/C=C/C or C\C=C\C
                    // and we need to normalise to one
                    g = Functions.normaliseDirectionalLabels(g);

                    // visit double bonds first, prefer C1=CC=C1 to C=1C=CC1
                    // visit hydrogens first
                    g.sort(new Graph.VisitHighOrderFirst())
                     .sort(new Graph.VisitHydrogenFirst());
                }

                String smiles = g.toSmiles(order);

                // the SMILES has been generated on a reordered molecule, transform
                // the ordering
                int[] canorder = new int[order.length];
                for (int i = 0; i < labels.length; i++)
                    canorder[i] = order[labels[i]];
                System.arraycopy(canorder, 0, order, 0, order.length);

                return smiles;
            }
            else {
                return g.toSmiles(order);
            }
        } catch (IOException e) {
            throw new CDKException(e.getMessage());
        }
    }

    /**
     * Generate a SMILES for the given <code>Reaction</code>.
     * 
     * @param reaction the reaction in question
     * @return the SMILES representation of the reaction
     * @throws org.openscience.cdk.exception.CDKException if there is an error during SMILES generation
     */
    public String createReactionSMILES(IReaction reaction) throws CDKException {
        StringBuffer reactionSMILES = new StringBuffer();
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            reactionSMILES.append(create(reactants.getAtomContainer(i)));
            if (i + 1 < reactants.getAtomContainerCount()) {
                reactionSMILES.append('.');
            }
        }
        reactionSMILES.append('>');
        IAtomContainerSet agents = reaction.getAgents();
        for (int i = 0; i < agents.getAtomContainerCount(); i++) {
            reactionSMILES.append(create(agents.getAtomContainer(i)));
            if (i + 1 < agents.getAtomContainerCount()) {
                reactionSMILES.append('.');
            }
        }
        reactionSMILES.append('>');
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            reactionSMILES.append(create(products.getAtomContainer(i)));
            if (i + 1 < products.getAtomContainerCount()) {
                reactionSMILES.append('.');
            }
        }
        return reactionSMILES.toString();
    }

    /**
     * Indicates whether output should be an aromatic SMILES.
     *
     * @param useAromaticityFlag if false only SP2-hybridized atoms will be lower case (default),
     * true=SP2 or aromaticity trigger lower case
     */
    @TestMethod("testSFBug956923")
    public void setUseAromaticityFlag(boolean useAromaticityFlag) {
        // ignore for now
    }

    /**
     * Given a molecule (possibly disconnected) compute the labels which
     * would order the atoms by increasing canonical labelling. If the SMILES
     * are isomeric (i.e. stereo and isotope specific) the InChI numbers are 
     * used. These numbers are loaded via reflection and the 'cdk-inchi' module
     * should be present on the classpath.
     * 
     * @param molecule the molecule to 
     * @return the permutation
     * @see Canon
     */
    private int[] labels(final IAtomContainer molecule) throws CDKException {
        long[] labels = isomeric ? inchiNumbers(molecule) 
                                 : Canon.label(molecule, GraphUtil.toAdjList(molecule));
        int[]  cpy    = new int[labels.length];
        for (int i = 0; i < labels.length; i++)
            cpy[i] = (int) labels[i] - 1;              
        return cpy;
    }

    /**
     * Obtain the InChI numbering for canonising SMILES. The cdk-smiles module
     * does not and should not depend on cdk-inchi and so the numbers are loaded
     * via reflection. If the class cannot be found on the classpath an 
     * exception is thrown.
     * 
     * @param container a structure
     * @return the inchi numbers
     * @throws CDKException the inchi numbers could not be obtained
     */
    private long[] inchiNumbers(IAtomContainer container) throws CDKException {
        // TODO: create an interface so we don't have to dynamically load the 
        // class each time
        String cname = "org.openscience.cdk.graph.invariant.InChINumbersTools";
        String mname = "getUSmilesNumbers";
        try {
            Class  c      = Class.forName(cname);
            Method method = c.getDeclaredMethod("getUSmilesNumbers", IAtomContainer.class);
            return (long[]) method.invoke(c, container);
        } catch (ClassNotFoundException e) {
            throw new CDKException("The cdk-inchi module is not loaded," +
                                           " this module is need when gernating absolute SMILES.");
        } catch (NoSuchMethodException e) {
            throw new CDKException("The method " + mname + " was not found", e);
        } catch (InvocationTargetException e) {
            throw new CDKException("An InChI could not be generated and used to canonise SMILES: " + e.getMessage(),
                                   e);
        } catch (IllegalAccessException e) {
            throw new CDKException("Could not access method to obtain InChI numbers.");
        }
    }

}
