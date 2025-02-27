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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiInput;
import io.github.dan2097.jnainchi.InchiInputFromInchiOutput;
import io.github.dan2097.jnainchi.InchiStatus;
import io.github.dan2097.jnainchi.InchiStereo;
import io.github.dan2097.jnainchi.JnaInchi;
import net.sf.jniinchi.INCHI_RET;

/**
 * <p>
 * This Java-specific class generates a CDK IAtomContainer from an InChI string.
 * 
 * It places calls to a JNA wrapper for the InChI C++ library to fulfill
 * requests for model data made by its superclass InChIToStructure.
 * 
 * @author Bob Hanson
 * @cdk.module inchi
 * @cdk.githash
 */
public class InChIToStructureJNA implements IInChIToStructure {

	private InchiInputFromInchiOutput output;
	private Map<InchiAtom, Integer> map;
	private List<InchiAtom> atoms;
	private InchiAtom thisAtom;
	private List<InchiBond> bonds;
	private InchiBond thisBond;
	private List<InchiStereo> stereos;
	private InchiStereo thisStereo;

	InChIToStructureJNA() {
		// not public
	}

	@Override
	public void initializeInchiModel(String inchi) {
		output = JnaInchi.getInchiInputFromInchi(inchi);
		InchiInput input = output.getInchiInput();
		atoms = input.getAtoms();
		bonds = input.getBonds();
		stereos = input.getStereos();
		map = new Hashtable<InchiAtom, Integer>();
		for (int i = getNumAtoms(); --i >= 0;)
			map.put(input.getAtom(i), Integer.valueOf(i));
	}

	@Override
	public void setAtom(int i) {
		thisAtom = atoms.get(i);
	}

	@Override
	public void setBond(int i) {
		thisBond = bonds.get(i);
	}

	@Override
	public void setStereo0D(int i) {
		thisStereo = stereos.get(i);
	}

	@Override
	public int getNumAtoms() {
		return atoms.size();
	}

	@Override
	public int getNumBonds() {
		return bonds.size();
	}

	@Override
	public int getNumStereo0D() {
		return stereos.size();
	}

	@Override
	public String getElementType() {
		return thisAtom.getElName();
	}

	@Override
	public double getX() {
		return thisAtom.getX();
	}

	@Override
	public double getY() {
		return thisAtom.getY();
	}

	@Override
	public double getZ() {
		return thisAtom.getZ();
	}

	@Override
	public int getCharge() {
		return thisAtom.getCharge();
	}

	@Override
	public int getImplicitH() {
		return thisAtom.getImplicitHydrogen();
	}

	@Override
	public int getIsotopicMass() {
		return thisAtom.getIsotopicMass();
	}

	@Override
	public int getImplicitDeuterium() {
		return thisAtom.getImplicitDeuterium();
	}

	@Override
	public int getImplicitTritium() {
		return thisAtom.getImplicitTritium();
	}

	@Override
	public String getRadical() {
		return uc(thisAtom.getRadical().name());
	}

	@Override
	public int getIndexOriginAtom() {
		return map.get(thisBond.getStart()).intValue();
	}

	@Override
	public int getIndexTargetAtom() {
		return map.get(thisBond.getEnd()).intValue();
	}

	@Override
	public String getInchiBondType() {
		return uc(thisBond.getType().name());
	}

	@Override
	public String getInchIBondStereo() {
		return uc(thisBond.getStereo().name());
	}

	@Override
	public int[] getNeighbors() {
		InchiAtom[] an = thisStereo.getAtoms();

		int n = an.length;
		int[] a = new int[n];

		// add for loop
		for (int i = 0; i < n; i++) {
			a[i] = map.get(an[i]).intValue();
		}
		return a;
	}

	@Override
	public int getCenterAtom() {
		InchiAtom ca = thisStereo.getCentralAtom();
		return (ca == null ? -1 : map.get(ca).intValue());
	}

	@Override
	public String getStereoType() {
		return uc(thisStereo.getType());
	}

	@Override
	public String getParity() {
		return uc(thisStereo.getParity());
	}

	/**
	 * Gets return status from InChI process. OKAY and WARNING indicate InChI has
	 * been generated, in all other cases InChI generation has failed. This returns
	 * the JNI INCHI enum and requires the optional "cdk-jniinchi-support" module to
	 * be loaded (or the full JNI InChI lib to be on the class path).
	 * 
	 * @deprecated use getStatus
	 */
	@Deprecated
	@Override
	public INCHI_RET getReturnStatus() {
		return JniInchiSupport.toJniStatus(output.getStatus());
	}

	/**
	 * Access the status of the InChI output.
	 * 
	 * @return the status
	 */
	@Override
	public InchiStatus getStatus() {
		return output.getStatus();
	}

	/**
	 * Gets generated (error/warning) messages.
	 */
	@Override
	public String getMessage() {
		return output.getMessage();
	}

	/**
	 * Gets generated log.
	 */
	@Override
	public String getLog() {
		return output.getLog();
	}

	/**
	 * <p>
	 * Returns warning flags, see INCHIDIFF in inchicmp.h.
	 *
	 * <p>
	 * [x][y]: <br>
	 * x=0 =&gt; Reconnected if present in InChI otherwise Disconnected/Normal <br>
	 * x=1 =&gt; Disconnected layer if Reconnected layer is present <br>
	 * y=1 =&gt; Main layer or Mobile-H <br>
	 * y=0 =&gt; Fixed-H layer
	 */
	@Override
	public long[][] getWarningFlags() {
		return output.getWarningFlags();
	}

}
