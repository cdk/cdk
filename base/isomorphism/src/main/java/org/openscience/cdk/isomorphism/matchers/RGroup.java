/*
 * Copyright (C) 2010  Mark Rijnbeek <mark_rynbeek@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may
 * distribute with programs based on this work.
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
 */
package org.openscience.cdk.isomorphism.matchers;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPseudoAtom;

import java.util.List;

/**
 * Represents a single substitute structure in an {@link RGroupList}. <P>
 * The order of attachment points is provided (first and second only, conform
 * RGFile spec). This order is relevant when the structure connects to the root
 * with more than one bond.
 * <P>
 * See also {@link RGroupList} and {@link RGroupQuery}.
 *
 * @cdk.keyword Rgroup
 * @cdk.keyword R group
 * @cdk.keyword R-group
 * @author Mark Rijnbeek
 */
public class RGroup implements IRGroup {

    /**
     * Standard label/title to be used for the root atom container.
     */
    public final static String ROOT_LABEL = "Root structure";

    /**
     * Makes a label/title to be used for a substituent.
     * @param rgroupNum R-Group number (1..32)
     * @return label for substituent, like "R3"
     */
    public static String makeLabel(int rgroupNum) {
        return "(R" + rgroupNum + ")";
    }

    IAtomContainer group;

    private static boolean isAttachmentPoint(IAtom atom, int num) {
        return atom.getAtomicNumber() == IAtom.Wildcard &&
               (atom instanceof IPseudoAtom) &&
               ((IPseudoAtom) atom).getAttachPointNum() == num;
    }

    private IAtom getAttachmentAtom(int num) {
        for (IAtom atom : group.atoms()) {
            if (isAttachmentPoint(atom, num)) {
                List<IAtom> atoms = group.getConnectedAtomsList(atom);
                if (atoms.size() == 1)
                    return atoms.get(0);
                break;
            }
        }
        return null;
    }

    private void setAttachmentAtom(IAtom attachTo, int num) {
        IAtom apo = null;
        IBond bond = null;
        for (IAtom atom : group.atoms()) {
            if (isAttachmentPoint(atom, 1)) {
                apo = atom;
                List<IBond> bonds = group.getConnectedBondsList(atom);
                if (bonds.size() != 1)
                    throw new IllegalArgumentException();
                bond = bonds.get(0);
                break;
            }
        }

        if (apo != null && bond != null) {
            // move exist attachment point
            if (bond.getEnd().equals(apo))
                bond.setAtom(attachTo, 0); // setBegin
            else if (bond.getBegin().equals(apo))
                bond.setAtom(attachTo, 1); // setEnd
            else
                throw new IllegalStateException();
        } else {
            assert apo == null;
            // create a new attachment point + bond
            apo = group.getBuilder().newInstance(IPseudoAtom.class);
            apo.setAtomicNumber(IElement.Wildcard);
            ((IPseudoAtom)apo).setAttachPointNum(num);
            group.addAtom(apo);
            group.newBond(attachTo, apo);
        }
    }

    /**
     * This function is used to set/move the attachment point on an RGroup.
     * It does not set coordinates or adjust hydrogen counts and will modifying
     * the underlying structure add/move bonds and atoms.
     *
     * @param firstAttachmentPoint the atom ato attach to
     * @deprecated use IPseudoAtom with attachNum to explicitly indicate
     *             attachment
     */
    @Deprecated
    public void setFirstAttachmentPoint(IAtom firstAttachmentPoint) {
        setAttachmentAtom(firstAttachmentPoint, 1);
    }

    @Override
    public IAtom getFirstAttachmentPoint() {
        return getAttachmentAtom(1);
    }

    /**
     * This function is used to set/move the attachment point on an RGroup.
     * It does not set coordinates or adjust hydrogen counts and will modifying
     * the underlying structure add/move bonds and atoms.
     *
     * @param secondAttachmentPoint the atom ato attach to
     * @deprecated use IPseudoAtom with attachNum to explicitly indicate
     *             attachment
     */
    @Deprecated
    public void setSecondAttachmentPoint(IAtom secondAttachmentPoint) {
        setAttachmentAtom(secondAttachmentPoint, 2);
    }

    @Override
    public IAtom getSecondAttachmentPoint() {
        return getAttachmentAtom(2);
    }

    public void setGroup(IAtomContainer group) {
        this.group = group;
    }

    @Override
    public IAtomContainer getGroup() {
        return group;
    }
}
