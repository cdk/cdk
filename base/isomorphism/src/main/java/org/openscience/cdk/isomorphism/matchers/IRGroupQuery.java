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

import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * Interface definition for Rgroup query classes. These must provide a root
 * structure, root attachment points and Rgroup definitions.
 *
 * @cdk.module  isomorphism
 * @cdk.githash
 * @cdk.keyword Rgroup
 * @cdk.keyword R group
 * @cdk.keyword R-group
 * @author Mark Rijnbeek
 */
public interface IRGroupQuery extends IChemObject {

    /**
     * Setter for the root structure of this R-Group.
     * @see #getRootStructure
     * @param rootStructure the root structure (or scaffold) container
     *
     */
    public void setRootStructure(IAtomContainer rootStructure);

    /**
     * Getter for the root structure of this R-Group.
     * @see #setRootStructure
     * @return the root structure (or scaffold) container
     */
    public IAtomContainer getRootStructure();

    /**
     * Setter for root attachment points = bonds that connect R pseudo-atoms to the scaffold.
     * @see #getRootAttachmentPoints()
     * @param rootAttachmentPoints Map with per R-group pseudo atom another map with an Integer and an IBond, the integer indicating 1st or 2nd attachment.
     */
    public void setRootAttachmentPoints(Map<IAtom, Map<Integer, IBond>> rootAttachmentPoints);

    /**
     * Getter for root attachment points = bonds that connect R pseudo-atoms to the scaffold.
     * @see #setRootAttachmentPoints(Map)
     * @return Map with per R-group pseudo atom another map with an Integer and an IBond, the integer indicating 1st or 2nd attachment.
     */
    public Map<IAtom, Map<Integer, IBond>> getRootAttachmentPoints();

    /**
     * Setter for the R-group definitions (substituents).
     * @see #getRGroupDefinitions
     * @param rGroupDefinitions map with an Integer and an RGroupList (substituent list), the Integer being the R-Group number (1..32).
     */
    public void setRGroupDefinitions(Map<Integer, RGroupList> rGroupDefinitions);

    /**
     * Getter for the R-group definitions (substituents).
     * @see #setRGroupDefinitions
     * @return rGroupDefinitions Map with an Integer and an RGroupList (substituent list), the Integer being the R-Group number (1..32).
     */
    public Map<Integer, RGroupList> getRGroupDefinitions();

    /**
     * Return the total number of atom containers (count the root plus all substituents).
     * @return count.
     */
    public int getAtomContainerCount();

    /**
     * Return all the substituent atom containers, in other words the atom containers
     * defined in this RGroupQuery except for the root structure.
     * @return list with all substituents
     */
    public List<IAtomContainer> getSubstituents();

    /**
     * Checks validity of the RGroupQuery.
     * Each distinct R# in the root must have a
     * a corresponding {@link RGroupList} definition.<br>
     * In file terms: $RGP blocks must be defined for each R-group number.
     * @return true when valid
     */
    public boolean areSubstituentsDefined();

    /**
     * Checks validity of RGroupQuery.
     * Each {@link RGroupList} definition must have one or more corresponding
     * R# atoms in the root block.
     * @return true when valid
     */
    public boolean areRootAtomsDefined();

    /**
     * Produces all combinations of the root structure (scaffold) with the R-groups
     * substituted in valid ways, using each R-group's definitions and conditions.
     * @return all valid combinations of the root structure (scaffold) with the
     *         R-groups substituted.
     * @throws Exception
     */
    public List<IAtomContainer> getAllConfigurations() throws Exception;

}
