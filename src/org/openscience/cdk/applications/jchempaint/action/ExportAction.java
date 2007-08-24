/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 2003-2007  The JChemPaint project
 *
 * Contact: jchempaint-devel@lists.sourceforge.net
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
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.io.JCPExportFileFilter;
import org.openscience.cdk.applications.jchempaint.io.JCPFileView;
import org.openscience.cdk.io.MDLWriter;
import org.w3c.dom.Node;

import com.sun.media.jai.codec.JPEGEncodeParam;

/**
 * Exporting the current model various formats
 *
 * @cdk.module  jchempaint
 * @author      Egon Willighagen
 * @cdk.require jai
 * @cdk.bug     1586156
 */
public class ExportAction extends SaveAsAction {

	private static final long serialVersionUID = -3287152749914283054L;
	
	private FileFilter currentFilter = null;
    
    /**
    * Opens a dialog frame and manages the saving of a file.
    */
    public void actionPerformed(ActionEvent e) {
        
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(jcpPanel.getCurrentWorkDirectory());
        JCPExportFileFilter.addChoosableFileFilters(chooser);
        if (currentFilter != null) {
            chooser.setFileFilter(currentFilter);
        }
        chooser.setFileView(new JCPFileView());
        int returnVal = chooser.showSaveDialog(jcpPanel);
        String type = null;
        currentFilter = chooser.getFileFilter();
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            type = ((JCPExportFileFilter)currentFilter).getType();
            
            File outFile = chooser.getSelectedFile();

            if (type.equals(JCPExportFileFilter.svg)) {
                try {
                    JChemPaintModel jcpm = jcpPanel.getJChemPaintModel();
                    ChemModel model = (ChemModel)jcpm.getChemModel();
                    saveAsSVG(model, outFile);
                } catch (Exception exc) {
                    String error = "Error while writing file: " + exc.getMessage();
                    logger.error(error);
                    logger.debug(exc);
                    JOptionPane.showMessageDialog(jcpPanel, error);
                }
            } else {
                // A binary image
                RenderedImage awtImage = jcpPanel.takeSnapshot();
                String filename = outFile.toString();
                logger.debug("Creating binary image: ", filename);
                if (type.equals(JCPExportFileFilter.png)) {
            		try {
            			ImageWriter writer = ImageIO.getImageWriters(
            				new ImageTypeSpecifier(awtImage), "png"
            			).next();
            			ImageTypeSpecifier specifier = new ImageTypeSpecifier(awtImage);
            			IIOMetadata meta = writer.getDefaultImageMetadata( specifier, null );

            			Node node = meta.getAsTree( "javax_imageio_png_1.0" );
            			IIOMetadataNode tExtNode = new IIOMetadataNode("tEXt");
            			IIOMetadataNode tExtEntryNode = new IIOMetadataNode("tEXtEntry");
            			tExtEntryNode.setAttribute( "keyword", "molfile" );
            			// create the MDL molfile
            			StringWriter outputString = new StringWriter();
            			MDLWriter mdlWriter = new MDLWriter(outputString);
            			JChemPaintModel jcpm = jcpPanel.getJChemPaintModel();
                        ChemModel model = (ChemModel)jcpm.getChemModel();
                        mdlWriter.write(model);
                        mdlWriter.close();
            			tExtEntryNode.setAttribute( "value", outputString.toString());
            			tExtNode.appendChild(tExtEntryNode);
            			node.appendChild(tExtNode);
            			meta.mergeTree("javax_imageio_png_1.0", node);
            			
            			ImageOutputStream ios = ImageIO.createImageOutputStream(new FileOutputStream(filename));
            			writer.setOutput(ios);
            			writer.write( meta, new IIOImage(awtImage, null, meta), null );
					} catch (Exception e1) {
						System.out.println("Error while writing PNG: " + e1.getMessage());
						e1.printStackTrace();
					}                	
                } else {
                	RenderedOp image = JAI.create("AWTImage", awtImage);
                	if (type.equals(JCPExportFileFilter.bmp)) {
                		JAI.create("filestore", image, filename, "BMP", null);
                	} else if (type.equals(JCPExportFileFilter.tiff)) {
                		JAI.create("filestore", image, filename, "TIFF", null);
                	} else if (type.equals(JCPExportFileFilter.jpg)) {
                		JAI.create("filestore", image, filename, "JPEG", new JPEGEncodeParam());
                	} else { // default to a PNG binary image
                		JAI.create("filestore", image, filename, "PNG", null);
                	}
                }
                logger.debug("Binary image saved to: ", filename);
            }
        }

        jcpPanel.setCurrentWorkDirectory(chooser.getCurrentDirectory());        
    }
    
}
    
