/*  $RCSfile$    
 *  $Author: kaihartmann $    
 *  $Date: 2006-09-20 21:12:37 +0200 (Mi, 20 Sep 2006) $    
 *  $Revision: 7001 $
 *
 *  Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.applications.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.FileInputStream;
import java.util.EventObject;

import javax.swing.JPanel;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.LoggingTool;


/**
 * A Swing-based implementation of Renderer2D for viewing molecules.
 *
 * @cdk.module applications
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.require swing
 *
 * @author     steinbeck
 * @cdk.created    2002-05-30
 */
public class MoleculeViewer2DPanel extends JPanel implements ICDKChangeListener
{

    private static final long serialVersionUID = -3688088406857192238L;
    
    public IAtomContainer atomContainer;
    public Renderer2DModel r2dm;
    public Renderer2D renderer;
    public String title = "Molecule Viewer";

    private Dimension preferredSize;

    private static LoggingTool logger;


    /**
     * Constructs a MoleculeViewer with a molecule to display and a
     * Renderer2DModel containing the information on how to display it.
     *
     * @param  r2dm   The settings determining how the molecule is displayed
     */
    public MoleculeViewer2DPanel(IAtomContainer atomContainer, Renderer2DModel r2dm)
    {
        logger = new LoggingTool(this);
        this.atomContainer = atomContainer;
        preferredSize = new Dimension(500, 500);
        this.r2dm = r2dm;
        r2dm.setBackgroundDimension(preferredSize);
        r2dm.addCDKChangeListener(this);
        renderer = new Renderer2D(r2dm);
    }


    /**
     *  Constructs a MoleculeViewer with a molecule to display
     */
    public MoleculeViewer2DPanel(IAtomContainer atomContainer)
    {
        this(atomContainer, new Renderer2DModel());
    }


    /**
     *  Constructs a MoleculeViewer with a molecule to display
     */
    public MoleculeViewer2DPanel()
    {
        this(null, new Renderer2DModel());
    }

 
    /**
     *  Sets a Renderer2DModel which determins the way a molecule is displayed
     *
     * @param  r2dm  The Renderer2DModel
     */
    public void setRenderer2DModel(Renderer2DModel r2dm)
    {
        this.r2dm = r2dm;
        r2dm.addCDKChangeListener(this);
        renderer = new Renderer2D(r2dm);
    }


    /**
     *  Sets the AtomContainer to be displayed
     *
     * @param  atomContainer  The AtomContainer to be displayed
     */
    public void setAtomContainer(IAtomContainer atomContainer)
    {
        this.atomContainer = atomContainer;
    }


    /**
     *  Gets the Renderer2DModel which determins the way a molecule is displayed
     *
     * @return    The Renderer2DModel value
     */
    public Renderer2DModel getRenderer2DModel()
    {
        return renderer.getRenderer2DModel();
    }


    /**
     *  Returns the AtomContainer which is being displayed
     *
     * @return    The AtomContainer which is being displayed
     */
    public IAtomContainer getAtomContainer()
    {
        return this.atomContainer;
    }


    public static void display(MoleculeViewer2DPanel moleculeViewer, IMolecule molecule, boolean generateCoordinates)
    {
        display(moleculeViewer, molecule, generateCoordinates, false);
    }


    public static MoleculeViewer2DPanel display(MoleculeViewer2DPanel moleculeViewer, IMolecule molecule, boolean generateCoordinates, boolean drawNumbers)
    {	
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();

        try
        {
            if (generateCoordinates)
            {
                sdg.setMolecule((IMolecule)molecule.clone());
                sdg.generateCoordinates();
                molecule = sdg.getMolecule();
            }
            moleculeViewer.setAtomContainer(molecule);
            moleculeViewer.setPreferredSize(new Dimension(600,400));
            Renderer2DModel r2dm = moleculeViewer.getRenderer2DModel();
            r2dm.setDrawNumbers(drawNumbers);
            
        }
        catch(Exception exc)
        {
            logger.debug(exc);
            System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
            exc.printStackTrace();
        }
        return moleculeViewer;
    }


    
    /**
     *  Paints the molecule onto the JPanel
     *
     * @param  graphics  The graphics used to paint with.
     */
    public void paint(Graphics graphics)
    {
        super.paint(graphics);
        if (atomContainer != null) {
            setBackground(r2dm.getBackColor());
            GeometryTools.translateAllPositive(atomContainer);
            GeometryTools.scaleMolecule(atomContainer, r2dm.getBackgroundDimension(), 0.8);
            GeometryTools.center(atomContainer, r2dm.getBackgroundDimension());
            renderer.paintMolecule(atomContainer, (Graphics2D)graphics,false,true);
        }
    }



    /**
     *  Method to notify this CDKChangeListener if something has changed in another object
     *
     * @param  eventObject  The EventObject containing information on the nature and source of the event
     */
    public void stateChanged(EventObject eventObject)
    {
        repaint();
    }


    /**
     *  The main method.
     *
     * @param  args  An MDL molfile
     */

    public static void main(String[] args)
    {
        IAtomContainer atomContainer = null;
        try
        {
            FileInputStream fis = new FileInputStream(args[0]);
            MDLReader mr = new MDLReader(fis);
            atomContainer = ((ChemFile) mr.read(new ChemFile())).getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
            fis.close();
        }
        catch (Exception exc)
        {
            logger.debug(exc);
            exc.printStackTrace();
        }

        new MoleculeViewer2D(atomContainer, new Renderer2DModel());
    }
}


