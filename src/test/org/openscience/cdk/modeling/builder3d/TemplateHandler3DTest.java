/* $Revision: 11555 $ $Author: egonw $ $Date: 2008-07-12 20:31:17 +0200 (Sat, 12 Jul 2008) $
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.modeling.builder3d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.modeling.builder3d.TemplateHandler3D;
import org.openscience.cdk.nonotify.NNAtomContainer;
import org.openscience.cdk.NewCDKTestCase;

/**
 * @cdk.module test-builder3d
 *
 * @author      chhoppe
 * @author      Christoph Steinbeck
 * @cdk.created 2004-11-04
 */
public class TemplateHandler3DTest extends NewCDKTestCase {
		
	@Test
	public void testGetInstance() throws Exception {
		TemplateHandler3D th3d = TemplateHandler3D.getInstance();
		// need to trigger a load of the templates
		th3d.mapTemplates(new NNAtomContainer(), 0);
		Assert.assertEquals(10751, th3d.getTemplateCount());
	}

}
