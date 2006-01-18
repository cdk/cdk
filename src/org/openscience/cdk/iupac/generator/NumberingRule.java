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
public class NumberingRule implements Rule {

    public final static String ATOMNUMBER =
      "org.openscience.cdk.iupac.generator.ATOM_NUMBER";

    private NamingRule namingRule;

    public NumberingRule() {
        namingRule = null;
    }

    public NumberingRule(NamingRule rule) {
        namingRule = rule;
    }

    public String getName() {
        return "Dummy Rule";
    };

    public IUPACNamePart apply(IAtomContainer ac) {
        return null;
    };

    public String localize(String s) {
       return s;
    }

}
