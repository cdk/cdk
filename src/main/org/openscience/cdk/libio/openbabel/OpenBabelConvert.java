/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.libio.openbabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.openscience.cdk.tools.LoggingTool;

/**
 * File writer thats convert input files with OpenBabel.
 * It has the option to obtain the file as ChemFile.
 * First, it's necessary that you install correct Openbabel.
 * It was tested with OpenBabel-1.100.2. More information in
 * <a href="http://openbabel.sourceforge.net/babel.shtml">http://openbabel.sourceforge.net/babel.shtml</a>.
 * 
 * <p>Currently supported types:
 * <table>
 * <tr>
 * <td>alc(Alchemy file)</td><td>prep(Amber PREP file)</td><td>bs(Ball & Stick file)</td>
 * </tr>
 * <tr>
 * <td>caccrt(Cacao Cartesian file)</td><td>cacint(Cacao Internal file)</td><td>cache(CAChe MolStruct file)</td>
 * </tr>
 * <tr>
 * <td>ccc(CCC file)</td><td>c3d1(Chem3D Cartesian 1 file)</td><td>c3d2(Chem3D Cartesian 2 file)</td>
 * </tr><tr>
 * <td>ct(ChemDraw Connection Table file)</td><td>cht(Chemtool file)</td><td>cml(Chemical Markup Language file)</td>
 * </tr><tr>
 * <td>crk2d(CRK2D: Chemical Resource Kit 2D file)</td><td>crk3d(CRK3D: Chemical Resource Kit 3D file)</td><td>cssr(CSD CSSR file)</td>
 * </tr><tr>
 * <td>box(Dock 3.5 Box file)</td><td>dmol(DMol3 Coordinates file)</td><td>feat(Feature file)</td>
 * </tr><tr>
 * <td>fh(Fenske-Hall Z-Matrix file)</td><td>gam(GAMESS Output file)</td><td>gamin(GAMESS Input file)</td>
 * </tr><tr>
 * <td>gamout(GAMESS Output file)</td><td>gcart(Gaussian Cartesian file)</td><td>gau(Gaussian Input file)</td>
 * </tr><tr>
 * <td>gpr(Ghemical Project file)</td><td>mm1gp(Ghemical MM file)</td><td>qm1gp(Ghemical QM file)</td>
 * </tr><tr>
 * <td>gr96a(GROMOS96(A)file)</td><td>gr96n(GROMOS96(nm)file)</td><td>hin(HyperChem HIN file)</td>
 * </tr><tr>
 * <td>jout(Jaguar Output file)</td><td>bin(OpenEye Binary file)</td><td>mmd(MacroModel file)</td>
 * </tr><tr>
 * <td>mmod(MacroModel file)</td><td>out(MacroModel file)</td><td>dat(MacroModel file)</td>
 * </tr><tr>
 * <td>car(MSI Biosym/Insight II CAR file)</td><td>sdf(MDL Isis SDF file)</td><td>sd(MDL Isis SDF file)</td>
 * </tr><tr>
 * <td>mdl(MDL Molfile file)</td><td>mol(MDL Molfile file)</td><td>mopcrt(MOPAC Cartesian file</td>
 * </tr><tr>
 * <td>mopout(MOPAC Output file)</td><td>mmads(MMADS file)</td><td>mpqc(MPQC file)</td>
 * </tr><tr>
 * <td>bgf(MSI BGF file)</td><td>nwo(NWChem Output file</td><td>pdb(PDB file)</td>
 * </tr><tr>
 * <td>ent(PDB file)</td><td>pqs(PQS file)</td><td>qcout(Q-Chem Output file)</td>
 * </tr><tr>
 * <td>res(ShelX file)</td><td>ins(ShelX file)</td><td>smi(SMILES file)</td>
 * </tr><tr>
 * <td>fix(SMILES Fix file)</td><td>report(Report file)</td><td>pov(POV-Ray Output file)</td>
 * </tr><tr>
 * </tr><tr>
 * <td>mol2(Sybyl Mol2 file)</td><td>unixyz(UniChem XYZ file)</td><td>vmol(ViewMol file)</td>
 * </tr><tr>
 * <td></td><td></td><td></td>
 * </tr><tr>
 * <td>xed(XED file)</td><td>xyz(XYZ file)</td><td>zin(ZINDO Input file)</td>
 * </tr><tr>
 * <td></td><td></td><td></td>
 * </tr>
 * </table>
 *
 * @author Miguel Rojas <miguelrojasch@uni-koeln.de>
 * @cdk.module libio-openbabel
 * @cdk.svnrev  $Revision$
 */
public class OpenBabelConvert {

    /* PATH to babel */
    private String pathToBabel = null;
    
    private final static LoggingTool logger = new LoggingTool(OpenBabelConvert.class);

    /**
     * Constructor of the ConvertOpenBabel
     *
     * @param path String which set the path of the progam OpenBabel. It will necessary
     *             for windows systems.
     */
    public OpenBabelConvert() throws Exception {
        this(null);
    }

    /**
     * Constructor of the ConvertOpenBabel
     *
     * @param path String which set the path of the progam OpenBabel. It will necessary
     *             for windows systems.
     */
    public OpenBabelConvert(String path) throws Exception {
        pathToBabel = getPath(path);
    }

    /**
     * Call the babel program.
     */
    public void convert(File inputFile, String inputType,
    					   File outputFile, String outputType,
    					   String addOptions) {
        try {
            String[] args = new String[6];
            args[0] = pathToBabel;
            args[1] = "-i" + inputType;
            args[2] = inputFile.getCanonicalPath();
            args[3] = "-o" + outputType;
            args[4] = outputFile.getCanonicalPath();
            args[5] = addOptions == null ? "" : addOptions;

            Process p = Runtime.getRuntime().exec(args);
            BufferedReader r = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String x;
            while ((x = r.readLine()) != null) {
                System.out.println(x);
            }
            r.close();
            p.waitFor();
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("There is some problem with babel. Check: ");
            System.err.println("PATH: " + pathToBabel);
        }
    }

    /**
     * Searches the babel executable from a set up reasonable picks.
     * 
     * @param suggestedPath
     * @return
     */
    private static String getPath(String suggestedPath) throws Exception {
    	if (suggestedPath != null) {
    		File suggestion = new File(suggestedPath);
    		if (suggestion.exists()) {
    			return suggestedPath;
    		}
    	}
    	String[] possibilities = {
    		"C:/Programme/openbabel-2.0.0awins/babel.exe", // likely??
    		"/usr/bin/babel", // most POSIX systems
    		"/usr/local/bin/babel" // private installation
    	};
    	File path = null;
    	for (int i=0; i<possibilities.length; i++) {
    		path = new File(possibilities[i]);
    	    if (path.exists()) {
    	    	logger.info("Babel executable found at: " + possibilities[i]);
    	    	return possibilities[i];
    	    }
        }
    	
    	throw new Exception("Cannot find the babel executable.");
    }
}


