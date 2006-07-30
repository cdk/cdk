package org.openscience.cdk.reaction;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.ISetOfReactions;

/**
 * Classes that implement this interface are Reactions types.
 *
 * @cdk.module reaction
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
     * Returns the names of the parameters for this reaction. 
     *
     * @return An array of String containing the names of the paraneters 
     * that this reaction can accept
     */
    public String[] getParameterNames();
    /** 
     * Returns a class matching that of the parameter with the given name.
     *
     * @param name The name of the parameter whose type is requested
     * @return An Object of the class corresponding to the parameter with the supplied name
     */
    public Object getParameterType(String name);
    
    /** 
     * Sets the parameters for this reaction. 
     *
     * Must be done before calling
     * calculate as the parameters influence the calculation outcome.
     *
     * @param params An array of Object containing the parameters for this reaction
     * @throws CDKException if invalid number of type of parameters are passed to it
     * 
     * @see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException;
    
    /** 
     * Returns the current parameter values.
     *
     * @return An array of Object containing the parameter values
     * @see #setParameters
     * */
    public Object[] getParameters();
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
    public ISetOfReactions initiate(IMoleculeSet reactants, IMoleculeSet agents) throws CDKException;

}
