package org.openscience.cdk.formula;

import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * Class to manipulate IsotopePattern objects.
 * 
 * @author Miguel Rojas Cherto
 *
 * @cdk.module  formula
 */
@TestClass("org.openscience.cdk.formula.IsotopePatternManipulatorTest")
public class IsotopePatternManipulator {

	/**
	 * Return the isotope pattern normalized to the highest abundance
	 * 
	 * @param isotopeP  The IsotopePattern object to normalize
	 * @return          The IsotopePattern normalized
	 */
	@TestMethod("testNormalize_IsotopePattern")
	public static IsotopePattern normalize(IsotopePattern isotopeP){
		IsotopeContainer isoHighest = null;
		
		double biggestAbundance = 0;
		/*Extraction of the isoContainer with the highest abundance*/
		for(IsotopeContainer isoContainer: isotopeP.getIsotopes()){
			double abundance = isoContainer.getIntensity();
			if(biggestAbundance < abundance){
				biggestAbundance = abundance;
				isoHighest = isoContainer;
			}
		}
		/*Normalize*/
		IsotopePattern isoNormalized = new IsotopePattern();
		for(IsotopeContainer isoContainer: isotopeP.getIsotopes()){
			double inten = isoContainer.getIntensity()/isoHighest.getIntensity();
			IsotopeContainer icClone;
			try {
				icClone = (IsotopeContainer) isoContainer.clone();
				icClone.setIntensity(inten);
				if(isoHighest.equals(isoContainer))
					isoNormalized.setMonoIsotope(icClone);
				else
					isoNormalized.addIsotope(icClone);
					
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			
		}
		isoNormalized.setCharge(isotopeP.getCharge());
		return isoNormalized;
	}
	/**
	 * Return the isotope pattern sorted and normalized by intensity
	 * to the highest abundance.
	 * 
	 * @param isotopeP  The IsotopePattern object to sort
	 * @return          The IsotopePattern sorted
	 */
	@TestMethod("testSortAndNormalizedByIntensity_IsotopePattern")
	public static IsotopePattern sortAndNormalizedByIntensity(IsotopePattern isotopeP){
		IsotopePattern isoNorma = normalize(isotopeP);
		return sortByIntensity(isoNorma);
	}
	/**
	 * Return the isotope pattern sorted by intensity
	 * to the highest abundance.
	 * 
	 * @param isotopeP  The IsotopePattern object to sort
	 * @return          The IsotopePattern sorted
	 */
	@TestMethod("testSortByIntensity_IsotopePattern")
	public static IsotopePattern sortByIntensity(IsotopePattern isotopeP){
		try {
			IsotopePattern isoSort = new IsotopePattern();
			List<IsotopeContainer> listISO = ((IsotopePattern)isotopeP.clone()).getIsotopes();

			int length = listISO.size()-1;
			for(int i = length ; i >= 0 ; i--){
				double intensity = 0;
				IsotopeContainer isoHighest = null;
				for(IsotopeContainer isoContainer: listISO){
					if(isoContainer.getIntensity() > intensity){
						isoHighest = isoContainer;
						intensity = isoContainer.getIntensity();
					}
				}
				if(i == length)
					isoSort.setMonoIsotope((IsotopeContainer) isoHighest.clone());
				else
					isoSort.addIsotope((IsotopeContainer) isoHighest.clone());
				
				listISO.remove(isoHighest);
				
			}
			isoSort.setCharge(isotopeP.getCharge());
			return isoSort;
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Return the isotope pattern sorted by mass
	 * to the highest abundance.
	 * 
	 * @param isotopeP  The IsotopePattern object to sort
	 * @return          The IsotopePattern sorted
	 */
	@TestMethod("testSortByMass_IsotopePattern")
	public static IsotopePattern sortByMass(IsotopePattern isotopeP){
		try {
			IsotopePattern isoSort = new IsotopePattern();
			List<IsotopeContainer> listISO = ((IsotopePattern)isotopeP.clone()).getIsotopes();

			int length = listISO.size()-1;
			for(int i = length ; i >= 0 ; i--){
				double mass = 100000;
				IsotopeContainer isoHighest = null;
				for(IsotopeContainer isoContainer: listISO){
					if(isoContainer.getMass() < mass){
						isoHighest = isoContainer;
						mass = isoContainer.getMass();
					}
				}
				if(i == length)
					isoSort.setMonoIsotope((IsotopeContainer) isoHighest.clone());
				else
					isoSort.addIsotope((IsotopeContainer) isoHighest.clone());
				
				listISO.remove(isoHighest);
				
			}
			isoSort.setCharge(isotopeP.getCharge());
			return isoSort;
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
