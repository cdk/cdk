/* Atom.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.layout;


import javax.vecmath.*;
import org.openscience.cdk.*;
import java.awt.Dimension;

public class LayoutTools
{

	/**
	 * Adds an automatically calculated offset to the coordinates of all atoms
	 * such that all coordinates are positive and the smallest x or y coordinate 
	 * is exactly zero.
	 *
	 * @param   molecule for which all the atoms are translated to positive coordinates
	 */
	public static void translateAllPositive(Molecule molecule)
	{
		double transX = 0,transY = 0;
		for (int i = 0; i < molecule.getAtomCount(); i++)
		{
			if (molecule.getAtomAt(i).getPoint2D().x < transX)
			{
				transX = molecule.getAtomAt(i).getPoint2D().x;
			}
			if (molecule.getAtomAt(i).getPoint2D().y < transY)
			{
				transY = molecule.getAtomAt(i).getPoint2D().y;
			}
		}
		translate2D(molecule,transX * -1,transY * -1);		
	}
	

	/**
	 * Translates the given molecule by the given Vector.
	 *
	 * @param   molecule  The molecule to be translated
	 * @param   transX  translation in x direction
	 * @param   transY  translation in y direction
	 */
	public static void translate2D(Molecule molecule, double transX, double transY)
	{
		translate2D(molecule, new Vector2d(transX, transY));
	}
	

	/**
	 * Multiplies all the coordinates of the atoms of the given molecule with the scalefactor.
	 *
	 * @param   molecule  The molecule to be scaled
	 */
	public static void scaleMolecule(Molecule molecule, double scaleFactor)
	{
		for (int i = 0; i < molecule.getAtomCount(); i++)
		{
			molecule.getAtomAt(i).getPoint2D().x *= scaleFactor;
			molecule.getAtomAt(i).getPoint2D().y *= scaleFactor;
		}
	}
	
    /**
	 * Scales a molecule such that it fills a given percentage of a given dimension
	 *
	 * @param   molecule  The molecule to be scaled
	 * @param   dim       The dimension to be filled
	 * @param   percentage  The percentage of the dimension to be filled
	 */
	public static void scaleMolecule(Molecule molecule, Dimension areaDim, double fillFactor)
	{
		Dimension molDim = get2DDimension(molecule);
		double widthFactor = (double)areaDim.width / (double)molDim.width;
		double heightFactor = (double)areaDim.height / (double)molDim.height;
		double scaleFactor = Math.min(widthFactor, heightFactor) * fillFactor;
		scaleMolecule(molecule, scaleFactor);
	}

	/**
	 * Returns the java.awt.Dimension of a molecule
	 *
	 * @param   molecule of which the dimension should be returned
	 * @return The java.awt.Dimension of this molecule
	 */
	public static Dimension get2DDimension(Molecule molecule)
	{
		double xOffset = 0, yOffset = 0;
		double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE, minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			if (molecule.getAtomAt(f).getX2D() > maxX)
				maxX = molecule.getAtomAt(f).getX2D();
			if (molecule.getAtomAt(f).getY2D() > maxY)
				maxY = molecule.getAtomAt(f).getY2D();
			if (molecule.getAtomAt(f).getX2D() < minX)
				minX = molecule.getAtomAt(f).getX2D();
			if (molecule.getAtomAt(f).getY2D() < minY)
				minY = molecule.getAtomAt(f).getY2D();
		}
		return new Dimension((int)(maxX - minX + 1),
			(int)(maxY - minY + 1));
	}


	/**
	 * Translates a molecule (i.e. its atom with the smallest coordinates)
	 *  to the orgin (0,0) and the translates it by the vector given by the dimension
	 *
	 * @param   molecule to be translated	
	 * @param   dimension that represents the translation vector 
	 */
	public static void translate2D(Molecule molecule, Vector2d vector)
	{
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			molecule.getAtomAt(f).getPoint2D().add(vector);
		}
	}
	
	/**
	 * Centers the molecule in the given area
	 *
	 * @param   molecule to be centered
	 * @param   areaDim in which the molecule is to be centered
	 */
	public static void center(Molecule molecule, Dimension areaDim)
	{
		Dimension molDim = get2DDimension(molecule);		
		int transX = (int)((areaDim.width - molDim.width) / 2);
		int transY = (int)((areaDim.height - molDim.height) / 2);
		translate2D(molecule, new Vector2d(transX, transY));
	}
	
}



