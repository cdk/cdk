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

import _ES6.InChIWeb;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import io.github.dan2097.jnainchi.InchiFlag;
import io.github.dan2097.jnainchi.InchiOptions;
import net.sf.jniinchi.INCHI_OPTION;

/**
 * 
 * This class modified by Bob Hanson to allow for dual platform JNA-InChI/Java
 * and INCHI-WEB WASM/JavaScript access. n
 * 
 * Note that JavaScript initialization of the WASM is asychonous. Be sure to
 * leave a time out or access InChiGeneratorFactory.getInstance() prior to a
 * Timer event (but not Thread.sleep(), which is not supported in JavaScript).
 * 
 * <p>
 * Factory providing access to {@link InChIGenerator} and
 * {@link InChIToStructure}. See those classes for examples of use. These
 * methods make use of the JNA-InChI library.
 * 
 * The two WASM-related files are in the _ES6/ folder. 
 * Do not try to compress these files with Google Closure Compiler.
 *
 * <p>
 * The {@link InChIGeneratorFactory} is a singleton class, which means that
 * there exists only one instance of the class. An instance of this class is
 * obtained with:
 * 
 * <pre>
 * InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
 * </pre>
 *
 * <p>
 * InChI/Structure interconversion is implemented in this way so that we can
 * check whether or not the native code required is available. If the native
 * code cannot be loaded during the first call to <code>getInstance</code>
 * method (when the instance is created) a {@link CDKException} will be thrown.
 * The most common problem is that the native code is not in the * the correct
 * location. Java searches the locations in the PATH environmental variable,
 * under Windows, and LD_LIBRARY_PATH under Linux, so the JNI-InChI native
 * libraries must be in one of these locations. If the JNI-InChI jar file is
 * being used and either the current working directory, or '.' are contained in
 * PATH of LD_LIBRARY_PATH then the native code should be placed automatically.
 * If the native files are in the correct location but fail to load, then they
 * may need to be recompiled for your system. See:
 * <ul>
 * <li>http://sourceforge.net/projects/jni-inchi
 * <li>http://www.iupac.org/inchi/
 * </ul>
 *
 * @author Sam Adams
 * @cdk.module inchi
 * @cdk.githash
 */
public class InChIGeneratorFactory {

	/**
	 * SwinJS code to allow platform switching; note that JavaScript is detected
	 * using a JavaDoc at.j2sNative tag;
	 * 
	 * do not set this final, as the java2script transpiler needs to leave the
	 * unevaluated javadoc.
	 */
	private /*nonfinal*/ static boolean isJS = (/** @j2sNative true || */false);
	
	static {
		if (isJS)
			InChIWeb.importWASM();
	}

    /**
     * Run the method immediately in Java or only after 
     * ensuring that JavaScript has initialized. 
     * 
     * @param r Runnable to run.
     */
	private void initAndRun(Runnable r) {
		/**
		 * @j2sNative
		 *    
		 *    if (!J2S) {
		 *      alert("J2S has not been installed");
		 *      System.exit(0);
		 *    }
		 *   var t = [];
		 *   t[0] = setInterval(
		 *      function(){
		 *       if (J2S.inchiWasmLoaded && J2S.inchiWasmLoaded()) {
		 *        clearInterval(t[0]);
		 *        System.out.println("InChI WASM initialized successfully");
		 *        r.run$();
		 *       }
		 *      }, 50);
		 */
		{
			r.run();
		}
	}

	/**
	 * These private methods select between JavaScript-WASM and JNA-InChI interface
	 * 
	 * @return the appropriate interface
	 */
	private static InChIGenerator getInChIGeneratorImpl() {
		return (isJS ? new InChIGeneratorJS() : new InChIGeneratorJNA());
	}

	private static InChIToStructure getInChIToStructureImpl() {
		return (isJS ? new InChIToStructureJS() : new InChIToStructureJNA());
	}

    private static InChIGeneratorFactory INSTANCE;

    /**
     * If the CDK aromaticity flag should be ignored and the bonds treated solely as single and double bonds.
     */
    @Deprecated
    private boolean ignoreAromaticBonds = true;

    private static InchiOptions DEFAULT_OPTIONS;

    private static InchiOptions getDefaultOptions() {
    	// lazy definition, only if needed
		if (DEFAULT_OPTIONS == null) {
			DEFAULT_OPTIONS = new InchiOptions.InchiOptionsBuilder()
		            .withFlag(InchiFlag.AuxNone)
		            .withTimeoutMilliSeconds(5000)
		            .build();
		}
		return DEFAULT_OPTIONS;
	}

    
    /**
     * <p>private constructor for InChIGeneratorFactory. 
     */
    private InChIGeneratorFactory() {
    }

    /**
     * Gives the one <code>InChIGeneratorFactory</code> instance,
     * if needed also creates it.
     *
     * @return the one <code>InChIGeneratorFactory</code> instance
     */
    public static InChIGeneratorFactory getInstance() {
    	return getInstance(null);
    }
    
    /**
     * This method allows for asynchronous JavaScript initialization of the WASM module.
     * 
     * @param r
     * @return
     */
    public static InChIGeneratorFactory getInstance(Runnable r) {
        synchronized (InChIGeneratorFactory.class) {
            if (INSTANCE == null) {
                INSTANCE = new InChIGeneratorFactory();
            }
            if (r != null) {
            	INSTANCE.initAndRun(r);
            }
            return INSTANCE;
        }
	}

	/**
     * Sets whether aromatic bonds should be treated as single and double bonds for the InChI generation. The bond type
     * INCHI_BOND_TYPE.ALTERN is considered special in contrast to single, double, and triple bonds,
     * and is not bulletproof. If the molecule has clearly defined single and double bonds,
     * the option can be used to force the class not to use the alternating bond type.
     * <p>
     * http://www.inchi-trust.org/fileadmin/user_upload/html/inchifaq/inchi-faq.html#16.3
     *
     * @param ignore if aromatic bonds should be treated as bonds of type single and double
     * @deprecated "the use of aromatic bonds is strongly discouraged" - InChI
     * FAQ, the InChI will fail for many compounds if ignore
     * aromatic bonds is not enabled and the compound have aromatic
     * flags.
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
     * @param container AtomContainer to generate InChI for.
     * @return the InChI generator object
     * @throws CDKException if the generator cannot be instantiated
     */
    public InChIGenerator getInChIGenerator(IAtomContainer container) throws CDKException {
        return getInChIGenerator(container, getDefaultOptions());
    }

    /**
     * Gets InChI generator for CDK IAtomContainer.
     *
     * @param container AtomContainer to generate InChI for.
     * @param options   Space or comma delimited string of options for InChI generation.
     * @return the InChI generator object
     * @throws CDKException if the generator cannot be instantiated
     */
    public InChIGenerator getInChIGenerator(IAtomContainer container, String options) throws CDKException {
        return getInChIGenerator(container, InChIOptionParser.parseString(options));
    }

	/**
	 * Get an InChI generator providing flags to customise the generation. If you
	 * need to provide a timeout the method that accepts an
	 * {@link io.github.dan2097.jnainchi.InchiOptions} should be used.
	 * 
	 * @param container the molecule
	 * @param flags     the option flags
	 * @return the InChI generator
	 * @throws CDKException something went wrong
	 * @see #getInChIGenerator(org.openscience.cdk.interfaces.IAtomContainer,
	 *      io.github.dan2097.jnainchi.InchiOptions)
	 */
	public InChIGenerator getInChIGenerator(IAtomContainer container, InchiFlag... flags) throws CDKException {
		// BH why this ? if (flags == null) throw new IllegalArgumentException("Null flags");
		InchiOptions options = (flags == null ? getDefaultOptions() : new InchiOptions.InchiOptionsBuilder().withFlag(flags).build());
		return getInChIGenerator(container, options);
	}

	/**
	 * Get an InChI generator providing options in the form of a precompiled
	 * InchiOptions object or default options.
	 * 
	 * All public methods go through here.
	 * 
	 * @param container the molecule
	 * @param options   the inchi option flags or null for default options
	 * @return the InChI generator
	 * @throws CDKException something went wrong
	 */
	public InChIGenerator getInChIGenerator(IAtomContainer container, InchiOptions options) throws CDKException {
		if (options == null)
			options = DEFAULT_OPTIONS;
			// BH why this? throw new IllegalArgumentException("Null flags");
		return getInChIGeneratorImpl().generateInChI(container, options, ignoreAromaticBonds);
	}


	/**
	 * Gets an InChI generator for CDK IAtomContainer using converted JNI-InChI options.
	 *
	 * @param container AtomContainer to generate InChI for.
	 * @param jniOptions   List of options (net.sf.jniinchi.INCHI_OPTION) for InChI
	 *                  generation.
	 * @return the InChI generator object
	 * @throws CDKException if the generator cannot be instantiated
	 * @deprecated use
	 *             {@link #getInChIGenerator(org.openscience.cdk.interfaces.IAtomContainer, io.github.dan2097.jnainchi.InchiOptions)}
	 */
	@Deprecated
	public InChIGenerator getInChIGenerator(IAtomContainer container, List<INCHI_OPTION> jniOptions) throws CDKException {
		if (jniOptions == null)
			throw new IllegalArgumentException("Null options");
        InchiOptions.InchiOptionsBuilder builder = new InchiOptions.InchiOptionsBuilder();
        for (INCHI_OPTION jniOpt : jniOptions) {
            InchiFlag flag = JniInchiSupport.toJnaOption(jniOpt);
            if (flag != null)
                builder.withFlag(flag);
        }
		return getInChIGenerator(container, builder.build());
	}

	/**
     * Gets structure generator for an InChI string using default options.
     *
     * @param inchi   InChI to generate structure from.
     * @param builder the builder to use
     * @return the InChI structure generator object
     * @throws CDKException if the generator cannot be instantiated
     */
    public InChIToStructure getInChIToStructure(String inchi, IChemObjectBuilder builder) throws CDKException {
        return getInChIToStructure(inchi, builder, getDefaultOptions());
    }

	/**
     * <p>Gets structure generator for an InChI string.
     *
     * @param inchi   InChI to generate structure from.
     * @param builder the builder to employ
     * @param options String of options for structure generation.
     * @return the InChI structure generator object
     * @throws CDKException if the generator cannot be instantiated
     */
    public InChIToStructure getInChIToStructure(String inchi, IChemObjectBuilder builder, String options)
            throws CDKException {
        return getInChIToStructure(inchi, builder, InChIOptionParser.parseString(options));
    }

	/**
     * <p>Gets structure generator for an InChI string.
     *
     * @param inchi   InChI to generate structure from.
     * @param options List of options (net.sf.jniinchi.INCHI_OPTION) for structure generation.
     * @param builder the builder to employ
     * @return the InChI structure generator object
     * @throws CDKException if the generator cannot be instantiated
     */
    public InChIToStructure getInChIToStructure(String inchi, IChemObjectBuilder builder, List<String> options)
            throws CDKException {
        return getInChIToStructure(inchi, builder, InChIOptionParser.parseStrings(options));
    }
    
    /**
     * All public methods go through here.
     * 
     * @param inchi   InChI to generate structure from.
     * @param options precompiled InchiOptions for structure generation.
     * @param builder the builder to employ
     * @return the InChI structure generator object
     * @throws CDKException if the generator cannot be instantiated
     */
    public InChIToStructure getInChIToStructure(String inchi, IChemObjectBuilder builder, InchiOptions options)
            throws CDKException {
        return getInChIToStructureImpl().getStructure(inchi, builder, options);
    }

}
