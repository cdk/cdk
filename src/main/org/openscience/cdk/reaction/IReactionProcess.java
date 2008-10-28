/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.reaction;

import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;

/**
 * Classes that implement this interface are Reactions types.
 *
 * @author      Miguel Rojas
 * @cdk.module  reaction
 * @cdk.svnrev  $Revision$
 */
public interface IReactionProcess {
	
	/**
	 * Returns a <code>Map</code> which specifies which reaction
	 * is implemented by this class. 
	 *
	 * These fields are used in the map:
	 * <ul>
	 * <li>Specification-Reference: refers to an entry in a unique dictionary or web page
	 * <li>Implementation-Title: anything
	 * <li>Implementation-Identifier: a unique identifier for this version of
	 *  this class
	 * <li>Implementation-Vendor: CDK, JOELib, or anything else
	 * </ul>
	 *
	 * @return An object containing the reaction specification
	 */
    public ReactionSpecification getSpecification();
    
    /** 
     * Sets the parameters for this reaction. 
     *
     * Must be done before calling
     * calculate as the parameters influence the calculation outcome.
     *
     * @param params A List of Objects containing the parameters for this reaction. 
     * 				 The key must be included into the Dictionary reacton-processes
     * @throws CDKException if invalid number of type of parameters are passed to it
     * 
     * @see #getParameterList
     */
    public void setParameterList(List<IParameterReact> params) throws CDKException;
    
    /** 
     * Returns the current parameter values.
     *
     * @return A List of Object containing the name and the type of the parameter
     * @see #setParameterList
     * */
    public List<IParameterReact> getParameterList();
    
    /** 
     * Initiates the process for the given Reaction.
     * 
     * Optionally, parameters may be set which can affect the course of the process.
     *
     * @param reactants   An {@link IMoleculeSet} for which this process should be initiate.
     * @param agents      An {@link IMoleculeSet} for which this process should be initiate.
     * 
     * @throws CDKException if an error occurs during the reaction process. 
     * See documentation for individual reaction processes
     */
    public IReactionSet initiate(IMoleculeSet reactants, IMoleculeSet agents) throws CDKException;

    /**
     * Return the IParameterReact if it exists given the class.
     * 
     * @param paramClass The class
     * @return           The IParameterReact
     */
    public IParameterReact getParameterClass(Class<?> paramClass);
    
}
