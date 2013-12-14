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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.invariant.Canon;
import org.openscience.cdk.graph.invariant.CanonicalLabeler;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IReaction;
import uk.ac.ebi.beam.Functions;
import uk.ac.ebi.beam.Graph;
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
     *  Create the SMILES generator.
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
     * Create a generator for arbitrary SMILES. Arbitrary SMILES are 
     * non-canonical and useful for storing information when it is not used
     * as an index (i.e. unique keys).
     * 
     * @return a new arbitrary SMILES generator
     */
    public static SmilesGenerator arbitary() {
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
     * Generate SMILES for the provided {@code molecule}.
     *
     * @param molecule The molecule to evaluate
     * @return the SMILES string
     */
    @TestMethod("testCisResorcinol,testEthylPropylPhenantren,testAlanin")
    public synchronized String createSMILES(IAtomContainer molecule) throws CDKException {
        Graph g = converter.toBeamGraph(molecule);
        
        // apply the CANON labelling
        if (canonical) {
            g = Functions.canonicalize(g, labels(molecule));
        }

        // collapse() removes redundant hydrogen labels
        return g.toSmiles();
    }

    /**
     * Generate a SMILES for the given <code>Reaction</code>.
     * 
     * @param reaction the reaction in question
     * @return the SMILES representation of the reaction
     * @throws org.openscience.cdk.exception.CDKException if there is an error during SMILES generation
     */
    public synchronized String createSMILES(IReaction reaction) throws CDKException {
        StringBuffer reactionSMILES = new StringBuffer();
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            reactionSMILES.append(createSMILES(reactants.getAtomContainer(i)));
            if (i + 1 < reactants.getAtomContainerCount()) {
                reactionSMILES.append('.');
            }
        }
        reactionSMILES.append('>');
        IAtomContainerSet agents = reaction.getAgents();
        for (int i = 0; i < agents.getAtomContainerCount(); i++) {
            reactionSMILES.append(createSMILES(agents.getAtomContainer(i)));
            if (i + 1 < agents.getAtomContainerCount()) {
                reactionSMILES.append('.');
            }
        }
        reactionSMILES.append('>');
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            reactionSMILES.append(createSMILES(products.getAtomContainer(i)));
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
     * would order the atoms by increasing canonical labelling.
     * 
     * @param molecule the molecule to 
     * @return the permutation
     * @see Canon
     */
    private final long[] labels(final IAtomContainer molecule) {
        long[] labels = Canon.label(molecule, GraphUtil.toAdjList(molecule));
        for (int i = 0; i < labels.length; i++)
            labels[i] -= 1;              
        return labels;
    }

}
