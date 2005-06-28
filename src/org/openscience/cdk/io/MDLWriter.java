/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */
package org.openscience.cdk.io;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.*;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.*;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.tools.LoggingTool;



/**
 * Writes a molecule or an array of molecules to a MDL mol or SDF file.
 * For writing a MDL molfile you can this code:
 * <pre>
 * MDLWriter writer = new MDLWriter(new FileWriter(new File("output.mol")));
 * writer.write((Molecule)molecule);
 * writer.close();
 * </pre>
 *
 * See {@cdk.cite DAL92}.
 *
 * @cdk.module io
 *
 * @cdk.keyword file format, MDL molfile
 */
public class MDLWriter extends DefaultChemObjectWriter {

    private BufferedWriter writer;
    private LoggingTool logger;
    private IsotopeFactory isotopeFactory = null;
    private int moleculeNumber;
    public Map sdFields=null;

    /**
     * Contructs a new MDLWriter that can write an array of
     * Molecules to a given OutputStream.
     *
     * @param   out  The OutputStream to write to
     */
    public MDLWriter(OutputStream out) throws Exception {
        this(new BufferedWriter(new OutputStreamWriter(out)));
    }

    public ChemFormat getFormat() {
        return new MDLFormat();
    }
    
    /**
     * Here you can set a map which will be used to build sd fields in the file.
     * The entries will be translated to sd fields like this:br>
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
     * Contructs a new MDLWriter that can write an array of 
     * Molecules to a Writer.
     *
     * @param   out  The Writer to write to
     */
    public MDLWriter(Writer out) throws Exception {
        writer = new BufferedWriter(out);
        logger = new LoggingTool(this);
        try {
            isotopeFactory = IsotopeFactory.getInstance();
        } catch (Exception exception) {
            logger.error("Failed to initiate isotope factory: ", exception.getMessage());
            logger.debug(exception);
            if (exception instanceof CDKException) {
                throw exception;
            } else {
                throw new CDKException("Failed to initiate isotope factory: " + exception.getMessage());
            }
        }
        this.moleculeNumber = 1;
    }

    /**
     * Flushes the output and closes this object.
     */
    public void close() throws IOException {
        writer.close();
    }

    /**
     * Writes a ChemObject to the MDL molfile formated output. 
     * It can only output ChemObjects of type Molecule and
     * SetOfMolecules.
     *
     * @param object class must be of type Molecule or SetOfMolecules.
     *
     * @see org.openscience.cdk.ChemFile
     */
	public void write(ChemObject object) throws CDKException
	{
		if (object instanceof SetOfMolecules)
		{
		    writeSetOfMolecules((SetOfMolecules)object);
		}
		else if (object instanceof Molecule)
		{
			try{
        boolean[] isVisible=new boolean[((Molecule)object).getAtomCount()];
        for(int i=0;i<isVisible.length;i++){
          isVisible[i]=true;
        }
		    writeMolecule((Molecule)object,isVisible);
			}
			catch (Exception ex){
				logger.error(ex.getMessage());
				logger.debug(ex);
                throw new CDKException("Exception while writing MDL file: " + ex.getMessage());
			}
		}
		else
		{
		    throw new CDKException("Only supported is writing of ChemFile and Molecule objects.");
		}
	}
	
    public ChemObject highestSupportedChemObject() {
        return new SetOfMolecules();
    }

	/**
	 * Writes an array of Molecules to an OutputStream in MDL sdf format.
	 *
	 * @param   molecules  Array of Molecules that is written to an OutputStream 
	 */
	private void writeSetOfMolecules(SetOfMolecules som)
	{
		Molecule[] molecules = som.getMolecules();
		for (int i = 0; i < som.getMoleculeCount(); i++)
		{
			try
			{
        boolean[] isVisible=new boolean[molecules[i].getAtomCount()];
        for(int k=0;k<isVisible.length;k++){
          isVisible[k]=true;
        }
				writeMolecule(molecules[i], isVisible);
			}
			catch (Exception exc)
			{
			}
		}
	}
	
	

	

	/**
	 * Writes a Molecule to an OutputStream in MDL sdf format.
	 *
	 * @param   molecule  Molecule that is written to an OutputStream 
	 */
    public void writeMolecule(Molecule molecule) throws Exception {
        boolean[] isVisible=new boolean[molecule.getAtomCount()];
        for(int i=0;i<isVisible.length;i++){
          isVisible[i]=true;
        }
        writeMolecule(molecule, isVisible);
    }
    
	/**
	 * Writes a Molecule to an OutputStream in MDL sdf format.
	 *
	 * @param   molecule  Molecule that is written to an OutputStream
   * @param   isVisible Should a certain atom be written to mdl?
	 */
    public void writeMolecule(Molecule molecule, boolean[] isVisible) throws Exception {
        int Bonorder, stereo;
        String line = "";
        // taking care of the $$$$ signs:
        // we do not write such a sign at the end of the first molecule, thus we have to write on BEFORE the second molecule
        if(moleculeNumber == 2) {
          writer.write("$$$$");
          writer.newLine();
        }
        // write header block
        // lines get shortened to 80 chars, that's in the spec
        String title = (String)molecule.getProperty(CDKConstants.TITLE);
        if (title == null) title = "";
        if(title.length()>80)
          title=title.substring(0,80);
        writer.write(title + "\n");
        writer.write("  CDK\n");
        String comment = (String)molecule.getProperty(CDKConstants.REMARK);
        if (comment == null) comment = "";
        if(comment.length()>80)
          comment=comment.substring(0,80);
        writer.write(comment + "\n");
        
        // write Counts line
        int upToWhichAtom=0;
        for(int i=0;i<isVisible.length;i++){
          if(isVisible[i])
            upToWhichAtom++;
        }
        line += formatMDLInt(upToWhichAtom, 3);
        int numberOfBonds=0;
        if(upToWhichAtom<molecule.getAtomCount()){
          for(int i=0;i<molecule.getBondCount();i++){
            if(isVisible[molecule.getAtomNumber(molecule.getBondAt(i).getAtoms()[0])] && isVisible[molecule.getAtomNumber(molecule.getBondAt(i).getAtoms()[1])])
              numberOfBonds++;
          }
        }else{
          numberOfBonds=molecule.getBondCount();
        }
        line += formatMDLInt(numberOfBonds, 3);
        line += "  0  0  0  0  0  0  0  0999 V2000\n";
        writer.write(line);

        // write Atom block
        Atom[] atoms = molecule.getAtoms();
        for (int f = 0; f < atoms.length; f++) {
          if(isVisible[f]){
            Atom atom = atoms[f];
            line = "";
            if (atom.getPoint3d() != null) {
                line += formatMDLFloat((float) atom.getX3d());
                line += formatMDLFloat((float) atom.getY3d());
                line += formatMDLFloat((float) atom.getZ3d()) + " ";
            } else if (atom.getPoint2d() != null) {
                line += formatMDLFloat((float) atom.getX2d());
                line += formatMDLFloat((float) atom.getY2d());
                line += "    0.0000 ";
            } else {
                // if no coordinates available, then output a number
                // of zeros
                line += formatMDLFloat((float)0.0);
                line += formatMDLFloat((float)0.0);
                line += formatMDLFloat((float)0.0) + " ";
            }
            if(molecule.getAtomAt(f) instanceof PseudoAtom)
		    line += formatMDLString(((PseudoAtom) molecule.getAtomAt(f)).getLabel(), 3);
	    else
		    line += formatMDLString(molecule.getAtomAt(f).getSymbol(), 3); 
            line += " 0  0  0  0  0  0  0  0  0  0  0  0";
            writer.write(line);
            writer.newLine();
          }
        }

        // write Bond block
        Bond[] bonds = molecule.getBonds();
        for (int g = 0; g < bonds.length; g++) {
          if(upToWhichAtom==molecule.getAtomCount() || (isVisible[molecule.getAtomNumber(molecule.getBondAt(g).getAtoms()[0])] && isVisible[molecule.getAtomNumber(molecule.getBondAt(g).getAtoms()[1])])){
            Bond bond = bonds[g];
            if (bond.getAtoms().length != 2) {
                logger.warn("Skipping bond with more/less than two atoms: " + bond);
            } else {
                if (bond.getStereo() == CDKConstants.STEREO_BOND_UP_INV || 
                    bond.getStereo() == CDKConstants.STEREO_BOND_DOWN_INV) {
                    // turn around atom coding to correct for inv stereo
                    line = formatMDLInt(molecule.getAtomNumber(bond.getAtomAt(1)) + 1,3);
                    line += formatMDLInt(molecule.getAtomNumber(bond.getAtomAt(0)) + 1,3);
                } else {
                    line = formatMDLInt(molecule.getAtomNumber(bond.getAtomAt(0)) + 1,3);
                    line += formatMDLInt(molecule.getAtomNumber(bond.getAtomAt(1)) + 1,3);
                }
                if (bond.getFlag(CDKConstants.ISAROMATIC)) {
                    line += formatMDLInt(4,3);
                } else {
                    line += formatMDLInt((int)bond.getOrder(),3);
                }
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
        }

        // write formal atomic charges
        for (int i = 0; i < atoms.length; i++) {
            Atom atom = atoms[i];
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
        for (int i = 0; i < atoms.length; i++) {
            Atom atom = atoms[i];
            if (!(atom instanceof PseudoAtom)) {
                int atomicMass = atom.getMassNumber();
                int majorMass = isotopeFactory.getMajorIsotope(atom.getSymbol()).getMassNumber();
                if (atomicMass != 0 && atomicMass != majorMass) {
                    writer.write("M  ISO  1 ");
                    writer.write(formatMDLInt(i+1,3));
                    writer.write(" ");
                    writer.write(formatMDLInt(atomicMass,3));
                    writer.newLine();
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
            writer.write((String)sdFields.get(element));
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


