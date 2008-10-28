/* $RCSfile$
 * $Author: rajarshi $
 * $Date: 2007-10-22 02:38:43 +0200 (Mon, 22 Oct 2007) $
 * $Revision: 9172 $
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Entry in a Dictionary for reactions.
 * 
 * @author       Miguel Rojas <miguelrojasch@users.sf.net>
 * @cdk.created  2008-01-01
 * @cdk.keyword  dictionary
 * @cdk.module   dict
 *
 * @see          Dictionary
 */
public class EntryReact extends Entry{
    
    private List<String> reactionInfo;
	private List<String> representations;
	private HashMap<String,String> parameters;
	private List<String> parametersValue;
	private List<String> reactionExample;
	private List<List<String>> parameterClass;
	private String mechanism;
    
    /**
     * Constructor of the EntryReact.
     * 
     * @param identifier The ID value
     * @param term
     */
    public EntryReact(String identifier, String term) {
        super(identifier, term);
        this.representations = new ArrayList<String>();
        this.parameters = new HashMap<String,String>();
        this.parametersValue = new ArrayList<String>();
        this.reactionExample = new ArrayList<String>();
        this.parameterClass = new ArrayList<List<String>>();
    }
    
    /**
     * Constructor of the EntryReact.
     * 
     * @param identifier The ID value
     */
    public EntryReact(String identifier) {
    	this(identifier, "");
    }
    
    public void setReactionMetadata(String metadata) {
        this.reactionInfo.add( metadata );
    }
    
    public List<String> getReactionMetadata() {
        return this.reactionInfo;
    }
    
    /**
     * Set the representation of the reaction.
     * 
     * @param contentRepr The representation of the reaction as String
     */
    public void setRepresentation(String contentRepr) {
    	this.representations.add(contentRepr);
    }
    
    /**
     * Get the Representation of the reaction.
     * 
     * @return A List of String of the reaction representations 
     */
    public List<String> getRepresentations() {
    	return this.representations;
    }
    
    /**
     * Set the parameters of the reaction.
     * 
     * @param nameParam The parameter names of the reaction as String
     * @param typeParam The parameter types of the reaction as String
     * @param value     The value default of the parameter
     */
    public void setParameters(String nameParam, String typeParam, String value) {
    	this.parameters.put(nameParam, typeParam);
    	this.parametersValue.add(value);
    }
    
    /**
     * Get the parameters of the reaction.
     * 
     * @return A HashMap with the parameters 
     */
    public HashMap<String,String> getParameters() {
    	return this.parameters;
    }

    /**
     * Get the IParameterReact's of the reaction.
     * 
     * @return A String List with the parameter class 
     */
    public List<List<String>> getParameterClass() {
    	return this.parameterClass;
    }
    /**
     * Add a IParameterReact's of the reaction.
     * 
     * @param A String List containing the information about this parameter.
     */
    public void addParameter(List<String> param) {
    	
    	this.parameterClass.add(param);
    }
    /**
     * Get the parameter value of the reaction.
     * 
     * @return A List with the parameter value 
     */
    public List<String> getParameterValue() {
    	return this.parametersValue;
    }

    /**
     * Set the mechanism of this reaction.
     * 
     * @param mechani The mechanism
     */
	public void setMechanism(String mechani) {
		this.mechanism = mechani; 
	}
	/**
     * Get the mechanism of this reaction.
     * 
     * @return The mechanism
     */
	public String getMechanism() {
		return this.mechanism; 
	}

	/**
	 * add a example for this reaction.
	 * 
	 * @param xml A reaction in XML scheme
	 */
	public void addExampleReaction(String xml) {
		this.reactionExample.add(xml);
		
	}

	/**
	 * add a example for this reaction.
	 * 
	 * @param xml A List of reactions in XML scheme
	 */
	public List<String> getExampleReactions() {
		return this.reactionExample;
		
	}
}
