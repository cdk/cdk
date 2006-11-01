/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2006  Christian Hoppe <chhoppe@users.sf.net>
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.AtomTypeTools;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Class implements methods to assign mmff94 atom types for a specific atom in an molecule. 
 *
 * @author         cho
 * @cdk.created    2005-18-07
 * @cdk.module     experimental
 */
public class MM2AtomTypeMatcher implements IAtomTypeMatcher {

	private LoggingTool logger;

	double maxBondOrder = 0;
	private AtomTypeFactory factory = null;
	AtomTypeTools atomTypeTools=null;
	
	String [] atomTypeIds={"C","Csp2","CdoubleBonded","Csp","HC","O","OdoubleBonded","N","Nsp2","Nsp",
			"F","CL","BR","I","S","Splus","SNbridged","SO2","Sthi","SI","LP","HO",
			"CR3R","HN","HOCO","P","B","BTET","HN2","C.","Cplus","GE",
			"SN","PB","SE","TE","D","NsingleDoubleBonded","CE3R","Nplus","NPYL","Oar",
			"Sthi","N2OX","HS","NdoubleDoubleBonded","NO2","OM","HNplus","OR","Car","HE",
			"NE","AR","KR","XE","MGplus2","PTET","FEplus2","FEplus3","NIplus2","NIplus3","COplus2","COplus3",
			"OX","OK","Cplusplus","NdoubleBondedC","NPDplus","NpositiveDoubleBonded","N2OX"
	};

	/**
	 * Constructor for the MMFF94AtomTypeMatcher object.
	 */
	public MM2AtomTypeMatcher() {
		logger = new LoggingTool(this);
		atomTypeTools=new AtomTypeTools();
	}

	private String getSphericalMatcher(IAtomType type) throws CDKException {//NOPMD
		return (String)type.getProperty(CDKConstants.SPHERICAL_MATCHER);
	}

	private String getSphericalMatcher(String type) throws CDKException {//NOPMD
		return getSphericalMatcher(factory.getAtomType(type));
	}

	/**
	 * Assign the mm2 atom type to a given atom.
	 * Before this method can be called the following has to be done:
	 * <pre>
	 * atomContainer = (AtomContainer)atomTypeTools.assignAtomTypePropertiesToAtom(
	 *   new Molecule(atomContainer)
	 * );
	 * </pre>
	 * 
	 * @param  atomContainer   AtomContainer
	 * @param  atomInterface   the target atom
	 * @exception CDKException Description of the Exception
     * @return                 the matching AtomType (AtomType class)
	 */
	public IAtomType findMatchingAtomType(IAtomContainer atomContainer, IAtom atomInterface) throws CDKException {
        if (factory == null) {
        	try {
        		factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mm2_atomtypes.xml",
        				atomContainer.getBuilder());
        	} catch (Exception ex1) {
        		logger.error("Could not instantiate the AtomType list!", ex1.getMessage());
        		logger.debug(ex1);
        		throw new CDKException("Could not instantiate the AtomType list!", ex1);
        	}
        }

		org.openscience.cdk.Atom atom = (org.openscience.cdk.Atom)atomInterface;
		logger.debug("****** Configure MM2 AtomType via findMatching ******");
		String atomSphericalMatcher = (String)atom.getProperty(CDKConstants.SPHERICAL_MATCHER);
		int atomChemicalGroupConstant = ((Integer)atom.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT)).intValue();
		int atomRingSize = 0; // not all atom types have ring sizes define; 0 is default 
	    Object oRingSize = atom.getProperty(CDKConstants.PART_OF_RING_OF_SIZE);
	    if (oRingSize != null) {
	    	atomRingSize = ((Integer)oRingSize).intValue();
	    }
		logger.debug(" Symbol:" + atom.getSymbol() +" HoseCode>" + atomSphericalMatcher + " ");
					
		if (atom instanceof PseudoAtom) {
				return factory.getAtomTypes("DU")[0];
		}
		
		Pattern p1 = null;
		String ID = "";
		boolean atomTypeFlag = false;
		Matcher mat1=null;
		double tmpMaxBondOrder = 0;
		maxBondOrder = atomContainer.getMaximumBondOrder(atom);
		logger.debug("Atom maxBond"+maxBondOrder+" ChemicalGroupConstant "+atomChemicalGroupConstant);
		for (int j = 0; j < atomTypeIds.length; j++){
			tmpMaxBondOrder = factory.getAtomType(atomTypeIds[j]).getMaxBondOrder();
			logger.debug(j + "ATOM TYPE "+ tmpMaxBondOrder + " " +getSphericalMatcher(atomTypeIds[j]));
			p1 =Pattern.compile(getSphericalMatcher(atomTypeIds[j]));
			mat1 = p1.matcher(atomSphericalMatcher);
			if (mat1.matches()) {
				ID = atomTypeIds[j];
				if (atomTypeIds[j].equals("C")) {
					if (atomChemicalGroupConstant!=-1) {
						if (atomRingSize==3) {
							ID="CR3R";
						}else if (atomChemicalGroupConstant==5){
							ID="Car";	
						}else if (maxBondOrder>1) {
							ID="Csp2";
						}
					}
										
					if (atom.getSymbol().equals("S")){
						if (atomChemicalGroupConstant==8){
							ID="Sthi";
						}else{						
							p1 = Pattern.compile(getSphericalMatcher("S"));
							mat1 = p1.matcher(atomSphericalMatcher);
							if (mat1.matches()) {
								ID="S";
							}
						}
					}
					
				} else if (atomTypeIds[j].equals("Csp2")) {
					if (atomChemicalGroupConstant!=-1) {
						if (atomChemicalGroupConstant==5) {
							ID="Car";
						}else if (atomRingSize==3) {
							ID="CE3R";
						}
					}
					p1 = Pattern.compile(getSphericalMatcher("CdoubleBonded"));
					mat1 = p1.matcher(atomSphericalMatcher);
					if (mat1.matches()) {
						ID="CdoubleBonded";
					}
				} else if (atomTypeIds[j].equals("O")) {
					//OH/Ether
					if (atomChemicalGroupConstant!=-1) {
						if (atomChemicalGroupConstant==6){
							ID="Oar";//furan
						}else if (atomRingSize==3) {
							ID="OR";//epoxy
						}
					}
					p1 = Pattern.compile(getSphericalMatcher("OX"));
					mat1 = p1.matcher(atomSphericalMatcher);
					if (mat1.matches() & atomChemicalGroupConstant==-1) {
						ID="OX";
					}
					
				} else if (atomTypeIds[j].equals("N")) {//n sp3
					if (atomContainer.getMaximumBondOrder(atom)>1 & atomContainer.getMaximumBondOrder(atom)<3){
						ID="Nsp2";
					}
					
					if (atomChemicalGroupConstant==4) {
						ID="NPYL";//Pyrole
					}else if (atomChemicalGroupConstant==10){
						ID="NsingleDoubleBonded";
						p1 = Pattern.compile(getSphericalMatcher("NPDplus"));
						mat1 = p1.matcher(atomSphericalMatcher);
						if (mat1.matches()) {
							ID="NPDplus";
						}
					}else{
						//Amid
						p1 = Pattern.compile(getSphericalMatcher("Namid"));
						mat1 = p1.matcher(atomSphericalMatcher);
						if (mat1.matches() & atomChemicalGroupConstant==-1) {
							ID="Nsp2";
						}else{
							p1 = Pattern.compile(getSphericalMatcher("N2OX"));
							mat1 = p1.matcher(atomSphericalMatcher);	
							if (mat1.matches() & atomChemicalGroupConstant==-1) {
								ID="N2OX";
							}
						}
					}
					
					
				} else if (atomTypeIds[j].equals("Nsp2")) {
					if (atomChemicalGroupConstant==12) {
							ID="=N-";//Pyridin
					}
					//Azo
					p1 = Pattern.compile(getSphericalMatcher("NsingleDoubleBonded"));
					mat1 = p1.matcher(atomSphericalMatcher);
					if (mat1.matches() & atomChemicalGroupConstant==-1) {
						ID="NsingleDoubleBonded";
					}else{
						p1 = Pattern.compile(getSphericalMatcher("NdoubleBondedC"));
						mat1 = p1.matcher(atomSphericalMatcher);	
						if (mat1.matches()) {
							ID="NdoubleBondedC";
						}
						
						p1 = Pattern.compile(getSphericalMatcher("N2OX"));
						mat1 = p1.matcher(atomSphericalMatcher);	
						if (mat1.matches()) {
							ID="N2OX";
						}
						
						p1 = Pattern.compile(getSphericalMatcher("NO2"));
						mat1 = p1.matcher(atomSphericalMatcher);	
						if (mat1.matches()) {
							ID="NO2";
						}
						
						p1 = Pattern.compile(getSphericalMatcher("NdoubleDoubleBonded"));
						mat1 = p1.matcher(atomSphericalMatcher);	
						if (mat1.matches()) {
							ID="NdoubleDoubleBonded";
						}
						
					}
					
				} else if (atomTypeIds[j].equals("HS")) {
					if (atom.getMaxBondOrder() > 1) {
						ID="HC";
					}
				} else if (atomTypeIds[j].equals("HO")) {
					//Enol,amid
					p1 = Pattern.compile(getSphericalMatcher("HOC"));
					mat1 = p1.matcher(atomSphericalMatcher);
					if (mat1.matches() & atomChemicalGroupConstant==-1) {
						ID="HN2";
					}
					//COOH
					p1 = Pattern.compile(getSphericalMatcher("HOCO"));
					mat1 = p1.matcher(atomSphericalMatcher);
					if (mat1.matches() & atomChemicalGroupConstant==-1) {
						ID="HOCO";
					}
				} else if (atomTypeIds[j].equals("HN")) {
					
					p1 = Pattern.compile(getSphericalMatcher("HN2"));
					mat1 = p1.matcher(atomSphericalMatcher);
					if (mat1.matches()) {
						ID="HN2";
					}
				} 
				atomTypeFlag = true;
				logger.debug(" MATCH AtomTypeID:"+j+ " " + ID);
				break;
			}//IF
		}//for end
		if (atomTypeFlag) {
			atomTypeFlag = false;
			logger.debug("ID in factory true:"+ID);
			return factory.getAtomType(ID);
		} else {
			logger.debug("NoSuchAtomTypeException: Atom is unkown with Symbol:" + atom.getSymbol() + " does not MATCH AtomType. HoseCode:" + atomSphericalMatcher);
			logger.debug("ID in factory false:"+ID);
			return factory.getAtomType("DU");
		}
	}
	
}

