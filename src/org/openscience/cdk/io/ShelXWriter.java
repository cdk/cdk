/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2004  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.MFAnalyser;

import freeware.PrintfFormat;

/**
 * <p>Serializes a SetOfMolecules or a Molecule object to ShelX code.
 * The output can be read with Platon.
 *
 * @cdk.module io
 *
 * @author Egon Willighagen
 *
 * @cdk.keyword file format, ShelX
 */
public class ShelXWriter extends DefaultChemObjectWriter {

    private Writer output;

    /**
     * Constructs a new ShelXWriter class. Output will be stored in the Writer
     * class given as parameter.
     *
     * @param out Writer to redirect the output to.
     */
    public ShelXWriter(Writer out) {
        output = out;
    }

    public String getFormatName() {
        return "ShelX";
    }
    
    /**
     * Flushes the output and closes this object
     */
    public void close() throws IOException {
        output.close();
    }

    /**
     * Serializes the ChemObject to ShelX and redirects it to the output Writer.
     *
     * @param object A Molecule of SetOfMolecules object
     */
    public void write(ChemObject object) throws CDKException {
        if (object instanceof Crystal) {
            write((Crystal)object);
        } else {
            throw new CDKException("Only Crystal objects can be read.");
        }
    };

    public ChemObject highestSupportedChemObject() {
        return new Crystal();
    }

    // Private procedures

    private void write(Crystal crystal) {
        write("TITLE Produced with CDK (http://cdk.sf.net/)\n");
        double[] a = crystal.getA();
        double[] b = crystal.getB();
        double[] c = crystal.getC();
        double alength = calcLengthAxis(a);
        double blength = calcLengthAxis(b);
        double clength = calcLengthAxis(c);
        double alpha = calcAxesAngle(b, c);
        double beta  = calcAxesAngle(a, c);
        double gamma = calcAxesAngle(a, b);
        PrintfFormat format = new PrintfFormat("%7.5lf");
        write("CELL " + format.sprintf(1.54184) + "   ");
        format = new PrintfFormat("%8.5lf");
        write(format.sprintf(alength) + "  ");
        write(format.sprintf(blength) + "  ");
        write(format.sprintf(clength) + " ");
        format = new PrintfFormat("%8.4lf");
        write(format.sprintf(alpha) + " ");
        write(format.sprintf(beta) + " ");
        write(format.sprintf(gamma) + "\n");
        format = new PrintfFormat("%1.5lf");
        write("ZERR " + format.sprintf((double)crystal.getZ()) +
              "    0.01000  0.01000   0.01000   0.0100   0.0100   0.0100\n");
        String spaceGroup = crystal.getSpaceGroup();
        if ("P1".equals(spaceGroup)) {
            write("LATT  -1\n");
        } else if ("P 2_1 2_1 2_1".equals(spaceGroup)) {
            write("LATT  -1\n");
            write("SYMM  1/2+X   , 1/2-Y   ,    -Z\n");
            write("SYMM     -X   , 1/2+Y   , 1/2-Z\n");
            write("SYMM  1/2-X   ,    -Y   , 1/2+Z\n");
        }
        MFAnalyser mfa = new MFAnalyser(crystal);
        String elemNames = "";
        String elemCounts = "";
        Vector asortedElements = mfa.getElements();
        Enumeration elements = asortedElements.elements();
        while (elements.hasMoreElements()) {
            String symbol = (String)elements.nextElement();
            elemNames += symbol + "    ".substring(symbol.length());
            String countS = new Integer(mfa.getAtomCount(symbol)).toString();
            elemCounts += countS + "    ".substring(countS.length());
        }
        write("SFAC  " + elemNames + "\n");
        write("UNIT  " + elemCounts + "\n");
        /* write atoms */
        format = new PrintfFormat("%7.5lf");
        for (int i = 0; i < crystal.getAtomCount(); i++) {
            Atom atom = crystal.getAtomAt(i);
            double[] reals = new double[3];
            reals[0] = atom.getX3D();
            reals[1] = atom.getY3D();
            reals[2] = atom.getZ3D();
            double[] fracs = realToFractional(reals, a, b, c);
            String symbol = atom.getSymbol();
            String output = symbol + (i+1);
            write(output);
            for (int j=1; j<5 - output.length(); j++) {
                write(" ");
            }
            write("     ");
            String elemID = new Integer(asortedElements.indexOf(symbol)+1).toString();
            write(elemID);
            write("    ".substring(elemID.length()));
            write(format.sprintf(fracs[0]) + "   ");
            write(format.sprintf(fracs[1]) + "   ");
            write(format.sprintf(fracs[2]) + "    11.00000    0.05000\n");
        }
        write("END\n");
    }

    private void write(String s) {
        try {
            output.write(s);
        } catch (IOException e) {
            System.err.println("CMLWriter IOException while printing \"" +
                                s + "\":\n" + e.toString());
        }
    }

    /** No checking whatsoever: use with care */
    private double[] fractionalToReal(double[] fracs, double[] a, double[] b, double[] c) {
        double[] reals = new double[3];
        /* xr = ax*xf + bx*xf + cx*xf */
        reals[0] = a[0]*fracs[0] + b[0]*fracs[0] + c[0]*fracs[0];
        /* yr = ay*yf + by*yf + cy*yf */
        reals[1] = a[1]*fracs[1] + b[1]*fracs[1] + c[1]*fracs[1];
        /* zr = az*zf + bz*zf + cz*zf */
        reals[2] = a[2]*fracs[2] + b[2]*fracs[2] + c[2]*fracs[2];
        
        return reals;
    }

    private double[] realToFractional(double[] reals, double[] a, double[] b, double[] c) {
        double[] fracs = new double[3];

        double[][] invaxis = new double[3][3];
        double det = a[0]*b[1]*c[2] - a[0]*b[2]*c[1] -
                     a[1]*b[0]*c[2] + a[1]*b[2]*c[0] +
                     a[2]*b[0]*c[1] - a[2]*b[1]*c[0];
        invaxis[0][0] = (b[1]*c[2] - b[2]*c[1])/det;
        invaxis[0][1] = (b[2]*c[0] - b[0]*c[2])/det;
        invaxis[0][2] = (b[0]*c[1] - b[1]*c[0])/det;

        invaxis[1][0] = (a[2]*c[1] - a[1]*c[2])/det;
        invaxis[1][1] = (a[0]*c[2] - a[2]*c[0])/det;
        invaxis[1][2] = (a[1]*c[0] - a[0]*c[1])/det;

        invaxis[2][0] = (a[1]*b[2] - a[2]*b[1])/det;
        invaxis[2][1] = (a[2]*b[0] - a[0]*b[2])/det;
        invaxis[2][2] = (a[0]*b[1] - a[1]*b[0])/det;

        fracs[0] = invaxis[0][0]*reals[0] + invaxis[0][1]*reals[1] + invaxis[0][2]*reals[2];
        fracs[1] = invaxis[1][0]*reals[0] + invaxis[1][1]*reals[1] + invaxis[1][2]*reals[2];
        fracs[2] = invaxis[2][0]*reals[0] + invaxis[2][1]*reals[1] + invaxis[2][2]*reals[2];

        return fracs;
    }
    
    /**
     * Calculate the length of an Axis
     */
    private double calcLengthAxis(double[] a) {
        return Math.pow(Math.pow(a[0],2.0) + Math.pow(a[1],2.0) + Math.pow(a[2],2.0), 0.5);
    };

    /**
     * Calculate the angle between two Axes
     */
    private double calcAxesAngle(double[] a, double[] b) {
        return (180.0/Math.PI)*Math.acos((a[0]*b[0]+a[1]*b[1]+a[2]*b[2])/
               (calcLengthAxis(a)*calcLengthAxis(b)));
    };
}
