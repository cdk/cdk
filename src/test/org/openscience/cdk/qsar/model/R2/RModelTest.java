/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.model.R2;

import java.io.File;
import java.io.FilenameFilter;

import org.junit.BeforeClass;
import org.openscience.cdk.CDKTestCase;

/**
 * TestSuite that runs all Model tests.
 *
 * @author Rajarshi Guha
 * @cdk.module test-qsar
 */
public class RModelTest extends CDKTestCase {

    private static boolean initialized = false;
    private static boolean foundR = false;
    private static boolean ldPathOK = false;
    
    @BeforeClass public static void setup() {
        if (!initialized) {
            String os = System.getProperty("os.name").toLowerCase();

            // check for R on a Unix system
            if (os.startsWith("linux")
                    || os.startsWith("mac os")
                    || os.startsWith("solaris")
                    || os.startsWith("aix")
                    || os.startsWith("freebsd")) {

                String rhome = System.getenv("R_HOME");
                String ldlibrarypath = System.getenv("LD_LIBRARY_PATH");

                // ok so env vars seem to be set. Are they correct?
                findR(rhome);
                checkLdPath(ldlibrarypath);
            } else if (os.startsWith("windows")) {
                // what variables does windows need set? And what are they set to?
            }
            initialized = true;
        }
    }
    
    public static boolean runRModelTests() {
        return foundR && ldPathOK;
    }
    
    public static boolean foundR() {
        return foundR;
    }
    public static boolean ldPathOK() {
        return ldPathOK;
    }
    
    private static boolean findR(String rhome) {
        String path1 = rhome + File.separator + "bin" + File.separator + "Rcmd.exe";
        String path2 = rhome + File.separator + "bin" + File.separator + "Rcmd";
        File file1 = new File(path1);
        File file2 = new File(path2);
        foundR = file1.exists() || file2.exists();
        return foundR;
    }

    private static boolean checkLdPath(String ldlibrarypath) {
        FilenameFilter filterJRIUnix = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equals("libjri.so");
            }
        };

        FilenameFilter filterLibRUnix = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equals("libR.so");
            }
        };

        boolean foundJRI = false;
        boolean foundR = false;

        String[] paths = ldlibrarypath.split(File.pathSeparator);
        for (int i = 0; i < paths.length; i++) {
            File dir = new File(paths[i]);
            String[] files1 = dir.list(filterJRIUnix);
            String[] files2 = dir.list(filterLibRUnix);
            if (files1 != null && files1.length > 0) foundJRI = true;
            if (files2 != null && files2.length > 0) foundR = true;
        }
        ldPathOK = foundJRI && foundR;
        return ldPathOK;
    }
}
