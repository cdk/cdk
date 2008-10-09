/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2000-2003  The Jmol Development Team
 * Copyright (C) 2003-2007  The CDK Project
 *
 * Contact: cdk-devel@lists.sf.net
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
import javax.vecmath.Vector3d;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PDBFormat;
import org.openscience.cdk.tools.FormatStringBuffer;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Saves small molecules in a rudimentary PDB format. It does not allow
 * writing of PDBProtein data structures.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.io.PDBWriterTest")
public class PDBWriter extends DefaultChemObjectWriter {

    final String SERIAL_FORMAT = "%5d";
    final String ATOM_NAME_FORMAT = "%-4s";
    final String POSITION_FORMAT = "%8.3f";	
	
    private BufferedWriter writer;
    
	public PDBWriter() {
		this(new StringWriter());
	}
	
    /**
     * Creates a PDB writer.
     * 
     * @param out the stream to write the PDB file to.
     */
    public PDBWriter(Writer out) {
    	try {
    		if (out instanceof BufferedWriter) {
                writer = (BufferedWriter)out;
            } else {
                writer = new BufferedWriter(out);
            }
        } catch (Exception exc) {
        }
    }

    public PDBWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }
    
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return PDBFormat.getInstance();
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
    
	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (ICrystal.class.equals(interfaces[i])) return true;
			if (IMolecule.class.equals(interfaces[i])) return true;
			if (IChemFile.class.equals(interfaces[i])) return true;
		}
    Class superClass = classObject.getSuperclass();
    if (superClass != null) return this.accepts(superClass);
		return false;
	}

    public void write(IChemObject object) throws CDKException {
        if (object instanceof IMolecule){
            writeMolecule((IMolecule)object);
        } else if (object instanceof ICrystal){
            writeCrystal((ICrystal)object);
        } else if (object instanceof IChemFile){
            IChemFile chemFile = (IChemFile)object;
            IChemSequence sequence = chemFile.getChemSequence(0);
            if (sequence != null) {
            	IChemModel model = sequence.getChemModel(0);
                if (model != null) {
                	ICrystal crystal = model.getCrystal();
                    if (crystal != null) {
                        write(crystal);
                    } else {
                    	Iterator containers = ChemModelManipulator.getAllAtomContainers(model).iterator();
                    	while (containers.hasNext()) {
                            writeMolecule(model.getBuilder().newMolecule(
                             	(IAtomContainer)containers.next()
                            ));
                    	}
                    }
                }
            }
        } else {
            throw new CDKException("Only supported is writing of Molecule, Crystal and ChemFile objects.");
        }
    }
    
   /**
    * Writes a single frame in PDB format to the Writer.
    *
    * @param molecule the Molecule to write
    */
   public void writeMolecule(IMolecule molecule) throws CDKException {
       
       try {
           int atomNumber = 1;
           
           String hetatmRecordName = "HETATM";
           String terRecordName = "TER";
           
           // Loop through the atoms and write them out:
           StringBuffer buffer = new StringBuffer();
           java.util.Iterator atoms = molecule.atoms().iterator();
           FormatStringBuffer fsb = new FormatStringBuffer("");
           while (atoms.hasNext()) {
               buffer.setLength(0);
               buffer.append(hetatmRecordName);
               fsb.reset(SERIAL_FORMAT).format(atomNumber);
               buffer.append(fsb.toString());
               buffer.append(' ');
               IAtom atom = (IAtom)atoms.next();
               fsb.reset(ATOM_NAME_FORMAT).format(atom.getSymbol());
               buffer.append(fsb.toString());
               buffer.append(" MOL          ");
               Point3d position = atom.getPoint3d();
               fsb.reset(POSITION_FORMAT).format(position.x);
               buffer.append(fsb.toString());
               fsb.reset(POSITION_FORMAT).format(position.y);
               buffer.append(fsb.toString());
               fsb.reset(POSITION_FORMAT).format(position.z);
               buffer.append(fsb.toString());
               
               writer.write(buffer.toString(), 0, buffer.length());
               writer.newLine();
               ++atomNumber;
           }
           writer.write(terRecordName, 0, terRecordName.length());
           writer.newLine();
       } catch (IOException exception) {
           throw new CDKException("Error while writing file: " + exception.getMessage(), exception);
       }
   }
   
   public void writeCrystal(ICrystal crystal) throws CDKException {
       try {
           writer.write("HEADER created with the CDK (http://cdk.sf.net/)");
           writer.newLine();
           Vector3d a = crystal.getA();
           Vector3d b = crystal.getB();
           Vector3d c = crystal.getC();
           double[] ucParams = CrystalGeometryTools.cartesianToNotional(a,b,c);
           final String LENGTH_FORMAT = "%4.3f";
           final String ANGLE_FORMAT = "%3.3f";
           FormatStringBuffer fsb = new FormatStringBuffer("");
           fsb.reset(LENGTH_FORMAT).format(ucParams[0]);
           writer.write("CRYST1 " + fsb.toString());
           fsb.reset(LENGTH_FORMAT).format(ucParams[1]);
           writer.write(fsb.toString());
           fsb.reset(LENGTH_FORMAT).format(ucParams[2]);
           writer.write(fsb.toString());
           fsb.reset(ANGLE_FORMAT).format(ucParams[3]);
           writer.write(fsb.toString());
           fsb.reset(ANGLE_FORMAT).format(ucParams[4]);
           writer.write(fsb.toString());
           fsb.reset(ANGLE_FORMAT).format(ucParams[4]);
           writer.write(fsb.toString());
           writer.newLine();
                                                                                                 
           // before saving the atoms, we need to create cartesian coordinates
           java.util.Iterator atoms = crystal.atoms().iterator();
           while (atoms.hasNext()) {
            	IAtom atom = (IAtom)atoms.next();
//            	logger.debug("PDBWriter: atom -> " + atom);
            	// if it got 3D coordinates, use that. If not, try fractional coordinates
            	if (atom.getPoint3d() == null && atom.getFractionalPoint3d() != null) {
            		Point3d frac = new Point3d(atom.getFractionalPoint3d());
                	Point3d cart = CrystalGeometryTools.fractionalToCartesian(a,b,c, frac);
                    atom.setPoint3d(cart);
            	}
            }
           writeMolecule(crystal.getBuilder().newMolecule(crystal));
       } catch (IOException exception) {
           throw new CDKException("Error while writing file: " + exception.getMessage(), exception);
       }
   }

   /**
     * Flushes the output and closes this object.
     */
   @TestMethod("testClose")
   public void close() throws IOException {
        writer.close();
    }

}
