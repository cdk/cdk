/*
 *  $RCSfile$
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
package org.openscience.cdk.qsar.descriptors.atomic;

import java.io.IOException;

import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  This class return the VdW radius of a given atom.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>atomPosition</td>
 *     <td>0</td>
 *     <td>The position of the atom whose protons calculate total partial charge</td>
 *   </tr>
 * </table>
 *
 * @author         mfe4
 * @cdk.created    2004-11-13
 * @cdk.module     qsar
 * @cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:vdwradius
 */
public class VdWRadiusDescriptor implements IMolecularDescriptor {

    private int atomPosition = 0;
    private AtomTypeFactory factory = null;
    private LoggingTool logger;


    /**
     *  Constructor for the VdWRadiusDescriptor object.
     *
     *  @throws IOException if an error ocurrs when reading atom type information
     *  @throws ClassNotFoundException if an error occurs during tom typing
     */
    public VdWRadiusDescriptor() throws IOException, ClassNotFoundException {
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
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#vdwradius",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the VdWRadiusDescriptor object.
     *
     *  This class takes a single parameter of type Integer indicating 
     *  which atom the radius is to be calculated for
     *
     *@param  params            The parameter is the atom position
     *@throws  CDKException if more than on parameter or a non-Integer parameters is specified
     *@see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("VdWRadiusDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter must be of type Integer");
        }
        atomPosition = ((Integer) params[0]).intValue();
    }


    /**
     *  Gets the parameters attribute of the VdWRadiusDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        Object[] params = new Object[1];
        params[0] = new Integer(atomPosition);
        return params;
    }


    /**
     *  This method calculate the Van der Waals radius of an atom.
     *
     *@param  container         The {@link IAtomContainer} for which the descriptor is to be calculated
     *@return                   The Van der Waals radius of the atom
     *@exception  CDKException  if an error occurs during atom typing
     */

    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
        if (factory == null) 
            try {
                factory = AtomTypeFactory.getInstance(
                    "org/openscience/cdk/config/data/jmol_atomtypes.txt", 
                    container.getBuilder()
                );
            } catch (Exception exception) {
                throw new CDKException("Could not instantiate AtomTypeFactory!", exception);
            }

        double vdwradius = 0;
        try {
            String symbol = container.getAtomAt(atomPosition).getSymbol();
            IAtomType type = factory.getAtomType(symbol);
            vdwradius = type.getVanderwaalsRadius();
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(vdwradius));
        } catch (Exception ex1) {
            logger.debug(ex1);
            throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString(), ex1);
        }
    }


    /**
     *  Gets the parameterNames attribute of the VdWRadiusDescriptor object.
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "atomPosition";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the VdWRadiusDescriptor object.
     *
     *@param  name  Description of the Parameter
     *@return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return new Integer(0);
    }
}

