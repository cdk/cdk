/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.io.StringReader;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.*;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.tools.LoggingTool;

/**
 * This Reader extracts the connection table from CDX files.
 *
 * @cdk.module experimental
 *
 * @cdk.keyword file format, CDX
 *
 * @author      E.L. Willighagen
 * @cdk.created 2005-03-23
 */
public class CDXReader {

    private LoggingTool logger;
    private InputStream input;
    
    public CDXReader(InputStream input) {
        logger = new LoggingTool(this);
        this.input = input;
    }
    
    public ChemObject read(ChemObject object) throws CDKException {
        // CDX files consist of groups of two bytes, each such words
        // indicates some instruction or value
        try {
            byte[] word = new byte[2];
            int byteCount = -1;
            while ((byteCount = input.read(word)) != -1) {
                logger.debug("read #bytes: ", byteCount);
                logger.debug("" + word[0], " ", "" + word[1]);
                logger.debug(convertToHex(word[0]), " ", convertToHex(word[1]));
                // very good! I just read two bytes!
            }
        } catch (IOException exception) {
            logger.error("Could not read CDX file: ", exception.getMessage());
            logger.debug(exception);
            throw new CDKException("Could not read CDX file: " + exception.getMessage());
        }
        return null;
    };
    
    private String convertToHex(byte bi) {
        Byte bite = new Byte(bi);
        String output = Integer.toHexString(bite.intValue()).toUpperCase();
        if (bite.intValue() < 16) {
            output = "0" + output;
        }
        return output;
    }
}
