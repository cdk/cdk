/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
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
 * 
 */
package org.openscience.cdk.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * Writes MDL mol files and SD files.
 * <BR><BR>
 * A MDL mol file contains a single molecule, whereas a MDL SD file contains
 * one or more molecules. This class is capable of writing both mol files and
 * SD files. The correct format is automatically chosen:
 * <ul>
 * <li>if {@link #write(IChemObject)} is called with a {@link org.openscience.cdk.MoleculeSet MoleculeSet}
 * as an argument a SD files is written</li>
 * <li>if one of the two writeMolecule methods (either {@link #writeMolecule(IMolecule) this one} or
 * {@link #writeMolecule(org.openscience.cdk.interfaces.IMolecule)} that one}) is called the first time, a mol file is written</li>
 * <li>if one of the two writeMolecule methods is called more than once the output is a SD file</li>
 * </ul>
 * 
 * <p>Thus, to write several molecules to a single SD file you can either use {@link #write(IChemObject)} and pass
 * a {@link org.openscience.cdk.MoleculeSet MoleculeSet} or you can repeatedly call one of the two
 * writeMolecule methods.
 * <p>For writing a MDL molfile you can this code:
 * <pre>
 * MDLWriter writer = new MDLWriter(new FileWriter(new File("output.mol")));
 * writer.write((Molecule)molecule);
 * writer.close();
 * </pre>
 *
 * See {@cdk.cite DAL92}.
 *
 * @cdk.module  io
 * @cdk.svnrev  $Revision$
 * @cdk.keyword file format, MDL molfile
 * @cdk.bug     1524466
 */
@TestClass("org.openscience.cdk.io.MDLWriterTest")
public class MDLWriter extends DefaultChemObjectWriter {

    private BufferedWriter writer;
    private LoggingTool logger;
    private int moleculeNumber;
    public Map sdFields=null;
    //private boolean writeAromatic=true;
    

    
    /**
     * Contructs a new MDLWriter that can write an array of 
     * Molecules to a Writer.
     *
     * @param   out  The Writer to write to
     */
    public MDLWriter(Writer out) {
    	logger = new LoggingTool(this);
    	try {
    		if (out instanceof BufferedWriter) {
                writer = (BufferedWriter)out;
            } else {
                writer = new BufferedWriter(out);
            }
        } catch (Exception exc) {
        }
        this.moleculeNumber = 1;
    }

    /**
     * Contructs a new MDLWriter that can write an array of
     * Molecules to a given OutputStream.
     *
     * @param   output  The OutputStream to write to
     */
    public MDLWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }
    
    public MDLWriter() {
        this(new StringWriter());
    }

    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return MDLFormat.getInstance();
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
    
    /**
     * 
     * Method does not do anything until now.
     *
     */
    public void dontWriteAromatic(){
      //writeAromatic=false;
    }
    
    /**
     * Here you can set a map which will be used to build sd fields in the file.
     * The entries will be translated to sd fields like this:<br>
     * &gt; &lt;key&gt;<br>
     * &gt; value<br>
     * empty line<br>
     *
     * @param  map The map to be used, map of String-String pairs
     */
    public void setSdFields(Map map){
      sdFields=map;
    }
    
    /**
     * Flushes the output and closes this object.
     */
    @TestMethod("testClose")
    public void close() throws IOException {
        writer.close();
    }

	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IMolecule.class.equals(interfaces[i])) return true;
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (IChemModel.class.equals(interfaces[i])) return true;
			if (IMoleculeSet.class.equals(interfaces[i])) return true;
		}
		return false;
	}

    /**
     * Writes a IChemObject to the MDL molfile formated output. 
     * It can only output ChemObjects of type ChemFile, Molecule and
     * MoleculeSet.
     *
     * @param object class must be of type ChemFile, Molecule or MoleculeSet.
     *
     * @see org.openscience.cdk.ChemFile
     */
	public void write(IChemObject object) throws CDKException {
		try {
			if (object instanceof IMoleculeSet) {
				writeMoleculeSet((IMoleculeSet)object);
				return;
			} else if (object instanceof IChemFile) {
				writeChemFile((IChemFile)object);
				return;
			} else if (object instanceof IChemModel) {
				IChemFile file = object.getBuilder().newChemFile();
				IChemSequence sequence = object.getBuilder().newChemSequence();
				sequence.addChemModel((IChemModel)object);
				file.addChemSequence(sequence);
				writeChemFile((IChemFile)file);
				return;
			} else if (object instanceof IMolecule) {
				writeMolecule((IMolecule)object);
				return;
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug(ex);
			throw new CDKException("Exception while writing MDL file: " + ex.getMessage(), ex);
		}
		throw new CDKException("Only supported is writing of ChemFile, MoleculeSet, AtomContainer and Molecule objects.");
	}
	
	/**
	 * Writes an array of Molecules to an OutputStream in MDL sdf format.
	 *
	 * @param   som  Array of Molecules that is written to an OutputStream
	 */
	private void writeMoleculeSet(IMoleculeSet som)
	{
		java.util.Iterator molecules = som.molecules().iterator();
		while (molecules.hasNext())
		{
			IMolecule mol = (IMolecule)molecules.next();
			try
			{
				boolean[] isVisible=new boolean[mol.getAtomCount()];
				for(int k=0;k<isVisible.length;k++){
					isVisible[k]=true;
				}
				writeMolecule(mol);
			}
			catch (Exception exc)
			{
			}
		}
	}
	
	private void writeChemFile(IChemFile file) throws Exception {
		List moleculesList = ChemFileManipulator.getAllAtomContainers(file);
		for (int i=0; i<moleculesList.size(); i++) {
			writeMolecule(file.getBuilder().newMolecule((IAtomContainer)moleculesList.get(i)));
		}
	}
	

	/**
	 * Writes a Molecule to an OutputStream in MDL sdf format.
	 *
	 * @param   container  Molecule that is written to an OutputStream
	 */
    public void writeMolecule(IMolecule container) throws Exception {
        String line = "";
        // taking care of the $$$$ signs:
        // we do not write such a sign at the end of the first molecule, thus we have to write on BEFORE the second molecule
        if(moleculeNumber == 2) {
          writer.write("$$$$");
          writer.newLine();
        }
        // write header block
        // lines get shortened to 80 chars, that's in the spec
        String title = (String)container.getProperty(CDKConstants.TITLE);
        if (title == null) title = "";
        if(title.length()>80)
          title=title.substring(0,80);
        writer.write(title);
        writer.newLine();
        
        /* From CTX spec
         * This line has the format:
         * IIPPPPPPPPMMDDYYHHmmddSSssssssssssEEEEEEEEEEEERRRRRR
         * (FORTRAN: A2<--A8--><---A10-->A2I2<--F10.5-><---F12.5--><-I6-> )
         * User's first and last initials (l), program name (P),
         * date/time (M/D/Y,H:m), dimensional codes (d), scaling factors (S, s), 
         * energy (E) if modeling program input, internal registry number (R) 
         * if input through MDL form.
         * A blank line can be substituted for line 2.
         */
        writer.write("  CDK    ");
        writer.write(new SimpleDateFormat("M/d/y,H:m",Locale.US).format(
        		     Calendar.getInstance(TimeZone.getDefault()).getTime())
        );
        writer.newLine();
        
        String comment = (String)container.getProperty(CDKConstants.REMARK);
        if (comment == null) comment = "";
        if(comment.length()>80)
          comment=comment.substring(0,80);
        writer.write(comment);
        writer.newLine();
        
        // write Counts line
        line += formatMDLInt(container.getAtomCount(), 3);
        line += formatMDLInt(container.getBondCount(), 3);
        line += "  0  0  0  0  0  0  0  0999 V2000";
        writer.write(line);
        writer.newLine();

        // write Atom block
        for (int f = 0; f < container.getAtomCount(); f++) {
        	IAtom atom = container.getAtom(f);
        	line = "";
        	if (atom.getPoint3d() != null) {
        		line += formatMDLFloat((float) atom.getPoint3d().x);
        		line += formatMDLFloat((float) atom.getPoint3d().y);
        		line += formatMDLFloat((float) atom.getPoint3d().z) + " ";
        	} else if (atom.getPoint2d() != null) {
        		line += formatMDLFloat((float) atom.getPoint2d().x);
        		line += formatMDLFloat((float) atom.getPoint2d().y);
        		line += "    0.0000 ";
        	} else {
        		// if no coordinates available, then output a number
        		// of zeros
        		line += formatMDLFloat((float)0.0);
        		line += formatMDLFloat((float)0.0);
        		line += formatMDLFloat((float)0.0) + " ";
        	}
        	if(container.getAtom(f) instanceof IPseudoAtom){
        		//according to http://www.google.co.uk/url?sa=t&ct=res&cd=2&url=http%3A%2F%2Fwww.mdl.com%2Fdownloads%2Fpublic%2Fctfile%2Fctfile.pdf&ei=MsJjSMbjAoyq1gbmj7zCDQ&usg=AFQjCNGaJSvH4wYy4FTXIaQ5f7hjoTdBAw&sig2=eSfruNOSsdMFdlrn7nhdAw an R group is written as R#
        		if(((IPseudoAtom) container.getAtom(f)).getLabel().equals("R"))
        			line += "R#";
        		else
        			line += formatMDLString(((IPseudoAtom) container.getAtom(f)).getLabel(), 3);
        	}else{
        		line += formatMDLString(container.getAtom(f).getSymbol(), 3);
        	}
        	line += " 0  0  0  0  0  0  0  0  0  0  0  0";
        	writer.write(line);
        	writer.newLine();
        }

        // write Bond block
        Iterator bonds = container.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();

        	if (bond.getAtomCount() != 2) {
        		logger.warn("Skipping bond with more/less than two atoms: " + bond);
        	} else {
        		if (bond.getStereo() == CDKConstants.STEREO_BOND_UP_INV || 
        				bond.getStereo() == CDKConstants.STEREO_BOND_DOWN_INV) {
        			// turn around atom coding to correct for inv stereo
        			line = formatMDLInt(container.getAtomNumber(bond.getAtom(1)) + 1,3);
        			line += formatMDLInt(container.getAtomNumber(bond.getAtom(0)) + 1,3);
        		} else {
        			line = formatMDLInt(container.getAtomNumber(bond.getAtom(0)) + 1,3);
        			line += formatMDLInt(container.getAtomNumber(bond.getAtom(1)) + 1,3);
        		}
        		line += formatMDLInt((int)bond.getOrder().ordinal()+1,3);
        		line += "  ";
        		switch(bond.getStereo()){
        		case CDKConstants.STEREO_BOND_UP:
        			line += "1";
        			break;
        		case CDKConstants.STEREO_BOND_UP_INV:
        			line += "1";
        			break;
        		case CDKConstants.STEREO_BOND_DOWN:
        			line += "6";
        			break;
        		case CDKConstants.STEREO_BOND_DOWN_INV:
        			line += "6";
        			break;
        		default:
        			line += "0";
        		}
        		line += "  0  0  0 ";
        		writer.write(line);
        		writer.newLine();
        	}
        }

        // write formal atomic charges
        for (int i = 0; i < container.getAtomCount(); i++) {
        	IAtom atom = container.getAtom(i);
            int charge = atom.getFormalCharge();
            if (charge != 0) {
                writer.write("M  CHG  1 ");
                writer.write(formatMDLInt(i+1,3));
                writer.write(" ");
                writer.write(formatMDLInt(charge,3));
                writer.newLine();
            }
        }
        
        // write formal isotope information
        for (int i = 0; i < container.getAtomCount(); i++) {
        	IAtom atom = container.getAtom(i);
            if (!(atom instanceof IPseudoAtom)) {
                Integer atomicMass = atom.getMassNumber();
                if (atomicMass != null) {
                	int majorMass = IsotopeFactory.getInstance(atom.getBuilder()).getMajorIsotope(atom.getSymbol()).getMassNumber();
                	if (atomicMass != majorMass) {
                		writer.write("M  ISO  1 ");
                		writer.write(formatMDLInt(i+1,3));
                		writer.write(" ");
                		writer.write(formatMDLInt(atomicMass,3));
                		writer.newLine();
                	}
                }
            }
        }
        
        // close molecule
        writer.write("M  END");
        writer.newLine();
        //write sdfields, if any
        if(sdFields!=null){
          Set set = sdFields.keySet();
          Iterator iterator = set.iterator();
          while (iterator.hasNext()) {
            Object element = iterator.next();
            writer.write("> <"+(String)element+">");
            writer.newLine();
            writer.write(sdFields.get(element).toString());
            writer.newLine();
            writer.newLine();
          }
        }
        // taking care of the $$$$ signs:
        // we write such a sign at the end of all except the first molecule
        if(moleculeNumber != 1) {
          writer.write("$$$$");
          writer.newLine();
        }
        moleculeNumber++;
        writer.flush();
    }

	/**
	 * Formats an int to fit into the connectiontable and changes it 
     * to a String.
	 *
	 * @param   i  The int to be formated
	 * @param   l  Length of the String
	 * @return     The String to be written into the connectiontable
	 */
    private String formatMDLInt(int i, int l) {
        String s = "", fs = "";
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        nf.setParseIntegerOnly(true);
        nf.setMinimumIntegerDigits(1);
        nf.setMaximumIntegerDigits(l);
        nf.setGroupingUsed(false);
        s = nf.format(i);
        l = l - s.length();
        for (int f = 0; f < l; f++)
            fs += " ";
        fs += s;
        return fs;
    }
	
	


	/**
	 * Formats a float to fit into the connectiontable and changes it
     * to a String.
	 *
	 * @param   fl  The float to be formated
	 * @return      The String to be written into the connectiontable
	 */
    private String formatMDLFloat(float fl) {
        String s = "", fs = "";
        int l;
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        nf.setMinimumIntegerDigits(1);
        nf.setMaximumIntegerDigits(4);
        nf.setMinimumFractionDigits(4);
        nf.setMaximumFractionDigits(4);
        nf.setGroupingUsed(false);
        s = nf.format(fl);
        l = 10 - s.length();
        for (int f = 0; f < l; f++)
            fs += " ";
        fs += s;
        return fs;
    }



	/**
	 * Formats a String to fit into the connectiontable.
	 *
	 * @param   s    The String to be formated
	 * @param   le   The length of the String
	 * @return       The String to be written in the connectiontable
	 */
    private String formatMDLString(String s, int le) {
        s = s.trim();
        if (s.length() > le)
            return s.substring(0, le);
        int l;
        l = le - s.length();
        for (int f = 0; f < l; f++)
            s += " ";
        return s;
    }

}


