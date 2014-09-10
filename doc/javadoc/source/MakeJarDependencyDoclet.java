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
 * This class makes an overview of which classes depend on which jar.
 */
public class MakeJarDependencyDoclet {

    private final String webUrl = "http://cdk.sf.net/";

    private final String javaDocBuildDependsTag = "cdk.builddepends";
    private final String javaDocDependsTag = "cdk.depends";
    private Hashtable jarBuildDependencies;
    private Hashtable jarDependencies;

    public MakeJarDependencyDoclet() {
        jarBuildDependencies = new Hashtable();
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

        // the build dependencies
        PrintWriter out = new PrintWriter((Writer)new FileWriter("builddepends.xml"));
        out.println("<variablelist><title>Build Dependencies</title>");
        Enumeration keys = jarBuildDependencies.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            out.println("  <varlistentry>");
            out.println("    <term>" + key + "</term>");
            Vector jarClasses = (Vector)jarBuildDependencies.get(key);
            Enumeration classes = jarClasses.elements();
            out.println("    <listitem><para>");
            while (classes.hasMoreElements()) {
                String packageClass = (String)classes.nextElement();
                out.println("      <ulink url=\"" + webUrl + "api/" +
                    toAPIPath(packageClass) + "\">" + packageClass + "</ulink>");
            }
            out.println("    </para></listitem>");
            out.println("  </varlistentry>");
        }
        out.println("</variablelist>");
        out.flush(); out.close();
        // the runtime dependencies
        out = new PrintWriter((Writer)new FileWriter("depends.xml"));
        out.println("<variablelist><title>Run-time Dependencies</title>");
        keys = jarDependencies.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            out.println("  <varlistentry>");
            out.println("    <term>" + key + "</term>");
            Vector jarClasses = (Vector)jarDependencies.get(key);
            Enumeration classes = jarClasses.elements();
            out.println("    <listitem><para>");
            while (classes.hasMoreElements()) {
                String packageClass = (String)classes.nextElement();
                out.println("      <ulink url=\"" + webUrl + "api/" +
                    toAPIPath(packageClass) + "\">" + packageClass + "</ulink>");
            }
            out.println("    </para></listitem>");
            out.println("  </varlistentry>");
        }
        out.println("</variablelist>");
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
        // first deal with build dependencies
        Tag[] tags = classDoc.tags(javaDocBuildDependsTag);
        if (tags.length > 0) {
            for (int i=0; i<tags.length; i++) {
                addClass(jarBuildDependencies, className, tags[i].text());
            }
        }
        // then deal with runtime dependencies
        tags = classDoc.tags(javaDocDependsTag);
        if (tags.length > 0) {
            for (int i=0; i<tags.length; i++) {
                addClass(jarDependencies, className, tags[i].text());
            }
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
            MakeJarDependencyDoclet doclet = new MakeJarDependencyDoclet();
            doclet.process(root);
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            return false;
        }
    }

}
