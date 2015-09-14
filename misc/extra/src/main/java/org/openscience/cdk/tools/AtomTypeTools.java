/* Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

import java.util.HashSet;
import java.util.Set;

/**
* AtomTypeTools is a helper class for assigning atom types to an atom.
*
* @author         cho
* @cdk.created    2005-18-07
* @cdk.module     extra
 * @cdk.githash
*/

public class AtomTypeTools {

    public static final int PYROLE_RING     = 4;
    public static final int FURAN_RING      = 6;
    public static final int THIOPHENE_RING  = 8;
    public static final int PYRIDINE_RING   = 10;
    public static final int PYRIMIDINE_RING = 12;
    public static final int BENZENE_RING = 5;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(AtomTypeTools.class);
    HOSECodeGenerator hcg = null;
    SmilesGenerator   sg  = null;

    /**
     * Constructor for the MMFF94AtomTypeMatcher object.
     */
    public AtomTypeTools() {
        hcg = new HOSECodeGenerator();
    }

    public IRingSet assignAtomTypePropertiesToAtom(IAtomContainer molecule) throws Exception {
        return assignAtomTypePropertiesToAtom(molecule, true);
    }

    /**
     *  Method assigns certain properties to an atom. Necessary for the atom type matching
     *  Properties:
     *  <ul>
     *   <li>aromaticity)
     *   <li>ChemicalGroup (CDKChemicalRingGroupConstant)
     *	 <li>SSSR
     *	 <li>Ring/Group, ringSize, aromaticity
     *	 <li>SphericalMatcher (HoSe Code)
     *  </ul>
     *
     *@param aromaticity boolean true/false true if aromaticity should be calculated
     *@return sssrf ringSetofTheMolecule
     *@exception Exception  Description of the Exception
     */
    public IRingSet assignAtomTypePropertiesToAtom(IAtomContainer molecule, boolean aromaticity) throws Exception {
        SmilesGenerator sg = new SmilesGenerator();

        //logger.debug("assignAtomTypePropertiesToAtom Start ...");
        logger.debug("assignAtomTypePropertiesToAtom Start ...");
        String hoseCode = "";
        IRingSet ringSetA = null;
        IRingSet ringSetMolecule = Cycles.sssr(molecule).toRingSet();
        logger.debug(ringSetMolecule);

        if (aromaticity) {
            try {
                Aromaticity.cdkLegacy().apply(molecule);
            } catch (Exception cdk1) {
                //logger.debug("AROMATICITYError: Cannot determine aromaticity due to: " + cdk1.toString());
                logger.error("AROMATICITYError: Cannot determine aromaticity due to: " + cdk1.toString());
            }
        }

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            // FIXME: remove casting
            IAtom atom2 = molecule.getAtom(i);
            //Atom aromatic is set by HueckelAromaticityDetector
            //Atom in ring?
            if (ringSetMolecule.contains(atom2)) {
                ringSetA = ringSetMolecule.getRings(atom2);
                RingSetManipulator.sort(ringSetA);
                IRing sring = (IRing) ringSetA.getAtomContainer(ringSetA.getAtomContainerCount() - 1);
                atom2.setProperty(CDKConstants.PART_OF_RING_OF_SIZE, Integer.valueOf(sring.getRingSize()));
                atom2.setProperty(
                        CDKConstants.CHEMICAL_GROUP_CONSTANT,
                        Integer.valueOf(ringSystemClassifier(sring, getSubgraphSmiles(sring, molecule))));
                atom2.setFlag(CDKConstants.ISINRING, true);
                atom2.setFlag(CDKConstants.ISALIPHATIC, false);
            } else {
                atom2.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, Integer.valueOf(CDKConstants.ISNOTINRING));
                atom2.setFlag(CDKConstants.ISINRING, false);
                atom2.setFlag(CDKConstants.ISALIPHATIC, true);
            }
            try {
                hoseCode = hcg.getHOSECode(molecule, atom2, 3);
                hoseCode = removeAromaticityFlagsFromHoseCode(hoseCode);
                atom2.setProperty(CDKConstants.SPHERICAL_MATCHER, hoseCode);
            } catch (CDKException ex1) {
                throw new CDKException("Could not build HOSECode from atom " + i + " due to " + ex1.toString(), ex1);
            }
        }
        return ringSetMolecule;
    }

    /**
     * New SMILES code respects atom valency hence a ring subgraph of 'o1cccc1CCCC' is correctly
     * written as 'o1ccc[c]1' note there is no hydrogen there since it was an external attachment.
     * To get unique subgraph SMILES we need to adjust valencies of atoms by adding Hydrogens. We
     * base this on the sum of bond orders removed.
     *
     * @param subgraph subgraph (atom and bond refs in 'molecule')
     * @param molecule the molecule
     * @return the canonical smiles of the subgraph
     * @throws CDKException something went wrong with SMILES gen
     */
    private static String getSubgraphSmiles(IAtomContainer subgraph, IAtomContainer molecule) throws CDKException {
        Set<IBond> bonds = new HashSet<>();
        for (IBond bond : subgraph.bonds())
            bonds.add(bond);

        Integer[] hCount = new Integer[subgraph.getAtomCount()];
        for (int i = 0; i < subgraph.getAtomCount(); i++) {
            final IAtom atom = subgraph.getAtom(i);
            int removed = 0;
            for (IBond bond : molecule.getConnectedBondsList(atom)) {
                if (!bonds.contains(bond))
                    removed += bond.getOrder().numeric();
            }
            hCount[i] = atom.getImplicitHydrogenCount();
            atom.setImplicitHydrogenCount(hCount[i] == null ? removed : hCount[i] + removed);
        }

        String smi = cansmi(subgraph);

        // reset for fused rings!
        for (int i = 0; i < subgraph.getAtomCount(); i++) {
            subgraph.getAtom(i).setImplicitHydrogenCount(hCount[i]);
        }

        return smi;
    }

    /**
     * Canonical SMILES for the provided molecule.
     *
     * @param mol molecule
     * @return the cansmi string
     * @throws CDKException something went wrong with SMILES gen
     */
    private static String cansmi(IAtomContainer mol) throws CDKException {
        return SmilesGenerator.unique().create(mol);
    }

    private String PYRROLE_SMI    = null;
    private String FURAN_SMI      = null;
    private String THIOPHENE_SMI  = null;
    private String PYRIDINE_SMI   = null;
    private String PYRIMIDINE_SMI = null;
    private String BENZENE_SMI    = null;

    private static String smicache(String cached, SmilesParser smipar, String input) throws CDKException {
        if (cached != null) return cached;
        return cached = cansmi(smipar.parseSmiles(input));
    }

    /**
     *  Identifies ringSystem and returns a number which corresponds to
     *  CDKChemicalRingConstant
     *
     *@param  ring    Ring class with the ring system
     *@param  smile  smile of the ring system
     *@return chemicalRingConstant
     */
    private int ringSystemClassifier(IRing ring, String smile) throws CDKException {
        /* System.out.println("IN AtomTypeTools Smile:"+smile); */
        logger.debug("Comparing ring systems: SMILES=", smile);

        final SmilesParser smipar = new SmilesParser(ring.getBuilder());

        if (smile.equals(smicache(PYRROLE_SMI, smipar, "c1cc[nH]c1")))
            return PYROLE_RING;
        else if (smile.equals(smicache(FURAN_SMI, smipar, "o1cccc1")))
            return FURAN_RING;
        else if (smile.equals(smicache(THIOPHENE_SMI, smipar, "c1ccsc1")))
            return THIOPHENE_RING;
        else if (smile.equals(smicache(PYRIDINE_SMI, smipar, "c1ccncc1")))
            return PYRIDINE_RING;
        else if (smile.equals(smicache(PYRIMIDINE_SMI, smipar, "c1cncnc1")))
            return PYRIMIDINE_RING;
        else if (smile.equals(smicache(BENZENE_SMI, smipar, "c1ccccc1")))
            return BENZENE_RING;

        int ncount = 0;
        for (int i = 0; i < ring.getAtomCount(); i++) {
            if (ring.getAtom(i).getSymbol().equals("N")) {
                ncount = ncount + 1;
            }
        }

        if (ring.getAtomCount() == 6 & ncount == 1) {
            return 10;
        } else if (ring.getAtomCount() == 5 & ncount == 1) {
            return 4;
        }

        if (ncount == 0) {
            return 3;
        } else {
            return 0;
        }
    }

    private String removeAromaticityFlagsFromHoseCode(String hoseCode) {
        String hosecode = "";
        for (int i = 0; i < hoseCode.length(); i++) {
            if (hoseCode.charAt(i) != '*') {
                hosecode = hosecode + hoseCode.charAt(i);
            }
        }
        return hosecode;
    }
}
