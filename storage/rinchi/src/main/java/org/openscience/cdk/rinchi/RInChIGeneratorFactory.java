/* Copyright (C) 2022,2024 Nikolay Kochev <nick@uni-plovdiv.net>, Uli Fechner
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
package org.openscience.cdk.rinchi;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IReaction;

/**
 * Factory providing access to {@link RInChIGenerator}, {@link RInChIToReaction}
 * and {@link RInChIDecomposition}.
 * <br>
 * See these classes for examples of use. Methods in these classes make use of the
 * JNA-InChI library.
 * <br><br>
 * The {@link RInChIGeneratorFactory} is a singleton class. An instance of this
 * class is obtained with:
 * <pre>
 * RInChIGeneratorFactory factory = RInChIGeneratorFactory.getInstance();
 * </pre>
 * <p>
 * RInChI/Reaction interconversion is implemented in this way so that we can
 * check whether the required native code is available. If the native
 * code cannot be loaded during the first call to {@link #getInstance()}
 * (when the instance is created) a {@link CDKException} will be thrown. The
 * most common problem is that the native code is not in the correct location.
 * </p>
 * See:
 * <ul>
 * <li><a href="https://github.com/dan2097/jna-inchi">https://github.com/dan2097/jna-inchi</a></li>
 * <li><a href="http://www.iupac.org/inchi/">http://www.iupac.org/inchi/</a></li>
 * <li><a href="https://www.inchi-trust.org/">https://www.inchi-trust.org/</a></li>
 * </ul>
 *
 * @author Nikolay Kochev
 * @author Uli Fechner
 * @cdk.module rinchi
 * @cdk.githash
 */
public final class RInChIGeneratorFactory {
    // this singleton pattern with a static inner class lets the JVM take
    // care of (1) lazy instantiation and (2) concurrency
    // https://blog.paumard.org/2011/04/22/bilan-sur-le-pattern-singleton/
    private static class SingletonInstanceHolder {
        public final static RInChIGeneratorFactory INSTANCE;

        static {
            INSTANCE = new RInChIGeneratorFactory();
            try {
                RInChIGeneratorFactory.class.getClassLoader().loadClass("io.github.dan2097.jnarinchi.JnaInchi");
            } catch (ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private RInChIGeneratorFactory() {
    }


    /**
     * Return the singleton instance of this class, if needed also creates it.
     *
     * @return the singleton instance of this class
     */
    public static RInChIGeneratorFactory getInstance() {
        return SingletonInstanceHolder.INSTANCE;
    }

    /**
     * Gets a Standard RInChI generator for a {@link IReaction}.
     *
     * @param reaction reaction to generate RInChI for
     * @return the RInChI generator object
     */
    public RInChIGenerator getRInChIGenerator(IReaction reaction) {
        return (new RInChIGenerator(reaction));
    }

    /**
     * Gets a RInChI generator for a {@link IReaction} providing one ore more options to customise the generation.
     *
     * @param reaction reaction to generate RInChI for
     * @param options    one or more options
     * @return the RInChI generator object
     */
    public RInChIGenerator getRInChIGenerator(IReaction reaction, RInChIOption... options) {
        return (new RInChIGenerator(reaction, options));
    }

    /**
     * Returns an instance of {@link RInChIToReaction} that consumes a RInChI string and produces an {@link IReaction}.
     *
     * @param rinchi RInChI to generate the reaction from
     */
    public RInChIToReaction getRInChIToReaction(String rinchi) {
        return (new RInChIToReaction(rinchi));
    }

    /**
     * Returns an instance of {@link RInChIToReaction} that consumes a RInChI string with an accompanying <i>AuxInfo</i>
     * string and produces an {@link IReaction}.
     *
     * @param rinchi  RInChI to generate reaction from
     * @param auxInfo RInChI auxiliary information (<i>AuxInfo</i>)
     */
    public RInChIToReaction getRInChIToReaction(String rinchi, String auxInfo) {
        return (new RInChIToReaction(rinchi, auxInfo));
    }

    /**
     * Consumes a RInChI string and produces a {@link RInChIDecomposition}.
     *
     * @param rinchi RInChI that is decomposed
     */
    public RInChIDecomposition getRInChIDecomposition(String rinchi) {
        return (new RInChIDecomposition(rinchi));
    }

    /**
     * Consumes a RInChI string with an accompanying <i>AuxInfo</i> string and produces a {@link RInChIDecomposition}.
     *
     * @param rinchi  RInChI that is decomposed
     * @param auxInfo RInChI auxiliary information (<i>AuxInfo</i>)
     */
    public RInChIDecomposition getRInChIDecomposition(String rinchi, String auxInfo) {
        return (new RInChIDecomposition(rinchi, auxInfo));
    }
}
