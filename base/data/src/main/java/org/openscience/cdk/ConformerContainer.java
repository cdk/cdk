/* Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * A memory-efficient data structure to store conformers for a single molecule.
 * 
 * Since all the conformers for a given molecule only differ in their 3D coordinates
 * this data structure stores a single {@link IAtomContainer} containing the atom and bond
 * details and a List of 3D coordinate sets, each element being the set of 3D coordinates
 * for a given conformer.
 * 
 * The class behaves in many ways as a {@code List<IAtomContainer>} object, though a few methods are not
 * implemented. Though it is possible to add conformers by hand, this data structure is
 * probably best used in combination with {@link org.openscience.cdk.io.iterator.IteratingMDLConformerReader} as
 * <pre>
 * IteratingMDLConformerReader reader = new IteratingMDLConformerReader(
 *          new FileReader(new File(filename)),
 *          DefaultChemObjectBuilder.getInstance());
 * while (reader.hasNext()) {
 *     ConformerContainer cc = (ConformerContainer) reader.next();
 *     for (IAtomContainer conformer : cc) {
 *         // do something with each conformer
 *     }
 * }
 * </pre>
 *
 * @cdk.module data
 * @cdk.githash
 * @author Rajarshi Guha
 * @see org.openscience.cdk.io.iterator.IteratingMDLConformerReader
 */
public class ConformerContainer implements List<IAtomContainer> {

    private IAtomContainer  atomContainer = null;
    private String          title         = null;
    private List<Point3d[]> coordinates;

    private Point3d[] getCoordinateList(IAtomContainer atomContainer) {

        Point3d[] tmp = new Point3d[atomContainer.getAtomCount()];
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            IAtom atom = atomContainer.getAtom(i);
            if (atom.getPoint3d() == null) throw new IllegalArgumentException("Molecule must have 3D coordinates");
            tmp[i] = new Point3d(atom.getPoint3d());
        }
        return tmp;
    }

    public ConformerContainer() {
        coordinates = new ArrayList<Point3d[]>();
    }

    /**
     * Create a ConformerContainer object from a single molecule object.
     * 
     * Using this constructor, the resultant conformer container will
     * contain a single conformer. More conformers can be added using the
     * {@link #add} method.
     * 
     * Note that the constructor will use the title of the input molecule
     * when adding new molecules as conformers. That is, the title of any molecule
     * to be added as a conformer should match the title of the input molecule.
     *
     * @param atomContainer The base molecule (or first conformer).
     */
    public ConformerContainer(IAtomContainer atomContainer) {
        this.atomContainer = atomContainer;
        title = (String) atomContainer.getTitle();
        coordinates = new ArrayList<Point3d[]>();
        coordinates.add(getCoordinateList(atomContainer));
    }

    /**
     * Create a ConformerContainer from an array of molecules.
     * 
     * This constructor can be used when you have an array of conformers of a given
     * molecule. Note that this constructor will assume that all molecules in the
     * input array will have the same title.
     *
     * @param atomContainers The array of conformers
     */
    public ConformerContainer(IAtomContainer[] atomContainers) {
        if (atomContainers.length == 0) throw new IllegalArgumentException("Can't use a zero-length molecule array");

        // lets check that the titles match
        title = atomContainers[0].getTitle();
        for (IAtomContainer atomContainer : atomContainers) {
            String nextTitle = atomContainer.getTitle();
            if (title != null && !nextTitle.equals(title))
                throw new IllegalArgumentException("Titles of all molecules must match");
        }

        this.atomContainer = atomContainers[0];
        coordinates = new ArrayList<Point3d[]>();
        for (IAtomContainer container : atomContainers) {
            coordinates.add(getCoordinateList(container));
        }
    }

    /**
     * Get the title of the conformers.
     * 
     * Note that all conformers for a given molecule will have the same
     * title.
     *
     * @return The title for the conformers
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the number of conformers stored.
     *
     * @return The number of conformers
     */
    @Override
    public int size() {
        return coordinates.size();
    }

    /**
     * Checks whether any conformers are stored or not.
     *
     * @return true if there is at least one conformer, otherwise false
     */
    @Override
    public boolean isEmpty() {
        return coordinates.isEmpty();
    }

    /**
     * Checks to see whether the specified conformer is currently stored.
     * 
     * This method first checks whether the title of the supplied molecule
     * matches the stored title. If not, it returns false. If the title matches
     * it then checks all the coordinates to see whether they match. If all
     * coordinates match it returns true else false.
     *
     * @param o The IAtomContainer to check for
     * @return true if it is present, false otherwise
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * Gets an iterator over the conformers.
     *
     * @return an iterator over the conformers. Each iteration will return an IAtomContainer object
     *         corresponding to the current conformer.
     */
    @Override
    public Iterator<IAtomContainer> iterator() {
        return new CCIterator();
    }

    /**
     * Returns the conformers in the form of an array of IAtomContainers.
     * 
     * Beware that if you have a large number of conformers you may run out
     * memory during construction of the array since IAtomContainer's are not
     * light weight objects!
     *
     * @return The conformers as an array of individual IAtomContainers.
     */
    @Override
    public Object[] toArray() {
        IAtomContainer[] ret = new IAtomContainer[coordinates.size()];
        int index = 0;
        for (Point3d[] coords : coordinates) {
            try {
                IAtomContainer conf = (IAtomContainer) atomContainer.clone();
                for (int i = 0; i < coords.length; i++) {
                    IAtom atom = conf.getAtom(i);
                    atom.setPoint3d(coords[i]);
                }
                ret[index++] = conf;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public <IAtomContainer> IAtomContainer[] toArray(IAtomContainer[] ts) {
        throw new UnsupportedOperationException();
    }

    /**
     * Add a conformer to the end of the list.
     * 
     * This method allows you to add a IAtomContainer object as another conformer.
     * Before adding it ensures that the title of specific object matches the
     * stored title for these conformers. It will also check that the number of
     * atoms in the specified molecule match the number of atoms in the current set
     * of conformers.
     * 
     * This method will not check for duplicate conformers.
     *
     * @param atomContainer The new conformer to add.
     * @return true
     */
    @Override
    public boolean add(IAtomContainer atomContainer) {
        if (this.atomContainer == null) {
            this.atomContainer = atomContainer;
            title = (String) atomContainer.getTitle();
        }
        if (title == null) {
            throw new IllegalArgumentException("At least one of the input molecules does not have a title");
        }
        if (!title.equals(atomContainer.getTitle()))
            throw new IllegalArgumentException("The input molecules does not have the same title ('" + title
                    + "') as the other conformers ('" + atomContainer.getTitle() + "')");

        if (atomContainer.getAtomCount() != this.atomContainer.getAtomCount())
            throw new IllegalArgumentException("Doesn't have the same number of atoms as the rest of the conformers");

        coordinates.add(getCoordinateList(atomContainer));
        return true;
    }

    /**
     * Remove the specified conformer.
     *
     * @param o The conformer to remove (should be castable to IAtomContainer)
     * @return true if the specified conformer was present and removed, false if not found
     */
    @Override
    public boolean remove(Object o) {
        IAtomContainer atomContainer = (IAtomContainer) o;

        // we should never have a null conformer
        if (atomContainer == null) return false;

        int index = indexOf(atomContainer);
        if (index >= 0) {
            remove(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends IAtomContainer> atomContainers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int i, Collection<? extends IAtomContainer> iAtomContainers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get rid of all the conformers but keeps atom and bond information.
     */
    @Override
    public void clear() {
        coordinates.clear();
    }

    /**
     * Get the conformer at a specified position.
     *
     * @param i The position of the requested conformer
     * @return The conformer
     */
    @Override
    public IAtomContainer get(int i) {
        Point3d[] tmp = coordinates.get(i);
        for (int j = 0; j < atomContainer.getAtomCount(); j++) {
            IAtom atom = atomContainer.getAtom(j);
            atom.setPoint3d(tmp[j]);
        }
        return atomContainer;
    }

    @Override
    public IAtomContainer set(int i, IAtomContainer atomContainer) {
        if (!title.equals(atomContainer.getTitle()))
            throw new IllegalArgumentException(
                    "The input molecules does not have the same title as the other conformers");
        Point3d[] tmp = getCoordinateList(atomContainer);
        IAtomContainer oldAtomContainer = get(i);
        coordinates.set(i, tmp);
        return oldAtomContainer;
    }

    @Override
    public void add(int i, IAtomContainer atomContainer) {
        if (this.atomContainer == null) {
            this.atomContainer = atomContainer;
            title = atomContainer.getTitle();
        }

        if (!title.equals(atomContainer.getTitle()))
            throw new IllegalArgumentException(
                    "The input molecules does not have the same title as the other conformers");

        if (atomContainer.getAtomCount() != this.atomContainer.getAtomCount())
            throw new IllegalArgumentException("Doesn't have the same number of atoms as the rest of the conformers");

        Point3d[] tmp = getCoordinateList(atomContainer);
        coordinates.add(i, tmp);
    }

    /**
     * Removes the conformer at the specified position.
     *
     * @param i The position in the list to remove
     * @return The conformer that was at the specified position
     */
    @Override
    public IAtomContainer remove(int i) {
        IAtomContainer oldAtomContainer = get(i);
        coordinates.remove(i);
        return oldAtomContainer;
    }

    /**
     * Returns the lowest index at which the specific IAtomContainer appears in the list or -1 if is not found.
     * 
     * A given IAtomContainer will occur in the list if the title matches the stored title for
     * the conformers in this container and if the coordinates for each atom in the specified molecule
     * are equal to the coordinates of the corresponding atoms in a conformer.
     *
     * @param o The IAtomContainer whose presence is being tested
     * @return The index where o was found
     */
    @Override
    public int indexOf(Object o) {
        IAtomContainer atomContainer = (IAtomContainer) o;
        if (!atomContainer.getTitle().equals(title)) return -1;

        if (atomContainer.getAtomCount() != this.atomContainer.getAtomCount()) return -1;

        boolean coordsMatch;
        int index = 0;
        for (Point3d[] coords : coordinates) {
            coordsMatch = true;
            for (int i = 0; i < atomContainer.getAtomCount(); i++) {
                Point3d p = atomContainer.getAtom(i).getPoint3d();
                if (!(p.x == coords[i].x && p.y == coords[i].y && p.z == coords[i].z)) {
                    coordsMatch = false;
                    break;
                }
            }
            if (coordsMatch) return index;
            index++;
        }
        return -1;
    }

    /**
     * Returns the highest index at which the specific IAtomContainer appears in the list or -1 if is not found.
     * 
     * A given IAtomContainer will occur in the list if the title matches the stored title for
     * the conformers in this container and if the coordinates for each atom in the specified molecule
     * are equal to the coordinates of the corresponding atoms in a conformer.
     *
     * @param o The IAtomContainer whose presence is being tested
     * @return The index where o was found
     */
    @Override
    public int lastIndexOf(Object o) {
        IAtomContainer atomContainer = (IAtomContainer) o;
        if (!atomContainer.getTitle().equals(title)) return -1;

        if (atomContainer.getAtomCount() != coordinates.get(0).length) return -1;

        boolean coordsMatch;
        for (int j = coordinates.size() - 1; j >= 0; j--) {
            Point3d[] coords = coordinates.get(j);
            coordsMatch = true;
            for (int i = 0; i < atomContainer.getAtomCount(); i++) {
                Point3d p = atomContainer.getAtom(i).getPoint3d();
                if (!(p.x == coords[i].x && p.y == coords[i].y && p.z == coords[i].z)) {
                    coordsMatch = false;
                    break;
                }
            }
            if (coordsMatch) return j;
        }
        return -1;
    }

    @Override
    public ListIterator<IAtomContainer> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<IAtomContainer> listIterator(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IAtomContainer> subList(int i, int i1) {
        throw new UnsupportedOperationException();
    }

    private class CCIterator implements Iterator<IAtomContainer> {

        int current = 0;
        int last    = -1;

        @Override
        public boolean hasNext() {
            return current != coordinates.size();
        }

        @Override
        public IAtomContainer next() {
            Point3d[] tmp = coordinates.get(current);
            for (int j = 0; j < atomContainer.getAtomCount(); j++) {
                IAtom atom = atomContainer.getAtom(j);
                atom.setPoint3d(tmp[j]);
            }
            last = current++;
            return atomContainer;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
