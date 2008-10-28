/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008 Miguel Rojas <miguelrojasch@users.sf.net>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.dict.Dictionary;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.dict.EntryReact;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.tools.LoggingTool;

/**
 * <p>The base class for all chemical reactions objects in this cdk. 
 * It provides methods for adding parameters</p>
 * 
 * @author         Miguel Rojas
 * 
 * @cdk.created    2008-02-01
 * @cdk.module     reaction
 * @cdk.set        reaction-types
 * 
 */
@TestClass("org.openscience.cdk.reaction.ReactionEngineTest")
public class ReactionEngine{
	private static LoggingTool logger = new LoggingTool(ReactionEngine.class);

	private Dictionary dictionary;
	public HashMap<String, Object> paramsMap;

	public IReactionMechanism mechanism;

	public List<IParameterReact> paramsMap2;
	/**
	 * Constructor of the ReactionEngine object.
	 */
	public ReactionEngine(){
		
		try {
			IReactionProcess reaction = (IReactionProcess) this;
	    	EntryReact entry = initiateDictionary("reaction-processes",(IReactionProcess)reaction);
	    	initiateParameterMap2(entry);
	    	reaction.setParameterList(getParameterList());
	    	extractMechanism(entry);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
    }
	/**
	 * Extract the mechanism necessary for this reaction.
	 * 
	 * @param entry  The EntryReact object
	 */
	private void extractMechanism(EntryReact entry) {
		String mechanismName = "org.openscience.cdk.reaction.mechanism."+entry.getMechanism();
		try {
            mechanism = (IReactionMechanism) this.getClass().getClassLoader().loadClass(mechanismName).newInstance();
            logger.info("Loaded mechanism: ", mechanismName);
        } catch (ClassNotFoundException exception) {
            logger.error("Could not find this IReactionMechanism: ", mechanismName);
            logger.debug(exception);
        } catch (Exception exception) {
            logger.error("Could not load this IReactionMechanism: ", mechanismName);
            logger.debug(exception);
        }
	}
	/**
	 * Open the Dictionary OWLReact.
	 * 
	 * @param nameDict  Name of the Dictionary
	 * @param reaction  The IReactionProcess
	 * @return          The entry for this reaction
	 */
	private EntryReact initiateDictionary(String nameDict, IReactionProcess reaction) {
		DictionaryDatabase db = new DictionaryDatabase();
		dictionary = db.getDictionary(nameDict);
    	String entryString = reaction.getSpecification().getSpecificationReference();
		entryString = entryString.substring(entryString.indexOf("#")+1, entryString.length());
    	
    	return (EntryReact) dictionary.getEntry(entryString.toLowerCase());
	}
	/**
	 * Creates a map with the name and type of the parameters.
	 */
	private void initiateParameterMap2(EntryReact entry){
		List<List<String>> paramDic = entry.getParameterClass();

		paramsMap2 = new ArrayList<IParameterReact>();
		for(Iterator<List<String>> it = paramDic.iterator(); it.hasNext();){
			List<String> param = it.next();
			String paramName = "org.openscience.cdk.reaction.type.parameters."+param.get(0);
			try {
				IParameterReact ipc = (IParameterReact) this.getClass().getClassLoader().loadClass(paramName).newInstance();
			    ipc.setParameter(Boolean.parseBoolean(param.get(1)));
	            ipc.setValue(param.get(2));
	            
				logger.info("Loaded parameter class: ", paramName);
	            paramsMap2.add(ipc);
	    	} catch (ClassNotFoundException exception) {
	            logger.error("Could not find this IParameterReact: ", paramName);
	            logger.debug(exception);
	        } catch (Exception exception) {
	            logger.error("Could not load this IParameterReact: ", paramName);
	            logger.debug(exception);
	        }
		}
	}
	/** 
     * Returns the current parameter Map for this reaction.
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
    @TestMethod(value="testSetParameterList_List")
    public void setParameterList(List<IParameterReact> params) throws CDKException{
    	paramsMap2 = params;
    }
    
    /** 
     * Returns the current parameter values.
     *
     * @return A List of Object containing the name and the type of the parameter
     * @see #setParameterList
     * */
    @TestMethod(value="testGetParameterList")
    public List<IParameterReact> getParameterList(){
    	return paramsMap2;
    }
    /**
     * Return the IParameterReact if it exists given the class.
     * 
     * @param paramClass The class
     * @return           The IParameterReact
     */
    @TestMethod(value="testGetParameterClass_Class")
    public IParameterReact getParameterClass(Class<?> paramClass){
    	for(Iterator<IParameterReact> it = paramsMap2.iterator(); it.hasNext();){
    		IParameterReact ipr = it.next();
    		if(ipr.getClass().equals(paramClass))
    			return ipr;
    	}
    	
    	return null;
    }
    
}
