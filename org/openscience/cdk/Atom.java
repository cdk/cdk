/* Decompiled by Mocha from Atom.class */
/* Originally compiled from Atom.java */

package org.openscience.cdk;

import java.io.PrintStream;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

public  class Atom extends ChemObject implements Cloneable {

    protected Element element;
    protected Point2d point2D;
    protected Point3d point3D;
    protected int hydrogenCount;
    protected int stereoParity;

    public void setElement(Element element)
    {
        this.element = element;
    }

    public void setHydrogenCount(int i)
    {
        hydrogenCount = i;
    }

    public void setPoint2D(Point2d point2d)
    {
        point2D = point2d;
    }

    public void setPoint3D(Point3d point3d)
    {
        point3D = point3d;
    }

    public void setStereoParity(int i)
    {
        stereoParity = i;
    }

    public Element getElement()
    {
        return element;
    }

    public int getHydrogenCount()
    {
        return hydrogenCount;
    }

    public Point2d getPoint2D()
    {
        return point2D;
    }

    public Point3d getPoint3D()
    {
        return point3D;
    }

    public double getX2D()
    {
        return point2D.x;
    }

    public double getY2D()
    {
        return point2D.y;
    }

    public double getX3D()
    {
        return point3D.x;
    }

    public double getY3D()
    {
        return point3D.y;
    }

    public double getZ3D()
    {
        return point3D.z;
    }

    public void setX2D(double d)
    {
        point2D.x = d;
    }

    public void setY2D(double d)
    {
        point2D.y = d;
    }

    public void setX3D(double d)
    {
        point3D.x = d;
    }

    public void setY3D(double d)
    {
        point3D.y = d;
    }

    public void setZ3D(double d)
    {
        point3D.z = d;
    }

    public int getStereoParity()
    {
        return stereoParity;
    }

    public String toString()
    {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Atom " + getElement().getSymbol() + "\n");
        stringBuffer.append("Hydrogen count: " + getHydrogenCount() + "\n");
        stringBuffer.append("Stereo Parity: " + getStereoParity() + "\n");
        stringBuffer.append("2D coordinates: " + getPoint2D() + "\n");
        stringBuffer.append("3D coordinates: " + getPoint3D() + "\n");
        return stringBuffer.toString();
    }

    public Object clone()
    {
        Atom atom = null;
        try
        {
            atom = (Atom)super.clone();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
        return atom;
    }

    public Atom(String string)
    {
        this(new Element(string));
    }

    public Atom(Element element)
    {
        this.element = element;
	point2D = new Point2d();
	point3D = new Point3d();
    }

    public Atom(Element element, Point3d point3d)
    {
        this(element);
        point3D = point3d;
    }

    public Atom(Element element, Point2d point2d)
    {
        this(element);
        point2D = point2d;
    }
}
