/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA

 *
 */
import com.sun.javadoc.*;
import java.util.*;
import java.io.*;

/**
 * This class is used to make the files cdk/src/*.set files.
 */
public class MakeCDKSetFilesDoclet {

    private final String javaDocModuleTag = "cdk.set";
    private Hashtable cdkPackages;

    public MakeCDKSetFilesDoclet() {
        cdkPackages = new Hashtable();
    }

    public void process(RootDoc root) throws IOException {
        processPackages(root.specifiedPackages());

        // output information in .javafiles and .classes files
        Enumeration keys = cdkPackages.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            
            // create one file for each cdk package = key
            PrintWriter outJava = new PrintWriter((Writer)new FileWriter(key + ".set"));
            Vector packageClasses = (Vector)cdkPackages.get(key);
            Enumeration classes = packageClasses.elements();
            while (classes.hasMoreElements()) {
                String packageClass = (String)classes.nextElement();
                outJava.println(packageClass);
            }
            outJava.flush(); outJava.close();
        }
    }

    private void processPackages(PackageDoc[] pkgs) throws IOException {
        for (int i=0; i < pkgs.length; i++) {
            processClasses(pkgs[i].allClasses());
        }
    }

    private void addClassToCDKPackage(String packageClass, String cdkPackageName) {
        Vector packageClasses = (Vector)cdkPackages.get(cdkPackageName);
        if (packageClasses == null) {
            packageClasses = new Vector();
            cdkPackages.put(cdkPackageName, packageClasses);
        }
        packageClasses.addElement(packageClass);
    }
    
    private void processClass(ClassDoc classDoc) throws IOException {
        String className = classDoc.qualifiedName();
        // first deal with modules
        Tag[] tags = classDoc.tags(javaDocModuleTag);
        String cdkPackage = null;
        if (tags.length > 0) {
            cdkPackage = tags[0].text(); // a class can be in only one module!
            addClassToCDKPackage(className, cdkPackage);
        }
    }
    
    private void processClasses(ClassDoc[] classes) throws IOException {
        for (int i=0; i<classes.length; i++) {
            ClassDoc doc = classes[i];
            processClass(doc);
        }
    }

    public static boolean start(RootDoc root) {
        try {
            MakeCDKSetFilesDoclet doclet = new MakeCDKSetFilesDoclet();
            doclet.process(root);
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            return false;
        }
    }

}
