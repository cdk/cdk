package org.openscience.cdk.libio.cml;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import nu.xom.Attribute;
import nu.xom.Element;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.libio.md.ChargeGroup;
import org.openscience.cdk.libio.md.MDMolecule;
import org.openscience.cdk.libio.md.Residue;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLScalar;

/**
 * Customize persistence of MDMolecule by adding support for residues and chargegroups
 * 
 * @author ola
 *
 */
public class MDMoleculeCustomizer implements ICMLCustomizer {

    private final static String MD_NAMESPACE = "md";
    private final static String MD_URI = "http://www.bioclipse.net/md";

    /**
     * No customization for bonds
     */
	public void customize(IBond bond, Object nodeToAdd) throws Exception {
		//customizeIChemObject(bond, nodeToAdd);
	}
	
	/**
	 * Customize Atom
	 */
    public void customize(IAtom atom, Object nodeToAdd) throws Exception {
    	customizeIChemObject(atom, nodeToAdd);
    }
    
	/**
	 * Customize Molecule
	 */
    public void customize(IAtomContainer molecule, Object nodeToAdd) throws Exception {
    	customizeIChemObject(molecule, nodeToAdd);
    }


    private void customizeIChemObject(IChemObject object, Object nodeToAdd) throws Exception {
    	if (!(nodeToAdd instanceof Element))
    		throw new CDKException("NodeToAdd must be of type nu.xom.Element!");

    	//The nodeToAdd
    	Element element = (Element)nodeToAdd;

    	//List of properties
    	Element propList = new CMLPropertyList();

        //Set up up the metadata list
        Element metadataList = new CMLMetadataList();
        metadataList.addNamespaceDeclaration(MD_NAMESPACE, MD_URI);

        //Create a Property
    	Element property = new CMLProperty();
        property.appendChild(metadataList);

        //Append the Property with the metadata to the PropertyList
        propList.appendChild(property);

    	if ((object instanceof MDMolecule)){
        	MDMolecule mdmol = (MDMolecule) object;

        	//Residues
        	if (mdmol.getResidues().size()>0){
            	Iterator<Residue> it=mdmol.getResidues().iterator();
            	while (it.hasNext()){
            		Residue residue=it.next();
            		int number=residue.getNumber();
            		String name=residue.getName();

            		//FIXME: persist the Residue
  
            	}
        	}

        	//Chargegroups
        	if (mdmol.getChargeGroups().size()>0){
            	Iterator<ChargeGroup> it=mdmol.getChargeGroups().iterator();
            	while (it.hasNext()){
            		ChargeGroup chargeGroup=it.next();
            		int number=chargeGroup.getNumber();

            		//FIXME: persist the ChargeGroup
  
            	}
        	}


        	//Add the list of properties to the element
            if (propList != null) {
                element.appendChild(propList);
            }

    	}

    	if ((object instanceof Residue)){
    		//TODO
    	}

    	if ((object instanceof ChargeGroup)){
    		//TODO
    	}

    	
  
    }
  
	
}
