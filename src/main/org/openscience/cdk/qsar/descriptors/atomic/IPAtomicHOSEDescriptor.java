/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.HOSECodeGenerator;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 *  This class returns the ionization potential of an atom containg lone 
 *  pair electrons. It is
 *  based on a decision tree which is extracted from Weka(J48) from 
 *  experimental values. Up to now is only possible predict for 
 *  Cl,Br,I,N,P,O,S Atoms and they are not belong to
 *  conjugated system or not adjacent to an double bond.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td></td>
 *     <td></td>
 *     <td>no parameters</td>
 *   </tr>
 * </table>
 *
 * @author       Miguel Rojas
 * @cdk.created  2006-05-26
 * @cdk.module   qsaratomic
 * @cdk.svnrev   $Revision$
 * @cdk.set      qsar-descriptors
 * @cdk.dictref  qsar-descriptors:ionizationPotential
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.IPAtomicHOSEDescriptorTest")
public class IPAtomicHOSEDescriptor extends AbstractAtomicDescriptor {

    private static final String[] descriptorNames = {"ipAtomicHOSE"};
    
	/** Maximum spheres to use by the HoseCode model.*/
	int maxSpheresToUse = 10;
	
	private IPdb db = new IPdb();
	
	/**
	 *  Constructor for the IPAtomicHOSEDescriptor object.
	 */
	public IPAtomicHOSEDescriptor() {
	}
	/**
	 *  Gets the specification attribute of the IPAtomicHOSEDescriptor object
	 *
	 *@return    The specification value
	 */
	@TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ionizationPotential",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}
    /**
     * This descriptor does have any parameter.
     */
    @TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
    }


    /**
     *  Gets the parameters attribute of the IPAtomicHOSEDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    @TestMethod(value="testGetParameters")
    public Object[] getParameters() {
        return null;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return descriptorNames;
    }


    /**
	 *  This method calculates the ionization potential of an atom.
	 *
	 *@param  atom          The IAtom to ionize.
	 *@param  container         Parameter is the IAtomContainer.
	 *@return                   The ionization potential. Not possible the ionization.
	 */
	@TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer container) {
        double value;
    	// FIXME: for now I'll cache a few modified atomic properties, and restore them at the end of this method
    	String originalAtomtypeName = atom.getAtomTypeName();
    	Integer originalNeighborCount = atom.getFormalNeighbourCount();
    	Integer originalValency = atom.getValency();
    	IAtomType.Hybridization originalHybridization = atom.getHybridization();

        if (!isCachedAtomContainer(container)) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
                LonePairElectronChecker lpcheck = new LonePairElectronChecker();
                lpcheck.saturate(container);
            } catch (CDKException e) {
                return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                        new DoubleResult(Double.NaN), descriptorNames, e);
            }

        }
		value = db.extractIP(container, atom);
    	// restore original props
    	atom.setAtomTypeName(originalAtomtypeName);
    	atom.setFormalNeighbourCount(originalNeighborCount);
    	atom.setValency(originalValency);
    	atom.setHybridization(originalHybridization);

		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(value),descriptorNames);
		
	}
	/**
	 * Looking if the Atom belongs to the halogen family.
	 * 
	 * @param  atom  The IAtom 
	 * @return       True, if it belongs
	 */
	private boolean familyHalogen(IAtom atom) {
		String symbol = atom.getSymbol();
        return symbol.equals("F") ||
                symbol.equals("Cl") ||
                symbol.equals("Br") ||
                symbol.equals("I");
	}
	 /**
     * Gets the parameterNames attribute of the IPAtomicHOSEDescriptor object.
     *
     * @return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return new String[0];
    }


    /**
     * Gets the parameterType attribute of the IPAtomicHOSEDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return null;
    }
    
    /**
     * Class defining the database containing the relation between the energy for ionizing and the HOSEcode 
     * fingerprints
     * 
     * @author Miguel Rojas
     *
     */
	private class IPdb {
		
		HashMap<String, HashMap<String,Double>> listGroup = new HashMap<String, HashMap<String,Double>>();
		HashMap<String, HashMap<String,Double>> listGroupS = new HashMap<String, HashMap<String,Double>>();
		/**
		 * The constructor of the IPdb.
		 * 
		 */
		public IPdb(){
			
		}
		
		/**
		 * extract from the db the ionization energy.
		 * 
		 * @param container  The IAtomContainer
		 * @param atom       The IAtom
		 * @return           The energy value
		 */
		public double extractIP(IAtomContainer container, IAtom atom) {
			// loading the files if they are not done
			String name = "";
			String nameS = "";
			HashMap<String,Double> hoseVSenergy = new HashMap<String,Double>();
			HashMap<String,Double> hoseVSenergyS = new HashMap<String,Double>();
			if(familyHalogen(atom)){
				name = "X_IP_HOSE.db";
				nameS = "X_IP_HOSE_S.db";
				if(listGroup.containsKey(name)){
					hoseVSenergy = listGroup.get(name);
					hoseVSenergyS = listGroupS.get(nameS);
				}else{
					String path = "org/openscience/cdk/qsar/descriptors/atomic/data/"+name;
					String pathS = "org/openscience/cdk/qsar/descriptors/atomic/data/"+nameS;
					InputStream ins = this.getClass().getClassLoader().getResourceAsStream(path);
					BufferedReader insr = new BufferedReader(new InputStreamReader(ins));
					hoseVSenergy = extractAttributes(insr);
					
					ins = this.getClass().getClassLoader().getResourceAsStream(pathS);
					insr = new BufferedReader(new InputStreamReader(ins));
					hoseVSenergyS = extractAttributes(insr);
				}
			} else return 0;
			
			try {
				HOSECodeGenerator hcg = new HOSECodeGenerator();
				//Check starting from the exact sphere hose code and maximal a value of 10 
				int exactSphere = 0;
				String hoseCode = "";
				for(int spheres = maxSpheresToUse; spheres > 0; spheres--){
					 hcg.getSpheres((Molecule) container, atom, spheres, true);
					 List<IAtom> atoms = hcg.getNodesInSphere(spheres);
					 if(atoms.size() != 0){
						 exactSphere = spheres;
						 hoseCode = hcg.getHOSECode(container, atom, spheres,true);
						 if(hoseVSenergy.containsKey(hoseCode)){
							  return hoseVSenergy.get(hoseCode);
						  }
						 if(hoseVSenergyS.containsKey(hoseCode)){
							  return hoseVSenergyS.get(hoseCode);
						  }
						 break;
					 }
				}
				//Check starting from the rings bigger and smaller
				//TODO:IP: Better application
				for(int i = 0; i < 3; i++) { // two rings
					for(int plusMinus = 0; plusMinus < 2; plusMinus++){ // plus==bigger, minus==smaller
						int sign = -1;
						if(plusMinus== 1)
							sign = 1;
							
						StringTokenizer st = new StringTokenizer(hoseCode, "()/");
						StringBuffer hoseCodeBuffer = new StringBuffer();
						  int sum = exactSphere+sign*(i+1);
						  for (int k = 0; k < sum; k++) {
						    if (st.hasMoreTokens()) {
						      String partcode = st.nextToken();
						      hoseCodeBuffer.append(partcode);
						    }
						    if (k == 0) {
						      hoseCodeBuffer.append("(");
						    } else if (k == 3) {
						      hoseCodeBuffer.append(")");
						    } else {
						      hoseCodeBuffer.append("/");
						    }
						  }
						  String hoseCodeBU = hoseCodeBuffer.toString();
						  
						  if(hoseVSenergyS.containsKey(hoseCodeBU)){
							  return hoseVSenergyS.get(hoseCodeBU);
						  }
					}
				}
			} catch (CDKException e) {
				e.printStackTrace();
			}
			return 0;
		}
		/** 
		 * Extract the Hose code and energy
		 * 
		 * @param input  The BufferedReader
		 * @return       HashMap with the Hose vs energy attributes
		 */
		private HashMap<String,Double> extractAttributes(BufferedReader input) {
			HashMap<String,Double> hoseVSenergy = new HashMap<String,Double>();
			String line;

			try {
				while ((line = input.readLine()) != null) {
					if(line.startsWith("#"))
						continue;
					List<String> values = extractInfo(line);
					if(values.get(1).equals(""))
						continue;
					hoseVSenergy.put(values.get(0), Double.valueOf(values.get(1)));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return hoseVSenergy;
		}
	}
	/**
	 * Extract the information from a line which contains HOSE_ID & energy.
	 * 
	 * @param str  String with the information
	 * @return     List with String = HOSECode and String = energy
	 */
	private static List<String> extractInfo(String str){
		
		StringBuffer idEdited = new StringBuffer();
		StringBuffer valEdited = new StringBuffer();
		
		int strlen = str.length();

		boolean foundSpace = false;
		int countSpace = 0;
		boolean foundDigit = false;
		for (int i = 0; i < strlen; i++) 
		{
			if(!foundDigit)
				if(Character.isLetter(str.charAt(i)))
					foundDigit = true;
			
			if(foundDigit){
				if (Character.isWhitespace(str.charAt(i))) 
				{
					if(countSpace == 0){
						foundSpace = true;
					}else
						break;
				}
				else 
				{
					if(foundSpace){
						valEdited.append(str.charAt(i));
					}
					else{
						idEdited.append(str.charAt(i));
					}
				}
			}
		}
		List<String> objec = new ArrayList<String>();
		objec.add(idEdited.toString());
		objec.add(valEdited.toString());
		return objec;
		
	}
}

