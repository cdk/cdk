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
        if (args.length < 1) {
            System.err.println("syntax: FileFormatGuesser <file> <file2> ...");
            System.exit(0);
        }
        
        for (int i=0; i<args.length; i++) {
            String ifilename = args[i];
            try {
                ReaderFactory factory = new ReaderFactory();
                File input = new File(ifilename);
                if (!input.isDirectory()) {
                    String format = factory.guessFormat(new FileReader(input));
                    System.out.println(ifilename + ": format=" + format);
                }
            } catch (Exception exception) {
                System.err.println(ifilename + ": error=");
                exception.printStackTrace();
            }
        }
    }
}



