/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Opens a print dialog
 *
 * @author        steinbeck
 * @cdk.module    jchempaint
 */
public class PrintAction extends JCPAction implements Printable {

	/**
	 *  Opens a dialog frame and manages the printing of a file.
	 *
	 * @param  event  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent event) {
		
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);
		if (printJob.printDialog()) {
			try {
				printJob.print();
			} catch (PrinterException pe) {
				System.out.println("Error printing: " + pe);
			}
		}
	}

	/**
	 *  Prints the actual drawingPanel
	 *
	 * @param  g           Graphics object of drawinPanel
	 * @param  pageFormat  Description of the Parameter
	 * @param  pageIndex   Description of the Parameter
	 * @return             Description of the Return Value
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		//get eventually selected parts
		IAtomContainer beforePrinting = jcpPanel.getJChemPaintModel().getRendererModel().getSelectedPart();
		//disable selection for printing
		jcpPanel.getJChemPaintModel().getRendererModel().setSelectedPart(new org.openscience.cdk.AtomContainer());
		if (pageIndex > 0) {
			//enable selection again
			jcpPanel.getJChemPaintModel().getRendererModel().setSelectedPart(beforePrinting);
			return (NO_SUCH_PAGE);
		}
		else {
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			jcpPanel.getDrawingPanel().setDoubleBuffered(false);
			jcpPanel.getDrawingPanel().paint(g2d);
			jcpPanel.getDrawingPanel().setDoubleBuffered(true);
			//enable selection again
			if (beforePrinting != null) jcpPanel.getJChemPaintModel().getRendererModel().setSelectedPart(beforePrinting);
			return (PAGE_EXISTS);
		}
	}
}

