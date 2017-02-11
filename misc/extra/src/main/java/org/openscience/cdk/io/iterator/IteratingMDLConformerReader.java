package org.openscience.cdk.io.iterator;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ConformerContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Iterate over conformers of a collection of molecules stored in SDF format.
 * 
 * This class is analogous to the {@link org.openscience.cdk.io.iterator.IteratingSDFReader} except that
 * rather than return a single {@link org.openscience.cdk.interfaces.IAtomContainer} at each iteration this
 * class will return all the conformers for a given molecule at each iteration.
 * 
 * The class assumes that the molecules are stored in SDF format and that all conformers for a given
 * molecule are in sequential order.
 * 
 * Currently, the code uses the title of each molecule in the SD file to perform te conformer check
 * and so it is important that all conformers for a given molecule have the same title field, but
 * different from the title fields of conformers of other molecules. In
 * the future the class will allow the user to perform the check using either the title or a more
 * rigorous (but more time-consuming) graph isomorphism check.
 * 
 * Example usage is
 * <pre>
 * String filename = "/Users/rguha/conf2.sdf";
 * IteratingMDLConformerReader2 reader = new IteratingMDLConformerReader2(
 *         new FileReader(new File(filename)), DefaultChemObjectBuilder.getInstance());
 * while (reader.hasNext()) {
 *      ConformerContainer2 cc = (ConformerContainer2) reader.next();
 * }
 * 
 * // do something with this set of conformers
 * 
 * </pre>
 *
 * @cdk.module extra
 * @cdk.githash
 * @author Rajarshi Guha
 * @see org.openscience.cdk.ConformerContainer
 * @cdk.keyword file format SDF
 * @cdk.keyword conformer conformation
 */
public class IteratingMDLConformerReader implements Iterator {

    private IteratingSDFReader imdlr;
    private ConformerContainer container;
    private IAtomContainer     lastMol     = null;

    private boolean            hasNext     = false;
    private boolean            nextIsKnown = false;

    public IteratingMDLConformerReader(Reader in, IChemObjectBuilder builder) {
        imdlr = new IteratingSDFReader(in, builder);
        container = new ConformerContainer();
    }

    public IteratingMDLConformerReader(InputStream in, IChemObjectBuilder builder) {
        imdlr = new IteratingSDFReader(in, builder);
        container = new ConformerContainer();
    }

    @Override
    public boolean hasNext() {

        boolean slurpedConformers = false;
        if (lastMol != null) container = new ConformerContainer(lastMol);

        if (!nextIsKnown) {
            while (imdlr.hasNext()) {
                slurpedConformers = true;
                IAtomContainer mol = (IAtomContainer) imdlr.next();
                if (container.size() == 0)
                    container.add(mol);
                else {
                    if (container.getTitle().equals(mol.getProperty(CDKConstants.TITLE)))
                        container.add(mol);
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

    @Override
    public Object next() {
        if (!nextIsKnown) hasNext();
        nextIsKnown = false;
        if (!hasNext) throw new NoSuchElementException();

        return container; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
