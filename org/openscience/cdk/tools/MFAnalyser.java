/* 
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
import java.util.Vector;

/** 
  * Analyses a molecular formula given in String format and builds 
  * an AtomContainer with the Atoms in the molecular formula.
  */
public class MFAnalyser{
	
	private String MF;
	private AtomContainer atomContainer;
	private int HCount = 0;
	private boolean verbose = false;
	
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
	  * formula, inlcuding all the hydrogens.
	  */
	public AtomContainer getAtomContainer(){
		return atomContainer;		
	}
	
	/** returns the complete set of Nodes, as implied by the molecular
	  * formula, inlcuding all the hydrogens.
	  */
	public String getMolecularFormula(){
		return MF;		
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
        for(int f = 0; f < ac.getAtomCount();f++)
        {
            i = si.getMajorIsotope(ac.getAtomAt(f).getElement().getSymbol());
            if(i != null)
            {
                mass += i.exactMass;
            }
            else
            {
                return 0;
            }
        }
        return mass;
    }

		
	/** Returns a set of nodes excluding all the hydrogens*/
	public AtomContainer getHeavyAtoms()
	{
		AtomContainer newAc = new AtomContainer();
		AtomContainer ac = getAtomContainer();
		for (int f = 0; f < ac.getAtomCount(); f++){
			if (!ac.getAtomAt(f).getElement().getSymbol().equals("H"))
			{
				newAc.addAtom(ac.getAtomAt(f));
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
            		ac.addAtom(new Atom(new Element(RecentElementSymbol)));
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
		String[] symbols;
		int[] elementCount;
		int HCount = 0;
		int numberOfElements = 0;
		boolean done;
		Atom atom = null;
		symbols = new String[ac.getAtomCount()];
		elementCount = new int[ac.getAtomCount()];
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			atom = ac.getAtomAt(f);
			symbol = atom.getElement().getSymbol();	
			if (atom.getHydrogenCount() > 0) HCount += atom.getHydrogenCount();		
			done = false;
			for (int g = 0; g < numberOfElements ; g++){
				if (symbols[g].equals(symbol)){
					elementCount[g]++;
					done = true;
					break;	
				}	
			}
			if (!done){
				symbols[numberOfElements] = symbol;
				elementCount[numberOfElements]++;
				numberOfElements++;
			}	
		}
		for (int g = 0; g < numberOfElements ; g++){
			mf += symbols[g];
			if (elementCount[g] > 1) mf += new Integer(elementCount[g]).toString();
			if (g == 0 && HCount > 0){
				mf += "H";
				if (HCount > 1) mf += new Integer(HCount).toString();
			} 

		}
		this.HCount = HCount;
		return mf;
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
			if (atomContainer.getAtomAt(f).getElement().getSymbol().equals(thisElement))
			{
 				atomCount++;
			}
		}
		return atomCount;
	}

 	/**
	 * If true the class talks when doing its work. 
	 *
	 */
	public void setVerbose(boolean verbose)
	{
		this.verbose = verbose;
	}
}
