/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.iupac.generator;

import java.util.Enumeration;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.Fragment;

/**
 * Represents the concept of a fragment with free valences.
 * An example use would be a set of templates with amino acid
 * residues in a protein generator.
 *
 * @cdk.module experimental
 *
 * @author     egonw
 * @cdk.created    August 14th 2002
 *
 * @cdk.keyword    fragment
 * @cdk.keyword    free valence
 */
public class FragmentWithAtomicValencies extends Fragment {

    private static final long serialVersionUID = -1762677470500460605L;
    
    private int[] valenciesAt;

    /**
     *  Creates an empty Fragment
     */
    public FragmentWithAtomicValencies() {
        super();
        valenciesAt = new int[20]; // allow for 20 valencies
    }

    /**
     *  Creates an Fragment with the atoms and bonds in AtomContainer.
     *  The atoms in the container are considered to be connected, but
     *  this is not checked.
     *
     *  @param ac   AtomContainer with atoms and bonds to add
     */
    public FragmentWithAtomicValencies(IAtomContainer ac) {
        this();
        this.add(ac);
    }

    /**
     * Add a valency to this Fragment. The valencies are localized
     * on the atoms.
     */
    public void addValencyAtAtom(org.openscience.cdk.interfaces.IAtom a) throws Exception {
//        System.out.println("test");
        int atomNumber = super.getAtomNumber(a);
//        System.out.println("test");
        int valencyIndex = getFreeValences();
//        System.out.println("test");
        valenciesAt[valencyIndex] = atomNumber;
//        System.out.println("test");
        return;
    }

    /**
     * Returns an Enumeration of Atoms.
     */
    public Enumeration getAtomicValences() {
        Vector result = new Vector();
        for (int i=0; i < getFreeValences(); i++) {
            result.add(getAtomAt(valenciesAt[i]));
        }
        return result.elements();
    }

    /**
     * Do not use this method in this class. Use addValencyAtAtom()
     * instead.
     *
     * @deprecated
     */
    public void setFreeValences(int count) {
        // do nothing!
    }
}


