import com.sun.javadoc.*;
import java.util.*;
import java.io.*;

public class MakeClassesDotModDoclet {

    private final String omitPackageNamePart = "org.openscience.cdk.";

    private PrintWriter out;

    public MakeClassesDotModDoclet(PrintWriter out) {
        this.out = out;
    }

    public void process(RootDoc root) throws IOException {
        processPackages(root.specifiedPackages());
    }

    private void processPackages(PackageDoc[] pkgs) throws IOException {
        for (int i=0; i < pkgs.length; i++) {
            out.println("<-- package: " + pkgs[i] + " -->");
            processClasses(pkgs[i].ordinaryClasses());
            out.println();
        }
    }

    private void processClass(ClassDoc classDoc) throws IOException {
        String className = classDoc.qualifiedName().substring(omitPackageNamePart.length());
        String apiPath = toAPIPath(className);
        out.println("<!ENTITY " + className + " '<ulink url=\"api/" +
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
            PrintWriter out = new PrintWriter((Writer)new FileWriter("classes.mod"));
            MakeClassesDotModDoclet stats = new MakeClassesDotModDoclet(out);
            stats.process(root);
            out.flush();
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }

}
