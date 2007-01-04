/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2007  The JChemPaint project
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
package org.openscience.cdk.applications.jchempaint.io;

import javax.swing.JFileChooser;

/**
 * It intentionally extends JCPFileFilter to remove redundant
 * data.
 *
 * @cdk.module jchempaint
 * @author  Egon Willighagen
 * @cdk.created 2002-10-05
 */
public class JCPSaveFileFilter extends JCPFileFilter {

    // only those extensions are given here that are *not* on JCPFileFilter
    public final static String svg = "svg";
    public final static String smiles = "smiles";
    public final static String cdk = "cdk";

    public JCPSaveFileFilter(String type) {
        super(type);
    }

    /**
     * Adds the JCPFileFilter to the JFileChooser object.
     */
    public static void addChoosableFileFilters(JFileChooser chooser) {
        chooser.addChoosableFileFilter(new JCPFileFilter(JCPFileFilter.mol));
        chooser.addChoosableFileFilter(new JCPSaveFileFilter(JCPSaveFileFilter.svg));
        chooser.addChoosableFileFilter(new JCPSaveFileFilter(JCPSaveFileFilter.smiles));
        chooser.addChoosableFileFilter(new JCPSaveFileFilter(JCPSaveFileFilter.cdk));
        chooser.addChoosableFileFilter(new JCPFileFilter(JCPFileFilter.cml));
    }

    /**
     * The description of this filter.
     */
    public String getDescription() {
        String type = (String)types.get(0);
        String result = super.getDescription();
        if (result == null) {
            if (type.equals(svg)) {
                result = "Scalable Vector Graphics";
            } else if (type.equals(smiles)) {
                result = "SMILES";
            } else if (type.equals(cdk)) {
                result = "CDK source code fragment";
            }
        }
        return result;
    }

}
