/*
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

import org.openscience.cdk.Bond;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.qsar.result.*;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import java.lang.Math;
import java.util.Vector;


/**
 * A topological descriptor combining distance and adjacency information.
 * This descriptor is described by Sharma et al. {@cdk.cite SHA97} and has been shown
 * to correlate well with a number of physical properties. The descriptor is also reported to
 * have good discriminatory ability. 
 * <p>
 * The eccentric connectivity index for a hydrogen supressed molecular graph is given by the 
 * expression
 * <center>
 * \xi^{c} = \sum_{i = 1}{n} E(i) V(i)
 * </center>
 * where E(i) is the eccentricity of the i<sup>th</sup> atom (path length from the 
 * i<sup>th</sup> atom to the atom farthest from it) and V(i) is the vertex degree of the
 * i<sup>th</sup> atom.
 * 
 * @author      Rajarshi Guha
 * @cdk.created     2005-03-19
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 */
public class EccentricConnectivityIndexDescriptor implements Descriptor {
    
    private LoggingTool logger;

    public EccentricConnectivityIndexDescriptor() {
        logger = new LoggingTool(this);
    }

	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:eccentricConnectivityIndex",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
    };

    /**
     *  Sets the parameters attribute of the EccentricConnectivityIndexDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     *  Gets the parameters attribute of the EccentricConnectivityIndexDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // no parameters to return
        return(null);
    }
    /**
     *  Gets the parameterNames attribute of the EccentricConnectivityIndexDescriptor object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return(null);
    }


    /**
     *  Gets the parameterType attribute of the EccentricConnectivityIndexDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
         return (null);
    }

    /**
     *  Calculates the eccentric connectivity
     *
     *@param  container  Parameter is the atom container.
     *@return            An IntegerResult value representing the eccentric connectivity index
     */

    public DescriptorValue calculate(AtomContainer container) {
        AtomContainer local = AtomContainerManipulator.removeHydrogens(container);

        int natom = local.getAtomCount();
        int[][] admat = AdjacencyMatrix.getMatrix(local);
        int[][] distmat = PathTools.computeFloydAPSP(admat);
        
        int eccenindex = 0;
        for (int i = 0; i < natom; i++) {
            int max = -1;
            for (int j = 0; j < natom; j++) {
                if (distmat[i][j] > max) max = distmat[i][j];
            }
            int degree = local.getBondCount(i);
            eccenindex += max * degree;
        }
        IntegerResult retval = new IntegerResult(eccenindex);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), retval);
    }
}
    

