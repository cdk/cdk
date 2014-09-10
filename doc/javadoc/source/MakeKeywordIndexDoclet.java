/* Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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

public class MakeKeywordIndexDoclet {

    private final String javaDocKeywordTag = "cdk.keyword";

    private final String rootToAPI = "/api/";
    private final String omitPackageNamePart = "org.openscience.cdk.";

    private TreeMap keywords;
    private TreeMap secondaryKeywords;
    private Hashtable primaryToSecondary;

    private PrintWriter out;

    public MakeKeywordIndexDoclet(PrintWriter out) {
        this.out = out;
        this.keywords = new TreeMap(new LowerCaseComparator());
        this.secondaryKeywords = new TreeMap(new LowerCaseComparator());
        this.primaryToSecondary = new Hashtable();
    }

    public void process(RootDoc root) throws IOException {
        processPackages(root.specifiedPackages());

        makeIndex();
    }

    private void makeIndex() throws IOException {
        out.println("<index><title>Keyword Index</title>");
        Iterator words = keywords.keySet().iterator();
        while (words.hasNext()) {
            String keyword = (String)words.next();
            out.println("  <indexentry>");
            out.println("    <primaryie>");
            out.println("      <keyword>" + keyword + "</keyword>");
            out.println("      " + (String)keywords.get(keyword));
            out.println("    </primaryie>");
            if (this.primaryToSecondary.containsKey(keyword)) {
                Vector v = (Vector)this.primaryToSecondary.get(keyword);
                Enumeration secondaryWords = v.elements();
                while (secondaryWords.hasMoreElements()) {
                    String completeWord = (String)secondaryWords.nextElement();
                    StringTokenizer st = new StringTokenizer(completeWord, ",");
                    String primaryWord = st.nextToken().trim();
                    String secondaryWord = st.nextToken().trim();
                    // there are secondary words
                    out.println("    <secondaryie>");
                    out.println("      <keyword>" + secondaryWord + "</keyword>");
                    out.println("      " + (String)secondaryKeywords.get(completeWord));
                    out.println("    </secondaryie>");
                }
            }
            out.println("  </indexentry>");
        }
        out.println("</index>");
    }

    private void processPackages(PackageDoc[] pkgs) throws IOException {
        for (int i=0; i < pkgs.length; i++) {
            processClasses(pkgs[i].allClasses());
        }
    }

    private void processKeyword(String word, String markedUpName) throws IOException {
        // System.out.println("Processing: " + word + " in " + markedUpName);
        if (word.indexOf(",") != -1) {
            StringTokenizer st = new StringTokenizer(word, ",");
            String primaryWord = st.nextToken().trim();
            String secondaryWord = st.nextToken().trim(); // dirty, should check!

            // add prim word to list of keywords if not available yet
            // i.e. "file format" is added as file format
            if (!this.keywords.containsKey(primaryWord)) {
                this.keywords.put(primaryWord, "");
            }
            // add secondary word to primary word
            // ie.e "file format, CML" is added as file format -> CML
            Vector v = new Vector();
            if (this.primaryToSecondary.containsKey(primaryWord)) {
                v = (Vector)this.primaryToSecondary.get(primaryWord);
                if (!v.contains(word)) {
                    v.add(word);
                }
            } else {
                v.add(word);
            }
            this.primaryToSecondary.put(primaryWord, v);
            // what is done here?
            if (this.secondaryKeywords.containsKey(word)) {
                this.secondaryKeywords.put(word,
                        this.secondaryKeywords.get(word) + "\n      " +
                        markedUpName);
            } else {
                this.secondaryKeywords.put(word, markedUpName);
            }
            // make copy based on secondary word, i.e.
            // "file format, CML" is placed as file format -> CML
            // and as CML
            if (this.keywords.containsKey(secondaryWord)) {
                this.keywords.put(secondaryWord, this.keywords.get(secondaryWord) + "\n      " +
                                        markedUpName);
            } else {
                this.keywords.put(secondaryWord, markedUpName);
            }
        } else {
            if (this.keywords.containsKey(word)) {
                String text = (String)this.keywords.get(word);
                if (text.length() > 0)
                    markedUpName =  text + "\n      " + markedUpName;
            }
            this.keywords.put(word, markedUpName);
        }
        // System.out.println("done");
    }

    private void processClass(ClassDoc classDoc) throws IOException {
        Tag[] tags = classDoc.tags(javaDocKeywordTag);
        for (int j=0; j<tags.length; j++) {
            String word = tags[j].text();
            String className = classDoc.qualifiedName().substring(omitPackageNamePart.length());
            String markedUpClassName = "<ulink url=\"" + rootToAPI +
                                    toAPIPath(className) + "\">" +
                                    className + "</ulink>";
            processKeyword(word, markedUpClassName);
        }
    }

    private void processMethod(MethodDoc methodDoc, String className) throws IOException {
        // System.out.println("Processing: " + methodDoc.qualifiedName());
        Tag[] tags = methodDoc.tags(javaDocKeywordTag);
        for (int j=0; j<tags.length; j++) {
            String word = tags[j].text();
            String methodName = methodDoc.name();
            String markedUpMethodName = "<ulink url=\"" + rootToAPI +
                                    toAPIPath(className) + "#" +
                                    methodName + "()\">" +
                                    className + "." +
                                    methodName + "()</ulink>";
            processKeyword(word, markedUpMethodName);
        }
        // System.out.println("done");
    }

    private void processClasses(ClassDoc[] classes) throws IOException {
        for (int i=0; i<classes.length; i++) {
            ClassDoc doc = classes[i];
            // process keyword tags
            processClass(doc);
            // process class methods
            String className = doc.qualifiedName().substring(omitPackageNamePart.length());
            MethodDoc[] methods = doc.methods();
            for (int j=0; j<methods.length; j++) {
                processMethod(methods[j], className);
            }
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
            PrintWriter out = new PrintWriter((Writer)new FileWriter("keyword.index.xml"));
            MakeKeywordIndexDoclet stats = new MakeKeywordIndexDoclet(out);
            stats.process(root);
            out.flush();
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }

    class LowerCaseComparator implements java.util.Comparator {

        public int compare(Object o1, Object o2) {
            if (o1 instanceof String && o2 instanceof String) {
                return ((String)o1).toLowerCase().compareTo(((String)o2).toLowerCase());
            } else if (o1 instanceof String) {
                return 1;
            } else {
                return -1;
            }
        }

        public boolean equals(Object o1, Object o2) {
            if (o1 instanceof String && o2 instanceof String) {
                return ((String)o1).toLowerCase().equals(((String)o2).toLowerCase());
            } else {
                return false;
            }
        }
    }
}
