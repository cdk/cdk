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
		Map<Integer, Double> coeffs = null;
		
		/**
		 * Initialises the required coefficients for the trained model from the paper.
		 */
		public JPlogPCalculator() 
		{
				coeffs = new HashMap<>();
				initcoeffs();
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
		private void initcoeffs()
		{
			coeffs.put(115200, 0.3428999504964441);
			coeffs.put(134400, -0.6009339899021935);
			coeffs.put(207110, -0.2537868203838485);
			coeffs.put(134404, -1.175240011610751);
			coeffs.put(153100, 0.9269788584798107);
			coeffs.put(153101, 1.0143514773529836);
			coeffs.put(133402, -0.25834811742258956);
			coeffs.put(114200, 0.02852160272741147);
			coeffs.put(133403, -0.8194572348477968);
			coeffs.put(5401, 0.10394247591686294);
			coeffs.put(101147, 1.1304660454056645);
			coeffs.put(5402, 0.05199289233087018);
			coeffs.put(101146, 1.2682622213479229);
			coeffs.put(133401, -0.3667171872036366);
			coeffs.put(5403, 0.6787470799044643);
			coeffs.put(101149, 1.1949147655182892);
			coeffs.put(101148, 1.140089012601143);
			coeffs.put(101151, 1.124474664082366);
			coeffs.put(133404, -0.6914424692323852);
			coeffs.put(101150, -0.243037761960754);
			coeffs.put(107301, 0.17827258865107412);
			coeffs.put(107303, -0.018054804206490728);
			coeffs.put(107302, 0.174747113462705);
			coeffs.put(7207, -0.1900259417225229);
			coeffs.put(107304, 0.15377727288498777);
			coeffs.put(109101, 0.4139612626967727);
			coeffs.put(115501, -0.14127468400014018);
			coeffs.put(115500, 0.17611532140449737);
			coeffs.put(115503, 0.01466761555846904);
			coeffs.put(109103, 0.26645165703555906);
			coeffs.put(115502, -0.12059099254331078);
			coeffs.put(115505, -0.3426049607280402);
			coeffs.put(109105, 0.43024980310815464);
			coeffs.put(115504, -0.10220447776639764);
			coeffs.put(207409, -0.2390615198632319);
			coeffs.put(109107, 0.46498369443558685);
			coeffs.put(109109, 0.4802991193785544);
			coeffs.put(109108, 0.2432096581199898);
			coeffs.put(134202, -0.6346778955294479);
			coeffs.put(134200, -0.049120673015459124);
			coeffs.put(134201, 0.011636535054497877);
			coeffs.put(106303, -1.3926887806607753);
			coeffs.put(106302, -1.1712347846892444);
			coeffs.put(106305, 0.11166429492968204);
			coeffs.put(134210, 0.8723651043132042);
			coeffs.put(106304, 0.2093224791516782);
			coeffs.put(106307, -0.2713000174540411);
			coeffs.put(106306, 0.01721171808054029);
			coeffs.put(108101, -0.09302944637095042);
			coeffs.put(106308, 0.02622454278720872);
			coeffs.put(108103, 0.016350079235673567);
			coeffs.put(106311, 0.14287513828273285);
			coeffs.put(108102, 0.09827832065783548);
			coeffs.put(108105, -0.47007800462613053);
			coeffs.put(106313, 0.29015591756689035);
			coeffs.put(108104, 0.015437937018121088);
			coeffs.put(108107, 0.03827614806405689);
			coeffs.put(108106, 0.19321034882780294);
			coeffs.put(106314, -0.3943404186929503);
			coeffs.put(108109, -0.10401802166752877);
			coeffs.put(116301, -0.05382098343764403);
			coeffs.put(116300, 0.7387521460456399);
			coeffs.put(116303, -0.16756182500979416);
			coeffs.put(116302, -0.13139795079560512);
			coeffs.put(108110, 0.09217290650820302);
			coeffs.put(5201, 0.23346448860951585);
			coeffs.put(5202, 0.2039139119266068);
			coeffs.put(133200, -0.30769228187947917);
			coeffs.put(208208, 0.7096167841949101);
			coeffs.put(133201, -0.4991951722354514);
			coeffs.put(105301, -0.16207989873206854);
			coeffs.put(105300, -0.17440788934127419);
			coeffs.put(105303, -0.14262227829859978);
			coeffs.put(105302, -0.1772536453086892);
			coeffs.put(107101, 0.06472635260371458);
			coeffs.put(107103, -0.25579034271921075);
			coeffs.put(107102, 0.07815605927333318);
			coeffs.put(107104, -0.3248358665028741);
			coeffs.put(107107, 0.40442424583948916);
			coeffs.put(107106, 0.396893949325775);
			coeffs.put(207207, -0.07223876787467944);
			coeffs.put(115301, -0.02360338462248146);
			coeffs.put(107108, 0.11602222985765208);
			coeffs.put(207206, -0.24327541812800021);
			coeffs.put(115300, 0.08742831050292114);
			coeffs.put(115303, -0.23502791004771306);
			coeffs.put(115302, -0.10635975733575764);
			coeffs.put(117101, 0.7375898512161616);
			coeffs.put(117100, 0.711562233142568);
			coeffs.put(153200, 0.7953592172870871);
			coeffs.put(106103, -3.373528417699127);
			coeffs.put(106102, -3.222960398502975);
			coeffs.put(116600, 1.2414649166226785);
			coeffs.put(106107, -2.1610769299516286);
			coeffs.put(106106, -1.794277022889798);
			coeffs.put(114301, -0.13076494426939203);
			coeffs.put(106109, -0.7983974980016113);
			coeffs.put(114300, -0.2992443066268472);
			coeffs.put(114302, -0.3024419065452111);
			coeffs.put(106112, -1.1706517781448094);
			coeffs.put(116101, 1.25794766342382);
			coeffs.put(116100, 0.7033847677524618);
			coeffs.put(216210, 0.7111335744036255);
			coeffs.put(134302, -0.3781625467763615);
			coeffs.put(134303, -0.363065393849358);
			coeffs.put(134300, -0.46600673735969483);
			coeffs.put(134301, -0.38176583952591553);
			coeffs.put(106403, -0.6031848047124038);
			coeffs.put(106402, -0.19127514916328328);
			coeffs.put(8104, -0.24231144996862355);
			coeffs.put(108201, 0.03078259547644759);
			coeffs.put(8105, -0.42080392882157724);
			coeffs.put(108203, 0.16517108509661693);
			coeffs.put(8106, -0.29674843353741903);
			coeffs.put(108202, 0.12117897946674977);
			coeffs.put(116401, 0.7441487272627919);
			coeffs.put(108208, 0.06651412646122902);
			coeffs.put(116403, 0.3031855131038271);
			coeffs.put(116402, 0.543649606560247);
			coeffs.put(133302, -0.7027480278305203);
			coeffs.put(114100, 0.3786112153751248);
			coeffs.put(133303, -0.431414306011833);
			coeffs.put(116404, 0.02729394485921511);
			coeffs.put(133300, -0.20426419244061747);
			coeffs.put(133301, 0.2550357745554604);
			coeffs.put(135100, 0.8603178696688789);
			coeffs.put(135101, 0.7236559576494113);
			coeffs.put(107201, 0.260897789525647);
			coeffs.put(107203, 0.2070230861251806);
			coeffs.put(107202, 0.16622067949480449);
			coeffs.put(107205, -0.2544031618211347);
			coeffs.put(7108, 0.2615348564811823);
			coeffs.put(207303, -0.4723141310610628);
			coeffs.put(107204, 0.13376742355228766);
			coeffs.put(207302, 0.24697598201746412);
			coeffs.put(107207, 0.316655927726688);
			coeffs.put(207301, 0.36095025588716984);
			coeffs.put(107206, 0.3112912468366892);
			coeffs.put(115401, -0.33411394854894955);
			coeffs.put(115400, -0.10319130468863787);
			coeffs.put(115403, -0.7380151037685063);
			coeffs.put(207304, -0.07460535077700184);
			coeffs.put(115402, -0.38147795848833443);
			coeffs.put(115404, -0.8125502294660335);
			coeffs.put(207310, 0.3572618921544123);
			coeffs.put(153302, 0.6010094256860743);
			coeffs.put(134100, -0.13465231260837543);
			coeffs.put(134101, 0.11087519417725553);
			coeffs.put(153301, 0.5747227306225426);
			coeffs.put(106203, -2.393849677660491);
			coeffs.put(106202, -2.0873117350423795);
			coeffs.put(106204, -0.8226943130543642);
			coeffs.put(106207, -1.4051640234159233);
			coeffs.put(106206, -0.8797695620379107);
			coeffs.put(106209, 0.1943653458623092);
			coeffs.put(114401, -0.16364741554376241);
			coeffs.put(106208, -1.0638733077308757);
			coeffs.put(114400, -0.11449728057513861);
			coeffs.put(106211, -1.0460240915898267);
			coeffs.put(114403, -0.15482665868271833);
			coeffs.put(114402, -0.10981861418848725);
			coeffs.put(106210, 0.16195495590632014);
			coeffs.put(106212, -0.25091180447462924);
			coeffs.put(114404, -0.13028646729956206);
			coeffs.put(106215, -0.14549848501097237);
			coeffs.put(106214, -1.4797542026181651);
			coeffs.put(116201, 0.6388354010094074);
			coeffs.put(116200, 0.7924621516585404);
			coeffs.put(116202, 0.533270577934211);
			coeffs.put(216301, 0.16747913472407247);
			coeffs.put(216300, 0.8099240433489436);
			coeffs.put(105201, 0.07571701124699833);
			coeffs.put(105202, -0.06906898812339575);
			coeffs.put(116210, 0.5793769304831321);
			coeffs.put(216310, 0.16964757212192544);
		}
		
		public Map<Integer, Double> getCoeffs() {
			return coeffs;
		}

		public void setCoeffs(Map<Integer, Double> coeffs) {
			this.coeffs = coeffs;
		}
	}

}
