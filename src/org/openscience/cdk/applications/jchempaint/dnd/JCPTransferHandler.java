/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2006  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
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
package org.openscience.cdk.applications.jchempaint.dnd;

import org.openscience.cdk.tools.LoggingTool;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;

/**
 * Created after http://java.sun.com/docs/books/tutorial/uiswing/misc/example-1dot4/ArrayListTransferHandler.java
 *
 * @cdk.module jchempaint
 */
public class JCPTransferHandler extends TransferHandler {
    
	private static final long serialVersionUID = -2702180989701731589L;

    private DataFlavor localFlavor = DataFlavor.getTextPlainUnicodeFlavor();
	private LoggingTool logger;
    private String handlerFor;
    
    public JCPTransferHandler(String handlerFor) {
        logger = new LoggingTool(this);
        this.handlerFor = handlerFor;
        logger.debug("Instantiated new transfer handler for:", handlerFor);
    }

    public boolean importData(JComponent c, Transferable t) {
        logger.debug(handlerFor, "Importing data into: ", c.getClass().getName());
        if (!canImport(c, t.getTransferDataFlavors())) {
            logger.debug("Cannot import data...");
            return false;
        }

        return false;
    }

    protected void exportDone(JComponent c, Transferable data, int action) {
        // nothing to be done right now
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        logger.debug(handlerFor, "Offered formats:");
        for (int i=0; i<flavors.length; i++) {
            logger.debug("  ", flavors[i].getMimeType());
        }
        return true;
    }

    protected Transferable createTransferable(JComponent c) {
        logger.debug(handlerFor, "Creating transferable...");
        if (false) {
            // return new JCPTransferable(some content);
        }
        return null;
    }

    public int getSourceActions(JComponent c) {
        logger.debug(handlerFor, "Getting source action...");
        return COPY_OR_MOVE;
    }

    public class JCPTransferable implements Transferable {
        
        ArrayList data;

        public JCPTransferable(ArrayList alist) {
            data = alist;
        }

        public Object getTransferData(DataFlavor flavor)
                                 throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return data;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { localFlavor };
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            if (localFlavor.equals(flavor)) {
                return true;
            }
            return false;
        }
    }
}

