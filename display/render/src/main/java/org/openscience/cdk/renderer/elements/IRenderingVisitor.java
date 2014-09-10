/* Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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

import java.awt.geom.AffineTransform;

/**
 * An {@link IRenderingVisitor} is responsible of converting an abstract
 * chemical drawing into a widget set specific drawing. This approach ensures
 * that the rendering engine is widget toolkit independent. Current
 * supported widget toolkits include SWT, Swing, and SVG.
 *
 * @cdk.module  render
 * @cdk.githash
 */
public interface IRenderingVisitor {

    /**
     * Translates a {@link IRenderingElement} into a widget toolkit specific
     * rendering element.
     *
     * @param element Abstract rendering element reflecting some part of the
     *                chemical drawing.
     */
    public abstract void visit(IRenderingElement element);

    /**
     * Sets the affine transformations used.
     *
     * @param transform the affine transformation used.
     */
    public abstract void setTransform(AffineTransform transform);

}
