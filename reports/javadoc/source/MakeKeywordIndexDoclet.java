import com.sun.javadoc.*;
import java.util.*;
import java.io.*;

public class MakeKeywordIndexDoclet {

    private Hashtable keywords;

    private PrintWriter out;

    public MakeKeywordIndexDoclet(PrintWriter out) {
        this.out = out;
        this.keywords = new Hashtable();
    }

    public void process(RootDoc root) throws IOException {
        processPackages(root.specifiedPackages());

        makeIndex();
    }

    private void makeIndex() throws IOException {
        out.println("<index><title>Keyword Index</title>");
        Enumeration words = keywords.keys();
        while (words.hasMoreElements()) {
            String keyword = (String)words.nextElement();
            out.println("  <indexentry>" + keyword + " " +
                        (String)keywords.get(keyword) +
                        "</indexentry>");
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
                String className = classes[i].qualifiedName().substring(20);
                if (this.keywords.containsKey(word)) {
                    this.keywords.put(word, this.keywords.get(word) + ", " +
                                            className);
                } else {
                    this.keywords.put(word, className);
                }
            }
        }
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
}
