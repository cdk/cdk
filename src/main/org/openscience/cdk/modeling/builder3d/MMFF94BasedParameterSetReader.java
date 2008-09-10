/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
 * defined in mmff94.prm from moe. This class was added to be able to port
 * mmff94 to CDK.
 *
 * @author         chhoppe
 * @cdk.created    2004-09-07
 * @cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 * @cdk.keyword    atom type, mmff94
 */
public class MMFF94BasedParameterSetReader {

	private String configFile = "org/openscience/cdk/modeling/forcefield/data/mmff94.prm";
	private InputStream ins = null;
	private Map<String,Object> parameterSet;
	private List<IAtomType> atomTypes;
	private StringTokenizer st;
	private String key = "";
	private String sid;

	private String configFilevdW = "org/openscience/cdk/modeling/forcefield/data/mmffvdw.prm";
	private InputStream insvdW = null;
	private StringTokenizer stvdW;
	private String sidvdW;

	private String configFileDFSB = "org/openscience/cdk/modeling/forcefield/data/mmffdfsb.par";
	private InputStream insDFSB;
	private StringTokenizer stDFSB;

	/**
	 *Constructor for the MM2BasedParameterSetReader object
	 */
	public MMFF94BasedParameterSetReader() {
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
	 * Sets the file containing the config data
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
	private void setAtomTypeData() throws Exception {
		
		key = "data" + sid;
		List data = new Vector();
		
		String sradius = st.nextToken();
		String swell = st.nextToken();
		String sapol=st.nextToken();
		String sNeff=st.nextToken();
		//st.nextToken();
		String sDA=st.nextToken();
		String sq0=st.nextToken();
		String spbci=st.nextToken();
		String sfcadj=st.nextToken();
		
		
		stvdW.nextToken();
		stvdW.nextToken();
		String sA = stvdW.nextToken();
		String sG = stvdW.nextToken();
		
		try {
			double well = new Double(swell).doubleValue();
			double apol = new Double(sapol).doubleValue();
			double Neff = new Double(sNeff).doubleValue();
			double fcadj = new Double(sfcadj).doubleValue();
			//double pbci = new Double(spbci).doubleValue();
			double a = new Double(sA).doubleValue();
			double g = new Double(sG).doubleValue();
			
			data.add(new Double(well));
			data.add(new Double(apol));
			data.add(new Double(Neff));
			data.add(new String(sDA));
			data.add(new Double(fcadj));
			data.add(new Double(spbci));
			data.add(new Double(a));
			data.add(new Double(g));
			
		} catch (NumberFormatException nfe) {
			throw new IOException("Data: Malformed Number due to:"+nfe);
		}
		
		//logger.debug("data : " + data);
		parameterSet.put(key, data);
		
		key="vdw"+sid;
		data = new Vector();
		try{
			double radius = new Double(sradius).doubleValue();
			data.add(new Double(radius));

		}catch (NumberFormatException nfe2) {
//			logger.debug("vdwError: Malformed Number due to:"+nfe2);
		  }
		parameterSet.put(key, data);

		key="charge"+sid;
		data = new Vector();
		try{
			double q0 = new Double(sq0).doubleValue();
			data.add(new Double(q0));
		}catch (NumberFormatException nfe3) {
			System.out.println("Charge: Malformed Number due to:"+nfe3);
		}
		parameterSet.put(key, data);
	}
	
	/**
	 *  Read and stores the atom types in a vector
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setAtomTypes() throws Exception {
		String name = "";
		String rootType = "";
		//int an = 0;
		int rl = 255;
		int gl = 20;
		int bl = 147;
		int maxbond = 0;
		int atomNr=0;
		
		double mass = 0.0;
		st.nextToken();
		String sid = st.nextToken();
		rootType = st.nextToken();
		String smaxbond = st.nextToken();
		String satomNr=st.nextToken();
		String smass=st.nextToken();
		name = st.nextToken();
		
		
		try {
			maxbond = Integer.parseInt(smaxbond);
			mass= Double.parseDouble(smass);
			atomNr=Integer.parseInt(satomNr);
		
		} catch (NumberFormatException nfe) {
			throw new IOException("AtomTypeTable.ReadAtypes: " +
					"Malformed Number");
		}
		
		AtomType atomType = new AtomType(name, rootType);
		atomType.setAtomicNumber(atomNr);
		atomType.setExactMass(mass);
		atomType.setFormalNeighbourCount(maxbond);
		atomType.setSymbol(rootType);
		Color co = new Color(rl, gl, bl);
		atomType.setProperty("org.openscience.cdk.renderer.color", co);
		atomType.setAtomTypeName(sid);
		atomTypes.add(atomType);
	}
	
	
	/**
	 *  Sets the bond attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setBond() throws Exception {
		List data = new Vector();
		st.nextToken();
		String scode = st.nextToken();
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String slen = st.nextToken();
		String sk2 = st.nextToken();
		String sk3 = st.nextToken();
		String sk4 = st.nextToken();
		String sbci = st.nextToken();
		
		try {
			double len = new Double(slen).doubleValue();
			double k2 = new Double(sk2).doubleValue();
			double k3 = new Double(sk3).doubleValue();
			double k4 = new Double(sk4).doubleValue();
			double bci = new Double(sbci).doubleValue();
			data.add(new Double(len));
			data.add(new Double(k2));
			data.add(new Double(k3));
			data.add(new Double(k4));
			data.add(new Double(bci));
			
		} catch (NumberFormatException nfe) {
			throw new IOException("setBond: Malformed Number due to:"+nfe);
		}
		key = "bond" + scode + ";" + sid1 + ";" + sid2;
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
		String scode = st.nextToken(); // String scode
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String sid3 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();
		String value3 = st.nextToken();
		String value4 = st.nextToken();
		
		try {
		    //int code=new Integer(scode).intValue();
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			double va3 = new Double(value3).doubleValue();
			double va4 = new Double(value4).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));
			data.add(new Double(va3));
			data.add(new Double(va4));
			
			key = "angle" + scode + ";" + sid1 + ";" + sid2 + ";" + sid3;
			if (parameterSet.containsKey(key)) {
				data = (Vector) parameterSet.get(key);
				data.add(new Double(va1));
				data.add(new Double(va2));
				data.add(new Double(va3));
				data.add(new Double(va4));
			}
			parameterSet.put(key, data);

		} catch (NumberFormatException nfe) {
			throw new IOException("setAngle: Malformed Number due to:"+nfe);
		}
	}
	
	
	/**
	 *  Sets the strBnd attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setStrBnd() throws Exception {
		List data = new Vector();
		st.nextToken();
		String scode = st.nextToken(); // String scode
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String sid3 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();
		
		try {
		    //int code=new Integer(scode).intValue();
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			data.add(new Double(va1));
			data.add(new Double(va2));
			
		} catch (NumberFormatException nfe) {
			throw new IOException("setStrBnd: Malformed Number due to:"+nfe);
		}
		key = "strbnd" + scode + ";" + sid1 + ";" + sid2 + ";" + sid3;
		//logger.debug("key =" + key);
		parameterSet.put(key, data);
	}
	
	/**
	 *  Sets the torsion attribute stored into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setTorsion() throws Exception {
		List data = null;
		st.nextToken();
		String scode = st.nextToken(); // String scode
		String sid1 = st.nextToken();
		String sid2 = st.nextToken();
		String sid3 = st.nextToken();
		String sid4 = st.nextToken();
		String value1 = st.nextToken();
		String value2 = st.nextToken();
		String value3 = st.nextToken();
		String value4 = st.nextToken();
		String value5 = st.nextToken();
		
		try {
			double va1 = new Double(value1).doubleValue();
			double va2 = new Double(value2).doubleValue();
			double va3 = new Double(value3).doubleValue();
			double va4 = new Double(value4).doubleValue();
			double va5 = new Double(value5).doubleValue();

			key = "torsion" + scode + ";" + sid1 + ";" + sid2 + ";" + sid3 + ";" + sid4;
			//logger.debug("key = " + key);
			if (parameterSet.containsKey(key)) {
				data = (Vector) parameterSet.get(key);
				data.add(new Double(va1));
				data.add(new Double(va2));
				data.add(new Double(va3));
				data.add(new Double(va4));
				data.add(new Double(va5));
				//logger.debug("data = " + data);
			}
			else{
			    data = new Vector();
			    data.add(new Double(va1));
			    data.add(new Double(va2));
			    data.add(new Double(va3));
			    data.add(new Double(va4));
			    data.add(new Double(va5));
				//logger.debug("data = " + data);
			}

			parameterSet.put(key, data);

		} catch (NumberFormatException nfe) {
			throw new IOException("setTorsion: Malformed Number due to:"+nfe);
		}
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
		String sid3 = st.nextToken();
		String sid4 = st.nextToken();
		String value1 = st.nextToken();

		try {
			double va1 = new Double(value1).doubleValue();
			data.add(new Double(va1));
			key = "opbend" + sid1 + ";" + sid2 + ";" + sid3 + ";" + sid4;
			if (parameterSet.containsKey(key)) {
				data = (Vector) parameterSet.get(key);
				data.add(new Double(va1));
			}
			parameterSet.put(key, data);

		} catch (NumberFormatException nfe) {
			throw new IOException("setOpBend: Malformed Number due to:"+nfe);
		}
	}
	
	
	/**
	 *  Sets the Default Stretch-Bend Parameters into the parameter set
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void setDefaultStrBnd() throws Exception {
		//logger.debug("Sets the Default Stretch-Bend Parameters");
		List data = new Vector();
		stDFSB.nextToken();
		String sIR = stDFSB.nextToken();
		String sJR = stDFSB.nextToken();
		String sKR = stDFSB.nextToken();
		String skbaIJK = stDFSB.nextToken();
		String skbaKJI = stDFSB.nextToken();

		try {
			key = "DFSB" + sIR + ";" + sJR + ";" + sKR;
			double kbaIJK = new Double(skbaIJK).doubleValue();
			double kbaKJI = new Double(skbaKJI).doubleValue();
			data.add(new Double(kbaIJK));
			data.add(new Double(kbaKJI));
			parameterSet.put(key, data);

		} catch (NumberFormatException nfe) {
			throw new IOException("setDFSB: Malformed Number due to:"+nfe);
		}
	}
	
	
	/**
	 * The main method which parses through the force field configuration file
	 *
	 * @exception  Exception  Description of the Exception
	 */
	public void readParameterSets() throws Exception {
		//vdW,bond,angle,strbond,opbend,torsion,data
		//logger.debug("------ Read MMFF94 ParameterSets ------");

		if (ins == null) {
            ClassLoader loader = this.getClass().getClassLoader();
            System.out.println("loader.getClassName:" + loader.getClass().getName());
			ins = loader.getResourceAsStream(configFile);
		}
		if (ins == null) {
			throw new IOException("There was a problem getting the default stream: " + configFile);
		}
		
		BufferedReader r = new BufferedReader(new InputStreamReader(ins), 1024);
		String s;
		int[] a = {0, 0, 0, 0, 0, 0, 0, 0};
		
		if (insvdW == null) {
			insvdW = this.getClass().getClassLoader().getResourceAsStream(configFilevdW);
		}
		if (insvdW == null) {
			throw new IOException("There was a problem getting the default stream: " + configFilevdW);
		}
		
		BufferedReader rvdW = new BufferedReader(new InputStreamReader(insvdW), 1024);
		String svdW;
		int ntvdW;
				
		if (insDFSB == null) {
			insDFSB = this.getClass().getClassLoader().getResourceAsStream(configFileDFSB);
		}
		if (insDFSB == null) {
			throw new IOException("There was a problem getting the default stream: " + configFileDFSB);
		}
		
		BufferedReader rDFSB = new BufferedReader(new InputStreamReader(insDFSB), 1024);
		String sDFSB;
		int ntDFSB;
				
		try {
			while (true) {
				s = r.readLine();
				if (s == null) {
					break;
				}
				st = new StringTokenizer(s,"\t; ");
				int nt = st.countTokens();
				if (s.startsWith("atom") & nt <= 8) {
					setAtomTypes();
					a[0]++;
				} else if (s.startsWith("bond") & nt == 9) {
					setBond();
					a[1]++;
				} else if (s.startsWith("angle") & nt <= 10) {
					setAngle();
					a[2]++;
				} else if (s.startsWith("strbnd") & nt == 7) {
					setStrBnd();
					a[3]++;
				} else if (s.startsWith("torsion") & nt == 11) {
					setTorsion();
					a[4]++;					
				} else if (s.startsWith("opbend") & nt == 6) {
					setOpBend();
					a[5]++;
				} else if (s.startsWith("data") & nt == 10) {
					readatmmffvdw:
						while (true) {
							svdW = rvdW.readLine();
							if (svdW == null) {
								break;
							}
							stvdW = new StringTokenizer(svdW,"\t; ");
							ntvdW = stvdW.countTokens();
							//logger.debug("ntvdW : " + ntvdW);
							if (svdW.startsWith("vdw") & ntvdW == 9) {
								st.nextToken();
								sid = st.nextToken();
								stvdW.nextToken();
								sidvdW = stvdW.nextToken();
								if (sid.equals(sidvdW)) {
									setAtomTypeData();
									a[6]++;
								}
								break readatmmffvdw;
							}
						}// end while
				}
			}// end while

			ins.close();
			insvdW.close();
		} catch (IOException e) {
			System.err.println(e.toString());
			throw new IOException("There was a problem parsing the mmff94 forcefield");
		}

		try {
			//logger.debug("Parses the Default Stretch-Bend Parameters");
			while (true) {
				sDFSB = rDFSB.readLine();
				//logger.debug("sDFSB = " + sDFSB);
				if (sDFSB == null) {
					//logger.debug("sDFSB == null, break");
					break;
				}
				stDFSB = new StringTokenizer(sDFSB,"\t; ");
				ntDFSB = stDFSB.countTokens();
				//logger.debug("ntDFSB : " + ntDFSB);
				if (sDFSB.startsWith("DFSB") & ntDFSB == 6) {
					setDefaultStrBnd();
				}
			}
			insDFSB.close();
			//logger.debug("insDFSB closed");
		} catch (IOException e) {
			System.err.println(e.toString());
			throw new IOException("There was a problem parsing the Default Stretch-Bend Parameters (mmffdfsb.par)");
		}
	}

}


