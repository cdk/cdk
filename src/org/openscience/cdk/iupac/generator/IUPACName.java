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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

/**
 *  This class implements an IUPAC name.
 *  It provides a String representation of the name, and,
 *  in addition, a list of applied rules.
 *
 * @cdk.module experimental
 *
 * @author Egon Willighagen
 */
public class IUPACName {

    private Locale locale;
    /** A list of IUPACNameParts to build the name from or to get at applied rules */
    private LinkedList nameParts;

//Not needed?!    
//    private int atomsNamed;

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
     *
     * @return All the sub-parts of the name concatenated in the correct order.
     */
    public String getName() {
        //An iterator over the Global LinkedList - nameParts
        Iterator parts = getIUPACNameParts();
        StringBuffer sb = new StringBuffer();
        while (parts.hasNext()) {
            IUPACNamePart part = (IUPACNamePart)parts.next();
            sb.append(part.getName());
        }
        return sb.toString();
    }

    /**
     * Adds a Rule to the front of the list of applied rules to generate this name.
     * To be used with prefixes
     */
    public void addFront(IUPACNamePart ipc) {
        this.nameParts.addFirst(ipc);
    }

    /**
     * Adds a Rule to the end of the list of applied rules to generate this name.
     * To be used with suffixes.
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

    /**
     * Proivides a string representation of all the information held in this class.
     *
     * @return A list of the sub parts of the name and which rules were used to get them.
     * Each IUPACNamePart is seperated by a newline.
     */
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

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
