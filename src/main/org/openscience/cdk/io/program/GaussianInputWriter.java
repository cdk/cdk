/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2003-2008  Egon Willighagen <egonw@sci.kun.nl>
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
package org.openscience.cdk.io.program;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point3d;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.GaussianInputFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.IntegerIOSetting;
import org.openscience.cdk.io.setting.OptionIOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;

/**
 * File writer thats generates input files for Gaussian calculation
 * jobs. It was tested with Gaussian98.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 *
 * @cdk.keyword Gaussian (tm), input file
 */
@TestClass("org.openscience.cdk.io.program.GaussianInputWriterTest")
public class GaussianInputWriter extends DefaultChemObjectWriter {
  
    static BufferedWriter writer;
    
    IOSetting method;
    IOSetting basis;
    IOSetting comment;
    IOSetting command;
    IOSetting memory;
    BooleanIOSetting shell;
    IntegerIOSetting proccount;
    BooleanIOSetting usecheckpoint;
    
    /**
    * Constructs a new writer that produces input files to run a
    * Gaussian QM job.
    */
    public GaussianInputWriter(Writer out) {
    	try {
    		if (out instanceof BufferedWriter) {
                writer = (BufferedWriter)out;
            } else {
                writer = new BufferedWriter(out);
            }
        } catch (Exception exc) {
        }
        initIOSettings();
    }
    
    public GaussianInputWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }
    
    public GaussianInputWriter() {
        this(new StringWriter());
    }

    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return GaussianInputFormat.getInstance();
    }
    
    public void setWriter(Writer out) throws CDKException {
    	if (out instanceof BufferedWriter) {
            writer = (BufferedWriter)out;
        } else {
            writer = new BufferedWriter(out);
        }
    }

    public void setWriter(OutputStream output) throws CDKException {
    	setWriter(new OutputStreamWriter(output));
    }

    @TestMethod("testClose")
    public void close() throws IOException {
        writer.close();
    }
    
	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		if (IMolecule.class.isInstance(classObject)) return true;
		return false;
	}

	@TestMethod("testWrite")
    public void write(IChemObject object) throws CDKException {
        if (object instanceof IMolecule) {
            try {
                writeMolecule((IMolecule)object);
            } catch(Exception ex) {
                throw new CDKException("Error while writing Gaussian input file: " + ex.getMessage(), ex);
            }
        } else {
            throw new CDKException("GaussianInputWriter only supports output of Molecule classes.");
        }
    }
    
    /**
     * Writes a molecule for input for Gaussian.
     */
    public void writeMolecule(IMolecule mol) throws IOException {
        
        customizeJob();
        
        // write extra statements
        if (proccount.getSettingValue() > 1) {
            writer.write("%nprocl=" + proccount.getSettingValue());
            writer.newLine();
        }
        if (!memory.getSetting().equals("unset")) {
            writer.write("%Mem=" + memory.getSetting());
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
            // ok, no special command needed
        } else if (commandString.equals("geometry optimization")) {
            writer.write("opt");
        } else if (commandString.equals("IR frequency calculation")) {
            writer.write("freq");
        } else if (commandString.equals("IR frequency calculation (with Raman)")) {
            writer.write("freq=noraman");
        } else {
            // assume that user knows what he's doing
            writer.write(commandString);
        }
        writer.newLine();
        
        // next line is empty
        writer.newLine();
        
        // next line is comment
        writer.write(comment.getSetting());
        writer.newLine();
        
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
        Iterator<IAtom> atoms = mol.atoms().iterator();
        while (atoms.hasNext()) {
        	IAtom a = atoms.next();
            String st = a.getSymbol();
            
            // export Eucledian coordinates (indicated by the 0)
            st = st + " 0 ";
            
            // export the 3D coordinates
            Point3d p3 = a.getPoint3d();
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
        List<String> basisOptions = new ArrayList<String>();
        basisOptions.add("6-31g");
        basisOptions.add("6-31g*");
        basisOptions.add("6-31g(d)");
        basisOptions.add("6-311g");
        basisOptions.add("6-311+g**");
        basis = new OptionIOSetting("Basis", IOSetting.MEDIUM,
          "Which basis set do you want to use?", basisOptions, "6-31g");

        List<String> methodOptions = new ArrayList<String>();
        methodOptions.add("rb3lyp");
        methodOptions.add("b3lyp");
        methodOptions.add("rhf");
        method = new OptionIOSetting("Method", IOSetting.MEDIUM,
          "Which method do you want to use?", methodOptions, "b3lyp");
        
        List<String> commandOptions = new ArrayList<String>();
        commandOptions.add("energy calculation");
        commandOptions.add("geometry optimization");
        commandOptions.add("IR frequency calculation");
        commandOptions.add("IR frequency calculation (with Raman)");
        command = new OptionIOSetting("Command", IOSetting.HIGH,
          "What kind of job do you want to perform?", commandOptions, 
          "energy calculation");
        
        comment = new StringIOSetting("Comment", IOSetting.LOW,
          "What comment should be put in the file?", 
          "Created with CDK (http://cdk.sf.net/)");
        
        memory = new StringIOSetting("Memory", IOSetting.LOW,
          "How much memory do you want to use?", 
          "unset");
        
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
        fireIOSettingQuestion(memory);
        fireIOSettingQuestion(usecheckpoint);
    }
    
    public IOSetting[] getIOSettings() {
        IOSetting[] settings = new IOSetting[8];
        settings[0] = basis;
        settings[1] = method;
        settings[2] = command;
        settings[3] = comment;
        settings[4] = shell;
        settings[5] = proccount;
        settings[6] = usecheckpoint;
        settings[7] = memory;
        return settings;
    }
}


