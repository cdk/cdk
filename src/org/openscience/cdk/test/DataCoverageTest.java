/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module test
 */
public class DataCoverageTest extends TestCase {

    private final static String CLASS_LIST = "data.javafiles";
    
    private ClassLoader classLoader;
    
    public DataCoverageTest(String name) {
        super(name);
    }

    public void setUp() {
        classLoader = this.getClass().getClassLoader();
    }

    public static Test suite() {
        return new TestSuite(DataCoverageTest.class);
    }

    private Class loadClass(String className) {
        Class loadedClass = null;
        try {
            loadedClass = classLoader.loadClass(className);
        } catch (ClassNotFoundException exception) {
            fail("Could not find class: " + exception.getMessage());
        } catch (NoSuchMethodError error) {
            fail("No such method in class: " + error.getMessage());
        }
        return loadedClass;
    }
    
    public void testCoverage() {
        int missingTestsCount = 0;
        int uncoveredClassesCount = 0;
        
        // get the src/core.javafiles file
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                this.getClass().getClassLoader().getResourceAsStream(CLASS_LIST)
            ));
            while (reader.ready()) {
                // load them one by one
                String rawClassName = reader.readLine();
                rawClassName = rawClassName.substring(20);
                String className = convertSlash2Dot(
                    rawClassName.substring(0, rawClassName.indexOf('.'))
                );
                int errors = checkClass(className);
                missingTestsCount += errors;
                if (errors > 0) uncoveredClassesCount++;
            }
        } catch (Exception exception) {
            fail("Could not load the src/" + CLASS_LIST + " file!");
        }
        
        
        if (missingTestsCount > 0) {
            fail("The core module is not fully tested! Missing number of method tests: " + 
                 missingTestsCount + " in number of classes: " + uncoveredClassesCount);
        }
    }

    private int checkClass(String className) {
        // System.out.println("Checking : " + className);
        
        // the naming scheme: <package><class>Test
        final String basePackageName = "org.openscience.cdk.";
        final String testPackageName = "test.";
        
        // load both classes
        Class coreClass = loadClass(basePackageName + className);

        if (!coreClass.isInterface()) {
            Class testClass = loadClass(basePackageName + testPackageName + className + "Test");
            
            int missingTestsCount = 0;

            // make map of methods in the test class
            Vector testMethodNames = new Vector();
            Method[] testMethods = testClass.getMethods();
            for (int i=0; i<testMethods.length; i++) {
                testMethodNames.add(testMethods[i].getName());
            }
            
            // now process the methods of the class to be tested
            // first the constructors
            Constructor[] constructors = coreClass.getDeclaredConstructors();
            for (int i=0; i<constructors.length; i++) {
                int modifiers = constructors[i].getModifiers();
                if (!Modifier.isPrivate(modifiers) && !Modifier.isProtected(modifiers)) {
                    String testMethod = "test" + capitalizeName(removePackage(constructors[i].getName()));
                    Class[] paramTypes = constructors[i].getParameterTypes();
                    for (int j=0; j<paramTypes.length; j++) {
                        if (paramTypes[j].isArray()) {
                            testMethod = testMethod + "_array" + removePackage(paramTypes[j].getComponentType().getName());
                        } else {
                            testMethod = testMethod + "_" + removePackage(paramTypes[j].getName());
                        }
                    }
                    if (!testMethodNames.contains(testMethod)) {
                        System.out.println(removePackage(coreClass.getName()) + ": missing the expected test method: " + testMethod);
                        missingTestsCount++;
                    }
                }
            }
            
            // now the methods.
            Method[] methods = coreClass.getDeclaredMethods();
            for (int i=0; i<methods.length; i++) {
                int modifiers = methods[i].getModifiers();
                if (!Modifier.isPrivate(modifiers) && !Modifier.isProtected(modifiers)) {
                    String testMethod = "test" + capitalizeName(removePackage(methods[i].getName()));
                    Class[] paramTypes = methods[i].getParameterTypes();
                    for (int j=0; j<paramTypes.length; j++) {
                        if (paramTypes[j].isArray()) {
                            testMethod = testMethod + "_array" + removePackage(paramTypes[j].getComponentType().getName());
                        } else {
                            testMethod = testMethod + "_" + removePackage(paramTypes[j].getName());
                        }
                    }
                    if (!testMethodNames.contains(testMethod)) {
                        System.out.println(removePackage(coreClass.getName()) + ": missing the expected test method: " + testMethod);
                        missingTestsCount++;
                    }
                }
            }
            
            return missingTestsCount;
        } else {
            // interfaces should not be tested
            return 0;
        }
    }
    
    private String removePackage(String className) {
        return className.substring(1+className.lastIndexOf('.'));
    }
    
    private String capitalizeName(String name) {
        String capitalizedName = "";
        if (name == null) {
            capitalizedName = null;
        } else if (name.length() == 1) {
            capitalizedName = name.toUpperCase();
        } else if (name.length() > 1) {
            capitalizedName = name.substring(0,1).toUpperCase() + name.substring(1);
        }
        return capitalizedName;
    }
    
    private String convertSlash2Dot(String className) {
        StringBuffer sb = new StringBuffer();
        className = className;
        for (int i=0; i<className.length(); i++) {
            if (className.charAt(i) == '/') {
                sb.append('.');
            } else {
                sb.append(className.charAt(i));
            }
        }
        return sb.toString();
    }
}
