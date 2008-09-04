/* $Revision: 6707 $ $Author: egonw $ $Date: 2006-07-30 16:38:18 -0400 (Sun, 30 Jul 2006) $
 * 
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
 */
package net.sf.cdk.tools.coverage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CheckModuleCoverage {

	/** Modules that do no need to have a test suite */
	List<String> blackList = new ArrayList<String>();
	
	List<String> modules = new ArrayList<String>();
	
	private CheckModuleCoverage() {
		blackList.add("builder3dtools");
		blackList.add("interfaces");
		blackList.add("jchempaint");
		blackList.add("controlold");
		blackList.add("experimental");
		blackList.add("applications");
	}
	
	private void findModules() {
		// construct a list of modules, assuming runDoclet has been run
		File dir = new File("build");
		File[] files = dir.listFiles(new JavaFilesFilter());
		for (int i=0; i<files.length; i++) {
			String name = files[i].getName();
			if (!name.startsWith("test")) {
				String module = name.substring(0, name.indexOf('.'));
				if (!blackList.contains(module)) {
					modules.add(module);
				}
			}
		}
		System.out.println("Number of modules found: " + modules.size());
	}
	
	private void checkModuleSuites() {
		int missingSuites = 0;
		for (String module : modules) {
			String expectedSuite = "src/test/org/openscience/cdk/modulesuites/M" +
			    module + "Tests.java";
			if (!new File(expectedSuite).exists()) {
				System.out.println("No test suite found for: " + module);
				missingSuites++;
			}
		}
		if (missingSuites > 0) {
			System.out.println("Missing test suites: " + missingSuites);
		}
	}
	
  private void checkModuleSuiteContainsCoverageTest() {
      int missingCoverages = 0;
      for (String module : modules) {
          String expectedSuite = "src/test/org/openscience/cdk/modulesuites/M" +
              module + "Tests.java";
          File file = new File(expectedSuite);
          boolean coverageTestFound = false;
          if (file.exists()) {
              try {
                  BufferedReader reader = new BufferedReader(
                      new FileReader(file)
                  );
                  String line = reader.readLine();
                  while (line != null && !coverageTestFound) {
                      if (line.contains(module.substring(0,1).toUpperCase() +
                                        module.substring(1) + "CoverageTest")) {
                          coverageTestFound = true;
                      }
                      line = reader.readLine();
                  }
                  if (!coverageTestFound) {
                      System.out.println("Missing coverage test in suite: " + module);
                      missingCoverages++;
                  }
                  reader.close();
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }
      }
      if (missingCoverages > 0) {
        System.out.println("Missing coverage tests in suite: " + missingCoverages);
      }
    }
    
	private void checkCoverageTesting() {
		int missingCoverage = 0;
		for (String module : modules) {
			String expectedSuite = "src/test/org/openscience/cdk/coverage/" +
			    module.substring(0,1).toUpperCase() +
			    module.substring(1) + "CoverageTest.java";
			if (!new File(expectedSuite).exists()) {
				System.out.println("No code coverage checking found for: " + module);
				missingCoverage++;
			}
		}
		if (missingCoverage > 0) {
			System.out.println("Missing coverage checkers: " + missingCoverage);
		}
	}
	
	public static void main(String[] args) {
	    CheckModuleCoverage checker = new CheckModuleCoverage();
	    checker.findModules();
	    checker.checkModuleSuites();
	    checker.checkCoverageTesting();
	    checker.checkModuleSuiteContainsCoverageTest();
    }
	
	class JavaFilesFilter implements FileFilter {
		public boolean accept(File file) {
			return file.getName().endsWith(".javafiles");
        }		
	}
}
