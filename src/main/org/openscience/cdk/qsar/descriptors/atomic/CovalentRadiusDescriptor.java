/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.LoggingTool;

import java.io.IOException;

/**
 *  This class return the covalent radius of a given atom.
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
 * @author         Miguel Rojas
 * @cdk.created    2006-05-17
 * @cdk.module     qsaratomic
 * @cdk.svnrev  $Revision$
 * @cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:covalentradius
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.CovalentRadiusDescriptorTest")
public class CovalentRadiusDescriptor implements IAtomicDescriptor {

    private AtomTypeFactory factory = null;
    private LoggingTool logger;


    /**
     *  Constructor for the CovalentRadiusDescriptor object.
     *
     *  @throws IOException if an error ocurrs when reading atom type information
     *  @throws ClassNotFoundException if an error occurs during tom typing
     */
    public CovalentRadiusDescriptor() throws IOException, ClassNotFoundException {
        logger = new LoggingTool(this);
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
    @TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#covalentradius",
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
     *  Gets the parameters attribute of the VdWRadiusDescriptor object.
     *
     * @return    The parameters value
     * @see #setParameters
     */
    @TestMethod(value="testGetParameters")
    public Object[] getParameters() {
        return null;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return new String[]{"covalentRadius"};
    }


    /**
     *  This method calculate the Covalent radius of an atom.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  container         The {@link IAtomContainer} for which the descriptor is to be calculated
     *@return                   The Covalent radius of the atom     
     */

    @TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer container) {
        if (factory == null) 
            try {
                factory = AtomTypeFactory.getInstance(
                    "org/openscience/cdk/config/data/jmol_atomtypes.txt", 
                    container.getBuilder()
                );
            } catch (Exception exception) {
              return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new DoubleResult(Double.NaN),
                    getDescriptorNames(), exception);
            }

        double covalentradius;
        try {
            String symbol = atom.getSymbol();
            IAtomType type = factory.getAtomType(symbol);
            covalentradius = type.getCovalentRadius();
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new DoubleResult(covalentradius),
                    getDescriptorNames());
        } catch (Exception exception) {
            logger.debug(exception);
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new DoubleResult(Double.NaN),
                    getDescriptorNames(), exception);
        }
    }


    /**
     *  Gets the parameterNames attribute of the VdWRadiusDescriptor object.
     *
     * @return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return new String[0];
    }

    /**
     * Gets the parameterType attribute of the VdWRadiusDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return null;
    }
}

