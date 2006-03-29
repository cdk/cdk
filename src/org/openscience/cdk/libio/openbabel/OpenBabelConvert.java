/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLReader;

/**
 * <p>File writer thats convert input files with OpenBabel.
 * <p>And it has the option to obtain the file as ChemFile.
 * <p>First its necessary that you install correct Openbabel.
 * <p>It was tested with OpenBabel-1.100.2. More information in
 * http://openbabel.sourceforge.net/babel.shtml
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
 * @cdk module libio-openbabel
 */
public class OpenBabelConvert {

    /*Operating system name*/
    public static int SYSTEM_WINDOWS = 0;
    public static int SYSTEM_LINUX = 1;
    private static int SYSTEM;

    /*PATH of babel*/
    private String PATH = null;

    private IChemFile chemFile;

    /**
     * Constructor of the ConvertOpenBabel
     *
     * @param path String which set the path of the progam OpenBabel. It will necessary
     *             for windows systems.
     */
    public OpenBabelConvert(File path) {
        SYSTEM = getOperatingSystem();
        setPATH(path);

    }

    /**
     * set the path
     *
     * @param path String the path value
     */
    private void setPATH(File path) {
        if (!path.exists()) {
            System.out.println("The File-PAHT to load not exist: " + path.toString());
            System.exit(-1);
        }
        PATH = convertorFileToString(path);
        /*check if babel is installed correct, and it works*/
//		callBABEL(0,null,null,null);
    }

    /**
     * call the babel program
     *
     * @param TYPE_CALL  Option to make
     * @param file       File of the molecule
     * @param type       String type of molecule
     * @param addOptions Others options to
     */
    private void callBABEL(int TYPE_CALL,
                           String file, String type, String addOptions) {
        String[] args = null;
        switch (TYPE_CALL) {
            case 0: { /*test*/
                args = new String[1];
                args[0] = PATH;
                break;
            }
            case 1: { /*convert from "X" to cml without options*/
                args = new String[5];
                args[0] = PATH;
                args[1] = "-i" + type;
                args[2] = file;
                args[3] = "-ocml";
                args[4] = "data\u002Fmdl\u002Fmolecule_IN_MEMORY.cml";
                break;
            }
            case 2: { /*convert from "X" to cml with options*/
                args = new String[6];
                args[0] = PATH;
                args[1] = "-i" + type;
                args[2] = file;
                args[3] = "-ocml";
                args[4] = "data\u002Fmdl\u002Fmolecule_IN_MEMORY.cml";
                args[5] = addOptions;
                break;
            }
            case 3: { /*convert from cml to "X" without options*/
                args = new String[5];
                args[0] = PATH;
                args[1] = "-icml";
                args[2] = "data\u002Fmdl\u002Fmolecule_IN_MEMORY.cml";
                args[3] = "-o" + type;
                args[4] = file;
                break;
            }
            case 4: { /*convert from cml to "X" with options*/
                args = new String[6];
                args[0] = PATH;
                args[1] = "-icml";
                args[2] = "data\u002Fmdl\u002Fmolecule_IN_MEMORY.cml";
                args[3] = "-o" + type;
                args[4] = file;
                args[5] = addOptions;
                break;
            }
            default:
                ;
                break;
        }
        try {
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
            System.err.println("There is same problem with babel. Check: ");
            System.err.println("PATH: " + PATH);
        }
    }

    /**
     * set the Operating System of the machine
     * Now is only possible for:
     * -Windows
     * -Linux
     */
    public static int getOperatingSystem() {

        String systemString = System.getProperty("os.name").substring(0, 5);
        if (systemString.equals("Linux")) {
            return SYSTEM_LINUX;
        } else if (systemString.equals("Windo")) {
            return SYSTEM_WINDOWS;
        } else
            System.err.print("not system found");
        return SYSTEM_LINUX;
    }

    /**
     * Set the molecule to load for converting
     *
     * @param file File of the molecule
     * @param type String type of the molecule.
     *             It muss be the same convention as babel.
     */
    public void setInputFileToConvert(File file, String type, String addOptions) {
        if (!file.exists()) {
            System.out.println("The File-molecule to load not exist: " + file.toString());
            System.exit(0);
        } else {
            convertorFileToString(file);
            if (addOptions == null)
                callBABEL(1, convertorFileToString(file), type, addOptions);
            else
                callBABEL(2, convertorFileToString(file), type, addOptions);
            readCML();
        }


    }

    /**
     * convert the molecule
     *
     * @param file       File of the molecule
     * @param type       String type of the output
     * @param addOptions Additional options for the conversion
     * @see
     */
    public void convertTo(File file, String type, String addOptions) {
        convertorFileToString(file);
        if (addOptions == null)
            callBABEL(3, convertorFileToString(file), type, addOptions);
        else
            callBABEL(4, convertorFileToString(file), type, addOptions);

    }

    /**
     * read the created cml and read to chemFile
     */
    private void readCML() {
        String filename = "data/mdl/molecule_IN_MEMORY.cml";
        File file = new File(filename);
        try {
        	FileInputStream readerFile = new FileInputStream(file);
            CMLReader reader = new CMLReader(readerFile);
            chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * convert the string to string which recognizes babel
     *
     * @param file File value
     * @return String value
     */
    private String convertorFileToString(File file) {

        String fileString = "";
        int strlen = file.getAbsolutePath().length();
        String str = file.getAbsolutePath();

        for (int i = 0; i < strlen; i++) {
            if (Character.toString(str.charAt(i)).equals("/"))
                fileString = fileString + "\u002F";

            else
                fileString = fileString + str.charAt(i);
        }

        return fileString;
    }

    /**
     * get the ChemFile
     *
     * @return The ChemFile value
     */
    public IChemFile getChemFile() {
        return chemFile;
    }

    /**
     * resest the molecule_IN_MEMORY.cml to
     */
    public void resest() {
        File file = new File("data/mdl/molecule_IN_MEMORY.cml");
        if (file.exists())
            file.delete();
    }

    /**
     *
     * @param file File wich contains the path who babel is
     * @return Boolean, True if it exists
     */
    public static boolean hasOpenBabel(File file) {
        return file.exists();
	}
    
}


