/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.iupac.generator;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

/**
 *  This class implements a IUPAC name.
 *  It provides a String representation of the name, and,
 *  in addition, a list of applied rules.
 *
 * @author Egon Willighagen
 */
public class IUPACNamePart {

    private String name;
    private Rule rule;

    /**
     *  Constructor for a IUPAC name part.
     *
     * @param    name       Current name part.
     * @param    rule       Rule that generator this name part.
     */
    public IUPACNamePart(String name, Rule rule) {
        this.rule = rule;
        this.name = name;
    }

    /**
     *  Returns the IUPAC name.
     */
    public String getName() {
        return name != null ? name : "unknown";
    }

    /**
     *  Returns the applied Rule's.
     */
    public Rule getRule() {
        return rule;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getName());
        sb.append("\nRule: ");
        sb.append(rule.getName() + "\n");
        return sb.toString();
    }
}
