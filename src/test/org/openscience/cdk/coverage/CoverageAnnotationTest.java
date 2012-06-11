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
package org.openscience.cdk.coverage;

import org.junit.Assert;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
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
abstract public class CoverageAnnotationTest {

    private final static String BASEPACKAGENAME = "org.openscience.cdk.";

    private static String moduleName;
    private static ClassLoader classLoader;
    private static List<String> classesToTest;

    protected static void loadClassList(String classList, ClassLoader loader) throws Exception {
        classLoader = loader;
        classesToTest = new ArrayList<String>();

        // for a pretty message
        String[] comps = classList.split("\\.");
        moduleName = comps[0];

        InputStream stream = loader.getResourceAsStream(classList);
        if (stream == null) Assert.fail("File not found in the classpath: " + classList);
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
            Assert.fail("The " + moduleName + " module is not fully tested! Missing number of method tests: " +
                    missingTestsCount + " in number of classes: " + uncoveredClassesCount);
        }
        return true;
    }

    private int checkClass(String className) {
        Class coreClass = loadClass(getClassName(className));
        if (coreClass.isInterface()) return 0;
        if (Modifier.isAbstract(coreClass.getModifiers())) return 0;

        int missingTestCount = 0;
        HashMap<String, TestMethod> methodAnnotations = new HashMap<String, TestMethod>();

        // lets get all the constructors in the class we're checking
        // we're going to skip private.
        Constructor[] constructors = coreClass.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            int modifiers = constructor.getModifiers();
            if (Modifier.isPrivate(modifiers)) continue;

            TestMethod testMethodAnnotation = (TestMethod)constructor.getAnnotation(TestMethod.class);

            if (constructor.getName().startsWith("access$")) {
                // skip this test
            } else if (constructor.getAnnotations().length == 0) {
                // the constructor is private or implicitly defined because the class does not specify
                // any constructor at all (like CDKConstants)
                // -> skip this test
            } else if (testMethodAnnotation == null) {
                // if a method does not have the annotation, it's missing a test
                System.out.println(className + toString(constructor) + " does not have a test method");
                missingTestCount++;
            } else methodAnnotations.put(constructor.getName(), testMethodAnnotation);
        }

        // lets get all the methods in the class we're checking
        // we're going to skip private.
        Method[] sourceMethods = coreClass.getDeclaredMethods();
        for (Method method : sourceMethods) {
        	if (method.isBridge()) continue;

        	int modifiers = method.getModifiers();
            if (Modifier.isPrivate(modifiers)) continue;

            TestMethod testMethodAnnotation = method.getAnnotation(TestMethod.class);

            if (method.getName().startsWith("access$")) {
            	// skip this test
            } else if (testMethodAnnotation == null) {
                // if a method does not have the annotation, it's missing a test
                System.out.println(className + "#" + toString(method) + " does not have a test method");
                missingTestCount++;
            } else methodAnnotations.put(method.getName(), testMethodAnnotation);
        }

        // get the test class for this class, as noted in the class annotation
        // and get a list of methods in the test class. We assume that if a class
        // does not have a TestClass annotation it is not tested, even though individual
        // methods might have TestMethod annotations
        TestClass testClassAnnotation = (TestClass) coreClass.getAnnotation(TestClass.class);
        if (testClassAnnotation == null) {
        	if (coreClass.getDeclaredMethods().length == 0 && coreClass.getDeclaredConstructors().length <= 1) {
        		// that's fine, no functionality; something like CDKConstants or DataFeatures
        		// the 1 is for the default constructor; maybe the tested class should be 'abstract final'?
        		return 0;
        	} else {
        		System.out.println(className + " did not have a TestClass annotation");
        		return methodAnnotations.size() + missingTestCount + 1;
        	}
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
        for (Method method : testMethods) {
            if (method.getAnnotation(org.junit.Test.class) != null)
                testMethodNames.add(method.getName());
        }

        // at this point we look at the superclass of the test class and pull
        // test methods from there. This is done since some test classes for IO
        // and decsriptors have test methods in the superclass, rather than all
        // individual subclasses
        Class superClass = testClass.getSuperclass();
        Method[] superMethods = superClass.getMethods();
        for (Method method : superMethods) {
            if (method.getAnnotation(org.junit.Test.class) != null)
                testMethodNames.add(method.getName());
        }

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
                    System.out.println(
                    	className + "#" + key + " has a test method annotation '" +
                    	token + "' which is not found in test class: " + testClass.getName());
                    missingTestCount++;
                }
            }
        }

        return missingTestCount;
    }

    private String toString(Method method) {
        StringBuffer methodString = new StringBuffer();
        methodString.append(method.getName()).append('(');
        Class[] classes = method.getParameterTypes();
        for (int i=0;i<classes.length; i++) {
            Class clazz = classes[i];
            methodString.append(clazz.getName().substring(clazz.getName().lastIndexOf('.')+1));
            if ((i+1)<classes.length) methodString.append(',');
        }
        methodString.append(')');
        return methodString.toString();
    }

    private String toString(Constructor constructor) {
        StringBuffer methodString = new StringBuffer();
        methodString.append('(');
        Class[] classes = constructor.getParameterTypes();
        for (int i=0;i<classes.length; i++) {
            Class clazz = classes[i];
            methodString.append(clazz.getName().substring(clazz.getName().lastIndexOf('.')+1));
            if ((i+1)<classes.length) methodString.append(',');
        }
        methodString.append(')');
        return methodString.toString();
    }

    private Class loadClass(String className) {
        Class loadedClass = null;
        try {
            loadedClass = classLoader.loadClass(className);
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            Assert.fail("Could not find class: " + exception.getMessage());
        } catch (NoSuchMethodError error) {
            Assert.fail("No such method in class: " + error.getMessage());
        }
        return loadedClass;
    }

    private static String convertSlash2Dot(String className) {
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
        return BASEPACKAGENAME + className;
    }

}