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

import java.util.Locale;

/**
 *  This class knows which rule has which priority. It retrieves
 *  this information from a property file. This file may need to
 *  be localized.
 *
 *  An instance from this class is made by:
 *  <pre>
 *  Locale l = new Locale("NL"); // Dutch
 *  RulePriorityList priorities = RulePrioritList.getInstance(l);
 *  </pre>
 *
 * @cdk.module experimental
 *
 * @see java.util.Locale
 * @author Egon Willighagen
 */
public class RulePriorityList {

    /**
     *  Private constructor for this class.
     */
    private RulePriorityList(Locale l) {
    }

    public static RulePriorityList getInstance(Locale l) {
        return new RulePriorityList(l);
    }

    public double getPriority(IRule r) {
        return 1.0;
    }
}
