/*  $RCSfile$
 *  $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2004-2008  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.charges;

import java.util.Map;
import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.modeling.builder3d.ForceFieldConfigurator;

/**
 *  The calculation of the MMFF94 partial charges.
 *  Charges are stored as atom properties:
 *  for an AtomContainer ac, values are calculated with:
 *  <pre>
 *  HydrogenAdder hAdder = new HydrogenAdder();
 *  SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 *  IAtomContainer ac = sp.parseSmiles("CC");
 *  hAdder.addExplicitHydrogensToSatisfyValency((Molecule)ac);
 *  MMFF94PartialCharges mmff = new MMFF94PartialCharges();
 *  mmff.assignMMFF94PartialCharges(ac);
 *  </pre>
 *  and for each atom, the value is given by:
 *  <pre>
 *  ( (Double)atom.getProperty("MMFF94charge") ).doubleValue().
 *  </pre>
 *
 * @author      mfe4
 * @author      chhoppe
 * @cdk.created 2004-11-03
 * @cdk.module  forcefield
 * @cdk.svnrev  $Revision$
 */
public class MMFF94PartialCharges implements IChargeCalculator {

	
	/**
	 *  Constructor for the MMFF94PartialCharges object
	 */
	public MMFF94PartialCharges() { }
	
	
	
	/**
	 *  Main method which assigns MMFF94 partial charges
	 *
	 *@param  ac             AtomContainer
	 *@return                AtomContainer with MMFF94 partial charges as atom properties
	 *@exception  Exception  Possible Exceptions
	 */
	public IAtomContainer assignMMFF94PartialCharges(IAtomContainer ac) throws Exception {
		ForceFieldConfigurator ffc = new ForceFieldConfigurator();
		ffc.setForceFieldConfigurator("mmff94");
		ffc.assignAtomTyps((IMolecule)ac);
		Map<String,Object> parameterSet = ffc.getParameterSet();
		// for this calculation,
		// we need some values stored in the vector "data" in the
		// hashtable of these atomTypes:		
		double charge = 0;
		double formalCharge = 0;
		double formalChargeNeigh = 0;
		double theta = 0;
		double sumOfFormalCharges = 0;
		double sumOfBondIncrements = 0;
		org.openscience.cdk.interfaces.IAtom thisAtom = null;
		List<IAtom> neighboors;
		Object data = null;
		Object bondData = null;
		Object dataNeigh = null;
		java.util.Iterator<IAtom> atoms = ac.atoms().iterator();
		while(atoms.hasNext()) {
			//logger.debug("ATOM "+i+ " " +atoms[i].getSymbol());
			thisAtom = atoms.next();
			data = parameterSet.get("data"+thisAtom.getAtomTypeName());
			neighboors = ac.getConnectedAtomsList(thisAtom);
			formalCharge = thisAtom.getCharge();
			theta = (Double)((List)data).get(5);
			charge = formalCharge * (1 - (neighboors.size() * theta));
			sumOfFormalCharges = 0;
			sumOfBondIncrements = 0;
            for (IAtom neighboor : neighboors) {
                IAtom neighbour = (IAtom) neighboor;
                dataNeigh = parameterSet.get("data" + neighbour.getAtomTypeName());
                if (parameterSet.containsKey("bond" + thisAtom.getAtomTypeName() + ";" + neighbour.getAtomTypeName())) {
                    bondData = parameterSet.get("bond" + thisAtom.getAtomTypeName() + ";" + neighbour.getAtomTypeName());
                    sumOfBondIncrements -= (Double) ((List)bondData).get(4);
                } else
                if (parameterSet.containsKey("bond" + neighbour.getAtomTypeName() + ";" + thisAtom.getAtomTypeName())) {
                    bondData = parameterSet.get("bond" + neighbour.getAtomTypeName() + ";" + thisAtom.getAtomTypeName());
                    sumOfBondIncrements += (Double) ((List)bondData).get(4);
                } else {
                    // Maybe not all bonds have pbci in mmff94.prm, i.e. C-N
                    sumOfBondIncrements += (theta - (Double) ((List)dataNeigh).get(5));
                }


                dataNeigh = parameterSet.get("data" + neighbour.getID());
                formalChargeNeigh = neighbour.getCharge();
                sumOfFormalCharges += formalChargeNeigh;
            }
            charge += sumOfFormalCharges * theta;
			charge += sumOfBondIncrements;
			thisAtom.setProperty("MMFF94charge", charge);
			//logger.debug( "CHARGE :"+thisAtom.getProperty("MMFF94charge") );
		}
		return ac;
	}
	
	public void calculateCharges(IAtomContainer container) throws CDKException {
		try {
	        assignMMFF94PartialCharges(container);
        } catch (Exception exception) {
        	throw new CDKException(
    	        "Could not calculate MMFF94 partial charges: " +
    	        exception.getMessage(), exception
        	);
        }
	}
}
