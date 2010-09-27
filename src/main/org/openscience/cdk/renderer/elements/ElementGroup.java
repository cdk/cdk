/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A group of rendering elements, of any type.
 * 
 * @cdk.module  renderbasic
 * @cdk.githash
 */
public class ElementGroup 
       implements IRenderingElement, Iterable<IRenderingElement> {

    /**
     * The elements in the group. 
     */
    private final List<IRenderingElement> elements;

    /**
     * Create an empty element group.
     */
    public ElementGroup() {
        elements = new ArrayList<IRenderingElement>();
    }

    /** {@inheritDoc} */
    public Iterator<IRenderingElement> iterator() {
        return elements.iterator();
    }

    /**
     * Add a new element to the group.
     * 
     * @param element the element to add to the group
     */
    public void add(IRenderingElement element) {
        if (element == null) return;
        elements.add(element);
    }

    /**
     * Visit the members of the group.
     *  
     * @param visitor the class that will be visiting each element
     */
    public void visitChildren(IRenderingVisitor visitor) {
        for (IRenderingElement child : this.elements) {
            child.accept(visitor);
        }
    }

    /** {@inheritDoc} */
    public void accept( IRenderingVisitor v ) {
        v.visit( this );
    }

}
