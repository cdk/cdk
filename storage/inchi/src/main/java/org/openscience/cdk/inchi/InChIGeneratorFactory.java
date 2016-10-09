/* Copyright (C) 2006-2007  Sam Adams <sea36@users.sf.net>
 *                    2009  Jonathan Alvarsson <jonalv@users.sf.net>
 *                    2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.inchi;

import java.util.List;

import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.JniInchiWrapper;
import net.sf.jniinchi.LoadNativeLibraryException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * <p>Factory providing access to {@link InChIGenerator} and {@link InChIToStructure}.
 * See those classes for examples of use. These methods make use of the
 * JNI-InChI library.
 *
 * <p>The {@link InChIGeneratorFactory} is a singleton class, which means that there
 * exists only one instance of the class. An instance of this class is obtained
 * with:
 * <pre>
 * InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
 * </pre>
 *
 * <p>InChI/Structure interconversion is implemented in this way so that we can
 * check whether or not the native code required is available. If the native
 * code cannot be loaded during the first call to  <code>getInstance</code>
 * method (when the instance is created) a {@link CDKException} will be thrown. The
 * most common problem is that the native code is not in the * the correct
 * location. Java searches the locations in the PATH environmental
 * variable, under Windows, and LD_LIBRARY_PATH under Linux, so the JNI-InChI
 * native libraries must be in one of these locations. If the JNI-InChI jar file
 * is being used and either the current working directory, or '.' are contained
 * in PATH of LD_LIBRARY_PATH then the native code should be placed
 * automatically. If the native files are in the correct location but fail to
 * load, then they may need to be recompiled for your system. See:
 * <ul>
 * <li>http://sourceforge.net/projects/jni-inchi
 * <li>http://www.iupac.org/inchi/
 * </ul>
 *
 * @author Sam Adams
 *
 * @cdk.module inchi
 * @cdk.githash
 */
public class InChIGeneratorFactory {

    private static InChIGeneratorFactory INSTANCE;

    /**
     * If the CDK aromaticity flag should be ignored and the bonds treated solely as single and double bonds.
     */
    private boolean                      ignoreAromaticBonds = true;

    /**
     * <p>Constructor for InChIGeneratorFactory. Ensures that native code
     * required for InChI/Structure interconversion is available, otherwise
     * throws CDKException.
     *
     * @throws CDKException if unable to load native code
     */
    private InChIGeneratorFactory() throws CDKException {
        try {
            JniInchiWrapper.loadLibrary();
        } catch (LoadNativeLibraryException lnle) {
            throw new CDKException("Unable to load native code; " + lnle.getMessage(), lnle);
        }
    }

    /**
     * Gives the one <code>InChIGeneratorFactory</code> instance,
     * if needed also creates it.
     *
     * @return the one <code>InChIGeneratorFactory</code> instance
     * @throws CDKException if unable to load native code when attempting
     * to create the factory
     */
    public static InChIGeneratorFactory getInstance() throws CDKException {
        synchronized (InChIGeneratorFactory.class) {
            if (INSTANCE == null) {
                INSTANCE = new InChIGeneratorFactory();
            }
            return INSTANCE;
        }
    }

    /**
     * Sets whether aromatic bonds should be treated as single and double bonds for the InChI generation. The bond type
     * INCHI_BOND_TYPE.ALTERN is considered special in contrast to single, double, and triple bonds,
     * and is not bulletproof. If the molecule has clearly defined single and double bonds,
     * the option can be used to force the class not to use the alternating bond type.
     * <p/>
     * http://www.inchi-trust.org/fileadmin/user_upload/html/inchifaq/inchi-faq.html#16.3
     *
     * @param ignore if aromatic bonds should be treated as bonds of type single and double
     * @deprecated "the use of aromatic bonds is strongly discouraged" - InChI
     *             FAQ, the InChI will fail for many compounds if ignore
     *             aromatic bonds is not enabled and the compound have aromatic
     *             flags.
     */
    @Deprecated
    public void setIgnoreAromaticBonds(boolean ignore) {
        ignoreAromaticBonds = ignore;
    }

    /**
     * Returns whether aromatic bonds are treated as single and double bonds for the InChI generation.
     *
     * @return if aromatic bonds are treated as bonds of type single and double
     */
    @Deprecated
    public boolean getIgnoreAromaticBonds() {
        return ignoreAromaticBonds;
    }

    /**
     * Gets an Standard InChI generator for a {@link IAtomContainer}. AuxInfo is not
     * generated by this method, please use {@link #getInChIGenerator(IAtomContainer, List)}
     * with no options specified if you would like to generate AuxInfo.
     *
     * @param container     AtomContainer to generate InChI for.
     * @return the InChI generator object
     * @throws CDKException if the generator cannot be instantiated
     */
    public InChIGenerator getInChIGenerator(IAtomContainer container) throws CDKException {
        return (new InChIGenerator(container, ignoreAromaticBonds));
    }

    /**
     * Gets InChI generator for CDK IAtomContainer.
     *
     * @param container     AtomContainer to generate InChI for.
     * @param options       String of options for InChI generation.
     * @return the InChI generator object
     * @throws CDKException if the generator cannot be instantiated
     */
    public InChIGenerator getInChIGenerator(IAtomContainer container, String options) throws CDKException {
        return (new InChIGenerator(container, options, ignoreAromaticBonds));
    }

    /**
     * Gets InChI generator for CDK IAtomContainer.
     *
     * @param container     AtomContainer to generate InChI for.
     * @param options       List of options (net.sf.jniinchi.INCHI_OPTION) for InChI generation.
     * @return the InChI generator object
     * @throws CDKException  if the generator cannot be instantiated
     */
    public InChIGenerator getInChIGenerator(IAtomContainer container, List<INCHI_OPTION> options) throws CDKException {
        if (options == null) throw new IllegalArgumentException("Null options");
        return (new InChIGenerator(container, options, ignoreAromaticBonds));
    }

    /**
     * Gets structure generator for an InChI string.
     *
     * @param inchi         InChI to generate structure from.
     * @param builder the builder to use
     * @return   the InChI structure generator object
     * @throws CDKException    if the generator cannot be instantiated
     */
    public InChIToStructure getInChIToStructure(String inchi, IChemObjectBuilder builder) throws CDKException {
        return (new InChIToStructure(inchi, builder));
    }

    /**
     * <p>Gets structure generator for an InChI string.
     *
     * @param inchi         InChI to generate structure from.
     * @param builder the builder to employ
     * @param options       String of options for structure generation.
     * @return   the InChI structure generator object
     * @throws CDKException    if the generator cannot be instantiated
     */
    public InChIToStructure getInChIToStructure(String inchi, IChemObjectBuilder builder, String options)
            throws CDKException {
        return (new InChIToStructure(inchi, builder, options));
    }

    /**
     * <p>Gets structure generator for an InChI string.
     *
     * @param inchi         InChI to generate structure from.
     * @param options       List of options (net.sf.jniinchi.INCHI_OPTION) for structure generation.
     * @param builder the builder to employ
     * @return   the InChI structure generator object
     * @throws CDKException    if the generator cannot be instantiated
     */
    public InChIToStructure getInChIToStructure(String inchi, IChemObjectBuilder builder, List<String> options)
            throws CDKException {
        return (new InChIToStructure(inchi, builder, options));
    }
}
