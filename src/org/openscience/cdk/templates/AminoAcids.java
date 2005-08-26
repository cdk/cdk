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
import java.util.Enumeration;

import org.openscience.cdk.AminoAcid;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.dict.DictRef;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Tool that provides templates for the (natural) amino acids.
 *
 * @author      Martin Eklund <martin.eklund@farmbio.uu.se>
 * @cdk.module  pdb
 * @cdk.keyword templates
 * @cdk.keyword amino acids, stuctures
 * @cdk.created 2005-02-08
 */
public class AminoAcids {

	private static final LoggingTool logger = new LoggingTool(AminoAcids.class);
	
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

        ChemFile list = new ChemFile();
        CMLReader reader = new CMLReader(
        	AminoAcids.class.getClassLoader().getResourceAsStream(
        			"data/templates/list_aminoacids.cml"
        	)
        );
        try {
        	list = (ChemFile)reader.read(list);
        	AtomContainer[] containers = ChemFileManipulator.getAllAtomContainers(list);
        	for (int i=0; i<containers.length; i++) {
        		logger.debug("Adding AA: ", containers[i]);
        		// convert into an AminoAcid
        		AminoAcid aminoAcid = new AminoAcid();
        		Atom[] atoms = containers[i].getAtoms();
        		Enumeration props = containers[i].getProperties().keys();
        		while (props.hasMoreElements()) {
        			Object next = props.nextElement();
        			logger.debug("Prop class: " + next.getClass().getName());
        			logger.debug("Prop: " + next.toString());
        			if (next instanceof DictRef) {
        				DictRef dictRef = (DictRef)next;
        				// System.out.println("DictRef type: " + dictRef.getType());
        				if (dictRef.getType().equals("pdb:residueName")) {
        					aminoAcid.setProperty(RESIDUE_NAME, containers[i].getProperty(dictRef).toString().toUpperCase());
        				} else if (dictRef.getType().equals("pdb:oneLetterCode")) {
        					aminoAcid.setProperty(RESIDUE_NAME_SHORT, containers[i].getProperty(dictRef));
        				} else if (dictRef.getType().equals("pdb:id")) {
        					aminoAcid.setProperty(ID, containers[i].getProperty(dictRef));
        					logger.debug("Set AA ID to: ", containers[i].getProperty(dictRef));
        				} else {
        					logger.error("Cannot deal with dictRef!");
        				}
        			}
        		}
        		for (int atomCount=0; atomCount<atoms.length; atomCount++) {
        			Atom atom = atoms[atomCount];
        			String dictRef = (String)atom.getProperty("org.openscience.cdk.dict");
        			if (dictRef != null && dictRef.equals("pdb:nTerminus")) {
        				aminoAcid.addNTerminus(atom);
        			} else if (dictRef != null && dictRef.equals("pdb:cTerminus")) {
        				aminoAcid.addCTerminus(atom);
        			} else {
        				aminoAcid.addAtom(atom);
        			}
        		}
        		Bond[] bonds = containers[i].getBonds();
        		for (int bondCount=0; bondCount<bonds.length; bondCount++) {
        			aminoAcid.addBond(bonds[bondCount]);
        		}
                aminoAcid.setProperty(NO_ATOMS, "" + aminoAcid.getAtomCount());
                aminoAcid.setProperty(NO_BONDS, "" + aminoAcid.getBondCount());
                if (i < aminoAcids.length) {
                	aminoAcids[i] = aminoAcid;
                } else {
                	logger.error("Could not store AminoAcid! Array too short!");
                }
        	}
        } catch (Exception exception) {
        	logger.error("Failed reading file: ", exception.getMessage());
        	logger.debug(exception);
        }
        
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
