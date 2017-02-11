/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 */
package org.openscience.cdk.smsd.helper;

import java.util.ArrayList;
import java.util.List;


/**
 * Class that handles atoms and assignes an integer lable to them.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 */
public class LabelContainer {

    private List<String>          labelMap     = null;
    private int                   labelCounter = 0;
    private static LabelContainer instance     = null;

    protected LabelContainer() {

        // System.err.println("List Initialized");
        labelMap = new ArrayList<String>();
        labelMap.add(labelCounter++, "X");
        labelMap.add(labelCounter++, "R");
    }

    /**
     * Create ids from atom labels
     * @return instance of this object
     */
    synchronized public static LabelContainer getInstance() {
        if (instance == null) {
            instance = new LabelContainer();
        }
        return instance;
    }

    /**
     * Add label if its not present
     * @param label
     */
    synchronized public void addLabel(String label) {
        if (!labelMap.contains(label)) {
            labelMap.add(labelCounter++, label);
        }
    }

    /**
     * Returns label ID
     * @param label
     * @return labelID
     */
    synchronized public Integer getLabelID(String label) {
        addLabel(label);
        return labelMap.indexOf(label);
    }

    /**
     * Returns Label of a given ID
     * @param labelID
     * @return label
     */
    synchronized public String getLabel(Integer labelID) {
        return labelMap.get(labelID);
    }

    /**
     * Returns label count
     * @return size of the labels
     */
    synchronized public int getSize() {
        return labelMap.size();
    }
}
