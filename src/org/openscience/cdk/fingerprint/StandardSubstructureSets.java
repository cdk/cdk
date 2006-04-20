package org.openscience.cdk.fingerprint;

import org.openscience.cdk.interfaces.ISetOfAtomContainers;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * Default sets of atom containers aimed for use with the substructure
 * 
 * @author egonw
 * 
 * @cdk.module experimental
 */
public class StandardSubstructureSets {

	private static ISetOfAtomContainers functionalGroupSubstructureSet = null;
	
	/**
	 * @return A set of the functional groups.
	 */
	public static ISetOfAtomContainers getFunctionalGroupSubstructureSet() throws Exception {
		if (functionalGroupSubstructureSet == null) {
			functionalGroupSubstructureSet = new org.openscience.cdk.SetOfAtomContainers();
			
			SmilesParser parser = new SmilesParser();
			String[] groups = {
				"O=CO", // carboxilyc acid 
				"[H]N[H]", // amine
				"O[H]", // hydroxide
				"COC", // alkoxyalkane
				"O=C[H]", // aldehyde
				"O=S(=O)O", // sulfonate
				"O=P(=O)O" // phosphate
		    };
			for (int i=0; i<groups.length; i++) {
			    functionalGroupSubstructureSet.addAtomContainer(
			    	parser.parseSmiles(groups[i])
			    );
			}
		}
		
		return functionalGroupSubstructureSet;
	}
	
}
