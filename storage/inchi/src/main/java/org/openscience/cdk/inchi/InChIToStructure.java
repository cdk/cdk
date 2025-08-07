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

import io.github.dan2097.jnainchi.InchiInputFromInchiOutput;
import io.github.dan2097.jnainchi.InchiOptions;
import io.github.dan2097.jnainchi.InchiStatus;
import io.github.dan2097.jnainchi.JnaInchi;
import net.sf.jniinchi.INCHI_RET;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.List;

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
 * <br>
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
 */
public class InChIToStructure {

    protected InchiInputFromInchiOutput output;

    protected InchiOptions options;

    protected IAtomContainer          molecule;

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(InChIToStructure.class);

    /**
     * Constructor. Generates CDK AtomContainer from InChI.
     * @param inchi
     * @throws CDKException
     */
    protected InChIToStructure(String inchi, IChemObjectBuilder builder, InchiOptions options) throws CDKException {
        if (inchi == null)
            throw new IllegalArgumentException("Null InChI string provided");
        if (options == null)
            throw new IllegalArgumentException("Null options provided");
        this.output = JnaInchi.getInchiInputFromInchi(inchi);
        this.options = options;
        this.molecule = InChIOutputToStructure.generateAtomContainerFromInChIOutput(this.output, builder);
    }

    /**
     * Constructor. Generates CDK AtomContainer from InChI.
     * @param inchi
     * @throws CDKException
     */
    protected InChIToStructure(String inchi, IChemObjectBuilder builder) throws CDKException {
        this(inchi, builder, new InchiOptions.InchiOptionsBuilder().build());
    }

    /**
     * Constructor. Generates CMLMolecule from InChI.
     * @param inchi
     * @param options
     * @throws CDKException
     */
    protected InChIToStructure(String inchi, IChemObjectBuilder builder, String options) throws CDKException {
        this(inchi, builder, InChIOptionParser.parseString(options));
    }

    /**
     * Constructor. Generates CMLMolecule from InChI.
     * @param inchi
     * @param options
     * @throws CDKException
     */
    protected InChIToStructure(String inchi, IChemObjectBuilder builder, List<String> options) throws CDKException {
        this(inchi, builder, InChIOptionParser.parseStrings(options));
    }

    /**
     * Returns generated molecule.
     * @return An AtomContainer object
     */
    public IAtomContainer getAtomContainer() {
        return (molecule);
    }

    /**
     * Gets return status from InChI process.  OKAY and WARNING indicate
     * InChI has been generated, in all other cases InChI generation
     * has failed. This returns the JNI INCHI enum and requires the optional
     * "cdk-jniinchi-support" module to be loaded (or the full JNI InChI lib
     * to be on the class path).
     * @deprecated use getStatus
     */
    @Deprecated
    public INCHI_RET getReturnStatus() {
        return JniInchiSupport.toJniStatus(output.getStatus());
    }

    /**
     * Access the status of the InChI output.
     * @return the status
     */
    public InchiStatus getStatus() {
        return output.getStatus();
    }

    /**
     * Gets generated (error/warning) messages.
     */
    public String getMessage() {
        return output.getMessage();
    }

    /**
     * Gets generated log.
     */
    public String getLog() {
        return output.getLog();
    }

    /**
     * <p>Returns warning flags, see INCHIDIFF in inchicmp.h.
     *
     * <p>[x][y]:
     * <br>x=0 =&gt; Reconnected if present in InChI otherwise Disconnected/Normal
     * <br>x=1 =&gt; Disconnected layer if Reconnected layer is present
     * <br>y=1 =&gt; Main layer or Mobile-H
     * <br>y=0 =&gt; Fixed-H layer
     */
    public long[][] getWarningFlags() {
        return output.getWarningFlags();
    }

}
