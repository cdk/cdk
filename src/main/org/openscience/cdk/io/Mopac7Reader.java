/* Mopac7Reader.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-8 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
 * 
 * Contact: nina@acad.bg
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.mopac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;

import javax.vecmath.Point3d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.DefaultChemObjectReader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.LoggingTool;



/**
 * Reads MOPAC output, extracts several electronic parameters and assigns them as a molecule properties.<br>
 * parameters "NO. OF FILLED LEVELS",	"TOTAL ENERGY","FINAL HEAT OF FORMATION","IONIZATION POTENTIAL","ELECTRONIC ENERGY","CORE-CORE REPULSION","MOLECULAR WEIGHT"
 * Doesn't update structure coordinates ! (TODO fix) <br>
 * Used in {@link MopacShell} 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-8
 */
public class Mopac7Reader extends DefaultChemObjectReader {
    BufferedReader input = null;
    protected static LoggingTool logger = new LoggingTool(Mopac7Reader.class);
    public static String[] parameters = {
    		"NO. OF FILLED LEVELS",
        	"TOTAL ENERGY",
        	"FINAL HEAT OF FORMATION",
        	"IONIZATION POTENTIAL",
        	"ELECTRONIC ENERGY",
        	"CORE-CORE REPULSION",
        	"MOLECULAR WEIGHT",
        	DescriptorMopacShell.EHOMO,
        	DescriptorMopacShell.ELUMO};
    public static String[] units = {
		"",
    	"EV",
    	"KJ",
    	"",
    	"EV",
    	"EV",
    	"",
    	"EV",
    	"EV"};    
    protected static String eigenvalues = "EIGENVALUES";
    protected static String filledLevels = "NO. OF FILLED LEVELS";
    
    /**
     * Contructs a new Mopac7reader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public Mopac7Reader(Reader in) {
        input = new BufferedReader(in);

    }

    public Mopac7Reader(InputStream input) {
        this(new InputStreamReader(input));
    }
    /*
     *FINAL HEAT OF FORMATION =        -32.90826 KCAL =   -137.68818 KJ


          TOTAL ENERGY          =      -1618.31024 EV
          ELECTRONIC ENERGY       =      -6569.42640 EV  POINT GROUP:     C1  
          CORE-CORE REPULSION     =       4951.11615 EV

          IONIZATION POTENTIAL    =         10.76839
          NO. OF FILLED LEVELS    =         23
          MOLECULAR WEIGHT        =    122.123
 
     */
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectReader#read(IChemObject)
     */
    public IChemObject read(IChemObject arg0) throws CDKException {
    	final String[] expected_columns = { "NO.","ATOM","X","Y","Z"};
        StringBuffer eigenvalues = new StringBuffer();
        if (arg0 instanceof IAtomContainer) {
             IAtomContainer a = (IAtomContainer) arg0;
	         try {
	            String line = input.readLine();
	            while (line != null) {
	            	if (line.indexOf("****  MAX. NUMBER OF ATOMS ALLOWED")> -1) 
	            		throw new CDKException(line);
	            	if (line.indexOf("TO CONTINUE CALCULATION SPECIFY \"GEO-OK\"")> -1)
	            		throw new CDKException(line);
	            	if ("CARTESIAN COORDINATES".equals(line.trim())) {

	            		IAtomContainer atomcontainer = ((IAtomContainer)arg0);
	            		input.readLine(); //reads blank line
	            		line = input.readLine();
	            		
	            		String[] columns = line.trim().split(" +");
	            		int ok = 0;
	            		if (columns.length==expected_columns.length)
	            			for (int i=0; i < expected_columns.length;i++)
	            				ok += (columns[i].equals(expected_columns[i]))?1:0;

	            		if (ok < expected_columns.length) continue;
	            		//if (!"    NO.       ATOM         X         Y         Z".equals(line)) continue;

	            		input.readLine(); //reads blank line
                        int atomIndex = 0;
	                    while (!line.trim().equals("")) {
	                        line = input.readLine();
	                        StringTokenizer st = new StringTokenizer(line);
	                        int token = 0;

	                        IAtom atom = null;
	                        double[] point3d = new double[3];
	                        while (st.hasMoreTokens()) {
	                        	String t = st.nextToken();
	                        	switch (token) {
	                        	case 0: {
	                        		atomIndex = Integer.parseInt(t)-1;
	                        		if (atomIndex < atomcontainer.getAtomCount()) {
	                        			atom = atomcontainer.getAtom(atomIndex);
	                        		} else 
	                        			atom = null;
	                        		break;
	                        	}
	                        	case 1: {
	                        		if ((atom != null) && (!t.equals(atom.getSymbol())))
	                        			atom = null;
	                        		break;
	                        	}
	                        	case 2: {
	                        		point3d[0] = Double.parseDouble(t);
	                        		break;
	                        	}
	                        	case 3: {
	                        		point3d[1] = Double.parseDouble(t);
	                        		break;
	                        	}
	                        	case 4: {
	                        		point3d[2] = Double.parseDouble(t);
	                        		if (atom != null)
	                        			atom.setPoint3d(new Point3d(point3d));
	                        		break;
	                        	}

	                        	}
	                        	token++;
	                        	if (atom == null) break;
	                        }
	                        if ((atom == null) || ( (atomIndex+1) >= atomcontainer.getAtomCount()))
	                        	break;
	                        
	                    }
	            		
	            	} else if (line.indexOf(Mopac7Reader.eigenvalues) >= 0) {
	                    line = input.readLine();
	                    line = input.readLine();
	                    while (!line.trim().equals("")) {
	                        eigenvalues.append(line);
	                        line = input.readLine();
	                    }
	                    a.setProperty(Mopac7Reader.eigenvalues,eigenvalues.toString());
	                } else
	                    for (int i=0; i < parameters.length;i++)
	    		            if (line.indexOf(parameters[i]) >= 0) {
	    		            	String v = line.substring(line.lastIndexOf("=")+1).trim();
	    		            	
	    		            	/*
	    		            	v = v.replaceAll("EV","");
	    		            	v = v.replaceAll("KCAL","");
	    		            	v = v.replaceAll("KJ","");
	    		            	*/
	    		            	v = v.replaceAll(Mopac7Reader.units[i],"").trim();
	    		            	int p = v.indexOf(" ");
	    		            	if (p >= 0) 
	    		            		v = v.substring(0,p-1);
			                    a.setProperty(parameters[i],v.trim());
			                    break;
	    		            }    
	                line = input.readLine();
	            }
	            calcHomoLumo(a);
	            return a;
            } catch (Exception x) {
                throw new CDKException(x.getMessage());
            }
        } else return null;
    }
    protected void calcHomoLumo(IAtomContainer mol) {
        Object eig = mol.getProperty(eigenvalues);
        if (eig == null) return;
        //mol.getProperties().remove(eigenvalues);
        Object nfl = mol.getProperty(filledLevels);
        //mol.getProperties().remove(filledLevels);
        if (nfl == null) return;
        int n = 0;
        try {
            n = Integer.parseInt(nfl.toString());
        } catch (Exception x) {
            return;
        }
        String[] e = eig.toString().split("\\s");
        int m = 0;
        for (int i=0; i < e.length;i++) {
            if (e[i].trim().equals("")) continue;
            else 
                try {
                    double d = Double.parseDouble(e[i]);
                    m++;
                    if (m==n) {mol.setProperty(DescriptorMopacShell.EHOMO,e[i]); }
                    else if (m==(n+1)) {  mol.setProperty(DescriptorMopacShell.ELUMO,e[i]); }
                } catch (Exception x) {
                    return;
                }                
        }
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectReader#setReader(java.io.Reader)
     */
    public void setReader(Reader arg0) throws CDKException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectReader#setReader(java.io.InputStream)
     */
    public void setReader(InputStream arg0) throws CDKException {
        // TODO Auto-generated method stub

    }

 
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectIO#close()
     */
    public void close() throws IOException {
        input.close();

    }
            
    public String toString() {
    	return "MOPAC7 format";
    }
	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (IMoleculeSet.class.equals(interfaces[i])) return true;
			if (IMolecule.class.equals(interfaces[i])) return true;
		}
		return false;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        // TODO Auto-generated method stub
        return null;
    }
}
