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

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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
    
    /**
     * Creates amino acid Molecule objects.
     * 
     * @return som, a SetOfMolecules containin the amino acids
     */
    public static SetOfMolecules createAAs() {
        // Create set of molecules
        SetOfMolecules som = new SetOfMolecules();
        
        // Create glycine
        Molecule glycine = new Molecule();
        glycine.addAtom(new Atom("N"));
        glycine.addAtom(new Atom("C"));
        glycine.addAtom(new Atom("C"));
        glycine.addAtom(new Atom("O"));
        glycine.addBond(0, 1, 1);
        glycine.addBond(1, 2, 1);
        glycine.addBond(2, 3, 2);
        glycine.setProperty("molecule_name", "GLY");
        glycine.setProperty("molecule_name_short", "G");
        glycine.setProperty("no_atoms", "4");
        glycine.setProperty("no_bonds", "3");
        glycine.setProperty("id", "0");
        som.addMolecule(glycine);
        
        // Create alanine
        Molecule alanine = new Molecule();
        alanine.addAtom(new Atom("N"));
        alanine.addAtom(new Atom("C"));
        alanine.addAtom(new Atom("C"));
        alanine.addAtom(new Atom("O"));
        alanine.addAtom(new Atom("C"));
        alanine.addBond(0, 1, 1);
        alanine.addBond(1, 2, 1);
        alanine.addBond(2, 3, 2);
        alanine.addBond(3, 4, 1);
        alanine.setProperty("molecule_name", "ALA");
        alanine.setProperty("molecule_name_short", "A");
        alanine.setProperty("no_atoms", "5");
        alanine.setProperty("no_bonds", "4");
        alanine.setProperty("id", "3");
        som.addMolecule(alanine);
        
        // Create valine
        Molecule valine = new Molecule();
        valine.addAtom(new Atom("N"));
        valine.addAtom(new Atom("C"));
        valine.addAtom(new Atom("C"));
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
        valine.setProperty("molecule_name", "VAL");
        valine.setProperty("molecule_name_short", "V");
        valine.setProperty("no_atoms", "7");
        valine.setProperty("no_bonds", "6");
        valine.setProperty("id", "7");
        som.addMolecule(valine);
        
        // Create leucine
        Molecule leucine = new Molecule();
        leucine.addAtom(new Atom("N"));
        leucine.addAtom(new Atom("C"));
        leucine.addAtom(new Atom("C"));
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
        leucine.setProperty("molecule_name", "LEU");
        leucine.setProperty("molecule_name_short", "L");
        leucine.setProperty("no_atoms", "8");
        leucine.setProperty("no_bonds", "7");
        leucine.setProperty("id", "13");
        som.addMolecule(leucine);
        
        // Create isoleucine
        Molecule isoleucine = new Molecule();
        isoleucine.addAtom(new Atom("N"));
        isoleucine.addAtom(new Atom("C"));
        isoleucine.addAtom(new Atom("C"));
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
        isoleucine.setProperty("molecule_name", "ILE");
        isoleucine.setProperty("molecule_name_short", "I");
        isoleucine.setProperty("no_atoms", "8");
        isoleucine.setProperty("no_bonds", "7");
        isoleucine.setProperty("id", "20");
        som.addMolecule(isoleucine);
        
        // Create serine
        Molecule serine = new Molecule();
        serine.addAtom(new Atom("N"));
        serine.addAtom(new Atom("C"));
        serine.addAtom(new Atom("C"));
        serine.addAtom(new Atom("O"));
        serine.addAtom(new Atom("C"));
        serine.addAtom(new Atom("O"));
        serine.addBond(0, 1, 1);
        serine.addBond(1, 2, 1);
        serine.addBond(2, 3, 2);
        serine.addBond(3, 4, 1);
        serine.addBond(4, 5, 1);
        serine.setProperty("molecule_name", "SER");
        serine.setProperty("molecule_name_short", "S");
        serine.setProperty("no_atoms", "6");
        serine.setProperty("no_bonds", "5");
        serine.setProperty("id", "27");
        som.addMolecule(serine);
        
        // Create threonine
        Molecule threonine = new Molecule();
        threonine.addAtom(new Atom("N"));
        threonine.addAtom(new Atom("C"));
        threonine.addAtom(new Atom("C"));
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
        threonine.setProperty("molecule_name", "THR");
        threonine.setProperty("molecule_name_short", "T");
        threonine.setProperty("no_atoms", "7");
        threonine.setProperty("no_bonds", "6");
        threonine.setProperty("id", "32");
        som.addMolecule(threonine);
        
        // Create cysteine
        Molecule cysteine = new Molecule();
        cysteine.addAtom(new Atom("N"));
        cysteine.addAtom(new Atom("C"));
        cysteine.addAtom(new Atom("C"));
        cysteine.addAtom(new Atom("O"));
        cysteine.addAtom(new Atom("C"));
        cysteine.addAtom(new Atom("S"));
        cysteine.addBond(0, 1, 1);
        cysteine.addBond(1, 2, 1);
        cysteine.addBond(2, 3, 2);
        cysteine.addBond(3, 4, 1);
        cysteine.addBond(4, 5, 1);
        cysteine.setProperty("molecule_name", "CYS");
        cysteine.setProperty("molecule_name_short", "C");
        cysteine.setProperty("no_atoms", "6");
        cysteine.setProperty("no_bonds", "5");
        cysteine.setProperty("id", "38");
        som.addMolecule(cysteine);
        
        // Create methionine
        Molecule methionine = new Molecule();
        methionine.addAtom(new Atom("N"));
        methionine.addAtom(new Atom("C"));
        methionine.addAtom(new Atom("C"));
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
        methionine.setProperty("molecule_name", "MET");
        methionine.setProperty("molecule_name_short", "M");
        methionine.setProperty("no_atoms", "8");
        methionine.setProperty("no_bonds", "7");
        methionine.setProperty("id", "43");
        som.addMolecule(methionine);
        
        // Create aspartic acid
        Molecule asparticAcid = new Molecule();
        asparticAcid.addAtom(new Atom("N"));
        asparticAcid.addAtom(new Atom("C"));
        asparticAcid.addAtom(new Atom("C"));
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
        asparticAcid.setProperty("molecule_name", "ASP");
        asparticAcid.setProperty("molecule_name_short", "D");
        asparticAcid.setProperty("no_atoms", "8");
        asparticAcid.setProperty("no_bonds", "7");
        asparticAcid.setProperty("id", "50");
        som.addMolecule(asparticAcid);
        
        // Create asparagine
        Molecule asparagine = new Molecule();
        asparagine.addAtom(new Atom("N"));
        asparagine.addAtom(new Atom("C"));
        asparagine.addAtom(new Atom("C"));
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
        asparagine.setProperty("molecule_name", "ASN");
        asparagine.setProperty("molecule_name_short", "N");
        asparagine.setProperty("no_atoms", "8");
        asparagine.setProperty("no_bonds", "7");
        asparagine.setProperty("id", "57");
        som.addMolecule(asparagine);
        
        // Create glutamic acid
        Molecule glutamicAcid = new Molecule();
        glutamicAcid.addAtom(new Atom("N"));
        glutamicAcid.addAtom(new Atom("C"));
        glutamicAcid.addAtom(new Atom("C"));
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
        glutamicAcid.setProperty("molecule_name", "GLU");
        glutamicAcid.setProperty("molecule_name_short", "E");
        glutamicAcid.setProperty("no_atoms", "9");
        glutamicAcid.setProperty("no_bonds", "8");
        glutamicAcid.setProperty("id", "64");
        som.addMolecule(glutamicAcid);
        
        // Create glutamine
        Molecule glutamine = new Molecule();
        glutamine.addAtom(new Atom("N"));
        glutamine.addAtom(new Atom("C"));
        glutamine.addAtom(new Atom("C"));
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
        glutamine.setProperty("molecule_name", "GLN");
        glutamine.setProperty("molecule_name_short", "Q");
        glutamine.setProperty("no_atoms", "9");
        glutamine.setProperty("no_bonds", "8");
        glutamine.setProperty("id", "72");
        som.addMolecule(glutamine);
        
        // Create arginine
        Molecule arginine = new Molecule();
        arginine.addAtom(new Atom("N"));
        arginine.addAtom(new Atom("C"));
        arginine.addAtom(new Atom("C"));
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
        arginine.setProperty("molecule_name", "ARG");
        arginine.setProperty("molecule_name_short", "R");
        arginine.setProperty("no_atoms", "11");
        arginine.setProperty("no_bonds", "10");
        arginine.setProperty("id", "80");
        som.addMolecule(arginine);
        
        // Create lysine
        Molecule lysine = new Molecule();
        lysine.addAtom(new Atom("N"));
        lysine.addAtom(new Atom("C"));
        lysine.addAtom(new Atom("C"));
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
        lysine.setProperty("molecule_name", "LYS");
        lysine.setProperty("molecule_name_short", "K");
        lysine.setProperty("no_atoms", "9");
        lysine.setProperty("no_bonds", "8");
        lysine.setProperty("id", "90");
        som.addMolecule(lysine);
        
        // Create histidine
        Molecule histidine = new Molecule();
        histidine.addAtom(new Atom("N"));
        histidine.addAtom(new Atom("C"));
        histidine.addAtom(new Atom("C"));
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
        histidine.setProperty("molecule_name", "HIS");
        histidine.setProperty("molecule_name_short", "H");
        histidine.setProperty("no_atoms", "10");
        histidine.setProperty("no_bonds", "10");
        histidine.setProperty("id", "98");
        som.addMolecule(histidine);
        
        // Create phenylalanine
        Molecule phenylalanine = new Molecule();
        phenylalanine.addAtom(new Atom("N"));
        phenylalanine.addAtom(new Atom("C"));
        phenylalanine.addAtom(new Atom("C"));
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
        phenylalanine.setProperty("molecule_name", "PHE");
        phenylalanine.setProperty("molecule_name_short", "F");
        phenylalanine.setProperty("no_atoms", "11");
        phenylalanine.setProperty("no_bonds", "11");
        phenylalanine.setProperty("id", "108");
        som.addMolecule(phenylalanine);
        
        // Create tyrosine
        Molecule tyrosine = new Molecule();
        tyrosine.addAtom(new Atom("N"));
        tyrosine.addAtom(new Atom("C"));
        tyrosine.addAtom(new Atom("C"));
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
        tyrosine.setProperty("molecule_name", "TYR");
        tyrosine.setProperty("molecule_name_short", "Y");
        tyrosine.setProperty("no_atoms", "12");
        tyrosine.setProperty("no_bonds", "12");
        tyrosine.setProperty("id", "119");
        som.addMolecule(tyrosine);
        
        // Create tryptophane
        Molecule tryptophane = new Molecule();
        tryptophane.addAtom(new Atom("N"));
        tryptophane.addAtom(new Atom("C"));
        tryptophane.addAtom(new Atom("C"));
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
        tryptophane.setProperty("molecule_name", "TRP");
        tryptophane.setProperty("molecule_name_short", "W");
        tryptophane.setProperty("no_atoms", "14");
        tryptophane.setProperty("no_bonds", "15");
        tryptophane.setProperty("id", "131");
        som.addMolecule(tryptophane);
        
        // Create proline
        Molecule proline = new Molecule();
        proline.addAtom(new Atom("N"));
        proline.addAtom(new Atom("C"));
        proline.addAtom(new Atom("C"));
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
        proline.setProperty("molecule_name", "PRO");
        proline.setProperty("molecule_name_short", "P");
        proline.setProperty("no_atoms", "7");
        proline.setProperty("no_bonds", "7");
        proline.setProperty("id", "146");
        som.addMolecule(proline);
        
        return som;
    }
}
