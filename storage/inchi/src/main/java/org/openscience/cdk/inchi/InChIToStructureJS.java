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

import io.github.dan2097.jnainchi.InchiStatus;
import net.sf.jniinchi.INCHI_RET;

/**
 * This JavaScript-specific class generates a CDK IAtomContainer from an InChI
 * string. It places calls to inchi-web.wasm.
 * 
 * @author Bob Hanson
 * @cdk.module inchi
 * @cdk.githash
 */
public class InChIToStructureJS extends InChIToStructureAbs {

	private int retCode;
	
  //all javascript maps and arrays, only accessible through j2sNative.
  	Object[] atoms, bonds, stereo;
  	private Object thisAtom;
  	private Object thisBond;
  	private Object thisStereo;
	private Map<String, Object> output;

  	public InChIToStructureJS(String inchi, IChemObjectBuilder builder) throws CDKException {
  		super(inchi, builder);
	}

	public InChIToStructureJS(String inchi, IChemObjectBuilder builder, String options) throws CDKException {
  		super(inchi, builder, options);
	}

	public InChIToStructureJS(String inchi, IChemObjectBuilder builder, List<String> options) throws CDKException {
  		super(inchi, builder, options);
	}

	@Override
  	public void initializeInchiModel(String inchi) {
  		/**
  		 * @j2sNative 
  		 * 
  		 * this.output = J2S.modelFromInchi(inchi);
  		 * 
  		 * var json = JSON.parse(this.output.model); 
  		 * this.atoms = (json.atoms || []); 
  		 * this.bonds = (json.bonds || []); 
  		 * this.stereo = (json.stereo || []);
  		 */
  	}

  	/// Atoms ///

  	@Override
  	public int getNumAtoms() {
  		return this.atoms.length;
  	}

  	@Override
  	public void setAtom(int i) {
  	    thisAtom = atoms[i];
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
  		return getInt(thisAtom, "isotopicMass", 0);
  	}

	@Override
	int getImplicitDeuterium() {
  		return getInt(thisAtom, "implicitDeuterium", 0);
	}

	@Override
	int getImplicitTritium() {
  		return getInt(thisAtom, "implicitTritium", 0);
	}

	@Override
	String getRadical() {
		return uc(getString(thisAtom, "radical", "NONE"));
	}

  	/// Bonds ///

  	@Override
  	public int getNumBonds() {
  		return bonds.length;  
  	}

  	@Override
  	public void setBond(int i) {
  		thisBond = bonds[i];
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
  		return uc(getString(thisBond, "type", "SINGLE"));
  	}

  	/// Stereo ///

  	@Override
  	public int getNumStereo0D() {
  		return stereo.length;
  	}

	@Override
  	public void setStereo0D(int i) {
		thisStereo = stereo[i];
  	}

  	@Override
  	public String getParity() {
  		return uc(getString(thisStereo, "parity", ""));
  	}

  	@Override
  	public String getStereoType() {
  		return uc(getString(thisStereo, "type", "NONE"));
  	}

  	@Override
  	public int getCenterAtom() {
  		return getInt(thisStereo, "centralAtom", -1);
  	}

	@Override
	public int[] getNeighbors() {
		return (/** @j2sNative this.thisStereo.neighbors || */
		null);
	}

	private int getInt(Object map, String name, int defaultValue) {
		return (/** @j2sNative map[name] ? map[name] : */
		defaultValue);
	}

	private double getDouble(Object map, String name, double defaultValue) {
		return (/** @j2sNative map[name] ? map[name] : */
		defaultValue);
	}

	private String getString(Object map, String name, String defaultValue) {
		return (/** @j2sNative map[name] ? map[name] : */
		defaultValue);
	}

	@Override
	String getInchIBondStereo() {
		return uc(getString(thisBond, "stereo", "NONE"));
	}

	@Override
	public String getMessage() {
		return getString(output, "message", "");
	}

	@Override
	public String getLog() {
		return getString(output, "log", "");
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

	@Override
	public long[][] getWarningFlags() {
		// not implemented
		return new long[2][2];
	}

	@Override
	@Deprecated
	public INCHI_RET getReturnStatus() {
		// Not implmemented in JavaScript
		return null;
	}



}
