/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2006-2007  Sam Adams <sea36@users.sf.net>
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

import net.sf.jniinchi.JniInchiWrapper;
import net.sf.jniinchi.LoadNativeLibraryException;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import java.util.List;

/**
 * <p>Factory providing access to InChIGenerator and InChIToStructure. See those
 * classes for examples of use. These methods make use of the JNI-InChI library.
 * 
 * <p>InChI/Structure interconversion is implemented in this way so that we can
 * check whether or not the native code required is available. If the native
 * code cannot be loaded a CDKException will be thrown when the constructor
 * is called. The most common problem is that the native code is not in the
 * the correct location. Java searches the locations in the PATH environmental
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
 * @cdk.svnrev  $Revision$
 */
public class InChIGeneratorFactory {
    
    /**
     * <p>Constructor for InChIGeneratorFactory. Ensures that native code
     * required for InChI/Structure interconversion is available, otherwise
     * throws CDKException.
     * 
     * @throws CDKException if unable to load native code
     */
    public InChIGeneratorFactory() throws CDKException {
        try {
            JniInchiWrapper.loadLibrary();
        } catch (LoadNativeLibraryException lnle) {
            throw new CDKException("Unable to load native code; " + lnle.getMessage(), lnle);
        }
    }
    
    /**
     * Gets InChI generator for CDK IAtomContainer.
     * 
     * @param container     AtomContainer to generate InChI for.
     * @return the InChI generator object
     * @throws CDKException if the generator cannot be instantiated
     */
    public InChIGenerator getInChIGenerator(IAtomContainer container) throws CDKException {
        return(new InChIGenerator(container));
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
        return(new InChIGenerator(container, options));
    }
    
    /**
     * Gets InChI generator for CDK IAtomContainer.
     * 
     * @param container     AtomContainer to generate InChI for.
     * @param options       List of options (net.sf.jniinchi.INCHI_OPTION) for InChI generation.
     * @return the InChI generator object
     * @throws CDKException  if the generator cannot be instantiated
     */
    public InChIGenerator getInChIGenerator(IAtomContainer container, List options) throws CDKException {
        return(new InChIGenerator(container, options));
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
        return(new InChIToStructure(inchi, builder));
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
    public InChIToStructure getInChIToStructure(String inchi, IChemObjectBuilder builder, String options) throws CDKException {
        return(new InChIToStructure(inchi, builder, options));
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
    public InChIToStructure getInChIToStructure(String inchi, IChemObjectBuilder builder, List options) throws CDKException {
        return(new InChIToStructure(inchi, builder, options));
    }
}
