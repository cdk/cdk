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

public class JavaDocStats {

    private int publicObjects = 0;
    private int privateObjects = 0;
    private int protectedObjects = 0;

    private int interfaces = 0;
    private int classes = 0;

    private int totalObjects = 0;
    private int objectsWithDescription = 0;

    private Hashtable authors;

    private PrintWriter out;

    public JavaDocStats(PrintWriter out) {
        this.out = out;
    }

    public void process(RootDoc root) throws IOException {
        processPackages(root.specifiedPackages());
    }

    private void processPackages(PackageDoc[] pkgs) throws IOException {
        for (int i=0; i < pkgs.length; i++) {
            // do per package statistics
            publicObjects = 0;
            privateObjects = 0;
            protectedObjects = 0;
            interfaces = 0;
            classes = 0;
            authors = new Hashtable();
            totalObjects = 0;
            objectsWithDescription = 0;

            // generate statistics
            processClasses(pkgs[i].allClasses());

            // output statistics
            out.println("Package " + pkgs[i].name());
            out.println("  Object types");
            out.println("    classes   : " + classes);
            out.println("    interfaces: " + interfaces);
            out.println("  Object accessibility");
            out.println("    public    : " + publicObjects);
            out.println("    private   : " + privateObjects);
            out.println("    protected : " + protectedObjects);
            out.println("  Object has description");
            out.println("    with      : " + objectsWithDescription);
            out.println("    without   : " + (totalObjects - objectsWithDescription));
            out.println("  Object authors stats");
            Enumeration authorNames = authors.keys();
            while (authorNames.hasMoreElements()) {
                String author = (String)authorNames.nextElement();
                out.println("    " + author + " : " + authors.get(author));
            }
        }
    }

    private void processClasses(ClassDoc[] classes) throws IOException {
        for (int i=0; i<classes.length; i++) {
            totalObjects++;

            // process basic Java stuff
            if ( classes[i].isInterface() )
                this.interfaces++;
            else
                this.classes++;
            if ( classes[i].isPublic() )
                this.publicObjects++;
            if ( classes[i].isProtected() )
                this.protectedObjects++;
            if ( classes[i].isPrivate() )
                this.privateObjects++;

            // has object a description?
            if (classes[i].commentText().length() > 0) {
                objectsWithDescription++;
            }

            // process tags
            Tag[] tags = classes[i].tags("author");
            for (int j=0; j<tags.length; j++) {
                String author = tags[j].text();
                if (this.authors.containsKey(author)) {
                    this.authors.put(author,
                                new Integer(((Integer)authors.get(author)).intValue()+1));
                } else {
                    this.authors.put(author, new Integer(1));
                }
            }
        }
    }

    public static boolean start(RootDoc root) {
        try {
            PrintWriter out = new PrintWriter((Writer)new FileWriter("javadoc.stats"));
            JavaDocStats stats = new JavaDocStats(out);
            stats.process(root);
            out.flush();
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }
}
