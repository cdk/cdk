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
import org.openscience.cdk.Bond;
import org.openscience.cdk.Element;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.qsar.result.*;
import junit.framework.AssertionFailedError;
import java.util.Map;
import java.util.Hashtable;
import java.util.ArrayList;


/**
 *  Atomic valence connectivity index (order 1):
 *  http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html
 *  http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH
 *  returned values are:
 *  chi1v is the Atomic valence connectivity index (order 1),
 *  chi1_C is the Carbon valence connectivity index (order 1);
 *  valence is the number of s and p valence electrons of atom.
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module	qsar
 * @cdk.set     qsar-descriptors
 */
public class ValenceConnectivityOrderOneDescriptor implements Descriptor {

	/**
	 *  Constructor for the ValenceConnectivityOrderOneDescriptor object
	 */
	public ValenceConnectivityOrderOneDescriptor() { }


	/**
	 *  Gets the specification attribute of the
	 *  ValenceConnectivityOrderOneDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:chiValuesVCOO",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the
	 *  ValenceConnectivityOrderOneDescriptor object
	 *
	 *@param  params            The new parameters value
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		// no parameters for this descriptor
	}


	/**
	 *  Gets the parameters attribute of the
	 *  ValenceConnectivityOrderOneDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// no parameters to return
		return (null);
	}


	/**
	 *  calculates the chi1v and chi1v_C descriptors for an atom container
	 *
	 *@param  ac                AtomContainer
	 *@return                   chi1v and chi1_C returned as arrayList
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorResult calculate(AtomContainer ac) throws CDKException {
		Hashtable valences = new Hashtable();
		valences.put("Li", new Integer(1));
		valences.put("Be", new Integer(2));
		valences.put("B", new Integer(3));
		valences.put("C", new Integer(4));
		valences.put("N", new Integer(5));
		valences.put("O", new Integer(6));
		valences.put("F", new Integer(7));
		valences.put("Na", new Integer(1));
		valences.put("Mg", new Integer(2));
		valences.put("Al", new Integer(3));
		valences.put("Si", new Integer(4));
		valences.put("P", new Integer(5));
		valences.put("S", new Integer(6));
		valences.put("Cl", new Integer(7));
		valences.put("K", new Integer(1));
		valences.put("Ca", new Integer(2));
		valences.put("Ga", new Integer(3));
		valences.put("Ge", new Integer(4));
		valences.put("As", new Integer(5));
		valences.put("Se", new Integer(6));
		valences.put("Br", new Integer(7));
		valences.put("Rb", new Integer(1));
		valences.put("Sr", new Integer(2));
		valences.put("In", new Integer(3));
		valences.put("Sn", new Integer(4));
		valences.put("Sb", new Integer(5));
		valences.put("Te", new Integer(6));
		valences.put("I", new Integer(7));
		valences.put("Cs", new Integer(1));
		valences.put("Ba", new Integer(2));
		valences.put("Tl", new Integer(3));
		valences.put("Pb", new Integer(4));
		valences.put("Bi", new Integer(5));
		valences.put("Po", new Integer(6));
		valences.put("At", new Integer(7));
		valences.put("Fr", new Integer(1));
		valences.put("Ra", new Integer(2));
		
		int valence = 0;
		int atomicNumber = 0;
		int hcount = 0;
		int atomValue = 0;
		double val0 = 0;
		double val1 = 0;
		DoubleArrayResult chiValuesVCOO = new DoubleArrayResult(2);
		ArrayList chiAtom = new ArrayList(2);
		ArrayList chiCarbon = new ArrayList(2);
		double chi1v = 0;
		double chi1v_C = 0;
		Atom[] atoms = null;
		Atom[] neighatoms = null;
		Element element = null;
		IsotopeFactory elfac = null;
		String symbol = null;
		Bond[] bonds = ac.getBonds();
		for (int b = 0; b < bonds.length; b++) {
			atoms = bonds[b].getAtoms();
			if ((!atoms[0].getSymbol().equals("H")) || (!atoms[1].getSymbol().equals("H"))) {
				val0 = 0;
				val1 = 0;
				chiAtom.clear();
				chiCarbon.clear();
				for (int a = 0; a < atoms.length; a++) {
					symbol = atoms[a].getSymbol();
					try {
						elfac = IsotopeFactory.getInstance();
					} catch (Exception exc) {
						throw new AssertionFailedError("Problem instantiating IsotopeFactory: " + exc.toString());
					}
					try {
						element = elfac.getElement(symbol);
					} catch (Exception exc) {
						throw new AssertionFailedError("Problem getting isotope " + symbol + " from ElementFactory: " + exc.toString());
					}
					atomicNumber = element.getAtomicNumber();
					valence = ((Integer)valences.get(symbol)).intValue();
					hcount = 0;
					atomValue = 0;
					neighatoms = ac.getConnectedAtoms(atoms[a]);
					for (int n = 0; n < neighatoms.length; n++) {
						if (neighatoms[n].getSymbol().equals("H")) {
							hcount += 1;
						}
					}
					hcount += atoms[a].getHydrogenCount();
					atomValue = (valence - hcount) / (atomicNumber - valence - 1);
					if(atomValue > 0) {
						chiAtom.add(new Double(atomValue));
						System.out.println(symbol+"= atomvalue: "+atomValue+",val: "+valence+",h:"+hcount);
					}
				}
				val0 = ( (Double)chiAtom.get(0) ).doubleValue();
				val1 = ( (Double)chiAtom.get(1) ).doubleValue();
				if((atoms[0].getSymbol().equals("C")) && (atoms[1].getSymbol().equals("C"))) {
					chi1v_C += 1/(Math.sqrt(val0 * val1));
				}
				chi1v += 1/(Math.sqrt(val0 * val1));
			}
		}
		chiValuesVCOO.add(chi1v);
		chiValuesVCOO.add(chi1v_C);		
		return chiValuesVCOO;
	}


	/**
	 *  Gets the parameterNames attribute of the
	 *  ValenceConnectivityOrderOneDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		// no param names to return
		return (null);
	}



	/**
	 *  Gets the parameterType attribute of the
	 *  ValenceConnectivityOrderOneDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return (null);
	}
}

