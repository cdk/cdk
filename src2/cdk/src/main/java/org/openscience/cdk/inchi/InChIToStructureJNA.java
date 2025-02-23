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
 * This class generates a CDK IAtomContainer from an InChI string. It places
 * calls to a JNI wrapper for the InChI C++ library.
 *
 * <p>
 * The generated IAtomContainer will have all 2D and 3D coordinates set to 0.0,
 * but may have atom parities set. Double bond and allene stereochemistry are
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
 * <p>
 * <br>
 *
 * @author Sam Adams
 *
 * @cdk.module inchi
 * @cdk.githash
 */
public class InChIToStructureJNA extends InChIToStructure {

	protected InchiInputFromInchiOutput output;

	/**
	 * Gets return status from InChI process. OKAY and WARNING indicate InChI has
	 * been generated, in all other cases InChI generation has failed. This returns
	 * the JNI INCHI enum and requires the optional "cdk-jniinchi-support" module to
	 * be loaded (or the full JNI InChI lib to be on the class path).
	 * 
	 * @deprecated use getStatus
	 */
	@Deprecated
	public INCHI_RET getReturnStatus() {
		return JniInchiSupport.toJniStatus(output.getStatus());
	}

	/**
	 * Access the status of the InChI output.
	 * 
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
	public long[][] getWarningFlags() {
		return output.getWarningFlags();
	}

	private List<InchiAtom> atoms;
	private InchiAtom thisAtom;
	private List<InchiBond> bonds;
	private InchiBond thisBond;
	private List<InchiStereo> stereos;
	private InchiStereo thisStereo;

	private Map<InchiAtom, Integer> map = new Hashtable<InchiAtom, Integer>();

	@Override
	void initializeInchiModel(String inchi) {
		output = JnaInchi.getInchiInputFromInchi(inchi);
		InchiInput input = output.getInchiInput();
		atoms = input.getAtoms();
		bonds = input.getBonds();
		stereos = input.getStereos();
		for (int i = getNumAtoms(); --i >= 0;)
			map.put(input.getAtom(i), Integer.valueOf(i));
	}

	@Override
	void setAtom(int i) {
		thisAtom = atoms.get(i);
	}

	@Override
	void setBond(int i) {
		thisBond = bonds.get(i);
	}

	@Override
	void setStereo0D(int i) {
		thisStereo = stereos.get(i);
	}

	@Override
	int getNumAtoms() {
		return atoms.size();
	}

	@Override
	int getNumBonds() {
		return bonds.size();
	}

	@Override
	int getNumStereo0D() {
		return stereos.size();
	}

	@Override
	String getElementType() {
		return thisAtom.getElName();
	}

	@Override
	double getX() {
		return thisAtom.getX();
	}

	@Override
	double getY() {
		return thisAtom.getY();
	}

	@Override
	double getZ() {
		return thisAtom.getZ();
	}

	@Override
	int getCharge() {
		return thisAtom.getCharge();
	}

	@Override
	int getImplicitH() {
		return thisAtom.getImplicitHydrogen();
	}

	@Override
	int getIsotopicMass() {
		return thisAtom.getIsotopicMass();
	}

	@Override
	int getImplicitDeuterium() {
		return thisAtom.getImplicitDeuterium();
	}

	@Override
	int getImplicitTritium() {
		return thisAtom.getImplicitTritium();
	}

	@Override
	String getRadical() {
		return thisAtom.getRadical().name();
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
		return thisBond.getType().name();
	}

	@Override
	String getInchIBondStereo() {
		return thisBond.getStereo().name();
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

	private static String uc(Object o) {
		return o.toString().toUpperCase();
	}

}
