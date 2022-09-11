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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module test-render
 */
class RendererModelTest {

    @Test
    void testGetRenderingParameter() {
        IGenerator<IChemObject> generator = new IGenerator<IChemObject>() {

            final IGeneratorParameter<Boolean> someParam = new SomeParam();

            @Override
            public List<IGeneratorParameter<?>> getParameters() {
                return new ArrayList<IGeneratorParameter<?>>() {

                    {
                        add(someParam);
                    }
                };
            }

            @Override
            public IRenderingElement generate(IChemObject object, RendererModel model) {
                // TODO Auto-generated method stub
                return null;
            }
        };
        RendererModel model = new RendererModel();
        model.registerParameters(generator);
        Assertions.assertEquals(Boolean.FALSE, model.getParameter(SomeParam.class).getDefault());
    }

    @Test
    void testHasParameter() {
        IGenerator<IChemObject> generator = new IGenerator<IChemObject>() {

            final IGeneratorParameter<Boolean> someParam = new SomeParam();

            @Override
            public List<IGeneratorParameter<?>> getParameters() {
                return new ArrayList<IGeneratorParameter<?>>() {

                    {
                        add(someParam);
                    }
                };
            }

            @Override
            public IRenderingElement generate(IChemObject object, RendererModel model) {
                // TODO Auto-generated method stub
                return null;
            }
        };
        RendererModel model = new RendererModel();
        Assertions.assertFalse(model.hasParameter(SomeParam.class));
        model.registerParameters(generator);
        Assertions.assertTrue(model.hasParameter(SomeParam.class));
    }

    @Test
    void testReturningTheRealParamaterValue() {
        IGenerator<IChemObject> generator = new IGenerator<IChemObject>() {

            final IGeneratorParameter<Boolean> someParam = new SomeParam();

            @Override
            public List<IGeneratorParameter<?>> getParameters() {
                return new ArrayList<IGeneratorParameter<?>>() {

                    {
                        add(someParam);
                    }
                };
            }

            @Override
            public IRenderingElement generate(IChemObject object, RendererModel model) {
                // TODO Auto-generated method stub
                return null;
            }
        };
        RendererModel model = new RendererModel();
        model.registerParameters(generator);
        IGeneratorParameter<Boolean> param = model.getParameter(SomeParam.class);
        // test the default value
        Assertions.assertEquals(Boolean.FALSE, param.getValue());
        param.setValue(Boolean.TRUE);
        Assertions.assertEquals(Boolean.TRUE, model.getParameter(SomeParam.class).getValue());
    }

    @Test
    void testSetRenderingParameter() {
        IGenerator<IChemObject> generator = new IGenerator<IChemObject>() {

            final IGeneratorParameter<Boolean> someParam = new SomeParam();

            @Override
            public List<IGeneratorParameter<?>> getParameters() {
                return new ArrayList<IGeneratorParameter<?>>() {

                    {
                        add(someParam);
                    }
                };
            }

            @Override
            public IRenderingElement generate(IChemObject object, RendererModel model) {
                // TODO Auto-generated method stub
                return null;
            }
        };
        RendererModel model = new RendererModel();
        model.registerParameters(generator);
        Assertions.assertEquals(Boolean.FALSE, model.get(SomeParam.class));
        model.set(SomeParam.class, true);
        Assertions.assertEquals(Boolean.TRUE, model.get(SomeParam.class));
    }

    @Test
    void testGetDefaultRenderingParameter() {
        IGenerator<IChemObject> generator = new IGenerator<IChemObject>() {

            final IGeneratorParameter<Boolean> someParam = new SomeParam();

            @Override
            public List<IGeneratorParameter<?>> getParameters() {
                return new ArrayList<IGeneratorParameter<?>>() {

                    {
                        add(someParam);
                    }
                };
            }

            @Override
            public IRenderingElement generate(IChemObject object, RendererModel model) {
                // TODO Auto-generated method stub
                return null;
            }
        };
        RendererModel model = new RendererModel();
        model.registerParameters(generator);
        Assertions.assertEquals(Boolean.FALSE, model.getDefault(SomeParam.class));
    }

    @Test
    void testGetRenderingParameters() {
        IGenerator<IChemObject> generator = new IGenerator<IChemObject>() {

            final IGeneratorParameter<Boolean> someParam = new SomeParam();

            @Override
            public List<IGeneratorParameter<?>> getParameters() {
                return new ArrayList<IGeneratorParameter<?>>() {

                    {
                        add(someParam);
                    }
                };
            }

            @Override
            public IRenderingElement generate(IChemObject object, RendererModel model) {
                // TODO Auto-generated method stub
                return null;
            }
        };
        RendererModel model = new RendererModel();
        int nDefaultParams = model.getRenderingParameters().size();
        model.registerParameters(generator);
        List<IGeneratorParameter<?>> params = model.getRenderingParameters();
        Assertions.assertNotNull(params);
        Assertions.assertEquals(nDefaultParams + 1, params.size()); // the registered one + defaults

        List<Class<?>> paramClasses = new ArrayList<>();
        for (IGeneratorParameter<?> param : params)
            paramClasses.add(param.getClass());

        assertThat(paramClasses, hasItem(SomeParam.class));
    }

    @Test
    void testGetSetNotification() {
        RendererModel model = new RendererModel();
        // test the default setting
        Assertions.assertTrue(model.getNotification());
        model.setNotification(false);
        Assertions.assertFalse(model.getNotification());
        model.setNotification(true);
        Assertions.assertTrue(model.getNotification());
    }

    @Test
    void testNoDefaultToolTips() {
        RendererModel model = new RendererModel();
        // test: no default tool tips
        Assertions.assertNull(model.getToolTipText(new Atom()));
        // but a non-null map
        Assertions.assertNotNull(model.getToolTipTextMap());
    }

    @Test
    void testToolTipFunctionality() {
        Map<IAtom, String> tips = new HashMap<>();
        IAtom anonAtom = new Atom();
        tips.put(anonAtom, "Repelsteeltje");
        RendererModel model = new RendererModel();
        model.setToolTipTextMap(tips);
        Assertions.assertEquals(tips, model.getToolTipTextMap());
        Assertions.assertEquals("Repelsteeltje", model.getToolTipText(anonAtom));
    }

    @Test
    void testClipboardContent() {
        RendererModel model = new RendererModel();
        // test default
        Assertions.assertNull(model.getClipboardContent());
        IAtomContainer content = new AtomContainer();
        model.setClipboardContent(content);
        Assertions.assertEquals(content, model.getClipboardContent());
        model.setClipboardContent(null);
        Assertions.assertNull(model.getClipboardContent());
    }

    @Test
    void testExternalSelectedPart() {
        RendererModel model = new RendererModel();
        // test default
        Assertions.assertNull(model.getExternalSelectedPart());
        IAtomContainer content = new AtomContainer();
        model.setExternalSelectedPart(content);
        Assertions.assertEquals(content, model.getExternalSelectedPart());
        model.setExternalSelectedPart(null);
        Assertions.assertNull(model.getExternalSelectedPart());
    }

    @Test
    void testHighlightedAtom() {
        RendererModel model = new RendererModel();
        // test default
        Assertions.assertNull(model.getHighlightedAtom());
        IAtom content = new Atom();
        model.setHighlightedAtom(content);
        Assertions.assertEquals(content, model.getHighlightedAtom());
        model.setHighlightedAtom(null);
        Assertions.assertNull(model.getHighlightedAtom());
    }

    @Test
    void testHighlightedBond() {
        RendererModel model = new RendererModel();
        // test default
        Assertions.assertNull(model.getHighlightedBond());
        IBond content = new Bond();
        model.setHighlightedBond(content);
        Assertions.assertEquals(content, model.getHighlightedBond());
        model.setHighlightedBond(null);
        Assertions.assertNull(model.getHighlightedBond());
    }

    class MockSelection implements IChemObjectSelection {

        @Override
        public void select(IChemModel chemModel) {}

        @Override
        public IAtomContainer getConnectedAtomContainer() {
            return null;
        }

        @Override
        public boolean isFilled() {
            return false;
        }

        @Override
        public boolean contains(IChemObject obj) {
            return false;
        }

        @Override
        public <E extends IChemObject> Collection<E> elements(Class<E> clazz) {
            return null;
        }
    }

    @Test
    void testSelection() {
        RendererModel model = new RendererModel();
        // test default
        Assertions.assertNull(model.getSelection());
        IChemObjectSelection content = new MockSelection();
        model.setSelection(content);
        Assertions.assertEquals(content, model.getSelection());
        model.setSelection(null);
        Assertions.assertNull(model.getSelection());
    }

    class MockListener implements ICDKChangeListener {

        boolean isChanged = false;

        @Override
        public void stateChanged(EventObject event) {
            isChanged = true;
        }
    }

    @Test
    void testListening() {
        RendererModel model = new RendererModel();
        // test default
        MockListener listener = new MockListener();
        model.addCDKChangeListener(listener);
        Assertions.assertFalse(listener.isChanged);
        model.fireChange();
        Assertions.assertTrue(listener.isChanged);

        // test unregistering
        listener.isChanged = false;
        Assertions.assertFalse(listener.isChanged);
        model.removeCDKChangeListener(listener);
        model.fireChange();
        Assertions.assertFalse(listener.isChanged);
    }

    @Test
    void testMerge() {
        RendererModel model = new RendererModel();
        Assertions.assertNotNull(model.getMerge());
        // any further testing I can do here?
    }
}
