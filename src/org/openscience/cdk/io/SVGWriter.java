/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.io;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.SVGFormat;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.tools.LoggingTool;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * Writes the SVG strings to output. This class makes
 * use of the Batik library for producing SVG.
 * See <a href="http://xml.apache.org/">xml.apache.org</a>.
 *
 * @cdk.module extra
 *
 * @cdk.keyword file format
 * @cdk.keyword vector graphics, SVG
 * @cdk.keyword scalable vector graphics
 *
 * @author  Egon Willighagen
 * @cdk.created 2002-09-30
 *
 * @cdk.builddepends batik-awt-util.jar
 * @cdk.builddepends batik-dom.jar
 * @cdk.builddepends batik-svggen.jar
 * @cdk.builddepends batik-util.jar
 * @cdk.builddepends batik-xml.jar
 * @cdk.require swing
 */
public class SVGWriter extends DefaultChemObjectWriter {

    private LoggingTool logger;
    static BufferedWriter writer;

    /**
     * Constructor.
     * @param output the stream to write the SVG file to.
     */
    public SVGWriter(Writer out) {
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

    public SVGWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }
    
    public SVGWriter() {
        this(new StringWriter());
    }
    
    public IChemFormat getFormat() {
        return new SVGFormat();
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
     * @param   object  IChemObject of which the data is outputted.
     */
    public void write(IChemObject object) throws CDKException {
        if (object instanceof IAtomContainer) {
            writeAtomContainer((IAtomContainer)object);
        } else {
            throw new CDKException("Only supported is writing of AtomContainer objects.");
        }
    }

    /**
     * Writes the content from molecule to output.
     *
     * @param   molecule  Molecule of which the data is outputed.
     */
    public void writeAtomContainer(IAtomContainer molecule) {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
        ctx.setComment("Generated by the Chemistry Development Kit (http://cdk.sf.net/) with Batik SVG Generator");
        SVGGraphics2D svgGenerator = new SVGGraphics2D(ctx, false);
        // SVGPaintPanel paintPanel = new SVGPaintPanel(molecule);
        // paintPanel.paint((Graphics)svgGenerator);
        // let's try to do without the SVGPaintPanel
        Renderer2D r2d = new Renderer2D();
        IAtomContainer container = (IAtomContainer)molecule.clone();
        GeometryTools.translateAllPositive(container);
        GeometryTools.center(container, new Dimension(600,400));
        r2d.paintMolecule(container, (Graphics2D)svgGenerator,false,true);
        
        // save to stream
        boolean useCSS = true;
        try {
            svgGenerator.stream(writer, useCSS);
        } catch (IOException h) {
            logger.error("Error while writen SVG.");
        }
    }

}
