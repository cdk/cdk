/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2003  The Jmol Development Team
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
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.io;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.io.setting.*;
import org.openscience.cdk.tools.LoggingTool;
import java.util.Vector;
import java.io.*;

/**
 * Class that implements the new MDL mol format introduced in August 2002.
 * The overall syntax is compatible with the old format, but I consider
 * the format completely different, and thus implemented a separate Reader
 * for it.
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @created 2003-10-05
 * 
 * @keyword MDL V3000
 */
public class MDLRXNV3000Reader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private LoggingTool logger = null;

    public MDLRXNV3000Reader(Reader in) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        input = new BufferedReader(in);
        initIOSettings();
    }

    public ChemObject read(ChemObject object) throws CDKException {
        return new ChemObject();
    }
    
    public boolean accepts(ChemObject object) {
        return true;
    }

    public void close() throws IOException {
        input.close();
    }
    
    private void initIOSettings() {
    }
    
    private void customizeJob() {
    }

    public IOSetting[] getIOSettings() {
        return new IOSetting[0];
    }
    
}
