/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.dict;

import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;

import org.openscience.cdk.tools.LoggingTool;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

/**
 * Dictionary with entries build from an OWL file.
 *
 * @author       Egon Willighagen <egonw@users.sf.net>
 * @cdk.created  2005-11-18
 * @cdk.keyword  dictionary
 *
 * @cdk.depends  xom-1.0.jar
 */
public class OWLFile extends Dictionary {

    public OWLFile() {
        super();
    }

    public static Dictionary unmarshal(Reader reader) {
        LoggingTool logger = new LoggingTool(Dictionary.class);
        Dictionary dict = new Dictionary();
        try {
            Builder parser = new Builder();
            Document doc = parser.build(reader);
        } catch (ParsingException ex) {
            logger.error("Dictionary is not well-formed: ", ex.getMessage());
            logger.debug("Error at line " + ex.getLineNumber(),
                         ", column " + ex.getColumnNumber());
        } catch (IOException ex) { 
            logger.error("Due to an IOException, the parser could not check:",
                ex.getMessage()
            );
            logger.debug(ex);
        }
        return dict;
    }

}
