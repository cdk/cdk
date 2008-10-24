/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Sum of the atomic polarizabilities (including implicit hydrogens).
 *
 * Polarizabilities are taken from 
 * <a href="http://www.sunysccc.edu/academic/mst/ptable/p-table2.htm">http://www.sunysccc.edu/academic/mst/ptable/p-table2.htm</a>.
 * <p>
 * This class need explicit hydrogens.
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
 * Returns a single value with name <i>apol</i>.
 *
 * @author      mfe4
 * @cdk.created 2004-11-13
 * @cdk.module  qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:apol
 *
 * @cdk.keyword polarizability, atomic
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.APolDescriptorTest")
public class APolDescriptor implements IMolecularDescriptor {

    private LoggingTool logger;
    private IsotopeFactory ifac = null;
    /* Atomic polarizabilities ordered by atomic number from 1 to 102. */
    private static double[] polarizabilities;
    private static final String[] names = {"apol"};

    /**
     *  Constructor for the APolDescriptor object.
     */
    public APolDescriptor() {
        logger = new LoggingTool(this);
        if (polarizabilities == null) {
            polarizabilities = new double[] {0, 0.666793, 0.204956, 24.3, 5.6, 3.03, 1.76, 
                1.1, 0.802, 0.557, 0.3956, 23.6, 10.6, 6.8, 5.38, 3.63, 2.9, 2.18, 1.6411, 
                43.4, 22.8, 17.8, 14.6, 12.4, 11.6, 9.4, 8.4, 7.5, 6.8, 6.1, 7.1, 8.12, 6.07, 
                4.31, 3.77, 3.05, 2.4844, 47.3, 27.6, 22.7, 17.9, 15.7, 12.8, 11.4, 9.6, 8.6, 
                4.8, 7.2, 7.2, 10.2, 7.7, 6.6, 5.5, 5.35, 4.044, 59.6, 39.7, 31.1, 29.6, 28.2, 
                31.4, 30.1, 28.8, 27.7, 23.5, 25.5, 24.5, 23.6, 22.7, 21.8, 21, 21.9, 16.2, 
                13.1, 11.1, 9.7, 8.5, 7.6, 6.5, 5.8, 5.7, 7.6, 6.8, 7.4, 6.8, 6, 5.3, 48.7, 
                38.3, 32.1, 32.1, 25.4, 27.4, 24.8, 24.5, 23.3, 23, 22.7, 20.5,19.7,23.8,18.2,17.5};
        }
    }

    /**
     * Returns a <code>Map</code> which specifies which descriptor
     * is implemented by this class. 
     *
     * These fields are used in the map:
     * <ul>
     * <li>Specification-Reference: refers to an entry in a unique dictionary
     * <li>Implementation-Title: anything
     * <li>Implementation-Identifier: a unique identifier for this version of
     *  this class
     * <li>Implementation-Vendor: CDK, JOELib, or anything else
     * </ul>
     *
     * @return An object containing the descriptor specification
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#apol",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    };

    /**
     *  Sets the parameters attribute of the APolDescriptor object.
     *
     *  This descriptor does not take any parameters
     *
     *@param  params            The new parameters value
     *@throws  CDKException  no exception is thrown
     *@see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }


    /**
     *  Gets the parameters attribute of the APolDescriptor object.
     *
     *  This method does not return any parameters
     *
     *@return    The parameters value
     *@see #setParameters
     */
    public Object[] getParameters() {
        // no parameters for this descriptor
        return (null);
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }


    /**
     * Calculate the sum of atomic polarizabilities in an {@link IAtomContainer}.
     *
     *@param  container  The {@link IAtomContainer} for which the descriptor is to be calculated
     *@return            The sum of atomic polarizabilities
     *@throws CDKException if there is an error in getting element symbols from the
     * {@link IsotopeFactory}
     */
    @TestMethod("testAPolDescriptorTest")
    public DescriptorValue calculate(IAtomContainer container) {
        double apol = 0;
        int atomicNumber = 0;
        try {
            ifac = IsotopeFactory.getInstance(container.getBuilder());			
            IElement element = null;
            java.util.Iterator atoms = container.atoms().iterator();
            String symbol = null;
            while (atoms.hasNext()) {
                symbol = ((IAtom)atoms.next()).getSymbol();
                element = ifac.getElement(symbol);
                atomicNumber = element.getAtomicNumber();
                apol += polarizabilities[atomicNumber];
            }
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new DoubleResult(apol), getDescriptorNames());
        } catch (Exception ex1) {
            logger.debug(ex1);
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new DoubleResult(Double.NaN), getDescriptorNames(),
                    new CDKException("Problems with IsotopeFactory due to " + ex1.toString(), ex1));            
        }
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    @TestMethod("testGetDescriptorResultType")
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResult(0.0);
    }


    /**
     *  Gets the parameterNames attribute of the APolDescriptor object.
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }


    /**
     *  Gets the parameterType attribute of the APolDescriptor object.
     *
     *@param  name  Description of the Parameter
     *@return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return (null);
    }
}

