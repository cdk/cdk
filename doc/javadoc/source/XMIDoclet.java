/* Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
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
 * JavaDoc Doclet that will output a XMI file, which serializes UML
 * diagrams.
 *
 * <p>The first versions were written for use with
 * Umbrello (http://uml.sf.net/).
 */
public class XMIDoclet {

    private PrintWriter out;

    /**
     * Constructor for he XMIDoclet.
     *
     * @param out PrintWriter to which the output is send.
     */
    public XMIDoclet(PrintWriter out) {
        this.out = out;
    }

    /**
     * Method that serializes RootDoc objects containing the
     * information from the Java source code.
     *
     * @param root a RootDoc
     */
    public void process(RootDoc root) throws IOException {
        // out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<XMI xmi.version=\"1.1\" >");

        out.println(" <XMI.header>");
        out.println("  <XMI.metamodel href=\"/vol/acfiles7/egonw/SourceForge/CDK/cdk/reports/javadoc/test2.xmi\" version=\"1.1\" name=\"UML\" />");
        out.println("  <XMI.model href=\"/vol/acfiles7/egonw/SourceForge/CDK/cdk/reports/javadoc/test2.xmi\" version=\"1\" name=\"/vol/acfiles7/egonw/SourceForge/CDK/cdk/reports/javadoc/test2.xmi\" />");
        out.println(" </XMI.header>");

        out.println(" <XMI.content>");
        out.println("  <docsettings viewid=\"-1\" documentation=\"\" uniqueid=\"1\" />");
        out.println("  <umlobjects>");
        generateUMLClass(root.specifiedPackages());
        out.println("  </umlobjects>");
        out.println("  <diagrams/>");
        out.println("  <listview>");
        out.println("   <listitem open=\"1\" type=\"800\" id=\"-1\" label=\"Views\" >");
        generateLogicalView(root.specifiedPackages());
        out.println("    <listitem open=\"1\" type=\"802\" id=\"-1\" label=\"Use Case View\" />");
        out.println("   </listitem>");
        out.println("  </listview>");
        out.println(" </XMI.content>");
        out.println("</XMI>");
    }

    /**
     * Method that serializes PackageDoc objects by serializing it's classes
     * into &lt;UML:Class> elements.
     *
     * @param pkgs an array of PackageDoc objects
     */
    private void generateUMLClass(PackageDoc[] pkgs) throws IOException {
        for (int i=0; i < pkgs.length; i++) {
            // generate statistics
            generateUMLClass(pkgs[i].ordinaryClasses(), pkgs[i].name());
        }
    }

    /**
     * Method that serializes ClassDoc objects into &lt;UML:Class> elements.
     *
     * @param pkgs an array of ClassDoc objects
     */
    private void generateUMLClass(ClassDoc[] classes, String pkgname) throws IOException {
        for (int i=0; i<classes.length; i++) {
            ClassDoc c = classes[i];
            out.println("    <UML:Class stereotype=\"\" " +
                                     "package=\"" + pkgname + "\" " +
                                     "xmi.id=\"" + i + "\" " +
                                     "abstract=\"0\" " +
                                     "documentation=\"\" " +
                                     "name=\"" + c.name() + "\" " +
                                     "scope=\"200\">");
            MethodDoc[] methods = c.methods();
            for (int j=0; j<methods.length; j++) {
                MethodDoc md = methods[j];
                out.println("     <UML:Operation stereotype=\"\" " +
                                                "xmi.id=\"" + (1000*i + j) + "\" " +
                                                "type=\"void\" " +
                                                "abstract=\"0\" " +
                                                "documentation=\"\" " +
                                                "name=\"" + md.name() + "\" " +
                                                "scope=\"200\" />");
            }
            out.println("    </UML:Class>");
        }
    }

    /**
     * Method that serializes PackageDoc objects by serializing it's classes
     * into &lt;listitem> elements (Umbrello specific)?.
     *
     * @param pkgs an array of PackageDoc objects
     */
    private void generateLogicalView(PackageDoc[] pkgs) throws IOException {
        for (int i=0; i < pkgs.length; i++) {
            // generate statistics
            generateLogicalView(pkgs[i].ordinaryClasses(), pkgs[i].name());
        }
    }

    /**
     * Method that serializes ClassDoc objects into &lt;listitem> elements
     * (Umbrello specific)?.
     *
     * @param pkgs an array of ClassDoc objects
     */
     private void generateLogicalView(ClassDoc[] classes, String pkgname) throws IOException {
        out.println("    <listitem open=\"1\" type=\"801\" id=\"-1\" label=\"Logical View\" >");
        for (int i=0; i<classes.length; i++) {
            ClassDoc c = classes[i];
            MethodDoc[] methods = c.methods();
            for (int j=0; j<methods.length; j++) {
                MethodDoc md = methods[j];
                out.println("    <listitem open=\"0\" " +
                                          "type=\"815\" " +
                                          "id=\"" + (1000*i + j) + "\" " +
                                          "label=\"" + md.name() + "\" />");
            }
        }
        out.println("    </listitem>");
    }

    /**
     * Method that must be implemented by JavaDoc Doclets.
     *
     * @param root the RootDoc element determined by JavaDoc
     */
    public static boolean start(RootDoc root) {
        try {
            String filename = "classes.xmi";
            PrintWriter out = new PrintWriter((Writer)new FileWriter(filename));
            XMIDoclet doclet = new XMIDoclet(out);
            doclet.process(root);
            out.flush();
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }
}
