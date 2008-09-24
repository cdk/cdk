/* $Revision$ $Author$ $Date$
 *  
 * Copyright (C) 2007  Egon Willighagen
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
package org.openscience.cdk.tools;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import java.util.Hashtable;
import java.util.Map;

/**
 * Adds implicit hydrogens based on atom type definitions. The class assumes
 * that CDK atom types are already detected. A full code example is:
 * <pre>
 *   IMolecule methane = new Molecule();
 *   IAtom carbon = new Atom("C");
 *   methane.addAtom(carbon);
 *   CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(methane.getBuilder());
 *   Iterator<IAtom> atoms = methane.atoms();
 *   while (atoms.hasNext()) {
 *     IAtom atom = atoms.next();
 *     IAtomType type = matcher.findMatchingAtomType(methane, atom);
 *     AtomTypeManipulator.configure(atom, type);
 *   }
 *   CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(methane.getBuilder());
 *   adder.addImplicitHydrogens(methane);
 * </pre>
 *
 * <p>If you want to add the hydrogens to a specific atom only,
 * use this example:
 * <pre>
 *   IMolecule ethane = new Molecule();
 *   IAtom carbon1 = new Atom("C");
 *   IAtom carbon2 = new Atom("C");
 *   ethane.addAtom(carbon1);
 *   ethane.addAtom(carbon2);
 *   CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(ethane.getBuilder());
 *   IAtomType type = matcher.findMatchingAtomType(ethane, carbon1);
 *   AtomTypeManipulator.configure(carbon1, type);
 *   CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(ethane.getBuilder());
 *   adder.addImplicitHydrogens(ethane, carbon1);
 * </pre>
 * 
 * @author     egonw
 * @cdk.module valencycheck
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.tools.CDKHydrogenAdderTest")
public class CDKHydrogenAdder {

    private AtomTypeFactory atomTypeList;
    private final static String ATOM_TYPE_LIST = "org/openscience/cdk/dict/data/cdk-atom-types.owl";

    private static Map<String,CDKHydrogenAdder> tables = new Hashtable<String,CDKHydrogenAdder>(3);

    private CDKHydrogenAdder(IChemObjectBuilder builder) {
        if (atomTypeList == null)
            atomTypeList = AtomTypeFactory.getInstance(ATOM_TYPE_LIST, builder);
    }


    @TestMethod("testInstance")
    public static CDKHydrogenAdder getInstance(IChemObjectBuilder builder) {
        if (!tables.containsKey(builder.getClass().getName()))
            tables.put(builder.getClass().getName(), new CDKHydrogenAdder(builder));
        return tables.get(builder.getClass().getName());
    }


	/**
	 * Sets implicit hydrogen counts for all atoms in the given IAtomContainer.
	 * 
	 * @param  container The molecule to which H's will be added
	 * @throws CDKException Throws if insufficient information is present
	 */
    @TestMethod("testMethane,testFormaldehyde,testHCN")
    public void addImplicitHydrogens(IAtomContainer container) throws CDKException {
        for (IAtom atom : container.atoms()) {
            addImplicitHydrogens(container, atom);
        }        
	}
	
	/**
	 * Sets the implicit hydrogen count for the indicated IAtom in the given IAtomContainer.
	 * If the atom type is "X", then the atom is assigned zero implicit hydrogens.
	 * 
	 * @param  container  The molecule to which H's will be added
	 * @param  atom         IAtom to set the implicit hydrogen count for
	 * @throws CDKException Throws if insufficient information is present
	 */
    @TestMethod("testImpHByAtom")
    public void addImplicitHydrogens(IAtomContainer container, IAtom atom) throws CDKException {
		if (atom.getAtomTypeName() == null)
			throw new CDKException("IAtom is not typed! " + atom.getSymbol());
		
		if ("X".equals(atom.getAtomTypeName())) {
			atom.setHydrogenCount(0);
			return; 
		}
		
		IAtomType type =  atomTypeList.getAtomType(atom.getAtomTypeName());
		if (type == null)
			throw new CDKException("Atom type is not a recognized CDK atom type: " + atom.getAtomTypeName());
		
		if (type.getFormalNeighbourCount() == CDKConstants.UNSET)
			throw new CDKException("Atom type is too general; cannot decide the number of implicit hydrogen to add for: " + atom.getAtomTypeName());
		
		// very simply counting: each missing explicit neighbor is a missing hydrogen
		atom.setHydrogenCount(
			type.getFormalNeighbourCount() - container.getConnectedAtomsCount(atom)
		);
	}
}
