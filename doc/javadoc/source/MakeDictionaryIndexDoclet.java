/* Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 */
package net.sf.cdk.tools;

import com.sun.javadoc.*;
import java.util.*;
import java.io.*;

/**
 * This class makes an overview of which classes refer to which entry in
 * which dictionary.
 */
public class MakeDictionaryIndexDoclet {

    private final String webUrl = "http://cdk.sf.net/";

    private final String javaDocDictRefTag = "cdk.dictref";
    private Hashtable jarDependencies;

    public MakeDictionaryIndexDoclet() {
        jarDependencies = new Hashtable();
    }

    private String toAPIPath(String className) {
        StringBuffer sb = new StringBuffer();
        className = className;
        for (int i=0; i<className.length(); i++) {
            if (className.charAt(i) == '.') {
                sb.append('/');
            } else {
                sb.append(className.charAt(i));
            }
        }
        return sb.toString();
    }

    public void process(RootDoc root) throws IOException {
        processPackages(root.specifiedPackages());

        // the dictionary references
        PrintWriter out = new PrintWriter((Writer)new FileWriter("cdk.dictref.xml"));
        out.println("<dictRefs>");
        Enumeration keys = jarDependencies.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            StringTokenizer tokenizer = new StringTokenizer(key, ":");
            if (tokenizer.countTokens() != 2) {
                out.println("  <!-- unparsable dictref: " + key + " -->");
            } else {
                String dictCode = tokenizer.nextToken();
                String entry = tokenizer.nextToken();
                Vector jarClasses = (Vector)jarDependencies.get(key);
                Enumeration classes = jarClasses.elements();
                while (classes.hasMoreElements()) {
                    out.println("  <dictRef>");
                    out.println("    <dictionary>" + dictCode + "</dictionary>");
                    out.println("    <entry>" + entry + "</entry>");
                    String packageClass = (String)classes.nextElement();
                    out.println("    <class>" + packageClass.substring(16) + "</class>");
                    out.println("    <api>api/" + toAPIPath(packageClass) + ".html</api>");
                    out.println("  </dictRef>");
                }
            }
        }
        out.println("</dictRefs>");
        out.flush(); out.close();
    }

    private void processPackages(PackageDoc[] pkgs) throws IOException {
        for (int i=0; i < pkgs.length; i++) {
            processClasses(pkgs[i].allClasses());
        }
    }

    private void addClass(Hashtable table, String packageClass, String cdkPackageName) {
        Vector packageClasses = (Vector)table.get(cdkPackageName);
        if (packageClasses == null) {
            packageClasses = new Vector();
            table.put(cdkPackageName, packageClasses);
        }
        packageClasses.addElement(packageClass);
    }

    private void processClass(ClassDoc classDoc) throws IOException {
        if (classDoc == null) return;

        String className = classDoc.qualifiedName();
        // first deal with class tags
        Tag[] tags = classDoc.tags(javaDocDictRefTag);
        if (tags.length > 0) {
            for (int i=0; i<tags.length; i++) {
                addClass(jarDependencies, className, tags[i].text());
            }
        }
    }

    private void processMethod(MethodDoc methodDoc, String className) throws IOException {
        // System.out.println("Processing: " + methodDoc.qualifiedName());
        Tag[] tags = methodDoc.tags(javaDocDictRefTag);
        for (int j=0; j<tags.length; j++) {
            String word = tags[j].text();
            // System.out.println("tag text: " + word);
            addClass(jarDependencies, className, word);
        }
        // System.out.println("done");
    }

    private void processClasses(ClassDoc[] classes) throws IOException {
        for (int i=0; i<classes.length; i++) {
            ClassDoc doc = classes[i];
            processClass(doc);
            // process class methods
            MethodDoc[] methods = doc.methods();
            for (int j=0; j<methods.length; j++) {
                processMethod(methods[j], doc.qualifiedName());
            }
        }
    }

    public static boolean start(RootDoc root) {
        try {
            MakeDictionaryIndexDoclet doclet = new MakeDictionaryIndexDoclet();
            doclet.process(root);
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            return false;
        }
    }

}
