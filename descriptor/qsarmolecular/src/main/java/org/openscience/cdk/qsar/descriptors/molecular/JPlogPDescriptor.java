/* Copyright (C) 2018  Jeffrey Plante (Lhasa Limited)  <Jeffrey.Plante@lhasalimited.org>
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
package org.openscience.cdk.qsar.descriptors.molecular;

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * A logP model donated by Lhasa Limited. It is based on an atom contribution
 * model. See {@cdk.cite Plante2018}.
 *  
 * @author Jeffrey Plante
 * @cdk.created 2018-12-15
 * @cdk.keyword JPLogP
 * @cdk.keyword descriptor
 * @cdk.keyword lipophilicity
 */
public class JPlogPDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

	private static final String[] NAMES = { "JPLogP" };
	private boolean addImplicitH = true;
	JPlogPCalculator jplogp = null;

	/**
	 * Default constructor which will setup the required coefficients to enable
	 * a prediction
	 */
	public JPlogPDescriptor() 
	{
		jplogp = new JPlogPCalculator();
	}

	@Override
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification("JPlogP developed at Lhasa Limited www.lhasalimited.org",
				this.getClass().getName(), "Jeffrey Plante - Lhasa Limited");
	}

	@Override
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "addImplicitH";
		return params;
	}

	@Override
	public Object getParameterType(String name) {
		if ("addImplicitH".equals(name))
			return true;
		return null;
	}

	@Override
	public void setParameters(Object[] params) throws CDKException {
		if (params.length != 1) {
			throw new CDKException("JPLogPDescriptor expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter must be of type Boolean");
		}
		addImplicitH = (Boolean) params[0];
	}

	@Override
	public Object[] getParameters() {
		Object[] params = new Object[1];
		params[0] = addImplicitH;
		return params;
	}

	@Override
	public String[] getDescriptorNames() {
		return NAMES;
	}

	@Override
	public IDescriptorResult getDescriptorResultType() {
		return new DoubleResult(0.0);
	}

	private DescriptorValue getDummyDescriptorValue(Exception e) {
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
				new DoubleResult(Double.NaN), getDescriptorNames(), e);
	}

	@Override
	public DescriptorValue calculate(IAtomContainer container) {
		IAtomContainer struct;
		try {
			struct = (IAtomContainer) container.clone();
			AtomContainerManipulator.percieveAtomTypesAndConfigureUnsetProperties(struct);
			CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(struct.getBuilder());
			hAdder.addImplicitHydrogens(struct);
			AtomContainerManipulator.convertImplicitToExplicitHydrogens(struct);
			Aromaticity.cdkLegacy().apply(struct);
		} catch (CloneNotSupportedException e) {
			return getDummyDescriptorValue(e);
		} catch (CDKException e) {
			return getDummyDescriptorValue(e);
		}


		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(jplogp.calcLogP(struct)),
				getDescriptorNames());
	}



	/**
	 * The class that calculated the logP according to the JPlogP method described in:
	 * Journal of Cheminformatics 2018 10:61 https://doi.org/10.1186/s13321-018-0316-5
	 * 
	 * This is lower level access and should normally be obtained through the descriptor above.
	 * 
	 * @author Jeffrey
	 *
	 */
	protected class JPlogPCalculator
	{
		Map<Integer, Double> coeffs  = null;

		/**
		 * Initialises the required coefficients for the trained model from the paper.
		 */
		public JPlogPCalculator(Object[] params)
		{
				coeffs = new HashMap<>();
				initcoeffs(params);
		}

		public JPlogPCalculator() {
			this(WEIGHTS_CURR);
		}
		
		/**
		 * Given a structure in the correct configuration (explicit H and aromatised) it will return the logP as a Double
		 * or if it is out of domain (encounters an unknown atomtype) it will return Double.NaN
		 * @param struct the structure to calculate it must have explicit H and be aromatised.
		 * @return The calculated logP as a Double
		 */
		protected Double calcLogP(IAtomContainer struct)
		{
			boolean inDomain = true;
			Double logP = 0.0;
			
			for (IAtom atom : struct.atoms()) 
			{
				Integer atomtype = getAtomTypeCode(atom);
				Double increment = 0.0;
				try {
					increment = coeffs.get(atomtype);
				} catch (NullPointerException e) {
					inDomain = false;
				}
				if (inDomain && (increment != null)) {
					logP += increment;
				} else {
					System.out.println(atomtype + " not found");
					return Double.NaN;
				}
			}
			return logP;
		}
		
		/**
		 * Used in Training the model
		 * 
		 * @param struct
		 * @return Map representing the Hologram of the given structure
		 */
		public Map<Integer, Integer> getMappedHologram(IAtomContainer struct) {
			Map<Integer, Integer> holo = new HashMap<>();
			for (int i = 0; i < struct.getAtomCount(); i++) {
				IAtom atom = struct.getAtom(i);
				Integer type = getAtomTypeCode(atom);
				if (holo.containsKey(type)) {
					int count = holo.get(type);
					count++;
					holo.put(type, count);
				} else {
					holo.put(type, 1);
				}
			}
			return holo;
		}
		
		/**
		 * Returns the AtomCode for the logP atomtype as previously developed at
		 * Lhasa see citation at top of class for more information
		 * 
		 * @param atom
		 *            the atom to type
		 * @return Integer of the type CAANSS c = charge+1 AA = Atomic Number N =
		 *         NonH Neighbour count SS = elemental subselection via bonding and
		 *         neighbour identification
		 */
		private Integer getAtomTypeCode(IAtom atom) {
			Integer returnMe = 0;
			String element = atom.getSymbol();

			// Setup initial parameters for assigning atomtype
			int nonHNeighbours = nonHNeighbours(atom);
			int charge = atom.getFormalCharge();
			int aNum = atom.getAtomicNumber();
			int toadd = 0;

			// Initialise the type integer with what we know so far
			returnMe += 100000 * (charge + 1);
			returnMe += aNum * 1000;
			returnMe += nonHNeighbours * 100;

			switch (element) {
			case "C":
				toadd = getCarbonSpecial(atom);
				break;

			case "N":
				toadd = getNitrogenSpecial(atom);
				break;

			case "O":
				toadd = getOxygenSpecial(atom);
				break;

			case "H":
				toadd = getHydrogenSpecial(atom);
				break;

			case "F":
				toadd = getFluorineSpecial(atom);
				break;

			default:
				toadd = getDefaultSpecial(atom);
				break;
			}
			returnMe += toadd;
			// check for any errors and if so return a null value
			if (toadd != 99) {
				return returnMe;
			} else
				return null;
		}

		/**
		 * Determines and returns the SS (subsection) portion of the atomtype integer for a Hydrogen Atom
		 * @param atom
		 * @return the final 2 digits for the given atom
		 */
		protected int getHydrogenSpecial(IAtom atom) {
			int toadd = 0;
			int bondCount = atom.getBondCount();
			if (bondCount > 0) {
				IAtom neighbour = atom.bonds().iterator().next().getOther(atom);
				int numNeighbours = neighbour.getBondCount();
				if (neighbour.getSymbol().equals("C")) {
					if (carbonylConjugated(neighbour))
						toadd = 51;
					else {
						double formalOxState = getNumMoreElectronegativethanCarbon(neighbour);
						switch (numNeighbours) {
						case 4:
							if (formalOxState == 0.0)
								toadd = 46;
							else if (formalOxState == 1.0)
								toadd = 47;
							else if (formalOxState == 2.0)
								toadd = 48;
							else if (formalOxState == 3.0)
								toadd = 49;
							break;
						case 3:
							if (formalOxState == 0.0)
								toadd = 47;
							else if (formalOxState == 1.0)
								toadd = 48;
							else if (formalOxState >= 2.0)
								toadd = 49;
							break;
						case 2:
							if (formalOxState == 0.0)
								toadd = 48;
							else if (formalOxState >= 1.0)
								toadd = 49;
							break;
						case 1:
							toadd = 121;
							break;
						}
					}
				} else
					toadd = 50;
			}
			return toadd;
		}

		/**
		 * Determines and returns the SS (subsection) portion of the atomtype integer for a "Default" ie not C,N,O,H,F Atom
		 * @param atom
		 * @return the final 2 digits for the given atom
		 */
		protected int getDefaultSpecial(IAtom atom) {
			int toadd;
			int[] polarbondCounts = getPolarBondArray(atom);
			int singleBondPolar = polarbondCounts[0];
			int doubleBondPolar = polarbondCounts[2];
			int tripleBondPolar = polarbondCounts[3];
			int aromaticBondPolar = polarbondCounts[1];

			if (atom.isAromatic())
				toadd = 10;
			else
				toadd = singleBondPolar + doubleBondPolar + tripleBondPolar + aromaticBondPolar;
			return toadd;
		}

		/**
		 * Determines and returns the SS (subsection) portion of the atomtype integer for a Fluorine Atom
		 * @param atom
		 * @return the final 2 digits for the given atom
		 */
		protected int getFluorineSpecial(IAtom atom) {
			int toadd;
			int numconn = atom.getBondCount();
			int neighbourconn = 0;
			if (numconn == 1) {
				IBond bond = atom.bonds().iterator().next();
				IAtom next = bond.getOther(atom);
				neighbourconn = next.getBondCount();
				double ox = getNumMoreElectronegativethanCarbon(next);
				if (next.getSymbol().matches("S"))
					toadd = 8; // F-S
				else if (next.getSymbol().matches("B"))
					toadd = 9; // F-B
				else if (!next.getSymbol().matches("C"))
					toadd = 1; // F-hetero
				else if (neighbourconn == 2)
					toadd = 2;
				else if (neighbourconn == 3)
					toadd = 3;
				else if (neighbourconn == 4 && ox <= 2)
					toadd = 5;
				else if (neighbourconn == 4 && ox > 2)
					toadd = 7;
				else
					toadd = 99;
			} else
				toadd = 99;
			return toadd;
		}

		/**
		 * Determines and returns the SS (subsection) portion of the atomtype integer for an Oxygen Atom
		 * @param atom
		 * @return the final 2 digits for the given atom
		 */
		protected int getOxygenSpecial(IAtom atom) {
			int toadd;
			int numConnections = atom.getBondCount();
			switch (numConnections) {
			case 2:
				if (boundTo(atom, "N"))
					toadd = 1; // A-O-N
				else if (boundTo(atom, "S"))
					toadd = 2; // A-O-S
				else if (atom.isAromatic())
					toadd = 8; // A-=O-=A
				else
					toadd = 3; // A-O-A
				break;
			case 1:
				if (boundTo(atom, "N"))
					toadd = 4; // O=N
				else if (boundTo(atom, "S"))
					toadd = 5; // O=S
				else if (checkAlphaCarbonyl(atom, "O"))
					toadd = 6; // O=A-O
				else if (checkAlphaCarbonyl(atom, "N"))
					toadd = 9; // O=A-N
				else if (checkAlphaCarbonyl(atom, "S"))
					toadd = 10; // O=A-S
				else
					toadd = 7; // O=A
				break;
			default:
				toadd = 0;
			}
			return toadd;
		}

		/**
		 * Determines and returns the SS (subsection) portion of the atomtype integer for a Nitrogen Atom
		 * @param atom
		 * @return the final 2 digits for the given atom
		 */
		protected int getNitrogenSpecial(IAtom atom) {
			int toadd;
			int numConnections = atom.getBondCount();
			int[] polarbondCounts = getPolarBondArray(atom);
			int singleBondPolar = polarbondCounts[0];
			switch (numConnections) {
			case 4:
				toadd = 9; // A-N(-A)(-A)-A probably also positively charged
				break;
			case 3:
				if (nextToAromatic(atom))
					toadd = 1; // A-N(-A)-Aromatic Atom
				else if (carbonylConjugated(atom))
					toadd = 2; // N-A=Polar
				else if (doubleBondHetero(atom))
					toadd = 10; // A-(A-)N={ONS} probably also charged
				else if (singleBondPolar > 0)
					toadd = 3; // A-N(-A)-Polar
				else
					toadd = 4; // A-N(-A)-A
				break;
			case 2:
				if (atom.isAromatic())
					toadd = 5; // A-=N-=A
				else if (doubleBondHetero(atom))
					toadd = 6; // A-N=Polar
				else
					toadd = 7; // A-N=A
				break;
			case 1:
				toadd = 8; // N%A
				break;
			default:
				toadd = 0; // N????
			}
			return toadd;
		}

		/**
		 * Determines and returns the SS (subsection) portion of the atomtype integer for a Carbon Atom
		 * @param atom
		 * @return the final 2 digits for the given atom
		 */
		protected int getCarbonSpecial(IAtom atom) {
			int toadd;
			int numConnections = atom.getBondCount();
			int[] polarbondCounts = getPolarBondArray(atom);
			int singleBondPolar = polarbondCounts[0];
			int doubleBondPolar = polarbondCounts[2];
			int tripleBondPolar = polarbondCounts[3];
			int aromaticBondPolar = polarbondCounts[1];
			switch (numConnections) {
			case 4: // 4 connections
				toadd = 2; // sp3 C
				if (singleBondPolar > 0)
					toadd = 3; // sp3 carbon next to S,N,P,O
				break;
			case 3: // 3 connections
				if (atom.isAromatic()) {
					if (aromaticBondPolar >= 1 && singleBondPolar == 0)
						toadd = 11; // A-=C(-A)-=Polar
					else if (aromaticBondPolar == 0 && singleBondPolar == 1)
						toadd = 5; // A-=C(-Polar)-=A
					else if (aromaticBondPolar >= 1 && singleBondPolar == 1)
						toadd = 13; // A-=C-(Polar)-=Polar or P-=C(-P)-=P
					else
						toadd = 4; // A-=C(-A)-=A
				} else {
					if (doubleBondPolar == 1 && singleBondPolar == 0)
						toadd = 7; // A-C(=Polar)-A
					else if (singleBondPolar >= 1 && doubleBondPolar == 0)
						toadd = 8; // A-C(=A)-Polar
					else if (doubleBondPolar == 1 && singleBondPolar >= 1)
						toadd = 14; // A-C(=P)-P
					else
						toadd = 6; // A-C(=A)-A
				}
				break;
			case 2: // 2 connections
				if (tripleBondPolar == 1 && singleBondPolar == 0)
					toadd = 12; // A-C%Polar
				else if (tripleBondPolar == 0 && singleBondPolar == 1)
					toadd = 10; // Polar-C%A
				else if (tripleBondPolar == 1 && singleBondPolar == 1)
					toadd = 15; // p-C%P
				else
					toadd = 9; // A-C%A
				break;
			default:
				toadd = 0; // C???
				if (singleBondPolar > 0 || doubleBondPolar > 0 || aromaticBondPolar > 0 || tripleBondPolar > 0)
					toadd = 1; // C??Polar
			}
			return toadd;
		}

		/**
		 * Should be called from the carbonyl oxygen
		 * @param atom
		 * @param symbol
		 * @return true if there is an atom of sybmol alpha to the carbonyl
		 */
		protected boolean checkAlphaCarbonyl(IAtom atom, String symbol) {
			for (IBond bond : atom.bonds()) {
				IAtom next = bond.getOther(atom);
				for (IBond bond2 : next.bonds()) {
					IAtom next2 = bond2.getOther(next);
					if (next2.getSymbol().equals(symbol) && bond2.getOrder().numeric() == 1)
						return true;
				}
			}
			return false;
		}

		/**
		 * 
		 * @param atom
		 * @param symbol
		 * @return true if the atom has a bond to an atom of the given symbol
		 */
		protected boolean boundTo(IAtom atom, String symbol) {
			for (IBond bond : atom.bonds()) {
				IAtom next = bond.getOther(atom);
				if (next.getSymbol().equals(symbol))
					return true;
			}
			return false;
		}

		/**
		 * 
		 * @param atom
		 * @return bond order for electron withdrawing atoms from the given atom ie
		 *         =O = 2
		 */
		protected double getNumMoreElectronegativethanCarbon(IAtom atom) {
			double returnme = 0;
			for (IBond bond : atom.bonds()) {
				IAtom compare = bond.getOther(atom);
				double bondOrder = bond.getOrder().numeric();
				if (electronWithdrawing(compare))
					returnme += bondOrder;
			}
			return returnme;
		}

		/**
		 * 
		 * @param atom
		 * @return true if the atom is considered electron withdrawing relative to
		 *         carbon (N,O,S,F,Cl,Br,I)
		 */
		protected boolean electronWithdrawing(IAtom atom) {
			String symbol = atom.getSymbol();
			if (symbol.equals("N") || symbol.equals("O") || symbol.equals("S") || symbol.equals("F") || symbol.equals("Cl")
					|| symbol.equals("Br") || symbol.equals("I"))
				return true;
			else
				return false;
		}

		/**
		 * 
		 * @param atom
		 * @return number of heavy atoms bound to the atom
		 */
		protected int nonHNeighbours(IAtom atom) {
			int returnMe = 0;
			for (IBond bond : atom.bonds()) {
				IAtom neighbor = bond.getOther(atom);
				if (neighbor.getAtomicNumber() != 1)
					returnMe++;
			}
			return returnMe;
		}

		/**
		 * 
		 * @param atom
		 * @return in array of bond information to polar atoms array form is
		 *         [single, aromatic, double, triple]
		 */
		protected int[] getPolarBondArray(IAtom atom) {
			int[] array = new int[4];
			for (IBond bond : atom.bonds()) {
				IAtom neighbor = bond.getOther(atom);
				if (isPolar(neighbor)) {
					if (bond.isAromatic())
						array[1]++;
					else if (bond.getOrder().numeric() == 1)
						array[0]++;
					else if (bond.getOrder().numeric() == 2)
						array[2]++;
					else if (bond.getOrder().numeric() == 3)
						array[3]++;
				}
			}
			return array;
		}

		/**
		 * 
		 * @param atom
		 * @return true if atom is a "polar atom" (O,N,S,P)
		 */
		protected boolean isPolar(IAtom atom) {
			if (atom.getSymbol().matches("O") || atom.getSymbol().matches("S") || atom.getSymbol().matches("N")
					|| atom.getSymbol().matches("P")) {
				return true;
			} else
				return false;
		}

		/**
		 * 
		 * @param atom
		 * @return true if atom is doublebonded to a heteroatom (polar atom)
		 */
		protected boolean doubleBondHetero(IAtom atom) {
			for (IBond bond : atom.bonds()) {
				IAtom neighbour = bond.getOther(atom);
				if (!(bond.isAromatic()) && isPolar(neighbour) && bond.getOrder().numeric() == 2)
					return true;
			}
			return false;
		}

		/**
		 * 
		 * @param atom
		 * @return true if atom is singly bonded to a carbonyl
		 */
		protected boolean carbonylConjugated(IAtom atom) {
			for (IBond bond : atom.bonds()) {
				IAtom next = bond.getOther(atom);
				if (!(bond.isAromatic()) && bond.getOrder().numeric() == 1 && doubleBondHetero(next))
					return true;
			}
			return false;
		}

		/**
		 * 
		 * @param atom
		 * @return true if single bonded to an aromatic atom
		 */
		protected boolean nextToAromatic(IAtom atom) 
		{
			if(!atom.isAromatic())
			{
				for (IBond bond : atom.bonds()) {
					IAtom next = bond.getOther(atom);
					if (next.isAromatic() 
							&& bond.getOrder().numeric() == 1)
						return true;
				}
			}
			return false;
		}

		/**
		 * initialise the model with the required values. Could instead read from a
		 * serialised file, but this is simpler and the number of coefficients isn't
		 * too large. Quite simple to update as well when able to output the model
		 * to the screen with minor text manupilation with regex strings.
		 */
		private void initcoeffs(Object[] objs) {
			for (int i = 0; i < objs.length; i+=2) {
				coeffs.put((Integer)objs[i], (Double)objs[i+1]);
			}
		}

		public Map<Integer, Double> getCoeffs() {
			return coeffs;
		}

		public void setCoeffs(Map<Integer, Double> coeffs) {
			this.coeffs = coeffs;
		}
	}

	public static final Object[] WEIGHTS_CURR = new Object[]{
			115201, 0.09994758075256505d,
			115200, 0.3379378715836258d,
			134400, -0.601185704550091d,
			115202, 0.30788026393512663d,
			207110, -0.26496784659823264d,
			134404, -1.1724223083800398d,
			115210, -0.08346526510422402d,
			153100, 0.9270429695306335d,
			153101, 1.0145354986151272d,
			116503, 0.4425591506257104d,
			133402, -0.2557512835716269d,
			114200, 0.01526633068977459d,
			133403, -0.8169297847709985d,
			5401, 0.10441747048024147d,
			101147, 1.2616122128062803d,
			5402, 0.05162677089603265d,
			101146, 1.3994445700193028d,
			133401, -0.3639701318790265d,
			5403, 0.6788714142147848d,
			101149, 1.3258747052968567d,
			101148, 1.2711079053599976d,
			101151, 1.2556350911435799d,
			133404, -0.6891007636859257d,
			101150, -0.2363827296335956d,
			107301, 0.1787473725640726d,
			107303, -0.016959741231455404d,
			107302, 0.17510323694483412d,
			7207, -0.1902718970453204d,
			107304, 0.1542614658530209d,
			109101, 0.41374364339440817d,
			115501, -0.14068629542864905d,
			115500, 0.17750686328369028d,
			115503, 0.013887172778612027d,
			109103, 0.26651823980406203d,
			115502, -0.11992335751384754d,
			115505, -0.34311166776744884d,
			109105, 0.43019405241170144d,
			115504, -0.1025811855926768d,
			207409, -0.23852255872700964d,
			109107, 0.46487210540519147d,
			109109, 0.4801727828186138d,
			109108, 0.2430918227919903d,
			134202, -0.631693669606887d,
			134200, -0.04910253697266963d,
			134201, 0.011171177597208612d,
			106303, -1.5239332211866237d,
			106302, -1.3023723757750838d,
			106305, 0.11154050989797104d,
			134210, 0.8725452294362313d,
			106304, 0.20930194677993302d,
			106307, -0.2690238196488019d,
			106306, 0.01713355115342431d,
			108101, -0.09994092418236927d,
			106308, 0.026068238656834854d,
			108103, 0.009836423634389751d,
			106311, 0.1427758223243856d,
			108102, 0.09143879448168145d,
			108105, -0.4749180976677123d,
			106313, 0.2897701159093737d,
			108104, 0.015474674863268114d,
			108107, 0.03602987937616161d,
			108106, 0.19048034389371205d,
			106314, -0.39178934954486583d,
			108109, -0.10666832936758427d,
			116301, -0.04914876490157527d,
			116300, 0.7367961788572792d,
			116303, -0.16169601215900375d,
			116302, -0.12643665926860584d,
			108110, 0.08928780909185945d,
			5201, 0.2201279736215536d,
			5202, 0.19045980382858146d,
			133200, -0.3076946592117401d,
			208208, 0.7099234399396344d,
			133201, -0.49680932374826d,
			105301, -0.1621399782797916d,
			105300, -0.174370011345452d,
			105303, -0.1432571497021001d,
			105302, -0.17755682989875274d,
			107101, 0.051702151541644426d,
			107103, -0.2691841786263712d,
			107102, 0.06496457627779738d,
			107104, -0.33802382979998147d,
			107107, 0.394978253857827d,
			107106, 0.3859974866654674d,
			207207, -0.07239795705976523d,
			115301, -0.023836916386805847d,
			107108, 0.11642255395641928d,
			207206, -0.24558051744952064d,
			115300, 0.08797456644925557d,
			115303, -0.23605983536956895d,
			115302, -0.10814292962539623d,
			117101, 0.7369857420763359d,
			117100, 0.7116079866622599d,
			153200, 0.7888787630537003d,
			106103, -3.767115237033892d,
			106102, -3.616478490407742d,
			116600, 1.2424471654297324d,
			106107, -2.416958126564593d,
			106106, -2.0565095206356196d,
			114301, -0.13761744158191205d,
			106109, -0.929959267287108d,
			114300, -0.3058393642193975d,
			114302, -0.3095457315739295d,
			106112, -1.3012751020335893d,
			116101, 1.2551746199963494d,
			116100, 0.7001698255404422d,
			105100, 0.36881886842575007d,
			216210, 0.7113783097640652d,
			134302, -0.37554769340770927d,
			134303, -0.36036185997589393d,
			115100, 0.6096777283013224d,
			134300, -0.4657894122488925d,
			134301, -0.3795150596315356d,
			106403, -0.6035455702256183d,
			106402, -0.19123067665076543d,
			8104, -0.24195926611016633d,
			108201, 0.030702954811754002d,
			8105, -0.4215369017701643d,
			108203, 0.16547595574733062d,
			8106, -0.2964579842443157d,
			108202, 0.12058552604519218d,
			116401, 0.7460081218102875d,
			116400, 0.9078902724309305d,
			108208, 0.06665724849079398d,
			116403, 0.3132146936243478d,
			116402, 0.5536499705080733d,
			133302, -0.7030388263360682d,
			114100, 0.3587262776601395d,
			133303, -0.4317778955330324d,
			116404, 0.0372737885433136d,
			133300, -0.2042709645502799d,
			133301, 0.25473886515880656d,
			135100, 0.8603892414982898d,
			135101, 0.7235643505309818d,
			107201, 0.25454958160787483d,
			107203, 0.20036994532453556d,
			107202, 0.15973823557158393d,
			107205, -0.2543417785988061d,
			7108, 0.2634019114789942d,
			207303, -0.47290811359153156d,
			107204, 0.1275311923081761d,
			207302, 0.24535814336356004d,
			107207, 0.3145047278208787d,
			207301, 0.36070459894156437d,
			107206, 0.3113299058370724d,
			115401, -0.33332080419904164d,
			115400, -0.10861521692106911d,
			115403, -0.7356011823089021d,
			207304, -0.07464529856367844d,
			115402, -0.37912343134016635d,
			115404, -0.8103937160471404d,
			207310, 0.35707776316923207d,
			153302, 0.6015475892374368d,
			134100, -0.1363315394071071d,
			134101, 0.1092440266426836d,
			153301, 0.5748646504612611d,
			106203, -2.6563042881537227d,
			106202, -2.349643420766774d,
			106204, -0.953850490807928d,
			106207, -1.5339493033443787d,
			106206, -1.0109064714897187d,
			106209, 0.19490613802180773d,
			114401, -0.16389842380630032d,
			106208, -1.195325147036d,
			114400, -0.11455385699777243d,
			106211, -1.1770219185049515d,
			114403, -0.1553108949228919d,
			114402, -0.1103226433176957d,
			106210, 0.16163164657523407d,
			106212, -0.2512600932013654d,
			114404, -0.13111265719465162d,
			106215, -0.14610561930731214d,
			106214, -1.6084019086078098d,
			116201, 0.6390448917281739d,
			116200, 0.7928741182178681d,
			116202, 0.5335850658686033d,
			216301, 0.16768140357444056d,
			216300, 0.8104414861979382d,
			105201, 0.06904337130830694d,
			105202, -0.0745914491562598d,
			116210, 0.5791544003852439d,
			216310, 0.16982252294512776d};

	// Original Coeffs from the paper
	public static final Object[] WEIGHTS_PLANTE_2018 = new Object[]{
			115200, 0.3428999504964441d,
			134400, -0.6009339899021935d,
			207110, -0.2537868203838485d,
			134404, -1.175240011610751d,
			153100, 0.9269788584798107d,
			153101, 1.0143514773529836d,
			133402, -0.25834811742258956d,
			114200, 0.02852160272741147d,
			133403, -0.8194572348477968d,
			5401, 0.10394247591686294d,
			101147, 1.1304660454056645d,
			5402, 0.05199289233087018d,
			101146, 1.2682622213479229d,
			133401, -0.3667171872036366d,
			5403, 0.6787470799044643d,
			101149, 1.1949147655182892d,
			101148, 1.140089012601143d,
			101151, 1.124474664082366d,
			133404, -0.6914424692323852d,
			101150, -0.243037761960754d,
			107301, 0.17827258865107412d,
			107303, -0.018054804206490728d,
			107302, 0.174747113462705d,
			7207, -0.1900259417225229d,
			107304, 0.15377727288498777d,
			109101, 0.4139612626967727d,
			115501, -0.14127468400014018d,
			115500, 0.17611532140449737d,
			115503, 0.01466761555846904d,
			109103, 0.26645165703555906d,
			115502, -0.12059099254331078d,
			115505, -0.3426049607280402d,
			109105, 0.43024980310815464d,
			115504, -0.10220447776639764d,
			207409, -0.2390615198632319d,
			109107, 0.46498369443558685d,
			109109, 0.4802991193785544d,
			109108, 0.2432096581199898d,
			134202, -0.6346778955294479d,
			134200, -0.049120673015459124d,
			134201, 0.011636535054497877d,
			106303, -1.3926887806607753d,
			106302, -1.1712347846892444d,
			106305, 0.11166429492968204d,
			134210, 0.8723651043132042d,
			106304, 0.2093224791516782d,
			106307, -0.2713000174540411d,
			106306, 0.01721171808054029d,
			108101, -0.09302944637095042d,
			106308, 0.02622454278720872d,
			108103, 0.016350079235673567d,
			106311, 0.14287513828273285d,
			108102, 0.09827832065783548d,
			108105, -0.47007800462613053d,
			106313, 0.29015591756689035d,
			108104, 0.015437937018121088d,
			108107, 0.03827614806405689d,
			108106, 0.19321034882780294d,
			106314, -0.3943404186929503d,
			108109, -0.10401802166752877d,
			116301, -0.05382098343764403d,
			116300, 0.7387521460456399d,
			116303, -0.16756182500979416d,
			116302, -0.13139795079560512d,
			108110, 0.09217290650820302d,
			5201, 0.23346448860951585d,
			5202, 0.2039139119266068d,
			133200, -0.30769228187947917d,
			208208, 0.7096167841949101d,
			133201, -0.4991951722354514d,
			105301, -0.16207989873206854d,
			105300, -0.17440788934127419d,
			105303, -0.14262227829859978d,
			105302, -0.1772536453086892d,
			107101, 0.06472635260371458d,
			107103, -0.25579034271921075d,
			107102, 0.07815605927333318d,
			107104, -0.3248358665028741d,
			107107, 0.40442424583948916d,
			107106, 0.396893949325775d,
			207207, -0.07223876787467944d,
			115301, -0.02360338462248146d,
			107108, 0.11602222985765208d,
			207206, -0.24327541812800021d,
			115300, 0.08742831050292114d,
			115303, -0.23502791004771306d,
			115302, -0.10635975733575764d,
			117101, 0.7375898512161616d,
			117100, 0.711562233142568d,
			153200, 0.7953592172870871d,
			106103, -3.373528417699127d,
			106102, -3.222960398502975d,
			116600, 1.2414649166226785d,
			106107, -2.1610769299516286d,
			106106, -1.794277022889798d,
			114301, -0.13076494426939203d,
			106109, -0.7983974980016113d,
			114300, -0.2992443066268472d,
			114302, -0.3024419065452111d,
			106112, -1.1706517781448094d,
			116101, 1.25794766342382d,
			116100, 0.7033847677524618d,
			216210, 0.7111335744036255d,
			134302, -0.3781625467763615d,
			134303, -0.363065393849358d,
			134300, -0.46600673735969483d,
			134301, -0.38176583952591553d,
			106403, -0.6031848047124038d,
			106402, -0.19127514916328328d,
			8104, -0.24231144996862355d,
			108201, 0.03078259547644759d,
			8105, -0.42080392882157724d,
			108203, 0.16517108509661693d,
			8106, -0.29674843353741903d,
			108202, 0.12117897946674977d,
			116401, 0.7441487272627919d,
			108208, 0.06651412646122902d,
			116403, 0.3031855131038271d,
			116402, 0.543649606560247d,
			133302, -0.7027480278305203d,
			114100, 0.3786112153751248d,
			133303, -0.431414306011833d,
			116404, 0.02729394485921511d,
			133300, -0.20426419244061747d,
			133301, 0.2550357745554604d,
			135100, 0.8603178696688789d,
			135101, 0.7236559576494113d,
			107201, 0.260897789525647d,
			107203, 0.2070230861251806d,
			107202, 0.16622067949480449d,
			107205, -0.2544031618211347d,
			7108, 0.2615348564811823d,
			207303, -0.4723141310610628d,
			107204, 0.13376742355228766d,
			207302, 0.24697598201746412d,
			107207, 0.316655927726688d,
			207301, 0.36095025588716984d,
			107206, 0.3112912468366892d,
			115401, -0.33411394854894955d,
			115400, -0.10319130468863787d,
			115403, -0.7380151037685063d,
			207304, -0.07460535077700184d,
			115402, -0.38147795848833443d,
			115404, -0.8125502294660335d,
			207310, 0.3572618921544123d,
			153302, 0.6010094256860743d,
			134100, -0.13465231260837543d,
			134101, 0.11087519417725553d,
			153301, 0.5747227306225426d,
			106203, -2.393849677660491d,
			106202, -2.0873117350423795d,
			106204, -0.8226943130543642d,
			106207, -1.4051640234159233d,
			106206, -0.8797695620379107d,
			106209, 0.1943653458623092d,
			114401, -0.16364741554376241d,
			106208, -1.0638733077308757d,
			114400, -0.11449728057513861d,
			106211, -1.0460240915898267d,
			114403, -0.15482665868271833d,
			114402, -0.10981861418848725d,
			106210, 0.16195495590632014d,
			106212, -0.25091180447462924d,
			114404, -0.13028646729956206d,
			106215, -0.14549848501097237d,
			106214, -1.4797542026181651d,
			116201, 0.6388354010094074d,
			116200, 0.7924621516585404d,
			116202, 0.533270577934211d,
			216301, 0.16747913472407247d,
			216300, 0.8099240433489436d,
			105201, 0.07571701124699833d,
			105202, -0.06906898812339575d,
			116210, 0.5793769304831321d,
			216310, 0.16964757212192544d
	};
}
