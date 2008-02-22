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
package org.openscience.cdk.applications.jchempaint.io;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JFileChooser;

/**
 * An export filter for JCP file formats
 *
 * @cdk.module jchempaint 
 * @cdk.svnrev  $Revision$
 * @author  Egon Willighagen
 * @cdk.created 2003-04-01
 */
public class JCPExportFileFilter extends javax.swing.filechooser.FileFilter implements IJCPFileFilter {

    // only those extensions are given here that are *not* on JCPFileFilter
    public final static String bmp = "bmp";
    public final static String png = "png";
    public final static String tiff = "tiff";
    public final static String jpg = "jpg";
    public final static String svg = "svg";

    protected List types;

    public JCPExportFileFilter(String type) {
		super();
		types = new ArrayList();
        types.add(type);
    }

    /**
     * Adds the JCPFileFilter to the JFileChooser object.
     */
    public static void addChoosableFileFilters(JFileChooser chooser) {
        chooser.addChoosableFileFilter(new JCPExportFileFilter(JCPExportFileFilter.bmp));
        chooser.addChoosableFileFilter(new JCPExportFileFilter(JCPExportFileFilter.jpg));
        chooser.addChoosableFileFilter(new JCPExportFileFilter(JCPExportFileFilter.png));
        chooser.addChoosableFileFilter(new JCPExportFileFilter(JCPExportFileFilter.svg));
        chooser.addChoosableFileFilter(new JCPExportFileFilter(JCPExportFileFilter.tiff));
    }

    /**
     * The description of this filter.
     */
    public String getDescription() {
        String type = (String)types.get(0);
        String result = "Unknown";
        if (type.equals(png)) {
            result = "PNG";
        } else if (type.equals(bmp)) {
            result = "BMP";
        } else if (type.equals(tiff)) {
            result = "TIFF";
        } else if (type.equals(jpg)) {
            result = "JPEG";
        } else if (type.equals(svg)) {
            result = "Scalable Vector Graphics";
        }
        return result;
    }

    // Accept all directories and all gif, jpg, or tiff files.
    public boolean accept(File f) {
        boolean accepted = false;
        if (f.isDirectory()) {
            accepted = true;
        }

        String extension = getExtension(f);
		if (extension != null) {
            if (types.contains(extension)) {
            	accepted = true;
            }
    	}
        return accepted;
    }

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) 
	{
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

	public String getType() {
		return (String)types.get(0);
	}

	public void setType(String type) {
		types.add(type);
	}
}
