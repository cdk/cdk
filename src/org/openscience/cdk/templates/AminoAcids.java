/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2005-2006  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.templates;

import java.util.HashMap;
import java.util.Enumeration;

import org.openscience.cdk.AminoAcid;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.dict.DictRef;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.tools.manipulator.AminoAcidManipulator;
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

    	if (aminoAcids == null) {
            createAAs();
        }
        
        int[][] info = new int[153][4];
        
        int counter = 0;
        int total = 0;
        for (int aa=0; aa<aminoAcids.length; aa++) {
        	AminoAcid acid = aminoAcids[aa];
        	org.openscience.cdk.interfaces.IBond[] bonds = acid.getBonds();
        	logger.debug("#bonds for ", acid.getProperty(RESIDUE_NAME).toString(), " = " + bonds.length);
        	total += bonds.length;
        	logger.debug("total #bonds: ", total);
        	for (int bCounter=0; bCounter<bonds.length; bCounter++) {
        		info[counter][0] = counter;
        		info[counter][1] = acid.getAtomNumber(bonds[bCounter].getAtomAt(0));
        		info[counter][2] = acid.getAtomNumber(bonds[bCounter].getAtomAt(1));
        		info[counter][3] = (int)bonds[bCounter].getOrder();
        		counter++;
        	}
        }
        
        if (counter>152) {
        	logger.error("Error while creating AA info! Bond count is too small: ", counter);
        }
        
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

        IChemFile list = new org.openscience.cdk.ChemFile();
        CMLReader reader = new CMLReader(
        	AminoAcids.class.getClassLoader().getResourceAsStream(
        			"data/templates/list_aminoacids.cml"
        	)
        );
        try {
        	list = (IChemFile)reader.read(list);
        	IAtomContainer[] containers = ChemFileManipulator.getAllAtomContainers(list);
        	for (int i=0; i<containers.length; i++) {
        		logger.debug("Adding AA: ", containers[i]);
        		// convert into an AminoAcid
        		AminoAcid aminoAcid = new AminoAcid();
        		org.openscience.cdk.interfaces.IAtom[] atoms = containers[i].getAtoms();
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
        			org.openscience.cdk.interfaces.IAtom atom = atoms[atomCount];
        			String dictRef = (String)atom.getProperty("org.openscience.cdk.dict");
        			if (dictRef != null && dictRef.equals("pdb:nTerminus")) {
        				aminoAcid.addNTerminus(atom);
        			} else if (dictRef != null && dictRef.equals("pdb:cTerminus")) {
        				aminoAcid.addCTerminus(atom);
        			} else {
        				aminoAcid.addAtom(atom);
        			}
        		}
        		org.openscience.cdk.interfaces.IBond[] bonds = containers[i].getBonds();
        		for (int bondCount=0; bondCount<bonds.length; bondCount++) {
        			aminoAcid.addBond(bonds[bondCount]);
        		}
        		AminoAcidManipulator.removeAcidicOxygen(aminoAcid);
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
