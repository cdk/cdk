/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.AtomValenceDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.LoggingTool;


/**
 *  Atomic valence connectivity index (order 1). See
 *  <a href="http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html">http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html</a> and
 *  <a href="http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH">http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH</a>.
 *
 *  <p>Returned value is:
 *  <ul>
 *    <li>chi0vC is the Carbon valence connectivity index (order 1),
 *  </ul>
 *  where valence is the number of s and p valence electrons of atom.
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
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module	qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:chi1vC
 * @cdk.bug     1298108
 */
public class ValenceCarbonConnectivityOrderOneDescriptor implements IMolecularDescriptor {

    private LoggingTool logger;
    private static Map valences;
    private AtomValenceDescriptor avd = null;
	/**
	 *  Constructor for the ValenceCarbonConnectivityOrderOneDescriptor object
	 */
	public ValenceCarbonConnectivityOrderOneDescriptor() { 
            logger = new LoggingTool(this);
	    if (valences == null) { 
		avd = new AtomValenceDescriptor();
		valences = avd.valencesTable;
	    }
        }


	/**
	 *  Gets the specification attribute of the
	 *  ValenceCarbonConnectivityOrderOneDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#chi1vC",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the
	 *  ValenceCarbonConnectivityOrderOneDescriptor object
	 *
	 *@param  params            The new parameters value
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		// no parameters for this descriptor
	}


	/**
	 *  Gets the parameters attribute of the
	 *  ValenceCarbonConnectivityOrderOneDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// no parameters to return
		return (null);
	}


	/**
	 *  calculates the Carbon valence connectivity index (order 1) descriptor for an atom container
	 *
	 *@param  atomContainer                AtomContainer
	 *@return                   Carbon valence connectivity index (order 1)
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(IAtomContainer atomContainer) throws CDKException {
		int valence = 0;
		int atomicNumber = 0;
		int hcount = 0;
		int atomValue = 0;
		double val0 = 0;
		double val1 = 0;
		ArrayList chiAtom = new ArrayList(2);
		//double chi1v = 0;
		double chi1vC = 0;
		IAtom curAtom = null;
		IBond bond = null;
		java.util.List neighatoms = null;
		IElement element = null;
		IsotopeFactory elfac = null;
		String symbol = null;
		Iterator bonds = atomContainer.bonds();
		while (bonds.hasNext()) {
			bond = (IBond)bonds.next();
			if ((!bond.getAtom(0).getSymbol().equals("H")) && (!bond.getAtom(0).getSymbol().equals("H"))) {
				val0 = 0;
				val1 = 0;
				chiAtom.clear();
				java.util.Iterator bondIter = bond.atoms();
				while (bondIter.hasNext()) {
					curAtom = (IAtom)bondIter.next();
					symbol = curAtom.getSymbol();
					try {
						elfac = IsotopeFactory.getInstance(atomContainer.getBuilder());
					} catch (Exception exc) {
                                            logger.debug(exc);
						throw new CDKException("Problem instantiating IsotopeFactory: " + exc.toString(), exc);
					}
					try {
						element = elfac.getElement(symbol);
					} catch (Exception exc) {
                                            logger.debug(exc);
						throw new CDKException("Problem getting isotope " + symbol + " from ElementFactory: " + exc.toString(), exc);
					}
					atomicNumber = element.getAtomicNumber();
					valence = ((Integer)valences.get(symbol)).intValue();
					hcount = 0;
					atomValue = 0;
					neighatoms = atomContainer.getConnectedAtomsList(curAtom);
					for (int n = 0; n < neighatoms.size(); n++) {
						if (((IAtom)neighatoms.get(n)).getSymbol().equals("H")) {
							hcount += 1;
						}
					}
					hcount += curAtom.getHydrogenCount();
					atomValue = (valence - hcount) / (atomicNumber - valence - 1);
					//if(atomValue > 0) {
						chiAtom.add(new Double(atomValue));
						//System.out.println(symbol+"= atomvalue: "+atomValue+",val: "+valence);
					//}
				}
				val0 = ( (Double)chiAtom.get(0) ).doubleValue();
				val1 = ( (Double)chiAtom.get(1) ).doubleValue();
				if(val0 > 0 && val1 >0) {
					if((bond.getAtom(0).getSymbol().equals("C")) && (bond.getAtom(1).getSymbol().equals("C"))) {
						chi1vC += 1/(Math.sqrt(val0 * val1));
					}
				}
			}
		}		
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(chi1vC), new String[] {"chi1vC"});
	}


	/**
	 *  Gets the parameterNames attribute of the
	 *  ValenceCarbonConnectivityOrderOneDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		// no param names to return
		return (null);
	}



	/**
	 *  Gets the parameterType attribute of the
	 *  ValenceCarbonConnectivityOrderOneDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return (null);
	}
}

