/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2001-2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.ShelXFormat;
import org.openscience.cdk.tools.FormatStringBuffer;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * <p>Serializes a MoleculeSet or a Molecule object to ShelX code.
 * The output can be read with Platon.
 *
 * @cdk.module  extra
 * @cdk.svnrev  $Revision$
 *
 * @author Egon Willighagen
 *
 * @cdk.keyword file format, ShelX
 */
@TestClass("org.openscience.cdk.io.ShelXWriterTest")
public class ShelXWriter extends DefaultChemObjectWriter {

    private BufferedWriter writer;
	//private LoggingTool logger;

    /**
     * Constructs a new ShelXWriter class. Output will be stored in the Writer
     * class given as parameter.
     *
     * @param out Writer to redirect the output to.
     */
    public ShelXWriter(Writer out) {
    	//logger = new LoggingTool(this);
    	try {
    		if (out instanceof BufferedWriter) {
                writer = (BufferedWriter)out;
            } else {
                writer = new BufferedWriter(out);
            }
        } catch (Exception exc) {
        }
    }

    public ShelXWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }
    
    public ShelXWriter() {
        this(new StringWriter());
    }
    
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return ShelXFormat.getInstance();
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
     * Flushes the output and closes this object
     */
    @TestMethod("testClose")
    public void close() throws IOException {
    	writer.close();
    }

	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (ICrystal.class.equals(interfaces[i])) return true;
		}
		return false;
	}

    /**
     * Serializes the IChemObject to ShelX and redirects it to the output Writer.
     *
     * @param object A Molecule of MoleculeSet object
     */
    public void write(IChemObject object) throws CDKException {
        if (object instanceof ICrystal) {
            writeCrystal((ICrystal)object);
        } else {
            throw new CDKException("Only Crystal objects can be read.");
        }
    };

    // Private procedures

    private void writeCrystal(ICrystal crystal) {
        
        Object title = crystal.getProperty(CDKConstants.TITLE);
        if (title != null && title.toString().trim().length() > 0) {
            writeln("TITL " + title.toString().trim());
        } else {
            writeln("TITL Produced with CDK (http://cdk.sf.net/)");
        }
        Vector3d a = crystal.getA();
        Vector3d b = crystal.getB();
        Vector3d c = crystal.getC();
        double alength = a.length();
        double blength = b.length();
        double clength = c.length();
        double alpha = Math.toDegrees(b.angle(c));
        double beta  = Math.toDegrees(a.angle(c));
        double gamma = Math.toDegrees(a.angle(b));
        FormatStringBuffer format = new FormatStringBuffer("%7.5lf");
        write("CELL " + format.reset("%7.5f").format(1.54184).toString() + "   ");
        write(format.reset("%8.5f").format(alength) + "  ");
        write(format.reset("%8.5f").format(blength) + "  ");
        write(format.reset("%8.5f").format(clength) + " ");
        write(format.reset("%8.4f").format(alpha) + " ");
        write(format.reset("%8.4f").format(beta) + " ");
        writeln(format.reset("%8.4f").format(gamma) + "");
        writeln("ZERR " + format.reset("%1.5f").format((double)crystal.getZ()) +
              "    0.01000  0.01000   0.01000   0.0100   0.0100   0.0100");
        String spaceGroup = crystal.getSpaceGroup();
        if ("P1".equals(spaceGroup)) {
            writeln("LATT  -1");
        } else if ("P 2_1 2_1 2_1".equals(spaceGroup)) {
            writeln("LATT  -1");
            writeln("SYMM  1/2+X   , 1/2-Y   ,    -Z");
            writeln("SYMM     -X   , 1/2+Y   , 1/2-Z");
            writeln("SYMM  1/2-X   ,    -Y   , 1/2+Z");
        }
//        MFAnalyser mfa = new MFAnalyser(crystal);
        String elemNames = "";
        String elemCounts = "";
        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(crystal);
        List<IElement> asortedElements = MolecularFormulaManipulator.elements(formula);
        Iterator<IElement> elements = asortedElements.iterator();
        while (elements.hasNext()) {
        	IElement element = elements.next();
            String symbol = element.getSymbol();
            elemNames += symbol + "    ".substring(symbol.length());
            String countS = Integer.valueOf(MolecularFormulaManipulator.getElementCount(formula, element)).toString();
            elemCounts += countS + "    ".substring(countS.length());
        }
        writeln("SFAC  " + elemNames);
        writeln("UNIT  " + elemCounts);
        /* write atoms */
        for (int i = 0; i < crystal.getAtomCount(); i++) {
        	IAtom atom = crystal.getAtom(i);
            Point3d cartCoord = atom.getPoint3d();
            Point3d fracCoord = CrystalGeometryTools.cartesianToFractional(a, b, c, cartCoord);
            String symbol = atom.getSymbol();
            String output = symbol + (i+1);
            write(output);
            for (int j=1; j<5 - output.length(); j++) {
                write(" ");
            }
            write("     ");
            String elemID = Integer.valueOf(asortedElements.indexOf(symbol)+1).toString();
            write(elemID);
            write("    ".substring(elemID.length()));
            write(format.reset("%7.5f").format(fracCoord.x) + "   ");
            write(format.reset("%7.5f").format(fracCoord.y) + "   ");
            writeln(format.reset("%7.5f").format(fracCoord.z) + "    11.00000    0.05000");
        }
        writeln("END");
    }

    private void write(String s) {
        try {
        	writer.write(s);
        } catch (IOException e) {
            System.err.println("CMLWriter IOException while printing \"" +
                                s + "\":" + e.toString());
        }
    }

    private void writeln(String s) {
        try {
        	writer.write(s);
        	writer.newLine();
        } catch (IOException e) {
            System.err.println("CMLWriter IOException while printing \"" +
                                s + "\":" + e.toString());
        }
    }

}
