/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.io.iterator;

import org.openscience.cdk.exception.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.io.setting.*;
import java.io.*;
import java.util.*;
import javax.vecmath.*;

/**
 * Iterating MDL SDF reader. It allows to iterate over all molecules
 * in the SDF file, without reading them into memory first. Suitable
 * for very large SDF files.
 *
 * <p>For parsing the molecules it still uses the normal MDLReader.
 *
 * @see org.openscience.cdk.io.MDLReader
 * 
 * @author     Egon Willighagen <egonw@sci.kun.nl>
 * @created    2003-10-19
 *
 * @keyword    file format, MDL molfile
 * @keyword    file format, SDF
 */
public class IteratingMDLReader extends DefaultIteratingChemObjectReader {

    BufferedReader input = null;
    private org.openscience.cdk.tools.LoggingTool logger = null;
    
    /**
     * Contructs a new MDLReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public IteratingMDLReader(Reader in) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        input = new BufferedReader(in);
    }

    public boolean hasNext() {
        return false;
    }
    
    public Object next() {
        return new Object();
    }
    
    public void close() throws IOException {
        input.close();
    }
    
}

