package org.openscience.cdk.test.qsar.model.R2;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FilenameFilter;

/**
 * TestSuite that runs all Model tests.
 *
 * @author Rajarshi Guha
 * @cdk.module test-qsar
 */
public class QSARRModelTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("All QSAR R Based Modeling Tests");
        try {
            String os = System.getProperty("os.name").toLowerCase();

            // check for R on a Unix system
            if (os.startsWith("linux")
                    || os.startsWith("mac os")
                    || os.startsWith("solaris")
                    || os.startsWith("aix")
                    || os.startsWith("freebsd")) {

                String rhome = System.getenv("R_HOME");
                String ldlibrarypath = System.getenv("LD_LIBRARY_PATH");

                if (rhome == null || rhome.equals(""))
                    throw new RuntimeException("R_HOME must be set for the R tests to run");
                if (ldlibrarypath == null || ldlibrarypath.equals(""))
                    throw new RuntimeException("LD_LIBRARY_PATH must be set for the R tests to run");

                // ok so env vars seem to be set. Are they correct?
                if (!foundR(rhome))
                    throw new RuntimeException("R_HOME does not appear to be correctly set: " + rhome);
                if (!ldPathOK(ldlibrarypath))
                    throw new RuntimeException("LD_LIBRARY_HOME does not appear to be correctly set: " + ldlibrarypath);
            } else if (os.startsWith("windows")) {
                // what variables does windows need set? And what are they set to?
            }

            Class testClass = suite.getClass().getClassLoader().loadClass("org.openscience.cdk.test.qsar.model.R2.LinearRegressionModelTest");
            suite.addTest(new TestSuite(testClass));
            testClass = suite.getClass().getClassLoader().loadClass("org.openscience.cdk.test.qsar.model.R2.CNNRegressionModelTest");
            suite.addTest(new TestSuite(testClass));
            System.out.println("Found RJava, running R tests...");
        } catch (ClassNotFoundException exception) {
            System.out.println("RJava is not found, skipping R tests...");
        } catch (RuntimeException exception) {
            System.out.println("Required environment variable(s) were missing or not valid, skipping R tests ...");
            exception.printStackTrace();
        } catch (Exception exception) {
            System.out.println("Could not load an R model test: " + exception.getMessage());
            exception.printStackTrace();
        }
        return suite;
    }

    private static boolean foundR(String rhome) {
        String path1 = rhome + File.separator + "bin" + File.separator + "Rcmd.exe";
        String path2 = rhome + File.separator + "bin" + File.separator + "Rcmd";
        File file1 = new File(path1);
        File file2 = new File(path2);
        return file1.exists() || file2.exists();
    }

    private static boolean ldPathOK(String ldlibrarypath) {
        FilenameFilter filterJRIUnix = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equals("libjri.so");
            }
        };

        FilenameFilter filterLibRUnix = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equals("libR.so");
            }
        };

        boolean foundJRI = false;
        boolean foundR = false;

        String[] paths = ldlibrarypath.split(File.pathSeparator);
        for (int i = 0; i < paths.length; i++) {
            File dir = new File(paths[i]);
            String[] files1 = dir.list(filterJRIUnix);
            String[] files2 = dir.list(filterLibRUnix);
            if (files1 != null && files1.length > 0) foundJRI = true;
            if (files2 != null && files2.length > 0) foundR = true;
        }
        return foundJRI && foundR;
    }
}
