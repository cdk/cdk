/* $Revision: 7636 $ $Author: ospjuth $ $Date: 2007-01-04 17:46:10 +0000 (Thu, 04 Jan 2007) $
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
 * 
 * @cdk.set       libio-cml-customizers
 */

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

            		//FIXME: persist the Residue
            		CMLMolecule resMol = new CMLMolecule();
            		resMol.setDictRef("md:residue");
            		resMol.setTitle(residue.getName());
            		// etc: add number, refs to atoms etc
  
                    //FIXME: add the <molecule> child to root molecule
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
  
                    //FIXME: add the <molecule> child to root molecule
            		molToCustomize.appendChild(cgMol);
            	}
        	}
    	}
    }
  	
}
