/*  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * @cdk.module renderbasic
 */
@TestClass("org.openscience.cdk.renderer.elements.WedgeLineElementTest")
public class WedgeLineElement extends LineElement {

	public final boolean isDashed;
	public final Direction direction;

	public enum Direction {
		toFirst, toSecond;
	}

	@TestMethod("testConstructor")
	public WedgeLineElement(double x1, double y1, double x2, double y2,
			double width, boolean dashed, Direction direction, Color color) {
		super(x1, y1, x2, y2, width, color);
		this.isDashed = dashed;
		this.direction = direction;
	}

	public WedgeLineElement(LineElement element, boolean dashed,
			Direction direction, Color color) {
		this(direction == Direction.toFirst ? element.x2: element.x1,
			 direction == Direction.toFirst ? element.y2: element.y1,
			 direction == Direction.toFirst ? element.x1 : element.x2,
			 direction == Direction.toFirst ? element.y1 : element.y2,
		     element.width, dashed, direction, color);
	}

	@Override
	@TestMethod("testAccept")
	public void accept(IRenderingVisitor v) {
		v.visit(this);
	}
}
