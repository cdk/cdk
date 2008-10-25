/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
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
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.List;

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
 * @cdk.module qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:largestPiSystem
 */


@TestClass("org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptorTest")
public class LargestPiSystemDescriptor implements IMolecularDescriptor {

    private boolean checkAromaticity = false;
    private static final String[] names = {"nAtomP"};


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
        checkAromaticity = (Boolean) params[0];
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
        params[0] = checkAromaticity;
        return params;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        return new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), new IntegerResult((int) Double.NaN), getDescriptorNames(), e);
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
     * @see #setParameters
     */
    public DescriptorValue calculate(IAtomContainer container) {
        boolean[] originalFlag4 = new boolean[container.getAtomCount()];
        for (int i=0; i<originalFlag4.length; i++) {
            originalFlag4[i] = container.getAtom(i).getFlag(4);
        }
        if (checkAromaticity) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
                CDKHueckelAromaticityDetector.detectAromaticity(container);
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }
        }
        int largestPiSystemAtomsCount = 0;
        List<IAtom> startSphere;
        List<IAtom> path;
        //Set all VisitedFlags to False
        for (int i = 0; i < container.getAtomCount(); i++) {
            container.getAtom(i).setFlag(CDKConstants.VISITED, false);
        }
        //logger.debug("Set all atoms to Visited False");
        for (int i = 0; i < container.getAtomCount(); i++) {
            //Possible pi System double bond or triple bond, charge, N or O (free electron pair)
            //logger.debug("atom:"+i+" maxBondOrder:"+container.getMaximumBondOrder(atoms[i])+" Aromatic:"+atoms[i].getFlag(CDKConstants.ISAROMATIC)+" FormalCharge:"+atoms[i].getFormalCharge()+" Charge:"+atoms[i].getCharge()+" Flag:"+atoms[i].getFlag(CDKConstants.VISITED));
            if ((container.getMaximumBondOrder(container.getAtom(i)) != IBond.Order.SINGLE ||
            	Math.abs(container.getAtom(i).getFormalCharge()) >= 1 ||
            	container.getAtom(i).getFlag(CDKConstants.ISAROMATIC) || 
            	container.getAtom(i).getSymbol().equals("N") || 
            	container.getAtom(i).getSymbol().equals("O")) & 
            	!container.getAtom(i).getFlag(CDKConstants.VISITED))
            {
                //logger.debug("...... -> Accepted");
                startSphere = new ArrayList<IAtom>();
                path = new ArrayList<IAtom>();
                startSphere.add(container.getAtom(i));
                try {
                    breadthFirstSearch(container, startSphere, path);
                } catch (CDKException e) {
                    return getDummyDescriptorValue(e);
                }
                if (path.size() > largestPiSystemAtomsCount) {
                    largestPiSystemAtomsCount = path.size();
                }
            }

        }
        // restore original flag values
        for (int i=0; i<originalFlag4.length; i++) {
            container.getAtom(i).setFlag(4, originalFlag4[i]);
        }

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new IntegerResult(largestPiSystemAtomsCount),
                getDescriptorNames());
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
        return new IntegerResult(1);
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
     * @param path      An array list which stores the atoms belonging to the pi system
     * @throws org.openscience.cdk.exception.CDKException
     *          Description of the
     *          Exception
     */
    public void breadthFirstSearch(IAtomContainer container, List<IAtom> sphere, List<IAtom> path) throws org.openscience.cdk.exception.CDKException {
        IAtom nextAtom;
       List<IAtom> newSphere = new ArrayList<IAtom>();
        //logger.debug("Start of breadthFirstSearch");
        for (IAtom atom : sphere) {
            //logger.debug("BreadthFirstSearch around atom " + (atomNr + 1));
            List bonds = container.getConnectedBondsList(atom);
            for (Object bond : bonds) {
                nextAtom = ((IBond) bond).getConnectedAtom(atom);
                if ((container.getMaximumBondOrder(nextAtom) != IBond.Order.SINGLE ||
                        Math.abs(nextAtom.getFormalCharge()) >= 1 ||
                        nextAtom.getFlag(CDKConstants.ISAROMATIC) ||
                        nextAtom.getSymbol().equals("N") ||
                        nextAtom.getSymbol().equals("O")) &
                        !nextAtom.getFlag(CDKConstants.VISITED)) {
                    //logger.debug("BDS> AtomNr:"+container.getAtomNumber(nextAtom)+" maxBondOrder:"+container.getMaximumBondOrder(nextAtom)+" Aromatic:"+nextAtom.getFlag(CDKConstants.ISAROMATIC)+" FormalCharge:"+nextAtom.getFormalCharge()+" Charge:"+nextAtom.getCharge()+" Flag:"+nextAtom.getFlag(CDKConstants.VISITED));
                    path.add(nextAtom);
                    //logger.debug("BreadthFirstSearch is meeting new atom " + (nextAtomNr + 1));
                    nextAtom.setFlag(CDKConstants.VISITED, true);
                    if (container.getConnectedBondsCount(nextAtom) > 1) {
                        newSphere.add(nextAtom);
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
        if ("checkAromaticity".equals(name)) return Boolean.FALSE;
    	return null;
    }
}


