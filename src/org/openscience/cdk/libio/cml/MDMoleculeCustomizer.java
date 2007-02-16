package org.openscience.cdk.libio.cml;

import java.util.Iterator;

import nu.xom.Element;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.libio.md.ChargeGroup;
import org.openscience.cdk.libio.md.MDMolecule;
import org.openscience.cdk.libio.md.Residue;
import org.xmlcml.cml.element.CMLMolecule;

/**
 * Customize persistence of MDMolecule by adding support for residues and chargegroups
 * 
 * @author ola
 *
 */
public class MDMoleculeCustomizer implements ICMLCustomizer {

    /**
     * No customization for bonds
     */
	public void customize(IBond bond, Object nodeToAdd) throws Exception {
		// nothing to do
	}
	
	/**
	 * Customize Atom
	 */
    public void customize(IAtom atom, Object nodeToAdd) throws Exception {
    	// nothing to do
    }
    
	/**
	 * Customize Molecule
	 */
    public void customize(IAtomContainer molecule, Object nodeToAdd) throws Exception {
    	if (!(nodeToAdd instanceof Element))
    		throw new CDKException("NodeToAdd must be of type nu.xom.Element!");

    	//The nodeToAdd
    	Element element = (Element)nodeToAdd;

    	if ((molecule instanceof MDMolecule)){
        	MDMolecule mdmol = (MDMolecule) molecule;

        	//Residues
        	if (mdmol.getResidues().size()>0){
            	Iterator it=mdmol.getResidues().iterator();
            	while (it.hasNext()){
            		Residue residue=(Residue) it.next();
            		int number=residue.getNumber();

            		//FIXME: persist the Residue
            		CMLMolecule resMol = new CMLMolecule();
            		resMol.setDictRef("md:residue");
            		resMol.setTitle(residue.getName());
            		// etc: add number, refs to atoms etc
  
                    //FIXME: add the <molecule> child to root molecule
                    element.appendChild(resMol);
            	}
        	}

        	//Chargegroups
        	if (mdmol.getChargeGroups().size()>0){
            	Iterator it=mdmol.getChargeGroups().iterator();
            	while (it.hasNext()){
            		ChargeGroup chargeGroup=(ChargeGroup) it.next();
            		int number=chargeGroup.getNumber();

            		//FIXME: persist the ChargeGroup
            		CMLMolecule cgMol = new CMLMolecule();
            		cgMol.setDictRef("md:chargeGroup");
            		// etc: add name, refs to atoms etc
  
                    //FIXME: add the <molecule> child to root molecule
                    element.appendChild(cgMol);
            	}
        	}
    	}
    }
  	
}
