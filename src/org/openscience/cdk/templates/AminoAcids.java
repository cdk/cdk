/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.templates;

import java.util.HashMap;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.AminoAcid;
import org.openscience.cdk.SetOfAtomContainers;
import org.openscience.cdk.SetOfMolecules;

/**
 * Tool that provides templates for the (natural) amino acids.
 *
 * @author      Martin Eklund <martin.eklund@farmbio.uu.se>
 * @cdk.module  standard
 * @cdk.keyword templates
 * @cdk.keyword amino acids, stuctures
 * @cdk.created 2005-02-08
 */
public class AminoAcids {

    /**
     * Creates matrix with info about the bonds in the amino acids.
     * 0 = bond id, 1 = atom1 in bond, 2 = atom2 in bond, 3 = bond order.
     * @return info
     */
    public static int[][] aaBondInfo()	{
        
        int[][] info = {
                {0, 0, 1, 1},	/*GLYCIN*/  
                {1, 1, 2, 1},
                {2, 2, 3, 2},
                {3, 0, 1, 1},	/*ALANINE*/	
                {4, 1, 2, 1},
                {5, 2, 3, 2},
                {6, 1, 4, 1},
                {7, 0, 1, 1},	/*VALINE*/
                {8, 1, 2, 1},
                {9, 2, 3, 2},
                {10, 1, 4, 1},
                {11, 4, 5, 1},
                {12, 4, 6, 1},
                {13, 0, 1, 1},	/*LEUCINE*/
                {14, 1, 2, 1},
                {15, 2, 3, 2},
                {16, 1, 4, 1},
                {17, 4, 5, 1},
                {18, 5, 6, 1},
                {19, 5, 7, 1},
                {20, 0, 1, 1},	/*ISOLEUCINE*/
                {21, 1, 2, 1},
                {22, 2, 3, 2},
                {23, 1, 4, 1},
                {24, 4, 5, 1},	
                {25, 4, 6, 1},
                {26, 5, 7, 1},
                {27, 0, 1, 1},	/*SERINE*/
                {28, 1, 2, 1},
                {29, 2, 3, 2},
                {30, 1, 4, 1},
                {31, 4, 5, 1},
                {32, 0, 1, 1},	/*THREONINE*/
                {33, 1, 2, 1},
                {34, 2, 3, 2},
                {35, 1, 4, 1},
                {36, 4, 5, 1},
                {37, 4, 6, 1},
                {38, 0, 1, 1},	/*CYSTEINE*/
                {39, 1, 2, 1},
                {40, 2, 3, 2},
                {41, 1, 4, 1},
                {42, 4, 5, 1},
                {43, 0, 1, 1},	/*METHIONINE*/
                {44, 1, 2, 1},
                {45, 2, 3, 2},
                {46, 1, 4, 1},
                {47, 4, 5, 1},
                {48, 5, 6, 1},
                {49, 6, 7, 1},
                {50, 0, 1, 1},	/*ASPARTIC ACID*/	
                {51, 1, 2, 1},
                {52, 2, 3, 2},
                {53, 1, 4, 1},
                {54, 4, 5, 1},
                {55, 5, 6, 2},
                {56, 5, 7, 1},
                {57, 0, 1, 1},	/*ASPARAGINE*/
                {58, 1, 2, 1},
                {59, 2, 3, 2},
                {60, 1, 4, 1},
                {61, 4, 5, 1},
                {62, 5, 6, 2},
                {63, 5, 7, 1},
                {64, 0, 1, 1},	/*GLUTAMIC ACID*/
                {65, 1, 2, 1},
                {66, 2, 3, 2},
                {67, 1, 4, 1},
                {68, 4, 5, 1},
                {69, 5, 6, 1},
                {70, 6, 7, 2},
                {71, 6, 8, 1},
                {72, 0, 1, 1},	/*GLUTAMINE*/
                {73, 1, 2, 1},
                {74, 2, 3, 2},
                {75, 1, 4, 1},
                {76, 4, 5, 1},
                {77, 5, 6, 1},
                {78, 6, 7, 2},
                {79, 6, 8, 1},
                {80, 0, 1, 1},	/*ARGININE*/
                {81, 1, 2, 1},
                {82, 2, 3, 2},
                {83, 1, 4, 1},
                {84, 4, 5, 1},
                {85, 5, 6, 1},
                {86, 6, 7, 1},
                {87, 7, 8, 1},
                {88, 8, 9, 2},
                {89, 8, 10, 1},
                {90, 0, 1, 1},	/*LYSINE*/
                {91, 1, 2, 1},
                {92, 2, 3, 2},
                {93, 1, 4, 1},
                {94, 4, 5, 1},
                {95, 5, 6, 1},
                {96, 6, 7, 1},
                {97, 7, 8, 1},
                {98, 0, 1, 1},	/*HISTIDINE*/
                {99, 1, 2, 1},
                {100, 2, 3, 2},
                {101, 1, 4, 1},
                {102, 4, 5, 1},
                {103, 5, 6, 1},
                {104, 5, 7, 2},
                {105, 6, 8, 2},
                {106, 7, 9, 1},
                {107, 8, 9, 2},
                {108, 0, 1, 1},	/*PHENYLALANINE*/
                {109, 1, 2, 1},
                {110, 2, 3, 2},
                {111, 1, 4, 1},
                {112, 4, 5, 1},
                {113, 5, 6, 1},
                {114, 5, 7, 2},
                {115, 6, 8, 2},
                {116, 7, 9, 1},
                {117, 8, 10, 1},
                {118, 9, 10, 2},
                {119, 0, 1, 1},	/*TYROSINE*/
                {120, 1, 2, 1},
                {121, 2, 3, 2},
                {122, 1, 4, 1},
                {123, 4, 5, 1},
                {124, 5, 6, 1},
                {125, 5, 7, 2},
                {126, 6, 8, 2},
                {127, 7, 9, 1},
                {128, 8, 10, 1},
                {129, 9, 10, 2},
                {130, 10, 11, 1},
                {131, 0, 1, 1},	/*TRYPTOPHANE*/
                {132, 1, 2, 1},
                {133, 2, 3, 2},
                {134, 1, 4, 1},
                {135, 4, 5, 1},
                {136, 5, 6, 2},
                {137, 5, 7, 1},
                {138, 6, 8, 1},
                {139, 8, 9, 1},
                {140, 7, 9, 2},
                {141, 7, 10, 1},
                {142, 9, 11, 1},
                {143, 10, 12, 2},
                {144, 11, 13, 2},
                {145, 12, 13, 1},
                {146, 0, 1, 1},	/*PROLINE*/
                {147, 1, 2, 1},
                {148, 2, 3, 2},
                {149, 1, 4, 1},
                {150, 4, 5, 1},
                {151, 5, 6, 1},
                {152, 0, 6, 1}};
        
        return info;
    }
    
    private static AminoAcid[] aminoAcids = null;
    
    public final static String RESIDUE_NAME = "residueName";
    public final static String RESIDUE_NAME_SHORT = "residueNameShort";
    public final static String NO_ATOMS = "noOfAtoms";
    public final static String NO_BONDS = "noOfBonds";
    public final static String ID = "id";
    
    /**
     * Creates amino acid AminoAcid objects.
     * 
     * @return aminoAcids, a HashMap containing the amino acids as AminoAcids.
     */
    public static AminoAcid[] createAAs() {
        if (aminoAcids != null) {
            return aminoAcids;
        }
        
        // Create set of AtomContainers
        aminoAcids = new AminoAcid[20];
        
        // Create glycine
        AminoAcid glycine = new AminoAcid();
        glycine.addNTerminus(new Atom("N"));
        glycine.addAtom(new Atom("C"));
        glycine.addCTerminus(new Atom("C"));
        glycine.addAtom(new Atom("O"));
        glycine.addBond(0, 1, 1);
        glycine.addBond(1, 2, 1);
        glycine.addBond(2, 3, 2);
        glycine.setProperty(RESIDUE_NAME, "GLY");
        glycine.setProperty(RESIDUE_NAME_SHORT, "G");
        glycine.setProperty(NO_ATOMS, "4");
        glycine.setProperty(NO_BONDS, "3");
        glycine.setProperty(ID, "0");
        aminoAcids[0] = glycine;
        
        // Create alanine
        AminoAcid alanine = new AminoAcid();
        alanine.addNTerminus(new Atom("N"));
        alanine.addAtom(new Atom("C"));
        alanine.addCTerminus(new Atom("C"));
        alanine.addAtom(new Atom("O"));
        alanine.addAtom(new Atom("C"));
        alanine.addBond(0, 1, 1);
        alanine.addBond(1, 2, 1);
        alanine.addBond(2, 3, 2);
        alanine.addBond(3, 4, 1);
        alanine.setProperty(RESIDUE_NAME, "ALA");
        alanine.setProperty(RESIDUE_NAME_SHORT, "A");
        alanine.setProperty(NO_ATOMS, "5");
        alanine.setProperty(NO_BONDS, "4");
        alanine.setProperty(ID, "3");
        aminoAcids[1] = alanine;
        
        // Create valine
        AminoAcid valine = new AminoAcid();
        valine.addNTerminus(new Atom("N"));
        valine.addAtom(new Atom("C"));
        valine.addCTerminus(new Atom("C"));
        valine.addAtom(new Atom("O"));
        valine.addAtom(new Atom("C"));
        valine.addAtom(new Atom("C"));
        valine.addAtom(new Atom("C"));
        valine.addBond(0, 1, 1);
        valine.addBond(1, 2, 1);
        valine.addBond(2, 3, 2);
        valine.addBond(3, 4, 1);
        valine.addBond(4, 5, 1);
        valine.addBond(4, 6, 1);
        valine.setProperty(RESIDUE_NAME, "VAL");
        valine.setProperty(RESIDUE_NAME_SHORT, "V");
        valine.setProperty(NO_ATOMS, "7");
        valine.setProperty(NO_BONDS, "6");
        valine.setProperty(ID, "7");
        aminoAcids[2] = valine;
        
        // Create leucine
        AminoAcid leucine = new AminoAcid();
        leucine.addNTerminus(new Atom("N"));
        leucine.addAtom(new Atom("C"));
        leucine.addCTerminus(new Atom("C"));
        leucine.addAtom(new Atom("O"));
        leucine.addAtom(new Atom("C"));
        leucine.addAtom(new Atom("C"));
        leucine.addAtom(new Atom("C"));
        leucine.addAtom(new Atom("C"));
        leucine.addBond(0, 1, 1);
        leucine.addBond(1, 2, 1);
        leucine.addBond(2, 3, 2);
        leucine.addBond(3, 4, 1);
        leucine.addBond(4, 5, 1);
        leucine.addBond(5, 6, 1);
        leucine.addBond(5, 7, 1);
        leucine.setProperty(RESIDUE_NAME, "LEU");
        leucine.setProperty(RESIDUE_NAME_SHORT, "L");
        leucine.setProperty(NO_ATOMS, "8");
        leucine.setProperty(NO_BONDS, "7");
        leucine.setProperty(ID, "13");
        aminoAcids[3] = leucine;
        
        // Create isoleucine
        AminoAcid isoleucine = new AminoAcid();
        isoleucine.addNTerminus(new Atom("N"));
        isoleucine.addAtom(new Atom("C"));
        isoleucine.addCTerminus(new Atom("C"));
        isoleucine.addAtom(new Atom("O"));
        isoleucine.addAtom(new Atom("C"));
        isoleucine.addAtom(new Atom("C"));
        isoleucine.addAtom(new Atom("C"));
        isoleucine.addAtom(new Atom("C"));
        isoleucine.addBond(0, 1, 1);
        isoleucine.addBond(1, 2, 1);
        isoleucine.addBond(2, 3, 2);
        isoleucine.addBond(3, 4, 1);
        isoleucine.addBond(4, 5, 1);
        isoleucine.addBond(4, 6, 1);
        isoleucine.addBond(6, 7, 1);
        isoleucine.setProperty(RESIDUE_NAME, "ILE");
        isoleucine.setProperty(RESIDUE_NAME_SHORT, "I");
        isoleucine.setProperty(NO_ATOMS, "8");
        isoleucine.setProperty(NO_BONDS, "7");
        isoleucine.setProperty(ID, "20");
        aminoAcids[4] = isoleucine;
        
        // Create serine
        AminoAcid serine = new AminoAcid();
        serine.addNTerminus(new Atom("N"));
        serine.addAtom(new Atom("C"));
        serine.addCTerminus(new Atom("C"));
        serine.addAtom(new Atom("O"));
        serine.addAtom(new Atom("C"));
        serine.addAtom(new Atom("O"));
        serine.addBond(0, 1, 1);
        serine.addBond(1, 2, 1);
        serine.addBond(2, 3, 2);
        serine.addBond(3, 4, 1);
        serine.addBond(4, 5, 1);
        serine.setProperty(RESIDUE_NAME, "SER");
        serine.setProperty(RESIDUE_NAME_SHORT, "S");
        serine.setProperty(NO_ATOMS, "6");
        serine.setProperty(NO_BONDS, "5");
        serine.setProperty(ID, "27");
        aminoAcids[5] = serine;
        
        // Create threonine
        AminoAcid threonine = new AminoAcid();
        threonine.addNTerminus(new Atom("N"));
        threonine.addAtom(new Atom("C"));
        threonine.addCTerminus(new Atom("C"));
        threonine.addAtom(new Atom("O"));
        threonine.addAtom(new Atom("C"));
        threonine.addAtom(new Atom("O"));
        threonine.addAtom(new Atom("C"));
        threonine.addBond(0, 1, 1);
        threonine.addBond(1, 2, 1);
        threonine.addBond(2, 3, 2);
        threonine.addBond(1, 4, 1);
        threonine.addBond(4, 5, 1);
        threonine.addBond(4, 6, 1);
        threonine.setProperty(RESIDUE_NAME, "THR");
        threonine.setProperty(RESIDUE_NAME_SHORT, "T");
        threonine.setProperty(NO_ATOMS, "7");
        threonine.setProperty(NO_BONDS, "6");
        threonine.setProperty(ID, "32");
        aminoAcids[6] = threonine;
        
        // Create cysteine
        AminoAcid cysteine = new AminoAcid();
        cysteine.addNTerminus(new Atom("N"));
        cysteine.addAtom(new Atom("C"));
        cysteine.addCTerminus(new Atom("C"));
        cysteine.addAtom(new Atom("O"));
        cysteine.addAtom(new Atom("C"));
        cysteine.addAtom(new Atom("S"));
        cysteine.addBond(0, 1, 1);
        cysteine.addBond(1, 2, 1);
        cysteine.addBond(2, 3, 2);
        cysteine.addBond(3, 4, 1);
        cysteine.addBond(4, 5, 1);
        cysteine.setProperty(RESIDUE_NAME, "CYS");
        cysteine.setProperty(RESIDUE_NAME_SHORT, "C");
        cysteine.setProperty(NO_ATOMS, "6");
        cysteine.setProperty(NO_BONDS, "5");
        cysteine.setProperty(ID, "38");
        aminoAcids[7] = cysteine;
        
        // Create methionine
        AminoAcid methionine = new AminoAcid();
        methionine.addNTerminus(new Atom("N"));
        methionine.addAtom(new Atom("C"));
        methionine.addCTerminus(new Atom("C"));
        methionine.addAtom(new Atom("O"));
        methionine.addAtom(new Atom("C"));
        methionine.addAtom(new Atom("C"));
        methionine.addAtom(new Atom("S"));
        methionine.addAtom(new Atom("C"));
        methionine.addBond(0, 1, 1);
        methionine.addBond(1, 2, 1);
        methionine.addBond(2, 3, 2);
        methionine.addBond(3, 4, 1);
        methionine.addBond(4, 5, 1);
        methionine.addBond(5, 6, 1);
        methionine.addBond(6, 7, 1);
        methionine.setProperty(RESIDUE_NAME, "MET");
        methionine.setProperty(RESIDUE_NAME_SHORT, "M");
        methionine.setProperty(NO_ATOMS, "8");
        methionine.setProperty(NO_BONDS, "7");
        methionine.setProperty(ID, "43");
        aminoAcids[8] = methionine;
        
        // Create aspartic acid
        AminoAcid asparticAcid = new AminoAcid();
        asparticAcid.addNTerminus(new Atom("N"));
        asparticAcid.addAtom(new Atom("C"));
        asparticAcid.addCTerminus(new Atom("C"));
        asparticAcid.addAtom(new Atom("O"));
        asparticAcid.addAtom(new Atom("C"));
        asparticAcid.addAtom(new Atom("C"));
        asparticAcid.addAtom(new Atom("O"));
        asparticAcid.addAtom(new Atom("O"));
        asparticAcid.addBond(0, 1, 1);
        asparticAcid.addBond(1, 2, 1);
        asparticAcid.addBond(2, 3, 2);
        asparticAcid.addBond(3, 4, 1);
        asparticAcid.addBond(4, 5, 1);
        asparticAcid.addBond(5, 6, 2);
        asparticAcid.addBond(6, 7, 1);
        asparticAcid.setProperty(RESIDUE_NAME, "ASP");
        asparticAcid.setProperty(RESIDUE_NAME_SHORT, "D");
        asparticAcid.setProperty(NO_ATOMS, "8");
        asparticAcid.setProperty(NO_BONDS, "7");
        asparticAcid.setProperty(ID, "50");
        aminoAcids[9] = asparticAcid;
        
        // Create asparagine
        AminoAcid asparagine = new AminoAcid();
        asparagine.addNTerminus(new Atom("N"));
        asparagine.addAtom(new Atom("C"));
        asparagine.addCTerminus(new Atom("C"));
        asparagine.addAtom(new Atom("O"));
        asparagine.addAtom(new Atom("C"));
        asparagine.addAtom(new Atom("C"));
        asparagine.addAtom(new Atom("O"));
        asparagine.addAtom(new Atom("N"));
        asparagine.addBond(0, 1, 1);
        asparagine.addBond(1, 2, 1);
        asparagine.addBond(2, 3, 2);
        asparagine.addBond(3, 4, 1);
        asparagine.addBond(4, 5, 1);
        asparagine.addBond(5, 6, 2);
        asparagine.addBond(5, 7, 1);
        asparagine.setProperty(RESIDUE_NAME, "ASN");
        asparagine.setProperty(RESIDUE_NAME_SHORT, "N");
        asparagine.setProperty(NO_ATOMS, "8");
        asparagine.setProperty(NO_BONDS, "7");
        asparagine.setProperty(ID, "57");
        aminoAcids[10] = asparagine;
        
        // Create glutamic acid
        AminoAcid glutamicAcid = new AminoAcid();
        glutamicAcid.addNTerminus(new Atom("N"));
        glutamicAcid.addAtom(new Atom("C"));
        glutamicAcid.addCTerminus(new Atom("C"));
        glutamicAcid.addAtom(new Atom("O"));
        glutamicAcid.addAtom(new Atom("C"));
        glutamicAcid.addAtom(new Atom("C"));
        glutamicAcid.addAtom(new Atom("C"));
        glutamicAcid.addAtom(new Atom("O"));
        glutamicAcid.addAtom(new Atom("O"));
        glutamicAcid.addBond(0, 1, 1);
        glutamicAcid.addBond(1, 2, 1);
        glutamicAcid.addBond(2, 3, 2);
        glutamicAcid.addBond(3, 4, 1);
        glutamicAcid.addBond(4, 5, 1);
        glutamicAcid.addBond(5, 6, 1);
        glutamicAcid.addBond(6, 7, 2);
        glutamicAcid.addBond(7, 8, 1);
        glutamicAcid.setProperty(RESIDUE_NAME, "GLU");
        glutamicAcid.setProperty(RESIDUE_NAME_SHORT, "E");
        glutamicAcid.setProperty(NO_ATOMS, "9");
        glutamicAcid.setProperty(NO_BONDS, "8");
        glutamicAcid.setProperty(ID, "64");
        aminoAcids[11] = glutamicAcid;
        
        // Create glutamine
        AminoAcid glutamine = new AminoAcid();
        glutamine.addNTerminus(new Atom("N"));
        glutamine.addAtom(new Atom("C"));
        glutamine.addCTerminus(new Atom("C"));
        glutamine.addAtom(new Atom("O"));
        glutamine.addAtom(new Atom("C"));
        glutamine.addAtom(new Atom("C"));
        glutamine.addAtom(new Atom("C"));
        glutamine.addAtom(new Atom("O"));
        glutamine.addAtom(new Atom("N"));
        glutamine.addBond(0, 1, 1);
        glutamine.addBond(1, 2, 1);
        glutamine.addBond(2, 3, 2);
        glutamine.addBond(3, 4, 1);
        glutamine.addBond(4, 5, 1);
        glutamine.addBond(5, 6, 1);
        glutamine.addBond(6, 7, 2);
        glutamine.addBond(6, 8, 1);
        glutamine.setProperty(RESIDUE_NAME, "GLN");
        glutamine.setProperty(RESIDUE_NAME_SHORT, "Q");
        glutamine.setProperty(NO_ATOMS, "9");
        glutamine.setProperty(NO_BONDS, "8");
        glutamine.setProperty(ID, "72");
        aminoAcids[12] = glutamine;
        
        // Create arginine
        AminoAcid arginine = new AminoAcid();
        arginine.addNTerminus(new Atom("N"));
        arginine.addAtom(new Atom("C"));
        arginine.addCTerminus(new Atom("C"));
        arginine.addAtom(new Atom("O"));
        arginine.addAtom(new Atom("C"));
        arginine.addAtom(new Atom("C"));
        arginine.addAtom(new Atom("C"));
        arginine.addAtom(new Atom("N"));
        arginine.addAtom(new Atom("C"));
        arginine.addAtom(new Atom("N"));
        arginine.addAtom(new Atom("N"));
        arginine.addBond(0, 1, 1);
        arginine.addBond(1, 2, 1);
        arginine.addBond(2, 3, 2);
        arginine.addBond(3, 4, 1);
        arginine.addBond(4, 5, 1);
        arginine.addBond(5, 6, 1);
        arginine.addBond(6, 7, 1);
        arginine.addBond(7, 8, 1);
        arginine.addBond(8, 9, 2);
        arginine.addBond(8, 10, 1);
        arginine.setProperty(RESIDUE_NAME, "ARG");
        arginine.setProperty(RESIDUE_NAME_SHORT, "R");
        arginine.setProperty(NO_ATOMS, "11");
        arginine.setProperty(NO_BONDS, "10");
        arginine.setProperty(ID, "80");
        aminoAcids[13] = arginine;
        
        // Create lysine
        AminoAcid lysine = new AminoAcid();
        lysine.addNTerminus(new Atom("N"));
        lysine.addAtom(new Atom("C"));
        lysine.addCTerminus(new Atom("C"));
        lysine.addAtom(new Atom("O"));
        lysine.addAtom(new Atom("C"));
        lysine.addAtom(new Atom("C"));
        lysine.addAtom(new Atom("C"));
        lysine.addAtom(new Atom("C"));
        lysine.addAtom(new Atom("N"));
        lysine.addBond(0, 1, 1);
        lysine.addBond(1, 2, 1);
        lysine.addBond(2, 3, 2);
        lysine.addBond(3, 4, 1);
        lysine.addBond(4, 5, 1);
        lysine.addBond(5, 6, 1);
        lysine.addBond(6, 7, 1);
        lysine.addBond(7, 8, 1);
        lysine.setProperty(RESIDUE_NAME, "LYS");
        lysine.setProperty(RESIDUE_NAME_SHORT, "K");
        lysine.setProperty(NO_ATOMS, "9");
        lysine.setProperty(NO_BONDS, "8");
        lysine.setProperty(ID, "90");
        aminoAcids[14] = lysine;
        
        // Create histidine
        AminoAcid histidine = new AminoAcid();
        histidine.addNTerminus(new Atom("N"));
        histidine.addAtom(new Atom("C"));
        histidine.addCTerminus(new Atom("C"));
        histidine.addAtom(new Atom("O"));
        histidine.addAtom(new Atom("C"));
        histidine.addAtom(new Atom("C"));
        histidine.addAtom(new Atom("N"));
        histidine.addAtom(new Atom("C"));
        histidine.addAtom(new Atom("C"));
        histidine.addAtom(new Atom("N"));
        histidine.addBond(0, 1, 1);
        histidine.addBond(1, 2, 1);
        histidine.addBond(2, 3, 2);
        histidine.addBond(3, 4, 1);
        histidine.addBond(4, 5, 1);
        histidine.addBond(5, 6, 1);
        histidine.addBond(5, 7, 2);
        histidine.addBond(6, 8, 2);
        histidine.addBond(7, 9, 1);
        histidine.addBond(8, 9, 2);
        histidine.setProperty(RESIDUE_NAME, "HIS");
        histidine.setProperty(RESIDUE_NAME_SHORT, "H");
        histidine.setProperty(NO_ATOMS, "10");
        histidine.setProperty(NO_BONDS, "10");
        histidine.setProperty(ID, "98");
        aminoAcids[15] = histidine;
        
        // Create phenylalanine
        AminoAcid phenylalanine = new AminoAcid();
        phenylalanine.addNTerminus(new Atom("N"));
        phenylalanine.addAtom(new Atom("C"));
        phenylalanine.addCTerminus(new Atom("C"));
        phenylalanine.addAtom(new Atom("O"));
        phenylalanine.addAtom(new Atom("C"));
        phenylalanine.addAtom(new Atom("C"));
        phenylalanine.addAtom(new Atom("C"));
        phenylalanine.addAtom(new Atom("C"));
        phenylalanine.addAtom(new Atom("C"));
        phenylalanine.addAtom(new Atom("C"));
        phenylalanine.addAtom(new Atom("C"));
        phenylalanine.addBond(0, 1, 1);
        phenylalanine.addBond(1, 2, 1);
        phenylalanine.addBond(2, 3, 2);
        phenylalanine.addBond(3, 4, 1);
        phenylalanine.addBond(4, 5, 1);
        phenylalanine.addBond(5, 6, 1);
        phenylalanine.addBond(5, 7, 2);
        phenylalanine.addBond(6, 8, 2);
        phenylalanine.addBond(7, 9, 1);
        phenylalanine.addBond(8, 10, 1);
        phenylalanine.addBond(9, 10, 2);
        phenylalanine.setProperty(RESIDUE_NAME, "PHE");
        phenylalanine.setProperty(RESIDUE_NAME_SHORT, "F");
        phenylalanine.setProperty(NO_ATOMS, "11");
        phenylalanine.setProperty(NO_BONDS, "11");
        phenylalanine.setProperty(ID, "108");
        aminoAcids[16] = phenylalanine;
        
        // Create tyrosine
        AminoAcid tyrosine = new AminoAcid();
        tyrosine.addNTerminus(new Atom("N"));
        tyrosine.addAtom(new Atom("C"));
        tyrosine.addCTerminus(new Atom("C"));
        tyrosine.addAtom(new Atom("O"));
        tyrosine.addAtom(new Atom("C"));
        tyrosine.addAtom(new Atom("C"));
        tyrosine.addAtom(new Atom("C"));
        tyrosine.addAtom(new Atom("C"));
        tyrosine.addAtom(new Atom("C"));
        tyrosine.addAtom(new Atom("C"));
        tyrosine.addAtom(new Atom("C"));
        tyrosine.addAtom(new Atom("O"));
        tyrosine.addBond(0, 1, 1);
        tyrosine.addBond(1, 2, 1);
        tyrosine.addBond(2, 3, 2);
        tyrosine.addBond(3, 4, 1);
        tyrosine.addBond(4, 5, 1);
        tyrosine.addBond(5, 6, 1);
        tyrosine.addBond(5, 7, 2);
        tyrosine.addBond(6, 8, 2);
        tyrosine.addBond(7, 9, 1);
        tyrosine.addBond(8, 10, 1);
        tyrosine.addBond(9, 10, 2);
        tyrosine.addBond(10, 11, 1);
        tyrosine.setProperty(RESIDUE_NAME, "TYR");
        tyrosine.setProperty(RESIDUE_NAME_SHORT, "Y");
        tyrosine.setProperty(NO_ATOMS, "12");
        tyrosine.setProperty(NO_BONDS, "12");
        tyrosine.setProperty(ID, "119");
        aminoAcids[17] = tyrosine;
        
        // Create tryptophane
        AminoAcid tryptophane = new AminoAcid();
        tryptophane.addNTerminus(new Atom("N"));
        tryptophane.addAtom(new Atom("C"));
        tryptophane.addCTerminus(new Atom("C"));
        tryptophane.addAtom(new Atom("O"));
        tryptophane.addAtom(new Atom("C"));
        tryptophane.addAtom(new Atom("C"));
        tryptophane.addAtom(new Atom("C"));
        tryptophane.addAtom(new Atom("C"));
        tryptophane.addAtom(new Atom("N"));
        tryptophane.addAtom(new Atom("C"));
        tryptophane.addAtom(new Atom("C"));
        tryptophane.addAtom(new Atom("C"));
        tryptophane.addAtom(new Atom("C"));
        tryptophane.addAtom(new Atom("C"));
        tryptophane.addBond(0, 1, 1);
        tryptophane.addBond(1, 2, 1);
        tryptophane.addBond(2, 3, 2);
        tryptophane.addBond(3, 4, 1);
        tryptophane.addBond(4, 5, 1);
        tryptophane.addBond(5, 6, 2);
        tryptophane.addBond(5, 7, 1);
        tryptophane.addBond(6, 8, 1);
        tryptophane.addBond(8, 9, 1);
        tryptophane.addBond(7, 9, 2);
        tryptophane.addBond(7, 10, 1);
        tryptophane.addBond(9, 11, 1);
        tryptophane.addBond(10, 12, 2);
        tryptophane.addBond(11, 13, 2);
        tryptophane.addBond(12, 13, 1);
        tryptophane.setProperty(RESIDUE_NAME, "TRP");
        tryptophane.setProperty(RESIDUE_NAME_SHORT, "W");
        tryptophane.setProperty(NO_ATOMS, "14");
        tryptophane.setProperty(NO_BONDS, "15");
        tryptophane.setProperty(ID, "131");
        aminoAcids[18] = tryptophane;
        
        // Create proline
        AminoAcid proline = new AminoAcid();
        proline.addNTerminus(new Atom("N"));
        proline.addAtom(new Atom("C"));
        proline.addCTerminus(new Atom("C"));
        proline.addAtom(new Atom("O"));
        proline.addAtom(new Atom("C"));
        proline.addAtom(new Atom("C"));
        proline.addAtom(new Atom("C"));
        proline.addBond(0, 1, 1);
        proline.addBond(1, 2, 1);
        proline.addBond(2, 3, 2);
        proline.addBond(3, 4, 1);
        proline.addBond(4, 5, 1);
        proline.addBond(5, 6, 1);
        proline.addBond(1, 6, 1);
        proline.setProperty(RESIDUE_NAME, "PRO");
        proline.setProperty(RESIDUE_NAME_SHORT, "P");
        proline.setProperty(NO_ATOMS, "7");
        proline.setProperty(NO_BONDS, "7");
        proline.setProperty(ID, "146");
        aminoAcids[19] = proline;
        
        return aminoAcids;
    }

    /**
     * Returns a HashMap where the key is one of G, A, V, L, I, S, T, C, M, D,
     * N, E, Q, R, K, H, F, Y, W and P.
     */
    public static HashMap getHashMapBySingleCharCode() {
        AminoAcid[] monomers = createAAs();
        HashMap map = new HashMap();
        for (int i=0; i<monomers.length; i++) {
            map.put(monomers[i].getProperty(RESIDUE_NAME_SHORT), monomers[i]);
        }
        return map;
    }
    
    /**
     * Returns a HashMap where the key is one of GLY, ALA, VAL, LEU, ILE, SER,
     * THR, CYS, MET, ASP, ASN, GLU, GLN, ARG, LYS, HIS, PHE, TYR, TRP AND PRO.
     */
    public static HashMap getHashMapByThreeLetterCode() {
        AminoAcid[] monomers = createAAs();
        HashMap map = new HashMap();
        for (int i=0; i<monomers.length; i++) {
            map.put(monomers[i].getProperty(RESIDUE_NAME), monomers[i]);
        }
        return map;
    }
}
