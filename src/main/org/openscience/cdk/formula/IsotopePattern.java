package org.openscience.cdk.formula;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;



/**
 * This class defines the properties of a deisotoped
 * pattern distribution. A isotope pattern is a set of 
 * compounds with different set of isotopes.
 * 
 * @author Miguel Rojas Cherto
 * 
 * @cdk.module formula
 */
@TestClass("org.openscience.cdk.formula.IsotopePatternTest")
public class IsotopePattern {

	private List<IsotopeContainer> isotopeCList = new ArrayList<IsotopeContainer>();

	private int monoIsotopePosition;

	private double chargI=0;
	
	/**
	 * Constructor of the IsotopePattern object.
	 */
	public IsotopePattern(){
		
	}
	/**
	 * Set the mono isotope object.
	 * 
	 *  @param isoContainer   The IsotopeContainer object
	 */
	@TestMethod("testSetMonoIsotope_IsotopeContainer")
	public void setMonoIsotope(IsotopeContainer isoContainer){
		isotopeCList.add(isoContainer);
		monoIsotopePosition = isotopeCList.indexOf(isoContainer);
	}
	/**
	 * Add an isotope object.
	 * 
	 *  @param isoContainer   The IsotopeContainer object
	 */
	@TestMethod("testAddIsotope_IsotopeContainer")
	public void addIsotope(IsotopeContainer isoContainer){
		isotopeCList.add(isoContainer);
	}
	/**
     * Returns the mono-isotope peak that form this isotope pattern.
     * 
     * @return The IsotopeContainer acting as mono-isotope
     */
	@TestMethod("testGetMonoIsotope")
    public IsotopeContainer getMonoIsotope(){
    	return isotopeCList.get(monoIsotopePosition);
    }
    
    /**
     * Returns the all isotopes that form this isotope pattern.
     * 
     * @return The IsotopeContainer acting as mono-isotope
     */
	@TestMethod("testGetIsotopes")
    public List<IsotopeContainer > getIsotopes(){
    	return isotopeCList;
    }
    /**
     * Returns the a isotopes given a specific position.
     * 
     * @param  The position
     * @return The isotope
     */
	@TestMethod("testGetIsotope_int")
    public IsotopeContainer getIsotope(int position){
    	return isotopeCList.get(position);
    }
    /**
     * Returns the number of isotopes in this pattern.
     * 
     * @return The number of isotopes
     */
	@TestMethod("testGetNumberOfIsotopes")
    public int getNumberOfIsotopes(){
    	return isotopeCList.size();
    }

    /**
     * Set the charge in this pattern.
     * 
     * @param charge The charge value
     */
	@TestMethod("testSetCharge_double")
	public void setCharge(double charge) {
		chargI = charge;
		
	}

    /**
     * Get the charge in this pattern.
     * 
     * @return The charge value
     */
	@TestMethod("testGetCharge")
	public double getCharge() {
		return chargI;
		
	}
    /**
	 * Clones this IsotopePattern object and its content. 
	 *
	 * @return    The cloned object
	 */
	@TestMethod("testClone")
	public Object clone() throws CloneNotSupportedException {
    	IsotopePattern isoClone = new IsotopePattern();
    	IsotopeContainer isoHighest = getMonoIsotope();
    	for(IsotopeContainer isoContainer: isotopeCList){
    		if(isoHighest.equals(isoContainer))
    			isoClone.setMonoIsotope((IsotopeContainer) isoContainer.clone());
    		else
    			isoClone.addIsotope((IsotopeContainer) isoContainer.clone());
    	}
    	isoClone.setCharge(getCharge());
    	return isoClone;
    }
}
