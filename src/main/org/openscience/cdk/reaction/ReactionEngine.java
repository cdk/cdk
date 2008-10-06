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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.dict.Dictionary;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.dict.EntryReact;
import org.openscience.cdk.exception.CDKException;

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
    
	private Dictionary dictionary;
	public HashMap<String, Object> paramsMap;
	
	/**
	 * Constructor of the ReactionEngine object.
	 */
	public ReactionEngine(){
		
		try {
			IReactionProcess reaction = (IReactionProcess) this;
	    	EntryReact entry = initiateDictionary("reaction-processes",(IReactionProcess)reaction);
	    	initiateParameterMap(entry);
		} catch (Exception e) {
			e.printStackTrace();
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
	private void initiateParameterMap(EntryReact entry){
		HashMap<String, String> paramDic = entry.getParameters();
		List<String> value = entry.getParameterValue();
		
		paramsMap = new HashMap<String,Object>();
		Set<String> set= paramDic.keySet (  ) ; 
	    Iterator<String> iter = set.iterator (  ) ; 
	    int i=0; 
	    while(iter.hasNext()){  
	       String key = iter.next();
		   if(paramDic.get(key).equals("boolean")){
			   boolean valueB;
			   if(value.get(i).equals("false"))
				   valueB = false;
			   else
				   valueB = true;
			   paramsMap.put(key, new Boolean(valueB));
		   }
	       i++; 
	    }
		
	}
	/** 
     * Returns the current parameter Map for this reaction.
     *
     * @return A HashMap of Object containing the name and the type of the parameter
     * */
    @TestMethod("testGetParameters")
    public HashMap<String,Object> getParameters(){
    	return paramsMap;
    }
    /**
	 *  Sets the parameters attribute of the ElectronImpactNBEReaction object.
	 *
	 * @param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							 should be set before to ionize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 * @exception  CDKException  Description of the Exception
	 */
    @TestMethod("testSetParameters_HashMap")
	public void setParameters(HashMap<String,Object> params) throws CDKException {
		
		if (params.size() != paramsMap.size()) {
			
			throw new CDKException("This IReactionProcess doesn't expect this number "+paramsMap.size()+" of parameters");
		}
		Set<String> set= params.keySet(); 
	    Iterator<String> iter = set.iterator(); 
	    while(iter.hasNext()){  
	       String key = iter.next();
	       if(!paramsMap.containsKey(key))
	    	   throw new CDKException("The key of this parameter doesn't exist into the dictiontary");
	       else
	    	   paramsMap.put(key, params.get(key));
	    }
	}
}
