package org.openscience.cdk.modeling.builder3d;

import java.util.Hashtable;
import java.util.Vector;


/**
 * Set the right atoms order to get the parameters.
 *
 * @author         chhoppe
 * @cdk.created    2004-10-8
 * @cdk.module     builder3d
 */
public class MMFF94ParametersCall {
	
	private Hashtable pSet = null;
	private final static double DEFAULT_BOND_LENGTH = 1.5;
	private final static double DEFAULT_ANGLE = 90;			// Only to test


	public MMFF94ParametersCall(){}


	/**
	 *  Initialize the AtomOrder class
	 * 
	 * @param  parameterSet  Force Field parameter as Hashtable
	 */
	public void initialize(Hashtable parameterSet) {
		pSet = parameterSet;
	}


	/**
	 *  Gets the bond parameter set
	 *
	 * @param  id1            atom1 id
	 * @param  id2            atom2 id
	 * @return                The distance value from the force field parameter set
	 * @exception  Exception  Description of the Exception
	 */
	public Vector getBondData(String id1, String id2) throws Exception {
		String dkey = "";
		if (pSet.containsKey(("bond" + id1 + ";" + id2))) {
			dkey="bond" + id1 + ";" + id2;
		}else if (pSet.containsKey(("bond" + id2 + ";" + id1))) {
			dkey = "bond" + id2 + ";" + id1;
		} /*else {
			System.out.println("KEYError:Unknown distance key in pSet: " + id2 + " ;" + id1+" take default bon length:"+DEFAULT_BOND_LENGTH);
			return DEFAULT_BOND_LENGTH;
		}*/
		return (Vector) pSet.get(dkey);
	}


	/**
	 *  Gets the angle parameter set
	 *
	 * @param  id1            ID from Atom 1.
	 * @param  id2            ID from Atom 2.
	 * @param  id3            ID from Atom 3.
	 * @return                The angle data from the force field parameter set
	 * @exception  Exception  Description of the Exception
	 */
	public Vector getAngleData(String id1, String id2, String id3) throws Exception {
		String akey = "";
		if (pSet.containsKey(("angle" + id1 + ";" + id2 + ";" + id3))) {
			akey = "angle" + id1 + ";" + id2 + ";" + id3;
		} else if (pSet.containsKey(("angle" + id3 + ";" + id2 + ";" + id1))) {
			akey = "angle" + id3 + ";" + id2 + ";" + id1;
		} /*else {
			System.out.println("KEYErrorAngle:Unknown angle key in pSet: " +id2 + " ; " + id3 + " ; " + id1+" take default angle:"+DEFAULT_ANGLE);
	   		return (Vector)[DEFAULT_ANGLE,0,0];
		}*/
		return (Vector) pSet.get(akey);
	}


	/**
	 *  Gets the bond-angle interaction parameter set
	 *
	 * @param  id1            ID from Atom 1.
	 * @param  id2            ID from Atom 2.
	 * @param  id3            ID from Atom 3.
	 * @return                The bond-angle interaction data from the force field parameter set
	 * @exception  Exception  Description of the Exception
	 */
	public Vector getBondAngleInteractionData(String id1, String id2, String id3) throws Exception {
		String akey = "";
		if (pSet.containsKey(("strbnd" + id1 + ";" + id2 + ";" + id3))) {
			akey = "strbnd" + id1 + ";" + id2 + ";" + id3;
		} else if (pSet.containsKey(("strbnd" + id3 + ";" + id2 + ";" + id1))) {
			akey = "strbnd" + id3 + ";" + id2 + ";" + id1;
		} /*else {
			System.out.println("KEYErrorAngle:Unknown angle key in pSet: " +id2 + " ; " + id3 + " ; " + id1+" take default angle:"+DEFAULT_ANGLE);
			return (Vector)[DEFAULT_ANGLE,0,0];
		}*/
		//System.out.println("akey : " + akey);
		return (Vector) pSet.get(akey);
	}
}


