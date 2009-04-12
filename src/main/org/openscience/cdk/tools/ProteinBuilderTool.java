/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) Project
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
package org.openscience.cdk.tools;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.templates.AminoAcids;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class that facilitates building protein structures. Building DNA and RNA
 * is done by a complementary class <code>NucleicAcidBuilderTool</code> (to be
 * written).
 * 
 * @cdk.module pdb
 * @cdk.svnrev  $Revision$
 */
public class ProteinBuilderTool {

    private static LoggingTool logger = new LoggingTool(ProteinBuilderTool.class);

    /**
     * Builds a protein by connecting a new amino acid at the N-terminus of the
     * given strand.
     *
     * @param protein protein to which the strand belongs
     * @param aaToAdd amino acid to add to the strand of the protein
     * @param strand  strand to which the protein is added
     */
    public static BioPolymer addAminoAcidAtNTerminus(
        BioPolymer protein, AminoAcid aaToAdd, Strand strand, AminoAcid aaToAddTo)
    {
        // then add the amino acid
        addAminoAcid(protein, aaToAdd, strand);
        // Now think about the protein back bone connection
        if (protein.getMonomerCount() == 0) {
            // make the connection between that aminoAcid's C-terminus and the 
            // protein's N-terminus
            protein.addBond(
                new Bond(aaToAddTo.getNTerminus(), aaToAdd.getCTerminus(), IBond.Order.SINGLE)
            );
        } // else : no current N-terminus, so nothing special to do
        return protein;
    }

    /**
     * Builds a protein by connecting a new amino acid at the C-terminus of the
     * given strand. The acidic oxygen of the added amino acid is removed so that
     * additional amino acids can be added savely. But this also means that you
     * might want to add an oxygen at the end of the protein building!
     *
     * @param protein protein to which the strand belongs
     * @param aaToAdd amino acid to add to the strand of the protein
     * @param strand  strand to which the protein is added
     */
    public static BioPolymer addAminoAcidAtCTerminus(
        BioPolymer protein, AminoAcid aaToAdd, Strand strand, AminoAcid aaToAddTo)
    {
        // then add the amino acid
        addAminoAcid(protein, aaToAdd, strand);
        // Now think about the protein back bone connection
        if ((protein.getMonomerCount() != 0) && (aaToAddTo != null)) {
            // make the connection between that aminoAcid's N-terminus and the 
            // protein's C-terminus
            protein.addBond(
                new Bond(aaToAddTo.getCTerminus(), aaToAdd.getNTerminus(), IBond.Order.SINGLE)
            );
        } // else : no current C-terminus, so nothing special to do
        return protein;
    }

    /**
     * Creates a BioPolymer from a sequence of amino acid as identified by a
     * the sequence of there one letter codes.
     *
     * <p>For example:
     * <pre>
     * BioPolymer protein = ProteinBuilderTool.createProtein("GAGA");
     * </pre>
     */
    public static BioPolymer createProtein(String sequence) throws CDKException {
        Map<String,AminoAcid> templates = AminoAcids.getHashMapBySingleCharCode();
        BioPolymer protein = new BioPolymer();
        Strand strand = new Strand();
        AminoAcid previousAA = null;
        for (int i=0; i<sequence.length(); i++) {
            String aminoAcidCode = "" + sequence.charAt(i);
            logger.debug("Adding AA: " + aminoAcidCode);
            if (aminoAcidCode.equals(" ")) {
                // fine, just skip spaces
            } else {
                AminoAcid aminoAcid = (AminoAcid)templates.get(aminoAcidCode);
                if (aminoAcid == null) {
                    throw new CDKException("Cannot build sequence! Unknown amino acid: " + aminoAcidCode);
                }
                try {
                    aminoAcid = (AminoAcid)aminoAcid.clone();
                } catch (CloneNotSupportedException e) {
                    throw new CDKException("Cannot build sequence! Clone exception: " + e.getMessage());
                }
                aminoAcid.setMonomerName(aminoAcidCode + i);
                logger.debug("protein: ", protein);
                logger.debug("strand: ", strand);
                addAminoAcidAtCTerminus(protein, aminoAcid, strand, previousAA);
                previousAA = aminoAcid;
            }
        }
        // add the last oxygen of the protein
        Atom oxygen = new Atom("O");
        // ... to amino acid
        previousAA.addAtom(oxygen);
        Bond bond = new Bond(oxygen, previousAA.getCTerminus(), IBond.Order.SINGLE);
        previousAA.addBond(bond);
        // ... and to protein
        protein.addAtom(oxygen, previousAA, strand);
        protein.addBond(bond);
        return protein;
    }

    private static BioPolymer addAminoAcid(BioPolymer protein, AminoAcid aaToAdd, Strand strand) {
        for (IAtom atom : aaToAdd.atoms())  protein.addAtom(atom, aaToAdd, strand);
        for (IBond bond : aaToAdd.bonds()) protein.addBond(bond);
        return protein;
    }
}
