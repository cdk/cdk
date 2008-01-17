/* $Revision: 8453 $ $Author: egonw $ $Date: 2007-06-29 07:28:01 -0400 (Fri, 29 Jun 2007) $    
 * 
 * Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.test;

import junit.framework.Test;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * This test class is <b>not</b> intended to be tested directly,
 * but serve as helper class for actual coverage testers.
 *
 * @cdk.module test
 */
abstract public class CoverageAnnotationTest extends CDKTestCase {

    private final String basePackageName = "org.openscience.cdk.";

    private String moduleName;
    private ClassLoader classLoader;
    private List<String> classesToTest;

    public CoverageAnnotationTest(String name) {
        super(name);
        classesToTest = null;
    }

    protected void setUp() throws Exception {
        classLoader = this.getClass().getClassLoader();
    }

    /**
     * This method must be overwritten by subclasses.
     *
     * @return null as this is not meant to be called directly
     */
    public static Test suite() {
        return null;
    }

    protected void loadClassList(String classList) throws Exception {
        classesToTest = new ArrayList<String>();

        // for a pretty message
        String[] comps = classList.split("\\.");
        moduleName = comps[0];

        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(classList);
        if (stream == null) fail("File not found in the classpath: " + classList);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        while (reader.ready()) {
            // load them one by one
            String rawClassName = reader.readLine();
            if (rawClassName == null) break;
            rawClassName = rawClassName.substring(20);
            String className = convertSlash2Dot(
                    rawClassName.substring(0, rawClassName.indexOf('.'))
            );
            classesToTest.add(className);
        }
    }

    protected boolean runCoverageTest() {
        int missingTestsCount = 0;
        int uncoveredClassesCount = 0;
        for (String className : classesToTest) {
            int errors = checkClass(className);
            missingTestsCount += errors;
            if (errors > 0) uncoveredClassesCount++;
        }
        if (missingTestsCount > 0) {
            fail("The " + moduleName + " module is not fully tested! Missing number of method tests: " +
                    missingTestsCount + " in number of classes: " + uncoveredClassesCount);
        }
        return true;
    }

    private int checkClass(String className) {
        Class coreClass = loadClass(getClassName(className));
        if (coreClass.isInterface()) return 0;

        // lets get all the methods in the class we're checking
        // we're going to skip private and protected methods. We
        // also skip the toString() method
        int missingTestCount = 0;
        HashMap<String, TestMethod> methodAnnotations = new HashMap<String, TestMethod>();
        Method[] sourceMethods = coreClass.getDeclaredMethods();
        for (Method method : sourceMethods) {
            int modifiers = method.getModifiers();
            if (Modifier.isPrivate(modifiers) || Modifier.isProtected(modifiers)) continue;

            TestMethod testMethodAnnotation = method.getAnnotation(TestMethod.class);

            // if a method does not have the annotation, it's missing a test
            if (testMethodAnnotation == null) {
                System.out.println(className + "#" + method.getName() + " does not have a test method");
                missingTestCount++;
            } else methodAnnotations.put(method.getName(), testMethodAnnotation);
        }

        // get the test class for this class, as noted in the class annotation
        // and get a list of methods in the test class. We assume that if a class
        // does not have a TestClass annotation it is not tested, even though individual
        // methods mighthave TestMethod annotations
        TestClass testClassAnnotation = (TestClass) coreClass.getAnnotation(TestClass.class);
        if (testClassAnnotation == null) {
            System.out.println(className + " did not have a TestClass annotation");
            return methodAnnotations.size() + missingTestCount + 1;
        }
        Class testClass;
        try {
            testClass = this.getClass().getClassLoader().loadClass(testClassAnnotation.value());
        } catch (ClassNotFoundException e) {
            System.out.println(className + " refers to a non-existing test class: " + testClassAnnotation.value());
            return methodAnnotations.size() + missingTestCount + 1;
        }
        List<String> testMethodNames = new ArrayList<String>();
        Method[] testMethods = testClass.getMethods();
        for (Method method : testMethods) testMethodNames.add(method.getName());

        // now we check that the methods specified in the annotations
        // of the source class are found in the specified test class
        // if not, we count it as a missing test
        Set<String> keys = methodAnnotations.keySet();
        for (String key : keys) {
            TestMethod annotation = methodAnnotations.get(key);

            // this may be a comma separated list of test method names
            String[] tokens = annotation.value().split(",");
            for (String token : tokens) {
                if (!testMethodNames.contains(token.trim())) {
                    System.out.println(className + "#" + key + " has a test method annotation which is not found in test class: " + testClass.getName());
                    missingTestCount++;
                }
            }
        }

        return missingTestCount;
    }


    private Class loadClass(String className) {
        Class loadedClass = null;
        try {
            loadedClass = classLoader.loadClass(className);
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            fail("Could not find class: " + exception.getMessage());
        } catch (NoSuchMethodError error) {
            fail("No such method in class: " + error.getMessage());
        }
        return loadedClass;
    }

    private String convertSlash2Dot(String className) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < className.length(); i++) {
            if (className.charAt(i) == '/') {
                sb.append('.');
            } else {
                sb.append(className.charAt(i));
            }
        }
        return sb.toString();
    }

    private String getClassName(String className) {
        return basePackageName + className;
    }

}