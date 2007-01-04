/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.libio.jmol;

import java.util.Hashtable;

import javax.vecmath.Point3d;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter.AtomIterator;
import org.jmol.api.JmolAdapter.BondIterator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Only converts Jmol objects to CDK objects; the CdkJmolAdapter is not used
 * right now.
 *
 * @author        egonw
 * @author        Miguel Howard
 * @cdk.created   2004-04-25
 * @cdk.module    io-jmol
 * @cdk.keyword   adapter, Jmol
 *
 * @cdk.depends   jmolApis.jar
 * @cdk.depends   jmolIO.jar
 */
public class Convertor {

	private IChemObjectBuilder builder = null;
	
    public Convertor(IChemObjectBuilder builder) {
    	this.builder = builder;
    }

    /**
     * Converts a Jmol <i>model</i> to a CDK AtomContainer.
     *
     * @param model A Jmol model as returned by the method ModelAdapter.openBufferedReader()
     */
    public IAtomContainer convert(Object model) throws CDKException {
        IAtomContainer atomContainer = builder.newAtomContainer();
        SmarterJmolAdapter adapter = new SmarterJmolAdapter(null);
        // use this hashtable to map the ModelAdapter Unique IDs to
        // our CDK Atom's
        Hashtable htMapUidsToAtoms = new Hashtable();
        AtomIterator atomIterator = adapter.getAtomIterator(model);
        while (atomIterator.hasNext()) {
            IAtom atom = builder.newAtom(atomIterator.getElementSymbol());
            atom.setPoint3d(
            	new Point3d(
            		atomIterator.getX(),
            		atomIterator.getY(),
            		atomIterator.getZ()
            	)
            );
            htMapUidsToAtoms.put(atomIterator.getUniqueID(), atom);
            atomContainer.addAtom(atom);
        }
        BondIterator bondIterator = adapter.getBondIterator(model);
        while (bondIterator.hasNext()) {
            Object uid1 = bondIterator.getAtomUniqueID1();
            Object uid2 = bondIterator.getAtomUniqueID2();
            int order = bondIterator.getEncodedOrder();
            // now, look up the uids in our atom map.
            IAtom atom1 = (IAtom)htMapUidsToAtoms.get(uid1);
            IAtom atom2 = (IAtom)htMapUidsToAtoms.get(uid2);
            IBond bond = builder.newBond(atom1, atom2, (double)order);
            atomContainer.addBond(bond);
        }
        return atomContainer;
    };

    /**
     * Empty stub to convert a CDK object into a Jmol object.
     * 
     * @param container
     * @return null
     * 
     * @deprecated Use a Jmol ModelAdapter instead.
     */
    public Object convert(IAtomContainer container) {
        // I need something like the CdkModelAdapter from Jmol here
        return null;
    }
}

