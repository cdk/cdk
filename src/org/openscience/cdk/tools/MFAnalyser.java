/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.tools;

import org.openscience.cdk.*;
import java.util.*;

/** 
 * Analyses a molecular formula given in String format and builds 
 * an AtomContainer with the Atoms in the molecular formula.
 *
 * @cdkPackage standard
 *
 * @keyword molecule, molecular mass
 * @keyword molecule, molecular formula
 */
public class MFAnalyser{
	
	private String MF;
	private AtomContainer atomContainer;
	private int HCount = 0;

	/** Construct an instance of MFAnalyser, initialized with a molecular 
	  * formula string. The string is immediatly analysed and a set of Nodes
	  * is built based on this analysis
	  */
	public MFAnalyser(String MF){
		this.MF = MF;
		this.atomContainer = analyseMF(MF);
	}	
	
	/** Construct an instance of MFAnalyser, initialized with a set of Nodes
	  * The set is analysed and a molecular formular is constructed
	  *  based on this analysis
	  */
	public MFAnalyser(AtomContainer ac){
		this.atomContainer = ac;
		this.MF = analyseAtomContainer(ac);
	}	

	/** returns the complete set of Nodes, as implied by the molecular
	 * formula, including all the hydrogens.
	 */
	public AtomContainer getAtomContainer(){
		return atomContainer;		
	}
	
	/**
     * Returns the complete set of Nodes, as implied by the molecular
	 * formula, including all the hydrogens.
     *
     * @see #getHTMLMolecularFormula()
     */
	public String getMolecularFormula(){
		return MF;		
	}
  
  /**
   * Returns the string representation of the molecule formula with
   * numbers wrapped in &lt;sub&gt;&lt;/sub&gt; tags. Useful for displaying
   * formulae in Swing components or on the web.
   *
   * @return A HTML representation of the molecular formula.
   */
   public String getHTMLMolecularFormula(){
     boolean lastCharacterWasDigit = false;
     boolean currentCharacterIsDigit;
     StringBuffer htmlString = new StringBuffer (MF);
     
     for (int characterCounter = 0; characterCounter <= htmlString.length(); characterCounter++){
       try{
         currentCharacterIsDigit = Character.isDigit(htmlString.charAt(characterCounter));
       }
       catch (StringIndexOutOfBoundsException oobe){
         currentCharacterIsDigit = false;
       }
       
       if (currentCharacterIsDigit && !lastCharacterWasDigit){
         //Insert an opening sub and move the counter beyond it
         htmlString.insert (characterCounter, "<sub>");
         characterCounter += 5;
       }
       else if (lastCharacterWasDigit && !currentCharacterIsDigit){
         //Insert a closing sub and move the counter beyond it
         htmlString.insert (characterCounter, "</sub>");
         characterCounter += 6;
       }
       
       lastCharacterWasDigit = currentCharacterIsDigit;
     }
     
     return htmlString.toString();
   }

   /**
     * returns the exact mass for a given molecular formula
     **/
    public float getMass()
    {
        float mass = 0;
        Isotope i;
        StandardIsotopes si = new StandardIsotopes();
        AtomContainer ac = getAtomContainer();
        Isotope h= si.getMajorIsotope("H");
        for(int f = 0; f < ac.getAtomCount();f++)
        {
            i = si.getMajorIsotope(ac.getAtomAt(f).getSymbol());
            if(i != null)
            {
                mass += i.exactMass;
            }
            else
            {
                return 0;
            }
            mass += ac.getAtomAt(f).getHydrogenCount()*h.exactMass;
        }
        return mass;
    }

		
  /**
   * Produces an AtomContainer without explicit Hs but with H count from one with Hs. The new molecule is a deep copy.
   *
   * @return                The mol without Hs.
   */
  public AtomContainer removeHydrogens(){
    AtomContainer ac = getAtomContainer();
    Molecule mol = new Molecule();
    Map map=new HashMap();
    for (int i = 0; i < ac.getAtomCount(); i++) {
      Atom atom = ac.getAtomAt(i);
      if (!atom.getSymbol().equals("H")) {
        Atom a = (Atom)atom.clone();
        a.setHydrogenCount(0);
        mol.addAtom(a);
        map.put(ac.getAtomAt(i),a);
      }
    }
    for (int i = 0; i < ac.getBondCount(); i++) {
      Atom[] atoms = ac.getBondAt(i).getAtoms();
      boolean isH = false;
      for (int k = 0; k < atoms.length; k++) {
        if (atoms[k].getSymbol().equals("H")) {
          isH = true;
          break;
        }
      }
      if (!isH) {
        Bond bond = (Bond)ac.getBondAt(i).clone();
        bond.setAtoms(new Atom[]{(Atom)map.get(atoms[0]),(Atom)map.get(atoms[1])});
        mol.addBond(bond);
      }
    }
    for (int i = 0; i < ac.getAtomCount(); i++) {
      if(ac.getAtomAt(i).getSymbol().equals("H"))
        ((Atom)map.get(ac.getConnectedAtoms(ac.getAtomAt(i))[0])).setHydrogenCount(((Atom)map.get(ac.getConnectedAtoms(ac.getAtomAt(i))[0])).getHydrogenCount()+1);
    }
    return (mol);
  }


  	/** 
     * Returns a set of nodes excluding all the hydrogens
     *
     * @keyword hydrogen, removal
     */
	public List getHeavyAtoms()
	{
		ArrayList newAc = new ArrayList();
		AtomContainer ac = getAtomContainer();
		for (int f = 0; f < ac.getAtomCount(); f++){
			if (!ac.getAtomAt(f).getSymbol().equals("H"))
			{
				newAc.add(ac.getAtomAt(f));
			}
		}	
		return newAc;
	}	
	
	/** Method that actually does the work of analysing the molecular formula */
    private AtomContainer analyseMF(String MF){
	AtomContainer ac = new AtomContainer();
        
        char ThisChar; /* Buffer for */
        String RecentElementSymbol = new String();
        String RecentElementCountString = new String("0"); /* String to be converted to an integer */
        int RecentElementCount;
        
        if (MF.length() == 0)
            return null;

        for (int f = 0; f < MF.length(); f ++){
            ThisChar = MF.charAt(f);
            if (f < MF.length()){
                if (ThisChar >= 'A' && ThisChar <= 'Z'){ /* New Element begins */            
                    RecentElementSymbol = java.lang.String.valueOf(ThisChar);
                    RecentElementCountString = "0";
                }
                if (ThisChar >= 'a' && ThisChar<= 'z'){ /* Two-letter Element continued */            
                    RecentElementSymbol += ThisChar;                
                }
                if (ThisChar >= '0' && ThisChar<= '9'){ /* Two-letter Element continued */            
                        RecentElementCountString += ThisChar;                   
                }
            }
            if (f == MF.length() - 1 || (MF.charAt(f + 1) >= 'A' && MF.charAt(f + 1 ) <= 'Z')){
                /* Here an element symbol as well as its number should have been read completely */                    
                Integer RecentElementCountInteger = new Integer(RecentElementCountString);
                RecentElementCount = RecentElementCountInteger.intValue();
                if (RecentElementCount == 0){
                    RecentElementCount = 1;
            	}
            	for (int g = 0; g < RecentElementCount; g++){
            		ac.addAtom(new Atom(RecentElementSymbol));
            	}
            }
        }
        return ac;
    }
    
    
	 /** 
	 * Analyses a set of Nodes that has been changed or recently loaded
	 * and  returns a molecular formula
	 *
	 * @return  a string containing the molecular formula.    
	 */
	public String analyseAtomContainer(AtomContainer ac)
	{
		String symbol, mf = "";
		SortedMap symbols=new TreeMap();
		int HCount = 0;
		int numberOfElements = 0;
		Atom atom = null;
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			atom = ac.getAtomAt(f);
			symbol = atom.getSymbol();	
			if (atom.getHydrogenCount() > 0) HCount += atom.getHydrogenCount();		
			if(symbols.get(symbol)!=null)
				symbols.put(symbol,new Integer(((Integer)symbols.get(symbol)).intValue()+1));
			else
				symbols.put(symbol,new Integer(1));
		}
		mf = addSymbolToFormula(symbols, "C", mf);
    if(symbols.get("H")!=null){
      mf = addSymbolToFormula(symbols, "H", mf);
    }else{
      if (HCount > 0){
        mf += "H";
        if (HCount > 1) mf += new Integer(HCount).toString();
      }
		}
		mf = addSymbolToFormula(symbols, "N", mf);
		mf = addSymbolToFormula(symbols, "O", mf);
		mf = addSymbolToFormula(symbols, "S", mf);
		mf = addSymbolToFormula(symbols, "P", mf);
		Iterator it = symbols.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			if(!((String)key).equals("C")&&!((String)key).equals("H")&&!((String)key).equals("N")&&!((String)key).equals("O")&&!((String)key).equals("S")&&!((String)key).equals("P")){
				mf=addSymbolToFormula(symbols, (String)key, mf);
			}
		}
		this.HCount = HCount;
		return mf;
	}
    
 	/**
	 * Adds an element to a chemical formual string
	 *
	 * @param   sm         The map containing the elements
	 * @param   symbol     The symbol to add
	 * @param   formula    The chemical formula
	 */
	private String addSymbolToFormula(SortedMap sm, String symbol, String formula){
		if(sm.get(symbol)!=null){
			formula += symbol;
			if (!sm.get(symbol).equals(new Integer(1)))
				formula += sm.get(symbol).toString();
		}
		return(formula);
	}


 	/**
	 * Checks a set of Nodes for the occurence of a particular
	 * element.
	 *
	 * @return  The number of atoms for the particular element in the formula
	 */
	public int getAtomCount(String thisElement){
		int atomCount = 0;
		if (thisElement.equals("H") &&  HCount > 0) return HCount;
		for (int f = 0; f < atomContainer.getAtomCount(); f++){
			if (atomContainer.getAtomAt(f).getSymbol().equals(thisElement))
			{
 				atomCount++;
			}
		}
		return atomCount;
	}
    
    /**
     * Returns a Vector with asorted element names.
     * The order is determined by ElementComparator.
     *
     * @see ElementComparator
     */
    public Vector getElements() {
        TreeSet elements = new TreeSet(new ElementComparator());
        for (int f = 0; f < atomContainer.getAtomCount(); f++){
            String symbol = atomContainer.getAtomAt(f).getSymbol();
            if (!elements.contains(symbol)) {
                elements.add(symbol);
            }
        }
        Vector results = new Vector();
        Iterator iter = elements.iterator();
        while (iter.hasNext()) {
            results.add((String)iter.next());
        }
        return results;
    }

    /**
     * Returns the number of distinct elements in the formula.
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
     * @return           The element formula as a string
     */
    public static String generateElementFormula(Molecule mol, String[] elements) {
        int num = elements.length;
        StringBuffer formula = new StringBuffer();
        int[] elementCount = new int[num];
        for (int i = 0; i < mol.getAtomCount(); i++) {
            String symbol = mol.getAtomAt(i).getSymbol();
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
	 *
	 * @return    a Hashtable, keys are the elemental symbols (Strings) and values are the no. of occurrence (Integer objects).
	 */
	public Hashtable getFormulaHashtable() {
		Hashtable formula = new Hashtable();
		Vector elements = this.getElements();
		for (int i = 0; i < elements.size(); i++) {
			Integer numOfAtom = new Integer(atomContainer.getAtomCount());
			formula.put(elements.get(i), numOfAtom);
		}
		return formula;
	}
    
}


