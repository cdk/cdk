/*
 *  Copyright (C) 2002-2006  The Jmol Development Team
 *
 *  Contact: jmol-developers@lists.sf.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
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

package org.openscience.cdk.applications.jchempaint.applet;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Method;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.applications.jchempaint.JChemPaintViewerOnlyPanel;
import org.openscience.cdk.controller.Controller2D;

/**
 * The
 * 
 * @cdk.module jchempaint.applet
 * @author dirk49
 * @cdk.created 04. Mai 2005
 */
public class JChemPaintViewerOnlyApplet extends JChemPaintAbstractApplet implements MouseMotionListener{

    private Applet spectrumApplet;
    private Object lastHighlighted=null;
    private Controller2D controller;
  
  /* (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	public void init() {
    JChemPaintViewerOnlyPanel jcpvop = new JChemPaintViewerOnlyPanel(new Dimension((int)this.getSize().getWidth()-100,(int)this.getSize().getHeight()-100), "applet");
    setTheJcpp(jcpvop);
		String atomNumbers=getParameter("spectrumRenderer");
    if(atomNumbers!=null){
      getTheJcpp().getDrawingPanel().addMouseMotionListener(this);
    }
    super.init();
	}
	
	/* (non-Javadoc)
	 * @see java.applet.Applet#start()
	 */
	public void start() {
		super.start();
	}
	/* (non-Javadoc)
	 * @see java.applet.Applet#stop()
	 */
	public void stop() {
		super.stop();
	}
  
  
  public void mouseDragged(MouseEvent event)
  {
  }
  
  
  public void mouseMoved(MouseEvent event)
  {
    if(getParameter("spectrumRenderer")==null)
      return;
    try{
      getSpectrumApplet();
      if(controller==null)
        controller=new Controller2D(getTheJcpp().getChemModel(),getTheJcpp().getJChemPaintModel().getRendererModel());
      int[] screenCoords = {event.getX(), event.getY()};
      int[] mouseCoords = controller.getWorldCoordinates(screenCoords);
      int mouseX = mouseCoords[0];
      int mouseY = mouseCoords[1];
      IChemObject objectInRange = controller.getChemObjectInRange(mouseX, mouseY);
      if (objectInRange!=lastHighlighted && objectInRange instanceof IAtom)
      {
   	    getTheJcpp().getJChemPaintModel().getRendererModel().setExternalHighlightColor(Color.RED);
   	    IAtomContainer ac=getTheJcpp().getJChemPaintModel().getChemModel().getSetOfMolecules().getBuilder().newAtomContainer();
   	    ac.addAtom((IAtom)objectInRange);
   	    getTheJcpp().getJChemPaintModel().getRendererModel().setExternalSelectedPart(ac);
        highlightPeakInSpectrum(getTheJcpp().getChemModel().getSetOfMolecules().getMolecule(0).getAtomNumber((IAtom)objectInRange));
        repaint();
        lastHighlighted=objectInRange;
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  
  /**
   * Handles interaction with structure viewer,
   * highlighted atoms in spectrum view will be highlighted in structure
   * @param atomNumber atom number of peaks highlighted in spectrum
   */
  public void highlightPeakInSpectrum(int atomNumber) throws Exception{
    if(getParameter("spectrumRenderer")==null)
      return;
		Method highlightMethod = getSpectrumApplet().getClass().getMethod("highlightPeakInSpectrum", new Class[] { Integer.TYPE });
    highlightMethod.invoke(getSpectrumApplet(),	new Object[] { new Integer(atomNumber) });
    spectrumApplet.repaint();
  }


  private Applet getSpectrumApplet() {
      if (spectrumApplet == null) {
          String s = getParameter("spectrumRenderer");
          if ((s != null) && (s.length() > 0)) {
              spectrumApplet = getAppletContext().getApplet(s);
          }
      }
      return spectrumApplet;
  }
}
