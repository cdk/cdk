/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005  The JChemPaint project
 * 
 * Contact: jchempaint-devel@lists.sourceforge.net
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.io.StringWriter;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.smiles.SmilesGenerator;

/**
 * Action to copy/paste structures.
 *
 * @cdk.module jchempaint
 * @author     Egon Willighagen <e.willighagen@science.ru.nl>
 */
public class CopyPasteAction extends JCPAction{

	private DataFlavor molFlavor=new DataFlavor ("chemical/x-mdl-molfile", "mdl mol file format");
    
	public void actionPerformed(ActionEvent e) {
    	try {
    		//handleSystemClipboard();
	        logger.info("  type  ", type);
	        logger.debug("  source ", e.getSource());
	        JChemPaintModel jcpModel = jcpPanel.getJChemPaintModel();
	        Renderer2DModel renderModel = jcpModel.getRendererModel();
	        if ("copy".equals(type)) {
	            AtomContainer tocopy = renderModel.getSelectedPart();
	            if (tocopy == null) {
	            	return;
	            }
	            Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
	            JcpSelection jcpselection=new JcpSelection(tocopy);
	            sysClip.setContents(jcpselection,null);
	        } else if ("paste".equals(type)) {
	        	Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
	        	Transferable transfer = sysClip.getContents( null );
	        	if(transfer!=null && (transfer.isDataFlavorSupported (molFlavor))) {
	        		String mol = (String) transfer.getTransferData (molFlavor);
		        	MDLReader mdlreader = new MDLReader(new StringReader(mol));
		            AtomContainer topaste = (AtomContainer) mdlreader.read(new Molecule()); 
		            if (topaste != null) {
		                topaste = (AtomContainer)topaste.clone();
		                ChemModel chemModel = jcpModel.getChemModel();
		                //translate the new structure a bit
		                GeometryTools.translate2D(topaste, 25, 25); //in pixels
		                //paste the new structure into the active model
		                SetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
		                if (moleculeSet == null) {
		                    moleculeSet = new SetOfMolecules();
		                }
		                moleculeSet.addMolecule(new Molecule(topaste));
		                //make the pasted structure selected
		                renderModel.setSelectedPart(topaste);
		            }
	        	}
	        }
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	//handleSystemClipboard();
    }
    
    void handleSystemClipboard()
    {
		Clipboard clipboard = jcpPanel.getToolkit().getSystemClipboard();
		Transferable clipboardContent = clipboard.getContents(this);
		DataFlavor flavors[]=clipboardContent.getTransferDataFlavors();
		String text = "System.clipoard content";
		for(int i=0;i<flavors.length;++i)
		{
			text+="\n\n Name: "+ flavors[i].getHumanPresentableName();
			text+="\n MIME Type: "+flavors[i].getMimeType();
			text+="\n Class: ";
			Class cl = flavors[i].getRepresentationClass();
			if(cl==null) text+="null";
			else text+=cl.getName();
		}
		System.err.println(text);
		logger.debug(text);
    }

    class JcpSelection implements Transferable, ClipboardOwner {
  	  private DataFlavor [] supportedFlavors = {molFlavor, DataFlavor.stringFlavor};
      String mol;
      String smiles;

      public JcpSelection (AtomContainer tocopy1) throws Exception{
    	  Molecule tocopy=new Molecule(tocopy1);
          StringWriter sw = new StringWriter();
          new MDLWriter(sw).writeMolecule(tocopy);
    	  this.mol=sw.toString();
    	  SmilesGenerator sg=new SmilesGenerator();
    	  smiles = sg.createSMILES(tocopy);
      }
    	
      public synchronized DataFlavor [] getTransferDataFlavors () {
    	return (supportedFlavors);
   	  }
      
      public boolean isDataFlavorSupported (DataFlavor parFlavor) {
    	  for(int i=0;i<supportedFlavors.length;i++){
    		  if(supportedFlavors[i].equals(parFlavor))
    			  return true;
    	  }
    	  return false;
      }
    	
      public synchronized Object getTransferData (DataFlavor parFlavor)	throws UnsupportedFlavorException {
    	if (parFlavor.equals (molFlavor))
    		return (mol);
    	else if(parFlavor.equals(DataFlavor.stringFlavor))
    		return(smiles);
    	else
    		throw new UnsupportedFlavorException (parFlavor);
      }
      
      public void lostOwnership (Clipboard parClipboard, Transferable parTransferable) {
    	System.out.println ("Lost ownership");
      }
   }
}

