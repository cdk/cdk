/*
 * OrbitalsRenderer3DTest.java
 *
 * Autor: Stephan Michels
 * EMail: stephan@vern.chem.tu-berlin.de
 * Datum: 9.6.2001
 *
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.test.renderer;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.openscience.cdk.Atom;
import org.openscience.cdk.math.Matrix;
import org.openscience.cdk.math.Vector;
import org.openscience.cdk.math.qm.GaussiansBasis;
import org.openscience.cdk.math.qm.Orbitals;
import org.openscience.cdk.renderer.AcceleratedRenderer3D;
import org.openscience.cdk.renderer.AcceleratedRenderer3DModel;

/**
 * This class test the OrbitalRenderer3D
 *
 * @cdk.module test-java3d
 *
 * @author     benedikta
 */
public class OrbitalsRenderer3DTest {
	private AcceleratedRenderer3DModel model;

  private int[] nx = new int[]{0, 1, 1};
  private int[] ny = new int[]{0, 0, 1};
  private int[] nz = new int[]{0, 0, 1};

  private double[] alpha = new double[]{1d, 1d, 1d};

  private Vector[] r = new Vector[]
  {
    new Vector(new double[]{0d,0d,0d}),
    new Vector(new double[]{0d,0d,0d}),
    new Vector(new double[]{0d,0d,0d})
  };

  private double[][] orbitalvalues = new double[][]
  {
    {1d, 0d, 0d},
    {0d, 1d, 0d},
    {0d, 0d, 4d}
  };

	/**
	 *  Constructor for the OrbitalsRenderer3DTest object
	 */
	public OrbitalsRenderer3DTest() 
  {
    GaussiansBasis basis = new GaussiansBasis(nx,ny,nz,alpha,r,new Atom[0]);
    Matrix coeff = new Matrix(orbitalvalues);
	  Orbitals orbitals = new Orbitals(basis, coeff);

	  JFrame frame = new JFrame("OrbitalsRenderer3DTest");
	  frame.getContentPane().setLayout(new BorderLayout());

    model = new AcceleratedRenderer3DModel(orbitals, 0);
    AcceleratedRenderer3D renderer = new AcceleratedRenderer3D(model);

    frame.getContentPane().add(renderer, BorderLayout.CENTER);
  
    JComboBox orbitalChooser = new JComboBox();
    orbitalChooser.setLightWeightPopupEnabled(false);
		for (int i = 0; i < coeff.rows; i++) 
    {
		  orbitalChooser.addItem((i + 1) + ".Orbital");
		}

		orbitalChooser.addItemListener(new ItemListener() 
    {
		  public void itemStateChanged(ItemEvent e) 
      {
		    model.setCurrentOrbital(((JComboBox) e.getSource()).getSelectedIndex());
			}
		});
		frame.getContentPane().add(orbitalChooser, BorderLayout.SOUTH);

		JCheckBox opaque = new JCheckBox("Opaque", true);
		opaque.addItemListener(new ItemListener() 
    {
		  public void itemStateChanged(ItemEvent e) 
      {
			  model.setOrbitalOpaque(((JCheckBox) e.getSource()).isSelected());
			}
			
    });
		frame.getContentPane().add(opaque, BorderLayout.NORTH);

		//frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}

	public static void main(String[] args) 
  {
	  new OrbitalsRenderer3DTest();
	}
}

