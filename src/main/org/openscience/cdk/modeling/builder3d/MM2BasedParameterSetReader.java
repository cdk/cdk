/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
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
package org.openscience.cdk.modeling.builder3d;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.openscience.cdk.AtomType;
import org.openscience.cdk.interfaces.IAtomType;

/**
 * AtomType list configurator that uses the ParameterSet originally
 * defined in mm2.prm from tinker. This class was added to be able to port
 * mm2 to CDK.
 *
 * @author         chhoppe
 * @cdk.created    2004-09-07
 * @cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 * @cdk.keyword    atom type, MM2
 */
public class MM2BasedParameterSetReader {

//	private final LoggingTool logger = new LoggingTool(MM2BasedParameterSetReader.class);
	
	private String configFile = "org/openscience/cdk/modeling/forcefield/data/mm2.prm";
	private InputStream ins = null;
	private Map<String, Object> parameterSet;
	private List<IAtomType> atomTypes;
	private StringTokenizer st;
	private String key = "";

	/**
	 * Constructor for the MM2BasedParameterSetReader object.
	 */
	public MM2BasedParameterSetReader() {
		parameterSet = new Hashtable<String,Object>();
		atomTypes = new Vector<IAtomType>();
	}

	public Map<String,Object> getParamterSet(){
		return parameterSet;
	}
	
	public List<IAtomType> getAtomTypes(){
		return atomTypes;
	}
	/**
	 * Sets the file containing the config data.
	 *
	 * @param  ins  The new inputStream type InputStream
	 */
	public void setInputStream(InputStream ins) {
		this.ins = ins;
	}

	/**
	 * Read a text based configuration file out of the force field mm2 file
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setForceFieldDefinitions() throws Exception {
		String sid = st.nextToken();
		String svalue = st.nextToken();
		if (sid.equals(">bontunit")) {
			try {
				double value1 = new Double(svalue).doubleValue();
				key = sid.substring(1);
				parameterSet.put(key, new Double(value1));
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals(">bond-cubic")) {
			try {
				double value1 = new Double(svalue).doubleValue();
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, new Double(value1));
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals(">bond-quartic")) {
			try {
				double value1 = new Double(svalue).doubleValue();
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, new Double(value1));
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals(">angleunit")) {
			try {
				double value1 = new Double(svalue).doubleValue();
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, new Double(value1));
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals(">angle-sextic")) {
			try {
				double value1 = new Double(svalue).doubleValue();
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, new Double(value1));
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals(">strbndunit")) {
			try {
				double value1 = new Double(svalue).doubleValue();
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, new Double(value1));
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals(">opbendunit")) {
			try {
				double value1 = new Double(svalue).doubleValue();
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, new Double(value1));
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals(">torsionunit")) {
			try {
				double value1 = new Double(svalue).doubleValue();
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, new Double(value1));
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals(">vdwtype")) {
			key = sid.substring(1);
			//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
			parameterSet.put(key, svalue);
		} else if (sid.equals(">radiusrule")) {
			key = sid.substring(1);
			//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
			parameterSet.put(key, svalue);
		} else if (sid.equals(">radiustype")) {
			key = sid.substring(1);
			//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
			parameterSet.put(key, svalue);
		} else if (sid.equals(">radiussize")) {
			key = sid.substring(1);
			//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
			parameterSet.put(key, svalue);
		} else if (sid.equals(">epsilonrule")) {
			key = sid.substring(1);
			//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
			parameterSet.put(key, svalue);
		} else if (sid.equals(">a-expterm")) {
			try {
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, svalue);
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals("b-expterm")) {
			try {
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, svalue);
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals(">c-expterm")) {
			try {
				double value1 = new Double(svalue).doubleValue();
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, new Double(value1));
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals(">vdw-14-scale")) {
			try {
				double value1 = new Double(svalue).doubleValue();
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, new Double(value1));
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals(">chg-14-scale")) {
			try {
				double value1 = new Double(svalue).doubleValue();
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, new Double(value1));
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else if (sid.equals(">dielectric")) {
			try {
				double value1 = new Double(svalue).doubleValue();
				key = sid.substring(1);
				//if (parameterSet.containsKey(key)){logger.debug("KeyError: hasKey "+key);}
				parameterSet.put(key, new Double(value1));
			} catch (NumberFormatException nfe) {
				throw new IOException("VdWaalsTable.ReadvdWaals: " +
						"Malformed Number");
			}
		} else {
		}
	}

	/**
	 *  Read and stores the atom types in a vector
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setAtomTypes() throws Exception {
		String name = "";
		String rootType = "";
		int an = 0;
		int rl = 255;
		int gl = 20;
		int bl = 147;
		int maxbond = 0;

		double mass = 0.0;
		st.nextToken();
		String sid = st.nextToken();
		rootType = st.nextToken();
		name = st.nextToken();
		String san = st.nextToken();
		String sam = st.nextToken();
		String smaxbond = st.nextToken();

		try {
			mass = new Double(sam).doubleValue();
			an = Integer.parseInt(san);
			maxbond = Integer.parseInt(smaxbond);

		} catch (NumberFormatException nfe) {
			throw new IOException("AtomTypeTable.ReadAtypes: " +
					"Malformed Number");
		}

		IAtomType atomType = new AtomType(name, rootType);
		atomType.setAtomicNumber(an);
		atomType.setExactMass(mass);
		atomType.setFormalNeighbourCount(maxbond);
		atomType.setSymbol(rootType);
		Color co = new Color(rl, gl, bl);
		atomType.setProperty("org.openscience.cdk.renderer.color", co);
		atomType.setAtomTypeName(sid);
		atomTypes.add(atomType);
	}

	/**
	 *  Read vdw radius, stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setvdWaals() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid = st.nextToken();
		String sradius = st.nextToken();
		String sepsi = st.nextToken();
		try {
			double epsi = new Double(sepsi).doubleValue();
			double radius = new Double(sradius).doubleValue();
			data.add(new Double(radius));
			data.add(new Double(epsi));

		} catch (NumberFormatException nfe) {
			throw new IOException("VdWaalsTable.ReadvdWaals: " +
					"Malformed Number");
		}
		key = "vdw" + sid;
		//if (parameterSet.containsKey(key)){System.out.println("KeyError: hasKey "+key);}
		parameterSet.put(key, data);
	}

	/**
	 *  Read vdW pair radius,stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setvdWaalpr() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();
		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));

		} catch (NumberFormatException nfe) {
			throw new IOException("VdWaalsTable.ReadvdWaalsPR:Malformed Number due to"+nfe.toString());
		}
		key = "vdwpr" + sid1 + ";" + sid2;
		parameterSet.put(key, data);
	}

	/**
	 *  Sets the bond attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setBond() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));

		} catch (NumberFormatException nfe) {
			throw new IOException("setBond: Malformed Number");
		}
		key = "bond" + sid1 + ";" + sid2;
		parameterSet.put(key, data);
	}

	/**
	 *  Sets the bond3 attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setBond3() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));

		} catch (NumberFormatException nfe) {
			throw new IOException("setBond3: Malformed Number");
		}
		key = "bond3_" + sid1 + ";" + sid2;
		parameterSet.put(key, data);
	}

	/**
	 *  Sets the bond4 attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setBond4() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));

		} catch (NumberFormatException nfe) {
			throw new IOException("setBond4: Malformed Number");
		}
		key = "bond4_" + sid1 + ";" + sid2;
		parameterSet.put(key, data);
	}

	/**
	 *  Sets the angle attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setAngle() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String sid3 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();
		String value3 = st.nextToken();
		String value4 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			double va3 = new Double(value3).doubleValue();
			double va4 = new Double(value4).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));
			data.add(new Double(va3));
			data.add(new Double(va4));

			key = "angle" + sid1 + ";" + sid2 + ";" + sid3;
			if (parameterSet.containsKey(key)) {
				data = (Vector) parameterSet.get(key);
				data.add(new Double(va1));
				data.add(new Double(va2));
				data.add(new Double(va3));
				data.add(new Double(va4));
			}
			parameterSet.put(key, data);

		} catch (NumberFormatException nfe) {
			throw new IOException("setAngle: Malformed Number");
		}

	}

	/**
	 *  Sets the angle3 attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setAngle3() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String sid3 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();
		String value3 = st.nextToken();
		String value4 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			double va3 = new Double(value3).doubleValue();
			double va4 = new Double(value4).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));
			data.add(new Double(va3));
			data.add(new Double(va4));

		} catch (NumberFormatException nfe) {
			throw new IOException("setAngle3: Malformed Number");
		}
		key = "angle3_" + sid1 + ";" + sid2 + ";" + sid3;
		parameterSet.put(key, data);
	}

	/**
	 *  Sets the angle4 attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setAngle4() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String sid3 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();
		String value3 = st.nextToken();
		String value4 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			double va3 = new Double(value3).doubleValue();
			double va4 = new Double(value4).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));
			data.add(new Double(va3));
			data.add(new Double(va4));

		} catch (NumberFormatException nfe) {
			throw new IOException("setAngle4: Malformed Number");
		}
		key = "angle4_" + sid1 + ";" + sid2 + ";" + sid3;
		parameterSet.put(key, data);
	}

	/**
	 *  Sets the strBnd attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setStrBnd() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String value1 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			data.add(new Double(va1));
		} catch (NumberFormatException nfe) {
			throw new IOException("setStrBnd: Malformed Number");
		}
		key = "strbnd" + sid1;
		parameterSet.put(key, data);
	}

	/**
	 *  Sets the opBend attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setOpBend() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String value1 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			data.add(new Double(va1));
			key = "opbend" + sid1 + ";" + sid2;
			if (parameterSet.containsKey(key)) {
				//logger.debug("KeyError: hasKey "+key);
				data = (Vector) parameterSet.get(key);
				data.add(new Double(va1));
			}
			parameterSet.put(key, data);

		} catch (NumberFormatException nfe) {
			throw new IOException("setOpBend: Malformed Number");
		}

	}

	/**
	 *  Sets the torsion attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setTorsion() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String sid3 = st.nextToken();
		String sid4 = st.nextToken();
		String value1 = st.nextToken();
		st.nextToken();
		st.nextToken();
		String value2 = st.nextToken();
		st.nextToken();
		st.nextToken();
		String value3 = st.nextToken();
		st.nextToken();
		st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			double va3 = new Double(value3).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));
			data.add(new Double(va3));

			key = "torsion" + sid1 + ";" + sid2 + ";" + sid3 + ";" + sid4;
			if (parameterSet.containsKey(key)) {
				data = (Vector) parameterSet.get(key);
				data.add(new Double(va1));
				data.add(new Double(va2));
				data.add(new Double(va3));
			}
			parameterSet.put(key, data);
		} catch (NumberFormatException nfe) {
			throw new IOException("setTorsion: Malformed Number");
		}

	}

	/**
	 *  Sets the torsion4 attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setTorsion4() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String sid3 = st.nextToken();
		String sid4 = st.nextToken();
		String value1 = st.nextToken();
		st.nextToken();
		st.nextToken();
		String value2 = st.nextToken();
		st.nextToken();
		st.nextToken();
		String value3 = st.nextToken();
		st.nextToken();
		st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			double va3 = new Double(value3).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));
			data.add(new Double(va3));

		} catch (NumberFormatException nfe) {
			throw new IOException("setTorsion4: Malformed Number");
		}
		key = "torsion4_" + sid1 + ";" + sid2 + ";" + sid3 + ";" + sid4;
		parameterSet.put(key, data);
	}

	/**
	 *  Sets the charge attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setCharge() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String value1 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			data.add(new Double(va1));
		} catch (NumberFormatException nfe) {
			throw new IOException("setCharge: Malformed Number due to "+nfe.toString());
		}
		key = "charge" + sid1;
		parameterSet.put(key, data);
	}

	/**
	 *  Sets the dipole attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setDipole() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));

		} catch (NumberFormatException nfe) {
			throw new IOException("setDipole: " +
					"Malformed Number");
		}
		key = "dipole" + sid1 + ";" + sid2;
		parameterSet.put(key, data);
	}

	/**
	 *  Sets the dipole3 attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setDipole3() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));

		} catch (NumberFormatException nfe) {
			throw new IOException("setDipole3: " +
					"Malformed Number");
		}
		key = "dipole3_" + sid1 + ";" + sid2;
		parameterSet.put(key, data);
	}

	/**
	 *  Sets the piAtom attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setPiAtom() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();
		String value3 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			double va3 = new Double(value3).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));
			data.add(new Double(va3));

		} catch (NumberFormatException nfe) {
			throw new IOException("setPiAtom: " +
					"Malformed Number");
		}
		key = "piatom" + sid1;
		parameterSet.put(key, data);
	}

	/**
	 *  Sets the piBond attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setPiBond() throws Exception {
		List data = new Vector();
		st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));

		} catch (NumberFormatException nfe) {
			throw new IOException("setPiBond: " +
					"Malformed Number");
		}
		key = "pibond" + sid1 + ";" + sid2;
		parameterSet.put(key, data);
	}

	/**
	 * The main method which parses through the force field configuration file
	 *
	 * @exception  Exception  Description of the Exception
	 */
	public void readParameterSets() throws Exception {
		//vdW,vdWp,bond,bond4,bond3,angle,angle4,angle3,
		//strbond,opbend,torsion,torsion4,charge,dipole,
		//dipole3,piatom,pibond,dipole3
		//logger.debug("------ ReadParameterSets ------");

		if (ins == null) {
			ins = getClass().getResourceAsStream(configFile);
		}
		if (ins == null) {
			throw new IOException("There was a problem getting the default stream: " + configFile);
		}

		// read the contents from file
		BufferedReader r = new BufferedReader(new InputStreamReader(ins), 1024);
		String s;
		int[] a = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		try {
			while (true) {
				s = r.readLine();
				if (s == null) {
					break;
				}
				st = new StringTokenizer(s, "\t ;");
				int nt = st.countTokens();
				if (s.startsWith(">") & nt > 1) {
					setForceFieldDefinitions();
					a[0]++;
				} else if (s.startsWith("atom") & nt <= 8) {
					a[0]++;
					setAtomTypes();
				} else if (s.startsWith("vdw ") & nt <= 5) {
					setvdWaals();
					a[1]++;
				} else if (s.startsWith("vdwpr ") & nt <= 6) {
					setvdWaalpr();
					a[2]++;
				} else if (s.startsWith("bond ") & nt <= 7) {
					setBond();
					a[3]++;
				} else if (s.startsWith("bond4 ") & nt == 5) {
					setBond4();
					a[4]++;
				} else if (s.startsWith("bond3 ") & nt == 5) {
					setBond3();
					a[5]++;
				} else if (s.startsWith("angle ") & nt == 8) {
					setAngle();
					a[6]++;
				} else if (s.startsWith("angle4 ") & nt == 8) {
					setAngle4();
					a[17]++;
				} else if (s.startsWith("angle3 ") & nt == 8) {
					setAngle3();
					a[7]++;
				} else if (s.startsWith("strbnd ") & nt == 5) {
					setStrBnd();
					a[8]++;
				} else if (s.startsWith("opbend ") & nt == 4) {
					setOpBend();
					a[9]++;
				} else if (s.startsWith("torsion ") & nt == 14) {
					setTorsion();
					a[10]++;
				} else if (s.startsWith("torsion4 ") & nt == 14) {
					setTorsion4();
					a[11]++;
				} else if (s.startsWith("charge ") & nt == 3) {
					setCharge();
					a[12]++;
				} else if (s.startsWith("dipole ") & nt == 5) {
					setDipole();
					a[13]++;
				} else if (s.startsWith("dipole3 ") & nt == 5) {
					setDipole3();
					a[14]++;
				} else if (s.startsWith("piatom ") & nt == 5) {
					setPiAtom();
					a[15]++;
				} else if (s.startsWith("pibond ") & nt == 5) {
					setPiBond();
					a[16]++;
				} 
			}// end while
			ins.close();
		} catch (IOException e) {
			throw new IOException("There was a problem parsing the mm2 forcefield due to:"+e.toString());
		}
	}
}


