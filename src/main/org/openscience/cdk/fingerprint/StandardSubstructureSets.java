package org.openscience.cdk.fingerprint;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * Default sets of atom containers aimed for use with the substructure
 * 
 * @author egonw
 * 
 * @cdk.module experimental
 * @cdk.svnrev  $Revision$
 */
public class StandardSubstructureSets {

	private static IAtomContainerSet functionalGroupSubstructureSet = null;
	
	/**
	 * @return A set of the functional groups.
     * @throws Exception if there is an error parsing SMILES for the functional groups
	 */
	public static IAtomContainerSet getFunctionalGroupSubstructureSet() throws Exception {
		if (functionalGroupSubstructureSet == null) {
			functionalGroupSubstructureSet = new org.openscience.cdk.AtomContainerSet();
			
			SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
			String[] groups = {
				"O=CO", // carboxilyc acid 
				"[H]N[H]", // amine
				"O[H]", // hydroxide
				"COC", // alkoxyalkane
				"O=C[H]", // aldehyde
				"O=S(=O)O", // sulfonate
				"O=P(=O)O" // phosphate
		    };
            for (String group : groups) {
                functionalGroupSubstructureSet.addAtomContainer(
                        parser.parseSmiles(group)
                );
            }
		}
		
		return functionalGroupSubstructureSet;
	}
	
}
