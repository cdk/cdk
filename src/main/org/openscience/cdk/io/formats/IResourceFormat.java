/* $RCSfile$
 * $Author$
 * $Date$  
 * $Revision$
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.formats;

/**
 * This class is the interface that all ResourceFormat's should implement.
 * An implementation is supposed to be a singleton class, so it should have
 * only private constructors, and implement the getInstance() method.
 *
 * @cdk.module ioformats
 * @cdk.svnrev  $Revision$
 *
 * @author      Egon Willighagen <egonw@users.sf.net>
 * @cdk.created 2006-03-04
 */
public interface IResourceFormat {

    /**
     * Returns a one-lined format name of the format.
     */
    public String getFormatName();

    /**
     * Returns the preferred resource name extension.
     */
    public String getPreferredNameExtension();

    /**
     * Returns an array of common resource name extensions.
     */
    public String[] getNameExtensions();

    /**
     * Returns the accepted MIME type for this format.
     *
     * @return null if no MIME type has been accepted on
     */
    public String getMIMEType();
    
    /**
     * Indicates if the format is an XML-based language.
     * 
     * @return if the format is XML-based.
     */
    public boolean isXMLBased();
}

