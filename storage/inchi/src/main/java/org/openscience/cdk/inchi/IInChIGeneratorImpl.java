package org.openscience.cdk.inchi;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import io.github.dan2097.jnainchi.InchiOptions;
import io.github.dan2097.jnainchi.InchiStatus;
import net.sf.jniinchi.INCHI_RET;

public interface IInChIGeneratorImpl {

    /**
     * Gets return status from InChI process.  OKAY and WARNING indicate
     * InChI has been generated, in all other cases InChI generation
     * has failed. This returns the JNI INCHI enum and requires the optional
     * "cdk-jniinchi-support" module to be loaded (or the full JNI InChI lib
     * to be on the class path).
     * @deprecated use {@link #getStatus()}
     */
	@Deprecated
	
    INCHI_RET getReturnStatus();

    /**
     * Access the status of the InChI output.
     * @return the status
     */
	
    InchiStatus getStatus();

    /**
     * Gets generated InChI string.
     */
	
    String getInchi();
    
    /**
     * Gets generated InChIKey string.
     */
	
    String getInchiKey() throws CDKException;

    /**
     * Gets auxiliary information.
     */
	
    String getAuxInfo();

    /**
     * Gets generated (error/warning) messages.
     */
	
    String getMessage();

    /**
     * Gets generated log.
     */
	
    String getLog();

	void generateInchiFromCDKAtomContainer(IAtomContainer atomContainer, InchiOptions options,
			boolean ignoreAromaticBonds) throws CDKException;

}
