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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.qsar;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.*;
import org.openscience.cdk.graph.*;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.graph.matrix.*;


/**
 * Calculates the Weiner number for a molecular graph.
 * 
 *  Wiener path number: half the sum of all the distance matrix entries; Wiener
 *  polarity number: half the sum of all the distance matrix entries with a
 *  value of 3. For more informations, see
 *  <a href="http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH">here</a> or
 *  <a href="http://www.csam.montclair.edu/~burch/bpnew.pdf">here</a>.
 *
 *@author         mfe4
 *@cdk.created        December 7, 2004
 *@cdk.created    2004-11-03
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:wienerNumbers
 */
public class WienerNumbersDescriptor implements Descriptor {

    double[][] matr = null;
    DoubleArrayResult wienerNumbers = null;
    ConnectionMatrix connectionMatrix = new ConnectionMatrix();
    PathTools pathTools = new PathTools();
    AtomContainerManipulator atm =  new AtomContainerManipulator();

    /**
     *  Constructor for the WienerNumbersDescriptor object.
     */
    public WienerNumbersDescriptor() { 

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
                "http://qsar.sourceforge.net/dicts/qsar-descriptors:wienerNumbers",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the WienerNumbersDescriptor object.
     *
     *  This descriptor does not take any parameters
     *
     *@param  params            The new parameters value
     *@exception  CDKException  This method will not throw any exceptions
    *@see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }


    /**
     *  Gets the parameters attribute of the WienerNumbersDescriptor object.
     *
     *  This descriptor does not return any parameters
     *
     *@return    The parameters value
     *@see #setParameters
     */
    public Object[] getParameters() {
        return (null);
        // no parameters to return
    }


    /**
     * Calculate the Weiner numbers.
     *
     *@param  atomContainer   The {@link AtomContainer} for which this descriptor is to be calculated
     *@return                   wiener numbers as array of 2 doubles
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(AtomContainer atomContainer) throws CDKException {
        wienerNumbers = new DoubleArrayResult(2);
        double wienerPathNumber = 0; //weinerPath
        double wienerPolarityNumber = 0; //weinerPol

        // "matr" is the connection matrix
        matr = connectionMatrix.getMatrix(atm.removeHydrogens(atomContainer));
        // and "distances" is ist matrix of int where 
        // for example distance[1][2] = length of the shortest path
        // between atom at position 1 and atom at position 2.
        int[][] distances = pathTools.computeFloydAPSP(matr);

        int partial = 0;
        //wienerPolarityNumber = 0;
        for (int i = 0; i < distances.length; i++) {
            for (int j = 0; j < distances.length; j++) {
                partial = distances[i][j];
                wienerPathNumber += partial;
                if (partial == 3) {
                    wienerPolarityNumber += 1;
                }
            }
        }
        wienerPathNumber = wienerPathNumber / 2;
        wienerPolarityNumber = wienerPolarityNumber / 2;

        wienerNumbers.add(wienerPathNumber);
        wienerNumbers.add(wienerPolarityNumber);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), wienerNumbers);
    }

    /**
     *  Gets the parameterNames attribute of the WienerNumbersDescriptor object.
     *
     * This descriptor does not return any parameters
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }



    /**
     *  Gets the parameterType attribute of the WienerNumbersDescriptor object.
     *
     *@param  name  Description of the Parameter
     *@return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return (null);
    }
}

