/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2003  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.applications;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.exception.*;
import java.io.*;
import java.util.*;

/**
 * Program that guesses the format of a file.
 *
 *  @keyword command line util
 *  @keyword file format
 */
public class FileFormatGuesser {

    /**
     * Actual program.
     */
    public static void main(String[] args) {
        File input;
        if (args.length == 1) {
            String ifilename = args[0];
            
            try {
                ReaderFactory factory = new ReaderFactory();
                String format = factory.guessFormat(new FileReader(
                    new File(ifilename)));
                System.out.println("Format: " + format);
            } catch (Exception exception) {
                System.err.println("Could not determine format due to error:");
                exception.printStackTrace();
            }
            
        } else {
            System.err.println("syntax: FileFormatGuesser <file>");
            System.exit(1);
        }
    }
}



