/* Copyright (C) 2009-2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.interfaces;

import org.openscience.cdk.tools.LoggingToolFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A helper class to instantiate a {@link ICDKObject} instance for a specific
 * implementation.
 *
 * @author        egonw
 * @cdk.module    interfaces
 * @cdk.githash
 */
public interface IChemObjectBuilder {

    /**
     * Creates a new instance of an {@link ICDKObject}, using the constructor defined by the
     * given parameters.
     *
     * @param <T>    Class of an interface extending {@link ICDKObject} or {@link ICDKObject}
     *               itself.
     * @param clazz  Interface class to instantiate an instance for.
     * @param params Parameters passed to the constructor of the created instance.
     * @return       Instance created.
     *
     * @throws IllegalArgumentException Exception thrown when the {@link IChemObjectBuilder}
     *               builder cannot instantiate the <code>clazz</code> with the given parameters.
     */
    <T extends ICDKObject> T newInstance(Class<T> clazz, Object... params) throws IllegalArgumentException;

    /**
     * Create a new atom using the default constructor. This method is considerably faster
     * than the dynamic dispatch of {@code newInstance(IAtom.class)} and should be used for
     * high throughput applications (e.g. IO).
     *
     * @return new atom
     */
    IAtom newAtom();

    /**
     * Create a new bond using the default constructor. This method is considerably faster
     * than the dynamic dispatch of {@code newInstance(IBond.class)} and should be used for
     * high throughput applications (e.g. IO).
     *
     * @return new bond
     */
    IBond newBond();

    /**
     * Create a new atom container using the default constructor. This method is considerably faster
     * than the dynamic dispatch of {@code newInstance(IAtomContainer.class)} and should be used for
     * high throughput applications (e.g. IO).
     *
     * @return the new atom container
     */
    IAtomContainer newAtomContainer();

    /**
     * Create a new reaction using the default constructor. This method is considerably faster
     * than the dynamic dispatch of {@code newInstance(IReaction.class)} and should be used for
     * high throughput applications (e.g. IO).
     *
     * @return the new reaction
     */
    IReaction newReaction();

    /**
     * This function is used to find an IChemObject builder using reflection. It
     * first tries to create a SilentChemObjectBuilder and failing that a
     * DefaultChemObjectBuilder. It should be used sparingly since APIs should
     * require a builder is passed in.
     *
     * @return the IChemObject builder
     */
    static IChemObjectBuilder find() {
        return ChemObjectBuilderCache.INSTANCE.get();
    }
}
