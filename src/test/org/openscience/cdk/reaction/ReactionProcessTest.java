/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2008  Miguel Rojas <miguelrojasch@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.reaction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.dict.Dictionary;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.dict.EntryReact;
import org.openscience.cdk.exception.CDKException;

/**
 * Tests for IReactionProcess implementations.
 *
 * @cdk.module test-reaction
 */
public abstract class ReactionProcessTest extends NewCDKTestCase {
	
	private static IReactionProcess reaction;
	private static Dictionary dictionary;
	private static String entryString;
	
	/**
	 * Set the IReactionProcess to analyzed
	 * 
	 * @param descriptorClass   The IReactionProcess class
	 * @throws Exception
	 */
	public static void setReaction(Class<?> reactionClass) throws Exception {
		if(ReactionProcessTest.dictionary == null)
			ReactionProcessTest.dictionary = openingDictionary();
		
		Object reaction = (Object)reactionClass.newInstance();
		if (!(reaction instanceof IReactionProcess)) {
			throw new CDKException("The passed reaction class must be a IReactionProcess");
		}
		ReactionProcessTest.reaction = (IReactionProcess)reaction;
		ReactionProcessTest.entryString = "";
		
		
	}
	
	/**
	 * Open the Dictionary OWLReact.
	 * 
	 * @return The dictionary reaction-processes
	 */
	private static Dictionary openingDictionary() {
		DictionaryDatabase db = new DictionaryDatabase();
    	Dictionary dict = db.getDictionary("reaction-processes");
		return dict;
	}

	/**
	 * Makes sure that the extending class has set the super.descriptor.
	 * Each extending class should have this bit of code (JUnit3 formalism):
	 * <pre>
	 * public void setUp() {
	 *   // Pass a Class, not an Object!
	 *   setDescriptor(SomeDescriptor.class);
	 * }
	 * 
	 * <p>The unit tests in the extending class may use this instance, but
	 * are not required.
	 * 
	 * </pre>
	 */
	@Test public void testHasSetSuperDotDescriptor() {
		Assert.assertNotNull("The extending class must set the super.descriptor in its setUp() method.", reaction);    	
	}
	
	/**
	 * Test if this entry has a definition schema in the Dictionary.
	 * 
	 * @throws Exception
	 */
	@Test public void testGetEntryDefinition() throws Exception {
    	
		ReactionProcessTest.entryString = reaction.getSpecification().getSpecificationReference();
		ReactionProcessTest.entryString = ReactionProcessTest.entryString.substring(ReactionProcessTest.entryString.indexOf("#")+1, ReactionProcessTest.entryString.length());
    	
    	EntryReact entry = (EntryReact) dictionary.getEntry(ReactionProcessTest.entryString.toLowerCase());
    	
    	Assert.assertNotNull(
    			"The definition entry for ["+entryString+"] must not be null.",
    			entry.getDefinition());
    	
    }   
	/**
	 * Checks if the parameterization key is consistent.
	 * 
	 * @throws Exception 
	 */
	@Test public void testGetParameters() throws Exception {
        HashMap<String,Object> paramObj = reaction.getParameters();
        
        ReactionProcessTest.entryString = reaction.getSpecification().getSpecificationReference();
		ReactionProcessTest.entryString = ReactionProcessTest.entryString.substring(ReactionProcessTest.entryString.indexOf("#")+1, ReactionProcessTest.entryString.length());
    	
		EntryReact entry = (EntryReact) dictionary.getEntry(ReactionProcessTest.entryString.toLowerCase());
        HashMap<String, String> paramDic = entry.getParameters();
        
        Assert.assertSame(
    			"The parameters entry for ["+entryString+"]  must contain the same lenght as the reaction object.",
    			paramObj.size(),paramDic.size());
	}
    /**
	 * Checks if the parameterization key is consistent.
	 * 
	 * @throws Exception 
	 */
	@Test public void testGetParameterKeyDict() throws Exception {
        HashMap<String,Object> paramObj = reaction.getParameters();
        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
        HashMap<String, String> paramDic = entry.getParameters();
        
        Set<String> set= paramDic.keySet(); 
	    Iterator<String> iter = set.iterator(); 
	    while(iter.hasNext()){  
	       String key = iter.next();
	       Assert.assertTrue("The key "+key+" doesn't exist into the IReactionProcess. ",
	    		   paramObj.containsKey(key));
        }
    }
	/**
	 * Checks if the parameterization key is consistent.
	 * 
	 * @throws Exception 
	 */
	@Test public void testGetParameterKeyReact() throws Exception {
        HashMap<String,Object> paramObj = reaction.getParameters();
        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
        HashMap<String, String> paramDic = entry.getParameters();
        
        Set<String> set= paramObj.keySet(); 
	    Iterator<String> iter = set.iterator(); 
	    while(iter.hasNext()){  
	       String key = iter.next();
	       Assert.assertTrue("The key "+key+" doesn't exist into the Dictionary. ",
	    		   paramDic.containsKey(key));
        }
    }
    
//	/**
//	 * Checks if the parameterization type is consistent.
//	 * 
//	 * @throws Exception 
//	 */
//	@Test public void testGetParameterObject() throws Exception {
//
//		 HashMap<String,Object> paramObj = reaction.getParameters();
//         EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
//         HashMap<String, String> paramDic = entry.getParameters();
//        
//         Set<String> set= paramObj.keySet(); 
//	     Iterator<String> iter = set.iterator(); 
//	     while(iter.hasNext()){  
//	        String key = iter.next();
//	        Object valueDict = paramDic.get(key); 
//	        Object valueObj = paramObj.get(key);
//	        Assert.assertSame(
//	    			"The parameters entry for ["+entryString+"]  must contain the same lenght as the reaction object.",
//	    			valueDict,valueObj);
//         }
//    }
	
	/**
	 * Test the specification of the IReactionProcess.
	 * 
	 */
	@Test public void testGetSpecification() {
    	ReactionSpecification spec = reaction.getSpecification();
    	Assert.assertNotNull(
    		"The descriptor specification returned must not be null.",
    		spec
    	);

    	Assert.assertNotNull(
    		"The specification identifier must not be null.",
    		spec.getImplementationIdentifier()
    	);
    	Assert.assertNotSame(
       		"The specification identifier must not be empty.",
       		0, spec.getImplementationIdentifier().length()
       	);

    	Assert.assertNotNull(
       		"The specification title must not be null.",
       		spec.getImplementationTitle()
    	);
    	Assert.assertNotSame(
    		"The specification title must not be empty.",
    		0, spec.getImplementationTitle().length()
    	);

    	Assert.assertNotNull(
       		"The specification vendor must not be null.",
       		spec.getImplementationVendor()
    	);
    	Assert.assertNotSame(
    		"The specification vendor must not be empty.",
    		0, spec.getImplementationVendor().length()
    	);

    	Assert.assertNotNull(
       		"The specification reference must not be null.",
       		spec.getSpecificationReference()
    	);
    	Assert.assertNotSame(
    		"The specification reference must not be empty.",
    		0, spec.getSpecificationReference().length()
    	);
    }
    
	@Test public void testSetParameters_arrayObject() throws Exception {
    	HashMap<String,Object> defaultParams = reaction.getParameters();
    	reaction.setParameters(defaultParams);
    }    
	
	/**
	 * Test if the reaction process is contained in the Dictionary as a entry.
	 * 
	 * @throws Exception
	 */
	@Test public void testGetDictionaryEntry() throws Exception {
    	
    	EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
    	Assert.assertNotNull(
    			"The Entry ["+entryString+"] doesn't exist in OWL Dictionary.",
    			entry);
    	
    }    
	
	/**
	 * Test if this entry has a definition schema in the Dictionary.
	 * 
	 * @throws Exception
	 */
	@Test public void testGetEntryDescription() throws Exception {
    	
		EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
    	
    	Assert.assertNotNull(
    			"The description entry for ["+entryString+"] must not be null.",
    			entry.getDescription());
    }  
	
	/**
	 * Test if this entry has at least one representation schema in the Dictionary.
	 * 
	 * @throws Exception
	 */
	@Test public void testGetEntryRepresentation() throws Exception {
    	
    	EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
    	
    	Assert.assertNotSame(
    			"The representation entry for ["+entryString+"]  must contain at least one representation.",
    			0,entry.getRepresentations().size());
    }  
	
}
