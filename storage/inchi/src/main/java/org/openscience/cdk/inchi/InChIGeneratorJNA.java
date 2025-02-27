/* Copyright (C) 2006-2007  Sam Adams <sea36@users.sf.net>
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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import io.github.dan2097.jnainchi.InchiFlag;
import io.github.dan2097.jnainchi.InchiOptions;
import io.github.dan2097.jnainchi.InchiStatus;
import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;

/**
 * <p>This class generates the IUPAC International Chemical Identifier (InChI) for
 * a CDK IAtomContainer. It places calls to a JNI wrapper for the InChI C++ library.
 *
 * <p>If the atom container has 3D coordinates for all of its atoms then they
 * will be used, otherwise 2D coordinates will be used if available.
 *
 * <p><i>Spin multiplicities and some aspects of stereochemistry are not
 * currently handled completely.</i>
 * <b>Example usage</b><br/>
 *
 * <code>// Generate factory - throws CDKException if native code does not load</code><br>
 * <code>InChIGeneratorFactory factory = new InChIGeneratorFactory();</code><br>
 * <code>// Get InChIGenerator</code><br>
 * <code>InChIGenerator gen = factory.getInChIGenerator(container);</code><br>
 * <code></code><br>
 * <code>INCHI_RET ret = gen.getReturnStatus();</code><br>
 * <code>if (ret == INCHI_RET.WARNING) {</code><br>
 * <code>  // InChI generated, but with warning message</code><br>
 * <code>  System.out.println("InChI warning: " + gen.getMessage());</code><br>
 * <code>} else if (ret != INCHI_RET.OKAY) {</code><br>
 * <code>  // InChI generation failed</code><br>
 * <code>  throw new CDKException("InChI failed: " + ret.toString()</code><br>
 * <code>    + " [" + gen.getMessage() + "]");</code><br>
 * <code>}</code><br>
 * <code></code><br>
 * <code>String inchi = gen.getInchi();</code><br>
 * <code>String auxinfo = gen.getAuxInfo();</code><br>
 * <p>
 * <b>
 * TODO: distinguish between singlet and undefined spin multiplicity<br>
 * TODO: double bond and allene parities<br>
 * TODO: problem recognising bond stereochemistry<br>
 * </b>
 *
 * @author Sam Adams
 * @cdk.module inchi
 * @cdk.githash
 */
public abstract class InChIGeneratorJNA {

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
    
	protected final InchiOptions options;
	
	protected final boolean auxNone;

    /**
     * (never referenced)
     * 
     * AtomContainer instance refers to.
     */
    protected IAtomContainer atomContainer;
    
	protected InChIGeneratorJNA(IAtomContainer atomContainer,
			                 InchiOptions options,
			                 boolean ignoreAromaticBonds) throws CDKException {
        this.atomContainer = atomContainer;
		this.options = (options == null ? getDefaultOptions() : options);
		generateInchiFromCDKAtomContainer(atomContainer, ignoreAromaticBonds);
		auxNone = this.options.getFlags().contains(InchiFlag.AuxNone);
	}

	/**
     * <p>Constructor. Generates InChI from CDK AtomContainer.
     *
     * <p>Reads atoms, bonds etc from atom container and converts to format
     * InChI library requires, then calls the library.
     *
     * @param atomContainer       AtomContainer to generate InChI for.
     * @param ignoreAromaticBonds if aromatic bonds should be treated as bonds of type single and double
     * @throws org.openscience.cdk.exception.CDKException if there is an
     *                                                    error during InChI generation
     */
    protected InChIGeneratorJNA(IAtomContainer atomContainer, boolean ignoreAromaticBonds) throws CDKException {
        this(atomContainer, getDefaultOptions(), ignoreAromaticBonds);
    }

	/**
     * <p>Constructor. Generates InChI from CDK AtomContainer.
     *
     * <p>Reads atoms, bonds etc from atom container and converts to format
     * InChI library requires, then calls the library.
     *
     * @param atomContainer       AtomContainer to generate InChI for.
     * @param optStr              Space or comma delimited string of options to pass to InChI library.
     *                            Each option may optionally be preceded by a command line
     *                            switch (/ or -).
     * @param ignoreAromaticBonds if aromatic bonds should be treated as bonds of type single and double
     * @throws CDKException
     */
    protected InChIGeneratorJNA(IAtomContainer atomContainer, String optStr, boolean ignoreAromaticBonds)
    		throws CDKException {
    	this(atomContainer, InChIOptionParser.parseString(optStr), ignoreAromaticBonds);
	}

    private static InchiOptions convertJniToJnaOpts(List<INCHI_OPTION> jniOpts) {
        InchiOptions.InchiOptionsBuilder builder = new InchiOptions.InchiOptionsBuilder();
        for (INCHI_OPTION jniOpt : jniOpts) {
            InchiFlag flag = JniInchiSupport.toJnaOption(jniOpt);
            if (flag != null)
                builder.withFlag(flag);
        }
        return builder.build();
    }

    /**
     * <p>Constructor. Generates InChI from CDK AtomContainer.
     *
     * <p>Reads atoms, bonds etc from atom container and converts to format
     * InChI library requires, then calls the library.
     *
     * @param atomContainer       AtomContainer to generate InChI for.
     * @param opts                List of INCHI_OPTION.
     * @param ignoreAromaticBonds if aromatic bonds should be treated as bonds of type single and double
     * @throws CDKException
     */
    @Deprecated
    protected InChIGeneratorJNA(IAtomContainer atomContainer, List<INCHI_OPTION> opts, boolean ignoreAromaticBonds)
            throws CDKException {
        this(atomContainer, convertJniToJnaOpts(opts), ignoreAromaticBonds);
    }

	abstract protected void generateInchiFromCDKAtomContainer(IAtomContainer atomContainer, boolean ignoreAromaticBonds) throws CDKException;

    /**
     * Gets return status from InChI process.  OKAY and WARNING indicate
     * InChI has been generated, in all other cases InChI generation
     * has failed. This returns the JNI INCHI enum and requires the optional
     * "cdk-jniinchi-support" module to be loaded (or the full JNI InChI lib
     * to be on the class path).
     * @deprecated use {@link #getStatus()}
     */
	@Deprecated
    public abstract INCHI_RET getReturnStatus();

    /**
     * Access the status of the InChI output.
     * @return the status
     */
    public abstract InchiStatus getStatus();

    /**
     * Gets generated InChI string.
     */
    public abstract String getInchi();
    
    /**
     * Gets generated InChIKey string.
     */
    public abstract String getInchiKey() throws CDKException;

    /**
     * Gets auxiliary information.
     */
    public abstract String getAuxInfo();

    /**
     * Gets generated (error/warning) messages.
     */
    public abstract String getMessage();

    /**
     * Gets generated log.
     */
    public abstract String getLog();


}
