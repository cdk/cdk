package org.openscience.cdk.modeling.builder3d;

import java.util.List;
import java.util.Map;

/**
 * Set the right atoms order to get the parameters.
 *
 * @author         chhoppe
 * @cdk.created    2004-10-8
 * @cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 */
public class MMFF94ParametersCall {
	
	private Map<String,List> pSet = null;
	//private final static double DEFAULT_BOND_LENGTH = 1.5;
	//private final static double DEFAULT_ANGLE = 90;			// Only to test
	//private final static double DEFAULT_TORSION_ANGLE = 90;

	
	public MMFF94ParametersCall(){}


	/**
	 * Initialize the AtomOrder class.
	 * 
	 * @param  parameterSet  Force Field parameter as Map
	 */
	public void initialize(Map<String,List> parameterSet) {
		pSet = parameterSet;
	}


	/**
	 *  Gets the bond parameter set.
	 *
	 * @param  id1            atom1 id
	 * @param  id2            atom2 id
	 * @return                The distance value from the force field parameter set
	 * @exception  Exception  Description of the Exception
	 */
	public List getBondData(String code, String id1, String id2) throws Exception {
		String dkey = "";
		if (pSet.containsKey(("bond" + code + ";" + id1 + ";" + id2))) {
			dkey="bond" + code + ";" + id1 + ";" + id2;
		}else if (pSet.containsKey(("bond" + code + ";" + id2 + ";" + id1))) {
			dkey = "bond" + code + ";" + id2 + ";" + id1;
		} /*else {
			System.out.println("KEYError:Unknown distance key in pSet: " + code + ";" + id2 + " ;" + id1+" take default bon length:" + DEFAULT_BOND_LENGTH);
			return DEFAULT_BOND_LENGTH;
			}*/
		//logger.debug("dkey = " + dkey);
		return (List) pSet.get(dkey);
	}


	/**
	 *  Gets the angle parameter set.
	 *
	 * @param  id1            ID from Atom 1.
	 * @param  id2            ID from Atom 2.
	 * @param  id3            ID from Atom 3.
	 * @return                The angle data from the force field parameter set
	 * @exception  Exception  Description of the Exception
	 */
	public List getAngleData(String angleType, String id1, String id2, String id3) throws Exception {
		String akey = "";
		if (pSet.containsKey(("angle" + angleType + ";" + id1 + ";" + id2 + ";" + id3))) {
			akey = "angle" + angleType + ";" + id1 + ";" + id2 + ";" + id3;
		} else if (pSet.containsKey(("angle" + angleType + ";" + id3 + ";" + id2 + ";" + id1))) {
			akey = "angle" + angleType + ";" + id3 + ";" + id2 + ";" + id1;
		} /*else {
			System.out.println("KEYErrorAngle:Unknown angle key in pSet: " + angleType + ";" + id1 + " ; " + id2 + " ; " + id3 +" take default angle:" + DEFAULT_ANGLE);
	   		return (Vector)[DEFAULT_ANGLE,0,0];
	   		}*/
		//logger.debug("angle key : " + akey);
		return (List) pSet.get(akey);
	}


	/**
	 *  Gets the bond-angle interaction parameter set.
	 *
	 * @param  id1            ID from Atom 1.
	 * @param  id2            ID from Atom 2.
	 * @param  id3            ID from Atom 3.
	 * @return                The bond-angle interaction data from the force field parameter set
	 * @exception  Exception  Description of the Exception
	 */
	public List getBondAngleInteractionData(String strbndType, String id1, String id2, String id3) throws Exception {
		String akey = "";
		if (pSet.containsKey(("strbnd" + strbndType + ";" + id1 + ";" + id2 + ";" + id3))) {
			akey = "strbnd" + strbndType + ";" + id1 + ";" + id2 + ";" + id3;
		} else if (pSet.containsKey(("strbnd" + strbndType + ";" + id1 + ";" + id3 + ";" + id2))) {
			akey = "strbnd" + strbndType + ";" + id1 + ";" + id3 + ";" + id2;
		} else if (pSet.containsKey(("strbnd" + strbndType + ";" + id2 + ";" + id1 + ";" + id3))) {
			akey = "strbnd" + strbndType + ";" + id2 + ";" + id1 + ";" + id3;
		} else if (pSet.containsKey(("strbnd" + strbndType + ";" + id2 + ";" + id3 + ";" + id1))) {
			akey = "strbnd" + strbndType + ";" + id2 + ";" + id3 + ";" + id1;
		} else if (pSet.containsKey(("strbnd" + strbndType + ";" + id3 + ";" + id1 + ";" + id2))) {
			akey = "strbnd" + strbndType + ";" + id3 + ";" + id1 + ";" + id2;
		} else if (pSet.containsKey(("strbnd" + strbndType + ";" + id3 + ";" + id2 + ";" + id1))) {
			akey = "strbnd" + strbndType + ";" + id3 + ";" + id2 + ";" + id1;
		} /*else {
			System.out.println("KEYErrorAngle:Unknown angle key in pSet: " +id1 + " ; " + id2 + " ; " + id3+" take default angle:" + DEFAULT_ANGLE);
			return (Vector)[DEFAULT_ANGLE,0,0];
			}*/
		//logger.debug("akey : " + akey);
		return (List) pSet.get(akey);
	}
	

	/**
	 * Gets the bond-angle interaction parameter set.
	 *
	 * @param  iR             ID from Atom 1.
	 * @param  jR             ID from Atom 2.
	 * @param  kR             ID from Atom 3.
	 * @return                The bond-angle interaction data from the force field parameter set
	 * @exception  Exception  Description of the Exception
	 */
	public List getDefaultStretchBendData(int iR, int jR, int kR) throws Exception {
		String dfsbkey = "";
		if (pSet.containsKey(("DFSB" + iR + ";" + jR + ";" + kR))) {
			dfsbkey = "DFSB" + iR + ";" + jR + ";" + kR;
		}  /*else {
			System.out.println("KEYErrorDefaultStretchBend:Unknown default stretch-bend key in pSet: " + iR + " ; " + jR + " ; " + kR);
			}*/
		//logger.debug("dfsbkey : " + dfsbkey);
		return (List) pSet.get(dfsbkey);
	}
	
		
	/**
	 *  Gets the bond parameter set.
	 *
	 * @param  id1            atom1 id
	 * @param  id2            atom2 id
	 * @return                The distance value from the force field parameter set
	 * @exception  Exception  Description of the Exception
	 */
	public List getTorsionData(String code, String id1, String id2, String id3, String id4) throws Exception {
		String dkey = "";
		if (pSet.containsKey(("torsion" + code + ";" + id1 + ";" + id2 + ";" + id3 + ";" + id4))) {
			dkey="torsion" + code + ";" + id1 + ";" + id2 + ";" + id3 + ";" + id4;
		}else if (pSet.containsKey(("torsion" + code + ";" + id4 + ";" + id3 + ";" + id2 + ";" + id1))) {
			dkey = "torsion" + code + ";" + id4 + ";" + id3 + ";" + id2 + ";" + id1;
		}else if (pSet.containsKey(("torsion" + code + ";*;" + id2 + ";" + id3 + ";*"))) {
			dkey="torsion" + code + ";*;" + id2 + ";" + id3 + ";*";
		}else if (pSet.containsKey(("torsion" + code + ";*;" + id3 + ";" + id2 + ";*"))) {
			dkey = "torsion" + code + ";*;" + id3 + ";" + id2 + ";*";
		}else if (pSet.containsKey(("torsion" + 0 + ";*;" + id2 + ";" + id3 + ";*"))) {
			dkey = "torsion" + 0 + ";*;" + id2 + ";" + id3 + ";*";
		}else if (pSet.containsKey(("torsion" + 0 + ";*;" + id3 + ";" + id2 + ";*"))) {
			dkey = "torsion" + 0 + ";*;" + id3 + ";" + id2 + ";*";
		} /*else {
			System.out.println("KEYError:Unknown distance key in pSet: torsion" + code + ";" + id1 + ";" + id2 + ";" + id3 + ";" + id4 + " take default torsion angle:" + DEFAULT_TORSION_ANGLES);
			return DEFAULT_TORSION_ANGLE;
			}*/
		//logger.debug("dkey = " + dkey);
		return (List) pSet.get(dkey);
	}
	
}


