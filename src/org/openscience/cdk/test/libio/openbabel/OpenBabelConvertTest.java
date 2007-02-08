/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 * 
 * Contact: cdk-devel@slists.sourceforge.net
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
 *  */
package org.openscience.cdk.test.libio.openbabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.libio.openbabel.OpenBabelConvert;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the convertor using OpenBabel.
 *
 * @cdk.module test-extra
 * 
 * @author     Miguel Rojas <miguelrojasch@uni-koeln.de>
 */
public class OpenBabelConvertTest extends CDKTestCase {

    private static org.openscience.cdk.tools.LoggingTool logger;

    public OpenBabelConvertTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
    	TestSuite suite = new TestSuite("The OpenBabel Tests");
    	if (OpenBabelConvert.hasOpenBabel(getPATH())) {
    		System.out.println("Found OpenBabel: running tests.");
    		suite.addTest(new TestSuite(OpenBabelConvertTest.class));
    	} else {
    		System.out.println("No OpenBabel found: not running tests.");
    	}
    	return suite;
    }

    public void test5_Hexen_3_one() throws Exception {
        String filenameInput = "src/data/mdl/540545.mol";
        logger.info("Testing: " + filenameInput);
        /* the path only necessary for windows systems*/
        File PATH = getPATH();

        OpenBabelConvert convertOB = new OpenBabelConvert(PATH);
        convertOB.setInputFileToConvert(new File(filenameInput),"mol",null);
        File tmpFile = File.createTempFile("540545.", ".cml");
        convertOB.convertTo(tmpFile,"cml","-h");
        IChemFile chemFile = convertOB.getChemFile();
        convertOB.reset();

//      test the resulting ChemFile content
        assertNotNull(chemFile);

        BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
        String line = reader.readLine();
        int lineCount = 0;
        while (line != null) {
        	lineCount++; 
        	line = reader.readLine();
        }
        assertTrue(lineCount > 0);
    }

    private static File getPATH(){
    	String[] possibilities = {
    		"C:/Programme/openbabel-2.0.0awins/babel.exe", // likely??
    		"/usr/bin/babel", // most POSIX systems
    		"/usr/local/bin/babel" // private installation
    	};
    	File PATH = null;
    	for (int i=0; i<possibilities.length; i++) {
    		PATH = new File(possibilities[i]);
    	    if (PATH.exists()) break;
        }
    	return PATH;
    }
}
