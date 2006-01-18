/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.Isotope;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Analyses a molecular formula given in String format and builds
 * an AtomContainer with the Atoms in the molecular formula.
 *
 * @author         seb
 * @cdk.created    13. April 2005
 * @cdk.module     standard
 * @cdk.keyword    molecule, molecular mass
 * @cdk.keyword    molecule, molecular formula
 * @cdk.keyword    molecule, double bond equivalents
 */
public class MFAnalyser {

	private final static String H_ELEMENT_SYMBOL = "H";

	private String MF;
	private IAtomContainer atomContainer;
	private int HCount = 0;
	HashMap massMap=new HashMap();

	/**
	 * Construct an instance of MFAnalyser, initialized with a molecular
	 * formula string. The string is immediatly analysed and a set of Nodes
	 * is built based on this analysis
	 *
	 * @param  MF  Description of the Parameter
	 * @param target TODO
	 */
	public MFAnalyser(String MF, IAtomContainer target) {
		this.MF = MF;
		this.atomContainer = analyseMF(MF, target);
		initMassMap();
	}


	/**
	 * Construct an instance of MFAnalyser, initialized with a set of Nodes
	 * The set is analysed and a molecular formular is constructed
	 *  based on this analysis
	 *
	 * @param  ac  Description of the Parameter
	 */
	public MFAnalyser(IAtomContainer ac) {
		this.atomContainer = ac;
		this.MF = analyseAtomContainer(ac);
		initMassMap();
	}
	
	private void initMassMap(){
		massMap.put("1","1.00794");
		massMap.put("2","4.002602");
		massMap.put("3","6.941");
		massMap.put("4","9.012182");
		massMap.put("5","10.811");
		massMap.put("6","12.0107");
		massMap.put("7","14.0067");
		massMap.put("8","15.9994");
		massMap.put("9","18.9984032");
		massMap.put("10","20.1797");
		massMap.put("11","22.989770");
		massMap.put("12","24.3050");
		massMap.put("13","26.981538");
		massMap.put("14","28.0855");
		massMap.put("15","30.973761");
		massMap.put("16","32.065");
		massMap.put("17","35.453");
		massMap.put("18","39.948");
		massMap.put("19","39.0983");
		massMap.put("20","40.078");
		massMap.put("21","44.955910");
		massMap.put("22","47.867");
		massMap.put("23","50.9415");
		massMap.put("24","51.9961");
		massMap.put("25","54.938049");
		massMap.put("26","55.845");
		massMap.put("27","58.933200");
		massMap.put("28","58.6934");
		massMap.put("29","	63.546");
		massMap.put("30","65.409");
		massMap.put("31","69.723");
		massMap.put("32","72.64");
		massMap.put("33","74.92160");
		massMap.put("34","78.96");
		massMap.put("35","79.904");
		massMap.put("36","83.798");
		massMap.put("37","85.4678");
		massMap.put("38","87.62");
		massMap.put("39","88.90585");
		massMap.put("40","91.224");
		massMap.put("41","92.90638");
		massMap.put("42","95.94");
		massMap.put("43","98");
		massMap.put("44","101.07");
		massMap.put("45","102.90550");
		massMap.put("46","106.42");
		massMap.put("47","107.8682");
		massMap.put("48","112.411");
		massMap.put("49","114.818");
		massMap.put("50","118.710");
		massMap.put("51","121.760");
		massMap.put("52","127.60");
		massMap.put("53","126.90447");
		massMap.put("54","131.293");
		massMap.put("55","132.90545");
		massMap.put("56","137.327");
		massMap.put("57","138.9055");
		massMap.put("58","140.116");
		massMap.put("59","140.90765");
		massMap.put("60","144.24");
		massMap.put("61","145");
		massMap.put("62","150.36");
		massMap.put("63","151.964");
		massMap.put("64","157.25");
		massMap.put("65","158.92534");
		massMap.put("66","162.500");
		massMap.put("67","164.93032");
		massMap.put("68","167.259");
		massMap.put("69","168.93421");
		massMap.put("70","173.04");
		massMap.put("71","174.967");
		massMap.put("72","178.49");
		massMap.put("73","180.9479");
		massMap.put("74","183.84");
		massMap.put("75","186.207");
		massMap.put("76","190.23");
		massMap.put("77","192.217");
		massMap.put("78","195.078");
		massMap.put("79","196.96655");
		massMap.put("80","200.59");
		massMap.put("81","204.3833");
		massMap.put("82","207.2");
		massMap.put("83","208.98038");
		massMap.put("84","209");
		massMap.put("85","210");
		massMap.put("86","222");
		massMap.put("87","223");
		massMap.put("88","226");
		massMap.put("89","227");
		massMap.put("90","232.0381");
		massMap.put("91","231.03588");
		massMap.put("92","238.02891");
		massMap.put("93","237");
		massMap.put("94","244");
		massMap.put("95","243");
		massMap.put("96","247");
		massMap.put("97","247");
		massMap.put("98","251");
		massMap.put("99","252");
		massMap.put("100","257");
		massMap.put("101","258");
		massMap.put("102","259");
		massMap.put("103","262");
		massMap.put("104","261");
		massMap.put("105","262");
		massMap.put("106","266");
		massMap.put("107","264");
		massMap.put("108","277");
		massMap.put("109","268");
		massMap.put("110","281");
		massMap.put("111","272");
		massMap.put("112","285");
		massMap.put("113","284");
		massMap.put("114","289");
		massMap.put("115","288");
		massMap.put("116","292");		
	}


	/**
	 * returns the complete set of Nodes, as implied by the molecular
	 * formula, including all the hydrogens.
	 *
	 * @return    The atomContainer value
	 */
	public IAtomContainer getAtomContainer() {
		return atomContainer;
	}


	/**
	 * Returns the complete set of Nodes, as implied by the molecular
	 * formula, including all the hydrogens.
	 *
	 * @return    The molecularFormula value
	 * @see       #getHTMLMolecularFormula()
	 */
	public String getMolecularFormula() {
		return MF;
	}


	/**
	 * Returns the string representation of the molecule formula with
	 * numbers wrapped in &lt;sub&gt;&lt;/sub&gt; tags. Useful for displaying
	 * formulae in Swing components or on the web.
	 *
	 * @return    A HTML representation of the molecular formula.
	 */
	public String getHTMLMolecularFormula() {
		boolean lastCharacterWasDigit = false;
		boolean currentCharacterIsDigit;
		StringBuffer htmlString = new StringBuffer(MF);

		for (int characterCounter = 0; characterCounter <= htmlString.length(); characterCounter++) {
			try {
				currentCharacterIsDigit = Character.isDigit(htmlString.charAt(characterCounter));
			} catch (StringIndexOutOfBoundsException oobe) {
				currentCharacterIsDigit = false;
			}

			if (currentCharacterIsDigit && !lastCharacterWasDigit) {
				//Insert an opening sub and move the counter beyond it
				htmlString.insert(characterCounter, "<sub>");
				characterCounter += 5;
			} else if (lastCharacterWasDigit && !currentCharacterIsDigit) {
				//Insert a closing sub and move the counter beyond it
				htmlString.insert(characterCounter, "</sub>");
				characterCounter += 6;
			}

			lastCharacterWasDigit = currentCharacterIsDigit;
		}

		return htmlString.toString();
	}


	/**
	 * returns the number of double bond equivalents in this molecule
	 *
	 * @return    The number of DBEs
	 */
	public float getDBE() throws IOException, ClassNotFoundException{
		int valencies[]=new int[5];
		AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/structgen_atomtypes.xml", getAtomContainer().getBuilder());
		IAtomContainer ac = getAtomContainer();
		for (int f = 0; f < ac.getAtomCount(); f++) {
   		    IAtomType[] types = factory.getAtomTypes(ac.getAtomAt(f).getSymbol());
   		    valencies[(int)types[0].getBondOrderSum()]++;
		}
		return  1 + (valencies[4]) + (valencies[3] /2) - (valencies[1] /2);
	}
	
	
	/**
	 * returns the exact mass for a given molecular formula, using major isotope for each element.
	 *
	 * @return    The mass value
	 */
	public float getMass() {
		float mass = 0;
		Isotope i;
		IsotopeFactory si = null;
		try {
			si = IsotopeFactory.getInstance(getAtomContainer().getBuilder());
		} catch (Exception exception) {
			System.err.println("Could not instantiate the IsotopeFactory: " + exception.getMessage());
		}
		IAtomContainer ac = getAtomContainer();
		Isotope h = si.getMajorIsotope(H_ELEMENT_SYMBOL);
		for (int f = 0; f < ac.getAtomCount(); f++) {
			i = si.getMajorIsotope(ac.getAtomAt(f).getSymbol());
			if (i != null) {
				mass += i.getExactMass();
			} else {
				return 0;
			}
			mass += ac.getAtomAt(f).getHydrogenCount() * h.getExactMass();
		}
		return mass;
	}


	/**
	 *  Gets the natural mass of this element, defined as average of masses of isotopes, weighted by abundance.
	 *
	 * @param  element                     Description of the Parameter
	 * @return                             The natural mass value
	 * @exception  java.io.IOException     Description of the Exception
	 * @exception  ClassNotFoundException  Description of the Exception
	 */
	public double getNaturalMass(IElement element) throws java.io.IOException, ClassNotFoundException {
		Isotope[] isotopes = IsotopeFactory.getInstance(getAtomContainer().getBuilder()).getIsotopes(element.getSymbol());
		double summedAbundances = 0;
		double summedWeightedAbundances = 0;
		for (int i = 0; i < isotopes.length; i++) {
			summedAbundances += isotopes[i].getNaturalAbundance();
			summedWeightedAbundances += isotopes[i].getNaturalAbundance() * isotopes[i].getExactMass();
		}
		return summedWeightedAbundances / summedAbundances;
	}


	public double getCanonicalMass(IElement element){
		return Double.parseDouble((String)massMap.get(element.getAtomicNumber()+""));
	}
	
	/**
	 * returns the exact mass for a given molecular formula, using using IUPAC official masses published in Pure Appl. Chem., (2003) 75, 1107-1122. 
	 *
	 * @return                             The naturalMass value
	 * @exception  java.io.IOException     Description of the Exception
	 * @exception  ClassNotFoundException  Description of the Exception
	 */
	public float getCanonicalMass() throws java.io.IOException, ClassNotFoundException {
		float mass = 0;
		IsotopeFactory si = null;
		try {
			si = IsotopeFactory.getInstance(getAtomContainer().getBuilder());
		} catch (Exception exception) {
			System.err.println("Could not instantiate the IsotopeFactory: " + exception.getMessage());
		}
		IAtomContainer ac = getAtomContainer();
		Isotope h = si.getMajorIsotope("H");
		for (int f = 0; f < ac.getAtomCount(); f++) {
			IElement i = si.getElement(ac.getAtomAt(f).getSymbol());
			if (i != null) {
				mass += getCanonicalMass(i);
			} else {
				return 0;
			}
			mass += ac.getAtomAt(f).getHydrogenCount() * getCanonicalMass(h);
		}
		return mass;
	}
	
	
	/**
	 * returns the exact mass for a given molecular formula, using weighted average of isotopes.
	 *
	 * @return                             The naturalMass value
	 * @exception  java.io.IOException     Description of the Exception
	 * @exception  ClassNotFoundException  Description of the Exception
	 */
	public float getNaturalMass() throws java.io.IOException, ClassNotFoundException {
		float mass = 0;
		IsotopeFactory si = null;
		try {
			si = IsotopeFactory.getInstance(getAtomContainer().getBuilder());
		} catch (Exception exception) {
			System.err.println("Could not instantiate the IsotopeFactory: " + exception.getMessage());
		}
		IAtomContainer ac = getAtomContainer();
		Isotope h = si.getMajorIsotope("H");
		for (int f = 0; f < ac.getAtomCount(); f++) {
			IElement i = si.getElement(ac.getAtomAt(f).getSymbol());
			if (i != null) {
				mass += getNaturalMass(i);
			} else {
				return 0;
			}
			mass += ac.getAtomAt(f).getHydrogenCount() * getNaturalMass(h);
		}
		return mass;
	}


	/**
	 * Produces an AtomContainer without explicit Hs but with H count from one with Hs.
	 * Hs bonded to more than one heavy atom are preserved.  The new molecule is a deep copy.
	 *
	 * @return         The mol without Hs.
	 * @cdk.keyword    hydrogen, removal
	 */
	public IAtomContainer removeHydrogensPreserveMultiplyBonded() {
		IAtomContainer ac = getAtomContainer();

		List h = new ArrayList();
		// H list.
		List multi_h = new ArrayList();
		// multiply bonded H

		// Find multiply bonded H.
		int count = ac.getBondCount();
		for (int i = 0;
				i < count;
				i++) {
			final IAtom[] atoms = ac.getBondAt(i).getAtoms();
			final int length = atoms.length;
			for (int k = 0;
					k < length;
					k++) {
				final IAtom atom = atoms[k];
				if (atom.getSymbol().equals(H_ELEMENT_SYMBOL)) {
					(h.contains(atom) ? multi_h : h).add(atom);
				}
			}

			// The short version (assumes atoms.length == 2 is always true).
//            (h.contains(atoms[0]) ? multi_h : h).add(atoms[0]);
//            (h.contains(atoms[1]) ? multi_h : h).add(atoms[1]);
		}

		return removeHydrogens(multi_h);
	}


	/**
	 * Produces an AtomContainer without explicit Hs (except those listed) but with H count from one with Hs.
	 * The new molecule is a deep copy.
	 *
	 * @param  preserve  a list of H atoms to preserve.
	 * @return           The mol without Hs.
	 * @cdk.keyword      hydrogen, removal
	 */
	private IAtomContainer removeHydrogens(List preserve) {
		IAtomContainer ac = getAtomContainer();

		Map map = new HashMap();
		// maps original atoms to clones.
		List remove = new ArrayList();
		// lists removed Hs.

		// Clone atoms except those to be removed.
		Molecule mol = ac.getBuilder().newMolecule();
		int count = ac.getAtomCount();
		for (int i = 0;
				i < count;
				i++) {
			// Clone/remove this atom?
			IAtom atom = ac.getAtomAt(i);
			if (!atom.getSymbol().equals(H_ELEMENT_SYMBOL) || preserve.contains(atom)) {
				IAtom a = (IAtom) atom.clone();
				a.setHydrogenCount(0);
				mol.addAtom(a);
				map.put(atom, a);
			} else {
				remove.add(atom);
				// maintain list of removed H.
			}
		}

		// Clone bonds except those involving removed atoms.
		count = ac.getBondCount();
		for (int i = 0;
				i < count;
				i++) {
			// Check bond.
			final IBond bond = ac.getBondAt(i);
			IAtom[] atoms = bond.getAtoms();
			boolean remove_bond = false;
			final int length = atoms.length;
			for (int k = 0;
					k < length;
					k++) {
				if (remove.contains(atoms[k])) {
					remove_bond = true;
					break;
				}
			}

			// Clone/remove this bond?
			if (!remove_bond) {
				// if (!remove.contains(atoms[0]) && !remove.contains(atoms[1]))

				IBond clone = (IBond) ac.getBondAt(i).clone();
				clone.setAtoms(new IAtom[]{(IAtom) map.get(atoms[0]), (IAtom) map.get(atoms[1])});
				mol.addBond(clone);
			}
		}

		// Recompute hydrogen counts of neighbours of removed Hydrogens.
		for (Iterator i = remove.iterator();
				i.hasNext(); ) {
			// Process neighbours.
			for (Iterator n = ac.getConnectedAtomsVector((IAtom) i.next()).iterator();
					n.hasNext(); ) {
				final IAtom neighb = (IAtom) map.get(n.next());
				neighb.setHydrogenCount(neighb.getHydrogenCount() + 1);
			}
		}

		return (mol);
	}


	/**
	 * Returns a set of nodes excluding all the hydrogens
	 *
	 * @return         The heavyAtoms value
	 * @cdk.keyword    hydrogen, removal
	 */
	public List getHeavyAtoms() {
		ArrayList newAc = new ArrayList();
		IAtomContainer ac = getAtomContainer();
		for (int f = 0; f < ac.getAtomCount(); f++) {
			if (!ac.getAtomAt(f).getSymbol().equals(H_ELEMENT_SYMBOL)) {
				newAc.add(ac.getAtomAt(f));
			}
		}
		return newAc;
	}


	/**
	 * Method that actually does the work of analysing the molecular formula
	 *
	 * @param  MF  molecular formula to create an AtomContainer from
	 * @param  ac  AtomContainer in which the Atom's and Bond's will be stored 
	 * @return     the filled AtomContainer
	 */
	private IAtomContainer analyseMF(String MF, IAtomContainer ac) {
		char ThisChar;
		/*
		 *  Buffer for
		 */
		String RecentElementSymbol = new String();
		String RecentElementCountString = new String("0");
		/*
		 *  String to be converted to an integer
		 */
		int RecentElementCount;

		if (MF.length() == 0) {
			return null;
		}

		for (int f = 0; f < MF.length(); f++) {
			ThisChar = MF.charAt(f);
			if (f < MF.length()) {
				if (ThisChar >= 'A' && ThisChar <= 'Z') {
					/*
					 *  New Element begins
					 */
					RecentElementSymbol = java.lang.String.valueOf(ThisChar);
					RecentElementCountString = "0";
				}
				if (ThisChar >= 'a' && ThisChar <= 'z') {
					/*
					 *  Two-letter Element continued
					 */
					RecentElementSymbol += ThisChar;
				}
				if (ThisChar >= '0' && ThisChar <= '9') {
					/*
					 *  Two-letter Element continued
					 */
					RecentElementCountString += ThisChar;
				}
			}
			if (f == MF.length() - 1 || (MF.charAt(f + 1) >= 'A' && MF.charAt(f + 1) <= 'Z')) {
				/*
				 *  Here an element symbol as well as its number should have been read completely
				 */
				Integer RecentElementCountInteger = new Integer(RecentElementCountString);
				RecentElementCount = RecentElementCountInteger.intValue();
				if (RecentElementCount == 0) {
					RecentElementCount = 1;
				}
				for (int g = 0; g < RecentElementCount; g++) {
					ac.addAtom(ac.getBuilder().newAtom(RecentElementSymbol));
				}
			}
		}
		return ac;
	}


	/**
	 * Analyses a set of Nodes that has been changed or recently loaded
	 * and  returns a molecular formula
	 *
	 * @param  ac  Description of the Parameter
	 * @return     a string containing the molecular formula.
	 */
	public String analyseAtomContainer(IAtomContainer ac) {
		String symbol;
		String mf = "";
		SortedMap symbols = new TreeMap();
		int HCount = 0;
		IAtom atom = null;
		for (int f = 0; f < ac.getAtomCount(); f++) {
			atom = ac.getAtomAt(f);
			symbol = atom.getSymbol();
			if (atom.getHydrogenCount() > 0) {
				HCount += atom.getHydrogenCount();
			}
			if (symbols.get(symbol) != null) {
				symbols.put(symbol, new Integer(((Integer) symbols.get(symbol)).intValue() + 1));
			} else {
				symbols.put(symbol, new Integer(1));
			}
		}
		mf = addSymbolToFormula(symbols, "C", mf);
		if (symbols.get(H_ELEMENT_SYMBOL) != null) {
			mf = addSymbolToFormula(symbols, H_ELEMENT_SYMBOL, mf);
		} else {
			if (HCount > 0) {
				mf += H_ELEMENT_SYMBOL;
				if (HCount > 1) {
					mf += Integer.toString(HCount);
				}
			}
		}
		mf = addSymbolToFormula(symbols, "N", mf);
		mf = addSymbolToFormula(symbols, "O", mf);
		mf = addSymbolToFormula(symbols, "S", mf);
		mf = addSymbolToFormula(symbols, "P", mf);
		Iterator it = symbols.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			if (!((String) key).equals("C") && !((String) key).equals(H_ELEMENT_SYMBOL) && !((String) key).equals("N") && !((String) key).equals("O") && !((String) key).equals("S") && !((String) key).equals("P")) {
				mf = addSymbolToFormula(symbols, (String) key, mf);
			}
		}
		this.HCount = HCount;
		return mf;
	}


	/**
	 * Adds an element to a chemical formual string
	 *
	 * @param  sm       The map containing the elements
	 * @param  symbol   The symbol to add
	 * @param  formula  The chemical formula
	 * @return          Description of the Return Value
	 */
	private String addSymbolToFormula(SortedMap sm, String symbol, String formula) {
		if (sm.get(symbol) != null) {
			formula += symbol;
			if (!sm.get(symbol).equals(new Integer(1))) {
				formula += sm.get(symbol).toString();
			}
		}
		return (formula);
	}


	/**
	 * Checks a set of Nodes for the occurence of a particular
	 * element.
	 *
	 * @param  thisElement  Description of the Parameter
	 * @return              The number of atoms for the particular element in the formula
	 */
	public int getAtomCount(String thisElement) {
		int atomCount = 0;
		if (thisElement.equals(H_ELEMENT_SYMBOL) && HCount > 0) {
			return HCount;
		}
		for (int f = 0; f < atomContainer.getAtomCount(); f++) {
			if (atomContainer.getAtomAt(f).getSymbol().equals(thisElement)) {
				atomCount++;
			}
		}
		return atomCount;
	}


	/**
	 * Returns a Vector with asorted element names.
	 * The order is determined by ElementComparator.
	 *
	 * @return    The elements value
	 * @see       ElementComparator
	 */
	public Vector getElements() {
		TreeSet elements = new TreeSet(new ElementComparator());
		for (int f = 0; f < atomContainer.getAtomCount(); f++) {
			String symbol = atomContainer.getAtomAt(f).getSymbol();
			if (!elements.contains(symbol)) {
				elements.add(symbol);
			}
		}
		Vector results = new Vector();
		Iterator iter = elements.iterator();
		while (iter.hasNext()) {
			results.add((String) iter.next());
		}
		return results;
	}


	/**
	 * Returns the number of distinct elements in the formula.
	 *
	 * @return    The elementCount value
	 */
	public int getElementCount() {
		return getElements().size();
	}


	/**
	 *  Gets a Molecule and an array of element symbols. Counts how many of each of these elements
	 *  the molecule contains. Then it returns the elements followed by their number as a string,
	 *  i.e. C15H8N3.
	 *
	 * @param  mol       The Molecule to be searched
	 * @param  elements  Description of the Parameter
	 * @return           The element formula as a string
	 */
	public static String generateElementFormula(Molecule mol, String[] elements) {
		int num = elements.length;
		StringBuffer formula = new StringBuffer();
		int[] elementCount = new int[num];
		for (int i = 0; i < mol.getAtomCount(); i++) {
			for (int j = 0; j < num; j++) {
				if (elements[j].equals(mol.getAtomAt(i).getSymbol())) {
					elementCount[j]++;
				}
			}
		}
		for (int i = 0; i < num; i++) {
			formula.append(elements[i] + elementCount[i]);
		}
		return formula.toString();
	}


	/**
	 *  Builds the elemental formula of a given molecule as a Hashtable.
	 *  Keys are the elemental symbols (Strings) and values are the no. of occurrence (Integer objects).
	 *
	 * @return    a Hashtable, keys are the elemental symbols and values are their no.
	 */
	public Hashtable getFormulaHashtable() {
		Hashtable formula = new Hashtable();
		Vector elements = this.getElements();
		for (int i = 0; i < elements.size(); i++) {
			Integer numOfAtom = new Integer(this.getAtomCount((String) elements.get(i)));
			formula.put(elements.get(i), numOfAtom);
		}
		return formula;
	}

	/**
	 * 
	 * Returns the string representation of the molecule formula with
	 * numbers wrapped in &lt;sub&gt;&lt;/sub&gt; tags and the total
	 * charge of AtomContainer in &lt;sup&gt;&lt;/sup&gt; tags
	 * Useful for displaying formulae in Swing components or on the web.
	 *
	 * @return    The html-string representation of the sum formula with charge 
	 * @see #getHTMLMolecularFormula()
	 */
	public String getHTMLMolecularFormulaWithCharge() {
		String formula = new MFAnalyser(atomContainer).getHTMLMolecularFormula();
		int charge = AtomContainerManipulator.getTotalFormalCharge(atomContainer);
		if (charge == 0)
		{
			return formula;
		} else if (charge < 0) {
			return formula + "<sup>" + charge * -1 + "-" + "</sup>";
		} else {
			return formula + "<sup>" + charge +"+" + "</sup>";
		}
	}
}


