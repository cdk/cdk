import com.sun.javadoc.*;
import java.io.*;

public class JavaDocStats {

    private PrintWriter out;

    public JavaDocStats(PrintWriter out) {
        this.out = out;
    }

    public void process(RootDoc root) throws IOException {
        processPackages(root.specifiedPackages());
    }

    private void processPackages(PackageDoc[] pkgs) throws IOException {
        for (int i=0; i < pkgs.length; i++) {
            processClasses(pkgs[i].ordinaryClasses());
        }
    }

    private void processClasses(ClassDoc[] classes) throws IOException {
        for (int i=0; i<classes.length; i++) {
            // System.out.println("Processing: " + classes[i].qualifiedName());

            // first field
            out.print(classes[i].qualifiedName() + " ");

            // second field
            if ( classes[i].isInterface() )
                out.print("interface ");
            else
                out.print("class ");

            // third field
            if ( classes[i].isPublic() )
                out.print("public ");
            if ( classes[i].isProtected() )
                out.print("protected ");
            if ( classes[i].isPrivate() )
                out.print("private ");

            // 4th field: extends
            out.print(classes[i].superclass() + " ");


            // 5th field: to which package does this class belong
            out.print(classes[i].containingPackage().toString() + " ");

            out.println();
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
