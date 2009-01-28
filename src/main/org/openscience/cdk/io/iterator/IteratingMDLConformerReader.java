package org.openscience.cdk.io.iterator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ConformerContainer;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterate over conformers of a collection of molecules stored in SDF format.
 * <p/>
 * This class is analogous to the {@link org.openscience.cdk.io.iterator.IteratingMDLReader} except that
 * rather than return a single {@link org.openscience.cdk.interfaces.IMolecule} at each iteration this
 * class will return all the conformers for a given molecule at each iteration.
 * <p/>
 * The class assumes that the molecules are stored in SDF format and that all conformers for a given
 * molecule are in sequential order.
 * <p/>
 * Currently, the code uses the title of each molecule in the SD file to perform te conformer check
 * and so it is important that all conformers for a given molecule have the same title field, but
 * different from the title fields of conformers of other molecules. In
 * the future the class will allow the user to perform the check using either the title or a more
 * rigorous (but more time-consuming) graph isomorphism check.
 * <p/>
 * Example usage is
 * <pre>
 * String filename = "/Users/rguha/conf2.sdf";
 * IteratingMDLConformerReader2 reader = new IteratingMDLConformerReader2(
 *         new FileReader(new File(filename)), DefaultChemObjectBuilder.getInstance());
 * while (reader.hasNext()) {
 *      ConformerContainer2 cc = (ConformerContainer2) reader.next();
 * }
 * <p/>
 * // do something with this set of conformers
 * <p/>
 * </pre>
 *
 * @cdk.module extra
 * @cdk.svnrev  $Revision$
 * @author Rajarshi Guha
 * @see org.openscience.cdk.ConformerContainer
 * @cdk.keyword file format SDF
 * @cdk.keyword conformer conformation
 */
@TestClass("org.openscience.cdk.io.iterator.IteratingMDLConformerReaderTest")
public class IteratingMDLConformerReader implements Iterator {
    private IteratingMDLReader imdlr;
    private ConformerContainer container;
    private IMolecule lastMol = null;

    private boolean hasNext = false;
    private boolean nextIsKnown = false;

    @TestMethod("testSDF")
    public IteratingMDLConformerReader(Reader in, IChemObjectBuilder builder) {
        imdlr = new IteratingMDLReader(in, builder);
        container = new ConformerContainer();
    }

    @TestMethod("testSDF")
    public IteratingMDLConformerReader(InputStream in, IChemObjectBuilder builder) {
        imdlr = new IteratingMDLReader(in, builder);
        container = new ConformerContainer();
    }

    @TestMethod("testSDF")
    public boolean hasNext() {

        boolean slurpedConformers = false;
        if (lastMol != null)
            container = new ConformerContainer(lastMol);


        if (!nextIsKnown) {
            while (imdlr.hasNext()) {
                slurpedConformers = true;
                IMolecule mol = (IMolecule) imdlr.next();
                if (container.size() == 0) container.add(mol);
                else {
                    if (container.getTitle().equals(mol.getProperty(CDKConstants.TITLE))) container.add(mol);
                    else {
                        lastMol = mol;
                        hasNext = true;
                        break;
                    }
                }
            }
            hasNext = container.size() > 0 && slurpedConformers;
        }

        if (!hasNext) container = null;
        nextIsKnown = true;
        return hasNext;
    }

    @TestMethod("testSDF")
    public Object next() {
        if (!nextIsKnown) hasNext();
        nextIsKnown = false;
        if (!hasNext) throw new NoSuchElementException();


        return container;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @TestMethod("testRemove")
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

