/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.atomtype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.AtomTypeTools;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Class implements methods to assign mmff94 atom types for a specific atom in
 * an molecule. The full list of mmff94 atom types is defined in the file
 * <b>cdk/config/data/mmff94_atomtypes.xml</b>.
 *
 * @author         cho
 * @cdk.created    2005-18-07
 * @cdk.module     experimental
 */
public class MMFF94AtomTypeMatcher implements AtomTypeMatcher {

	private LoggingTool logger;

	double maxBondOrder = 0;
	private AtomTypeFactory factory = null;
	AtomTypeTools atomTypeTools=null;
	
	String [] atomTypeIds={"C","Csp2","C=","Csp","CO2M","CNN+","C%","CIM+","CR4R","CR3R","CE4R",
			"Car","C5A","C5B","C5","HC","HO","HN","HOCO","HN=C","HN2",
			"HOCC","HOH","HOS","HN+","HO+","HO=+","HP","O","O=","OX",
			"OM","O+","O=+","OH2","Oar","N","N=C","NC=C","NSP","=N=",
			"NAZT","N+","N2OX","N3OX","NC#N","NO3","N=O","NC=O","NSO","N+=",
			"NCN+","NGD+","NR%","NM","N5M","NPYD","NPYL","NPD+","N5A","N5B",
			"NPOX","N5OX","N5+","N5","S","S=C",">SN","SO2","SX","SO2M",
			"=SO","Sthi","PTET","P","-P=C","F","CL","BR","I","SI",
			"CL04","FE+2","FE+3","F-","CL-","BR-","LI+","NA+","K+","ZN+2",
			"CA+2","CU+1","CU+2","MG+2","DU"};

	/**
	 * Constructor for the MMFF94AtomTypeMatcher object.
	 */
	public MMFF94AtomTypeMatcher() {
		logger = new LoggingTool(this);
		atomTypeTools=new AtomTypeTools();
	}


	/**
	 * Assign the mmff94 atom type to a given atom.
	 * Before this method can be called the following has to be done:
	 * atomContainer=(AtomContainer)atomTypeTools.assignAtomTypePropertiesToAtom(new Molecule(atomContainer));
	 *
	 * 
	 * @param  atomContainer   AtomContainer
	 * @param  atom            the target atom
	 * @exception CDKException Description of the Exception
     * @return                 the matching AtomType (AtomType class)
	 */
	public AtomType findMatchingAtomType(AtomContainer atomContainer, Atom atom) throws CDKException {
		//System.out.println("****** Configure MMFF94 AtomType via findMatching ******");
		System.out.print(" Symbol:" + atom.getSymbol() +" HoseCode>" + atom.getSphericalMatcher() + " ");
		logger.debug(" Symbol:" + atom.getSymbol() +" HoseCode>" + atom.getSphericalMatcher() + " ");
		try {
			factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mmff94_atomtypes.xml");
		} catch (Exception ex1) {
            logger.error(ex1.getMessage());
			logger.debug(ex1);
		}
					
		if (atom instanceof PseudoAtom) {
				return factory.getAtomTypes("DU")[0];
		}
		Pattern p1 = null;
		Pattern p2 = null;
		String ID = "";
		boolean atomTypeFlag = false;
		Matcher mat1=null;
		Matcher mat2=null;
		double tmpMaxBondOrder = 0;
		maxBondOrder = atomContainer.getMaximumBondOrder(atom);
		for (int j = 0; j < atomTypeIds.length; j++){
        	tmpMaxBondOrder = factory.getAtomType(atomTypeIds[j]).getMaxBondOrder();
			logger.debug(j + "ATOM TYPE "+ tmpMaxBondOrder + " " +factory.getAtomType(atomTypeIds[j]).getSphericalMatcher());
			p1 =Pattern.compile(factory.getAtomType(atomTypeIds[j]).getSphericalMatcher());
			mat1 = p1.matcher(atom.getSphericalMatcher());
			if (mat1.matches()) {
				ID = atomTypeIds[j];
				if (atomTypeIds[j].equals("C")) {
					if (atom.getChemicalGroupConstant()!=-1) {//in Ring
						if (maxBondOrder == 1){
							if (atom.getRingSize() == 3) {
								ID = atomTypeIds[9];//sp3 3mem rings
							}else if (atom.getRingSize() == 4) {
								ID = atomTypeIds[8];//sp3 4mem rings
							}
						}else{//sp2
							p1 =Pattern.compile(factory.getAtomType(atomTypeIds[13]).getSphericalMatcher());//C5B
							mat1 = p1.matcher(atom.getSphericalMatcher());
							p2 =Pattern.compile(factory.getAtomType(atomTypeIds[12]).getSphericalMatcher());//C5A
							mat2 = p2.matcher(atom.getSphericalMatcher());
							if (mat1.matches() && atom.getChemicalGroupConstant()%2==0 && atom.getFlag(CDKConstants.ISAROMATIC) && atom.getRingSize()==5){
								ID = atomTypeIds[13];
							}else if (mat2.matches() && atom.getChemicalGroupConstant()%2==0 && atom.getFlag(CDKConstants.ISAROMATIC) && atom.getRingSize()==5){
								ID = atomTypeIds[12];
							}else if (atom.getChemicalGroupConstant()%2==0 && atom.getFlag(CDKConstants.ISAROMATIC) && atom.getRingSize()==5) {
								ID = atomTypeIds[14];//C5 in het 5 ring
							}else if (atom.getFlag(CDKConstants.ISAROMATIC)) {
								ID = atomTypeIds[11];//Car in benzene, pyroll
							}
						}
						
					}else{//not in Ring
						p1 = Pattern.compile(factory.getAtomType(atomTypeIds[66]).getSphericalMatcher());//S=C
						mat1 = p1.matcher(atom.getSphericalMatcher());
						if (mat1.matches()){
							ID = atomTypeIds[66];//S=C
						}
					}
					
				} else if (atomTypeIds[j].equals("Csp2")) {
					if (atom.getChemicalGroupConstant()%2==0 & atom.getRingSize()==4 & !atom.getFlag(CDKConstants.ISAROMATIC)) {
						ID = atomTypeIds[10];//CE4R					
					}
					
				} else if (atomTypeIds[j].equals("C=")) {
					if (atom.getChemicalGroupConstant()%2==0 && atom.getFlag(CDKConstants.ISAROMATIC)) {
						ID = atomTypeIds[12];//C5A
					}
				
				} else if (atomTypeIds[j].equals("N")) {
					//Amid
					p1 = Pattern.compile(factory.getAtomType(atomTypeIds[48]).getSphericalMatcher());//NC=0
					mat1 = p1.matcher(atom.getSphericalMatcher());
					if (mat1.matches() & atom.getChemicalGroupConstant()==-1) {
						ID = atomTypeIds[48];//NC=O
					}
					//nsp3 oxide
					p1 = Pattern.compile(factory.getAtomType(atomTypeIds[44]).getSphericalMatcher());//sp3 n-oxide
					mat1 = p1.matcher(atom.getSphericalMatcher());
					if (mat1.matches() && maxBondOrder==tmpMaxBondOrder){
						ID = atomTypeIds[44];
					}
					//ring sytems
					p1 = Pattern.compile(factory.getAtomType(atomTypeIds[56]).getSphericalMatcher());
					mat1 = p1.matcher(atom.getSphericalMatcher());
					
					if (atom.getChemicalGroupConstant()==10){
						ID = atomTypeIds[56];						
					}else if (atom.getChemicalGroupConstant()==4){
						ID = atomTypeIds[57];
					}else if (atom.getChemicalGroupConstant()%2==0 & atom.getRingSize()==5 & atom.getFlag(CDKConstants.ISAROMATIC)){
						ID=atomTypeIds[64];
					}
					//Nsp2-Oxides
					p1 = Pattern.compile(factory.getAtomType(atomTypeIds[61]).getSphericalMatcher());//npox
					mat1 = p1.matcher(atom.getSphericalMatcher());
					
					if (mat1.matches() && maxBondOrder==tmpMaxBondOrder){
						ID=atomTypeIds[43];
					}
					if (atom.getFlag(CDKConstants.ISAROMATIC)){
						if(mat1.matches()&& atom.getChemicalGroupConstant()==12){
							ID = atomTypeIds[61];
						}else if(mat1.matches()&& atom.getRingSize()==5){
							ID = atomTypeIds[62];
						}
					}
					//NC#N
					p1 = Pattern.compile(factory.getAtomType(atomTypeIds[45]).getSphericalMatcher());
					mat1 = p1.matcher(atom.getSphericalMatcher());
					if (mat1.matches()){
						ID = atomTypeIds[45];
					}
					
				}else if (atomTypeIds[j].equals("N=C")) {
					//n beta heteroaromatic ring
					p1 = Pattern.compile(factory.getAtomType(atomTypeIds[59]).getSphericalMatcher());
					mat1 = p1.matcher(atom.getSphericalMatcher());
					if (atom.getChemicalGroupConstant()!=-1) {
						if (mat1.matches() && atom.getChemicalGroupConstant()%2==0 && atom.getFlag(CDKConstants.ISAROMATIC) && atom.getRingSize()==5){
							ID = atomTypeIds[59];//N5A
						}else if(atom.getChemicalGroupConstant()==10){//NPYD
							ID = atomTypeIds[56];
						}else if(atom.getChemicalGroupConstant()==4){//NPYL
							ID = atomTypeIds[57];
						}
					}
					//N2OX
					p1 = Pattern.compile(factory.getAtomType(atomTypeIds[43]).getSphericalMatcher());
					mat1 = p1.matcher(atom.getSphericalMatcher());
					if (mat1.matches()){
						if (atom.getChemicalGroupConstant()==10){
							ID = atomTypeIds[61];//npox
						}else if (atom.getFlag(CDKConstants.ISAROMATIC) && atom.getRingSize()==5){
							ID = atomTypeIds[62];//n5ox
						}else {
							ID = atomTypeIds[43];//n2ox
						}
					}
					
				}else if (atomTypeIds[j].equals("N2OX")){
					//NO3
					p1 = Pattern.compile(factory.getAtomType(atomTypeIds[46]).getSphericalMatcher());
					mat1 = p1.matcher(atom.getSphericalMatcher());
					if (mat1.matches() && atom.getChemicalGroupConstant()==-1){
						ID = atomTypeIds[46];//NO3
					}
					if (atom.getChemicalGroupConstant()==12){
						ID = atomTypeIds[61];//NPOX
					}else if (atom.getChemicalGroupConstant()!=-1 && atom.getFlag(CDKConstants.ISAROMATIC) && atom.getRingSize()==5){
						ID = atomTypeIds[62];//N5OX
					}
				
				}else if (atomTypeIds[j].equals("=N=") || atomTypeIds[j].equals("NAZT")){
					if (atom.getChemicalGroupConstant()!=-1 && atom.getFlag(CDKConstants.ISAROMATIC)
						&& atom.getRingSize()==5){
						ID = atomTypeIds[59];//aromatic N5A
					}
					
				}else if (atomTypeIds[j].equals("N+=")){ 
					if (atom.getChemicalGroupConstant()!=-1 && atom.getFlag(CDKConstants.ISAROMATIC)
						&& atom.getRingSize()==5){
						ID = atomTypeIds[63];//n5+
					}else if (atom.getChemicalGroupConstant()==12){
						ID = atomTypeIds[58];//npd+
					}
				
								
					
				}else if (atomTypeIds[j].equals("O")){
					if (atom.getChemicalGroupConstant()==6){
							ID = atomTypeIds[35];//Oar
					}
					
				}else if (atomTypeIds[j].equals("HO")){
					p1 = Pattern.compile(factory.getAtomType(atomTypeIds[21]).getSphericalMatcher());
					mat1 = p1.matcher(atom.getSphericalMatcher());
					if (mat1.matches()){
						ID = atomTypeIds[21];//HOCC
					}
					p1 = Pattern.compile(factory.getAtomType(atomTypeIds[18]).getSphericalMatcher());
					mat1 = p1.matcher(atom.getSphericalMatcher());
					if (mat1.matches()){
						ID = atomTypeIds[18];//HOCO					
					}
										
				}else if (atomTypeIds[j].equals("P")){
					p1 = Pattern.compile(factory.getAtomType(atomTypeIds[75]).getSphericalMatcher());
					mat1 = p1.matcher(atom.getSphericalMatcher());
					if (mat1.matches()){
						ID = atomTypeIds[75];//-P=C
					}
				}else if (atomTypeIds[j].equals("S")){
					if (atom.getRingSize()==5 && atom.getFlag(CDKConstants.ISAROMATIC)){
						ID = atomTypeIds[72];//Sthiophen
					}
				}else if (atomTypeIds[j].equals("HC")){
					p1 = Pattern.compile(factory.getAtomType("HP").getSphericalMatcher());
					mat1 = p1.matcher(atom.getSphericalMatcher());
					if (mat1.matches()){
						ID = "HP";
					}
				}
				
				atomTypeFlag = true;
				System.out.println(" MATCH AtomTypeID:"+j+ " " + ID);
				logger.debug(" MATCH AtomTypeID:"+j+ " " + ID);
				break;
			}//IF
		}//for end
		if (atomTypeFlag) {
			atomTypeFlag = false;
			return factory.getAtomType(ID);
		} else {
			//System.out.println("NoSuchAtomTypeException: Atom is unkown with Symbol:" + atom.getSymbol() + " does not MATCH AtomType. HoseCode:" + atom.getSphericalMatcher());
			return factory.getAtomType("DU");
		}
	}
}

