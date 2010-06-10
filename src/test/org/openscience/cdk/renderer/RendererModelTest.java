/* Copyright (C) 2010  Egon Willighagen <egon.willighagen@gmail.com>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
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
package org.openscience.cdk.renderer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;

/**
 * @cdk.module test-render
 */
public class RendererModelTest {

	@Test
	public void testGetRenderingParameter() {
		IGenerator<IChemObject> generator = new IGenerator<IChemObject>() {
			IGeneratorParameter<Boolean> someParam = new SomeParam(); 
			@Override
			public List<IGeneratorParameter<?>> getParameters() {
				return new ArrayList<IGeneratorParameter<?>>() {{
					add(someParam);
				}};
			}
			@Override
			public IRenderingElement generate(IChemObject object,
					RendererModel model) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		RendererModel model = new RendererModel();
		model.registerParameters(generator);
		Assert.assertEquals(
			Boolean.FALSE,
			model.getParameter(SomeParam.class).getDefault()
		);
	}

	@Test
	public void testReturningTheRealParamaterValue() {
		IGenerator<IChemObject> generator = new IGenerator<IChemObject>() {
			IGeneratorParameter<Boolean> someParam = new SomeParam(); 
			@Override
			public List<IGeneratorParameter<?>> getParameters() {
				return new ArrayList<IGeneratorParameter<?>>() {{
					add(someParam);
				}};
			}
			@Override
			public IRenderingElement generate(IChemObject object,
					RendererModel model) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		RendererModel model = new RendererModel();
		model.registerParameters(generator);
		IGeneratorParameter<Boolean> param =
			model.getParameter(SomeParam.class);
		// test the default value
		Assert.assertEquals(Boolean.FALSE, param.getValue());
		param.setValue(Boolean.TRUE);
		Assert.assertEquals(
			Boolean.TRUE,
			model.getParameter(SomeParam.class).getValue()
		);
	}
}
