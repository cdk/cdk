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

public class MakeClassesDotWebDotModDoclet {

    private final String omitPackageNamePart = "org.openscience.cdk.";
    private final String webUrl = "http://cdk.sf.net/";

    private PrintWriter out;

    public MakeClassesDotWebDotModDoclet(PrintWriter out) {
        this.out = out;
    }

    public void process(RootDoc root) throws IOException {
        processPackages(root.specifiedPackages());
    }

    private void processPackages(PackageDoc[] pkgs) throws IOException {
        for (int i=0; i < pkgs.length; i++) {
            out.println("<!-- package: " + pkgs[i] + " -->");
            processClasses(pkgs[i].allClasses());
            out.println();
        }
    }

    private void processClass(ClassDoc classDoc) throws IOException {
        String className = classDoc.qualifiedName().substring(omitPackageNamePart.length());
        String apiPath = toAPIPath(className);
        out.println("<!ENTITY " + className + " '<ulink url=\"" +
                    webUrl + "api/" +
                    apiPath + "\">" + className + "</ulink>'>");
    }

    private void processClasses(ClassDoc[] classes) throws IOException {
        for (int i=0; i<classes.length; i++) {
            ClassDoc doc = classes[i];
            processClass(doc);
        }
    }

    private String toAPIPath(String className) {
        StringBuffer sb = new StringBuffer();
        className = omitPackageNamePart + className;
        for (int i=0; i<className.length(); i++) {
            if (className.charAt(i) == '.') {
                sb.append('/');
            } else {
                sb.append(className.charAt(i));
            }
        }
        sb.append(".html");
        return sb.toString();
    }

    public static boolean start(RootDoc root) {
        try {
            PrintWriter out = new PrintWriter((Writer)new FileWriter("classes.web.mod"));
            MakeClassesDotWebDotModDoclet stats = new MakeClassesDotWebDotModDoclet(out);
            stats.process(root);
            out.flush();
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }

}
