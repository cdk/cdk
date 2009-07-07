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

import java.awt.Color;

/**
 * @cdk.module renderbasic
 */
public class OvalElement implements IRenderingElement {

	public final double x;
	public final double y;
	public final double radius;
	public final boolean fill;
	public final Color color;

	public OvalElement(double x, double y, Color color) {
		this(x, y, 10, color);
	}

	public OvalElement(double x, double y, double radius, Color color) {
		this(x, y, radius, true, color);
	}

	public OvalElement(double x, double y, double radius, boolean fill, Color color) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.fill = fill;
		this.color = color;
	}

	public void accept(IRenderingVisitor v) {
		v.visit(this);
	}
}
