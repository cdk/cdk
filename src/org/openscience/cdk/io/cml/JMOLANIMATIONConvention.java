/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io.cml;

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.Attributes;

/**
 * @author Egon Willighagen <egonw@sci.kun.nl>
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 */
public class JMOLANIMATIONConvention extends CMLCoreModule {

    private final int UNKNOWN = -1;
    private final int ENERGY = 1;

    private int current;
    private String frame_energy;
    private LoggingTool logger;

    public JMOLANIMATIONConvention(IChemFile chemFile) {
        super(chemFile);
        logger = new LoggingTool(this);
        current = UNKNOWN;
    }

    public JMOLANIMATIONConvention(ICMLModule conv) {
        super(conv);
        logger = new LoggingTool(this);
    }

    public void startElement(CMLStack xpath, String uri, String local, String raw, Attributes atts) {
        String name = local;
        if (name.equals("list")) {
            logger.debug("Oke, JMOLANIMATION seems to be kicked in :)");
//            cdo.startObject("Animation");
            currentChemSequence = currentChemFile.getBuilder().newChemSequence();
            super.startElement(xpath, uri, local, raw, atts);
        } else if (name.equals("molecule")) {
//            cdo.startObject("Frame");
        	currentChemModel = currentChemFile.getBuilder().newChemModel();
            logger.debug("New frame being parsed.");
            super.startElement(xpath, uri, local, raw, atts);
        } else if (name.equals("float")) {
            boolean isEnergy = false;
            logger.debug("FLOAT found!");
            for (int i = 0; i < atts.getLength(); i++) {
              logger.debug(" att: ", atts.getQName(i), " -> ", atts.getValue(i));
                if (atts.getQName(i).equals("title")
                        && atts.getValue(i).equals("FRAME_ENERGY")) {
                    isEnergy = true;
                }
            }
            if (isEnergy) {
                // oke, this is the frames energy!
                current = ENERGY;
            } else {
                super.startElement(xpath, uri, local, raw, atts);
            }
        } else {
            super.startElement(xpath, uri, local, raw, atts);
        }
    }

    public void endElement(CMLStack xpath, String uri, String local, String raw) {
        String name = local;
        if (current == ENERGY) {
//            cdo.setObjectProperty("Frame", "energy", frame_energy);
                // + " " + units);
        	// FIXME: does not have a ChemFileCDO equivalent
            current = UNKNOWN;
            frame_energy = "";
        } else if (name.equals("list")) {
            super.endElement(xpath, uri, local, raw);
//            cdo.endObject("Animation");
            currentChemFile.addChemSequence(currentChemSequence);
        } else if (name.equals("molecule")) {
            super.endElement(xpath, uri, local, raw);
//            cdo.endObject("Frame");
            // nothing done in the CD upon this event
        } else {
            super.endElement(xpath, uri, local, raw);
        }
    }

    public void characterData(CMLStack xpath, char ch[], int start, int length) {
        if (current == ENERGY) {
            frame_energy = new String(ch, start, length);
        } else {
            super.characterData(xpath, ch, start, length);
        }
    }
}
