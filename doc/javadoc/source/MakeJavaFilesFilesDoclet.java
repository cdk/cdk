import com.sun.javadoc.*;
import java.util.*;
import java.io.*;

/**
 * This class is used to make the files cdk/src/*.javafiles .
 */
public class MakeJavaFilesFilesDoclet {

    private Hashtable cdkPackages;

    public MakeJavaFilesFilesDoclet() {
        cdkPackages = new Hashtable();
    }

    public void process(RootDoc root) throws IOException {
        processPackages(root.specifiedPackages());
        Enumeration keys = cdkPackages.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            
            // create one file for each cdk package = key
            PrintWriter out = new PrintWriter((Writer)new FileWriter(key + ".javafiles"));
            Vector packageClasses = (Vector)cdkPackages.get(key);
            Enumeration classes = packageClasses.elements();
            while (classes.hasMoreElements()) {
                String packageClass = (String)classes.nextElement();
                out.println(packageClass);
            }
            out.flush();
            out.close();
        }
    }

    private void processPackages(PackageDoc[] pkgs) throws IOException {
        for (int i=0; i < pkgs.length; i++) {
            processClasses(pkgs[i].ordinaryClasses());
        }
    }

    private void addClassToCDKPackage(String packageClass, String cdkPackageName) {
        Vector packageClasses = (Vector)cdkPackages.get(cdkPackageName);
        if (packageClasses == null) {
            packageClasses = new Vector();
            cdkPackages.put(cdkPackageName, packageClasses);
        }
        packageClasses.addElement(packageClass);
    }
    
    private void processClass(ClassDoc classDoc) throws IOException {
        String className = classDoc.qualifiedName();
        Tag[] tags = classDoc.tags("cdkPackage");
        if (tags.length > 0) {
            addClassToCDKPackage(className, tags[0].text());
        } else {
            // ok, if not given then it is part of cdk-extra
            addClassToCDKPackage(className, "extra");
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
            MakeJavaFilesFilesDoclet doclet = new MakeJavaFilesFilesDoclet();
            doclet.process(root);
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            return false;
        }
    }

}
