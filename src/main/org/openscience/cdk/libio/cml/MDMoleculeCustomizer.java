/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2007  Ola Spjuth <ospjuth@users.sf.net>
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
 */
package org.openscience.cdk.libio.cml;

import java.util.Iterator;

import nu.xom.Attribute;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.libio.md.ChargeGroup;
import org.openscience.cdk.libio.md.MDMolecule;
import org.openscience.cdk.libio.md.Residue;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;

/**
 * Customize persistence of MDMolecule by adding support for residues and charge groups.
 * 
 * @author ola
 * @cdk.module libiomd
 * @cdk.svnrev  $Revision$
 * 
 * @cdk.set       libio-cml-customizers
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
    	if (!(nodeToAdd instanceof CMLMolecule))
    		throw new CDKException("NodeToAdd must be of type nu.xom.Element!");

    	//The nodeToAdd
    	CMLMolecule molToCustomize = (CMLMolecule)nodeToAdd;

    	if ((molecule instanceof MDMolecule)){
        	MDMolecule mdmol = (MDMolecule) molecule;
        	molToCustomize.setConvention("md:mdMolecule");
        	molToCustomize.addNamespaceDeclaration("md", "http://www.bioclipse.net/mdmolecule/");

    		//Residues
        	if (mdmol.getResidues().size()>0){
            	Iterator it=mdmol.getResidues().iterator();
            	while (it.hasNext()){
            		Residue residue=(Residue) it.next();
            		int number=residue.getNumber();

                    CMLMolecule resMol = new CMLMolecule();
            		resMol.setDictRef("md:residue");
            		resMol.setTitle(residue.getName());

            		//Append resNo
            		CMLScalar residueNumber=new CMLScalar(number);
            		residueNumber.addAttribute(new Attribute("dictRef", "md:resNumber"));
            		resMol.addScalar(residueNumber);

            		// prefix for residue atom id
            		String rprefix = "r" + number;
            		//Append atoms
            		CMLAtomArray ar=new CMLAtomArray();
            		for (int i=0; i<residue.getAtomCount();i++){
            			CMLAtom cmlAtom=new CMLAtom();
//            			System.out.println("atom ID: "+ residue.getAtom(i).getID());
//            			cmlAtom.addAttribute(new Attribute("ref", residue.getAtom(i).getID()));
            			// the next thing is better, but throws an exception
            			// 
            			// setRef to keep consistent usage
            			// setId to satisfy Jumbo 54. need for all atoms to have id
            			cmlAtom.setRef(residue.getAtom(i).getID());
            			cmlAtom.setId(rprefix + "_" + residue.getAtom(i).getID());
            			ar.addAtom(cmlAtom);
            		}
            		resMol.addAtomArray(ar);
            		
            		molToCustomize.appendChild(resMol);
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

              		//Append chgrpNo
            		CMLScalar cgNo=new CMLScalar(number);
            		cgNo.addAttribute(new Attribute("dictRef", "md:cgNumber"));
            		cgMol.appendChild(cgNo);
 
            		// prefix for residue atom id
            		String cprefix = "cg" + number;

            		//Append atoms from chargeGroup as it is an AC
            		CMLAtomArray ar=new CMLAtomArray();
            		for (int i=0; i<chargeGroup.getAtomCount();i++){
            			CMLAtom cmlAtom=new CMLAtom();
            			// setRef to keep consistent usage
            			// setId to satisfy Jumbo 5.4 need for all atoms to have id
            			cmlAtom.setRef(chargeGroup.getAtom(i).getID());
            			cmlAtom.setId(cprefix + "_" + chargeGroup.getAtom(i).getID());

                		//Append switching atom?
            			if (chargeGroup.getAtom(i).equals(chargeGroup.getSwitchingAtom())) {
            				CMLScalar scalar = new CMLScalar();
            				scalar.setDictRef("md:switchingAtom");
            				cmlAtom.addScalar(scalar);
            			}
            			ar.addAtom(cmlAtom);
            		}
            		cgMol.addAtomArray(ar);
            		

            		molToCustomize.appendChild(cgMol);
            	}
        	}
    	}
    }
  	
}
