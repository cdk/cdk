/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.iupac.generator;

import org.openscience.cdk.AtomContainer;

/**
 *  This interface defines the API for the implementation of
 *  IUPAC rules in the org.openscience.cdk.iupac.generator
 *  package.
 *
 * @author Egon Willighagen
 */
public class NamingRule implements Rule {

    private IUPACNameLocalizer localizer;

    public NamingRule() {
        this.localizer = null;
    }

    public String getName() {
        return "Dummy Rule";
    };

    public IUPACNamePart apply(AtomContainer ac) {
        return null;
    };

    public NumberingRule getNumberingRule() {
        return null;
    };

    public void setIUPACNameLocalizer(IUPACNameLocalizer l) {
        this.localizer = l;
    }

    public String localize(String s) {
       return this.localizer != null ? localizer.localize(s) : s;
    }

}
