/* Decompiled by Mocha from AtomContainer.class */
/* Originally compiled from AtomContainer.java */

package org.openscience.cdk;

import java.io.PrintStream;
import java.util.Vector;
import javax.vecmath.Point2d;

public class AtomContainer extends ChemObject implements Cloneable
{
    protected int atomCount;
    protected int bondCount;
    protected int growArraySize;
    protected Atom atoms[];
    protected Bond bonds[];

    public void setAtoms(Atom aatom[])
    {
        atoms = aatom;
    }

    public void setBonds(Bond abond[])
    {
        bonds = abond;
    }

    public void setAtomAt(int i, Atom atom)
    {
        atoms[i] = atom;
    }

    public void setBondAt(int i, Bond bond)
    {
        bonds[i] = bond;
    }

    public Atom[] getAtoms()
    {
        Atom aatom[] = new Atom[getAtomCount()];
        System.arraycopy(atoms, 0, aatom, 0, aatom.length);
        return aatom;
    }

    public Bond[] getBonds()
    {
        Bond abond[] = new Bond[getBondCount()];
        System.arraycopy(bonds, 0, abond, 0, abond.length);
        return abond;
    }

    public Atom getAtomAt(int i)
    {
        return atoms[i];
    }

    public int getAtomNumber(Atom atom)
        throws Exception
    {
        for (int i = 0; i < getAtomCount(); i++)
            if (getAtomAt(i) == atom)
                return i;
        throw new Exception("No such Atom");
    }

    public Bond getBondAt(int i)
    {
        return bonds[i];
    }

    public Bond getBond(Atom atom1, Atom atom2)
        throws Exception
    {
        for (int i = 0; i < getBondCount(); i++)
        {
            if (bonds[i].contains(atom1))
            {
                try
                {
                    if (bonds[i].getConnectedAtom(atom1) == atom2)
                        return bonds[i];
                }
                catch (Exception e)
                {
                    throw e;
                }
            }
        }
        System.out.println("atom1  " + atom1.toString() + "atom2  " + atom2.toString());
        throw new Exception("atoms not connected");
    }

    public Atom[] getConnectedAtoms(Atom atom)
    {
        Vector vector = new Vector();
        for (int i = 0; i < bondCount; i++)
        {
            Bond bond = bonds[i];
            if (bond.contains(atom))
                vector.addElement(bond.getConnectedAtom(atom));
        }
        Atom aatom[] = new Atom[vector.size()];
        vector.copyInto(aatom);
        return aatom;
    }

    public Bond[] getConnectedBonds(Atom atom)
    {
        Vector vector = new Vector();
        for (int i = 0; i < bondCount; i++)
            if (bonds[i].contains(atom))
                vector.addElement(bonds[i]);
        Bond abond[] = new Bond[vector.size()];
        vector.copyInto(abond);
        return abond;
    }

    public int getDegree(int i)
    {
        return getDegree(getAtomAt(i));
    }

    public int getDegree(Atom atom)
    {
        int i = 0;
        for (int j = 0; j < bondCount; j++)
        {
            Bond bond = bonds[j];
            if (bond.contains(atom))
                i++;
        }
        return i;
    }

    public void add(AtomContainer atomContainer)
    {
        for (int i = 0; i < atomContainer.getAtomCount(); i++)
            addAtom(atomContainer.getAtomAt(i));
        for (int j = 0; j < atomContainer.getBondCount(); j++)
            addBond(atomContainer.getBondAt(j));
    }

    public void addAtom(Atom atom)
    {
        if (atomCount + 1 >= atoms.length)
            growAtomArray();
        atoms[atomCount] = atom;
        atomCount++;
    }

    public void addBond(Bond bond)
    {
        if (bondCount + 1 >= bonds.length)
            growBondArray();
        bonds[bondCount] = bond;
        bondCount++;
    }

    public void remove(AtomContainer atomContainer)
        throws Exception
    {
        for (int i = 0; i < atomContainer.getAtomCount(); i++)
            removeAtom(atomContainer.getAtomAt(i));
        for (int j = 0; j < atomContainer.getBondCount(); j++)
            removeBond(atomContainer.getBondAt(j));
    }

    public void removeBond(int i)
    {
        for (int j = i; j < bondCount - 1; j++)
            bonds[j] = bonds[j + 1];
        bonds[bondCount - 1] = null;
        bondCount--;
    }

    public void removeBond(Bond bond)
    {
        for (int i = 0; i < bondCount; i++)
            if (bonds[i].equals(bond))
                removeBond(i);
    }

    public void removeAtom(int i)
    {
        for (int j = i; j < atomCount - 1; j++)
            atoms[j] = atoms[j + 1];
        atoms[atomCount - 1] = null;
        atomCount--;
    }

    public void removeAtom(Atom atom)
        throws Exception
    {
        int i = getAtomNumber(atom);
        removeAtom(i);
    }

    public void removeAllElements()
    {
        atoms = new Atom[growArraySize];
        bonds = new Bond[growArraySize];
        atomCount = 0;
        bondCount = 0;
    }

    public void addBond(int i1, int j, int k, int i2)
    {
        if (bondCount >= bonds.length)
            growBondArray();
        Bond bond = new Bond(getAtomAt(i1), getAtomAt(j), k, i2);
        addBond(bond);
    }

    public void addBond(int i, int j, int k)
    {
        if (bondCount >= bonds.length)
            growBondArray();
        Bond bond = new Bond(getAtomAt(i), getAtomAt(j), k);
        addBond(bond);
    }

    protected void growBondArray()
    {
        growArraySize = bonds.length;
        Bond abond[] = new Bond[bonds.length + growArraySize];
        System.arraycopy(bonds, 0, abond, 0, bonds.length);
        bonds = abond;
    }

    protected void growAtomArray()
    {
        growArraySize = atoms.length;
        Atom aatom[] = new Atom[atoms.length + growArraySize];
        System.arraycopy(atoms, 0, aatom, 0, atoms.length);
        atoms = aatom;
    }

    public int getAtomCount()
    {
        return atomCount;
    }

    public void setAtomCount(int i)
    {
        atomCount = i;
    }

    public int getBondCount()
    {
        return bondCount;
    }

    public AtomContainer getIntersection(AtomContainer atomContainer1)
    {
        AtomContainer atomContainer2 = new AtomContainer();
        for (int i = 0; i < getAtomCount(); i++)
            if (atomContainer1.contains(getAtomAt(i)))
                atomContainer2.addAtom(getAtomAt(i));
        for (int j = 0; j < getBondCount(); j++)
            if (atomContainer1.contains(getBondAt(j)))
                atomContainer2.addBond(getBondAt(j));
        return atomContainer2;
    }

    public boolean contains(Bond bond)
    {
        for (int i = 0; i < getBondCount(); i++)
            if (bond == bonds[i])
                return true;
        return false;
    }

    public boolean contains(Atom atom)
    {
        for (int i = 0; i < getAtomCount(); i++)
            if (atom == atoms[i])
                return true;
        return false;
    }

    public Point2d get2DCenter()
    {
        double d1 = 0.0;
        double d2 = 0.0;
        for (int i = 0; i < getAtomCount(); i++)
        {
            d1 += atoms[i].getPoint2D().x;
            d2 += atoms[i].getPoint2D().y;
        }
        Point2d point2d = new Point2d(d1 / atomCount, d2 / atomCount);
        return point2d;
    }

    public int[][] getConnectionMatrix()
        throws Exception
    {
        Bond bond = null;
        int aan[][] = new int[getAtomCount()][getAtomCount()];
        for (int k = 0; k < getBondCount(); k++)
        {
            bond = getBondAt(k);
            int i = getAtomNumber(bond.getAtomAt(0));
            int j = getAtomNumber(bond.getAtomAt(1));
            aan[i][j] = bond.getOrder();
            aan[j][i] = bond.getOrder();
        }
        return aan;
    }

    public String toString()
    {
        StringBuffer stringBuffer = new StringBuffer();
        System.out.println(new StringBuffer("Atomcount: ").append(getAtomCount()).toString());
        for (int i = 0; i < getAtomCount(); i++)
            stringBuffer.append(i + ". " + getAtomAt(i));
        System.out.println(new StringBuffer("Bondcount: ").append(getBondCount()).toString());
        for (int j = 0; j < getBondCount(); j++)
        {
            Bond bond = getBondAt(j);
            stringBuffer.append("Bond: ");
            for (int k = 0; k < bond.getAtomCount(); k++)
            {
                try
                {
                    stringBuffer.append(getAtomNumber(bond.getAtomAt(k)) + "   ");
                }
                catch (Exception e)
                {
                    stringBuffer.append("Inconsistent Bond Setting");
                    e.printStackTrace();
                }
            }
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }

    public Object clone()
    {
        AtomContainer atomContainer = null;
        try
        {
            atomContainer = (AtomContainer)super.clone();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
        atomContainer.atoms = (Atom[])atoms.clone();
        atomContainer.bonds = (Bond[])bonds.clone();
        return atomContainer;
    }

    public AtomContainer()
    {
        growArraySize = 10;
        atomCount = 0;
        bondCount = 0;
        atoms = new Atom[growArraySize];
        bonds = new Bond[growArraySize];
    }

    public AtomContainer(int i, int j)
    {
        growArraySize = 10;
        atomCount = i;
        bondCount = j;
        atoms = new Atom[i];
        bonds = new Bond[j];
    }
}
