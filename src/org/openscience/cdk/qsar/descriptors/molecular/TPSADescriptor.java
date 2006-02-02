/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.qsar.descriptors.molecular;

import java.util.HashMap;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Ring;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Calculation of topological polar surface area based on fragment 
 * contributions (TPSA) {@cdk.cite ERTL2000}.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>useAromaticity</td>
 *     <td>false</td>
 *     <td>If true, it will check aromaticity</td>
 *   </tr>
 * </table>
 * <p>
 * This descriptor works properly with AtomContainers whose atoms contain either <b>explicit hydrogens</b> or
 * <b>implicit hydrogens</b>.
 *
 * @author      mfe4
 * @author ulif
 * @cdk.created 2004-11-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:tpsa
 */
public class TPSADescriptor implements IDescriptor {
	private boolean checkAromaticity = false;
	private static HashMap map;
	
	/**
	 *  Constructor for the TPSADescriptor object.
	 */
	public TPSADescriptor() { 
		if (map == null) {
			map = new HashMap();
		// contributions:
		// every contribution is given by an atom profile;
		// positions in atom profile strings are: symbol, max-bond-order, bond-order-sum,
		// number-of-neighbours, Hcount, formal charge, aromatic-bonds, is-in-3-membered-ring, 
		// single-bonds, double-bonds, triple-bonds.
		
      map.put("N+1.0+3.0+3+0+0+0+0+3+0+0", new Double(3.24)); // 1
      map.put("N+2.0+3.0+2+0+0+0+0+1+1+0", new Double(12.36)); // 2
      map.put("N+3.0+3.0+1+0+0+0+0+0+0+1", new Double(23.79)); // 3
      map.put("N+2.0+5.0+3+0+0+0+0+1+2+0", new Double(11.68));  // 4
      map.put("N+3.0+5.0+2+0+0+0+0+0+1+1", new Double(13.6)); // 5
      map.put("N+1.0+3.0+3+0+0+0+1+3+0+0", new Double(3.01)); // 6
      map.put("N+1.0+3.0+3+1+0+0+0+3+0+0", new Double(12.03));  // 7
      map.put("N+1.0+3.0+3+1+0+0+1+3+0+0", new Double(21.94)); // 8
      map.put("N+2.0+3.0+2+1+0+0+0+1+1+0", new Double(23.85));  //9
      map.put("N+1.0+3.0+3+2+0+0+0+3+0+0", new Double(26.02));  // 10
      map.put("N+1.0+4.0+4+0+1+0+0+4+0+0", new Double(0.0));  //11
      map.put("N+2.0+4.0+3+0+1+0+0+2+1+0", new Double(3.01));  //12
      map.put("N+3.0+4.0+2+0+1+0+0+1+0+1", new Double(4.36));  //13
      map.put("N+1.0+4.0+4+1+1+0+0+4+0+0", new Double(4.44));  //14
      map.put("N+2.0+4.0+3+1+1+0+0+2+1+0", new Double(13.97));  //15
      map.put("N+1.0+4.0+4+2+1+0+0+4+0+0", new Double(16.61));  //16
      map.put("N+2.0+4.0+3+2+1+0+0+2+1+0", new Double(25.59));  //17
      map.put("N+1.0+4.0+4+3+1+0+0+4+0+0", new Double(27.64));  //18
      map.put("N+1.5+3.0+2+0+0+2+0+0+0+0", new Double(12.89));  //19
      map.put("N+1.5+4.5+3+0+0+3+0+0+0+0", new Double(4.41));  //20
      map.put("N+1.5+4.0+3+0+0+2+0+1+0+0", new Double(4.93));  //21
      map.put("N+2.0+5.0+3+0+0+2+0+0+1+0", new Double(8.39));  //22
      map.put("N+1.5+4.0+3+1+0+2+0+1+0+0", new Double(15.79));  //23
      map.put("N+1.5+4.5+3+0+1+3+0+0+0+0", new Double(4.1));  //24
      map.put("N+1.5+4.0+3+0+1+2+0+1+0+0", new Double(3.88));  //25
      map.put("N+1.5+4.0+3+1+1+2+0+1+0+0", new Double(14.14));  //26
      
      map.put("O+1.0+2.0+2+0+0+0+0+2+0+0", new Double(9.23));  //27
      map.put("O+1.0+2.0+2+0+0+0+1+2+0+0", new Double(12.53));  //28
      map.put("O+2.0+2.0+1+0+0+0+0+0+1+0", new Double(17.07));  //29
      map.put("O+1.0+1.0+1+0+-1+0+0+1+0+0", new Double(23.06));  //30
      map.put("O+1.0+2.0+2+1+0+0+0+2+0+0", new Double(20.23));  //31
      map.put("O+1.5+3.0+2+0+0+2+0+0+0+0", new Double(13.14));  //32
      
      map.put("S+1.0+2.0+2+0+0+0+0+2+0+0", new Double(25.3));  //33
      map.put("S+2.0+2.0+1+0+0+0+0+0+1+0", new Double(32.09));  //34
      map.put("S+2.0+4.0+3+0+0+0+0+2+1+0", new Double(19.21));  //35
      map.put("S+2.0+6.0+4+0+0+0+0+2+2+0", new Double(8.38));  //36
      map.put("S+1.0+2.0+2+1+0+0+0+2+0+0", new Double(38.8));  //37
      map.put("S+1.5+3.0+2+0+0+2+0+0+0+0", new Double(28.24));  //38
      map.put("S+2.0+5.0+3+0+0+2+0+0+1+0", new Double(21.7));  //39
      
      map.put("P+1.0+3.0+3+0+0+0+0+3+0+0", new Double(13.59));  //40
      map.put("P+2.0+3.0+3+0+0+0+0+1+1+0", new Double(34.14));  //41
      map.put("P+2.0+5.0+4+0+0+0+0+3+1+0", new Double(9.81));  //42
      map.put("P+2.0+5.0+4+1+0+0+0+3+1+0", new Double(23.47));  //43
		}
	}


	/**
	 * Gets the specification attribute of the TPSADescriptor object.
	 *
	 * @return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#tpsa",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the  TPSADescriptor object.
         *
         *  The descriptor takes a Boolean parameter to indicate whether
         *  the descriptor routine should check for aromaticity (TRUE) or
         *  not (FALSE).
	 *
	 * @param  params            The parameter value (TRUE or FALSE)
	 * @throws  CDKException  if the supplied parameter is not of type Boolean
         * @see #getParameters
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length != 1) {
			throw new CDKException("TPSADescriptor expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The first parameter must be of type Boolean");
		}
		// ok, all should be fine
		checkAromaticity = ((Boolean) params[0]).booleanValue();
	}


	/**
	 * Gets the parameters attribute of the TPSADescriptor object.
	 *
	 *@return    The parameter value. For this descriptor it returns a Boolean
         * indicating whether aromaticity was to be checked or not
         * @see #setParameters
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = new Boolean(checkAromaticity);
		return params;
	}


	/**
	 *  Calculates the TPSA for an atom container.
         *  
	 *  Before calling this method, you may want to set the parameter
         *  indicating that aromaticity should be checked. If no parameter is specified
         *  (or if it is set to FALSE) then it is assumed that aromaticaity has already been 
         *  checked.
         *  
	 *  Prior to calling this method it is necessary to either add implicit or explicit hydrogens 
	 *  using {@link org.openscience.cdk.tools.HydrogenAdder#addImplicitHydrogensToSatisfyValency} or
   * {@link org.openscience.cdk.tools.HydrogenAdder#addExplicitHydrogensToSatisfyValency}. 
         *
	 * @param  ac                The AtomContainer whose TPSA is to be calculated
	 * @return                   A double containing the topological surface area
	 * @exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(IAtomContainer ac) throws CDKException {
		Vector profiles = new Vector();
    
    // calculate the set of all rings
		IRingSet rs = (new AllRingsFinder()).findAllRings(ac);
    // check aromaticity if the descriptor parameter is set to true
    if (checkAromaticity)
			HueckelAromaticityDetector.detectAromaticity(ac, rs, true);
    
    // iterate over all atoms of ac
    org.openscience.cdk.interfaces.IAtom[] atoms = ac.getAtoms();
		for(int atomIndex = 0; atomIndex < atoms.length; atomIndex ++)
    {
			if( atoms[atomIndex].getSymbol().equals("N") || atoms[atomIndex].getSymbol().equals("O") ||
          atoms[atomIndex].getSymbol().equals("S") || atoms[atomIndex].getSymbol().equals("P") )
      {
				int singleBondCount = 0;
				int doubleBondCount = 0;
				int tripleBondCount = 0;
				int aromaticBondCount = 0;
        double maxBondOrder = 0;
        double bondOrderSum = 0;
				int hCount = 0;
        int isIn3MemberRing = 0;
				
        // counting the number of single/double/triple/aromatic bonds
        org.openscience.cdk.interfaces.IBond[] connectedBonds = ac.getConnectedBonds(atoms[atomIndex]);
				for(int bondIndex = 0; bondIndex < connectedBonds.length; bondIndex++)
        {
					if(connectedBonds[bondIndex].getFlag(CDKConstants.ISAROMATIC))
						aromaticBondCount++;
					else if(connectedBonds[bondIndex].getOrder() == CDKConstants.BONDORDER_SINGLE)
						singleBondCount++;
					else if(connectedBonds[bondIndex].getOrder() == CDKConstants.BONDORDER_DOUBLE)
						doubleBondCount++;
					else if(connectedBonds[bondIndex].getOrder() == CDKConstants.BONDORDER_TRIPLE)
						tripleBondCount++;
				}
				int formalCharge = atoms[atomIndex].getFormalCharge();
				org.openscience.cdk.interfaces.IAtom[] connectedAtoms = ac.getConnectedAtoms(atoms[atomIndex]);
				int numberOfNeighbours = connectedAtoms.length;
        // EXPLICIT hydrogens: count the number of hydrogen atoms
				for(int neighbourIndex = 0; neighbourIndex < numberOfNeighbours; neighbourIndex++)
					if(connectedAtoms[neighbourIndex].getSymbol().equals("H"))
						hCount++;
        // IMPLICIT hydrogens: count the number of hydrogen atoms and adjust other atom profile properties
        for(int hydrogenIndex = 0; hydrogenIndex < atoms[atomIndex].getHydrogenCount(); hydrogenIndex++)
        {
          hCount++;
          numberOfNeighbours++;
          singleBondCount++;
        }
        // Calculate bond order sum using the counters of single/double/triple/aromatic bonds
				bondOrderSum += singleBondCount * CDKConstants.BONDORDER_SINGLE;
        bondOrderSum += doubleBondCount * CDKConstants.BONDORDER_DOUBLE;
        bondOrderSum += tripleBondCount * CDKConstants.BONDORDER_TRIPLE;
        bondOrderSum += aromaticBondCount * CDKConstants.BONDORDER_AROMATIC;
        // setting maxBondOrder
        if(singleBondCount > 0)
					maxBondOrder = 1.0;
				if(aromaticBondCount > 0)
					maxBondOrder = 1.5;
				if(doubleBondCount > 0)
					maxBondOrder = 2.0;
				if(tripleBondCount > 0)
					maxBondOrder = 3.0;

				// isIn3MemberRing checker
				if(rs.contains(atoms[atomIndex]))
        {
					org.openscience.cdk.interfaces.IRingSet rsAtom = rs.getRings(atoms[atomIndex]);
					for(int ringSetIndex = 0; ringSetIndex < rsAtom.size(); ringSetIndex++)
					{
						Ring ring = (Ring)rsAtom.get(ringSetIndex);
						if(ring.getRingSize() == 3)
							isIn3MemberRing = 1;
					}
				}
				// create a profile of the current atom (atoms[atomIndex]) according to the profile definition in the constructor
        String profile = atoms[atomIndex].getSymbol() +"+"+ maxBondOrder +"+"+ bondOrderSum +"+"+ 
            numberOfNeighbours +"+"+ hCount +"+"+ formalCharge +"+"+ aromaticBondCount +"+"+
            isIn3MemberRing +"+"+ singleBondCount +"+"+ doubleBondCount +"+"+ tripleBondCount;
				//System.out.println("tpsa profile: "+ profile);
				profiles.add(profile);
			}
		}
		// END OF ATOM LOOP
    // calculate the tpsa for the AtomContainer ac
    double tpsa = 0;
		for(int profileIndex = 0; profileIndex < profiles.size(); profileIndex++)
    {
			if(map.containsKey(profiles.elementAt(profileIndex))) {
				tpsa += ((Double)map.get(profiles.elementAt(profileIndex))).doubleValue();
				//System.out.println("tpsa contribs: " + profiles.elementAt(profileIndex) + "\t" + ((Double)map.get(profiles.elementAt(profileIndex))).doubleValue());
			}
		}
		profiles.clear(); // remove all profiles from the profiles-Vector
    //System.out.println("tpsa: " + tpsa);
    
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(tpsa));
	}

	/**
	 *  Gets the parameterNames attribute of the  TPSADescriptor object.
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "useAromaticity";
		return params;
	}



	/**
	 * Gets the parameterType attribute of the TPSADescriptor object.
	 *
	 * @param  name  Description of the Parameter
	 * @return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(true);
	}
}