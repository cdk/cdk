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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Writer;

import io.github.dan2097.jnainchi.InchiOptions;
import io.github.dan2097.jnainchi.InchiStatus;
import net.sf.jniinchi.INCHI_RET;

/**
 * This class implements inchi-web.wasm from InChI-SwingJS, which is adapted
 * from https://github.com/IUPAC-InChI/InChI-Web-Demo by Bob Hanson and Josh
 * Charlton 2025.01.23-2025.01.24.
 * 
 * In the case of a Jmol model for mdel to InChI, we first generate MOL file
 * data.
 * 
 * For InChI to SMILES, we use the inchi-web.c method model_from_inchi() that we
 * developed with the assistance of Frank Lange.
 * 
 * The class originally adapted Richard Apodaca's 2020 molfile-to-inchi
 * LLVM-derived Web Assembly implementation of IUPAC InChI v. 1.05. see
 * https://depth-first.com/articles/2020/03/02/compiling-inchi-to-webassembly-part-2-from-molfile-to-inchi/
 * 
 * Note that this initialiation is asynchronous.
 * 
 * 
 */
public class InChIGeneratorJS implements InChIGenerator {

	private String soptions;

	/**
	 * a JavaScript map
	 */
	private Map<String, Object> output;

	private int retCode;

	private String inchi;
    
    @Override
	public InChIGenerator generateInChI(IAtomContainer atomContainer,
                             InchiOptions options,
                             boolean ignoreAromaticBonds) throws CDKException {
    	soptions = (options == null ? "" : options.toString());
        generateInchiFromCDKAtomContainer(atomContainer, ignoreAromaticBonds);
        return this;
    }

	/**
	 * <p>
	 * Reads atoms, bonds etc from atom container and converts to format InChI
	 * library requires, then places call for the library to generate the InChI.
	 *
	 * @param atomContainer AtomContainer to generate InChI for.
	 * @param ignore        Ignore aromatic bonds
	 * @throws CDKException
	 */
	@SuppressWarnings("resource")
	private void generateInchiFromCDKAtomContainer(IAtomContainer atomContainer, boolean ignore) throws CDKException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			
			new MDLV2000Writer(bos).setDate("").write(atomContainer);
			String moldata = bos.toString();
			System.out.println(moldata);
			inchi = execute("inchiFromMolfile", moldata, soptions, "inchi");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Execute J2S[method](data, optoins) to return a JSON structure, and
	 * select from that a given key. 
	 * 
	 * If key is null, just return the method itself. (Used to confirm that the
	 * module has loaded.)
	 * 
	 * Otherwise, execute the InChI-web-SwingJS method with the given data and
	 * options and return the STRING value JSON structure delivered by that key.
	 * 
	 * @param method
	 * @param options
	 * @param key
	 * @return the JSON value requested or the method requested
	 */
	private String execute(String method, String data, String options, String key) {
		Map<String, Object> output = null;
		String ret = null;
		int retCode = 0;
		/**
		 * @j2sNative output = J2S[method](data, options) || {};
		 *         ret = (output ? output[key] : null) || null;
		 *         retCode = (output == null ? -1 : output.ret_code);    
		 */
		this.output = output;
		this.retCode = retCode;
		return ret;
	}
	
	String getJSOutput(String key) {
    	Map<String, Object> output = this.output;
        return (output == null ? null : /** @j2sNative output[key] || */"");
	}

    /**
     * 
     * JNI-InChI interface is supported in JavaScript
     * 
     * Gets return status from InChI process.  OKAY and WARNING indicate
     * InChI has been generated, in all other cases InChI generation
     * has failed. This returns the JNI INCHI enum and requires the optional
     * "cdk-jniinchi-support" module to be loaded (or the full JNI InChI lib
     * to be on the class path).
     * @deprecated use {@link #getStatus()}
     */
    @Override
	@Deprecated
    public INCHI_RET getReturnStatus() {
    	return null;
//        return JniInchiSupport.toJniStatus(output.getStatus());
    }

    /**
     * Access the status of the InChI output.
     * @return the status
     */
    @Override
	public InchiStatus getStatus() {
    	switch (retCode) {
    	case 0:
    		return InchiStatus.SUCCESS;
    	case -1:
    		return InchiStatus.ERROR;
    	default:	
    		return InchiStatus.WARNING;   	
    	}
    }

    /**
     * Gets generated InChI string.
     */
    @Override
	public String getInchi() {
    	return inchi;
    }

    /**
     * Gets generated InChIKey string.
     */
    @Override
	public String getInchiKey() throws CDKException {
    	String key = execute("inchikeyFromInchi", inchi, soptions, "inchikey");
    	if (retCode == 0)
    		return key;
            throw new CDKException("Error while creating InChIKey: " + getMessage());
    }

    /**
     * Gets auxiliary information.
     */
    @Override
	public String getAuxInfo() {
    	return getJSOutput("auxinfo");
    }

    /**
     * Gets generated (error/warning) messages.
     */
    @Override
	public String getMessage() {
    	return getJSOutput("message");
    }

    /**
     * Gets generated log.
     */
    @Override
	public String getLog() {
    	return getJSOutput("log");
    }
}
