/* $Revision: 6707 $ $Author: egonw $ $Date: 2006-07-30 16:38:18 -0400 (Sun, 30 Jul 2006) $
 * 
 * Copyright (C) 2006  Egon Willighagen
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
package net.sf.cdk.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class that creates the ${build}/*.javafiles.
 * 
 * @author egonw
 */
public class MakeJavafilesFiles {

    private Map<String,List<String>> cdkPackages;
    private Map<String,List<String>> cdkSets;
   
    private String sourceDir = null;
    private String outputDir = null;

    public MakeJavafilesFiles(String sourceDir, String outputDir) {
        cdkPackages = new Hashtable<String,List<String>>();
        cdkSets = new Hashtable<String,List<String>>();
        this.sourceDir = sourceDir;
        this.outputDir = outputDir;
    }
	
    public void outputResults() {
        // output information in .javafiles and .classes files
        try {
			Iterator<String> keys = cdkPackages.keySet().iterator();
			while (keys.hasNext()) {
			    String key = (String)keys.next();
			    
			    // create one file for each cdk package = key
			    PrintWriter outJava = new PrintWriter(
			    	new FileWriter(outputDir + "/" + key + ".javafiles")
			    );
			    PrintWriter outClass = new PrintWriter(
			    	new FileWriter(outputDir + "/" + key + ".classes")
			    );
			    List<String> packageClasses = cdkPackages.get(key);
			    Iterator<String> classes = packageClasses.iterator();
			    while (classes.hasNext()) {
			        String packageClass = classes.next();
			        outJava.println(toAPIPath(packageClass) + ".java");
			        outClass.println(toAPIPath(packageClass) + "*.class");
			    }
			    outJava.flush(); outJava.close();
			    outClass.flush(); outClass.close();
			}
	        // output information in .set files
	        keys = cdkSets.keySet().iterator();
	        while (keys.hasNext()) {
	            String key = (String)keys.next();
	            
	            // create one file for each cdk package = key
	            PrintWriter outJava = new PrintWriter(
	            	new FileWriter(outputDir + "/" + key + ".set")
	            );
	            List<String> packageClasses = cdkSets.get(key);
	            Iterator<String> classes = packageClasses.iterator();
	            while (classes.hasNext()) {
	                String packageClass = (String)classes.next();
	                outJava.println(packageClass);
	            }
	            outJava.flush(); outJava.close();
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void processJavaSourceFiles(File path) {
    	if (path.isDirectory()) {
    		File[] files = path.listFiles();
    		for (int i=files.length;i>0;i--) {
    			processJavaSourceFiles(files[i-1]);
    		}
    	} else if (path.isFile() && path.getPath().endsWith(".java") &&
    			   !(path.getPath().indexOf("net/sf") != -1 ||
                             path.getPath().indexOf("net\\sf") != -1)) {
    		String[] moduleAndSet = getModuleAndSet(path);
    		if (moduleAndSet == null) {
    			System.out.println("Something wrong with the Java source file: " + path);    			
    		} else {
    			if (moduleAndSet[0] != null) {
        			addClassToCDKPackage(getSourceName(path), moduleAndSet[0]);
    			}
    			if (moduleAndSet[1] != null) {
    				addClassToCDKSet(getClassName(path), moduleAndSet[1]);
    			}
    		}
    	}
    }
    
    public String[] getModuleAndSet(File file) {
    	try {
    		String[] results = new String[2];
    		results[0] = "extra";
    		results[1] = null;
			BufferedReader reader = new BufferedReader(
				new FileReader(file)
			);
			String line = null;
			boolean inComment = false;
			while ((line = reader.readLine()) != null) {
				int index = line.indexOf("/**");
				if (index != -1) {
					inComment = true;
					if (line.substring(index).indexOf("**/") != -1) inComment = false;
				} else {
					if (line.indexOf("*/") != -1) inComment = false;
				}
				
				if (!inComment && (line.indexOf("public class") != -1 ||
						line.indexOf("public interface") != -1 ||
						line.indexOf("abstract class") != -1 ||
						line.indexOf("final class") != -1)) {
					// Nothing specified: return the default 'extra'
					reader.close();
					return results;
				}
				
				index = line.indexOf("@cdk.module");
				String name = "";
				if (index != -1) {
					index += 11;
					// skip the first chars
					while (Character.isWhitespace(line.charAt(index))) index++;
					while (index < line.length() && 
						   !Character.isWhitespace(line.charAt(index))) {
						name += line.charAt(index);
						index++;
					}
					results[0] = name;
				} else {
					index = line.indexOf("@cdk.set");
					String set = "";
					if (index != -1) {
						index += 11;
						// skip the first chars
						while (Character.isWhitespace(line.charAt(index))) index++;
						while (index < line.length() && 
							   !Character.isWhitespace(line.charAt(index))) {
							set += line.charAt(index);
							index++;
						}
						results[1] = set;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Syntax: MakeJavafilesFiles <sourceDir> <outputDir>");
			System.exit(-1);
		}
		
		MakeJavafilesFiles processor = new MakeJavafilesFiles(args[0], args[1]);
		
		processor.processJavaSourceFiles(new File(args[0]));
		processor.outputResults();
		
	}
	
    private String toAPIPath(String className) {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<className.length(); i++) {
            if (className.charAt(i) == '.') {
                sb.append('/');
            } else {
                sb.append(className.charAt(i));
            }
        }
        return sb.toString();
    }

    private String getSourceName(File classFile) {
    	// assume the pattern src/package/className.java
    	// return package/className
    	String tmp = classFile.getPath().substring(sourceDir.length()+1); 
        return tmp.substring(0, tmp.length()-5);
    }

    private String getClassName(File classFile) {
    	// assume the pattern src/package/className.java
    	// return package.className
    	StringBuffer sb = new StringBuffer();
    	String className = classFile.getPath().substring(sourceDir.length()+1);
        for (int i=0; i<className.length()-5; i++) {
            if (className.charAt(i) == '/' || className.charAt(i) == '\\') {
                sb.append('.');
            } else {
                sb.append(className.charAt(i));
            }
        }
        return sb.toString();
    }

    private void addClassToCDKPackage(String packageClass, String cdkPackageName) {
        List<String> packageClasses = cdkPackages.get(cdkPackageName);
        if (packageClasses == null) {
            packageClasses = new ArrayList<String>();
            cdkPackages.put(cdkPackageName, packageClasses);
        }
        packageClasses.add(packageClass);
    }

    private void addClassToCDKSet(String packageClass, String cdkPackageName) {
        List<String> packageClasses = cdkSets.get(cdkPackageName);
        if (packageClasses == null) {
            packageClasses = new ArrayList<String>();
            cdkSets.put(cdkPackageName, packageClasses);
        }
        packageClasses.add(packageClass);
    }

}
