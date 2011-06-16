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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.font.AWTFontManager;

/**
 * @cdk.module  test-renderawt
 * @cdk.githash
 *
 */
public class AWTDrawVisitorTest {

	@Test
	public void testConstructor() {
		Image image = new BufferedImage(
			100, 100, BufferedImage.TYPE_INT_RGB
		);
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		AWTDrawVisitor visitor = new AWTDrawVisitor(g2d);
		Assert.assertNotNull(visitor);
	}

	@Test
	public void testSetFontManager() {
		Image image = new BufferedImage(
			100, 100, BufferedImage.TYPE_INT_RGB
		);
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		AWTDrawVisitor visitor = new AWTDrawVisitor(g2d);
		visitor.setFontManager(new AWTFontManager());
		// at least we now know it did not crash...
		Assert.assertNotNull(visitor);
	}

	@Test
	public void testSetRendererModel() {
		Image image = new BufferedImage(
			100, 100, BufferedImage.TYPE_INT_RGB
		);
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		AWTDrawVisitor visitor = new AWTDrawVisitor(g2d);
		visitor.setRendererModel(new RendererModel());
		// at least we now know it did not crash...
		Assert.assertNotNull(visitor);
	}

	@Test
	public void testVisit() {
		Image image = new BufferedImage(
			100, 100, BufferedImage.TYPE_INT_RGB
		);
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		AWTDrawVisitor visitor = new AWTDrawVisitor(g2d);
		visitor.setFontManager(new AWTFontManager());
		visitor.setTransform(new AffineTransform());
		visitor.visit(new TextElement(2, 3, "Foo", Color.BLACK));
		// at least we now know it did not crash...
		Assert.assertNotNull(visitor);
	}
}
