/*
 * @(#)CMLResolver.java  0.1 2000/01/06
 *
 * This class was based on the DTDResolver class developed by Dan Gezelter.
 *
 * Information can be found at http://www.openscience.org/~egonw/cdopi/
 *
 * Copyright (c) 1999 E.L. Willighagen (egonw@sci.kun.nl)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.openscience.cdk.io.cml;

import org.xml.sax.*;
import java.net.URL;
import java.io.*;

public class CMLResolver implements EntityResolver {

    private org.openscience.cdk.tools.LoggingTool logger;

	public CMLResolver() {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
	}

    public InputSource resolveEntity (String publicId, String systemId) {
        logger.warn("CMLResolver: resolving " + publicId + ", " + systemId);
        systemId = systemId.toLowerCase();
        if ((systemId.indexOf("cml-1999-05-15.dtd") != -1) || (systemId.indexOf("cml.dtd") != -1)) {
            return getCMLType( "org/openscience/cdk/io/cml/data/cml.dtd" );
        } else {
		    logger.warn("Could not resolve " + systemId);
            return null;
        }
    }

    private InputSource getCMLType( String type ) {
	try {
	    URL url = ClassLoader.getSystemResource(type);
	    return new InputSource(new BufferedReader(new InputStreamReader(url.openStream())));
	} catch (Exception e) {
	    System.err.println("Error while trying to read CML DTD (" + type + "): " + e.toString());
	    return null;
	}
    }
}
