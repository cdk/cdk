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
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;

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
	public void testHasParameter() {
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
		Assert.assertFalse(
			model.hasParameter(SomeParam.class)
		);
		model.registerParameters(generator);
		Assert.assertTrue(
			model.hasParameter(SomeParam.class)
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

	@Test
	public void testSetRenderingParameter() {
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
			Boolean.FALSE, model.get(SomeParam.class)
		);
		model.set(SomeParam.class, true);
		Assert.assertEquals(
			Boolean.TRUE, model.get(SomeParam.class)
		);
	}

	@Test
	public void testGetDefaultRenderingParameter() {
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
			model.getDefault(SomeParam.class)
		);
	}

	@Test
	public void testGetRenderingParameters() {
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
		List<IGeneratorParameter<?>> params = model.getRenderingParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(3, params.size()); // the registered one + two defaults
		Assert.assertEquals(SomeParam.class, params.get(2).getClass());
	}
	
	@Test
	public void testGetSetNotification() {
		RendererModel model = new RendererModel();
		// test the default setting
		Assert.assertTrue(model.getNotification());
		model.setNotification(false);
		Assert.assertFalse(model.getNotification());
		model.setNotification(true);
		Assert.assertTrue(model.getNotification());
	}

	@Test
	public void testNoDefaultToolTips() {
		RendererModel model = new RendererModel();
		// test: no default tool tips
		Assert.assertNull(model.getToolTipText(new Atom()));
		// but a non-null map
		Assert.assertNotNull(model.getToolTipTextMap());
	}

	@Test
	public void testToolTipFunctionality() {
		Map<IAtom, String> tips = new HashMap<IAtom, String>();
		IAtom anonAtom = new Atom();
		tips.put(anonAtom, "Repelsteeltje");
		RendererModel model = new RendererModel();
		model.setToolTipTextMap(tips);
		Assert.assertEquals(tips, model.getToolTipTextMap());
		Assert.assertEquals("Repelsteeltje", model.getToolTipText(anonAtom));
	}

	@Test
	public void testClipboardContent() {
		RendererModel model = new RendererModel();
		// test default
		Assert.assertNull(model.getClipboardContent());
		IAtomContainer content = new AtomContainer();
		model.setClipboardContent(content);
		Assert.assertEquals(content, model.getClipboardContent());
		model.setClipboardContent(null);
		Assert.assertNull(model.getClipboardContent());
	}

	@Test
	public void testExternalSelectedPart() {
		RendererModel model = new RendererModel();
		// test default
		Assert.assertNull(model.getExternalSelectedPart());
		IAtomContainer content = new AtomContainer();
		model.setExternalSelectedPart(content);
		Assert.assertEquals(content, model.getExternalSelectedPart());
		model.setExternalSelectedPart(null);
		Assert.assertNull(model.getExternalSelectedPart());
	}

	@Test
	public void testHighlightedAtom() {
		RendererModel model = new RendererModel();
		// test default
		Assert.assertNull(model.getHighlightedAtom());
		IAtom content = new Atom();
		model.setHighlightedAtom(content);
		Assert.assertEquals(content, model.getHighlightedAtom());
		model.setHighlightedAtom(null);
		Assert.assertNull(model.getHighlightedAtom());
	}

	@Test
	public void testHighlightedBond() {
		RendererModel model = new RendererModel();
		// test default
		Assert.assertNull(model.getHighlightedBond());
		IBond content = new Bond();
		model.setHighlightedBond(content);
		Assert.assertEquals(content, model.getHighlightedBond());
		model.setHighlightedBond(null);
		Assert.assertNull(model.getHighlightedBond());
	}

	class MockSelection implements IChemObjectSelection {
		@Override public void select(IChemModel chemModel) {}
		@Override public IAtomContainer getConnectedAtomContainer() {
			return null;
		}
		@Override public boolean isFilled() { return false; }
		@Override public boolean contains(IChemObject obj) { return false; }
		@Override
		public <E extends IChemObject> Collection<E> elements(Class<E> clazz) {
			return null;
		}
	}

	@Test
	public void testSelection() {
		RendererModel model = new RendererModel();
		// test default
		Assert.assertNull(model.getSelection());
		IChemObjectSelection content = new MockSelection();
		model.setSelection(content);
		Assert.assertEquals(content, model.getSelection());
		model.setSelection(null);
		Assert.assertNull(model.getSelection());
	}

	class MockListener implements ICDKChangeListener {
		boolean isChanged = false;
		@Override public void stateChanged(EventObject event) {
			isChanged = true;
		}
	}
	
	@Test
	public void testListening() {
		RendererModel model = new RendererModel();
		// test default
		MockListener listener = new MockListener();
		model.addCDKChangeListener(listener);
		Assert.assertFalse(listener.isChanged);
		model.fireChange();
		Assert.assertTrue(listener.isChanged);

		// test unregistering
		listener.isChanged = false;
		Assert.assertFalse(listener.isChanged);
		model.removeCDKChangeListener(listener);
		model.fireChange();
		Assert.assertFalse(listener.isChanged);
	}

	@Test
	public void testMerge() {
		RendererModel model = new RendererModel();
		Assert.assertNotNull(model.getMerge());
		// any further testing I can do here?
	}
}
