/*
 * Copyright 2001 The Jmol Development Team
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
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.io.cml;

import java.util.*;
import org.openscience.cdk.io.cml.cdopi.*;
import org.xml.sax.*;

public class JMOLANIMATIONConvention extends Convention {

    private final int UNKNOWN = -1;
    private final int ENERGY = 1;

    private int current;
    private String frame_energy;
    private String units;

    public JMOLANIMATIONConvention(CDOInterface cdo) {
        super(cdo);
        current = UNKNOWN;
    };

    public JMOLANIMATIONConvention(Convention conv) {
        super(conv);
    };

    public CDOInterface returnCDO() {
        return this.cdo;
    };

    public void startDocument() {
        super.startDocument();
    };

    public void endDocument() {
        super.endDocument();
    };


    public void startElement(String uri, String local, String raw, Attributes atts) {
        String name = local;
        if (name.equals("list")) {
            // System.err.println("Oke, JMOLANIMATION seems to be kicked in :)");
            cdo.startObject("Animation");
            super.startElement(uri, local, raw, atts);
        } else if (name.equals("molecule")) {
            cdo.startObject("Frame");
            super.startElement(uri, local, raw, atts);
        } else if (name.equals("float")) {
            boolean isEnergy = false;
            System.err.println("FLOAT found!");
            for (int i = 0; i < atts.getLength(); i++) {
                System.err.println(" att: " + atts.getQName(i) + " -> "
                    + atts.getValue(i));
                if (atts.getQName(i).equals("title")
                        && atts.getValue(i).equals("FRAME_ENERGY")) {
                    isEnergy = true;
                } else if (atts.getQName(i).equals("units")) {
                    units = atts.getValue(i);
                }
            }
            if (isEnergy) {
                // oke, this is the frames energy!
                current = ENERGY;
            } else {
                super.startElement(uri, local, raw, atts);
            }
        } else {
            super.startElement(uri, local, raw, atts);
        }
    };

    public void endElement(String uri, String local, String raw) {
        String name = local;
        if (current == ENERGY) {
            cdo.setObjectProperty("Frame", "energy", frame_energy);
                // + " " + units);
            current = UNKNOWN;
            frame_energy = "";
            units = "";
        } else if (name.equals("list")) {
            super.endElement(uri, local, raw);
            cdo.endObject("Animation");
        } else if (name.equals("molecule")) {
            super.endElement(uri, local, raw);
            cdo.endObject("Frame");
        } else {
            super.endElement(uri, local, raw);
        }
    }

    public void characterData(char ch[], int start, int length) {
        if (current == ENERGY) {
            frame_energy = new String(ch, start, length);
        } else {
            super.characterData(ch, start, length);
        }
    }
}
