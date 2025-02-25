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

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedCisTrans;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import io.github.dan2097.jnainchi.InchiOptions;
import io.github.dan2097.jnainchi.InchiStatus;
import net.sf.jniinchi.INCHI_RET;

/**
 * Modified by Bob Hanson 2025.02.23 to allow both Java and JavaScript platform calls.
 * Also adds the ability to return 2D or 3D coordinates
 * <p>
 * This class generates a CDK IAtomContainer from an InChI string.
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
public abstract class InChIToStructure {

	protected String inchi;

	/**
	 * Constructor. Generates CDK AtomContainer from InChI.
	 * 
	 * Note that this comes via a platform subclass
	 * 
	 * @param inchi
	 * @param setDimension 
	 * @throws CDKException
	 */
	protected InChIToStructure getStructure(String inchi, IChemObjectBuilder builder, InchiOptions options) throws CDKException {
		this.inchi = inchi;
		if (inchi == null)
			throw new IllegalArgumentException("Null InChI string provided");
		if (options == null)
			throw new IllegalArgumentException("Null options provided");
		generateAtomContainerFromInchi(builder);
		return this;
	}

	/**
	 * Generate 2D or 3D coordinates for the returned molecule.
	 * 
	 * @param coordType  "2D" or "3D"
	 * @return
	 * @throws CDKException
	 */
	public InChIToStructure withCoordinates(String coordType) throws CDKException {
		if (coordType != null) {
			switch (coordType.toUpperCase()) {
			case "2D":
				StructureDiagramGenerator sdg = new StructureDiagramGenerator();
				sdg.generateCoordinates(molecule);
				break;
			case "3D":
				// TODO ??
				break;
			}
		}
		return this;
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
	abstract public INCHI_RET getReturnStatus();

	/**
	 * Access the status of the InChI output.
	 * 
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
	abstract public long[][] getWarningFlags();

	// magic number - indicates isotope mass is relative
	private static final int ISOTOPIC_SHIFT_FLAG = 10000;
	/**
	 * JNI-Inchi uses the magic number {#link ISOTOPIC_SHIFT_FLAG} plus the
	 * (possibly negative) relative mass. So any isotope value coming back from
	 * jni-inchi greater than this threshold value should be treated as a relative
	 * mass.
	 */
	private static final int ISOTOPIC_SHIFT_THRESHOLD = ISOTOPIC_SHIFT_FLAG - 100;

	private ILoggingTool logger = LoggingToolFactory.createLoggingTool(InChIToStructure.class);

	/** InChI mass values by atomic numbers - INCHI_BASE/src/util.c */
	private static final int[] defaultElemMass = new int[] { 0, 1, 4, 7, 9, 11, 12, 14, // H He Li Be B C N
			16, 19, 20, 23, 24, 27, 28, 31, // O F Ne Na Mg Al Si P
			32, 35, 40, 39, 40, 45, 48, 51, // S Cl Ar K Ca Sc Ti V
			52, 55, 56, 59, 59, 64, 65, 70, // Cr Mn Fe Co Ni Cu Zn Ga
			73, 75, 79, 80, 84, 85, 88, 89, // Ge As Se Br Kr Rb Sr Y
			91, 93, 96, 98, 101, 103, 106, 108, // Zr Nb Mo Tc Ru Rh Pd Ag
			112, 115, 119, 122, 128, 127, 131, 133, // Cd In Sn Sb Te I Xe Cs
			137, 139, 140, 141, 144, 145, 150, 152, // Ba La Ce Pr Nd Pm Sm Eu
			157, 159, 163, 165, 167, 169, 173, 175, // Gd Tb Dy Ho Er Tm Yb Lu
			178, 181, 184, 186, 190, 192, 195, 197, // Hf Ta W Re Os Ir Pt Au
			201, 204, 207, 209, 209, 210, 222, 223, // Hg Tl Pb Bi Po At Rn Fr
			226, 227, 232, 231, 238, 237, 244, 243, // Ra Ac Th Pa U Np Pu Am
			247, 247, 251, 252, 257, 258, 259, 260, // Cm Bk Cf Es Fm Md No Lr
			261, 270, 269, 270, 270, 278, 281, 281, // Rf Db Sg Bh Hs Mt Ds Rg
			285, 278, 289, 289, 293, 297, 294 };

	protected IAtomContainer molecule;
	private List<IAtom> inchi2cdkAtom;

	/**
	 * Flip the storage order of atoms in a bond.
	 * 
	 * @param bond the bond
	 */
	private void flip(IBond bond) {
		bond.setAtoms(new IAtom[] { bond.getEnd(), bond.getBegin() });
	}

	/**
	 * Gets structure from InChI, and converts InChI library data structure into an
	 * IAtomContainer.
	 *
	 * @throws CDKException
	 */
	private void generateAtomContainerFromInchi(IChemObjectBuilder builder) throws CDKException {

		// molecule = new AtomContainer();
		molecule = builder.newInstance(IAtomContainer.class);

		initializeInchiModel(inchi);
		int natoms = getNumAtoms();
		inchi2cdkAtom = new ArrayList<>();
		for (int i = 0; i < natoms; i++) {
			setAtom(i);
			IAtom cAt = builder.newInstance(IAtom.class);
			inchi2cdkAtom.add(cAt);
			cAt.setID("a" + i);
			int elem = Elements.ofString(getElementType()).number();
			cAt.setAtomicNumber(elem);

			// Ignore coordinates - all zero - unless aux info was given... but
			// the CDK doesn't have an API to provide that

			// InChI does not have unset properties so we set charge,
			// hydrogen count (implicit) and isotopic mass
			cAt.setFormalCharge(getCharge());
			cAt.setImplicitHydrogenCount(getImplicitH());
			int isotopicMass = getIsotopicMass();
			if (isotopicMass != 0) {
				if (isotopicMass > ISOTOPIC_SHIFT_THRESHOLD) {
					int delta = isotopicMass - ISOTOPIC_SHIFT_FLAG;
					if (elem > 0 && elem < defaultElemMass.length)
						cAt.setMassNumber(defaultElemMass[elem] + delta);
					else
						logger.error("Cannot set mass delta for element {}, no base mass?", elem);
				} else {
					cAt.setMassNumber(isotopicMass);
				}
			}

			molecule.addAtom(cAt);
			cAt = molecule.getAtom(molecule.getAtomCount() - 1);
			addHydrogenIsotopes(builder, cAt, 2, getImplicitDeuterium());
			addHydrogenIsotopes(builder, cAt, 3, getImplicitTritium());

			String radical = getRadical();
			switch (radical) {
			case "DOUBLET":
				molecule.addSingleElectron(molecule.indexOf(cAt));
				break;
			case "SINGLET":
			case "TRIPLET":
				// Information loss - we should make MDL SPIN_MULTIPLICITY avaliable to this API
				molecule.addSingleElectron(molecule.indexOf(cAt));
				molecule.addSingleElectron(molecule.indexOf(cAt));
				break;
			}
		}
		int nBonds = getNumBonds();
		for (int i = 0; i < nBonds; i++) {
			setBond(i);
			IBond cBo = builder.newInstance(IBond.class);
			IAtom atO = inchi2cdkAtom.get(getIndexOriginAtom());
			IAtom atT = inchi2cdkAtom.get(getIndexTargetAtom());
			cBo.setAtoms(new IAtom[] { atO, atT });

			String type = getInchiBondType();
			switch (type) {
			case "SINGLE":
				cBo.setOrder(IBond.Order.SINGLE);
				break;
			case "DOUBLE":
				cBo.setOrder(IBond.Order.DOUBLE);
				break;
			case "TRIPLE":
				cBo.setOrder(IBond.Order.TRIPLE);
				break;
			case "ALTERN":
				cBo.setIsInRing(true);
				break;
			default:
				throw new CDKException("Unknown bond type: " + type);
			}

			switch (getInchIBondStereo()) {
			case "NONE":
				cBo.setStereo(IBond.Stereo.NONE);
				break;
			case "SINGLE_1DOWN":
				cBo.setStereo(IBond.Stereo.DOWN);
				break;
			case "SINGLE_1UP":
				cBo.setStereo(IBond.Stereo.UP);
				break;
			case "SINGLE_2DOWN":
				cBo.setStereo(IBond.Stereo.DOWN_INVERTED);
				break;
			case "SINGLE_2UP":
				cBo.setStereo(IBond.Stereo.UP_INVERTED);
				break;
			case "SINGLE_1EITHER":
				cBo.setStereo(IBond.Stereo.UP_OR_DOWN);
				break;
			case "SINGLE_2EITHER":
				cBo.setStereo(IBond.Stereo.UP_OR_DOWN_INVERTED);
				break;
			}

			molecule.addBond(cBo);
		}

		int nStereo = getNumStereo0D();
		for (int i = 0; i < nStereo; i++) {
			setStereo0D(i);
			int[] neighbours = getNeighbors();
			String type = getStereoType();
			switch (type) {
			case "TETRAHEDRAL":
			case "ALLENE":
				int central = getCenterAtom();
				IAtom focus = inchi2cdkAtom.get(central);
				IAtom[] neighbors = new IAtom[] { inchi2cdkAtom.get(neighbours[0]), inchi2cdkAtom.get(neighbours[1]),
						inchi2cdkAtom.get(neighbours[2]), inchi2cdkAtom.get(neighbours[3]) };
				ITetrahedralChirality.Stereo stereo;

				// as per JNI InChI doc even is clockwise and odd is
				// anti-clockwise
				switch (getParity()) {
				case "ODD":
					stereo = ITetrahedralChirality.Stereo.ANTI_CLOCKWISE;
					break;
				case "EVEN":
					stereo = ITetrahedralChirality.Stereo.CLOCKWISE;
					break;
				default:
					// CDK Only supports parities of + or -
					continue;
				}

				IStereoElement<?, ?> stereoElement = null;
				switch (type) {
				case "TETRAHEDRAL":
					stereoElement = builder.newInstance(ITetrahedralChirality.class, focus, neighbors, stereo);
					break;
				case "ALLENE":
					// The periphals (p<i>) and terminals (t<i>) are refering to
					// the following atoms. The focus (f) is also shown.
					//
					// p0 p2
					// \ /
					// t0 = f = t1
					// / \
					// p1 p3
					IAtom[] peripherals = neighbors;
					IAtom[] terminals = ExtendedTetrahedral.findTerminalAtoms(molecule, focus);

					// InChI always provides the terminal atoms t0 and t1 as
					// periphals, here we find where they are and then add in
					// the other explicit atom. As the InChI create hydrogens
					// for stereo elements, there will always we an explicit
					// atom that can be found - it may be optionally suppressed
					// later.

					// not much documentation on this (at all) but they appear
					// to always be the middle two atoms (index 1, 2) we therefore
					// test these first - but handle the other indices just in
					// case
					for (IAtom terminal : terminals) {
						if (peripherals[1].equals(terminal)) {
							peripherals[1] = findOtherSinglyBonded(molecule, terminal, peripherals[0]);
						} else if (peripherals[2].equals(terminal)) {
							peripherals[2] = findOtherSinglyBonded(molecule, terminal, peripherals[3]);
						} else if (peripherals[0].equals(terminal)) {
							peripherals[0] = findOtherSinglyBonded(molecule, terminal, peripherals[1]);
						} else if (peripherals[3].equals(terminal)) {
							peripherals[3] = findOtherSinglyBonded(molecule, terminal, peripherals[2]);
						}
					}

					stereoElement = new ExtendedTetrahedral(focus, peripherals, stereo);
					break;
				}
				assert stereoElement != null;
				molecule.addStereoElement(stereoElement);
				break;
			case "DOUBLEBOND":
				boolean extended = false;

				// from JNI InChI doc
				// neighbor[4] : {#X,#A,#B,#Y} in this order
				// X central_atom : NO_ATOM
				// \ X Y type : INCHI_StereoType_DoubleBond
				// A == B \ /
				// \ A == B
				// Y
				IAtom x = inchi2cdkAtom.get(neighbours[0]);
				IAtom a = inchi2cdkAtom.get(neighbours[1]);
				IAtom b = inchi2cdkAtom.get(neighbours[2]);
				IAtom y = inchi2cdkAtom.get(neighbours[3]);

				IBond stereoBond = molecule.getBond(a, b);
				if (stereoBond == null) {
					extended = true;
					// A = C = C = B
					stereoBond = ExtendedCisTrans.findCentralBond(molecule, a);
					if (stereoBond == null)
						continue; // warn on invalid input?
					IAtom[] ends = ExtendedCisTrans.findTerminalAtoms(molecule, stereoBond);
					assert ends != null;
					if (ends[0] != a)
						flip(stereoBond);
				} else {
					if (!stereoBond.getBegin().equals(a))
						flip(stereoBond);
				}

				int config = getParity().equals("EVEN") ? IStereoElement.OPPOSITE : IStereoElement.TOGETHER;

				if (extended) {
					molecule.addStereoElement(new ExtendedCisTrans(stereoBond,
							new IBond[] { molecule.getBond(x, a), molecule.getBond(b, y) }, config));
				} else {
					molecule.addStereoElement(new DoubleBondStereochemistry(stereoBond,
							new IBond[] { molecule.getBond(x, a), molecule.getBond(b, y) }, config));
				}
			}
		}
	}

	private void addHydrogenIsotopes(IChemObjectBuilder builder, IAtom cAt, int mass, int count) {
		for (int j = 0; j < count; j++) {
			IAtom deut = builder.newInstance(IAtom.class);
			deut.setAtomicNumber(1);
			deut.setSymbol("H");
			deut.setMassNumber(mass);
			deut.setImplicitHydrogenCount(0);
			molecule.addAtom(deut);
			deut = molecule.getAtom(molecule.getAtomCount() - 1);
			IBond bond = builder.newInstance(IBond.class, cAt, deut, IBond.Order.SINGLE);
			molecule.addBond(bond);
		}
	}

	/**
	 * Finds a neighbor attached to 'atom' that is singley bonded and isn't
	 * 'exclude'. If no such atom exists, the 'atom' is returned.
	 *
	 * @param container a molecule container
	 * @param atom      the atom to find the neighbor or
	 * @param exclude   don't find this atom
	 * @return the other atom (or 'atom')
	 */
	private static IAtom findOtherSinglyBonded(IAtomContainer container, IAtom atom, IAtom exclude) {
		for (final IBond bond : container.getConnectedBondsList(atom)) {
			if (!IBond.Order.SINGLE.equals(bond.getOrder()) || bond.contains(exclude))
				continue;
			return bond.getOther(atom);
		}
		return atom;
	}

	/**
	 * Returns generated molecule.
	 * 
	 * @return An AtomContainer object
	 */
	public IAtomContainer getAtomContainer() {
		return (molecule);
	}

	abstract void initializeInchiModel(String inchi);

	// for-loop setters
	abstract void setAtom(int i);

	abstract void setBond(int i);

	abstract void setStereo0D(int i);

	// general counts
	abstract int getNumAtoms();

	abstract int getNumBonds();

	abstract int getNumStereo0D();

	// Atom Methods
	abstract String getElementType();

	abstract double getX();

	abstract double getY();

	abstract double getZ();

	abstract int getCharge();

	abstract int getImplicitH();

	abstract int getIsotopicMass();

	abstract int getImplicitDeuterium();

	abstract int getImplicitTritium();

	abstract String getRadical();

	// Bond Methods
	abstract int getIndexOriginAtom();

	abstract int getIndexTargetAtom();

	abstract String getInchiBondType();

	abstract String getInchIBondStereo();

	// Stereo Methods
	abstract String getParity();

	abstract String getStereoType();

	abstract int getCenterAtom();

	abstract int[] getNeighbors();

	protected static String uc(Object o) {
		return o.toString().toUpperCase();
	}

}
