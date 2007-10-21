/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2002-2007  Christoph Steinbeck
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
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.IOException;
import java.util.*;

/**
 * Analyses a molecular formula given in String format and builds
 * an AtomContainer with the Atoms in the molecular formula.
 * 
 * About implict H handling: By default the methods to calculate formula, natural and canonical mass
 * use the explicit Hs and only the explicit Hs if there is at least one in the molecule, implicit Hs are 
 * ignored. If there is no explicit H and only then the implicit Hs are used. If you use the constructor 
 * MFAnalyser(IAtomContainer ac, boolean useboth) and set useboth to true, all explicit Hs and all implicit Hs are used, 
 * the implicit ones also on atoms with explicit Hs.
 *
 * @author         Christoph Steinbeck
 * @author         Stefan Kuhn
 * @author         Egon Willighagen
 * @cdk.created    MFAnalyser
 * @cdk.module     standard
 * @cdk.svnrev  $Revision$
 * @cdk.keyword    molecule, molecular mass
 * @cdk.keyword    molecule, molecular formula
 * @cdk.keyword    molecule, double bond equivalents
 */
public class MFAnalyser {

	private final static String H_ELEMENT_SYMBOL = "H";

	private String MF;
	private IAtomContainer atomContainer;
	private int HCount = 0;
	private boolean useboth=false;
	
	private LoggingTool logger = new LoggingTool(MFAnalyser.class);
	
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
	}


	/**
	 * Construct an instance of MFAnalyser, initialized with a set of Nodes
	 * The set is analysed and a molecular formular is constructed
	 *  based on this analysis
	 *
	 * @param  ac  Description of the Parameter
	 */
	public MFAnalyser(IAtomContainer ac) {
		this(ac,false);
	}
	
	/**
	 * Construct an instance of MFAnalyser, initialized with a set of Nodes
	 * The set is analysed and a molecular formular is constructed
	 *  based on this analysis
	 *
	 * @param  ac  Description of the Parameter
	 * @param  useboth true=implicit and explicit hs will be used, false=explicit used, implicit only if no explicit
	 */
	public MFAnalyser(IAtomContainer ac, boolean useboth) {
		this.useboth=useboth;
		this.atomContainer = ac;
		this.MF = analyseAtomContainer(ac);
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
	 * Returns the number of double bond equivalents in this molecule.
	 *
	 * @return      The number of DBEs
	 * @cdk.keyword DBE
	 * @cdk.keyword double bond equivalent
	 */
	public float getDBE() throws IOException, ClassNotFoundException, CDKException{
		int valencies[]=new int[5];
		AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/structgen_atomtypes.xml", getAtomContainer().getBuilder());
		IAtomContainer ac = getAtomContainer();
		for (int f = 0; f < ac.getAtomCount(); f++) {
   		    IAtomType[] types = factory.getAtomTypes(ac.getAtom(f).getSymbol());
   		    if(types.length==0)
   		    	throw new CDKException("Calculation of double bond equivalents not possible due to problems with element "+ac.getAtom(f).getSymbol());
   		    valencies[(int)types[0].getBondOrderSum().intValue()+ac.getAtom(f).getFormalCharge()]++;
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
		IIsotope i;
		IsotopeFactory si = null;
		try {
			si = IsotopeFactory.getInstance(getAtomContainer().getBuilder());
		} catch (Exception exception) {
			System.err.println("Could not instantiate the IsotopeFactory: " + exception.getMessage());
		}
		IAtomContainer ac = getAtomContainer();
		IIsotope h = si.getMajorIsotope(H_ELEMENT_SYMBOL);
		for (int f = 0; f < ac.getAtomCount(); f++) {
			i = si.getMajorIsotope(ac.getAtom(f).getSymbol());
			if (i != null) {
				mass += i.getExactMass();
			} else {
				return 0;
			}
            int hcount = 0;
            if (ac.getAtom(f).getHydrogenCount() != CDKConstants.UNSET) hcount = ac.getAtom(f).getHydrogenCount();
            mass += hcount * h.getExactMass();
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
	public static double getNaturalMass(IElement element) throws java.io.IOException, ClassNotFoundException {
		IIsotope[] isotopes = IsotopeFactory.getInstance(element.getBuilder()).getIsotopes(element.getSymbol());
		double summedAbundances = 0;
		double summedWeightedAbundances = 0;
		double getNaturalMass = 0;
		for (int i = 0; i < isotopes.length; i++) {
			summedAbundances += isotopes[i].getNaturalAbundance();
			summedWeightedAbundances += isotopes[i].getNaturalAbundance() * isotopes[i].getExactMass();
			getNaturalMass = summedWeightedAbundances / summedAbundances;
		}
		return getNaturalMass;
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
		IIsotope h = si.getMajorIsotope("H");
		Map<String, Integer> symbols=this.getSymolMap(ac);
		Iterator<String> it = symbols.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (key.equals("H")){
				if(useboth){
					mass += MFAnalyser.getNaturalMass(h)*HCount;
				}else{
					if (symbols.get(key) != null) {
						mass += getNaturalMass(h)*symbols.get(key).intValue();
					} else {
						mass += getNaturalMass(h)*HCount;					
					}
				}
			}else{
				IElement i = si.getElement(key);
				mass += getNaturalMass(i)*symbols.get(key).intValue();
			}
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

		List<IAtom> h = new ArrayList<IAtom>();
		// H list.
		List<IAtom> multi_h = new ArrayList<IAtom>();
		// multiply bonded H

		// Find multiply bonded H.
		int count = ac.getBondCount();
		for (int i = 0;
				i < count;
				i++) {
			java.util.Iterator atoms = ac.getBond(i).atoms();
			while (atoms.hasNext()) {
				final IAtom atom = (IAtom)atoms.next();
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
	private IAtomContainer removeHydrogens(List<IAtom> preserve) {
		IAtomContainer ac = getAtomContainer();

		Map<IAtom,IAtom> map = new HashMap<IAtom,IAtom>();
		// maps original atoms to clones.
		List<IAtom> remove = new ArrayList<IAtom>();
		// lists removed Hs.

		// Clone atoms except those to be removed.
		IMolecule mol = ac.getBuilder().newMolecule();
		int count = ac.getAtomCount();
		for (int i = 0;
				i < count;
				i++) {
			// Clone/remove this atom?
			IAtom atom = ac.getAtom(i);
			if (!atom.getSymbol().equals(H_ELEMENT_SYMBOL) || preserve.contains(atom)) {
				IAtom a = null;
				try {
					a = (IAtom) atom.clone();
				} catch (CloneNotSupportedException e) {
					logger.error("Could not clone: ", atom);
					logger.debug(e);
				}
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
			final IBond bond = ac.getBond(i);
			IAtom atom0 = bond.getAtom(0);
			IAtom atom1 = bond.getAtom(1);
			java.util.Iterator atoms = bond.atoms();
			boolean remove_bond = false;
			while (atoms.hasNext()){
				if (remove.contains((IAtom)atoms.next())) {
					remove_bond = true;
					break;
				}
			}

			// Clone/remove this bond?
			if (!remove_bond) {
				// if (!remove.contains(atoms[0]) && !remove.contains(atoms[1]))

				IBond clone = null;
				try {
					clone = (IBond) ac.getBond(i).clone();
				} catch (CloneNotSupportedException e) {
					logger.error("Could not clone: ", ac.getBond(i));
					logger.debug(e);
				}
				clone.setAtoms(new IAtom[]{map.get(atom0), map.get(atom1)});
				mol.addBond(clone);
			}
		}

		// Recompute hydrogen counts of neighbours of removed Hydrogens.
		for (Iterator<IAtom> i = remove.iterator();
				i.hasNext(); ) {
			// Process neighbours.
			for (Iterator n = ac.getConnectedAtomsList(i.next()).iterator();
					n.hasNext(); ) {
				final IAtom neighb = map.get(n.next());
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
	public List<IAtom> getHeavyAtoms() {
		List<IAtom> newAc = new ArrayList<IAtom>();
		IAtomContainer ac = getAtomContainer();
		for (int f = 0; f < ac.getAtomCount(); f++) {
			if (!ac.getAtom(f).getSymbol().equals(H_ELEMENT_SYMBOL)) {
				newAc.add(ac.getAtom(f));
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
	 * creates a sorted hash map of elementsymbol-count of this ac
	 * 
	 * @param ac the atomcontainer to calculate with
	 * @return the hashmap
	 */
	private Map<String, Integer> getSymolMap(IAtomContainer ac){
		String symbol;
		SortedMap<String,Integer> symbols = new TreeMap<String,Integer>();
		IAtom atom = null;
		HCount=0;
		for (int f = 0; f < ac.getAtomCount(); f++) {
			atom = ac.getAtom(f);
			symbol = atom.getSymbol();
			if(useboth){
				
			}
            if (atom.getHydrogenCount() != CDKConstants.UNSET && atom.getHydrogenCount() > 0) {
				HCount += atom.getHydrogenCount();
			}
			if (symbols.get(symbol) != null) {
				symbols.put(symbol, symbols.get(symbol) + 1);
			} else {
				symbols.put(symbol, 1);
			}
		}
		if(useboth && symbols.get(H_ELEMENT_SYMBOL)!=null)
				HCount+=symbols.get(H_ELEMENT_SYMBOL).intValue();
		return symbols;
	}
	
	/**
	 * Analyses a set of Nodes that has been changed or recently loaded
	 * and  returns a molecular formula
	 *
	 * @param  ac  Description of the Parameter
	 * @return     a string containing the molecular formula.
	 */
	public String analyseAtomContainer(IAtomContainer ac) {
		String mf = "";
		Map<String, Integer> symbols = this.getSymolMap(ac);
		mf = addSymbolToFormula(symbols, "C", mf);
		if(useboth){
			if (HCount > 0)
				mf += H_ELEMENT_SYMBOL;
			if (HCount > 1) {
				mf += Integer.toString(HCount);
			}
		}else{
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
		}
		mf = addSymbolToFormula(symbols, "N", mf);
		mf = addSymbolToFormula(symbols, "O", mf);
		mf = addSymbolToFormula(symbols, "S", mf);
		mf = addSymbolToFormula(symbols, "P", mf);
		Iterator<String> it = symbols.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			if (!((String) key).equals("C") && !((String) key).equals(H_ELEMENT_SYMBOL) && !((String) key).equals("N") && !((String) key).equals("O") && !((String) key).equals("S") && !((String) key).equals("P")) {
				mf = addSymbolToFormula(symbols, (String) key, mf);
			}
		}
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
	private String addSymbolToFormula(Map<String, Integer> sm, String symbol, String formula) {
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
			if (atomContainer.getAtom(f).getSymbol().equals(thisElement)) {
				atomCount++;
			}
		}
		return atomCount;
	}


	/**
	 * Returns a Vector (of Strings) with asorted element names.
	 * The order is determined by ElementComparator.
	 *
	 * @return    The elements value
	 * @see       ElementComparator
	 */
	public List<String> getElements() {
		TreeSet<String> elements = new TreeSet<String>((Comparator<? super String>)new ElementComparator());
		for (int f = 0; f < atomContainer.getAtomCount(); f++) {
			String symbol = atomContainer.getAtom(f).getSymbol();
			if (!elements.contains(symbol)) {
				elements.add(symbol);
			}
		}
		List<String> results = new ArrayList<String>();
		Iterator<String> iter = elements.iterator();
		while (iter.hasNext()) {
			results.add(iter.next());
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
	public static String generateElementFormula(IMolecule mol, String[] elements) {
		int num = elements.length;
		StringBuffer formula = new StringBuffer();
		int[] elementCount = new int[num];
		for (int i = 0; i < mol.getAtomCount(); i++) {
			for (int j = 0; j < num; j++) {
				if (elements[j].equals(mol.getAtom(i).getSymbol())) {
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
	public Map<String, Integer> getFormulaHashtable() {
		Map<String, Integer> formula = new HashMap<String, Integer>();
		List<String> elements = this.getElements();
		for (int i = 0; i < elements.size(); i++) {
			Integer numOfAtom = new Integer(this.getAtomCount(elements.get(i)));
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
		String formula = new MFAnalyser(atomContainer,useboth).getHTMLMolecularFormula();
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


