import com.sun.javadoc.*;
import java.util.*;
import java.io.*;

public class MakeKeywordIndexDoclet {

    private final String rootToAPI = "/api/";
    private final String omitPackageNamePart = "org.openscience.cdk.";

    private TreeMap keywords;

    private PrintWriter out;

    public MakeKeywordIndexDoclet(PrintWriter out) {
        this.out = out;
        this.keywords = new TreeMap(new LowerCaseComparator());
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
            out.println("    <primaryie>" + keyword + " " +
                        (String)keywords.get(keyword) +
                        " </primaryie>");
            out.println("  </indexentry>");
        }
        out.println("</index>");
    }

    private void processPackages(PackageDoc[] pkgs) throws IOException {
        for (int i=0; i < pkgs.length; i++) {
            processClasses(pkgs[i].ordinaryClasses());
        }
    }

    private void processClasses(ClassDoc[] classes) throws IOException {
        for (int i=0; i<classes.length; i++) {
            // process tags
            Tag[] tags = classes[i].tags("keyword");
            for (int j=0; j<tags.length; j++) {
                String word = tags[j].text();
                String className = classes[i].qualifiedName().substring(omitPackageNamePart.length());
                String markedUpClassName = "<ulink url=\"" + rootToAPI +
                                           toAPIPath(className) + "\">" +
                                           className + "</ulink>";
                if (this.keywords.containsKey(word)) {
                    this.keywords.put(word, this.keywords.get(word) + ", " +
                                            markedUpClassName);
                } else {
                    this.keywords.put(word, markedUpClassName);
                }
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
