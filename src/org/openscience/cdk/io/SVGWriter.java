/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.io;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.JPanel;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.renderer.Renderer2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * Writes the SVG strings to output. This class makes
 * use of the Batik library for producing SVG.
 * See <a href="http://xml.apache.org/">xml.apache.org</a>.
 *
 * @cdkPackage extra
 *
 * @keyword file format
 * @keyword vector graphics, SVG
 * @keyword scalable vector graphics
 *
 * @author  Egon Willighagen
 * @created 2002-09-30
 *
 * @cdk.builddepends batik-awt-util.jar
 * @cdk.builddepends batik-dom.jar
 * @cdk.builddepends batik-svggen.jar
 * @cdk.builddepends batik-util.jar
 * @cdk.builddepends batik-xml.jar
 */
public class SVGWriter extends DefaultChemObjectWriter {

    private org.openscience.cdk.tools.LoggingTool logger;
    static BufferedWriter writer;

    /**
     * Contructs a new SMILESWriter that can write a list of SMILES to a Writer
     *
     * @param   out  The Writer to write to
     */
    public SVGWriter(Writer out) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        try {
            writer = new BufferedWriter(out);
        } catch (Exception exc) {
        }
    }

    public String getFormatName() {
        return "Scalable Vector Graphics (SVG)";
    }

    /**
     * Contructs a new SMILESWriter that can write an list of SMILES to a given OutputStream
     *
     * @param   out  The OutputStream to write to
     */
    public SVGWriter(FileOutputStream out) {
        this(new OutputStreamWriter(out));
    }

    /**
     * Flushes the output and closes this object
     */
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }

    /**
     * Writes the content from object to output.
     *
     * @param   object  ChemObject of which the data is outputted.
     */
    public void write(ChemObject object) throws CDKException {
        if (object instanceof AtomContainer) {
            writeAtomContainer((AtomContainer)object);
        } else {
            throw new CDKException("Only supported is writing of AtomContainer objects.");
        }
    }

    public ChemObject highestSupportedChemObject() {
        return new Molecule();
    }

    /**
     * Writes the content from molecule to output.
     *
     * @param   molecule  Molecule of which the data is outputed.
     */
    public void writeAtomContainer(AtomContainer molecule) {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        SVGPaintPanel paintPanel = new SVGPaintPanel(molecule);
        paintPanel.paint((Graphics)svgGenerator);
        boolean useCSS = false;
        try {
            svgGenerator.stream(writer, useCSS);
        } catch (IOException h) {
            logger.error("Error while writen SVG.");
        }
    }

    class SVGPaintPanel extends JPanel {

        private AtomContainer ac = null;
        private Renderer2D r2d = null;

        SVGPaintPanel(AtomContainer ac) {
            super();
            setLayout(null);
            setPreferredSize(new Dimension(600,400));
            setOpaque(true);
            setBackground(Color.white);
            r2d = new Renderer2D();

            this.ac = ac;
            GeometryTools.translateAllPositive(ac);
            double scaleFactor = GeometryTools.
                getScaleFactor(ac, r2d.getRenderer2DModel().getBondLength());
            GeometryTools.scaleMolecule(ac, scaleFactor);
            GeometryTools.center(ac, getPreferredSize());
        }

        public void paint(Graphics g) {
            super.paint(g);
            r2d.paintMolecule(ac, g);
        }

    }

}
