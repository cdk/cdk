/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1997-2007  Dan Gezelter
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.io.cml;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * This class resolves DOCTYPE declaration for Chemical Markup Language (CML)
 * files and uses a local version for validation. More information about
 * CML can be found at <a href="http://www.xml-cml.org/">http://www.xml-cml.org/</a>.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 **/
public class CMLResolver implements EntityResolver {

    private LoggingTool logger;

    public CMLResolver() {
        logger = new LoggingTool(this);
    }

    /**
     * Not implemented: always returns null.
     **/
    public InputSource getExternalSubset(String name, String baseURI) {
        return null;
    }

    /**
     * Not implemented, but uses resolveEntity(String publicId, String systemId)
     * instead.
     **/
    public InputSource resolveEntity(String name, String publicId,
                                     String baseURI, String systemId) {
        return resolveEntity(publicId, systemId);
    }

    /**
     * Resolves SYSTEM and PUBLIC identifiers for CML DTDs.
     *
     * @param publicId the PUBLIC identifier of the DTD (unused)
     * @param systemId the SYSTEM identifier of the DTD
     * @return the CML DTD as an InputSource or null if id's unresolvable
     */
    public InputSource resolveEntity (String publicId, String systemId) {
        logger.debug("CMLResolver: resolving ", publicId, ", ", systemId);
        systemId = systemId.toLowerCase();
        if ((systemId.indexOf("cml-1999-05-15.dtd") != -1) ||
            (systemId.indexOf("cml.dtd") != -1) ||
            (systemId.indexOf("cml1_0.dtd") != -1)) {
            logger.info("File has CML 1.0 DTD");
            return getCMLType( "cml1_0.dtd" );
        } else if ((systemId.indexOf("cml-2001-04-06.dtd") != -1) ||
                   (systemId.indexOf("cml1_0_1.dtd") != -1) ||
                   (systemId.indexOf("cml_1_0_1.dtd") != -1)) {
            logger.info("File has CML 1.0.1 DTD");
            return getCMLType( "cml1_0_1.dtd" );
        } else {
            logger.warn("Could not resolve systemID: ", systemId);
            return null;
        }
    }

    /**
     * Returns an InputSource of the appropriate CML DTD. It accepts
     * two CML DTD names: cml1_0.dtd and cml1_0_1.dtd. Returns null
     * for any other name.
     *
     * @param type the name of the CML DTD version
     * @return the InputSource to the CML DTD
     */
    private InputSource getCMLType( String type ) {
        try {
            InputStream ins = this.getClass().getClassLoader().getResourceAsStream("org/openscience/cdk/io/cml/data/" + type);
            return new InputSource(new BufferedReader(new InputStreamReader(ins)));
        } catch (Exception e) {
            logger.error("Error while trying to read CML DTD (" + type + "): ",
                         e.getMessage());
            logger.debug(e);
            return null;
        }
    }
}
