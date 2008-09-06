/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
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
package org.openscience.cdk.atomtype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.AtomTypeTools;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Class implements methods to assign mmff94 atom types for a specific atom in
 * an molecule. The full list of mmff94 atom types is defined in the file
 * <b>cdk/config/data/mmff94_atomtypes.xml</b>.
 *
 * @author         cho
 * @cdk.created    2005-18-07
 * @cdk.module     extra
 * @cdk.svnrev  $Revision$
 */
public class MMFF94AtomTypeMatcher implements IAtomTypeMatcher {

	private LoggingTool logger;

	IBond.Order maxBondOrder = IBond.Order.SINGLE;
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

	private String getSphericalMatcher(IAtomType type) throws CDKException {//NOPMD
		return (String)type.getProperty(CDKConstants.SPHERICAL_MATCHER);
	}

	private String getSphericalMatcher(String type) throws CDKException {//NOPMD
		return getSphericalMatcher(factory.getAtomType(type));
	}

	@TestMethod("testFindMatchingAtomType_IAtomContainer")
  public IAtomType[] findMatchingAtomType(IAtomContainer atomContainer) throws CDKException {
      IAtomType[] types = new IAtomType[atomContainer.getAtomCount()];
      int typeCounter = 0;
      for (IAtom atom : atomContainer.atoms()) {
          types[typeCounter] = findMatchingAtomType(atomContainer, atom);
          typeCounter++;
      }
      return types;
  }

	/**
	 * Assign the mmff94 atom type to a given atom.
	 * Before this method can be called the following has to be done:
	 * atomContainer=(AtomContainer)atomTypeTools.assignAtomTypePropertiesToAtom(new Molecule(atomContainer));
	 *
	 * @param  atomContainer   AtomContainer
	 * @param  atomInterface   the target atom
	 * @exception CDKException Description of the Exception
     * @return                 the matching AtomType (AtomType class)
	 */
	public IAtomType findMatchingAtomType(IAtomContainer atomContainer, IAtom atomInterface) throws CDKException {
        if (factory == null) {
		try {
			factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mmff94_atomtypes.xml",
                atomContainer.getBuilder()
            );
		} catch (Exception ex1) {
            logger.error(ex1.getMessage());
			logger.debug(ex1);
                throw new CDKException("Could not instantiate the AtomType list!", ex1);
		}
        }

		org.openscience.cdk.Atom atom = (org.openscience.cdk.Atom)atomInterface;
		//logger.debug("****** Configure MMFF94 AtomType via findMatching ******");
		//logger.debug(" Symbol:" + atom.getSymbol() +" HoseCode>" + atom.getSphericalMatcher() + " ");
		logger.debug(" Symbol:" + atom.getSymbol() +" HoseCode>" + atom.getProperty(CDKConstants.SPHERICAL_MATCHER) + " ");
		//System.out.print("IN MMFF94AtomTypeMatcher Symbol:" + atom.getSymbol() +" HoseCode>" + atom.getProperty(CDKConstants.SPHERICAL_MATCHER) + " ");
				
		if (atom instanceof PseudoAtom) {
				return factory.getAtomTypes("DU")[0];
		}
		Pattern p1 = null;
		Pattern p2 = null;
		String ID = "";
		boolean atomTypeFlag = false;
		Matcher mat1=null;
		Matcher mat2=null;
		IBond.Order tmpMaxBondOrder;
		maxBondOrder = atomContainer.getMaximumBondOrder(atom);
		for (int j = 0; j < atomTypeIds.length; j++){
        	tmpMaxBondOrder = factory.getAtomType(atomTypeIds[j]).getMaxBondOrder();
            String atomSphericalMatcher = (String)factory.getAtomType(atomTypeIds[j]).getProperty(CDKConstants.SPHERICAL_MATCHER);
			logger.debug(j + " ATOM TYPE "+ tmpMaxBondOrder + " " +atomSphericalMatcher);
			p1 =Pattern.compile(atomSphericalMatcher);
			mat1 = p1.matcher((String)atom.getProperty(CDKConstants.SPHERICAL_MATCHER));
			if (mat1.matches()) {
				ID = atomTypeIds[j];
				Object property = atom.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT);
	        		int atomChemGroupConstant = (Integer) property;
				Object ringSize = atom.getProperty(CDKConstants.PART_OF_RING_OF_SIZE);
				int atomRingSize = -1;
				if (ringSize != null) {
					atomRingSize = (Integer) ringSize;
				}
				if (atomTypeIds[j].equals("C")) {
					if (atomChemGroupConstant != -1) {//in Ring
						if (ringSize != null && maxBondOrder == IBond.Order.SINGLE){
							if (atomRingSize == 3) {
								ID = atomTypeIds[9];//sp3 3mem rings
							}else if (atomRingSize == 4) {
								ID = atomTypeIds[8];//sp3 4mem rings
							}
						}else{//sp2
							String type13Matcher = getSphericalMatcher(atomTypeIds[13]);
							p1 = Pattern.compile(type13Matcher);//C5B
							mat1 = p1.matcher(atomSphericalMatcher);
							String type12Matcher = getSphericalMatcher(atomTypeIds[12]);
							p2 =Pattern.compile(type12Matcher);//C5A
							mat2 = p2.matcher(atomSphericalMatcher);
							if (mat1.matches() && atomChemGroupConstant%2==0 && atom.getFlag(CDKConstants.ISAROMATIC) && atomRingSize==5){
								ID = atomTypeIds[13];
							}else if (mat2.matches() && atomChemGroupConstant%2==0 && atom.getFlag(CDKConstants.ISAROMATIC) && atomRingSize==5){
								ID = atomTypeIds[12];
							}else if (atomChemGroupConstant%2==0 && atom.getFlag(CDKConstants.ISAROMATIC) && atomRingSize==5) {
								ID = atomTypeIds[14];//C5 in het 5 ring
							}else if (atom.getFlag(CDKConstants.ISAROMATIC)) {
								ID = atomTypeIds[11];//Car in benzene, pyroll
							}
						}
						
					}else{//not in Ring
						p1 = Pattern.compile(getSphericalMatcher(atomTypeIds[66]));//S=C
						mat1 = p1.matcher(atomSphericalMatcher);
						if (mat1.matches()){
							ID = atomTypeIds[66];//S=C
						}
					}
					
				} else if (atomTypeIds[j].equals("Csp2")) {
					if (atomChemGroupConstant%2==0 & atomRingSize==4 & !atom.getFlag(CDKConstants.ISAROMATIC)) {
						ID = atomTypeIds[10];//CE4R					
					}
					
				} else if (atomTypeIds[j].equals("C=")) {
					if (atomChemGroupConstant%2==0 && atom.getFlag(CDKConstants.ISAROMATIC)) {
						ID = atomTypeIds[12];//C5A
					}
				
				} else if (atomTypeIds[j].equals("N")) {
					//Amid
					p1 = Pattern.compile(getSphericalMatcher(atomTypeIds[48]));//NC=0
					mat1 = p1.matcher(atomSphericalMatcher);
					if (mat1.matches() & atomChemGroupConstant==-1) {
						ID = atomTypeIds[48];//NC=O
					}
					//nsp3 oxide
					p1 = Pattern.compile(getSphericalMatcher(atomTypeIds[44]));//sp3 n-oxide
					mat1 = p1.matcher(atomSphericalMatcher);
					if (mat1.matches() && maxBondOrder==tmpMaxBondOrder){
						ID = atomTypeIds[44];
					}
					//ring sytems
					p1 = Pattern.compile(getSphericalMatcher(atomTypeIds[56]));
					mat1 = p1.matcher(atomSphericalMatcher);
					
					if (atomChemGroupConstant==10){
						ID = atomTypeIds[56];						
					}else if (atomChemGroupConstant==4){
						ID = atomTypeIds[57];
					}else if (atomChemGroupConstant%2==0 & atomRingSize==5 & atom.getFlag(CDKConstants.ISAROMATIC)){
						ID=atomTypeIds[64];
					}
					//Nsp2-Oxides
					p1 = Pattern.compile(getSphericalMatcher(atomTypeIds[61]));//npox
					mat1 = p1.matcher(atomSphericalMatcher);
					
					if (mat1.matches() && maxBondOrder==tmpMaxBondOrder){
						ID=atomTypeIds[43];
					}
					if (atom.getFlag(CDKConstants.ISAROMATIC)){
						if(mat1.matches()&& atomChemGroupConstant==12){
							ID = atomTypeIds[61];
						}else if(mat1.matches()&& atomRingSize==5){
							ID = atomTypeIds[62];
						}
					}
					//NC#N
					p1 = Pattern.compile(getSphericalMatcher(atomTypeIds[45]));
					mat1 = p1.matcher(getSphericalMatcher(atom));
					if (mat1.matches()){
						ID = atomTypeIds[45];
					}
					
				}else if (atomTypeIds[j].equals("N=C")) {
					//n beta heteroaromatic ring
					p1 = Pattern.compile(getSphericalMatcher(atomTypeIds[59]));
					mat1 = p1.matcher(getSphericalMatcher(atom));
					if (atomChemGroupConstant!=-1) {
						if (mat1.matches() && atomChemGroupConstant%2==0 && 
								atom.getFlag(CDKConstants.ISAROMATIC) && atomRingSize==5){
							ID = atomTypeIds[59];//N5A
						}else if(atomChemGroupConstant==10){//NPYD
							ID = atomTypeIds[56];
						}else if(atomChemGroupConstant==4){//NPYL
							ID = atomTypeIds[57];
						}
					}
					//N2OX
					p1 = Pattern.compile(getSphericalMatcher(atomTypeIds[43]));
					mat1 = p1.matcher(getSphericalMatcher(atom));
					if (mat1.matches()){
						if (atomChemGroupConstant==10){
							ID = atomTypeIds[61];//npox
						}else if (atom.getFlag(CDKConstants.ISAROMATIC) && atomRingSize==5){
							ID = atomTypeIds[62];//n5ox
						}else {
							ID = atomTypeIds[43];//n2ox
						}
					}
					
				}else if (atomTypeIds[j].equals("N2OX")){
					//NO3
					p1 = Pattern.compile(getSphericalMatcher(atomTypeIds[46]));
					mat1 = p1.matcher(getSphericalMatcher(atom));
					if (mat1.matches() && atomChemGroupConstant==-1){
						ID = atomTypeIds[46];//NO3
					}
					if (atomChemGroupConstant==12){
						ID = atomTypeIds[61];//NPOX
					}else if (atomChemGroupConstant!=-1 && atom.getFlag(CDKConstants.ISAROMATIC) && atomRingSize==5){
						ID = atomTypeIds[62];//N5OX
					}
				
				}else if (atomTypeIds[j].equals("=N=") || atomTypeIds[j].equals("NAZT")){
					if (atomChemGroupConstant!=-1 && atom.getFlag(CDKConstants.ISAROMATIC)
						&& atomRingSize==5){
						ID = atomTypeIds[59];//aromatic N5A
					}
					
				}else if (atomTypeIds[j].equals("N+=")){ 
					if (atomChemGroupConstant!=-1 && atom.getFlag(CDKConstants.ISAROMATIC)
						&& atomRingSize==5){
						ID = atomTypeIds[63];//n5+
					}else if (atomChemGroupConstant==12){
						ID = atomTypeIds[58];//npd+
					}
				
				}else if (atomTypeIds[j].equals("O")){
					if (atomChemGroupConstant==6){
							ID = atomTypeIds[35];//Oar
					}
					
				}else if (atomTypeIds[j].equals("HO")){
					p1 = Pattern.compile(getSphericalMatcher(atomTypeIds[21]));
					mat1 = p1.matcher(atomSphericalMatcher);
					if (mat1.matches()){
						ID = atomTypeIds[21];//HOCC
					}
					p1 = Pattern.compile(getSphericalMatcher(atomTypeIds[18]));
					mat1 = p1.matcher(atomSphericalMatcher);
					if (mat1.matches()){
						ID = atomTypeIds[18];//HOCO					
					}
										
				}else if (atomTypeIds[j].equals("P")){
					p1 = Pattern.compile(getSphericalMatcher(atomTypeIds[75]));
					mat1 = p1.matcher(atomSphericalMatcher);
					if (mat1.matches()){
						ID = atomTypeIds[75];//-P=C
					}
				}else if (atomTypeIds[j].equals("S")){
					if (atomRingSize==5 && atom.getFlag(CDKConstants.ISAROMATIC)){
						ID = atomTypeIds[72];//Sthiophen
					}
				}else if (atomTypeIds[j].equals("HC")){
					p1 =Pattern.compile(getSphericalMatcher("HP"));
					mat1 = p1.matcher((String)atom.getProperty(CDKConstants.SPHERICAL_MATCHER));
					if (mat1.matches()){
						ID = "HP";
					}
				}
				
				atomTypeFlag = true;
				logger.debug(" MATCH AtomTypeID:"+j+ " " + ID);
				break;
			}//IF
		}//for end
		if (atomTypeFlag) {
			atomTypeFlag = false;
			//System.out.println(" "+ID);
			return factory.getAtomType(ID);
		} else {
			return factory.getAtomType("DU");
		}
	}
}

