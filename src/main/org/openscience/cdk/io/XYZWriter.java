/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2002  Bradley A. Smith <bradley@baysmith.com>
 *               2002  Miguel Howard
 *               2003-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.XYZFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author  Bradley A. Smith <bradley@baysmith.com>
 * @author  J. Daniel Gezelter
 * @author  Egon Willighagen
 */
@TestClass("org.openscience.cdk.io.XYZWriterTest")
public class XYZWriter extends DefaultChemObjectWriter {
  
    private BufferedWriter writer;
    private LoggingTool logger;

    /**
    * Constructor.
    * 
    * @param out the stream to write the XYZ file to.
    */
    public XYZWriter(Writer out) {
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

    public XYZWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }
    
    public XYZWriter() {
        this(new StringWriter());
    }
    
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return XYZFormat.getInstance();
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
    @TestMethod("testClose")
    public void close() throws IOException {
    	writer.close();
    }
    
	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IMolecule.class.equals(interfaces[i])) return true;
		}
    Class superClass = classObject.getSuperclass();
    if (superClass != null) return this.accepts(superClass);
		return false;
	}

    public void write(IChemObject object) throws CDKException {
        if (object instanceof IMolecule) {
            try {
                writeMolecule((IMolecule)object);
            } catch(Exception ex) {
                throw new CDKException("Error while writing XYZ file: " + ex.getMessage(), ex);
            }
        } else {
            throw new CDKException("XYZWriter only supports output of Molecule classes.");
        }
    }

    /**
    * writes a single frame in XYZ format to the Writer.
    * @param mol the Molecule to write
    */
    public void writeMolecule(IMolecule mol) throws IOException {
        
        String st = "";
        boolean writecharge = true;
        
        try {
            
            String s1 = "" + mol.getAtomCount();
            writer.write(s1, 0, s1.length());
            writer.newLine();
            
            String s2 = null; // FIXME: add some interesting comment
            if (s2 != null) {
            	writer.write(s2, 0, s2.length());
            }
            writer.newLine();
            
            // Loop through the atoms and write them out:
            Iterator<IAtom> atoms = mol.atoms().iterator();
            while (atoms.hasNext()) {
            	IAtom a = atoms.next();
                st = a.getSymbol();
                
                Point3d p3 = a.getPoint3d();
                if (p3 != null) {
                    st = st + "\t" + new Double(p3.x).toString() + "\t"
                            + new Double(p3.y).toString() + "\t"
                            + new Double(p3.z).toString();
                }
                
                if (writecharge) {
                    double ct = a.getCharge() == CDKConstants.UNSET ? 0.0 : a.getCharge();
                    st = st + "\t" + ct;
                }
                
                writer.write(st, 0, st.length());
                writer.newLine();
                
            }
        } catch (IOException e) {
//            throw e;
            logger.error("Error while writing file: ", e.getMessage());
            logger.debug(e);
        }
    }
}


