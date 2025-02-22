/* Copyright (C) 2006-2007  Sam Adams <sea36@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General abstract public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General abstract public License for more details.
 *
 * You should have received a copy of the GNU Lesser General abstract public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.inchi;

import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import io.github.dan2097.jnainchi.InchiStatus;
import net.sf.jniinchi.INCHI_RET;

/**
 * <p>This class generates a CDK IAtomContainer from an InChI string.  It places
 * calls to a JNI wrapper for the InChI C++ library.
 *
 * <p>The generated IAtomContainer will have all 2D and 3D coordinates set to 0.0,
 * but may have atom parities set.  Double bond and allene stereochemistry are
 * not currently recorded.
 *
 * <br>
 * <b>Example usage</b>
 *
 * <code>// Generate factory - throws CDKException if native code does not load</code><br>
 * <code>InChIGeneratorFactory factory = new InChIGeneratorFactory();</code><br>
 * <code>// Get InChIToStructure</code><br>
 * <code>InChIToStructure intostruct = factory.getInChIToStructure(</code><br>
 * <code>  inchi, DefaultChemObjectBuilder.getInstance()</code><br>
 * <code>);</code><br>
 * <code></code><br>
 * <code>INCHI_RET ret = intostruct.getReturnStatus();</code><br>
 * <code>if (ret == INCHI_RET.WARNING) {</code><br>
 * <code>  // Structure generated, but with warning message</code><br>
 * <code>  System.out.println("InChI warning: " + intostruct.getMessage());</code><br>
 * <code>} else if (ret != INCHI_RET.OKAY) {</code><br>
 * <code>  // Structure generation failed</code><br>
 * <code>  throw new CDKException("Structure generation failed failed: " + ret.toString()</code><br>
 * <code>    + " [" + intostruct.getMessage() + "]");</code><br>
 * <code>}</code><br>
 * <code></code><br>
 * <code>IAtomContainer container = intostruct.getAtomContainer();</code><br>
 * <p><br>
 *
 * @author Sam Adams
 *
 * @cdk.module inchi
 * @cdk.githash
 */
public abstract class InChIToStructure {

    abstract InChIToStructure set(String inchi, IChemObjectBuilder builder) throws CDKException;

    abstract InChIToStructure set(String inchi, IChemObjectBuilder builder, String options) throws CDKException;

    abstract InChIToStructure set(String inchi, IChemObjectBuilder builder, List<String> options) throws CDKException;

    /**
     * Returns generated molecule.
     * @return An AtomContainer object
     */
    abstract public IAtomContainer getAtomContainer();
   
    /**
     * Gets return status from InChI process.  OKAY and WARNING indicate
     * InChI has been generated, in all other cases InChI generation
     * has failed. This returns the JNI INCHI enum and requires the optional
     * "cdk-jniinchi-support" module to be loaded (or the full JNI InChI lib
     * to be on the class path).
     * @deprecated use getStatus
     */
    @Deprecated
    abstract public INCHI_RET getReturnStatus();

    /**
     * Access the status of the InChI output.
     * @return the status
     */
    abstract public InchiStatus getStatus();

    /**
     * Gets generated (error/warning) messages.
     */
    abstract public String getMessage();

    /**
     * Gets generated log.
     */
    abstract public String getLog();

    /**
     * <p>Returns warning flags, see INCHIDIFF in inchicmp.h.
     *
     * <p>[x][y]:
     * <br>x=0 =&gt; Reconnected if present in InChI otherwise Disconnected/Normal
     * <br>x=1 =&gt; Disconnected layer if Reconnected layer is present
     * <br>y=1 =&gt; Main layer or Mobile-H
     * <br>y=0 =&gt; Fixed-H layer
     */
    abstract public long[][] getWarningFlags();

	
}
