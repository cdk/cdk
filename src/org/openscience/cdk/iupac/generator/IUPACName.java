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

import java.util.Enumeration;
import java.util.Locale;
import java.util.LinkedList;
import java.util.Iterator;

/**
 *  This class implements a IUPAC name.
 *  It provides a String representation of the name, and,
 *  in addition, a list of applied rules.
 *
 * @author Egon Willighagen
 */
public class IUPACName {

    private Locale locale;
    private LinkedList nameParts;

    private int atomsNamed;

    /**
     *  Constructor for a IUPAC name.
     *
     * @param       l       Locale of this name.
     */
    public IUPACName(Locale l) {
        this.locale = l;
        this.nameParts = new LinkedList();
    }

    /**
     *  Constructor for an English IUPAC name.
     */
    public IUPACName() {
        this(new Locale("en", "US"));
    }

    /**
     *  Returns the IUPAC name.
     */
    public String getName() {
        Iterator parts = getIUPACNameParts();
        StringBuffer sb = new StringBuffer();
        while (parts.hasNext()) {
            IUPACNamePart part = (IUPACNamePart)parts.next();
            sb.append(part.getName());
        }
        return sb.toString();
    }

    /**
     *  Adds a Rule to the list of applied rules to generate this name.
     */
    public void addFront(IUPACNamePart ipc) {
        this.nameParts.addFirst(ipc);
    }

    /**
     *  Adds a Rule to the list of applied rules to generate this name.
     */
    public void addEnd(IUPACNamePart ipc) {
        this.nameParts.addLast(ipc);
    }

    /**
     *  Returns an Enumeration with the list of applied Rule's.
     */
    public Iterator getIUPACNameParts() {
        return nameParts.iterator();
    }

    /**
     *  Returns the number of IUPAC name parts.
     */
    public int size() {
        return nameParts.size();
    }

    public String toString() {
        Iterator parts = getIUPACNameParts();
        StringBuffer sb = new StringBuffer();
        while (parts.hasNext()) {
            IUPACNamePart part = (IUPACNamePart)parts.next();
            sb.append(part.getName() + " (Rule " +
                      part.getRule().getName() + ")\n");
        }
        return sb.toString();
    }
}
