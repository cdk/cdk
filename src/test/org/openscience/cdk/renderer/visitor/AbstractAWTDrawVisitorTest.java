/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer.visitor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.font.IFontManager;

/**
 * @cdk.module  test-renderawt
 * @cdk.githash
 */
public class AbstractAWTDrawVisitorTest {

	private final class NestedAWTDrawVisitor extends AbstractAWTDrawVisitor {
		@Override
		public void visit(IRenderingElement element) {}

		@Override
		public void setRendererModel(RendererModel rendererModel) {}

		@Override
		public void setFontManager(IFontManager fontManager) {}
	}

	@Test
	public void testExtension() {
		AbstractAWTDrawVisitor visitor = new NestedAWTDrawVisitor();
		Assert.assertNotNull(visitor);
	}

	@Test
	public void testSetAffineTransformation() {
		AbstractAWTDrawVisitor visitor = new NestedAWTDrawVisitor();
		visitor.setTransform(new AffineTransform());
		Assert.assertNotNull(visitor);
	}

	@Test
	public void testGetTextBounds() {
		AbstractAWTDrawVisitor visitor = new NestedAWTDrawVisitor();
		visitor.setTransform(new AffineTransform());
		Image image = new BufferedImage(
			100, 100, BufferedImage.TYPE_INT_RGB
		);
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		Rectangle2D rectangle = visitor.getTextBounds("Foo", 3, 5, g2d);
		Assert.assertNotNull(rectangle);
	}

	@Test
	public void testTransformPoint() {
		AbstractAWTDrawVisitor visitor = new NestedAWTDrawVisitor();
		visitor.setTransform(new AffineTransform()); // no transform
		int[] transformed = visitor.transformPoint(1, 2);
		Assert.assertEquals(1, transformed[0]);
		Assert.assertEquals(2, transformed[1]);
	}

	@Test
	public void testGetTextBasePoint() {
		AbstractAWTDrawVisitor visitor = new NestedAWTDrawVisitor();
		visitor.setTransform(new AffineTransform());
		Image image = new BufferedImage(
			100, 100, BufferedImage.TYPE_INT_RGB
		);
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		Point point = visitor.getTextBasePoint("Foo", 3, 5, g2d);
		Assert.assertNotNull(point);
	}
}
