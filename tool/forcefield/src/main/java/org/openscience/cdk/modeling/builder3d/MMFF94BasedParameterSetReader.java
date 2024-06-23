/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

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

/**
 * AtomType list configurator that uses the ParameterSet originally defined in
 * mmff94.prm from moe. This class was added to be able to port mmff94 to CDK.
 *
 * @author chhoppe
 * @cdk.created 2004-09-07
 * @cdk.module forcefield
 * @cdk.githash
 * @cdk.keyword atom type, mmff94
 */
public class MMFF94BasedParameterSetReader {

    private final ILoggingTool  LOG            = LoggingToolFactory
                                                       .createLoggingTool(MMFF94BasedParameterSetReader.class);

    private final String              configFile     = "org/openscience/cdk/modeling/forcefield/data/mmff94.prm";
    private InputStream         ins            = null;
    private final Map<String, Object> parameterSet;
    private final List<IAtomType>     atomTypes;
    private StringTokenizer     st;
    private String              key            = "";
    private String              sid;

    private final String              configFilevdW  = "org/openscience/cdk/modeling/forcefield/data/mmffvdw.prm";
    private InputStream         insvdW         = null;
    private StringTokenizer     stvdW;
    private String              sidvdW;

    private final String              configFileDFSB = "org/openscience/cdk/modeling/forcefield/data/mmffdfsb.par";
    private InputStream         insDFSB;
    private StringTokenizer     stDFSB;

    /**
     * Constructor for the MM2BasedParameterSetReader object
     */
    public MMFF94BasedParameterSetReader() {
        parameterSet = new Hashtable<>();
        atomTypes = new Vector<>();
    }

    public Map<String, Object> getParamterSet() {
        return parameterSet;
    }

    public List<IAtomType> getAtomTypes() {
        return atomTypes;
    }

    /**
     * Sets the file containing the config data
     *
     * @param ins The new inputStream type InputStream
     */
    public void setInputStream(InputStream ins) {
        this.ins = ins;
    }

    /**
     * Read a text based configuration file out of the force field mm2 file
     *
     * @throws Exception Description of the Exception
     */
    private void setAtomTypeData() throws Exception {

        key = "data" + sid;
        List data = new Vector();

        String sradius = st.nextToken();
        String swell = st.nextToken();
        String sapol = st.nextToken();
        String sNeff = st.nextToken();
        //st.nextToken();
        String sDA = st.nextToken();
        String sq0 = st.nextToken();
        String spbci = st.nextToken();
        String sfcadj = st.nextToken();

        stvdW.nextToken();
        stvdW.nextToken();
        String sA = stvdW.nextToken();
        String sG = stvdW.nextToken();

        try {
            double well = Double.valueOf(swell);
            double apol = Double.valueOf(sapol);
            double Neff = Double.valueOf(sNeff);
            double fcadj = Double.valueOf(sfcadj);
            //double pbci = Double.valueOf(spbci).doubleValue();
            double a = Double.valueOf(sA);
            double g = Double.valueOf(sG);

            data.add(well);
            data.add(apol);
            data.add(Neff);
            data.add(sDA);
            data.add(fcadj);
            data.add(Double.valueOf(spbci));
            data.add(a);
            data.add(g);

        } catch (NumberFormatException nfe) {
            throw new IOException("Data: Malformed Number due to:" + nfe);
        }

        LOG.debug("data : well,apol,Neff,sDA,fcadj,spbci,a,g " + data);
        parameterSet.put(key, data);

        key = "vdw" + sid;
        data = new Vector();
        try {
            double radius = Double.valueOf(sradius);
            data.add(radius);

        } catch (NumberFormatException nfe2) {
            LOG.debug("vdwError: Malformed Number due to:" + nfe2);
        }
        parameterSet.put(key, data);

        key = "charge" + sid;
        data = new Vector();
        try {
            double q0 = Double.valueOf(sq0);
            data.add(q0);
        } catch (NumberFormatException nfe3) {
            System.err.println("Charge: Malformed Number due to:" + nfe3);
        }
        parameterSet.put(key, data);
    }

    /**
     * Read and stores the atom types in a vector
     *
     * @throws Exception Description of the Exception
     */
    private void setAtomTypes(IChemObjectBuilder builder) throws Exception {
        String name;
        String rootType;
        //int an = 0;
        int rl = 255;
        int gl = 20;
        int bl = 147;
        int maxbond;
        int atomNr;

        double mass;
        st.nextToken();
        String sid = st.nextToken();
        rootType = st.nextToken();
        String smaxbond = st.nextToken();
        String satomNr = st.nextToken();
        String smass = st.nextToken();
        name = st.nextToken();

        try {
            maxbond = Integer.parseInt(smaxbond);
            mass = Double.parseDouble(smass);
            atomNr = Integer.parseInt(satomNr);

        } catch (NumberFormatException nfe) {
            throw new IOException("AtomTypeTable.ReadAtypes: " + "Malformed Number");
        }

        IAtomType atomType = builder.newInstance(IAtomType.class, name, rootType);
        atomType.setAtomicNumber(atomNr);
        atomType.setExactMass(mass);
        atomType.setMassNumber(massNumber(atomNr, mass));
        atomType.setFormalNeighbourCount(maxbond);
        atomType.setSymbol(rootType);
        Color co = new Color(rl, gl, bl);
        atomType.setProperty("org.openscience.cdk.renderer.color", co);
        atomType.setAtomTypeName(sid);
        atomTypes.add(atomType);
    }

    /**
     * Sets the bond attribute stored into the parameter set
     *
     * @throws Exception Description of the Exception
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
            double len = Double.valueOf(slen);
            double k2 = Double.valueOf(sk2);
            double k3 = Double.valueOf(sk3);
            double k4 = Double.valueOf(sk4);
            double bci = Double.valueOf(sbci);
            data.add(len);
            data.add(k2);
            data.add(k3);
            data.add(k4);
            data.add(bci);

        } catch (NumberFormatException nfe) {
            throw new IOException("setBond: Malformed Number due to:" + nfe);
        }
        //		key = "bond" + scode + ";" + sid1 + ";" + sid2;
        key = "bond" + sid1 + ";" + sid2;
        parameterSet.put(key, data);
    }

    /**
     * Sets the angle attribute stored into the parameter set
     *
     * @throws Exception Description of the Exception
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
            double va1 = Double.valueOf(value1);
            double va2 = Double.valueOf(value2);
            double va3 = Double.valueOf(value3);
            double va4 = Double.valueOf(value4);
            data.add(va1);
            data.add(va2);
            data.add(va3);
            data.add(va4);

            //			key = "angle" + scode + ";" + sid1 + ";" + sid2 + ";" + sid3;
            key = "angle" + sid1 + ";" + sid2 + ";" + sid3;
            if (parameterSet.containsKey(key)) {
                data = (Vector) parameterSet.get(key);
                data.add(va1);
                data.add(va2);
                data.add(va3);
                data.add(va4);
            }
            parameterSet.put(key, data);

        } catch (NumberFormatException nfe) {
            throw new IOException("setAngle: Malformed Number due to:" + nfe);
        }
    }

    /**
     * Sets the strBnd attribute stored into the parameter set
     *
     * @throws Exception Description of the Exception
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
            double va1 = Double.valueOf(value1);
            double va2 = Double.valueOf(value2);
            data.add(va1);
            data.add(va2);

        } catch (NumberFormatException nfe) {
            throw new IOException("setStrBnd: Malformed Number due to:" + nfe);
        }
        key = "strbnd" + scode + ";" + sid1 + ";" + sid2 + ";" + sid3;
        LOG.debug("key =" + key);
        parameterSet.put(key, data);
    }

    /**
     * Sets the torsion attribute stored into the parameter set
     *
     * @throws Exception Description of the Exception
     */
    private void setTorsion() throws Exception {
        List data;
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
            double va1 = Double.valueOf(value1);
            double va2 = Double.valueOf(value2);
            double va3 = Double.valueOf(value3);
            double va4 = Double.valueOf(value4);
            double va5 = Double.valueOf(value5);

            key = "torsion" + scode + ";" + sid1 + ";" + sid2 + ";" + sid3 + ";" + sid4;
            LOG.debug("key = " + key);
            if (parameterSet.containsKey(key)) {
                data = (Vector) parameterSet.get(key);
                data.add(va1);
                data.add(va2);
                data.add(va3);
                data.add(va4);
                data.add(va5);
                LOG.debug("data = " + data);
            } else {
                data = new Vector();
                data.add(va1);
                data.add(va2);
                data.add(va3);
                data.add(va4);
                data.add(va5);
                LOG.debug("data = " + data);
            }

            parameterSet.put(key, data);

        } catch (NumberFormatException nfe) {
            throw new IOException("setTorsion: Malformed Number due to:" + nfe);
        }
    }

    /**
     * Sets the opBend attribute stored into the parameter set
     *
     * @throws Exception Description of the Exception
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
            double va1 = Double.valueOf(value1);
            data.add(va1);
            key = "opbend" + sid1 + ";" + sid2 + ";" + sid3 + ";" + sid4;
            if (parameterSet.containsKey(key)) {
                data = (Vector) parameterSet.get(key);
                data.add(va1);
            }
            parameterSet.put(key, data);

        } catch (NumberFormatException nfe) {
            throw new IOException("setOpBend: Malformed Number due to:" + nfe);
        }
    }

    /**
     * Sets the Default Stretch-Bend Parameters into the parameter set
     *
     * @throws Exception Description of the Exception
     */
    private void setDefaultStrBnd() throws Exception {
        LOG.debug("Sets the Default Stretch-Bend Parameters");
        List data = new Vector();
        stDFSB.nextToken();
        String sIR = stDFSB.nextToken();
        String sJR = stDFSB.nextToken();
        String sKR = stDFSB.nextToken();
        String skbaIJK = stDFSB.nextToken();
        String skbaKJI = stDFSB.nextToken();

        try {
            key = "DFSB" + sIR + ";" + sJR + ";" + sKR;
            double kbaIJK = Double.valueOf(skbaIJK);
            double kbaKJI = Double.valueOf(skbaKJI);
            data.add(kbaIJK);
            data.add(kbaKJI);
            parameterSet.put(key, data);

        } catch (NumberFormatException nfe) {
            throw new IOException("setDFSB: Malformed Number due to:" + nfe);
        }
    }

    /**
     * The main method which parses through the force field configuration file
     *
     * @throws Exception Description of the Exception
     */
    public void readParameterSets(IChemObjectBuilder builder) throws Exception {
        //vdW,bond,angle,strbond,opbend,torsion,data
        LOG.debug("------ Read MMFF94 ParameterSets ------");

        if (ins == null) {
            ClassLoader loader = this.getClass().getClassLoader();
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
                st = new StringTokenizer(s, "\t; ");
                int nt = st.countTokens();
                if (s.startsWith("atom") & nt <= 8) {
                    setAtomTypes(builder);
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
                    readatmmffvdw: while (true) {
                        svdW = rvdW.readLine();
                        if (svdW == null) {
                            break;
                        }
                        stvdW = new StringTokenizer(svdW, "\t; ");
                        ntvdW = stvdW.countTokens();
                        LOG.debug("ntvdW : " + ntvdW);
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
            throw new IOException("There was a problem parsing the mmff94 forcefield");
        }

        try {
            LOG.debug("Parses the Default Stretch-Bend Parameters");
            while (true) {
                sDFSB = rDFSB.readLine();
                LOG.debug("sDFSB = " + sDFSB);
                if (sDFSB == null) {
                    LOG.debug("sDFSB == null, break");
                    break;
                }
                stDFSB = new StringTokenizer(sDFSB, "\t; ");
                ntDFSB = stDFSB.countTokens();
                LOG.debug("ntDFSB : " + ntDFSB);
                if (sDFSB.startsWith("DFSB") & ntDFSB == 6) {
                    setDefaultStrBnd();
                }
            }
            insDFSB.close();
            LOG.debug("insDFSB closed");
        } catch (IOException e) {
            throw new IOException("There was a problem parsing the Default Stretch-Bend Parameters (mmffdfsb.par)");
        }
    }

    /**
     * Mass number for a atom with a given atomic number and exact mass.
     *
     * @param atomicNumber atomic number
     * @param exactMass    exact mass
     * @return the mass number (or null) if no mass number was found
     * @throws IOException isotope configuration could not be loaded
     */
    private Integer massNumber(int atomicNumber, double exactMass) throws IOException {
        String symbol = PeriodicTable.getSymbol(atomicNumber);
        IIsotope isotope = Isotopes.getInstance().getIsotope(symbol, exactMass, 0.001);
        return isotope != null ? isotope.getMassNumber() : null;
    }

}
