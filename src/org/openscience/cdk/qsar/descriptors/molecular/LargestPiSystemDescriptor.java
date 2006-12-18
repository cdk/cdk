/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2006  Christian Hoppe <chhoppe@users.sf.net>
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

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.ringsearch.AllRingsFinder;

import java.util.Vector;

/**
 * Class that returns the number of atoms in the largest pi system.
 * <p/>
 * <p>This descriptor uses these parameters:
 * <table border="1">
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td>checkAromaticity</td>
 * <td>false</td>
 * <td>True is the aromaticity has to be checked</td>
 * </tr>
 * </table>
 * <p/>
 * Returns a single value named <i>nAtomPi</i>
 *
 * @author chhoppe from EUROSCREEN
 * @cdk.created 2006-1-03
 * @cdk.module qsar
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:largestPiSystem
 */


public class LargestPiSystemDescriptor implements IMolecularDescriptor {

    private boolean checkAromaticity = false;


    /**
     * Constructor for the LargestPiSystemDescriptor object.
     */
    public LargestPiSystemDescriptor() {
    }

    /**
     * Returns a <code>Map</code> which specifies which descriptor
     * is implemented by this class.
     * <p/>
     * These fields are used in the map:
     * <ul>
     * <li>Specification-Reference: refers to an entry in a unique dictionary
     * <li>Implementation-Title: anything
     * <li>Implementation-Identifier: a unique identifier for this version of
     * this class
     * <li>Implementation-Vendor: CDK, JOELib, or anything else
     * </ul>
     *
     * @return An object containing the descriptor specification
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#largestPiSystem",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     * Sets the parameters attribute of the LargestPiSystemDescriptor object.
     * <p/>
     * This descriptor takes one parameter, which should be Boolean to indicate whether
     * aromaticity has been checked (TRUE) or not (FALSE).
     *
     * @param params The new parameters value
     * @throws CDKException if more than one parameter or a non-Boolean parameter is specified
     * @see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("LargestPiSystemDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Boolean)) {
            throw new CDKException("The first parameter must be of type Boolean");
        }
        // ok, all should be fine
        checkAromaticity = ((Boolean) params[0]).booleanValue();
    }


    /**
     * Gets the parameters attribute of the LargestPiSystemDescriptor object.
     *
     * @return The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Boolean(checkAromaticity);
        return params;
    }


    /**
     * Calculate the count of atoms of the largest pi system in the supplied {@link AtomContainer}.
     * <p/>
     * <p>The method require one parameter:
     * <ol>
     * <li>if checkAromaticity is true, the method check the aromaticity,
     * <li>if false, means that the aromaticity has already been checked
     * </ol>
     *
     * @param container The {@link IAtomContainer} for which this descriptor is to be calculated
     * @return the number of atoms in the largest pi system of this AtomContainer
     * @throws CDKException if there is a problem in atomaticity detection
     * @see #setParameters
     */
    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
        //System.out.println("LargestPiSystemDescriptor");
        if (checkAromaticity) {
            IRingSet rs = (new AllRingsFinder()).findAllRings(container);
            HueckelAromaticityDetector.detectAromaticity(container, rs, true);
        }
        int largestPiSystemAtomsCount = 0;
        Vector startSphere = null;
        Vector path = null;
        //Set all VisitedFlags to False
        for (int i = 0; i < container.getAtomCount(); i++) {
            container.getAtom(i).setFlag(CDKConstants.VISITED, false);
        }
        //System.out.println("Set all atoms to Visited False");
        for (int i = 0; i < container.getAtomCount(); i++) {
            //Possible pi System double bond or triple bond, charge, N or O (free electron pair)
            //System.out.println("atom:"+i+" maxBondOrder:"+container.getMaximumBondOrder(atoms[i])+" Aromatic:"+atoms[i].getFlag(CDKConstants.ISAROMATIC)+" FormalCharge:"+atoms[i].getFormalCharge()+" Charge:"+atoms[i].getCharge()+" Flag:"+atoms[i].getFlag(CDKConstants.VISITED));
            if ((container.getMaximumBondOrder(container.getAtom(i)) > 1 || Math.abs(container.getAtom(i).getFormalCharge()) >= 1 || container.getAtom(i).getFlag(CDKConstants.ISAROMATIC) || container.getAtom(i).getSymbol().equals("N") || container.getAtom(i).getSymbol().equals("O")) & !container.getAtom(i).getFlag(CDKConstants.VISITED))
            {
                //System.out.println("...... -> Accepted");
                startSphere = new Vector();
                path = new Vector();
                startSphere.addElement(container.getAtom(i));
                breadthFirstSearch(container, startSphere, path);
                if (path.size() > largestPiSystemAtomsCount) {
                    largestPiSystemAtomsCount = path.size();
                }
            }

        }


        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new IntegerResult(largestPiSystemAtomsCount),  new String[] {"nAtomP"});
    }

    /**
     * Performs a breadthFirstSearch in an AtomContainer starting with a
     * particular sphere, which usually consists of one start atom, and searches
     * for a pi system.
     *
     * @param container The AtomContainer to
     *                  be searched
     * @param sphere    A sphere of atoms to
     *                  start the search with
     * @param path      A vector which stores the atoms belonging to the pi system
     * @throws org.openscience.cdk.exception.CDKException
     *          Description of the
     *          Exception
     */
    public void breadthFirstSearch(IAtomContainer container, Vector sphere, Vector path) throws org.openscience.cdk.exception.CDKException {
        IAtom atom = null;
        IAtom nextAtom = null;
        Vector newSphere = new Vector();
        //System.out.println("Start of breadthFirstSearch");
        for (int i = 0; i < sphere.size(); i++) {
            atom = (IAtom) sphere.elementAt(i);
            //System.out.println("BreadthFirstSearch around atom " + (atomNr + 1));
            java.util.List bonds = container.getConnectedBondsList(atom);
            for (int j = 0; j < bonds.size(); j++) {
                nextAtom = ((IBond) bonds.get(j)).getConnectedAtom(atom);
                if ((container.getMaximumBondOrder(nextAtom) > 1 || Math.abs(nextAtom.getFormalCharge()) >= 1 || nextAtom.getFlag(CDKConstants.ISAROMATIC) || nextAtom.getSymbol().equals("N") || nextAtom.getSymbol().equals("O")) & !nextAtom.getFlag(CDKConstants.VISITED))
                {
                    //System.out.println("BDS> AtomNr:"+container.getAtomNumber(nextAtom)+" maxBondOrder:"+container.getMaximumBondOrder(nextAtom)+" Aromatic:"+nextAtom.getFlag(CDKConstants.ISAROMATIC)+" FormalCharge:"+nextAtom.getFormalCharge()+" Charge:"+nextAtom.getCharge()+" Flag:"+nextAtom.getFlag(CDKConstants.VISITED));
                    path.addElement(nextAtom);
                    //System.out.println("BreadthFirstSearch is meeting new atom " + (nextAtomNr + 1));
                    nextAtom.setFlag(CDKConstants.VISITED, true);
                    if (container.getConnectedBondsCount(nextAtom) > 1) {
                        newSphere.addElement(nextAtom);
                    }
                } else {
                    nextAtom.setFlag(CDKConstants.VISITED, true);
                }
            }
        }
        if (newSphere.size() > 0) {
            breadthFirstSearch(container, newSphere, path);
        }
    }


    /**
     * Gets the parameterNames attribute of the LargestPiSystemDescriptor object.
     *
     * @return The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "checkAromaticity";
        return params;
    }


    /**
     * Gets the parameterType attribute of the LargestPiSystemDescriptor object.
     *
     * @param name Description of the Parameter
     * @return An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        Object[] paramTypes = new Object[1];
        paramTypes[0] = new Boolean(true);
        return paramTypes;
    }
}


