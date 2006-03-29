/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2006  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer;

import java.awt.Color;
import java.util.Vector;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.math.qm.Orbitals;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;

/**
 * @cdk.module java3d
 *
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.created 2001-07-20
 */
public class AcceleratedRenderer3DModel {
	private TransformGroup root = new TransformGroup();

	private Vector atomObjects = new Vector();
	private Vector atomTransfroms = new Vector();
	private Color[] atomcolors = new Color[]
  {
    Color.white   , Color.green, Color.green, Color.green, new Color(0.8f,0.52f,0.25f), 
		Color.darkGray, Color.green, Color.red  , Color.green, Color.green                , 
		Color.green   , Color.green, Color.green, Color.green, Color.green                ,  
		Color.yellow  , Color.green
  };

	private Vector bondObjects = new Vector();
	private Vector bondTransforms = new Vector();

	private OrbitalsRenderer3D orbitalsscene;

	private Point3d center;

	public AcceleratedRenderer3DModel(AtomContainer container)
	{
		Atom[] atoms = container.getAtoms();
		Bond[] bonds = container.getBonds();

		int i,j;
		Sphere sphere;
		TransformGroup transformgroup;
		for(i=0; i<atoms.length; i++)
		{
			//System.out.println("OZ["+i+"]="+atoms[i].getElement().getAtomicNumber());
			sphere = getAtomObject(0.2, atomcolors[atoms[i].getAtomicNumber()]);
			atomObjects.addElement(sphere);

			transformgroup = new TransformGroup(getShiftTransformation(atoms[i].getPoint3d()));
			transformgroup.addChild(sphere);
			atomTransfroms.addElement(transformgroup);

			root.addChild(transformgroup);
		}

		Cylinder cylinder;
		try
		{
			double[][] cm = ConnectionMatrix.getMatrix(container);
			for(i=0; i<cm.length; i++)
				for(j=0; j<i; j++)
					if (cm[i][j]>0)
					{
						cylinder = getBondObject(0.05, /*Color.darkGray*/Color.gray);
						bondObjects.addElement(cylinder);

						transformgroup = new TransformGroup(getStrainTransformation(
													atoms[i].getPoint3d(), atoms[j].getPoint3d()));
			      transformgroup.addChild(cylinder);
			      atomTransfroms.addElement(transformgroup);
      
			      root.addChild(transformgroup);
					}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		center = GeometryTools.get3DCenter(container);
		center.negate();
		setTransformation(center, 1, 0, 0);
	}

	public AcceleratedRenderer3DModel(AtomContainer container, Orbitals orbitals, int orbital)
  {
		this(container);

		orbitalsscene = new OrbitalsRenderer3D(orbitals, 40);
		if (orbital!=0)
			orbitalsscene.setCurrentOrbital(orbital);
		root.addChild(orbitalsscene);
	}

  public AcceleratedRenderer3DModel(Orbitals orbitals, int orbital)
  {
    orbitalsscene = new OrbitalsRenderer3D(orbitals, 40);
    if (orbital!=0)
      orbitalsscene.setCurrentOrbital(orbital);
    root.addChild(orbitalsscene);
  }

	public TransformGroup getRoot()
	{
		return root;
	}

	public void setTransformation(Matrix4d m)
	{
		root.setTransform(new Transform3D(m));
	}

	public void setTransformation(Point3d t, double scale, double phi, double theta)
  {
		Transform3D tansformation = new Transform3D();
		tansformation.setIdentity();
		tansformation.rotY(theta);
		tansformation.rotX(phi);
		tansformation.setScale(scale);
		tansformation.setTranslation(new Vector3d(t));
		
    root.setTransform(tansformation);
  }

	public void setCurrentOrbital(int orbital)
	{
		if (orbitalsscene!=null)
			orbitalsscene.setCurrentOrbital(orbital);
	}

	public void setOrbitalOpaque(boolean flag)
  {
    orbitalsscene.setOpaque(flag);
  }

	public Sphere getAtomObject(double radius, Color color)
  {
    Sphere sphere = new Sphere((float)radius,Primitive.GENERATE_NORMALS,30);

    Color3f sphereColor= new Color3f(color);
    ColoringAttributes sphereColorAttr = new ColoringAttributes();
    sphereColorAttr.setColor(sphereColor);
    Appearance sphereAppearance = new Appearance();
    sphereAppearance.setColoringAttributes(sphereColorAttr);
    Material material = new Material();
    material.setDiffuseColor(sphereColor);
    sphereAppearance.setMaterial(material);

    sphere.setAppearance(sphereAppearance);

		return sphere;
  }

	public Cylinder getBondObject(double radius, Color color)
  {
    Cylinder cylinder = new Cylinder((float)radius, 0.8f);

    Appearance appearance = new Appearance();
    Material material = new Material();
    material.setDiffuseColor(new Color3f(color));
    appearance.setMaterial(material);

    cylinder.setAppearance(appearance);

		return cylinder;
  }

	public Transform3D getShiftTransformation(Point3d p)
	{
		Transform3D transform = new Transform3D();
		transform.setTranslation(new Vector3d(p));
		return transform;
	}

	public Transform3D getStrainTransformation(Point3d p1, Point3d p2)
  {
		Vector3d v1 = new Vector3d(p1);
		Vector3d v2 = new Vector3d(p2);

  	Vector3d pdistanz = new Vector3d();
		pdistanz.sub(v1,v2);
    double length = pdistanz.length();
    
		Vector3d middlePoint = new Vector3d();
    middlePoint.x = (v1.x+v2.x)/2d;
    middlePoint.y = (v1.y+v2.y)/2d;
    middlePoint.z = (v1.z+v2.z)/2d;
		Vector3d relativePoint = new Vector3d();
    relativePoint.x = (2*(v1.x-middlePoint.x))/length;
    relativePoint.y = (2*(v1.y-middlePoint.y))/length;
    relativePoint.z = (2*(v1.z-middlePoint.z))/length;
    
		Vector3d orginal = new Vector3d(0,1,0);
    double angle = pdistanz.angle(orginal);
    
		Vector3d normale = new Vector3d();
    normale.cross(orginal,pdistanz);
    
		Transform3D transform = new Transform3D();
    transform.setIdentity();
    transform.setScale(new Vector3d(1d,length,1d));
    transform.setTranslation(middlePoint);
    transform.setRotation(new AxisAngle4d(normale.x, normale.y, normale.z, angle));
    return transform;
  }
}
 
