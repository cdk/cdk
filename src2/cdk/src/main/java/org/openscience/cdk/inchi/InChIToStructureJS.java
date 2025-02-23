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
import java.util.Map;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import io.github.dan2097.jnainchi.InchiOptions;
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
public class InChIToStructureJS extends InChIToStructure {

	private int retCode;

	@Override
	protected InChIToStructure set(String inchi, IChemObjectBuilder builder, InchiOptions options) throws CDKException {
		execute("modelFromInchi", inchi, options.toString(), "model");
		
		return this;
	}

	/**
	 * Execute J2S[method](data, options) to return a JSON structure, and
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


  //all javascript maps and arrays, only accessible through j2sNative.
  	List<Map<String, Object>> atoms, bonds, stereo;
  	private Map<String, Object> thisAtom;
  	private Map<String, Object> thisBond;
  	private Map<String, Object> thisStereo;
  	@SuppressWarnings("unused")
	private Map<String, Object> output;

  	@Override
  	public void initializeInchiModel(String inchi) {
  		/**
  		 * @j2sNative 
  		 * 
  		 * this.output = J2S.modelFromInchi(inchi);
  		 * 
  		 * var json = JSON.parse(output.model); 
  		 * this.atoms = (json.atoms || []); 
  		 * this.bonds = (json.bonds || []); 
  		 * this.stereo = (json.stereo || []);
  		 */
  	}

  	/// Atoms ///

  	@Override
  	public int getNumAtoms() {
  		/**
  		 * @j2sNative return this.atoms.length;
  		 */
  		{
  			return 0;
  		}
  	}

  	@Override
  	public void setAtom(int i) {
  		/**
  		 * @j2sNative this.thisAtom = this.atoms[i];
  		 */
  	}

  	@Override
  	public String getElementType() {
  		return getString(thisAtom, "elname", "");
  	}

  	@Override
  	public double getX() {
  		return getDouble(thisAtom, "x", 0);
  	}

  	@Override
  	public double getY() {
  		return getDouble(thisAtom, "y", 0);
  	}

  	@Override
  	public double getZ() {
  		return getDouble(thisAtom, "z", 0);
  	}

  	@Override
  	public int getCharge() {
  		return getInt(thisAtom, "charge", 0);
  	}

  	@Override
  	public int getImplicitH() {
  		return getInt(thisAtom, "implicitH", 0);
  	}

  	@Override
  	public int getIsotopicMass() {
  		int mass = 0;
  		/**
  		 * @j2sNative mass = this.thisAtom["isotopicMass"] || 0;
  		 */
  		{
  		}
  		return mass;
  	}

	@Override
	int getImplicitDeuterium() {
  		/**
  		 * @j2sNative 
  		 * 
  		 * return this.thisAtom["implicitDeuterium"] || 0;
  		 */
  		{
  	  		return 0;
  		}
	}

	@Override
	int getImplicitTritium() {
  		/**
  		 * @j2sNative 
  		 * 
  		 * return this.thisAtom["implicitTritium"] || 0;
  		 */
  		{
  	  		return 0;
  		}
	}

	@Override
	String getRadical() {
		String radical = "";
  		/**
  		 * @j2sNative 
  		 * 
  		 * radical = this.thisAtom.radical || "NONE";
  		 */
  		{
  	  		return radical;
  		}
	}

  	/// Bonds ///

  	@Override
  	public int getNumBonds() {
  		/**
  		 * @j2sNative return this.bonds.length;
  		 */
  		{
  			return 0;
  		}
  	}

  	@Override
  	public void setBond(int i) {
  		/**
  		 * @j2sNative this.thisBond = this.bonds[i];
  		 */
  	}

  	@Override
  	public int getIndexOriginAtom() {
  		return getInt(thisBond, "originAtom", 0);
  	}

  	@Override
  	public int getIndexTargetAtom() {
  		return getInt(thisBond, "targetAtom", 0);
  	}

  	@Override
  	public String getInchiBondType() {
  		return getString(thisBond, "type", "SINGLE");
  	}

  	/// Stereo ///

  	@Override
  	public int getNumStereo0D() {
  		/**
  		 * @j2sNative return this.stereo.length;
  		 */
  		{
  			return 0;
  		}
  	}

  	@Override
  	public void setStereo0D(int i) {
  		/**
  		 * @j2sNative this.thisStereo = this.stereo[i];
  		 */
  		{
  		}
  	}

  	@Override
  	public String getParity() {
  		return getString(thisStereo, "parity", "");
  	}

  	@Override
  	public String getStereoType() {
  		return getString(thisStereo, "type", "NONE");
  	}

  	@Override
  	public int getCenterAtom() {
  		return getInt(thisStereo, "centralAtom", -1);
  	}

  	@Override
  	public int[] getNeighbors() {
  		/**
  		 * @j2sNative return this.thisStereo.neighbors;
  		 */
  		{
  			return null;
  		}
  	}

  	private int getInt(Map<String, Object> map, String name, int defaultValue) {
  		/**
  		 * @j2sNative var val = map[name]; if (val || val == 0) return val;
  		 */
  		{
  		}
  		return defaultValue;
  	}

  	private double getDouble(Map<String, Object> map, String name, double defaultValue) {
  		/**
  		 * @j2sNative var val = map[name]; if (val || val == 0) return val;
  		 */
  		{
  		}
  		return defaultValue;
  	}

  	private String getString(Map<String, Object> map, String name, String defaultValue) {
  		/**
  		 * @j2sNative var val = map[name]; if (val || val == "") return val;
  		 */
  		{
  		}
  		return defaultValue;
  	}

	@Override
	String getInchIBondStereo() {
		String stereo = "";
  		/**
  		 * @j2sNative 
  		 * 
  		 * stereo = this.thisBond.stereo || "NONE";
  		 */
  		{
  	  		return stereo;
  		}
	}

	@Override
	public String getMessage() {
		/**
		 * return this.output.message;
		 */
		{
			return null;
		}
	}

	@Override
	public String getLog() {
		/**
		 * return this.output.log;
		 */
		{
			return null;
		}
	}

	@Override
	public long[][] getWarningFlags() {
		// not implemented
		return new long[2][2];
	}

	@Override
	public INCHI_RET getReturnStatus() {
		return null;
	}

	@Override
    /**
     * Access the status of the InChI output.
     * @return the status
     */
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



}
