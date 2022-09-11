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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomType;

/**
 * This class is for testing the MMFF94 based parameter
 * reading in CDK.
 *
 * @author danielszisz
 * @version 04/16/2012
 * @cdk.module test-forcefield
 */
class MMFF94BasedParameterSetReaderTest {

    @Test
    void testreadParameterSets() throws Exception {
        MMFF94BasedParameterSetReader mmff94bpsr = new MMFF94BasedParameterSetReader();
        mmff94bpsr.readParameterSets(DefaultChemObjectBuilder.getInstance());
        Map<String, Object> parameterSet;
        parameterSet = mmff94bpsr.getParamterSet();

        //test atom type
        List<IAtomType> atomtypes = mmff94bpsr.getAtomTypes();
        IAtomType atomtype = atomtypes.get(0);
        String sid = "C";
        Assertions.assertEquals(sid, atomtype.getAtomTypeName());
        String rootType = "C";
        Assertions.assertEquals(rootType, atomtype.getSymbol());
        String smaxbond = "4";
        Assertions.assertEquals(Integer.parseInt(smaxbond), (int) atomtype.getFormalNeighbourCount());
        String satomNr = "6";
        Assertions.assertEquals(Integer.parseInt(satomNr), (int) atomtype.getAtomicNumber());

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
        List<Double> bonddata = new ArrayList<>();
        bonddata.add(new Double(slen));
        bonddata.add(new Double(sk2));
        bonddata.add(new Double(sk3));
        bonddata.add(new Double(sk4));
        bonddata.add(new Double(sbci));

        //strbnd
        //		scode = "0";
        sid1 = "C";
        sid2 = "C";
        String sid3 = "C";
        String value1 = "14.82507";
        String value2 = "14.82507";
        String strbndkey = "strbnd" + sid1 + ";" + sid2 + ";" + sid3;
        List<Double> strbnddata = new ArrayList<>();
        strbnddata.add(new Double(value1));
        strbnddata.add(new Double(value2));

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
        List<Double> angledata = new ArrayList<>();
        angledata.add(new Double(value1));
        angledata.add(new Double(value2));
        angledata.add(new Double(value3));
        angledata.add(new Double(value4));

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
        List<Double> torsiondata = new ArrayList<>();
        torsiondata.add(new Double(value1));
        torsiondata.add(new Double(value2));
        torsiondata.add(new Double(value3));
        torsiondata.add(new Double(value4));
        torsiondata.add(new Double(value5));

        //opbend
        //      scode = "0";
        sid1 = "O=";
        sid2 = "C=";
        sid3 = "CR4R";
        sid4 = "CR4R";
        value1 = "10.86681780";
        String opbendkey = "opbend" + ";" + sid1 + ";" + sid2 + ";" + sid3 + ";" + sid4;
        List<Double> opbenddata = new ArrayList<>();
        opbenddata.add(new Double(value1));

        //TODO data lines testing

        for (Entry<String, Object> e : parameterSet.entrySet()) {
            if (e.getKey().equals(bondkey))
                Assertions.assertEquals(bonddata, e.getValue());
            else if (e.getKey().equals(strbndkey))
                Assertions.assertEquals(strbnddata, e.getValue());
            else if (e.getKey().equals(anglekey))
                Assertions.assertEquals(angledata, e.getValue());
            else if (e.getKey().equals(torsionkey))
                Assertions.assertEquals(torsiondata, e.getValue());
            else if (e.getKey().equals(opbendkey)) Assertions.assertEquals(opbenddata, e.getValue());
        }
    }
}
