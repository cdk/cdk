/* Copyright (C) 2012 Daniel Szisz
 *
 * Contact: orlando@caesar.elte.hu
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.modeling.builder3d;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomType;

import org.junit.Test;

/**
 * This class is for testing the MMFF94 based parameter
 * reading in CDK.
 *
 * @author danielszisz
 * @version 04/16/2012
 * @cdk.module test-forcefield
 */
public class MMFF94BasedParameterSetReaderTest {

    @Test
    public void testreadParameterSets() throws Exception {
        MMFF94BasedParameterSetReader mmff94bpsr = new MMFF94BasedParameterSetReader();
        mmff94bpsr.readParameterSets(DefaultChemObjectBuilder.getInstance());
        Map<String, Object> parameterSet = new Hashtable<String, Object>();
        parameterSet = mmff94bpsr.getParamterSet();

        //test atom type
        List<IAtomType> atomtypes = mmff94bpsr.getAtomTypes();
        IAtomType atomtype = atomtypes.get(0);
        String sid = "C";
        assertEquals(sid, atomtype.getAtomTypeName());
        String rootType = "C";
        assertEquals(rootType, atomtype.getSymbol());
        String smaxbond = "4";
        assertEquals(Integer.parseInt(smaxbond), (int) atomtype.getFormalNeighbourCount());
        String satomNr = "6";
        assertEquals(Integer.parseInt(satomNr), (int) atomtype.getAtomicNumber());

        //atom
        //TODO testing

        //bond
        //		String scode = "0";
        String sid1 = "C";
        String sid2 = "C";
        String slen = "1.508";
        String sk2 = "306.432";
        String sk3 = "-612.865";
        String sk4 = "715.009";
        String sbci = "0.0000";
        String bondkey = "bond" + sid1 + ";" + sid2;
        List<Double> bonddata = new ArrayList<Double>();
        bonddata.add((Double) (new Double(slen).doubleValue()));
        bonddata.add((Double) (new Double(sk2).doubleValue()));
        bonddata.add((Double) (new Double(sk3).doubleValue()));
        bonddata.add((Double) (new Double(sk4).doubleValue()));
        bonddata.add((Double) (new Double(sbci).doubleValue()));

        //strbnd
        //		scode = "0";
        sid1 = "C";
        sid2 = "C";
        String sid3 = "C";
        String value1 = "14.82507";
        String value2 = "14.82507";
        String strbndkey = "strbnd" + sid1 + ";" + sid2 + ";" + sid3;
        List<Double> strbnddata = new ArrayList<Double>();
        strbnddata.add((Double) (new Double(value1).doubleValue()));
        strbnddata.add((Double) (new Double(value2).doubleValue()));

        //angle
        //      scode = "0";
        sid1 = "C=";
        sid2 = "C";
        sid3 = "N";
        value1 = "105.837";
        value2 = "86.1429";
        String value3 = "-34.5494";
        String value4 = "0";
        String anglekey = "angle" + sid1 + ";" + sid2 + ";" + sid3;
        List<Double> angledata = new ArrayList<Double>();
        angledata.add((Double) (new Double(value1).doubleValue()));
        angledata.add((Double) (new Double(value2).doubleValue()));
        angledata.add((Double) (new Double(value3).doubleValue()));
        angledata.add((Double) (new Double(value4).doubleValue()));

        //torsion
        //	    scode = "0";
        sid1 = "HC";
        sid2 = "C";
        sid3 = "C";
        String sid4 = "HC";
        value1 = "0.142";
        value2 = "0.693";
        value3 = "0.157";
        value4 = "0.000";
        String value5 = "0.000";
        String torsionkey = "torsion" + ";" + sid1 + ";" + sid2 + ";" + sid3 + ";" + sid4;
        List<Double> torsiondata = new ArrayList<Double>();
        torsiondata.add((Double) (new Double(value1).doubleValue()));
        torsiondata.add((Double) (new Double(value2).doubleValue()));
        torsiondata.add((Double) (new Double(value3).doubleValue()));
        torsiondata.add((Double) (new Double(value4).doubleValue()));
        torsiondata.add((Double) (new Double(value5).doubleValue()));

        //opbend
        //      scode = "0";
        sid1 = "O=";
        sid2 = "C=";
        sid3 = "CR4R";
        sid4 = "CR4R";
        value1 = "10.86681780";
        String opbendkey = "opbend" + ";" + sid1 + ";" + sid2 + ";" + sid3 + ";" + sid4;
        List<Double> opbenddata = new ArrayList<Double>();
        opbenddata.add((Double) (new Double(value1).doubleValue()));

        //TODO data lines testing

        for (Entry<String, Object> e : parameterSet.entrySet()) {
            if (e.getKey().equals(bondkey))
                assertEquals(bonddata, e.getValue());
            else if (e.getKey().equals(strbndkey))
                assertEquals(strbnddata, e.getValue());
            else if (e.getKey().equals(anglekey))
                assertEquals(angledata, e.getValue());
            else if (e.getKey().equals(torsionkey))
                assertEquals(torsiondata, e.getValue());
            else if (e.getKey().equals(opbendkey)) assertEquals(opbenddata, e.getValue());
        }
    }
}
