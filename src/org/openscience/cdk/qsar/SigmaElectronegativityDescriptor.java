/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.qsar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import java.util.Vector;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.tools.HydrogenAdder;
import java.util.Map;
import java.util.Hashtable;
import java.io.IOException;

public class SigmaElectronegativityDescriptor implements Descriptor {
	
	private int atomPosition=0;
	
	public SigmaElectronegativityDescriptor() { }
	
	public Map getSpecification() {
		Hashtable specs = new Hashtable();
		specs.put("Specification-Reference", "http://qsar.sourceforge.net/dicts/qsar-descriptors:sigmaElectronegativity");
		specs.put("Implementation-Title", this.getClass().getName());
		specs.put("Implementation-Identifier", "$Id$"); // added by CVS
		specs.put("Implementation-Vendor", "The Chemistry Development Kit");
		return specs;
	};

	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("SigmaElectronegativityDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		atomPosition = ((Integer)params[0]).intValue();
	}


	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = new Integer(atomPosition);
		return params;
	}

	public Object calculate(AtomContainer ac) throws CDKException {
		double sigmaElectronegativity = 0;
		double atomPartialCharge = 0;
		Molecule mol = new Molecule(ac);
		GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();	
		HydrogenAdder hAdder = new HydrogenAdder();
		try{		
			hAdder.addExplicitHydrogensToSatisfyValency(mol);
			System.out.println("AtomNr:"+mol.getAtomAt(atomPosition).getSymbol());
			peoe.assignGasteigerMarsiliPartialCharges(mol, true);
			double[] gasteigerFactors = peoe.assignGasteigerMarsiliFactors(mol);
			int stepSize = peoe.getStepSize();
			int start = (stepSize * (atomPosition)+atomPosition);
			sigmaElectronegativity = (( gasteigerFactors[start] ) + (mol.getAtomAt(atomPosition).getCharge() * gasteigerFactors[start + 1]) +  (gasteigerFactors[start + 2] * (( mol.getAtomAt(atomPosition).getCharge() * mol.getAtomAt(atomPosition).getCharge() ))));
			//sigmaElectronegativity = gasteigerFactors[start + 1];// +  (gasteigerFactors[start + 2] * (( mol.getAtomAt(atomPosition).getCharge() * mol.getAtomAt(atomPosition).getCharge() ))));
			return new Double(sigmaElectronegativity);
		}catch (Exception ex1){
			throw new CDKException("Problems with HydrogenAdder due to "+ex1.toString());
		}
	}

	
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "The position of the atom whose calculate sigma electronegativity";
		return params;
	}

	
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[1];
		paramTypes[0] = new Integer(1);
		return paramTypes;
	}
}

