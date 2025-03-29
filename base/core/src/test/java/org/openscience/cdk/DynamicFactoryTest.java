/*
 * Copyright (C) 2000-2012  John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ICDKObject;
import org.openscience.cdk.interfaces.IElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openscience.cdk.DynamicFactory.key;

/**
 * Unit test for the DynamicFactory.
 *
 * @author John May
 */
public class DynamicFactoryTest {

    /**
     * Ensure a negative size throws an exception.
     */
    @Test
    void testConstructor() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {new DynamicFactory(-1);});
    }

                                 /**
     * Check we can't register an interface.
     */
    @Test
    void testRegister_Interface() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    DynamicFactory factory = new DynamicFactory(0);
                                    factory.register(IAtom.class);
                                });
    }

    @Test
    void testRegister_AbstractClass() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    DynamicFactory factory = new DynamicFactory(0);
                                    factory.register(MockedAtom.class);
                                });
    }

    @Test
    void testRegister() throws Exception {

        DynamicFactory.InterfaceProvider accessor = mock(DynamicFactory.InterfaceProvider.class);
        IAtom mock = mock(MockedAtom.class);

        when(accessor.getInterfaces(mock.getClass())).thenReturn(new Class<?>[]{IAtom.class});

        DynamicFactory factory = new DynamicFactory(accessor, 5);

        Assertions.assertTrue(factory.implementorsOf(IAtom.class).isEmpty());

        factory.register(mock.getClass());

        Assertions.assertFalse(factory.implementorsOf(IAtom.class).isEmpty());

    }

    @Test
    void testRegister_NonCDKInterface() throws Exception {

        DynamicFactory.InterfaceProvider accessor = mock(DynamicFactory.InterfaceProvider.class);
        IAtom mock = mock(MockedAtom.class);

        // we should not register this implementation with comparable, we can
        // simulate this here
        when(accessor.getInterfaces(mock.getClass())).thenReturn(new Class<?>[]{Comparable.class});

        DynamicFactory factory = new DynamicFactory(accessor, 5);

        Assertions.assertFalse(factory.register(mock.getClass()));

    }

    @Test
    void testRegister_Explicit() throws Exception {

        IAtom mock = mock(MockedAtom.class);

        DynamicFactory factory = new DynamicFactory(5);

        Assertions.assertTrue(factory.implementorsOf(IAtom.class).isEmpty());

        // register the mock class
        factory.register(IAtom.class, mock.getClass());

        Assertions.assertFalse(factory.implementorsOf(IAtom.class).isEmpty());

        // Atom is a subclass of Element so we can actually register it as
        // element too

        Assertions.assertTrue(factory.implementorsOf(IElement.class).isEmpty());

        factory.register(IElement.class, mock.getClass());

        Assertions.assertFalse(factory.implementorsOf(IElement.class).isEmpty());

    }

    @Test
    void testRegister_PrivateConstructor() throws Exception {

        DynamicFactory factory = new DynamicFactory(5);

        Assertions.assertTrue(factory.implementorsOf(ICDKObject.class).isEmpty());

        // register the mock class
        factory.register(ICDKObject.class, DynamicFactoryTestMock.class);

        Iterator<?> it = factory.suggest(ICDKObject.class);
        List<Object> list = new ArrayList<>(5);
        while (it.hasNext()) {
            list.add(it.next());
        }

        assertThat("mocked atom should have two public constructors", list.size(), is(2));

    }

    @Test
    void testRegister_Constructor() throws Exception {

        DynamicFactory factory = new DynamicFactory(5);

        Assertions.assertTrue(factory.register(ICDKObject.class, DynamicFactoryTestMock.class.getConstructor(String.class)));

    }

    @SuppressWarnings("unchecked")
    // mocking generics
    @Test
    void testRegister_Constructor_Modifier() throws Exception {

        DynamicFactory factory = new DynamicFactory(5);

        DynamicFactory.CreationModifier modifier = mock(DynamicFactory.CreationModifier.class);

        Assertions.assertTrue(factory.register(ICDKObject.class, DynamicFactoryTestMock.class.getConstructor(String.class),
                                               modifier));

        Assertions.assertNotNull(factory.ofClass(ICDKObject.class, "empty"));

        // verify the modifier was invoked once
        verify(modifier).modify(any());

    }

    /**
     * Tests that we get an exception if we try to register two different
     * constructors to the same interface.
     *
     * @throws Exception
     */
    @Test
    void testRegister_Duplicate() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    DynamicFactory factory = new DynamicFactory(5);

                                    IAtom atom = mock(MockedAtom.class);
                                    IAtom pseudo = mock(MockedPseudoAtom.class);

                                    Assertions.assertTrue(factory.register(ICDKObject.class, pseudo.getClass()));

                                    // should throw an exception the mocked atom also has a constructor with
                                    // a single String parameter
                                    Assertions.assertFalse(factory.register(ICDKObject.class, atom.getClass()));
                                });
    }

    @Test
    void testOfClass_Instantiator() throws Exception {

        DynamicFactory factory = new DynamicFactory(5);

        factory.register(key(IAtom.class), new DynamicFactory.BasicCreator<IAtom>(null) {

            @Override
            public IAtom create(Object[] objects) {
                return mock(IAtom.class);
            }
        });

        Assertions.assertNotNull(factory.ofClass(IAtom.class));

    }

    @Test
    void testOfClass() throws Exception {

        IAtom mock = mock(MockedAtom.class);

        DynamicFactory factory = new DynamicFactory(5);

        Assertions.assertTrue(factory.implementorsOf(IAtom.class).isEmpty());

        // register the mock class
        factory.register(IAtom.class, mock.getClass());

        // ofClass needs to know the inner class is coming from 'this'
        IAtom instance = factory.ofClass(IAtom.class, this);

        Assertions.assertNotNull(instance);

    }

    /**
     * Check we get an exception when we try to build from a non-interface.
     * @throws Exception
     */
    @Test
    void testOfConcrete_Params() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    IAtom mock = mock(MockedAtom.class);

                                    DynamicFactory factory = new DynamicFactory(5);

                                    Assertions.assertTrue(factory.implementorsOf(IAtom.class).isEmpty());

                                    // register the mock class
                                    factory.register(IAtom.class, mock.getClass());

                                    // ofClass -> illegal argument, non-interface
                                    IAtom instance = factory.ofClass(mock.getClass(), this);
                                });
    }

    @Test
    void testOfConcrete() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    DynamicFactory factory = new DynamicFactory(5);

                                    Assertions.assertTrue(factory.implementorsOf(ICDKObject.class).isEmpty());

                                    // register the mock class
                                    factory.register(ICDKObject.class, DynamicFactoryTestMock.class);

                                    // ofClass -> illegal argument, non-interface
                                    ICDKObject instance = factory.ofClass(DynamicFactoryTestMock.class);
                                });
    }

    @Test
    void testOfClass_WithParams() throws Exception {

        IAtom mock = mock(MockedAtom.class);

        DynamicFactory factory = new DynamicFactory(5);

        Assertions.assertTrue(factory.implementorsOf(IAtom.class).isEmpty());

        // register the mock class
        factory.register(IAtom.class, mock.getClass());

        // ofClass needs to know the inner class is coming from 'this'
        IAtom instance = factory.ofClass(IAtom.class, this, "C");

        Assertions.assertNotNull(instance);

        Assertions.assertEquals("C", instance.getSymbol());

    }

    /**
     * Kind of already tested in other methods.
     */
    @Test
    void testSuggest() throws Exception {

        DynamicFactory factory = new DynamicFactory(5);

        IAtom atom = mock(MockedAtom.class);

        Assertions.assertFalse(factory.suggest(IAtom.class).hasNext());

        factory.register(IAtom.class, atom.getClass());

        Assertions.assertTrue(factory.suggest(IAtom.class).hasNext());
    }

    @Test
    void testImplementationsOf() throws Exception {

        DynamicFactory factory = new DynamicFactory(5);

        IElement element = mock(MockedElement.class);
        IElement atom = mock(MockedAtom.class);

        assertThat(factory.implementorsOf(IElement.class).size(), is(0));

        factory.register(IElement.class, element.getClass());

        assertThat(factory.implementorsOf(IElement.class).size(), is(1));

        factory.register(IElement.class, atom.getClass());

        assertThat(factory.implementorsOf(IElement.class).size(), is(2));
        assertThat(factory.implementorsOf(IElement.class).size(), is(2));
    }

    @Test
    void testKey_Default() {

        DynamicFactory.ConstructorKey key = DynamicFactory.key(IAtom.class);

        Assertions.assertEquals(IAtom.class, key.intf());
        Assertions.assertEquals(0, key.n());

    }

    @Test
    void testKey_Parameters() {

        DynamicFactory.ConstructorKey key = DynamicFactory.key(IBond.class, IAtom.class, IAtom.class);

        Assertions.assertEquals(IBond.class, key.intf());
        Assertions.assertEquals(2, key.n());
        Assertions.assertEquals(IAtom.class, key.type(0));
        Assertions.assertEquals(IAtom.class, key.type(1));

    }

    @Test
    void testKey_ArrayParameters() {

        DynamicFactory.ConstructorKey key = DynamicFactory.key(IBond.class, IAtom[].class);
        Assertions.assertEquals(IBond.class, key.intf());
        Assertions.assertEquals(1, key.n());
        Assertions.assertTrue(key.type(0).isArray());
        Assertions.assertEquals(IAtom[].class, key.type(0));

    }

    /**
     * Ensures primitive types are converted.
     */
    @Test
    void testKey_Primitives() {

        DynamicFactory.ConstructorKey key = DynamicFactory.key(IAtom.class, boolean.class, byte.class, char.class,
                short.class, int.class, float.class, long.class, double.class);

        Assertions.assertEquals(IAtom.class, key.intf());
        Assertions.assertEquals(8, key.n());
        Assertions.assertEquals(Boolean.class, key.type(0));
        Assertions.assertEquals(Byte.class, key.type(1));
        Assertions.assertEquals(Character.class, key.type(2));
        Assertions.assertEquals(Short.class, key.type(3));
        Assertions.assertEquals(Integer.class, key.type(4));
        Assertions.assertEquals(Float.class, key.type(5));
        Assertions.assertEquals(Long.class, key.type(6));
        Assertions.assertEquals(Double.class, key.type(7));

    }

    /**
     * Unit test ensures the factory wraps up varargs into the correct
     * representation. This is needed as passing a uniform array will be
     * converted to actual varargs - this test checks both cases.
     */
    @Test
    void testOfClass_Wrapping() {

        DynamicFactory factory = new DynamicFactory(5);

        // register ICDKObject with an mock instantiator
        factory.register(key(ICDKObject.class, IAtom[].class), new DynamicFactory.BasicCreator<IAtom>(null) {

            @Override
            public IAtom create(Object[] objects) {
                return mock(IAtom.class);
            }
        });

        // uniform parameter array
        Assertions.assertNotNull(factory.ofClass(ICDKObject.class, new IAtom[]{mock(IAtom.class), mock(IAtom.class),
                mock(IAtom.class)}));

        // is equivalent to just using varargs...
        Assertions.assertNotNull(factory.ofClass(ICDKObject.class, mock(IAtom.class), mock(IAtom.class), mock(IAtom.class)));

        // unless we double wrap it (which resolves to the same instantiator)
        Assertions.assertNotNull(factory.ofClass(ICDKObject.class, new Object[]{new IAtom[]{mock(IAtom.class), mock(IAtom.class),
                mock(IAtom.class)}}));

    }

    /**
     * Test mocks {@link org.openscience.cdk.DynamicFactory.CreationModifier}
     * and ensures the modify is called once when a registered implementation is
     * created.
     */
    @Test
    @SuppressWarnings("unchecked")
    void testRegister_WithModifier() {

        DynamicFactory factory = new DynamicFactory(5);

        IAtom mock = mock(IAtom.class);
        DynamicFactory.CreationModifier modifier = mock(DynamicFactory.CreationModifier.class);

        // ignore compiler warnings here... we're mocking so don't have a set type
        factory.register(IAtom.class, mock.getClass(), modifier);

        Assertions.assertNotNull(factory.ofClass(IAtom.class));

        // verify the modify method was called once
        verify(modifier, times(1)).modify(any());

    }

    /* some abstract classes to mock */

    public abstract class MockedAtom implements IAtom {

        private String symbol;

        MockedAtom() {}

        MockedAtom(String symbol) {
            this.symbol = symbol;
        }

        // can't properly mock as we're creating new instances (that themselves
        // aren't mocks...)
        @Override
        public String getSymbol() {
            return symbol;
        }

        @Override
        public abstract IAtom clone() throws CloneNotSupportedException;
    }

    public abstract class MockedPseudoAtom implements IAtom {

        MockedPseudoAtom(String ignored) {}

        @Override
        public abstract IAtom clone() throws CloneNotSupportedException;
    }

    public abstract class MockedElement implements IElement {

        MockedElement(IElement element) {}

        @Override
        public abstract IAtom clone() throws CloneNotSupportedException;
    }

}
