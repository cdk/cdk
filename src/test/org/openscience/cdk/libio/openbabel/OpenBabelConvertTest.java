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
package org.openscience.cdk.libio.openbabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.libio.openbabel.OpenBabelConvert;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the convertor using OpenBabel.
 *
 * @cdk.module nocompile
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
    	return new TestSuite(OpenBabelConvertTest.class);
    }

    public void test5_Hexen_3_one() throws Exception {
        String filenameInput = "data/mdl/540545.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filenameInput);
        File fileOutput = File.createTempFile("540545.", ".mol");
        FileOutputStream outs = new FileOutputStream(fileOutput);
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = ins.read(buf)) != -1) {
            	outs.write(buf, 0, i);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (ins != null) ins.close();
            if (outs != null) outs.close();
        }
        
        logger.info("Testing: " + fileOutput.getAbsolutePath());
        System.out.println("testing: " + fileOutput.getAbsolutePath());
        
        OpenBabelConvert convertOB = new OpenBabelConvert();
        
        File tmpFile = File.createTempFile("540545.", ".cml");
        System.out.println("testing: " + tmpFile.getAbsolutePath());
        convertOB.convert(fileOutput, "mol", tmpFile, "cml", "-h");
        
        BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
        String line = reader.readLine();
        int lineCount = 0;
        while (line != null) {
        	System.out.println("Line: " + line);
        	lineCount++; 
        	line = reader.readLine();
        }
        assertTrue(lineCount > 0);
    }

}
