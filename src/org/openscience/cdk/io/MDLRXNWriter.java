/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
import java.util.Locale;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.tools.LoggingTool;



/**
 * Writes a reaction to a MDL rxn or SDF file. Attention: Stoichiometric
 * coefficients have to be natural numbers.
 *
 * <pre>
 * MDLRXNWriter writer = new MDLRXNWriter(new FileWriter(new File("output.mol")));
 * writer.write((Molecule)molecule);
 * writer.close();
 * </pre>
 *
 * See {@cdk.cite DAL92}.
 *
 * @cdk.module io
 *
 * @cdk.keyword file format, MDL RXN file
 */
public class MDLRXNWriter extends DefaultChemObjectWriter {

	static BufferedWriter writer;
    private LoggingTool logger;

    
    /**
     * Contructs a new MDLWriter that can write an array of 
     * Molecules to a Writer.
     *
     * @param   out  The Writer to write to
     */
    public MDLRXNWriter(Writer out) throws Exception {
    	logger = new LoggingTool(this);
    	try {
    		if (out instanceof BufferedWriter) {
                writer = (BufferedWriter)out;
            } else {
                writer = new BufferedWriter(out);
            }
        } catch (Exception exc) {
        }
    }

    /**
     * Contructs a new MDLWriter that can write an array of
     * Molecules to a given OutputStream.
     *
     * @param   output  The OutputStream to write to
     */
    public MDLRXNWriter(OutputStream output) throws Exception {
        this(new BufferedWriter(new OutputStreamWriter(output)));
    }
    
    public MDLRXNWriter() throws Exception{
        this(new StringWriter());
    }

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
     * Flushes the output and closes this object.
     */
    public void close() throws IOException {
        writer.close();
    }

	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IReaction.class.equals(interfaces[i])) return true;
		}
		return false;
	}

	/**
     * Writes a IChemObject to the MDL RXN file formated output. 
     * It can only output ChemObjects of type Reaction
     *
     * @param object class must be of type Molecule or SetOfMolecules.
     *
     * @see org.openscience.cdk.ChemFile
     */
	public void write(IChemObject object) throws CDKException
	{
		if (object instanceof IReaction)
		{
		    writeReaction((IReaction)object);
		}
		else
		{
		    throw new CDKException("Only supported is writing Reaction objects.");
		}
	}
	
	/**
	 * Writes a Reaction to an OutputStream in MDL sdf format.
	 *
	 * @param   reaction  A Reaction that is written to an OutputStream 
	 */
	private void writeReaction(IReaction reaction) throws CDKException
	{
		int reactantCount = reaction.getReactantCount();
        int productCount = reaction.getProductCount();
        if (reactantCount <= 0 || productCount <= 0) {
            throw new CDKException("Either no reactants or no products present.");
        }
        
        try {
            writer.write("$RXN\n");
            // reaction name
            String line = (String)reaction.getProperty(CDKConstants.TITLE);
            if(line == null) line = "";
            if(line.length() > 80) line = line.substring(0,80);
            writer.write(line + "\n");
            // user/program/date&time/reaction registry no. line
            writer.newLine();
            // comment line
            line = (String)reaction.getProperty(CDKConstants.REMARK);
            if(line == null) line = "";
            if(line.length() > 80) line = line.substring(0,80);
            writer.write(line + "\n");
            
            line = "";
            line += formatMDLInt(reactantCount, 3);
            line += formatMDLInt(productCount, 3);
            writer.write(line + "\n");
            
            writeSetOfMolecules(reaction.getReactants());
            writeSetOfMolecules(reaction.getProducts());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            logger.debug(ex);
            throw new CDKException("Exception while writing MDL file: " + ex.getMessage(), ex);
        }
	}
	
    /**
	 * Writes a SetOfMolecules to an OutputStream for the reaction.
	 *
	 * @param   som  The SetOfMolecules that is written to an OutputStream 
	 */
	private void writeSetOfMolecules(IMoleculeSet som) throws IOException, CDKException {
        
        for (int i = 0; i < som.getMoleculeCount(); i++) {
        	IMolecule mol = som.getMolecule(i);
            for (int j = 0; j < som.getMultiplier(i); j++) {
                StringWriter sw = new StringWriter();
                writer.write("$MOL\n");
                MDLWriter mdlwriter = null;
                try {
                    mdlwriter = new MDLWriter(sw);
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    logger.debug(ex);
                    throw new CDKException("Exception while creating MDLWriter: " + ex.getMessage(), ex);
                }
                mdlwriter.write(mol);
                writer.write(sw.toString());
            }
        }
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
	
	

}


