/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.io.program;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.io.setting.*;
import org.openscience.cdk.exception.CDKException;
import javax.vecmath.Point3d;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

/**
 * File writer thats generates input files for Gaussian calculation
 * jobs. It was tested with Gaussian98.
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 *
 * @keyword Gaussian (tm), input file
 */
public class GaussianInputWriter extends DefaultChemObjectWriter {
  
    static BufferedWriter writer;

    IOSetting method;
    IOSetting basis;
    IOSetting comment;
    IOSetting command;
    BooleanIOSetting shell;
    IntegerIOSetting proccount;
    BooleanIOSetting usecheckpoint;
    
    /**
    * Constructs a new writer that produces input files to run a
    * Gaussian QM job.
    */
    public GaussianInputWriter(Writer out) {
        writer = new BufferedWriter(out);
        initIOSettings();
    }

    public void close() throws IOException {
        writer.close();
    }
    
    public void write(ChemObject object) throws CDKException {
        if (object instanceof Molecule) {
            try {
                writeMolecule((Molecule)object);
            } catch(Exception ex) {
                throw new CDKException("Error while writing Gaussian input file: " + ex.getMessage());
            }
        } else {
            throw new CDKException("GaussianInputWriter only supports output of Molecule classes.");
        }
    }

    public ChemObject highestSupportedChemObject() {
        return new Molecule();
    }
    
    /**
    * writes a single frame in XYZ format to the Writer.
    * @param cf the ChemFrame to write
    * @param w the Writer to write it to
    */
    public void writeMolecule(Molecule mol) throws IOException {
        
        customizeJob();
        
        // write extra statements
        if (proccount.getSettingValue() > 1) {
            writer.write("%nprocl=" + proccount.getSettingValue());
            writer.newLine();
        }
        if (usecheckpoint.isSet()) {
            if (mol.getID() != null && mol.getID().length() > 0) {
                writer.write("%chk=" + mol.getID() + ".chk");
            } else {
                // force different file names
                writer.write("%chk=" + System.currentTimeMillis() + ".chk");
            }
            writer.newLine();
        }
        
        // write the command line
        writer.write("# " + method.getSetting() + 
                     "/" + basis.getSetting() +  
                     " ");
        String commandString = command.getSetting();
        if (commandString.equals("energy calculation")) {
        } else if (commandString.equals("geometry optimization")) {
            writer.write("opt");
        } else if (commandString.equals("IR frequency calculation")) {
            writer.write("freq");
        }
        writer.newLine();
        
        // next line is empty
        writer.newLine();
        
        // next line is comment
        writer.write(comment.getSetting() + "\n");
        
        // next line is empty
        writer.newLine();

        /* next line contains two digits
         * the first is the total charge
         * the second is boolean indicating:
         *   0 = open shell
         *   1 = closed shell
         */
        writer.write("0 "); // FIXME: should write total charge of molecule
        if (shell.isSet()) {
            writer.write("0");
        } else {
            writer.write("1");
        }
        writer.newLine();
        
        // then come all the atoms. 
        // Loop through the atoms and write them out:
        Atom[] atoms = mol.getAtoms();
        for (int i = 0; i < atoms.length; i++) {
            Atom a = atoms[i];
            String st = a.getSymbol();
            
            // export Eucledian coordinates (indicated by the 0)
            st = st + " 0 ";
            
            // export the 3D coordinates
            Point3d p3 = a.getPoint3D();
            if (p3 != null) {
                st = st + new Double(p3.x).toString() + " "
                        + new Double(p3.y).toString() + " "
                        + new Double(p3.z).toString();
            }
            
            writer.write(st, 0, st.length());
            writer.newLine();
        }
        
        // G98 expects an empty line at the end
        writer.newLine();
    }
    
    private void initIOSettings() {
        Vector basisOptions = new Vector();
        basisOptions.add("6-31g");
        basisOptions.add("6-31g*");
        basisOptions.add("6-311g");
        basisOptions.add("6-311+g**");
        basis = new OptionIOSetting("Basis", IOSetting.MEDIUM,
          "Which basis set do you want to use?", basisOptions, "6-31g");

        Vector methodOptions = new Vector();
        methodOptions.add("b3lyp");
        methodOptions.add("rhf");
        method = new OptionIOSetting("Method", IOSetting.MEDIUM,
          "Which method do you want to use?", methodOptions, "b3lyp");
        
        Vector commandOptions = new Vector();
        commandOptions.add("energy calculation");
        commandOptions.add("geometry optimization");
        commandOptions.add("IR frequency calculation");
        command = new OptionIOSetting("Command", IOSetting.HIGH,
          "What kind of job do you want to perform?", commandOptions, 
          "energy calculation");
        
        comment = new StringIOSetting("Comment", IOSetting.LOW,
          "What comment should be put in the file?", 
          "Created with CDK (http://cdk.sf.net/)");
        
        shell = new BooleanIOSetting("OpenShell", IOSetting.MEDIUM,
          "Should the calculation be open shell?", 
          "false");

        proccount = new IntegerIOSetting("ProcessorCount", IOSetting.LOW,
          "How many processors should be used by Gaussian?", 
          "1");

        usecheckpoint = new BooleanIOSetting("UseCheckPointFile", IOSetting.LOW,
          "Should a check point file be saved?", 
          "false");
    }
    
    private void customizeJob() {
        fireIOSettingQuestion(basis);
        fireIOSettingQuestion(method);
        fireIOSettingQuestion(command);
        fireIOSettingQuestion(comment);
        fireIOSettingQuestion(shell);
        fireIOSettingQuestion(proccount);
        fireIOSettingQuestion(usecheckpoint);
    }
    
    public IOSetting[] getIOSettings() {
        IOSetting[] settings = new IOSetting[7];
        settings[0] = basis;
        settings[1] = method;
        settings[2] = command;
        settings[3] = comment;
        settings[4] = shell;
        settings[5] = proccount;
        settings[6] = usecheckpoint;
        return settings;
    }
}


