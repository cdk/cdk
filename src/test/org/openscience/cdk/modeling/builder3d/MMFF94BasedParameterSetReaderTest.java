package org.openscience.cdk.modeling.builder3d;


import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomType;




/**
 * This class is for testing the MMFF94 based parameter
 * reading in CDK.
 * 
 * @author danielszisz
 * @see org.openscience.cdk.modeling.builder3d.MMFF94BasedParameterSetReader
 */
public class MMFF94BasedParameterSetReaderTest {
	
	private String line, atomline, bondline, angleline, strbndline, torsionline,
	               opbendline, dataline;
	private boolean firstatomline = true;
	private boolean firstbondline = true;
	private boolean firstangleline = true;
	private boolean firststrbndline = true;
	private boolean firsttorsionline = true;
	private boolean firstopbendline = true;
	private boolean firstdataline = true;
	
	@Deprecated
	public void initializeMMFF94ParameterFileLines() {
		String fname = "org/openscience/cdk/modeling/forcefield/data/mmff94.prm";
		ClassLoader loader = MMFF94BasedParameterSetReader.class.getClassLoader();
		InputStream inps = loader.getResourceAsStream(fname);
		BufferedReader r = new BufferedReader(new InputStreamReader(inps), 1024);
		try {
			while(true) {
		line = r.readLine();
		if(line == null) {
			break;
		}
		else if(line.startsWith("atom")) {
			if(firstatomline) { 
				atomline = line;
				firstatomline = false;
			}
			else continue;
		}
		else if(line.startsWith("bond")) {
			if(firstbondline) {
			bondline = line;
			firstbondline = false;
	   	}
			else continue;
		}
		else if(line.startsWith("angle")) {
			if(firstangleline) {
				angleline = line;
				firstangleline = false;
			}
			else continue;
		}
		else if(line.startsWith("strbnd")) { 
			if(firststrbndline) {
				strbndline = line;
				firststrbndline = false;
			}
		}
		else if(line.startsWith("torsion")) { 
			if(firsttorsionline) {
				torsionline = line; 
				firsttorsionline = false;
			}
		}
		else if(line.startsWith("opbend")) { 
			if(firstopbendline) {
				opbendline = line;
				firstopbendline = false;
			}
		}
		else if(line.startsWith("data")) { 
			if(firstdataline) {
				dataline = line;
				firstdataline = false;
			}
		}
	}
} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	@Test
	public void testreadParameterSets() {
		MMFF94BasedParameterSetReader mmff94bpsr = new MMFF94BasedParameterSetReader();
		try { 
			mmff94bpsr.readParameterSets();
		} catch(Exception e) {
			e.printStackTrace();
		}
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
		bonddata.add((Double) (new Double(slen).doubleValue()) );
		bonddata.add((Double) (new Double(sk2).doubleValue())  );
		bonddata.add((Double) (new Double(sk3).doubleValue())  );
		bonddata.add((Double) (new Double(sk4).doubleValue())  );
		bonddata.add((Double) (new Double(sbci).doubleValue())  );
		
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
	    String opbendkey = "opbend" + ";" + sid1+ ";" + sid2 + ";" + sid3 + ";" +sid4 ;
	    List<Double> opbenddata = new ArrayList<Double>();
	    opbenddata.add((Double) (new Double(value1).doubleValue()));
	    
	    //TODO data lines testing
	    
	    for(Entry<String, Object> e : parameterSet.entrySet()) {
			if(e.getKey().equals(bondkey)) assertEquals(bonddata, e.getValue());
			else if(e.getKey().equals(strbndkey)) assertEquals(strbnddata, e.getValue());
			else if(e.getKey().equals(anglekey)) assertEquals(angledata, e.getValue());
			else if(e.getKey().equals(torsionkey)) assertEquals(torsiondata, e.getValue());
			else if(e.getKey().equals(opbendkey)) assertEquals(opbenddata, e.getValue());
		}
		
		
		
		
		
	}
	
	
	

}
