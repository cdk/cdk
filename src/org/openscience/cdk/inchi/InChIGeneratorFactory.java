package org.openscience.cdk.inchi;

import java.util.List;

import net.sf.jniinchi.JniInchiWrapper;
import net.sf.jniinchi.LoadNativeLibraryException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

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
 */
public class InChIGeneratorFactory {
    
    /**
     * <p>Constructor for InChIGeneratorFactory. Ensures that native code
     * required for InChI/Structure interconversion is available, otherwise
     * throws CDKException.
     * 
     * @throws CDKException
     */
    public InChIGeneratorFactory() throws CDKException {
        try {
            JniInchiWrapper.loadLibrary();
        } catch (LoadNativeLibraryException lnle) {
            throw new CDKException("Unable to load native code; " + lnle.getMessage());
        }
    }
    
    /**
     * <p>Gets InChI generator for CDK IAtomContainer.
     * 
     * @param container     AtomContainer to generate InChI for.
     */
    public InChIGenerator getInChIGenerator(IAtomContainer container) throws CDKException {
        return(new InChIGenerator(container));
    }
    
    /**
     * <p>Gets InChI generator for CDK IAtomContainer.
     * 
     * @param container     AtomContainer to generate InChI for.
     * @param options       String of options for InChI generation.
     * @return
     * @throws CDKException
     */
    public InChIGenerator getInChIGenerator(IAtomContainer container, String options) throws CDKException {
        return(new InChIGenerator(container, options));
    }
    
    /**
     * <p>Gets InChI generator for CDK IAtomContainer.
     * 
     * @param container     AtomContainer to generate InChI for.
     * @param options       List of options (net.sf.jniinchi.INCHI_OPTION) for InChI generation.
     * @return
     * @throws CDKException
     */
    public InChIGenerator getInChIGenerator(IAtomContainer container, List options) throws CDKException {
        return(new InChIGenerator(container, options));
    }
    
    /**
     * <p>Gets structure generator for an InChI string.
     * 
     * @param inchi         InChI to generate structure from.
     * @return
     * @throws CDKException
     */
    public InChIToStructure getInChIToStructure(String inchi) throws CDKException {
        return(new InChIToStructure(inchi));
    }
    
    /**
     * <p>Gets structure generator for an InChI string.
     * 
     * @param inchi         InChI to generate structure from.
     * @param options       String of options for structure generation.
     * @return
     * @throws CDKException
     */
    public InChIToStructure getInChIToStructure(String inchi, String options) throws CDKException {
        return(new InChIToStructure(inchi, options));
    }
    
    /**
     * <p>Gets structure generator for an InChI string.
     * 
     * @param inchi         InChI to generate structure from.
     * @param options       List of options (net.sf.jniinchi.INCHI_OPTION) for structure generation.
     * @return
     * @throws CDKException
     */
    public InChIToStructure getInChIToStructure(String inchi, List options) throws CDKException {
        return(new InChIToStructure(inchi, options));
    }
}
