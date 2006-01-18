/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 *  This interface defines the API for the implementation of
 *  IUPAC rules in the org.openscience.cdk.iupac.generator
 *  package.
 *
 * @cdk.module experimental
 *
 * @author Egon Willighagen
 */
public interface Rule {

    /** Returns the name of this rule.
     */
    public String getName();

    /** Applies this rule to this molecule.
     * 
     * @return null if this rule was not applicable
     */
    public IUPACNamePart apply(IAtomContainer ac);

    public String localize(String s);

    /**
     * Flag that must be set by a Rule's apply() method.
     *
     * @see #apply(IAtomContainer)
     */
    public final static String NONE_APPLICABLE =
      "org.openscience.cdk.iupac.generator.NONE_APPLICABLE";

    public final static String COMPLETED_FLAG =
      "org.openscience.cdk.iupac.generator.COMPLETED";

    public final static String ATOM_NAMED_FLAG =
      "org.openscience.cdk.iupac.generator.ATOM_NAMED";

    public final static String ATOM_NUMBERED_FLAG =
      "org.openscience.cdk.iupac.generator.ATOM_NUMBERED";

    public final static String ATOM_MUST_BE_NUMBERED_FLAG =
      "org.openscience.cdk.iupac.generator.ATOM_MUST_BE_NUMBERED";

    public final static String ATOM_HAS_VALENCY =
      "org.openscience.cdk.iupac.generator.ATOM_HAS_VALENCY";

    public final static String IUPAC_NAME =
      "org.openscience.cdk.iupac.generator.IUPAC_NAME";

    public final static String ELEMENT_COUNT =
      "org.openscience.cdk.iupac.generator.ELEMENT_COUNT";

    public final static String CARBON_COUNT =
      "org.openscience.cdk.iupac.generator.CARBON_COUNT";

    public final static String HYDROGEN_COUNT =
      "org.openscience.cdk.iupac.generator.HYDROGEN_COUNT";

    public final static String BROMO_COUNT =
      "org.openscience.cdk.iupac.generator.BROMO_COUNT";

    public final static String CHLORO_COUNT =
      "org.openscience.cdk.iupac.generator.CHLORO_COUNT";

    public final static String FLUORO_COUNT =
      "org.openscience.cdk.iupac.generator.FLUORO_COUNT";

}
