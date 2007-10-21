package org.openscience.cdk.modeling.forcefield;

//import org.openscience.cdk.tools.LoggingTool;


/**			
 *  Cutoffs and Smoothing Functions.
 *
 *@author     vlabarta
 *@cdk.created    February 28, 2005
 *@cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 */
public class SmoothingFunctions {
	double[] s = null;	// Smoothing function
	double cutoffr0 = 5;	// For Smoothing function (s)
	double cutoffr1 = 6;	// For Smoothing function (s)
	double dampingFactor = 1;	// For Smoothing function (s)
	//private LoggingTool logger;


	/**
	 *  Constructor for the SmoothingFunctions object
	 */
	public SmoothingFunctions() {        
		//logger = new LoggingTool(this);
	}


	/**
	 *  Calculate the smoothing function from atom distances.
	 *
	 *
	 *@param  atomDistances       3d distance between the atoms.
	 */
	public void setSmoothingFunction(double[] atomDistances) {
		
		s = new double[atomDistances.length];

		for (int i=0; i<atomDistances.length; i++) {

			if (atomDistances[i] < cutoffr0) {
				s[i] = 1;
			} else if (atomDistances[i] > cutoffr1) {
				s[i] = 0;
			} else {
				s[i] = 1 - dampingFactor * ((atomDistances[i] - cutoffr0) / (cutoffr1 - cutoffr0));
			}
			//logger.debug("s[" + i + "] = " + s[i]);
		}
	}


	/**
	 *  Get the smoothing function value for every atom distance.
	 *
	 *
	 *@return	smoothing function value for every atom distance.
	 */
	public double[] getSmoothingFunction() {
		return s;
	}	

}
