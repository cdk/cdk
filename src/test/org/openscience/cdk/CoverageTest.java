/* $Revision$ $Author$ $Date$    
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
package org.openscience.cdk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;

/**
 * This test class is <b>not</b> intended to be tested directly,
 * but serve as helper class for actual coverage testers.
 *
 * @cdk.module test
 */
abstract public class CoverageTest extends CDKTestCase {

    private final String basePackageName = "org.openscience.cdk.";
    
    private ClassLoader classLoader;
    private List<String> classesToTest;
    
    public CoverageTest(String name) {
        super(name);
        classesToTest = null;
    }

    protected void setUp() throws Exception {
        classLoader = this.getClass().getClassLoader();
    }

    /**
     * This method must be overwritten by subclasses.
     */
    public static Test suite() {
        return null;
    }

    protected void loadClassList(String classList) throws Exception {
        classesToTest = new ArrayList<String>();
        
        // get the src/core.javafiles file
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
        Iterator classes = classesToTest.iterator();
        while (classes.hasNext()) {
            String className = (String)classes.next();
            int errors = checkClass(className);
            missingTestsCount += errors;
            if (errors > 0) uncoveredClassesCount++;
        }
        if (missingTestsCount > 0) {
            fail("The module is not fully tested! Missing number of method tests: " + 
                 missingTestsCount + " in number of classes: " + uncoveredClassesCount);
        }
        return true;
    }
    
    private int checkClass(String className) {
        // logger.debug("Checking : " + className);
        
        // load both classes
        Class coreClass = loadClass(getClassName(className));

        if (!coreClass.isInterface()) {
            Class testClass = loadClass(getTestClassName(className));
            
            int missingTestsCount = 0;

            // make map of methods in the test class
            List<String> testMethodNames = new ArrayList<String>();
            Method[] testMethods = testClass.getMethods();
            for (int i=0; i<testMethods.length; i++) {
                testMethodNames.add(testMethods[i].getName());
            }
            
            
            // now process the methods of the class to be tested
            // now the methods.
            boolean nonstaticMethods = false;
            Method[] methods = coreClass.getDeclaredMethods();
            for (int i=0; i<methods.length; i++) {
                int modifiers = methods[i].getModifiers();
                if (!Modifier.isPrivate(modifiers) && !Modifier.isProtected(modifiers) &&
                    !removePackage(methods[i].getName()).startsWith("access")) {
                    String testMethod = "test" + capitalizeName(removePackage(methods[i].getName()));
                    Class[] paramTypes = methods[i].getParameterTypes();
                    for (int j=0; j<paramTypes.length; j++) {
                        if (paramTypes[j].isArray()) {
                        	if (paramTypes[j].getComponentType().isArray()) {
                        		testMethod = testMethod + "_array" + 
                        			stripBrackets(removePackage(paramTypes[j].getComponentType().getSimpleName())) +
                        			removePackage(paramTypes[j].getComponentType().getComponentType().getName());
                        	} else {
                        		testMethod = testMethod + "_array" + removePackage(paramTypes[j].getComponentType().getName());
                        	}
                        } else {
                            testMethod = testMethod + "_" + removePackage(paramTypes[j].getName());
                        }
                    }
                    // replace '$' with '_'
                    testMethod = replaceFunnyCharacters(testMethod);
                    if (!testMethod.equals("testClass$_String")) {
                    	if (!testMethodNames.contains(testMethod)) {
                    		System.out.println(removePackage(coreClass.getName()) + ": missing the expected test method: " + testMethod);
                    		missingTestsCount++;
                    	}
                    	if (!Modifier.isStatic(modifiers)) nonstaticMethods = true;
                    }
                }
            }
            
            // second the constructors
            // only test if public nonstatic methods are present in the class
            if (nonstaticMethods) {
            	
            	Constructor[] constructors = coreClass.getDeclaredConstructors();
            	for (int i=0; i<constructors.length; i++) {
            		int modifiers = constructors[i].getModifiers();
            		if (!Modifier.isPrivate(modifiers) && !Modifier.isProtected(modifiers)) {
            			String testMethod = "test" + capitalizeName(removePackage(constructors[i].getName()));
            			Class[] paramTypes = constructors[i].getParameterTypes();
            			for (int j=0; j<paramTypes.length; j++) {
            				if (paramTypes[j].isArray()) {
                            	if (paramTypes[j].getComponentType().isArray()) {
                            		testMethod = testMethod + "_array" + 
                            			stripBrackets(removePackage(paramTypes[j].getComponentType().getSimpleName())) +
                            			removePackage(paramTypes[j].getComponentType().getComponentType().getName());
                            	} else {
                            		testMethod = testMethod + "_array" + removePackage(paramTypes[j].getComponentType().getName());
                            	}
            				} else {
            					testMethod = testMethod + "_" + removePackage(paramTypes[j].getName());
            				}
            			}
            			testMethod = replaceFunnyCharacters(testMethod);
                        if (!testMethod.equals("testClass$_String")) {
                        	if (!testMethodNames.contains(testMethod)) {
                        		System.out.println(removePackage(coreClass.getName()) + ": missing the expected test method: " + testMethod);
                        		missingTestsCount++;
                        	}
                        }
            		}
            	}
            }
            
            return missingTestsCount;
        } else {
            // interfaces should not be tested
            return 0;
        }
    }

	private String replaceFunnyCharacters(String testMethod) {
	    if (testMethod.indexOf('$') != -1) {
	    	StringBuffer output = new StringBuffer();
	    	for (int j=0; j<testMethod.length(); j++) {
	    		char ch = testMethod.charAt(j);
	    		if (ch == '$') {
	    			if (j+1 == testMethod.length() || testMethod.charAt(j+1) == '_') {
	    				// drop if it's the last char, or if the next is an underscore too
	    			} else {
	    				output.append('_');
	    			}
	    		} else {
	    			output.append(ch);
	    		}
	    	}
	    	testMethod = output.toString();
	    }
	    return testMethod;
    }
    
    private String stripBrackets(String string) {
		// remove the [] at the end 
		return string.substring(0,string.length()-2);
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
        for (int i=0; i<className.length(); i++) {
            if (className.charAt(i) == '/') {
                sb.append('.');
            } else {
                sb.append(className.charAt(i));
            }
        }
        return sb.toString();
    }
    
    private String getTestClassName(String className) {
        return basePackageName + className + "Test";
    }
    
    private String getClassName(String className) {
        return basePackageName + className;
    }

}
